package com.hideaki.kk_reminder;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.hideaki.kk_reminder.UtilClass.LOCALE;

public class DefaultManuallySnoozeFragment extends BasePreferenceFragmentCompat {

  private MainActivity activity;
  static PreferenceScreen label;

  public static DefaultManuallySnoozeFragment newInstance() {

    return new DefaultManuallySnoozeFragment();
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
  }

  @Override
  public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {

    addPreferencesFromResource(R.xml.default_manually_snooze);
    setHasOptionsMenu(true);

    label = (PreferenceScreen)findPreference("label");
    label.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
      @Override
      public boolean onPreferenceClick(Preference preference) {

        DefaultManuallySnoozePickerDialogFragment dialog = new DefaultManuallySnoozePickerDialogFragment();
        dialog.show(activity.getSupportFragmentManager(), "default_manually_snooze_picker");
        return true;
      }
    });
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
      if(!LOCALE.equals(Locale.JAPAN)) summary += " ";
    }
    if(minute != 0) {
      summary += getResources().getQuantityString(R.plurals.minute, minute, minute);
      if(!LOCALE.equals(Locale.JAPAN)) summary += " ";
    }
    label.setTitle(summary);

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    FragmentManager manager = getFragmentManager();
    checkNotNull(manager);
    manager.popBackStack();
    return super.onOptionsItemSelected(item);
  }
}