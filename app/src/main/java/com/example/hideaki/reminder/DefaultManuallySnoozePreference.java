package com.example.hideaki.reminder;

import android.content.Context;
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DefaultManuallySnoozePreference extends Preference {

  private MainActivity activity;
  private static final List<String> hour_list = new ArrayList<>();
  private static final List<String> minute_list = new ArrayList<>();
  private NumberPicker hour_picker;
  private NumberPicker minute_picker;
  private int hour;
  private int minute;
  private String summary;

  static {

    for(int i = 0; i < 24; i++) hour_list.add(i + "時間");
    for(int i = 0; i < 60; i++) minute_list.add(i + "分");
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
    hour_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
      @Override
      public void onScrollStateChange(NumberPicker view, int scrollState) {
        switch(scrollState) {
          case SCROLL_STATE_IDLE: {
            if(hour_picker.getValue() == 0 && minute_picker.getValue() == 0) {
              hour = 24;
            }
            else hour = hour_picker.getValue();

            summary = "";
            if(hour != 0) {
              summary += hour + activity.getString(R.string.hour);
            }
            if(minute != 0) {
              summary += minute + activity.getString(R.string.minute);
            }
            summary += activity.getString(R.string.snooze);
            DefaultManuallySnoozeFragment.label.setSummary(summary);

            activity.generalSettings.setSnooze_default_hour(hour);
            activity.updateSettingsDB();

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
    minute_picker.setValue(minute);
    minute_picker.setDisplayedValues(minute_list.toArray(new String[0]));
    minute_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
      @Override
      public void onScrollStateChange(NumberPicker view, int scrollState) {
        switch(scrollState) {
          case SCROLL_STATE_IDLE: {
            if(hour_picker.getValue() == 0 && minute_picker.getValue() == 0) {
              hour = 24;
            }
            else hour = hour_picker.getValue();
            minute = minute_picker.getValue();

            summary = "";
            if(hour != 0) {
              summary += hour + activity.getString(R.string.hour);
            }
            if(minute != 0) {
              summary += minute + activity.getString(R.string.minute);
            }
            summary += activity.getString(R.string.snooze);
            DefaultManuallySnoozeFragment.label.setSummary(summary);

            activity.generalSettings.setSnooze_default_minute(minute);
            activity.updateSettingsDB();

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