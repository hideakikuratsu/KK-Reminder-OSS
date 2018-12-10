package com.hideaki.kk_reminder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

public class DayRepeatCustomPickerFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

  private static final String[] DAY_OF_WEEK_LIST_JA = {"月", "火", "水", "木", "金", "土", "日"};
  private static final String[] DAY_OF_WEEK_LIST_EN = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
  private static final String[] MONTH_LIST_EN = {"Jan.", "Feb.", "Mar.", "Apr.", "May", "Jun.", "Jul.",
      "Aug.", "Sep.", "Oct.", "Nov.", "Dec."};

  private static PreferenceScreen rootPreferenceScreen;
  private static Preference picker;
  private static Preference week;
  static CheckBoxPreference days_of_month;
  private static Preference days_of_month_picker;
  static CheckBoxPreference on_the_month;
  private static Preference on_the_month_picker;
  private static Preference year;
  private MainActivity activity;
  private static Locale locale = Locale.getDefault();

  public static DayRepeatCustomPickerFragment newInstance() {

    return new DayRepeatCustomPickerFragment();
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
    addPreferencesFromResource(R.xml.repeat_custom_item);
    setHasOptionsMenu(true);

    rootPreferenceScreen = getPreferenceScreen();
    picker = findPreference("picker");
    week = findPreference("week");
    days_of_month = (CheckBoxPreference)findPreference("days_of_month");
    days_of_month.setChecked(MainEditFragment.dayRepeat.isDays_of_month_setted());
    days_of_month.setOnPreferenceClickListener(this);
    days_of_month_picker = findPreference("days_of_month_picker");
    on_the_month = (CheckBoxPreference)findPreference("on_the_month");
    on_the_month.setChecked(!MainEditFragment.dayRepeat.isDays_of_month_setted());
    on_the_month.setOnPreferenceClickListener(this);
    on_the_month_picker = findPreference("on_the_month_picker");
    year = findPreference("year");
    rootPreferenceScreen.removeAll();
    addPickerPreference();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    checkNotNull(view);

    view.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));
    view.setFocusableInTouchMode(true);
    view.requestFocus();
    view.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
          registerCustomRepeat();
        }
        return false;
      }
    });

    Toolbar toolbar = activity.findViewById(R.id.toolbar_layout);
    activity.setSupportActionBar(toolbar);
    ActionBar actionBar = activity.getSupportActionBar();
    checkNotNull(actionBar);

    activity.drawerToggle.setDrawerIndicatorEnabled(false);
    actionBar.setHomeAsUpIndicator(activity.upArrow);
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(R.string.custom);

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    registerCustomRepeat();

    getFragmentManager().popBackStack();
    return super.onOptionsItemSelected(item);
  }

  private void registerCustomRepeat() {

    boolean match_to_template = false;
    if(MainEditFragment.dayRepeat.getInterval() == 1) {
      if(DayRepeatCustomPickerPreference.day) {
        match_to_template = true;
        DayRepeatEditFragment.everyday.setChecked(true);
        DayRepeatEditFragment.custom.setChecked(false);
        MainEditFragment.dayRepeat.setSetted(1);
        MainEditFragment.dayRepeat.setWhich_template(1);
        MainEditFragment.dayRepeat.setLabel(getString(R.string.everyday));
        DayRepeatEditFragment.label.setSummary(R.string.everyday);
      }
      else if(DayRepeatCustomPickerPreference.week) {
        int cal_day_of_week = MainEditFragment.final_cal.get(Calendar.DAY_OF_WEEK);
        if(DayRepeatCustomWeekPickerPreference.week == (1 << (cal_day_of_week - 2)) ||
            DayRepeatCustomWeekPickerPreference.week == (1 << (cal_day_of_week + 5))) {
          match_to_template = true;
          DayRepeatEditFragment.everyweek.setChecked(true);
          DayRepeatEditFragment.custom.setChecked(false);
          MainEditFragment.dayRepeat.setSetted(1 << 1);
          MainEditFragment.dayRepeat.setWhich_template(1 << 2);
          MainEditFragment.dayRepeat.setLabel(DayRepeatEditFragment.label_str_everyweek);
        }

        if(DayRepeatCustomWeekPickerPreference.week == Integer.parseInt("11111", 2)) {
          match_to_template = true;
          DayRepeatEditFragment.everyweekday.setChecked(true);
          DayRepeatEditFragment.custom.setChecked(false);
          MainEditFragment.dayRepeat.setSetted(1 << 1);
          MainEditFragment.dayRepeat.setWhich_template(1 << 1);
          MainEditFragment.dayRepeat.setLabel(getString(R.string.everyweekday));
          DayRepeatEditFragment.label.setSummary(R.string.everyweekday);
        }
      }
      else if(DayRepeatCustomPickerPreference.month && MainEditFragment.dayRepeat.isDays_of_month_setted()) {
        int cal_day_of_month = MainEditFragment.final_cal.get(Calendar.DAY_OF_MONTH);
        if(DayRepeatCustomDaysOfMonthPickerPreference.days_of_month == (1 << (cal_day_of_month - 1))) {
          match_to_template = true;
          DayRepeatEditFragment.everymonth.setChecked(true);
          DayRepeatEditFragment.custom.setChecked(false);
          MainEditFragment.dayRepeat.setSetted(1 << 2);
          MainEditFragment.dayRepeat.setWhich_template(1 << 3);
          MainEditFragment.dayRepeat.setLabel(DayRepeatEditFragment.label_str_everymonth);
        }
      }
      else if(DayRepeatCustomPickerPreference.year) {
        int cal_month = MainEditFragment.final_cal.get(Calendar.MONTH);
        if(DayRepeatCustomYearPickerPreference.year == (1 << cal_month)) {
          match_to_template = true;
          DayRepeatEditFragment.everyyear.setChecked(true);
          DayRepeatEditFragment.custom.setChecked(false);
          MainEditFragment.dayRepeat.setSetted(1 << 3);
          MainEditFragment.dayRepeat.setWhich_template(1 << 4);
          MainEditFragment.dayRepeat.setLabel(DayRepeatEditFragment.label_str_everyyear);
          MainEditFragment.dayRepeat.setDay_of_month_of_year(MainEditFragment.final_cal.get(Calendar.DAY_OF_MONTH));
        }
      }
    }

    if(!match_to_template) {
      MainEditFragment.dayRepeat.setWhich_template(1 << 5);
      if(DayRepeatEditFragment.everyday.isChecked()) DayRepeatEditFragment.everyday.setChecked(false);
      if(DayRepeatEditFragment.everyweekday.isChecked()) DayRepeatEditFragment.everyweekday.setChecked(false);
      if(DayRepeatEditFragment.everyweek.isChecked()) DayRepeatEditFragment.everyweek.setChecked(false);
      if(DayRepeatEditFragment.everymonth.isChecked()) DayRepeatEditFragment.everymonth.setChecked(false);
      if(DayRepeatEditFragment.everyyear.isChecked()) DayRepeatEditFragment.everyyear.setChecked(false);
      if(!DayRepeatEditFragment.custom.isChecked()) DayRepeatEditFragment.custom.setChecked(true);

      String label = "";
      String tmp = "";
      StringBuilder stringBuilder;
      int interval = MainEditFragment.dayRepeat.getInterval();

      if(DayRepeatCustomPickerPreference.day) {
        MainEditFragment.dayRepeat.setSetted(1);
        if(locale.equals(Locale.JAPAN)) {
          label = getString(R.string.per_day, MainEditFragment.dayRepeat.getInterval() - 1);
        }
        else {
          label = getString(R.string.per_day, MainEditFragment.dayRepeat.getInterval());
        }
      }
      else if(DayRepeatCustomPickerPreference.week) {
        MainEditFragment.dayRepeat.setSetted(1 << 1);
        if(interval == 1 && locale.equals(Locale.JAPAN)) label = "毎週";
        else {
          if(locale.equals(Locale.JAPAN)) {
            label = getResources().getQuantityString(R.plurals.per_week,
                interval - 1, interval - 1);
          }
          else {
            label = getResources().getQuantityString(R.plurals.per_week, interval, interval) + " on ";
          }
        }

        stringBuilder = new StringBuilder(tmp);
        int week = MainEditFragment.dayRepeat.getWeek();
        for(int i = 0; i < 7; i++) {
          if((week & (1 << i)) != 0) {
            if(locale.equals(Locale.JAPAN)) {
              stringBuilder.append(DAY_OF_WEEK_LIST_JA[i]).append(", ");
            }
            else stringBuilder.append(DAY_OF_WEEK_LIST_EN[i]).append(", ");
          }
        }
        tmp = stringBuilder.substring(0, stringBuilder.length() - 2);
        if(locale.equals(Locale.JAPAN)) label += tmp + "曜日";
        else label += tmp;
      }
      else if(DayRepeatCustomPickerPreference.month) {
        MainEditFragment.dayRepeat.setSetted(1 << 2);
        if(interval == 1 && locale.equals(Locale.JAPAN)) label = "毎月";
        else {
          if(locale.equals(Locale.JAPAN)) {
            label = getResources().getQuantityString(R.plurals.per_month,
                interval - 1, interval - 1);
          }
          else {
            label = getResources().getQuantityString(R.plurals.per_month, interval, interval) + " on the ";
          }
        }

        if(MainEditFragment.dayRepeat.isDays_of_month_setted()) {
          stringBuilder = new StringBuilder(tmp);
          int size = MainEditFragment.final_cal.getActualMaximum(Calendar.DAY_OF_MONTH);
          int days_of_month = MainEditFragment.dayRepeat.getDays_of_month();
          for(int i = 0; i < size; i++) {
            if((days_of_month & (1 << i)) != 0) {
              stringBuilder.append(i + 1).append(", ");
            }
          }
          if((days_of_month & (1 << 30)) != 0) {
            if(locale.equals(Locale.JAPAN)) {
              stringBuilder.append("最終, ");
            }
            else stringBuilder.append("Last, ");
          }
          tmp = stringBuilder.substring(0, stringBuilder.length() - 2);
          if(locale.equals(Locale.JAPAN)) label += tmp + "日";
          else label += tmp;
        }
        else if(!MainEditFragment.dayRepeat.isDays_of_month_setted()) {
          if(MainEditFragment.dayRepeat.getOrdinal_number() < 5) {
            if(locale.equals(Locale.JAPAN)) {
              label += "第" + MainEditFragment.dayRepeat.getOrdinal_number();
            }
            else {
              int ordinal_num = MainEditFragment.dayRepeat.getOrdinal_number();
              String ordinal_str = ordinal_num + "";
              if(ordinal_num == 1) ordinal_str += "st";
              else if(ordinal_num == 2) ordinal_str += "nd";
              else if(ordinal_num == 3) ordinal_str += "rd";
              else ordinal_str += "th";
              label += ordinal_str + " ";
            }
          }
          else {
            if(locale.equals(Locale.JAPAN)) label += "最終週の";
            else label += "Last ";
          }

          if(MainEditFragment.dayRepeat.getOn_the_month().ordinal() < 7) {
            if(locale.equals(Locale.JAPAN)) {
              label += DAY_OF_WEEK_LIST_JA[MainEditFragment.dayRepeat.getOn_the_month().ordinal()] + "曜日";
            }
            else {
              label += DAY_OF_WEEK_LIST_EN[MainEditFragment.dayRepeat.getOn_the_month().ordinal()];
            }
          }
          else if(MainEditFragment.dayRepeat.getOn_the_month().ordinal() == 7) {
            if(locale.equals(Locale.JAPAN)) label += "平日";
            else label += "Weekday";
          }
          else if(MainEditFragment.dayRepeat.getOn_the_month().ordinal() == 8) {
            if(locale.equals(Locale.JAPAN)) label += "週末";
            else label += "Weekend day";
          }
        }
      }
      else if(DayRepeatCustomPickerPreference.year) {
        MainEditFragment.dayRepeat.setSetted(1 << 3);
        MainEditFragment.dayRepeat.setDay_of_month_of_year(MainEditFragment.final_cal.get(Calendar.DAY_OF_MONTH));
        if(interval == 1 && locale.equals(Locale.JAPAN)) label = "毎年";
        else {
          if(locale.equals(Locale.JAPAN)) {
            label = getResources().getQuantityString(R.plurals.per_year,
                interval - 1, interval - 1);
          }
          else {
            label = getResources().getQuantityString(R.plurals.per_year, interval, interval) + " on the ";
            int day_of_month = MainEditFragment.final_cal.get(Calendar.DAY_OF_MONTH);
            label += day_of_month + "";
            if(day_of_month == 1) label += "st";
            else if(day_of_month == 2) label += "nd";
            else if(day_of_month == 3) label += "rd";
            else label += "th";
            label += " of ";
          }
        }

        stringBuilder = new StringBuilder(tmp);
        int year = MainEditFragment.dayRepeat.getYear();
        for(int i = 0; i < 12; i++) {
          if((year & (1 << i)) != 0) {
            if(locale.equals(Locale.JAPAN)) {
              stringBuilder.append(i + 1).append(", ");
            }
            else stringBuilder.append(MONTH_LIST_EN[i]).append(", ");
          }
        }
        tmp = stringBuilder.substring(0, stringBuilder.length() - 2);

        if(locale.equals(Locale.JAPAN)) {
          label += tmp + "月の" + MainEditFragment.final_cal.get(Calendar.DAY_OF_MONTH) + "日";
        }
        else label += tmp;
      }

      DayRepeatEditFragment.label_str_custom = label;
      MainEditFragment.dayRepeat.setLabel(label);
    }
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

  public static void addDaysOfMonthPickerPreference() {
    rootPreferenceScreen.addPreference(days_of_month_picker);
  }

  public static void addOnTheMonthPreference() {
    rootPreferenceScreen.addPreference(on_the_month);
  }

  public static void addOnTheMonthPickerPreference() {
    rootPreferenceScreen.addPreference(on_the_month_picker);
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

  public static void removeDaysOfMonthPickerPreference() {
    rootPreferenceScreen.removePreference(days_of_month_picker);
  }

  public static void removeOnTheMonthPreference() {
    rootPreferenceScreen.removePreference(on_the_month);
  }

  public static void removeOnTheMonthPickerPreference() {
    rootPreferenceScreen.removePreference(on_the_month_picker);
  }

  public static void removeYearPreference() {
    rootPreferenceScreen.removePreference(year);
  }

  @Override
  public boolean onPreferenceClick(Preference preference) {

    switch(preference.getKey()) {
      case "days_of_month": {
        if(days_of_month.isChecked()) {
          MainEditFragment.dayRepeat.setDays_of_month_setted(true);
          on_the_month.setChecked(false);
          addDaysOfMonthPickerPreference();
          removeOnTheMonthPickerPreference();
        }
        else days_of_month.setChecked(true);
        return true;
      }
      case "on_the_month": {
        if(on_the_month.isChecked()) {
          MainEditFragment.dayRepeat.setDays_of_month_setted(false);
          days_of_month.setChecked(false);
          addOnTheMonthPickerPreference();
          removeDaysOfMonthPickerPreference();
        }
        else on_the_month.setChecked(true);
        return true;
      }
    }

    return false;
  }
}