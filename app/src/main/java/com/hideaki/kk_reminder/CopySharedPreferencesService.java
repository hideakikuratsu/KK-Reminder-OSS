package com.hideaki.kk_reminder;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import static com.hideaki.kk_reminder.UtilClass.copySharedPreferencesKernel;

public class CopySharedPreferencesService extends IntentService {

  public CopySharedPreferencesService() {

    super(CopySharedPreferencesService.class.getSimpleName());
  }

  public CopySharedPreferencesService(String name) {

    super(name);
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {

    copySharedPreferencesKernel(this, false);
  }
}
