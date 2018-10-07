package com.hideaki.kk_reminder;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import java.util.Locale;

public class DayRepeatCustomOnTheMonthPickerPreference extends Preference {

  private static final String[] ORDINAL_NUMBER_LIST_JA = {"第一", "第二", "第三", "第四", "最終"};
  private static final String[] ORDINAL_NUMBER_LIST_EN = {"1st", "2nd", "3rd", "4th", "Last"};
  private static final String[] DAY_OF_WEEK_LIST_JA = {"月曜日", "火曜日", "水曜日", "木曜日", "金曜日",
      "土曜日", "日曜日", "平日", "週末"};
  private static final String[] DAY_OF_WEEK_LIST_EN = {"Mon", "Tue", "Wed", "Thu", "Fri",
      "Sat", "Sun", "Weekday", "Weekend Day"};

  private NumberPicker ordinal_number;
  private NumberPicker day_of_week;
  private static Locale locale = Locale.getDefault();

  public DayRepeatCustomOnTheMonthPickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    return View.inflate(getContext(), R.layout.repeat_custom_on_the_month_picker, null);
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    //ordinal_numberの実装
    ordinal_number = view.findViewById(R.id.ordinal_number);
    ordinal_number.setDisplayedValues(null);
    ordinal_number.setMaxValue(ORDINAL_NUMBER_LIST_JA.length);
    ordinal_number.setMinValue(1);
    if(MainEditFragment.dayRepeat.getOrdinal_number() == 0) {
      MainEditFragment.dayRepeat.setOrdinal_number(1);
    }
    ordinal_number.setValue(MainEditFragment.dayRepeat.getOrdinal_number());
    if(locale.equals(Locale.JAPAN)) {
      ordinal_number.setDisplayedValues(ORDINAL_NUMBER_LIST_JA);
    }
    else {
      ordinal_number.setDisplayedValues(ORDINAL_NUMBER_LIST_EN);
    }
    ordinal_number.setOnScrollListener(new NumberPicker.OnScrollListener() {
      @Override
      public void onScrollStateChange(NumberPicker view, int scrollState) {
        switch(scrollState) {
          case SCROLL_STATE_IDLE: {
            MainEditFragment.dayRepeat.setOrdinal_number(ordinal_number.getValue());
            break;
          }
          case SCROLL_STATE_FLING:
          case SCROLL_STATE_TOUCH_SCROLL: {
            break;
          }
        }
      }
    });

    //day_on_weekの実装
    day_of_week = view.findViewById(R.id.day_on_week);
    day_of_week.setDisplayedValues(null);
    day_of_week.setMaxValue(DAY_OF_WEEK_LIST_JA.length - 1);
    day_of_week.setMinValue(0);
    if(MainEditFragment.dayRepeat.getOn_the_month() == null) {
      MainEditFragment.dayRepeat.setOn_the_month(Week.MON);
    }
    day_of_week.setValue(MainEditFragment.dayRepeat.getOn_the_month().ordinal());
    if(locale.equals(Locale.JAPAN)) {
      day_of_week.setDisplayedValues(DAY_OF_WEEK_LIST_JA);
    }
    else {
      day_of_week.setDisplayedValues(DAY_OF_WEEK_LIST_EN);
    }
    day_of_week.setOnScrollListener(new NumberPicker.OnScrollListener() {
      @Override
      public void onScrollStateChange(NumberPicker view, int scrollState) {
        switch(scrollState) {
          case SCROLL_STATE_IDLE: {
            MainEditFragment.dayRepeat.setOn_the_month(Week.values()[day_of_week.getValue()]);
            break;
          }
          case SCROLL_STATE_FLING:
          case SCROLL_STATE_TOUCH_SCROLL: {
            break;
          }
        }
      }
    });
  }
}
