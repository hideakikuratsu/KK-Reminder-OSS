package com.hideaki.kk_reminder;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DefaultManuallySnoozePreference extends Preference {

  private MainActivity activity;
  private static final List<String> hour_list = new ArrayList<>();
  private static final List<String> minute_list = new ArrayList<>();
  private NumberPicker hour_picker;
  private NumberPicker minute_picker;
  private int hour;
  private int minute;
  private String summary;
  private static Locale locale = Locale.getDefault();

  static {

    if(locale.equals(Locale.JAPAN)) {
      for(int i = 0; i < 24; i++) {
        hour_list.add(i + "時");
      }

      for(int i = 0; i < 60; i++) {
        minute_list.add(i + "分");
      }
    }
    else {
      for(int i = 0; i < 24; i++) {
        hour_list.add(i + "");
      }

      for(int i = 0; i < 60; i++) {
        minute_list.add(i + "");
      }
    }
  }

  public DefaultManuallySnoozePreference(Context context, AttributeSet attrs) {

    super(context, attrs);
    activity = (MainActivity)context;
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    return View.inflate(getContext(), R.layout.manually_snooze_custom_layout, null);
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    TextView description = view.findViewById(R.id.description);
    description.setTextColor(Color.BLACK);
    hour = activity.generalSettings.getSnooze_default_hour();
    minute = activity.generalSettings.getSnooze_default_minute();

    //hour_pickerの実装
    hour_picker = view.findViewById(R.id.hour);
    hour_picker.setDisplayedValues(null);
    hour_picker.setMaxValue(23);
    hour_picker.setMinValue(0);
    hour_picker.setValue(hour);
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
                hour = 24;
              }
              else hour = hour_picker.getValue();

              summary = "";
              if(hour != 0) {
                summary += activity.getResources().getQuantityString(R.plurals.hour, hour, hour);
                if(!locale.equals(Locale.JAPAN)) summary += " ";
              }
              if(minute != 0) {
                summary += activity.getResources().getQuantityString(R.plurals.minute, minute, minute);
                if(!locale.equals(Locale.JAPAN)) summary += " ";
              }
              summary += activity.getString(R.string.snooze);
              DefaultManuallySnoozeFragment.label.setSummary(summary);

              activity.generalSettings.setSnooze_default_hour(hour);
              activity.updateSettingsDB();
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
    minute_picker.setValue(minute);
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
                hour = 24;
              }
              else hour = hour_picker.getValue();
              minute = minute_picker.getValue();

              summary = "";
              if(hour != 0) {
                summary += activity.getResources().getQuantityString(R.plurals.hour, hour, hour);
                if(!locale.equals(Locale.JAPAN)) summary += " ";
              }
              if(minute != 0) {
                summary += activity.getResources().getQuantityString(R.plurals.minute, minute, minute);
                if(!locale.equals(Locale.JAPAN)) summary += " ";
              }
              summary += activity.getString(R.string.snooze);
              DefaultManuallySnoozeFragment.label.setSummary(summary);

              activity.generalSettings.setSnooze_default_minute(minute);
              activity.updateSettingsDB();
            }
          }
        }, 100);
      }
    });
  }
}