package com.hideaki.kk_reminder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultManuallySnoozeFragment extends PreferenceFragment {

  private MainActivity activity;
  static PreferenceScreen label;
  private static Locale locale = Locale.getDefault();

  public static DefaultManuallySnoozeFragment newInstance() {

    return new DefaultManuallySnoozeFragment();
  }

  @TargetApi(23)
  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    onAttachToContext(context);
  }

  //API 23(Marshmallow)未満においてはこっちのonAttachが呼ばれる
  @SuppressWarnings("deprecation")
  @Override
  public void onAttach(Activity activity) {

    super.onAttach(activity);
    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      onAttachToContext(activity);
    }
  }

  //2つのonAttachの共通処理部分
  protected void onAttachToContext(Context context) {

    activity = (MainActivity)context;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.default_manually_snooze);
    setHasOptionsMenu(true);

    label = (PreferenceScreen)findPreference("label");
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
    actionBar.setTitle(R.string.default_manually_snooze);

    //時間表示の初期化
    int hour = activity.snooze_default_hour;
    int minute = activity.snooze_default_minute;
    String summary = "";
    if(hour != 0) {
      summary += getResources().getQuantityString(R.plurals.hour, hour, hour);
      if(!locale.equals(Locale.JAPAN)) summary += " ";
    }
    if(minute != 0) {
      summary += getResources().getQuantityString(R.plurals.minute, minute, minute);
      if(!locale.equals(Locale.JAPAN)) summary += " ";
    }
    summary += getString(R.string.snooze);
    label.setSummary(summary);

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    getFragmentManager().popBackStack();
    return super.onOptionsItemSelected(item);
  }
}