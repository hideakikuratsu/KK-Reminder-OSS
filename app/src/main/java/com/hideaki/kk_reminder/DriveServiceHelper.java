package com.hideaki.kk_reminder;

import android.util.Log;

import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.annotation.Nullable;

class DriveServiceHelper {

  private final Executor executor = Executors.newSingleThreadExecutor();
  private final Drive driveService;

  DriveServiceHelper(Drive driveService) {

    this.driveService = driveService;
  }

  @SuppressWarnings("SameParameterValue")
  Task<List<FileList>> queryFiles(final String mimeType) {

    TaskCompletionSource<List<FileList>> taskCompletionSource = new TaskCompletionSource<>();
    executor.execute(() -> {

      List<FileList> fileLists = new ArrayList<>();
      String pageToken = null;
      do {
        try {
          FileList result = driveService.files().list()
              .setQ("mimeType = '" + mimeType + "' and trashed = false")
              .setSpaces("drive")
              .setFields("nextPageToken, files(id, name)")
              .setPageToken(pageToken)
              .execute();

          fileLists.add(result);
          pageToken = result.getNextPageToken();
        }
        catch(IOException e) {
          Log.e("DriveServiceHelper#queryFiles", Log.getStackTraceString(e));
          taskCompletionSource.setException(e);
          return;
        }
      }
      while(pageToken != null);

      taskCompletionSource.setResult(fileLists);
    });

    return taskCompletionSource.getTask();
  }

  @SuppressWarnings("SameParameterValue")
  Task<FileList> queryFolder(final String folderName) {

    TaskCompletionSource<FileList> taskCompletionSource = new TaskCompletionSource<>();
    executor.execute(() -> {

      FileList fileList;
      try {
        fileList = driveService.files().list()
            .setQ("mimeType = '" + DriveFolder.MIME_TYPE + "' and name = '" + folderName + "'"
                + " and trashed = false")
            .setSpaces("drive")
            .execute();
      }
      catch(IOException e) {
        Log.e("DriveServiceHelper#queryFolder", Log.getStackTraceString(e));
        taskCompletionSource.setException(e);
        return;
      }

      taskCompletionSource.setResult(fileList);
    });

    return taskCompletionSource.getTask();
  }

  Task<File> createFile(
    final String fileName, final AbstractInputStreamContent content,
    @Nullable final String folderId
  ) {

    TaskCompletionSource<File> taskCompletionSource = new TaskCompletionSource<>();
    executor.execute(() -> {

      List<String> root;
      if(folderId == null) {
        root = Collections.singletonList("root");
      }
      else {
        root = Collections.singletonList(folderId);
      }

      File metadata = new File()
          .setParents(root)
          .setName(fileName);

      File file;
      try {
        file = driveService.files().create(metadata, content).execute();
        if(file == null) {
          throw new IOException("Null result when requesting file creation.");
        }
      }
      catch(IOException e) {
        Log.e("DriveServiceHelper#createFile", Log.getStackTraceString(e));
        taskCompletionSource.setException(e);
        return;
      }

      taskCompletionSource.setResult(file);
    });

    return taskCompletionSource.getTask();
  }

  @SuppressWarnings("SameParameterValue")
  Task<File> createFolder(final String folderName, @Nullable final String folderId) {

    TaskCompletionSource<File> taskCompletionSource = new TaskCompletionSource<>();
    executor.execute(() -> {

      List<String> root;
      if(folderId == null) {
        root = Collections.singletonList("root");
      }
      else {
        root = Collections.singletonList(folderId);
      }

      File metadata = new File()
          .setParents(root)
          .setMimeType(DriveFolder.MIME_TYPE)
          .setName(folderName);

      File file;
      try {
        file = driveService.files().create(metadata).execute();
        if(file == null) {
          throw new IOException("Null result when requesting folder creation.");
        }
      }
      catch(IOException e) {
        Log.e("DriveServiceHelper#createFolder", Log.getStackTraceString(e));
        taskCompletionSource.setException(e);
        return;
      }

      taskCompletionSource.setResult(file);
    });

    return taskCompletionSource.getTask();
  }

  Task<Void> downloadFile(final java.io.File targetFile, final String fileId) {

    TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();
    executor.execute(() -> {

      try {
        OutputStream outputStream = new FileOutputStream(targetFile);
        driveService.files().get(fileId).executeMediaAndDownloadTo(outputStream);
      }
      catch(IOException e) {
        Log.e("DriveServiceHelper#downloadFile", Log.getStackTraceString(e));
        taskCompletionSource.setException(e);
        return;
      }

      taskCompletionSource.setResult(null);
    });

    return taskCompletionSource.getTask();
  }
}