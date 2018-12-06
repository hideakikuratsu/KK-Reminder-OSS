package com.hideaki.kk_reminder;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;

import static android.content.Context.MODE_PRIVATE;
import static com.hideaki.kk_reminder.UtilClass.LIFECYCLE_COUNT;
import static com.hideaki.kk_reminder.UtilClass.PAUSED;
import static com.hideaki.kk_reminder.UtilClass.RESUMED;
import static com.hideaki.kk_reminder.UtilClass.STARTED;
import static com.hideaki.kk_reminder.UtilClass.STOPPED;

public class MyLifecycleHandler implements Application.ActivityLifecycleCallbacks {

  private int resumed;
  private int paused;
  private int started;
  private int stopped;

  @Override
  public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

  @Override
  public void onActivityStarted(Activity activity) {

    started++;
    saveCountInSharedPreferences(activity);
  }

  @Override
  public void onActivityResumed(Activity activity) {

    resumed++;
    saveCountInSharedPreferences(activity);
  }

  @Override
  public void onActivityPaused(Activity activity) {

    paused++;
    if(resumed == paused) {
      resumed = 0;
      paused = 0;
    }
    saveCountInSharedPreferences(activity);
  }

  @Override
  public void onActivityStopped(Activity activity) {

    stopped++;
    if(started == stopped) {
      started = 0;
      stopped = 0;
    }
    saveCountInSharedPreferences(activity);
  }

  @Override
  public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

  @Override
  public void onActivityDestroyed(Activity activity) {}

  private void saveCountInSharedPreferences(Activity activity) {

    SharedPreferences preferences = activity.getSharedPreferences(LIFECYCLE_COUNT, MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putInt(RESUMED, resumed);
    editor.putInt(PAUSED, paused);
    editor.putInt(STARTED, started);
    editor.putInt(STOPPED, stopped);
    editor.apply();
  }
}
