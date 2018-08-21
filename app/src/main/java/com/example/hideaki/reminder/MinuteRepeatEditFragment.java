package com.example.hideaki.reminder;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MinuteRepeatEditFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

  private ActionBar actionBar;
  private PreferenceScreen rootPreferenceScreen;
  static PreferenceScreen label;
  static String label_str;
  static CheckBoxPreference never;
  static CheckBoxPreference count;
  static CheckBoxPreference duration;
  private Preference count_picker;
  private Preference duration_picker;

  public static MinuteRepeatEditFragment newInstance() {

    return new MinuteRepeatEditFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.minute_repeat_edit);
    setHasOptionsMenu(true);

    actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
    actionBar.setTitle(getResources().getString(R.string.repeat_minute_unit));

    rootPreferenceScreen = getPreferenceScreen();
    label = (PreferenceScreen)findPreference("label");
    never = (CheckBoxPreference)findPreference("never");
    never.setOnPreferenceClickListener(this);
    count = (CheckBoxPreference)findPreference("count");
    count.setOnPreferenceClickListener(this);
    duration = (CheckBoxPreference)findPreference("duration");
    duration.setOnPreferenceClickListener(this);
    count_picker = findPreference("count_picker");
    duration_picker = findPreference("duration_picker");
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    view.setBackgroundColor(getResources().getColor(android.R.color.background_light));

    //チェック状態の初期化
    never.setChecked(false);
    count.setChecked(false);
    duration.setChecked(false);
    rootPreferenceScreen.removePreference(count_picker);
    rootPreferenceScreen.removePreference(duration_picker);
    switch(MainEditFragment.minuteRepeat.getWhich_setted()) {
      case 0:
        never.setChecked(true);
        break;
      case 1 << 0:
        count.setChecked(true);
        rootPreferenceScreen.addPreference(count_picker);
        break;
      case 1 << 1:
        duration.setChecked(true);
        rootPreferenceScreen.addPreference(duration_picker);
        break;
    }

    if(MainEditFragment.minuteRepeat.getLabel() == null) {
      label.setSummary(R.string.non_repeat);
    }
    else {
      label.setSummary(MainEditFragment.minuteRepeat.getLabel());
    }

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if(MainEditFragment.minuteRepeat.getInterval() > MainEditFragment.minuteRepeat.getOrgDuration()
        && duration.isChecked()) {
      //TODO: 警告ダイアログを出す
    }
    else {
      actionBar.setTitle(R.string.edit);
      getFragmentManager().popBackStack();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onPreferenceClick(Preference preference) {

    switch(preference.getKey()) {
      case "never":
        if(never.isChecked()) {
          if(count.isChecked()) {
            count.setChecked(false);
            rootPreferenceScreen.removePreference(count_picker);
          }
          if(duration.isChecked()) {
            duration.setChecked(false);
            rootPreferenceScreen.removePreference(duration_picker);
          }
          label.setSummary(R.string.non_repeat);

          MainEditFragment.minuteRepeat.setLabel(getActivity().getResources().getString(R.string.none));
          MainEditFragment.minuteRepeat.setWhich_setted(0);
        }
        else never.setChecked(true);
        return true;
      case "count":
        if(count.isChecked()) {
          if(never.isChecked()) never.setChecked(false);
          if(duration.isChecked()) {
            duration.setChecked(false);
            rootPreferenceScreen.removePreference(duration_picker);
          }
          rootPreferenceScreen.addPreference(count_picker);

          label_str = "タスク完了から";
          if(MainEditFragment.minuteRepeat.getHour() != 0) {
            label_str += MainEditFragment.minuteRepeat.getHour() + "時間";
          }
          if(MainEditFragment.minuteRepeat.getMinute() != 0) {
            label_str += MainEditFragment.minuteRepeat.getMinute() + "分";
          }
          label_str += "間隔で" + MainEditFragment.minuteRepeat.getOrg_count() + "回繰り返す";
          label.setSummary(label_str);

          MainEditFragment.minuteRepeat.setLabel(label_str);
          MainEditFragment.minuteRepeat.setWhich_setted(1 << 0);
        }
        else count.setChecked(true);
        return true;
      case "duration":
        if(duration.isChecked()) {
          if(never.isChecked()) never.setChecked(false);
          if(count.isChecked()) {
            count.setChecked(false);
            rootPreferenceScreen.removePreference(count_picker);
          }
          rootPreferenceScreen.addPreference(duration_picker);

          label_str = "タスク完了から";
          if(MainEditFragment.minuteRepeat.getHour() != 0) {
            label_str += MainEditFragment.minuteRepeat.getHour() + "時間";
          }
          if(MainEditFragment.minuteRepeat.getMinute() != 0) {
            label_str += MainEditFragment.minuteRepeat.getMinute() + "分";
          }
          label_str += "間隔で";
          if(MainEditFragment.minuteRepeat.getOrg_duration_hour() != 0) {
            label_str += MainEditFragment.minuteRepeat.getOrg_duration_hour() + "時間";
          }
          if(MainEditFragment.minuteRepeat.getOrg_duration_minute() != 0) {
            label_str += MainEditFragment.minuteRepeat.getOrg_duration_minute() + "分";
          }
          label_str += "経過するまで繰り返す";
          label.setSummary(label_str);

          MainEditFragment.minuteRepeat.setLabel(label_str);
          MainEditFragment.minuteRepeat.setWhich_setted(1 << 1);
        }
        else duration.setChecked(true);
        return true;
    }
    return false;
  }
}