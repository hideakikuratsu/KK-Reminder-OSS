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

import java.util.Calendar;

public class RepeatCustomPickerFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

  private static final String[] DAY_OF_WEEK_LIST = {"月", "火", "水", "木", "金", "土", "日"};

  private ActionBar actionBar;
  private static PreferenceScreen rootPreferenceScreen;
  private static Preference picker;
  private static Preference week;
  static CheckBoxPreference days_of_month;
  static CheckBoxPreference on_the_month;
  private static Preference year;
  private int cal_day_of_week;
  private int cal_day_of_month;
  private int cal_month;
  private boolean match_to_template;
  private String tmp = "";
  private int mask_num;

  public static RepeatCustomPickerFragment newInstance() {

    return new RepeatCustomPickerFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.repeat_custom_item);
    setHasOptionsMenu(true);

    actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
    actionBar.setTitle(R.string.custom);

    rootPreferenceScreen = getPreferenceScreen();
    picker = findPreference("picker");
    week = findPreference("week");
    days_of_month = (CheckBoxPreference)findPreference("days_of_month");
    days_of_month.setChecked(MainEditFragment.repeat.isDays_of_month_setted());
    days_of_month.setOnPreferenceClickListener(this);
    on_the_month = (CheckBoxPreference)findPreference("on_the_month");
    on_the_month.setChecked(!MainEditFragment.repeat.isDays_of_month_setted());
    on_the_month.setOnPreferenceClickListener(this);
    year = findPreference("year");
    rootPreferenceScreen.removeAll();
    addPickerPreference();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    view.setBackgroundColor(getResources().getColor(android.R.color.background_light));

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    match_to_template = false;
    if(MainEditFragment.repeat.getInterval() == 1) {
      if(RepeatCustomPickerPreference.day) {
        match_to_template = true;
        RepeatEditFragment.everyday.setChecked(true);
        RepeatEditFragment.custom.setChecked(false);
        MainEditFragment.repeat.setSetted(1 << 0);
        MainEditFragment.repeat.setWhich_template(1 << 0);
        MainEditFragment.repeat.setLabel(getActivity().getResources().getString(R.string.everyday));
        RepeatEditFragment.label.setSummary(R.string.everyday);
      }
      else if(RepeatCustomPickerPreference.week) {
        cal_day_of_week = MainEditFragment.final_cal.get(Calendar.DAY_OF_WEEK);
        if(RepeatCustomWeekPickerPreference.week == (1 << (cal_day_of_week - 2)) ||
            RepeatCustomWeekPickerPreference.week == (1 << (cal_day_of_week + 5))) {
          match_to_template = true;
          RepeatEditFragment.everyweek.setChecked(true);
          RepeatEditFragment.custom.setChecked(false);
          MainEditFragment.repeat.setSetted(1 << 1);
          MainEditFragment.repeat.setWhich_template(1 << 2);
          MainEditFragment.repeat.setLabel(RepeatEditFragment.label_str_everyweek);
        }

        if(RepeatCustomWeekPickerPreference.week == Integer.parseInt("11111", 2)) {
          match_to_template = true;
          RepeatEditFragment.everyweekday.setChecked(true);
          RepeatEditFragment.custom.setChecked(false);
          MainEditFragment.repeat.setSetted(1 << 1);
          MainEditFragment.repeat.setWhich_template(1 << 1);
          MainEditFragment.repeat.setLabel(getActivity().getResources().getString(R.string.everyweekday));
          RepeatEditFragment.label.setSummary(R.string.everyweekday);
        }
      }
      else if(RepeatCustomPickerPreference.month && MainEditFragment.repeat.isDays_of_month_setted()) {
        cal_day_of_month = MainEditFragment.final_cal.get(Calendar.DAY_OF_MONTH);
        if(RepeatCustomDaysOfMonthPickerPreference.days_of_month == (1 << (cal_day_of_month - 1))) {
          match_to_template = true;
          RepeatEditFragment.everymonth.setChecked(true);
          RepeatEditFragment.custom.setChecked(false);
          MainEditFragment.repeat.setSetted(1 << 2);
          MainEditFragment.repeat.setWhich_template(1 << 3);
          MainEditFragment.repeat.setLabel(RepeatEditFragment.label_str_everymonth);
        }
      }
      else if(RepeatCustomPickerPreference.year) {
        cal_month = MainEditFragment.final_cal.get(Calendar.MONTH);
        if(RepeatCustomYearPickerPreference.year == (1 << cal_month)) {
          match_to_template = true;
          RepeatEditFragment.everyyear.setChecked(true);
          RepeatEditFragment.custom.setChecked(false);
          MainEditFragment.repeat.setSetted(1 << 3);
          MainEditFragment.repeat.setWhich_template(1 << 4);
          MainEditFragment.repeat.setLabel(RepeatEditFragment.label_str_everyyear);
          MainEditFragment.repeat.setDay_of_month_of_year(MainEditFragment.final_cal.get(Calendar.DAY_OF_MONTH));
        }
      }
    }

    if(!match_to_template) {
      MainEditFragment.repeat.setWhich_template(1 << 5);
      if(RepeatEditFragment.everyday.isChecked()) RepeatEditFragment.everyday.setChecked(false);
      if(RepeatEditFragment.everyweekday.isChecked()) RepeatEditFragment.everyweekday.setChecked(false);
      if(RepeatEditFragment.everyweek.isChecked()) RepeatEditFragment.everyweek.setChecked(false);
      if(RepeatEditFragment.everymonth.isChecked()) RepeatEditFragment.everymonth.setChecked(false);
      if(RepeatEditFragment.everyyear.isChecked()) RepeatEditFragment.everyyear.setChecked(false);
      if(!RepeatEditFragment.custom.isChecked()) RepeatEditFragment.custom.setChecked(true);

      if(RepeatCustomPickerPreference.day) {
        MainEditFragment.repeat.setSetted(1 << 0);
        RepeatEditFragment.label_str_custom = MainEditFragment.repeat.getInterval() - 1 + "日おき";
      }
      else if(RepeatCustomPickerPreference.week) {
        MainEditFragment.repeat.setSetted(1 << 1);
        if(MainEditFragment.repeat.getInterval() == 1) RepeatEditFragment.label_str_custom = "毎週";
        else RepeatEditFragment.label_str_custom = MainEditFragment.repeat.getInterval() - 1 + "週間おきの";

        for(int i = 0; i < 7; i++) {
          if((MainEditFragment.repeat.getWeek() & (1 << i)) != 0) {
            tmp += DAY_OF_WEEK_LIST[i] + ", ";
          }
        }
        tmp = tmp.substring(0, tmp.length() - 2);
        RepeatEditFragment.label_str_custom += tmp + "曜日";
      }
      else if(RepeatCustomPickerPreference.month) {
        MainEditFragment.repeat.setSetted(1 << 2);
        if(MainEditFragment.repeat.isDays_of_month_setted()) {
          if(MainEditFragment.repeat.getInterval() == 1) RepeatEditFragment.label_str_custom = "毎月";
          else RepeatEditFragment.label_str_custom = MainEditFragment.repeat.getInterval() - 1 + "ヶ月おきの";

          for(int i = 0; i < MainEditFragment.final_cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            if((MainEditFragment.repeat.getDays_of_month() & (1 << i)) != 0) {
              tmp += (i + 1) + ", ";
            }
          }
          if((MainEditFragment.repeat.getDays_of_month() & (1 << 30)) != 0) tmp += "最終, ";
          tmp = tmp.substring(0, tmp.length() - 2);
          RepeatEditFragment.label_str_custom += tmp + "日";

        }
        else if(!MainEditFragment.repeat.isDays_of_month_setted()) {
          if(MainEditFragment.repeat.getInterval() == 1) RepeatEditFragment.label_str_custom = "毎月";
          else RepeatEditFragment.label_str_custom = MainEditFragment.repeat.getInterval() - 1 + "ヶ月おきの";

          if(MainEditFragment.repeat.getOrdinal_number() < 5) {
            RepeatEditFragment.label_str_custom += "第" + MainEditFragment.repeat.getOrdinal_number();
          }
          else RepeatEditFragment.label_str_custom += "最終週の";

          if(MainEditFragment.repeat.getOn_the_month().ordinal() < 7) {
            RepeatEditFragment.label_str_custom +=
                DAY_OF_WEEK_LIST[MainEditFragment.repeat.getOn_the_month().ordinal()] + "曜日";
          }
          else if(MainEditFragment.repeat.getOn_the_month().ordinal() == 7) {
            RepeatEditFragment.label_str_custom += "平日";
          }
          else if(MainEditFragment.repeat.getOn_the_month().ordinal() == 8) {
            RepeatEditFragment.label_str_custom += "週末";
          }
        }
      }
      else if(RepeatCustomPickerPreference.year) {
        MainEditFragment.repeat.setSetted(1 << 3);
        MainEditFragment.repeat.setDay_of_month_of_year(MainEditFragment.final_cal.get(Calendar.DAY_OF_MONTH));
        if(MainEditFragment.repeat.getInterval() == 1) RepeatEditFragment.label_str_custom = "毎年";
        else RepeatEditFragment.label_str_custom = MainEditFragment.repeat.getInterval() - 1 + "年おきの";

        for(int i = 0; i < 12; i++) {
          if((MainEditFragment.repeat.getYear() & (1 << i)) != 0) {
            tmp += (i + 1) + ", ";
          }
        }
        tmp = tmp.substring(0, tmp.length() - 2);

        RepeatEditFragment.label_str_custom +=
            tmp + "月の" + MainEditFragment.final_cal.get(Calendar.DAY_OF_MONTH) + "日";
      }

      MainEditFragment.repeat.setLabel(RepeatEditFragment.label_str_custom);
    }

    actionBar.setTitle(R.string.repeat);
    getFragmentManager().popBackStack();
    return super.onOptionsItemSelected(item);
  }

  public static void addPickerPreference() {
    rootPreferenceScreen.addPreference(picker);
  }

  public static void addWeekPreference() {
    rootPreferenceScreen.addPreference(week);
  }

  public static void addDaysOfMonthPreference() {
    rootPreferenceScreen.addPreference(days_of_month);
  }

  public static void addOnTheMonthPreference() {
    rootPreferenceScreen.addPreference(on_the_month);
  }

  public static void addYearPreference() {
    rootPreferenceScreen.addPreference(year);
  }

  public static void removeWeekPreference() {
    rootPreferenceScreen.removePreference(week);
  }

  public static void removeDaysOfMonthPreference() {
    rootPreferenceScreen.removePreference(days_of_month);
  }

  public static void removeOnTheMonthPreference() {
    rootPreferenceScreen.removePreference(on_the_month);
  }

  public static void removeYearPreference() {
    rootPreferenceScreen.removePreference(year);
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
      case "days_of_month":
        if(days_of_month.isChecked()) {
          if(MainEditFragment.repeat.getDays_of_month() == 0) {
            mask_num = MainEditFragment.final_cal.get(Calendar.DAY_OF_MONTH);
            MainEditFragment.repeat.setDays_of_month(1 << (mask_num - 1));
          }
          MainEditFragment.repeat.setDays_of_month_setted(true);
          on_the_month.setChecked(false);
        }
        else days_of_month.setChecked(true);
        transitionFragment(RepeatCustomDaysOfMonthPickerFragment.newInstance());
        return true;
      case "on_the_month":
        if(on_the_month.isChecked()) {
          MainEditFragment.repeat.setDays_of_month_setted(false);
          days_of_month.setChecked(false);
        }
        else on_the_month.setChecked(true);
        transitionFragment(RepeatCustomOnTheMonthPickerFragment.newInstance());
        return true;
    }
    return false;
  }
}