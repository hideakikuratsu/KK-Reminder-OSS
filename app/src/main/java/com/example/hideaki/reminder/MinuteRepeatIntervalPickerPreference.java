package com.example.hideaki.reminder;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.List;

public class MinuteRepeatIntervalPickerPreference extends Preference {

  private static List<String> hour_list = new ArrayList<>();
  private static List<String> minute_list = new ArrayList<>();
  private NumberPicker hour_picker;
  private NumberPicker minute_picker;

  static {

    for(int i = 0; i < 24; i++) {
      hour_list.add(i + "時間");
    }

    for(int i = 0; i < 60; i++) {
      minute_list.add(i + "分");
    }
  }

  public MinuteRepeatIntervalPickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    return View.inflate(getContext(), R.layout.minute_repeat_interval_picker, null);
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    //hour_pickerの実装
    hour_picker = view.findViewById(R.id.hour);
    hour_picker.setDisplayedValues(null);
    hour_picker.setMaxValue(23);
    hour_picker.setMinValue(0);
    hour_picker.setValue(MainEditFragment.minuteRepeat.getHour());
    hour_picker.setDisplayedValues(hour_list.toArray(new String[hour_list.size()]));
    hour_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
      @Override
      public void onScrollStateChange(NumberPicker view, int scrollState) {
        switch(scrollState) {
          case SCROLL_STATE_IDLE: {
            if(hour_picker.getValue() == 0 && minute_picker.getValue() == 0) {
              hour_picker.setValue(1);
            }
            MainEditFragment.minuteRepeat.setHour(hour_picker.getValue());

            if(MinuteRepeatEditFragment.count.isChecked()) {

              MinuteRepeatEditFragment.label_str = "タスク完了から";
              if(MainEditFragment.minuteRepeat.getHour() != 0) {
                MinuteRepeatEditFragment.label_str += MainEditFragment.minuteRepeat.getHour() + "時間";
              }
              if(MainEditFragment.minuteRepeat.getMinute() != 0) {
                MinuteRepeatEditFragment.label_str += MainEditFragment.minuteRepeat.getMinute() + "分";
              }
              MinuteRepeatEditFragment.label_str += "間隔で" + MainEditFragment.minuteRepeat.getOrg_count() + "回繰り返す";
              MinuteRepeatEditFragment.label.setSummary(MinuteRepeatEditFragment.label_str);

              MainEditFragment.minuteRepeat.setLabel(MinuteRepeatEditFragment.label_str);
            }
            else if(MinuteRepeatEditFragment.duration.isChecked()) {

              MinuteRepeatEditFragment.label_str = "タスク完了から";
              if(MainEditFragment.minuteRepeat.getHour() != 0) {
                MinuteRepeatEditFragment.label_str += MainEditFragment.minuteRepeat.getHour() + "時間";
              }
              if(MainEditFragment.minuteRepeat.getMinute() != 0) {
                MinuteRepeatEditFragment.label_str += MainEditFragment.minuteRepeat.getMinute() + "分";
              }
              MinuteRepeatEditFragment.label_str += "間隔で";
              if(MainEditFragment.minuteRepeat.getOrg_duration_hour() != 0) {
                MinuteRepeatEditFragment.label_str += MainEditFragment.minuteRepeat.getOrg_duration_hour() + "時間";
              }
              if(MainEditFragment.minuteRepeat.getOrg_duration_minute() != 0) {
                MinuteRepeatEditFragment.label_str += MainEditFragment.minuteRepeat.getOrg_duration_minute() + "分";
              }
              MinuteRepeatEditFragment.label_str += "経過するまで繰り返す";
              MinuteRepeatEditFragment.label.setSummary(MinuteRepeatEditFragment.label_str);

              MainEditFragment.minuteRepeat.setLabel(MinuteRepeatEditFragment.label_str);
            }
            break;
          }
          case SCROLL_STATE_FLING:
          case SCROLL_STATE_TOUCH_SCROLL: {
            break;
          }
        }
      }
    });

    //minute_pickerの実装
    minute_picker = view.findViewById(R.id.minute);
    minute_picker.setDisplayedValues(null);
    minute_picker.setMaxValue(59);
    minute_picker.setMinValue(0);
    minute_picker.setValue(MainEditFragment.minuteRepeat.getMinute());
    minute_picker.setDisplayedValues(minute_list.toArray(new String[minute_list.size()]));
    minute_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
      @Override
      public void onScrollStateChange(NumberPicker view, int scrollState) {
        switch(scrollState) {
          case SCROLL_STATE_IDLE: {
            if(hour_picker.getValue() == 0 && minute_picker.getValue() == 0) {
              hour_picker.setValue(1);
            }
            MainEditFragment.minuteRepeat.setMinute(minute_picker.getValue());

            if(MinuteRepeatEditFragment.count.isChecked()) {

              MinuteRepeatEditFragment.label_str = "タスク完了から";
              if(MainEditFragment.minuteRepeat.getHour() != 0) {
                MinuteRepeatEditFragment.label_str += MainEditFragment.minuteRepeat.getHour() + "時間";
              }
              if(MainEditFragment.minuteRepeat.getMinute() != 0) {
                MinuteRepeatEditFragment.label_str += MainEditFragment.minuteRepeat.getMinute() + "分";
              }
              MinuteRepeatEditFragment.label_str += "間隔で" + MainEditFragment.minuteRepeat.getOrg_count() + "回繰り返す";
              MinuteRepeatEditFragment.label.setSummary(MinuteRepeatEditFragment.label_str);

              MainEditFragment.minuteRepeat.setLabel(MinuteRepeatEditFragment.label_str);
            }
            else if(MinuteRepeatEditFragment.duration.isChecked()) {

              MinuteRepeatEditFragment.label_str = "タスク完了から";
              if(MainEditFragment.minuteRepeat.getHour() != 0) {
                MinuteRepeatEditFragment.label_str += MainEditFragment.minuteRepeat.getHour() + "時間";
              }
              if(MainEditFragment.minuteRepeat.getMinute() != 0) {
                MinuteRepeatEditFragment.label_str += MainEditFragment.minuteRepeat.getMinute() + "分";
              }
              MinuteRepeatEditFragment.label_str += "間隔で";
              if(MainEditFragment.minuteRepeat.getOrg_duration_hour() != 0) {
                MinuteRepeatEditFragment.label_str += MainEditFragment.minuteRepeat.getOrg_duration_hour() + "時間";
              }
              if(MainEditFragment.minuteRepeat.getOrg_duration_minute() != 0) {
                MinuteRepeatEditFragment.label_str += MainEditFragment.minuteRepeat.getOrg_duration_minute() + "分";
              }
              MinuteRepeatEditFragment.label_str += "経過するまで繰り返す";
              MinuteRepeatEditFragment.label.setSummary(MinuteRepeatEditFragment.label_str);

              MainEditFragment.minuteRepeat.setLabel(MinuteRepeatEditFragment.label_str);
            }
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
