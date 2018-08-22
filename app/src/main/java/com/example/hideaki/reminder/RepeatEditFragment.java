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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

public class RepeatEditFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

  private ActionBar actionBar;
  static CheckBoxPreference never;
  static CheckBoxPreference everyday;
  static CheckBoxPreference everyweekday;
  static CheckBoxPreference everyweek;
  static CheckBoxPreference everymonth;
  static CheckBoxPreference everyyear;
  static CheckBoxPreference custom;
  static PreferenceScreen label;
  static String label_str_everyweek;
  static String label_str_everymonth;
  static String label_str_everyyear;
  static String label_str_custom;
  private int mask_num;

  public static RepeatEditFragment newInstance() {

    return new RepeatEditFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.repeat_edit);
    setHasOptionsMenu(true);

    actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
    actionBar.setTitle(R.string.repeat_day_unit);

    never = (CheckBoxPreference)findPreference("never");
    everyday = (CheckBoxPreference)findPreference("everyday");
    everyweekday = (CheckBoxPreference)findPreference("everyweekday");
    everyweek = (CheckBoxPreference)findPreference("everyweek");
    everymonth = (CheckBoxPreference)findPreference("everymonth");
    everyyear = (CheckBoxPreference)findPreference("everyyear");
    custom = (CheckBoxPreference)findPreference("custom");
    label = (PreferenceScreen)findPreference("label");

    never.setOnPreferenceClickListener(this);
    everyday.setOnPreferenceClickListener(this);
    everyweekday.setOnPreferenceClickListener(this);
    everyweek.setOnPreferenceClickListener(this);
    everymonth.setOnPreferenceClickListener(this);
    everyyear.setOnPreferenceClickListener(this);
    custom.setOnPreferenceClickListener(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    view.setBackgroundColor(getResources().getColor(android.R.color.background_light));
    view.setFocusableInTouchMode(true);
    view.requestFocus();
    view.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
          actionBar.setTitle(R.string.edit);
        }
        return false;
      }
    });

    label_str_everyweek = getActivity().getResources().getString(R.string.everyweek)
        + DateFormat.format("E曜日", MainEditFragment.final_cal);
    everyweek.setTitle(label_str_everyweek);

    label_str_everymonth = getActivity().getResources().getString(R.string.everymonth)
        + DateFormat.format("d日", MainEditFragment.final_cal);
    everymonth.setTitle(label_str_everymonth);

    label_str_everyyear = getActivity().getResources().getString(R.string.everyyear)
        + DateFormat.format("M月d日", MainEditFragment.final_cal);
    everyyear.setTitle(label_str_everyyear);

    //チェック状態の初期化
    never.setChecked(false);
    everyday.setChecked(false);
    everyweekday.setChecked(false);
    everyweek.setChecked(false);
    everymonth.setChecked(false);
    everyyear.setChecked(false);
    custom.setChecked(false);
    switch(MainEditFragment.repeat.getWhich_template()) {
      case 0:
        never.setChecked(true);
        break;
      case 1 << 0:
        everyday.setChecked(true);
        break;
      case 1 << 1:
        everyweekday.setChecked(true);
        break;
      case 1 << 2:
        everyweek.setChecked(true);

        MainEditFragment.repeat.setLabel(label_str_everyweek);
        mask_num = MainEditFragment.final_cal.get(Calendar.DAY_OF_WEEK);
        mask_num = mask_num < 2 ? mask_num + 5 : mask_num - 2;
        MainEditFragment.repeat.setWeek(1 << mask_num);
        break;
      case 1 << 3:
        everymonth.setChecked(true);

        MainEditFragment.repeat.setLabel(label_str_everymonth);
        mask_num = MainEditFragment.final_cal.get(Calendar.DAY_OF_MONTH);
        MainEditFragment.repeat.setDays_of_month(1 << (mask_num - 1));
        break;
      case 1 << 4:
        everyyear.setChecked(true);

        MainEditFragment.repeat.setLabel(label_str_everyyear);
        mask_num = MainEditFragment.final_cal.get(Calendar.MONTH);
        MainEditFragment.repeat.setYear(1 << mask_num);
        MainEditFragment.repeat.setDay_of_month_of_year(MainEditFragment.final_cal.get(Calendar.DAY_OF_MONTH));
        break;
      case 1 << 5:
        custom.setChecked(true);
        break;
    }

    if(MainEditFragment.repeat.getLabel() == null) {
      label.setSummary(R.string.non_repeat);
    }
    else {
      label.setSummary(MainEditFragment.repeat.getLabel());
    }

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

    label.setSummary(MainEditFragment.repeat.getLabel());

    switch(preference.getKey()) {
      case "never":
        if(never.isChecked()) {
          if(everyday.isChecked()) everyday.setChecked(false);
          if(everyweekday.isChecked()) everyweekday.setChecked(false);
          if(everyweek.isChecked()) everyweek.setChecked(false);
          if(everymonth.isChecked()) everymonth.setChecked(false);
          if(everyyear.isChecked()) everyyear.setChecked(false);
          if(custom.isChecked()) custom.setChecked(false);
          label.setSummary(R.string.non_repeat);

          MainEditFragment.repeat.setLabel(getActivity().getResources().getString(R.string.none));
          MainEditFragment.repeat.setSetted(0);
          MainEditFragment.repeat.setWhich_template(0);
        }
        else never.setChecked(true);
        return true;
      case "everyday":
        if(everyday.isChecked()) {
          if(never.isChecked()) never.setChecked(false);
          if(everyweekday.isChecked()) everyweekday.setChecked(false);
          if(everyweek.isChecked()) everyweek.setChecked(false);
          if(everymonth.isChecked()) everymonth.setChecked(false);
          if(everyyear.isChecked()) everyyear.setChecked(false);
          if(custom.isChecked()) custom.setChecked(false);
          label.setSummary(R.string.everyday);

          MainEditFragment.repeat.setLabel(getActivity().getResources().getString(R.string.everyday));
          MainEditFragment.repeat.setSetted(1 << 0);
          MainEditFragment.repeat.setWhich_template(1 << 0);
          MainEditFragment.repeat.setInterval(1);
          MainEditFragment.repeat.setDay(true);
        }
        else everyday.setChecked(true);
        return true;
      case "everyweekday":
        if(everyweekday.isChecked()) {
          if(never.isChecked()) never.setChecked(false);
          if(everyday.isChecked()) everyday.setChecked(false);
          if(everyweek.isChecked()) everyweek.setChecked(false);
          if(everymonth.isChecked()) everymonth.setChecked(false);
          if(everyyear.isChecked()) everyyear.setChecked(false);
          if(custom.isChecked()) custom.setChecked(false);
          label.setSummary(R.string.everyweekday);

          MainEditFragment.repeat.setLabel(getActivity().getResources().getString(R.string.everyweekday));
          MainEditFragment.repeat.setSetted(1 << 1);
          MainEditFragment.repeat.setWhich_template(1 << 1);
          MainEditFragment.repeat.setInterval(1);
          MainEditFragment.repeat.setWeek(Integer.parseInt("11111", 2));
        }
        else everyweekday.setChecked(true);
        return true;
      case "everyweek":
        if(everyweek.isChecked()) {
          if(never.isChecked()) never.setChecked(false);
          if(everyday.isChecked()) everyday.setChecked(false);
          if(everyweekday.isChecked()) everyweekday.setChecked(false);
          if(everymonth.isChecked()) everymonth.setChecked(false);
          if(everyyear.isChecked()) everyyear.setChecked(false);
          if(custom.isChecked()) custom.setChecked(false);
          label.setSummary(label_str_everyweek);

          MainEditFragment.repeat.setLabel(label_str_everyweek);
          MainEditFragment.repeat.setSetted(1 << 1);
          MainEditFragment.repeat.setWhich_template(1 << 2);
          MainEditFragment.repeat.setInterval(1);
          mask_num = MainEditFragment.final_cal.get(Calendar.DAY_OF_WEEK);
          mask_num = mask_num < 2 ? mask_num + 5 : mask_num - 2;
          MainEditFragment.repeat.setWeek(1 << mask_num);
        }
        else everyweek.setChecked(true);
        return true;
      case "everymonth":
        if(everymonth.isChecked()) {
          if(never.isChecked()) never.setChecked(false);
          if(everyday.isChecked()) everyday.setChecked(false);
          if(everyweekday.isChecked()) everyweekday.setChecked(false);
          if(everyweek.isChecked()) everyweek.setChecked(false);
          if(everyyear.isChecked()) everyyear.setChecked(false);
          if(custom.isChecked()) custom.setChecked(false);
          label.setSummary(label_str_everymonth);

          MainEditFragment.repeat.setLabel(label_str_everymonth);
          MainEditFragment.repeat.setSetted(1 << 2);
          MainEditFragment.repeat.setWhich_template(1 << 3);
          MainEditFragment.repeat.setInterval(1);
          mask_num = MainEditFragment.final_cal.get(Calendar.DAY_OF_MONTH);
          MainEditFragment.repeat.setDays_of_month(1 << (mask_num - 1));
          if(!MainEditFragment.repeat.isDays_of_month_setted()) {
            MainEditFragment.repeat.setDays_of_month_setted(true);
          }
        }
        else everymonth.setChecked(true);
        return true;
      case "everyyear":
        if(everyyear.isChecked()) {
          if(never.isChecked()) never.setChecked(false);
          if(everyday.isChecked()) everyday.setChecked(false);
          if(everyweekday.isChecked()) everyweekday.setChecked(false);
          if(everyweek.isChecked()) everyweek.setChecked(false);
          if(everymonth.isChecked()) everymonth.setChecked(false);
          if(custom.isChecked()) custom.setChecked(false);
          label.setSummary(label_str_everyyear);

          MainEditFragment.repeat.setLabel(label_str_everyyear);
          MainEditFragment.repeat.setSetted(1 << 3);
          MainEditFragment.repeat.setWhich_template(1 << 4);
          MainEditFragment.repeat.setInterval(1);
          mask_num = MainEditFragment.final_cal.get(Calendar.MONTH);
          MainEditFragment.repeat.setYear(1 << mask_num);
          MainEditFragment.repeat.setDay_of_month_of_year(MainEditFragment.final_cal.get(Calendar.DAY_OF_MONTH));
        }
        else everyyear.setChecked(true);
        return true;
      case "custom":
        if(custom.isChecked()) {
          if(never.isChecked()) never.setChecked(false);
          if(everyday.isChecked()) everyday.setChecked(false);
          if(everyweekday.isChecked()) everyweekday.setChecked(false);
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
