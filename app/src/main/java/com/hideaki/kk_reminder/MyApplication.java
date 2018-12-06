package com.hideaki.kk_reminder;

import android.app.Application;

public class MyApplication extends Application {

  @Override
  public void onCreate() {

    super.onCreate();
    registerActivityLifecycleCallbacks(new MyLifecycleHandler());
  }
}
