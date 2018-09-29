package com.example.hideaki.reminder;

import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
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

import static com.google.common.base.Preconditions.checkNotNull;

public class NotifyIntervalEditFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

  private MainActivity activity;
  private PreferenceScreen rootPreferenceScreen;
  private CheckBoxPreference none;
  private CheckBoxPreference defaultNotify;
  private CheckBoxPreference everyMinute;
  private CheckBoxPreference everyFiveMinutes;
  private CheckBoxPreference everyFifteenMinutes;
  private CheckBoxPreference everyThirtyMinutes;
  private CheckBoxPreference everyHour;
  private CheckBoxPreference everyDay;
  static CheckBoxPreference custom;
  private PreferenceScreen custom_description;
  private Preference duration;
  private Preference time;
  static PreferenceScreen label;

  public static NotifyIntervalEditFragment newInstance() {

    return new NotifyIntervalEditFragment();
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.notify_interval_edit);
    setHasOptionsMenu(true);

    rootPreferenceScreen = getPreferenceScreen();
    none = (CheckBoxPreference)findPreference("none");
    defaultNotify = (CheckBoxPreference)findPreference("default");
    everyMinute = (CheckBoxPreference)findPreference("every_minute");
    everyFiveMinutes = (CheckBoxPreference)findPreference("every_five_minutes");
    everyFifteenMinutes = (CheckBoxPreference)findPreference("every_fifteen_minutes");
    everyThirtyMinutes = (CheckBoxPreference)findPreference("every_thirty_minutes");
    everyHour = (CheckBoxPreference)findPreference("every_hour");
    everyDay = (CheckBoxPreference)findPreference("every_day");
    custom = (CheckBoxPreference)findPreference("custom");
    custom_description = (PreferenceScreen)findPreference("custom_description");
    duration = findPreference("duration");
    time = findPreference("time");
    label = (PreferenceScreen)findPreference("label");

    none.setOnPreferenceClickListener(this);
    defaultNotify.setOnPreferenceClickListener(this);
    everyMinute.setOnPreferenceClickListener(this);
    everyFiveMinutes.setOnPreferenceClickListener(this);
    everyFifteenMinutes.setOnPreferenceClickListener(this);
    everyThirtyMinutes.setOnPreferenceClickListener(this);
    everyHour.setOnPreferenceClickListener(this);
    everyDay.setOnPreferenceClickListener(this);
    custom.setOnPreferenceClickListener(this);
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
    actionBar.setTitle(R.string.interval);

    //チェック状態の初期化
    none.setChecked(false);
    defaultNotify.setChecked(false);
    everyMinute.setChecked(false);
    everyFiveMinutes.setChecked(false);
    everyFifteenMinutes.setChecked(false);
    everyThirtyMinutes.setChecked(false);
    everyHour.setChecked(false);
    everyDay.setChecked(false);
    custom.setChecked(false);
    rootPreferenceScreen.removePreference(duration);
    rootPreferenceScreen.removePreference(time);
    rootPreferenceScreen.removePreference(custom_description);

    switch(MainEditFragment.notifyInterval.getWhich_setted()) {

      case 0: {

        none.setChecked(true);
        break;
      }
      case 1: {

        defaultNotify.setChecked(true);
        break;
      }
      case 1 << 1: {

        everyMinute.setChecked(true);
        break;
      }
      case 1 << 2: {

        everyFiveMinutes.setChecked(true);
        break;
      }
      case 1 << 3: {

        everyFifteenMinutes.setChecked(true);
        break;
      }
      case 1 << 4: {

        everyThirtyMinutes.setChecked(true);
        break;
      }
      case 1 << 5: {

        everyHour.setChecked(true);
        break;
      }
      case 1 << 6: {

        everyDay.setChecked(true);
        break;
      }
      case 1 << 7: {

        custom.setChecked(true);
        rootPreferenceScreen.addPreference(duration);
        rootPreferenceScreen.addPreference(time);
        rootPreferenceScreen.addPreference(custom_description);

        break;
      }
    }

    //ラベルの初期化
    String label_str = MainEditFragment.notifyInterval.getLabel();
    if(label_str == null || label_str.equals(activity.getString(R.string.none))) {
      label.setSummary(R.string.non_notify);
    }
    else {
      label.setSummary(label_str);
    }

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    getFragmentManager().popBackStack();
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onPreferenceClick(Preference preference) {


    int which_setted = MainEditFragment.notifyInterval.getWhich_setted();
    none.setChecked(false);
    defaultNotify.setChecked(false);
    everyMinute.setChecked(false);
    everyFiveMinutes.setChecked(false);
    everyFifteenMinutes.setChecked(false);
    everyThirtyMinutes.setChecked(false);
    everyHour.setChecked(false);
    everyDay.setChecked(false);
    custom.setChecked(false);
    if(which_setted == (1 << 7)) {
      rootPreferenceScreen.removePreference(duration);
      rootPreferenceScreen.removePreference(time);
      rootPreferenceScreen.removePreference(custom_description);
    }

    switch(preference.getKey()) {

      case "none": {

        none.setChecked(true);

        if(which_setted != 0) {
          label.setSummary(R.string.non_notify);

          MainEditFragment.notifyInterval.setLabel(activity.getString(R.string.none));
          MainEditFragment.notifyInterval.setWhich_setted(0);
          MainEditFragment.notifyInterval.setHour(0);
          MainEditFragment.notifyInterval.setMinute(0);
          MainEditFragment.notifyInterval.setOrg_time(0);
        }

        return true;
      }
      case "default": {

        defaultNotify.setChecked(true);

        if(which_setted != 1) {
          NotifyInterval notifyInterval = activity.generalSettings.getNotifyInterval();
          String label_str = notifyInterval.getLabel();
          if(label_str == null || label_str.equals(activity.getString(R.string.none))) {
            label.setSummary(R.string.non_notify);
          }
          else label.setSummary(label_str);

          MainEditFragment.notifyInterval = notifyInterval.clone();
        }

        return true;
      }
      case "every_minute": {

        everyMinute.setChecked(true);

        if(which_setted != 1 << 1) {
          label.setSummary(R.string.every_minute_summary);

          MainEditFragment.notifyInterval.setLabel(activity.getString(R.string.every_minute_summary));
          MainEditFragment.notifyInterval.setWhich_setted(1 << 1);
          MainEditFragment.notifyInterval.setHour(0);
          MainEditFragment.notifyInterval.setMinute(1);
          MainEditFragment.notifyInterval.setOrg_time(-1);
        }

        return true;
      }
      case "every_five_minutes": {

        everyFiveMinutes.setChecked(true);

        if(which_setted != 1 << 2) {
          label.setSummary(R.string.every_five_minutes_summary);

          MainEditFragment.notifyInterval.setLabel(activity.getString(R.string.every_five_minutes_summary));
          MainEditFragment.notifyInterval.setWhich_setted(1 << 2);
          MainEditFragment.notifyInterval.setHour(0);
          MainEditFragment.notifyInterval.setMinute(5);
          MainEditFragment.notifyInterval.setOrg_time(6);
        }

        return true;
      }
      case "every_fifteen_minutes": {

        everyFifteenMinutes.setChecked(true);

        if(which_setted != 1 << 3) {
          label.setSummary(R.string.every_fifteen_minutes_summary);

          MainEditFragment.notifyInterval.setLabel(activity.getString(R.string.every_fifteen_minutes_summary));
          MainEditFragment.notifyInterval.setWhich_setted(1 << 3);
          MainEditFragment.notifyInterval.setHour(0);
          MainEditFragment.notifyInterval.setMinute(15);
          MainEditFragment.notifyInterval.setOrg_time(6);
        }

        return true;
      }
      case "every_thirty_minutes": {

        everyThirtyMinutes.setChecked(true);

        if(which_setted != 1 << 4) {
          label.setSummary(R.string.every_thirty_minutes_summary);

          MainEditFragment.notifyInterval.setLabel(activity.getString(R.string.every_thirty_minutes_summary));
          MainEditFragment.notifyInterval.setWhich_setted(1 << 4);
          MainEditFragment.notifyInterval.setHour(0);
          MainEditFragment.notifyInterval.setMinute(30);
          MainEditFragment.notifyInterval.setOrg_time(6);
        }

        return true;
      }
      case "every_hour": {

        everyHour.setChecked(true);

        if(which_setted != 1 << 5) {
          label.setSummary(R.string.every_hour_summary);

          MainEditFragment.notifyInterval.setLabel(activity.getString(R.string.every_hour_summary));
          MainEditFragment.notifyInterval.setWhich_setted(1 << 5);
          MainEditFragment.notifyInterval.setHour(1);
          MainEditFragment.notifyInterval.setMinute(0);
          MainEditFragment.notifyInterval.setOrg_time(6);
        }

        return true;
      }
      case "every_day": {

        everyDay.setChecked(true);

        if(which_setted != 1 << 6) {
          label.setSummary(R.string.every_day_summary);

          MainEditFragment.notifyInterval.setLabel(activity.getString(R.string.every_day_summary));
          MainEditFragment.notifyInterval.setWhich_setted(1 << 6);
          MainEditFragment.notifyInterval.setHour(24);
          MainEditFragment.notifyInterval.setMinute(0);
          MainEditFragment.notifyInterval.setOrg_time(-1);
        }

        return true;
      }
      case "custom": {

        custom.setChecked(true);

        MainEditFragment.notifyInterval.setWhich_setted(1 << 7);
        rootPreferenceScreen.addPreference(duration);
        rootPreferenceScreen.addPreference(time);
        rootPreferenceScreen.addPreference(custom_description);

        return true;
      }
    }

    return false;
  }
}