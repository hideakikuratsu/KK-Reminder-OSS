package com.hideaki.kk_reminder;

import android.content.Context;
import android.os.Handler;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MinuteRepeatDurationPickerPreference extends Preference {

  private MainActivity activity;
  private static List<String> hour_list = new ArrayList<>();
  private static List<String> minute_list = new ArrayList<>();
  private NumberPicker hour_picker;
  private NumberPicker minute_picker;
  private static Locale locale = Locale.getDefault();

  static {

    if(locale.equals(Locale.JAPAN)) {
      for(int i = 0; i < 24; i++) {
        hour_list.add(i + "時間");
      }

      for(int i = 0; i < 60; i++) {
        minute_list.add(i + "分");
      }
    }
    else {
      for(int i = 0; i < 24; i++) {
        if(i == 0 || i == 1) {
          hour_list.add(i + " hour");
        }
        else hour_list.add(i + " hours");
      }

      for(int i = 0; i < 60; i++) {
        if(i == 0 || i == 1) {
          minute_list.add(i + " minute");
        }
        else minute_list.add(i + " minutes");
      }
    }
  }

  public MinuteRepeatDurationPickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
    activity = (MainActivity)context;
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    return View.inflate(getContext(), R.layout.minute_repeat_duration_picker, null);
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    if(MainEditFragment.minuteRepeat.getWhich_setted() == (1 << 1)) {
      //hour_pickerの実装
      hour_picker = view.findViewById(R.id.hour);
      hour_picker.setDisplayedValues(null);
      hour_picker.setMaxValue(23);
      hour_picker.setMinValue(0);
      hour_picker.setValue(MainEditFragment.minuteRepeat.getOrg_duration_hour());
      hour_picker.setDisplayedValues(hour_list.toArray(new String[0]));
      hour_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, final int newVal) {

          final Handler handler = new Handler();
          handler.postDelayed(new Runnable() {
            @Override
            public void run() {

              if(newVal == hour_picker.getValue()) {

                if(hour_picker.getValue() == 0 && minute_picker.getValue() == 0) {
                  hour_picker.setValue(1);
                }
                MainEditFragment.minuteRepeat.setOrg_duration_hour(hour_picker.getValue());

                String interval = "";
                int hour = MainEditFragment.minuteRepeat.getHour();
                if(hour != 0) {
                  interval += activity.getResources().getQuantityString(R.plurals.hour, hour, hour);
                  if(!locale.equals(Locale.JAPAN)) interval += " ";
                }
                int minute = MainEditFragment.minuteRepeat.getMinute();
                if(minute != 0) {
                  interval += activity.getResources().getQuantityString(R.plurals.minute, minute, minute);
                  if(!locale.equals(Locale.JAPAN)) interval += " ";
                }
                String duration = "";
                int duration_hour = MainEditFragment.minuteRepeat.getOrg_duration_hour();
                if(duration_hour != 0) {
                  duration += activity.getResources().getQuantityString(R.plurals.hour, duration_hour, duration_hour);
                  if(!locale.equals(Locale.JAPAN)) duration += " ";
                }
                int duration_minute = MainEditFragment.minuteRepeat.getOrg_duration_minute();
                if(duration_minute != 0) {
                  duration += activity.getResources().getQuantityString(R.plurals.minute, duration_minute, duration_minute);
                  if(!locale.equals(Locale.JAPAN)) duration += " ";
                }
                String label = activity.getString(R.string.repeat_minute_duration_format, interval, duration);

                MinuteRepeatEditFragment.label_str = label;
                MinuteRepeatEditFragment.label.setSummary(label);

                MainEditFragment.minuteRepeat.setLabel(label);
              }
            }
          }, 100);
        }
      });

      //minute_pickerの実装
      minute_picker = view.findViewById(R.id.minute);
      minute_picker.setDisplayedValues(null);
      minute_picker.setMaxValue(59);
      minute_picker.setMinValue(0);
      minute_picker.setValue(MainEditFragment.minuteRepeat.getOrg_duration_minute());
      minute_picker.setDisplayedValues(minute_list.toArray(new String[0]));
      minute_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, final int newVal) {

          final Handler handler = new Handler();
          handler.postDelayed(new Runnable() {
            @Override
            public void run() {

              if(newVal == minute_picker.getValue()) {

                if(hour_picker.getValue() == 0 && minute_picker.getValue() == 0) {
                  hour_picker.setValue(1);
                }
                MainEditFragment.minuteRepeat.setOrg_duration_minute(minute_picker.getValue());

                String interval = "";
                int hour = MainEditFragment.minuteRepeat.getHour();
                if(hour != 0) {
                  interval += activity.getResources().getQuantityString(R.plurals.hour, hour, hour);
                  if(!locale.equals(Locale.JAPAN)) interval += " ";
                }
                int minute = MainEditFragment.minuteRepeat.getMinute();
                if(minute != 0) {
                  interval += activity.getResources().getQuantityString(R.plurals.minute, minute, minute);
                  if(!locale.equals(Locale.JAPAN)) interval += " ";
                }
                String duration = "";
                int duration_hour = MainEditFragment.minuteRepeat.getOrg_duration_hour();
                if(duration_hour != 0) {
                  duration += activity.getResources().getQuantityString(R.plurals.hour, duration_hour, duration_hour);
                  if(!locale.equals(Locale.JAPAN)) duration += " ";
                }
                int duration_minute = MainEditFragment.minuteRepeat.getOrg_duration_minute();
                if(duration_minute != 0) {
                  duration += activity.getResources().getQuantityString(R.plurals.minute, duration_minute, duration_minute);
                  if(!locale.equals(Locale.JAPAN)) duration += " ";
                }
                String label = activity.getString(R.string.repeat_minute_duration_format, interval, duration);

                MinuteRepeatEditFragment.label_str = label;
                MinuteRepeatEditFragment.label.setSummary(label);

                MainEditFragment.minuteRepeat.setLabel(label);
              }
            }
          }, 100);
        }
      });
    }
  }
}
