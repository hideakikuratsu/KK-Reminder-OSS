package com.hideaki.kk_reminder;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import static com.hideaki.kk_reminder.UtilClass.copyDatabaseKernel;

public class CopyDatabaseService extends IntentService {

  public CopyDatabaseService() {

    super(CopyDatabaseService.class.getSimpleName());
  }

  public CopyDatabaseService(String name) {

    super(name);
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {

    copyDatabaseKernel(this, false);
  }
}
