package com.hideaki.kk_reminder;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceScreen;
import androidx.transition.Fade;
import androidx.transition.Transition;

import static com.hideaki.kk_reminder.UtilClass.LOCALE;
import static com.hideaki.kk_reminder.UtilClass.appendTimeLimitLabelOfDayRepeat;
import static java.util.Objects.requireNonNull;

public class DayRepeatEditFragment extends BasePreferenceFragmentCompat
    implements MyCheckBoxPreference.MyCheckBoxPreferenceCheckedChangeListener {

  private static final String[] MONTH_LIST_EN = {
    "Jan.", "Feb.", "Mar.", "Apr.", "May", "Jun.", "Jul.",
    "Aug.", "Sep.", "Oct.", "Nov.", "Dec."
  };
  CheckBoxPreference never;
  CheckBoxPreference everyday;
  CheckBoxPreference everyWeekday;
  CheckBoxPreference everyWeek;
  CheckBoxPreference everyMonth;
  CheckBoxPreference everyYear;
  CheckBoxPreference custom;
  CheckBoxPreference timeLimit;
  PreferenceScreen label;
  static String labelStrEveryWeek;
  static String labelStrEveryMonth;
  static String labelStrEveryYear;
  private int maskNum;
  private MainActivity activity;

  public static DayRepeatEditFragment newInstance() {

    return new DayRepeatEditFragment();
  }

  @Override
  public void onAttach(@NonNull Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
  }

  @Override
  public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {

    addPreferencesFromResource(R.xml.repeat_edit);
    setHasOptionsMenu(true);

    never = (CheckBoxPreference)findPreference("never");
    everyday = (CheckBoxPreference)findPreference("everyday");
    everyWeekday = (CheckBoxPreference)findPreference("every_weekday");
    everyWeek = (CheckBoxPreference)findPreference("every_week");
    everyMonth = (CheckBoxPreference)findPreference("every_month");
    everyYear = (CheckBoxPreference)findPreference("every_year");
    custom = (CheckBoxPreference)findPreference("custom");
    timeLimit = (CheckBoxPreference)findPreference("time_limit");
    label = (PreferenceScreen)findPreference("label");

    ((MyCheckBoxPreference)never).setOnMyCheckBoxPreferenceCheckedChangeListener(this);
    ((MyCheckBoxPreference)everyday).setOnMyCheckBoxPreferenceCheckedChangeListener(this);
    ((MyCheckBoxPreference)everyWeekday).setOnMyCheckBoxPreferenceCheckedChangeListener(this);
    ((MyCheckBoxPreference)everyWeek).setOnMyCheckBoxPreferenceCheckedChangeListener(this);
    ((MyCheckBoxPreference)everyMonth).setOnMyCheckBoxPreferenceCheckedChangeListener(this);
    ((MyCheckBoxPreference)everyYear).setOnMyCheckBoxPreferenceCheckedChangeListener(this);
    ((MyCheckBoxPreference)custom).setOnMyCheckBoxPreferenceCheckedChangeListener(this);
    ((MyCheckBoxPreference)timeLimit).setOnMyCheckBoxPreferenceCheckedChangeListener(this);
  }

  @Override
  public View onCreateView(
    LayoutInflater inflater,
    @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState
  ) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    requireNonNull(view);

    if(activity.isDarkMode) {
      view.setBackgroundColor(activity.backgroundMaterialDarkColor);
    }
    else {
      view.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));
    }

    Toolbar toolbar = activity.findViewById(R.id.toolbar_layout);
    activity.setSupportActionBar(toolbar);
    final ActionBar actionBar = activity.getSupportActionBar();
    requireNonNull(actionBar);

    activity.drawerToggle.setDrawerIndicatorEnabled(false);
    actionBar.setHomeAsUpIndicator(activity.upArrow);
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(R.string.repeat_day_unit);

    labelStrEveryWeek = getString(R.string.every_week);
    if(LOCALE.equals(Locale.JAPAN)) {
      labelStrEveryWeek += DateFormat.format("E曜日", MainEditFragment.finalCal);
    }
    else {
      labelStrEveryWeek += DateFormat.format(" on E", MainEditFragment.finalCal);
    }
    everyWeek.setTitle(labelStrEveryWeek);

    labelStrEveryMonth = getString(R.string.every_month);
    if(LOCALE.equals(Locale.JAPAN)) {
      labelStrEveryMonth += MainEditFragment.finalCal.get(Calendar.DAY_OF_MONTH) + "日";
    }
    else {
      int dayOfMonth = MainEditFragment.finalCal.get(Calendar.DAY_OF_MONTH);
      labelStrEveryMonth += " on the " + dayOfMonth;
      if(dayOfMonth == 1) {
        labelStrEveryMonth += "st";
      }
      else if(dayOfMonth == 2) {
        labelStrEveryMonth += "nd";
      }
      else if(dayOfMonth == 3) {
        labelStrEveryMonth += "rd";
      }
      else {
        labelStrEveryMonth += "th";
      }
    }
    everyMonth.setTitle(labelStrEveryMonth);

    labelStrEveryYear = getString(R.string.every_year);
    if(LOCALE.equals(Locale.JAPAN)) {
      labelStrEveryYear += DateFormat.format("M月d日", MainEditFragment.finalCal);
    }
    else {
      int dayOfMonth = MainEditFragment.finalCal.get(Calendar.DAY_OF_MONTH);
      labelStrEveryYear += " on the " + dayOfMonth;
      if(dayOfMonth == 1) {
        labelStrEveryYear += "st";
      }
      else if(dayOfMonth == 2) {
        labelStrEveryYear += "nd";
      }
      else if(dayOfMonth == 3) {
        labelStrEveryYear += "rd";
      }
      else {
        labelStrEveryYear += "th";
      }

      labelStrEveryYear += " of " + MONTH_LIST_EN[MainEditFragment.finalCal.get(Calendar.MONTH)];
    }

    everyYear.setTitle(labelStrEveryYear);

    // チェック状態の初期化
    never.setChecked(false);
    everyday.setChecked(false);
    everyWeekday.setChecked(false);
    everyWeek.setChecked(false);
    everyMonth.setChecked(false);
    everyYear.setChecked(false);
    custom.setChecked(false);
    timeLimit.setChecked(false);
    switch(MainEditFragment.dayRepeat.getWhichTemplate()) {
      case 0: {
        never.setChecked(true);
        break;
      }
      case 1: {
        everyday.setChecked(true);
        break;
      }
      case 1 << 1: {
        everyWeekday.setChecked(true);
        break;
      }
      case 1 << 2: {
        everyWeek.setChecked(true);

        MainEditFragment.dayRepeat.setLabel(appendTimeLimitLabelOfDayRepeat(
            MainEditFragment.dayRepeat.getTimeLimit(), labelStrEveryWeek
        ));
        maskNum = MainEditFragment.finalCal.get(Calendar.DAY_OF_WEEK);
        maskNum = maskNum < 2 ? maskNum + 5 : maskNum - 2;
        MainEditFragment.dayRepeat.setWeek(1 << maskNum);
        break;
      }
      case 1 << 3: {
        everyMonth.setChecked(true);

        MainEditFragment.dayRepeat.setLabel(appendTimeLimitLabelOfDayRepeat(
            MainEditFragment.dayRepeat.getTimeLimit(), labelStrEveryMonth
        ));
        maskNum = MainEditFragment.finalCal.get(Calendar.DAY_OF_MONTH);
        MainEditFragment.dayRepeat.setDaysOfMonth(1 << (maskNum - 1));
        break;
      }
      case 1 << 4: {
        everyYear.setChecked(true);

        MainEditFragment.dayRepeat.setLabel(appendTimeLimitLabelOfDayRepeat(
            MainEditFragment.dayRepeat.getTimeLimit(), labelStrEveryYear
        ));
        maskNum = MainEditFragment.finalCal.get(Calendar.MONTH);
        MainEditFragment.dayRepeat.setYear(1 << maskNum);
        MainEditFragment.dayRepeat.setDayOfMonthOfYear(MainEditFragment.finalCal.get(Calendar.DAY_OF_MONTH));
        break;
      }
      case 1 << 5: {
        custom.setChecked(true);
        break;
      }
    }

    //Time Limitのラベルの初期化
    new Handler(Looper.getMainLooper()).post(() -> {

      ((MyCheckBoxPreference)timeLimit).setMySummary(activity.getString(R.string.time_limit));
      ((MyCheckBoxPreference)timeLimit).showMySummary();
    });
    if(MainEditFragment.dayRepeat.getTimeLimit() != null) {
      MainEditFragment.tmpTimeLimit = (Calendar)MainEditFragment.dayRepeat.getTimeLimit().clone();
      timeLimit.setChecked(true);
      if(LOCALE.equals(Locale.JAPAN)) {
        this.timeLimit.setTitle(DateFormat.format(
            "yyyy年M月d日(E)",
            MainEditFragment.dayRepeat.getTimeLimit()
            ));
      }
      else {
        this.timeLimit.setTitle(DateFormat.format(
            "E, MMM d, yyyy",
            MainEditFragment.dayRepeat.getTimeLimit()
        ));
      }
    }
    else {
      this.timeLimit.setTitle(R.string.none);
    }

    // ラベルの初期化
    String labelStr = MainEditFragment.dayRepeat.getLabel();
    if(labelStr == null || labelStr.equals(getString(R.string.none))) {
      label.setSummary(R.string.non_repeat);
    }
    else {
      label.setSummary(labelStr);
    }

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {

    FragmentManager manager = requireNonNull(activity.getSupportFragmentManager());
    manager.popBackStack();
    return super.onOptionsItemSelected(item);
  }

  private void transitionFragment(PreferenceFragmentCompat next) {

    Transition transition = new Fade()
      .setDuration(300);
    this.setExitTransition(transition);
    next.setEnterTransition(transition);
    FragmentManager manager = requireNonNull(activity.getSupportFragmentManager());
    manager
      .beginTransaction()
      .remove(this)
      .add(R.id.content, next)
      .addToBackStack(null)
      .commit();
  }

  @Override
  public void onCheckedChange(String key, boolean checked) {

    if(key.equals("time_limit")) {
      if(checked) {
        DayRepeatTimeLimitPickerDialogFragment dialog =
            new DayRepeatTimeLimitPickerDialogFragment(this);
        dialog.show(activity.getSupportFragmentManager(), "day_repeat_time_limit_picker");
      }
      else {
        if(!never.isChecked()) {
          String label = this.label.getSummary().toString();
          System.out.println(label);
          String targetString;
          if(LOCALE.equals(Locale.JAPAN)) {
            targetString = " \\(.*まで\\)$";
          }
          else {
            targetString = " until.*$";
          }
          label = label.replaceAll(targetString, "");
          this.label.setSummary(label);
          MainEditFragment.dayRepeat.setLabel(label);
        }
        MainEditFragment.dayRepeat.setTimeLimit(null);
        timeLimit.setTitle(getString(R.string.none));
      }
    }
    else {
      never.setChecked(false);
      everyday.setChecked(false);
      everyWeekday.setChecked(false);
      everyWeek.setChecked(false);
      everyMonth.setChecked(false);
      everyYear.setChecked(false);
      custom.setChecked(false);

      switch(key) {
        case "never": {
          never.setChecked(true);
          this.label.setSummary(R.string.non_repeat);

          MainEditFragment.dayRepeat.setLabel(getString(R.string.none));
          MainEditFragment.dayRepeat.setWhichSet(0);
          MainEditFragment.dayRepeat.setWhichTemplate(0);

          break;
        }
        case "everyday": {
          everyday.setChecked(true);
          String label = appendTimeLimitLabelOfDayRepeat(
              MainEditFragment.dayRepeat.getTimeLimit(), getString(R.string.everyday)
          );
          this.label.setSummary(label);

          MainEditFragment.dayRepeat.setLabel(label);
          MainEditFragment.dayRepeat.setWhichSet(1);
          MainEditFragment.dayRepeat.setWhichTemplate(1);
          MainEditFragment.dayRepeat.setInterval(1);
          MainEditFragment.dayRepeat.setIsDay(true);

          break;
        }
        case "every_weekday": {
          everyWeekday.setChecked(true);
          String label = appendTimeLimitLabelOfDayRepeat(
              MainEditFragment.dayRepeat.getTimeLimit(), getString(R.string.every_weekday)
          );
          this.label.setSummary(label);

          MainEditFragment.dayRepeat.setLabel(label);
          MainEditFragment.dayRepeat.setWhichSet(1 << 1);
          MainEditFragment.dayRepeat.setWhichTemplate(1 << 1);
          MainEditFragment.dayRepeat.setInterval(1);
          MainEditFragment.dayRepeat.setWeek(Integer.parseInt("11111", 2));

          break;
        }
        case "every_week": {
          everyWeek.setChecked(true);
          String label = appendTimeLimitLabelOfDayRepeat(
              MainEditFragment.dayRepeat.getTimeLimit(), labelStrEveryWeek
          );
          this.label.setSummary(label);

          MainEditFragment.dayRepeat.setLabel(label);
          MainEditFragment.dayRepeat.setWhichSet(1 << 1);
          MainEditFragment.dayRepeat.setWhichTemplate(1 << 2);
          MainEditFragment.dayRepeat.setInterval(1);
          maskNum = MainEditFragment.finalCal.get(Calendar.DAY_OF_WEEK);
          maskNum = maskNum < 2 ? maskNum + 5 : maskNum - 2;
          MainEditFragment.dayRepeat.setWeek(1 << maskNum);

          break;
        }
        case "every_month": {
          everyMonth.setChecked(true);
          String label = appendTimeLimitLabelOfDayRepeat(
              MainEditFragment.dayRepeat.getTimeLimit(), labelStrEveryMonth
          );
          this.label.setSummary(label);

          MainEditFragment.dayRepeat.setLabel(label);
          MainEditFragment.dayRepeat.setWhichSet(1 << 2);
          MainEditFragment.dayRepeat.setWhichTemplate(1 << 3);
          MainEditFragment.dayRepeat.setInterval(1);
          maskNum = MainEditFragment.finalCal.get(Calendar.DAY_OF_MONTH);
          MainEditFragment.dayRepeat.setDaysOfMonth(1 << (maskNum - 1));
          if(!MainEditFragment.dayRepeat.isDaysOfMonthSet()) {
            MainEditFragment.dayRepeat.setIsDaysOfMonthSet(true);
          }

          break;
        }
        case "every_year": {
          everyYear.setChecked(true);
          String label = appendTimeLimitLabelOfDayRepeat(
              MainEditFragment.dayRepeat.getTimeLimit(), labelStrEveryYear
          );
          this.label.setSummary(label);

          MainEditFragment.dayRepeat.setLabel(label);
          MainEditFragment.dayRepeat.setWhichSet(1 << 3);
          MainEditFragment.dayRepeat.setWhichTemplate(1 << 4);
          MainEditFragment.dayRepeat.setInterval(1);
          maskNum = MainEditFragment.finalCal.get(Calendar.MONTH);
          MainEditFragment.dayRepeat.setYear(1 << maskNum);
          MainEditFragment.dayRepeat.setDayOfMonthOfYear(
              MainEditFragment.finalCal.get(Calendar.DAY_OF_MONTH)
          );

          break;
        }
        case "custom": {
          custom.setChecked(true);
          DayRepeatCustomPickerFragment fragment = DayRepeatCustomPickerFragment.newInstance();
          fragment.setDayRepeatEditFragment(this);
          transitionFragment(fragment);

          break;
        }
      }
    }
  }
}
