package com.example.hideaki.reminder;

import android.content.Context;
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

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
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
    int hour = activity.generalSettings.getSnooze_default_hour();
    int minute = activity.generalSettings.getSnooze_default_minute();
    String summary = "";
    if(hour != 0) {
      summary += activity.getResources().getQuantityString(R.plurals.hour, hour, hour);
      if(!locale.equals(Locale.JAPAN)) summary += " ";
    }
    if(minute != 0) {
      summary += activity.getResources().getQuantityString(R.plurals.minute, minute, minute);
      if(!locale.equals(Locale.JAPAN)) summary += " ";
    }
    summary += activity.getString(R.string.snooze);
    label.setSummary(summary);

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    getFragmentManager().popBackStack();
    return super.onOptionsItemSelected(item);
  }
}