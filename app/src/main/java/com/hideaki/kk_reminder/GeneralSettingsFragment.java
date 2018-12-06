package com.hideaki.kk_reminder;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import static com.google.common.base.Preconditions.checkNotNull;

public class GeneralSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

  static final String TAG = GeneralSettingsFragment.class.getSimpleName();

  private MainActivity activity;
  public BackupAndRestoreFragment backupAndRestoreFragment;

  public static GeneralSettingsFragment newInstance() {

    return new GeneralSettingsFragment();
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
    activity.drawerLayout.closeDrawer(GravityCompat.START);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.general_settings_edit);

    PreferenceScreen defaultNewTask = (PreferenceScreen)findPreference("new_task");
    PreferenceScreen manuallySnooze = (PreferenceScreen)findPreference("manually_snooze");
    PreferenceScreen defaultQuickPicker = (PreferenceScreen)findPreference("quick_picker");
    PreferenceCategory upgradeCategory = (PreferenceCategory)findPreference("upgrade_category");
    PreferenceScreen upgrade = (PreferenceScreen)findPreference("upgrade");
    PreferenceScreen primaryColor = (PreferenceScreen)findPreference("primary_color");
    PreferenceScreen secondaryColor = (PreferenceScreen)findPreference("secondary_color");
    PreferenceScreen backup = (PreferenceScreen)findPreference("backup");
    PreferenceScreen about = (PreferenceScreen)findPreference("this_app");

    defaultNewTask.setOnPreferenceClickListener(this);
    manuallySnooze.setOnPreferenceClickListener(this);
    defaultQuickPicker.setOnPreferenceClickListener(this);
    upgrade.setOnPreferenceClickListener(this);
    primaryColor.setOnPreferenceClickListener(this);
    secondaryColor.setOnPreferenceClickListener(this);
    backup.setOnPreferenceClickListener(this);
    about.setOnPreferenceClickListener(this);

    if(activity.generalSettings.isPremium()) {
      getPreferenceScreen().removePreference(upgradeCategory);
    }
    else {
      primaryColor.setSummary(activity.getString(R.string.premium_account_promotion));
      secondaryColor.setSummary(activity.getString(R.string.premium_account_promotion));
      backup.setSummary(activity.getString(R.string.premium_account_promotion));
    }
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

    activity.drawerToggle.setDrawerIndicatorEnabled(true);
    actionBar.setTitle(R.string.settings);

    //設定項目間の区切り線の非表示
    ListView listView = view.findViewById(android.R.id.list);
    listView.setDivider(null);

    return view;
  }

  @Override
  public boolean onPreferenceClick(Preference preference) {

    switch(preference.getKey()) {

      case "new_task": {

        activity.showMainEditFragment(activity.generalSettings.getItem(), TAG);
        return true;
      }
      case "manually_snooze": {

        transitionFragment(DefaultManuallySnoozeFragment.newInstance());
        return true;
      }
      case "quick_picker": {

        transitionFragment(DefaultQuickPickerFragment.newInstance());
        return true;
      }
      case "upgrade": {

        activity.promotionDialog.show();
        return true;
      }
      case "primary_color": {

        if(activity.generalSettings.isPremium()) {
          ColorPickerListAdapter.is_general_settings = true;
          activity.showColorPickerListViewFragment(TAG);
        }
        else activity.promotionDialog.show();
        return true;
      }
      case "secondary_color": {

        if(activity.generalSettings.isPremium()) {
          ColorPickerListAdapter.is_general_settings = true;
          activity.generalSettings.getTheme().setColor_primary(false);
          activity.showColorPickerListViewFragment(TAG);
        }
        else activity.promotionDialog.show();
        return true;
      }
      case "backup": {

        if(activity.generalSettings.isPremium()) {
          backupAndRestoreFragment = BackupAndRestoreFragment.newInstance();
          transitionFragment(backupAndRestoreFragment);
        }
        else activity.promotionDialog.show();
        return true;
      }
      case "this_app": {

        activity.showAboutThisAppFragment(TAG);
        return true;
      }

    }
    return false;
  }

  private void transitionFragment(PreferenceFragment next) {

    FragmentManager manager = getFragmentManager();
    manager
        .beginTransaction()
        .remove(this)
        .add(R.id.content, next)
        .addToBackStack(null)
        .commit();
  }
}