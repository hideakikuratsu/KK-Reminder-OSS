package com.example.hideaki.reminder;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class RepeatEditFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

  private ActionBar actionBar;
  static CheckBoxPreference never;
  static CheckBoxPreference everyday;
  static CheckBoxPreference weekday;
  static CheckBoxPreference everyweek;
  static CheckBoxPreference everymonth;
  static CheckBoxPreference everyyear;
  static CheckBoxPreference custom;
  static PreferenceScreen label;
  static String label_str;
  private static Repeat repeat;

  public static RepeatEditFragment newInstance() {

    return new RepeatEditFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.repeat_edit);
    setHasOptionsMenu(true);

    actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
    actionBar.setTitle(R.string.repeat);

    never = (CheckBoxPreference)findPreference("never");
    everyday = (CheckBoxPreference)findPreference("everyday");
    weekday = (CheckBoxPreference)findPreference("weekday");
    everyweek = (CheckBoxPreference)findPreference("everyweek");
    everymonth = (CheckBoxPreference)findPreference("everymonth");
    everyyear = (CheckBoxPreference)findPreference("everyyear");
    custom = (CheckBoxPreference)findPreference("custom");
    label = (PreferenceScreen)findPreference("label");

    //各テンプレート項目のタイトル設定
    label_str = getActivity().getResources().getString(R.string.everyweek)
        + DateFormat.format("E曜日", MainEditFragment.final_cal);
    everyweek.setTitle(label_str);

    label_str = getActivity().getResources().getString(R.string.everymonth)
        + DateFormat.format("d日", MainEditFragment.final_cal);
    everymonth.setTitle(label_str);

    label_str = getActivity().getResources().getString(R.string.everyyear)
        + DateFormat.format("M月d日", MainEditFragment.final_cal);
    everyyear.setTitle(label_str);

    //チェック状態の初期化
    if(repeat != MainEditFragment.repeat && !MainEditFragment.repeat.isSetted()) {
      never.setChecked(true);
      everyday.setChecked(false);
      weekday.setChecked(false);
      everyweek.setChecked(false);
      everymonth.setChecked(false);
      everyyear.setChecked(false);
      custom.setChecked(false);
      label.setSummary(R.string.non_repeat);

      repeat = MainEditFragment.repeat;
    }

    never.setOnPreferenceClickListener(this);
    everyday.setOnPreferenceClickListener(this);
    weekday.setOnPreferenceClickListener(this);
    everyweek.setOnPreferenceClickListener(this);
    everymonth.setOnPreferenceClickListener(this);
    everyyear.setOnPreferenceClickListener(this);
    custom.setOnPreferenceClickListener(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    view.setBackgroundColor(getResources().getColor(android.R.color.background_light));

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    actionBar.setTitle(R.string.edit);
    getFragmentManager().popBackStack();
    return super.onOptionsItemSelected(item);
  }

  private void transitionFragment(PreferenceFragment next) {

    getFragmentManager()
        .beginTransaction()
        .replace(android.R.id.content, next)
        .addToBackStack(null)
        .commit();
  }

  @Override
  public boolean onPreferenceClick(Preference preference) {

    switch(preference.getKey()) {
      case "never":
        if(never.isChecked()) {
          if(everyday.isChecked()) everyday.setChecked(false);
          if(weekday.isChecked()) weekday.setChecked(false);
          if(everyweek.isChecked()) everyweek.setChecked(false);
          if(everymonth.isChecked()) everymonth.setChecked(false);
          if(everyyear.isChecked()) everyyear.setChecked(false);
          if(custom.isChecked()) custom.setChecked(false);
          label.setSummary(R.string.non_repeat);
        }
        else never.setChecked(true);
        return true;
      case "everyday":
        if(everyday.isChecked()) {
          if(never.isChecked()) never.setChecked(false);
          if(weekday.isChecked()) weekday.setChecked(false);
          if(everyweek.isChecked()) everyweek.setChecked(false);
          if(everymonth.isChecked()) everymonth.setChecked(false);
          if(everyyear.isChecked()) everyyear.setChecked(false);
          if(custom.isChecked()) custom.setChecked(false);
          label.setSummary(R.string.everyday);
        }
        else everyday.setChecked(true);
        return true;
      case "weekday":
        if(weekday.isChecked()) {
          if(never.isChecked()) never.setChecked(false);
          if(everyday.isChecked()) everyday.setChecked(false);
          if(everyweek.isChecked()) everyweek.setChecked(false);
          if(everymonth.isChecked()) everymonth.setChecked(false);
          if(everyyear.isChecked()) everyyear.setChecked(false);
          if(custom.isChecked()) custom.setChecked(false);
          label.setSummary(R.string.weekday);
        }
        else weekday.setChecked(true);
        return true;
      case "everyweek":
        if(everyweek.isChecked()) {
          if(never.isChecked()) never.setChecked(false);
          if(everyday.isChecked()) everyday.setChecked(false);
          if(weekday.isChecked()) weekday.setChecked(false);
          if(everymonth.isChecked()) everymonth.setChecked(false);
          if(everyyear.isChecked()) everyyear.setChecked(false);
          if(custom.isChecked()) custom.setChecked(false);
          label_str = getActivity().getResources().getString(R.string.everyweek)
              + DateFormat.format("E曜日", MainEditFragment.final_cal);
          label.setSummary(label_str);
        }
        else everyweek.setChecked(true);
        return true;
      case "everymonth":
        if(everymonth.isChecked()) {
          if(never.isChecked()) never.setChecked(false);
          if(everyday.isChecked()) everyday.setChecked(false);
          if(weekday.isChecked()) weekday.setChecked(false);
          if(everyweek.isChecked()) everyweek.setChecked(false);
          if(everyyear.isChecked()) everyyear.setChecked(false);
          if(custom.isChecked()) custom.setChecked(false);
          label_str = getActivity().getResources().getString(R.string.everymonth)
              + DateFormat.format("d日", MainEditFragment.final_cal);
          label.setSummary(label_str);
        }
        else everymonth.setChecked(true);
        return true;
      case "everyyear":
        if(everyyear.isChecked()) {
          if(never.isChecked()) never.setChecked(false);
          if(everyday.isChecked()) everyday.setChecked(false);
          if(weekday.isChecked()) weekday.setChecked(false);
          if(everyweek.isChecked()) everyweek.setChecked(false);
          if(everymonth.isChecked()) everymonth.setChecked(false);
          if(custom.isChecked()) custom.setChecked(false);
          label_str = getActivity().getResources().getString(R.string.everyyear)
              + DateFormat.format("M月d日", MainEditFragment.final_cal);
          label.setSummary(label_str);
        }
        else everyyear.setChecked(true);
        return true;
      case "custom":
        if(custom.isChecked()) {
          if(never.isChecked()) never.setChecked(false);
          if(everyday.isChecked()) everyday.setChecked(false);
          if(weekday.isChecked()) weekday.setChecked(false);
          if(everyweek.isChecked()) everyweek.setChecked(false);
          if(everymonth.isChecked()) everymonth.setChecked(false);
          if(everyyear.isChecked()) everyyear.setChecked(false);
        }
        else custom.setChecked(true);
        transitionFragment(RepeatCustomPickerFragment.newInstance());
        return true;
    }
    return false;
  }
}
