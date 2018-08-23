package com.example.hideaki.reminder;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import java.util.Calendar;

public class RepeatCustomPickerPreference extends Preference {

  private final String[] SCALE_LIST = {"日", "週", "月", "年"};

  static boolean day = false;
  static boolean week = false;
  static boolean month = false;
  static boolean year = false;
  private NumberPicker interval;
  private NumberPicker scale;
  private int mask_num;

  public RepeatCustomPickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    View view = View.inflate(getContext(), R.layout.repeat_custom_picker, null);
    return view;
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    //intervalの実装
    interval = view.findViewById(R.id.interval);
    interval.setDisplayedValues(null);
    interval.setMaxValue(1000);
    interval.setMinValue(1);
    if(MainEditFragment.repeat.getInterval() == 0) {
      MainEditFragment.repeat.setInterval(1);
    }
    interval.setValue(MainEditFragment.repeat.getInterval());
    interval.setOnScrollListener(new NumberPicker.OnScrollListener() {
      @Override
      public void onScrollStateChange(NumberPicker view, int scrollState) {
        switch(scrollState) {
          case SCROLL_STATE_IDLE:
            MainEditFragment.repeat.setInterval(interval.getValue());
        }
      }
    });

    //scaleの実装
    scale = view.findViewById(R.id.scale);
    scale.setDisplayedValues(null);
    scale.setMaxValue(SCALE_LIST.length);
    scale.setMinValue(1);
    if(MainEditFragment.repeat.getScale() == 0) {
      MainEditFragment.repeat.setScale(1);
      MainEditFragment.repeat.setDay(true);
      day = true;
      week = false;
      month = false;
      year = false;
    }
    scale.setValue(MainEditFragment.repeat.getScale());
    scale.setDisplayedValues(SCALE_LIST);
    if(MainEditFragment.repeat.getScale() == 1) {
      day = true;
    }
    else if(MainEditFragment.repeat.getScale() == 2) {
      week = true;
      RepeatCustomPickerFragment.addWeekPreference();
    }
    else if(MainEditFragment.repeat.getScale() == 3) {
      month = true;
      RepeatCustomPickerFragment.addDaysOfMonthPreference();
      RepeatCustomPickerFragment.addOnTheMonthPreference();
      if(MainEditFragment.repeat.isDays_of_month_setted()) {
        RepeatCustomPickerFragment.addDaysOfMonthPickerPreference();
      }
      else {
        RepeatCustomPickerFragment.addOnTheMonthPickerPreference();
      }
    }
    else if(MainEditFragment.repeat.getScale() == 4) {
      year = true;
      RepeatCustomPickerFragment.addYearPreference();
    }
    scale.setOnScrollListener(new NumberPicker.OnScrollListener() {
      @Override
      public void onScrollStateChange(NumberPicker view, int scrollState) {
        switch(scrollState) {
          case SCROLL_STATE_IDLE:
            MainEditFragment.repeat.setScale(scale.getValue());
            if(MainEditFragment.repeat.getScale() == 1) {

              MainEditFragment.repeat.setDay(true);

              if(week) {
                RepeatCustomPickerFragment.removeWeekPreference();
                week = false;
              }
              else if(month) {
                RepeatCustomPickerFragment.removeDaysOfMonthPreference();
                RepeatCustomPickerFragment.removeOnTheMonthPreference();
                if(MainEditFragment.repeat.isDays_of_month_setted()) {
                  RepeatCustomPickerFragment.removeDaysOfMonthPickerPreference();
                }
                else {
                  RepeatCustomPickerFragment.removeOnTheMonthPickerPreference();
                }
                month = false;
              }
              else if(year) {
                RepeatCustomPickerFragment.removeYearPreference();
                year = false;
              }

              day = true;
            }
            else if(MainEditFragment.repeat.getScale() == 2) {

              if(day) day = false;
              else if(month) {
                RepeatCustomPickerFragment.removeDaysOfMonthPreference();
                RepeatCustomPickerFragment.removeOnTheMonthPreference();
                if(MainEditFragment.repeat.isDays_of_month_setted()) {
                  RepeatCustomPickerFragment.removeDaysOfMonthPickerPreference();
                }
                else {
                  RepeatCustomPickerFragment.removeOnTheMonthPickerPreference();
                }
                month = false;
              }
              else if(year) {
                RepeatCustomPickerFragment.removeYearPreference();
                year = false;
              }

              RepeatCustomPickerFragment.addWeekPreference();
              week = true;
            }
            else if(MainEditFragment.repeat.getScale() == 3) {

              if(day) day = false;
              else if(week) {
                RepeatCustomPickerFragment.removeWeekPreference();
                week = false;
              }
              else if(year) {
                RepeatCustomPickerFragment.removeYearPreference();
                year = false;
              }

              RepeatCustomPickerFragment.addDaysOfMonthPreference();
              RepeatCustomPickerFragment.addOnTheMonthPreference();
              if(MainEditFragment.repeat.isDays_of_month_setted()) {
                RepeatCustomPickerFragment.addDaysOfMonthPickerPreference();
              }
              else {
                RepeatCustomPickerFragment.addOnTheMonthPickerPreference();
              }
              month = true;

              if(MainEditFragment.repeat.getDays_of_month() == 0) {
                mask_num = MainEditFragment.final_cal.get(Calendar.DAY_OF_MONTH);
                MainEditFragment.repeat.setDays_of_month(1 << (mask_num - 1));
                RepeatCustomPickerFragment.days_of_month.setChecked(true);
                RepeatCustomPickerFragment.on_the_month.setChecked(false);
                MainEditFragment.repeat.setDays_of_month_setted(true);
              }
            }
            else if(MainEditFragment.repeat.getScale() == 4) {

              if(day) day = false;
              else if(week) {
                RepeatCustomPickerFragment.removeWeekPreference();
                week = false;
              }
              else if(month) {
                RepeatCustomPickerFragment.removeDaysOfMonthPreference();
                RepeatCustomPickerFragment.removeOnTheMonthPreference();
                if(MainEditFragment.repeat.isDays_of_month_setted()) {
                  RepeatCustomPickerFragment.removeDaysOfMonthPickerPreference();
                }
                else {
                  RepeatCustomPickerFragment.removeOnTheMonthPickerPreference();
                }
                month = false;
              }

              RepeatCustomPickerFragment.addYearPreference();
              year = true;
            }
        }
      }
    });
  }
}