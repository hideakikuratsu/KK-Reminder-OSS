package com.hideaki.kk_reminder;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import static android.content.Context.MODE_PRIVATE;
import static com.hideaki.kk_reminder.UtilClass.CREATED;
import static com.hideaki.kk_reminder.UtilClass.DESTROYED;
import static com.hideaki.kk_reminder.UtilClass.INT_GENERAL;

public class MyLifecycleHandler implements Application.ActivityLifecycleCallbacks {

  private int created;
  private int destroyed;
//  private int resumed;
//  private int paused;
//  private int started;
//  private int stopped;

  @Override
  public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    created++;
    saveCountInSharedPreferences(activity);
  }

  @Override
  public void onActivityStarted(Activity activity) {

//    started++;
//    saveCountInSharedPreferences(activity);
  }

  @Override
  public void onActivityResumed(Activity activity) {

//    resumed++;
//    saveCountInSharedPreferences(activity);
  }

  @Override
  public void onActivityPaused(Activity activity) {

//    paused++;
//    if(resumed == paused) {
//      resumed = 0;
//      paused = 0;
//    }
//    saveCountInSharedPreferences(activity);
  }

  @Override
  public void onActivityStopped(Activity activity) {

//    stopped++;
//    if(started == stopped) {
//      started = 0;
//      stopped = 0;
//    }
//    saveCountInSharedPreferences(activity);
  }

  @Override
  public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

  @Override
  public void onActivityDestroyed(Activity activity) {

    destroyed++;
    if(created == destroyed) {
      created = 0;
      destroyed = 0;
    }
    saveCountInSharedPreferences(activity);
  }

  private void saveCountInSharedPreferences(Activity activity) {

    activity
        .getSharedPreferences(INT_GENERAL, MODE_PRIVATE)
        .edit()
        .putInt(CREATED, created)
        .putInt(DESTROYED, destroyed)
//        .putInt(RESUMED, resumed)
//        .putInt(PAUSED, paused)
//        .putInt(STARTED, started)
//        .putInt(STOPPED, stopped)
        .apply();
  }
}
