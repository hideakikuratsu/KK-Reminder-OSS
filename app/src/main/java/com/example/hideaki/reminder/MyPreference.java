package com.example.hideaki.reminder;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyPreference extends Preference {

  final String[] day_of_week = {"日", "月", "火", "水", "木", "金", "土"};
  List<String> day_list = new ArrayList<>();
  List<String> hour_list = new ArrayList<>();
  List<String> minute_list = new ArrayList<>();
  static Calendar cal;
  Calendar norm = Calendar.getInstance();
  static Calendar final_cal = Calendar.getInstance();
  static Calendar now = (Calendar)final_cal.clone();
  int now_year;
  int offset;
  int whole_list_size;
  int first_list_size;
  boolean change_to_top = false;
  boolean change_to_bottom = false;
  NumberPicker day_picker;
  NumberPicker hour_picker;
  NumberPicker minute_picker;
  private PreferenceScreen alarm;

  public MyPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    setLayoutResource(R.layout.date_picker);
  }

  @Override
  protected void onBindView(View view) {
    super.onBindView(view);

    //初期化
    alarm = (PreferenceScreen)getPreferenceManager().findPreference("alarm");
    cal = (Calendar)final_cal.clone();
    now = (Calendar)final_cal.clone();
    final_cal.clear(Calendar.SECOND);
    day_list.clear();

    //day_pickerの実装
    now_year = cal.get(Calendar.YEAR);
    norm.set(now_year, 0, 1, 0, 0, 0);
    long day = (cal.getTimeInMillis() - norm.getTimeInMillis()) / (1000 * 60 * 60 * 24);
    setDayList(0);

    day_picker = view.findViewById(R.id.day);
    day_picker.setDisplayedValues(null);
    day_picker.setMaxValue(day_list.size()-1);
    day_picker.setMinValue(0);
    day_picker.setValue((int)day);
    day_picker.setDisplayedValues(day_list.toArray(new String[day_list.size()]));
    day_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
      @Override
      public void onScrollStateChange(NumberPicker view, int scrollState) {
        if((offset = day_picker.getValue()) < 10) {
          change_to_bottom = true;
          if(change_to_top) {
            now_year -= 1;
            change_to_top = false;
          }
          day_list.clear();
          setDayList(-1);
          first_list_size = day_list.size();
          setDayList(1);
          now_year -= 1;
          day_picker.setMaxValue(day_list.size()-1);
          day_picker.setDisplayedValues(day_list.toArray(new String[day_list.size()]));
          day_picker.setValue(first_list_size + offset);
        }
        else if((offset = day_picker.getValue()) > day_list.size() - 10) {
          change_to_top = true;
          if(change_to_bottom) {
            now_year += 1;
            change_to_bottom = false;
          }
          whole_list_size = day_list.size();
          day_list.clear();
          setDayList(0);
          first_list_size = day_list.size();
          setDayList(1);
          day_picker.setMaxValue(day_list.size()-1);
          day_picker.setDisplayedValues(day_list.toArray(new String[day_list.size()]));
          day_picker.setValue(offset - (whole_list_size - first_list_size));
        }
        switch(scrollState) {
          case SCROLL_STATE_IDLE:
            Calendar tmp = (Calendar)norm.clone();
            tmp.add(Calendar.DAY_OF_MONTH, day_picker.getValue());
            final_cal.set(tmp.get(Calendar.YEAR), tmp.get(Calendar.MONTH), tmp.get(Calendar.DAY_OF_MONTH));
            if(now.get(Calendar.YEAR) == final_cal.get(Calendar.YEAR)) {
              alarm.setTitle(new SimpleDateFormat("M月d日(E)H:mm").format(final_cal.getTime()));
            }
            else {
              alarm.setTitle(new SimpleDateFormat("yyyy年M月d日(E)H:mm").format(final_cal.getTime()));
            }
        }
      }
    });

    //hour_pickerの実装
    for(int i = 0; i < 24; i++) {
      hour_list.add(i + "時");
    }
    hour_picker = view.findViewById(R.id.hour);
    hour_picker.setDisplayedValues(null);
    hour_picker.setMaxValue(23);
    hour_picker.setMinValue(0);
    hour_picker.setValue(now.get(Calendar.HOUR_OF_DAY));
    hour_picker.setDisplayedValues(hour_list.toArray(new String[hour_list.size()]));
    hour_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
      @Override
      public void onScrollStateChange(NumberPicker view, int scrollState) {
        switch(scrollState) {
          case SCROLL_STATE_IDLE:
            final_cal.set(Calendar.HOUR_OF_DAY, hour_picker.getValue());
            if(now.get(Calendar.YEAR) == final_cal.get(Calendar.YEAR)) {
              alarm.setTitle(new SimpleDateFormat("M月d日(E)H:mm").format(final_cal.getTime()));
            }
            else {
              alarm.setTitle(new SimpleDateFormat("yyyy年M月d日(E)H:mm").format(final_cal.getTime()));
            }
        }
      }
    });

    //minute_pickerの実装
    for(int i = 0; i < 60; i++) {
      minute_list.add(i + "分");
    }
    minute_picker = view.findViewById(R.id.minute);
    minute_picker.setDisplayedValues(null);
    minute_picker.setMaxValue(59);
    minute_picker.setMinValue(0);
    minute_picker.setValue(now.get(Calendar.MINUTE));
    minute_picker.setDisplayedValues(minute_list.toArray(new String[minute_list.size()]));
    minute_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
      @Override
      public void onScrollStateChange(NumberPicker view, int scrollState) {
        switch(scrollState) {
          case SCROLL_STATE_IDLE:
            final_cal.set(Calendar.MINUTE, minute_picker.getValue());
            if(now.get(Calendar.YEAR) == final_cal.get(Calendar.YEAR)) {
              alarm.setTitle(new SimpleDateFormat("M月d日(E)H:mm").format(final_cal.getTime()));
            }
            else {
              alarm.setTitle(new SimpleDateFormat("yyyy年M月d日(E)H:mm").format(final_cal.getTime()));
            }
        }
      }
    });
  }

  private void setDayList(int shift) {

    cal.clear();
    cal.set(Calendar.YEAR, now_year + shift);
    for(int i = 0; i < 12; i++) {
      cal.set(Calendar.DAY_OF_MONTH, 1);
      cal.set(Calendar.MONTH, i);
      for(int j = 1; j <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); j++) {
        cal.set(Calendar.DAY_OF_MONTH, j);
        day_list.add(cal.get(Calendar.YEAR) + "年" + (i+1) + "月" + j + "日 " +
            day_of_week[cal.get(Calendar.DAY_OF_WEEK) - 1]);
      }
    }

    now_year = cal.get(Calendar.YEAR);
  }
}
