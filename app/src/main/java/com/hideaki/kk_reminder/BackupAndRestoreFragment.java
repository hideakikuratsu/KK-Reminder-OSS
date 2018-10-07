package com.hideaki.kk_reminder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import static com.hideaki.kk_reminder.UtilClass.RC_SIGN_IN;
import static com.google.common.base.Preconditions.checkNotNull;

public class BackupAndRestoreFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

  @SuppressLint("SdCardPath")
  private static final String DATABASE_PATH = "/data/data/com.hideaki.kk_reminder/databases/";
  private static final String FILE_NAME = MyDatabaseHelper.DATABASE;
  private static final String MIME_TYPE = "application/x-sqlite-3";

  private MainActivity activity;
  private PreferenceCategory preferenceCategory;
  private GoogleSignInAccount signInAccount;
  private DriveResourceClient driveResourceClient;
  private GoogleSignInClient signInClient;
  private PreferenceScreen logout;
  private boolean is_backup;

  public static BackupAndRestoreFragment newInstance() {

    return new BackupAndRestoreFragment();
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.backup_and_restore);
    setHasOptionsMenu(true);

    preferenceCategory = (PreferenceCategory)findPreference("backup_and_restore");
    PreferenceScreen backup = (PreferenceScreen)findPreference("backup");
    PreferenceScreen restore = (PreferenceScreen)findPreference("restore");
    logout = (PreferenceScreen)findPreference("logout");

    backup.setOnPreferenceClickListener(this);
    restore.setOnPreferenceClickListener(this);
    logout.setOnPreferenceClickListener(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    checkNotNull(view);

    view.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));

    Toolbar toolbar = activity.findViewById(R.id.toolbar_layout);
    activity.setSupportActionBar(toolbar);
    ActionBar actionBar = activity.getSupportActionBar();
    checkNotNull(actionBar);

    activity.drawerToggle.setDrawerIndicatorEnabled(false);
    actionBar.setHomeAsUpIndicator(activity.upArrow);
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(R.string.backup_and_restore);

    //ログイン状態の初期化
    signInAccount = GoogleSignIn.getLastSignedInAccount(activity);

    if(signInAccount != null) {
      GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          .requestScopes(Drive.SCOPE_APPFOLDER)
          .build();
      signInClient = GoogleSignIn.getClient(activity, signInOptions);

      preferenceCategory.addPreference(logout);
    }
    else preferenceCategory.removePreference(logout);

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    getFragmentManager().popBackStack();
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onPreferenceClick(Preference preference) {

    switch(preference.getKey()) {

      case "backup": {

        if(signInAccount != null) {
          driveResourceClient = Drive.getDriveResourceClient(activity, signInAccount);
          backupToDrive();
        }
        else {
          is_backup = true;
          signIn();
        }
        return true;
      }
      case "restore": {

        if(signInAccount != null) {
          driveResourceClient = Drive.getDriveResourceClient(activity, signInAccount);
          restoreFromDrive();
        }
        else {
          is_backup = false;
          signIn();
        }
        return true;
      }
      case "logout": {

        signOut();
        return true;
      }
    }
    return false;
  }

  private void restoreFromDrive() {

    final Query query = new Query.Builder()
        .addFilter(Filters.and(
            Filters.eq(SearchableField.TITLE, FILE_NAME),
            Filters.eq(SearchableField.MIME_TYPE, MIME_TYPE)
        ))
        .build();

    driveResourceClient
        .getAppFolder()
        .continueWithTask(new Continuation<DriveFolder, Task<MetadataBuffer>>() {
          @Override
          public Task<MetadataBuffer> then(@NonNull Task<DriveFolder> task) {

            DriveFolder appFolder = task.getResult();
            return driveResourceClient.queryChildren(appFolder, query);
          }
        })
        .addOnSuccessListener(new OnSuccessListener<MetadataBuffer>() {
          @Override
          public void onSuccess(MetadataBuffer metadata) {

            int count = metadata.getCount();

            if(count == 0) {
              Toast.makeText(activity,
                  activity.getString(R.string.backup_not_exists), Toast.LENGTH_LONG
              ).show();
            }
            else {
              readDriveFile(metadata.get(0).getDriveId().asDriveFile());
            }

            metadata.release();
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {

            Toast.makeText(activity,
                activity.getString(R.string.fail_to_query), Toast.LENGTH_LONG
            ).show();
          }
        });
  }

  private void readDriveFile(DriveFile file) {

    driveResourceClient
        .openFile(file, DriveFile.MODE_READ_ONLY)
        .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
          @Override
          public Task<Void> then(@NonNull Task<DriveContents> task) {

            DriveContents contents = task.getResult();

            File file = new File(DATABASE_PATH + FILE_NAME);
            InputStream inputStream = contents.getInputStream();
            try {
              OutputStream outputStream = new FileOutputStream(file);
              byte[] buf = new byte[4096];
              int c;
              while((c = inputStream.read(buf, 0, buf.length)) > 0) {
                outputStream.write(buf, 0, c);
                outputStream.flush();
              }
              outputStream.close();
            } catch (IOException e) {
              e.printStackTrace();
            }

            return driveResourceClient.discardContents(contents);
          }
        })
        .addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void aVoid) {

            Toast.makeText(activity,
                activity.getString(R.string.success_to_restore), Toast.LENGTH_LONG
            ).show();

            activity.which_menu_open = 0;
            activity.which_submenu_open = 0;
            activity.finish();
            startActivity(new Intent(activity, MainActivity.class));
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {

            Toast.makeText(activity,
                activity.getString(R.string.fail_to_restore), Toast.LENGTH_LONG
            ).show();
          }
        });
  }

  private void backupToDrive() {

    final Query query = new Query.Builder()
        .addFilter(Filters.and(
            Filters.eq(SearchableField.TITLE, FILE_NAME),
            Filters.eq(SearchableField.MIME_TYPE, MIME_TYPE)
        ))
        .build();

    driveResourceClient
        .getAppFolder()
        .continueWithTask(new Continuation<DriveFolder, Task<MetadataBuffer>>() {
          @Override
          public Task<MetadataBuffer> then(@NonNull Task<DriveFolder> task) {

            DriveFolder appFolder = task.getResult();
            return driveResourceClient.queryChildren(appFolder, query);
          }
        })
        .addOnSuccessListener(new OnSuccessListener<MetadataBuffer>() {
          @Override
          public void onSuccess(MetadataBuffer metadata) {

            int count = metadata.getCount();

            if(count == 0) createFileInAppFolder();
            else {
              writeToDriveFile(metadata.get(0).getDriveId().asDriveFile());
            }

            metadata.release();
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {

            Toast.makeText(activity,
                activity.getString(R.string.fail_to_query), Toast.LENGTH_LONG
            ).show();
          }
        });
  }

  private void createFileInAppFolder() {

    final Task<DriveFolder> appFolderTask = driveResourceClient.getAppFolder();
    final Task<DriveContents> createContentsTask = driveResourceClient.createContents();

    Tasks.whenAll(appFolderTask, createContentsTask)
        .continueWithTask(new Continuation<Void, Task<DriveFile>>() {
          @Override
          public Task<DriveFile> then(@NonNull Task<Void> task) {

            DriveFolder appFolder = appFolderTask.getResult();
            DriveContents contents = createContentsTask.getResult();

            File file = new File(DATABASE_PATH + FILE_NAME);
            OutputStream outputStream = contents.getOutputStream();
            try {
              InputStream inputStream = new FileInputStream(file);
              byte[] buf = new byte[4096];
              int c;
              while((c = inputStream.read(buf, 0, buf.length)) > 0) {
                outputStream.write(buf, 0, c);
                outputStream.flush();
              }
              outputStream.close();
            } catch (IOException e) {
              e.printStackTrace();
            }

            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(FILE_NAME)
                .setMimeType(MIME_TYPE)
                .build();

            return driveResourceClient.createFile(appFolder, changeSet, contents);
          }
        })
        .addOnSuccessListener(new OnSuccessListener<DriveFile>() {
          @Override
          public void onSuccess(DriveFile driveFile) {

            Toast.makeText(activity,
                activity.getString(R.string.create_new_backup), Toast.LENGTH_LONG
            ).show();
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {

            Toast.makeText(activity,
                activity.getString(R.string.fail_to_create_new_backup), Toast.LENGTH_LONG
            ).show();
          }
        });
  }

  private void writeToDriveFile(DriveFile file) {

    driveResourceClient
        .openFile(file, DriveFile.MODE_WRITE_ONLY)
        .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
          @Override
          public Task<Void> then(@NonNull Task<DriveContents> task) {

            DriveContents contents = task.getResult();

            File file = new File(DATABASE_PATH + FILE_NAME);
            OutputStream outputStream = contents.getOutputStream();
            try {
              InputStream inputStream = new FileInputStream(file);
              byte[] buf = new byte[4096];
              int c;
              while((c = inputStream.read(buf, 0, buf.length)) > 0) {
                outputStream.write(buf, 0, c);
                outputStream.flush();
              }
              outputStream.close();
            } catch (IOException e) {
              e.printStackTrace();
            }

            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setLastViewedByMeDate(new Date())
                .build();

            return driveResourceClient.commitContents(contents, changeSet);
          }
        })
        .addOnSuccessListener(new OnSuccessListener<Void>() {
          @Override
          public void onSuccess(Void aVoid) {

            Toast.makeText(activity,
                activity.getString(R.string.update_backup), Toast.LENGTH_LONG
            ).show();
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {

            Toast.makeText(activity,
                activity.getString(R.string.fail_to_update_backup), Toast.LENGTH_LONG
            ).show();
          }
        });
  }

  private void signIn() {

    if(signInAccount == null) {
      GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
          .requestScopes(Drive.SCOPE_APPFOLDER)
          .build();
      signInClient = GoogleSignIn.getClient(activity, signInOptions);

      startActivityForResult(signInClient.getSignInIntent(), RC_SIGN_IN);
    }
    else {
      preferenceCategory.addPreference(logout);
      driveResourceClient = Drive.getDriveResourceClient(activity, signInAccount);
      if(is_backup) backupToDrive();
      else restoreFromDrive();
    }
  }

  private void signOut() {

    signInClient.signOut()
        .addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {

            preferenceCategory.removePreference(logout);
            signInAccount = null;
            signInClient = null;
            driveResourceClient = null;
            Toast.makeText(activity,
                activity.getString(R.string.logout_done), Toast.LENGTH_LONG
            ).show();
          }
        })
        .addOnFailureListener(new OnFailureListener() {
          @Override
          public void onFailure(@NonNull Exception e) {

            Toast.makeText(activity,
                activity.getString(R.string.fail_to_logout), Toast.LENGTH_LONG
            ).show();
          }
        });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {

    super.onActivityResult(requestCode, resultCode, data);

    switch(requestCode) {
      case RC_SIGN_IN: {

        if(resultCode != Activity.RESULT_OK) return;

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        task
            .addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
              @Override
              public void onSuccess(GoogleSignInAccount googleSignInAccount) {

                preferenceCategory.addPreference(logout);
                driveResourceClient = Drive.getDriveResourceClient(activity, googleSignInAccount);
                if(is_backup) backupToDrive();
                else restoreFromDrive();
              }
            })
            .addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception e) {

                Toast.makeText(activity,
                    activity.getString(R.string.error), Toast.LENGTH_LONG
                ).show();
              }
            });

        break;
      }
    }
  }
}