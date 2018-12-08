package com.hideaki.kk_reminder;

import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;

public class MyApplication extends MultiDexApplication {

  @Override
  public void onCreate() {

    super.onCreate();
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    registerActivityLifecycleCallbacks(new MyLifecycleHandler());
  }
}
