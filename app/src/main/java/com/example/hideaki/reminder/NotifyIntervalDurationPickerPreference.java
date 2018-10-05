package com.example.hideaki.reminder;

import android.content.Context;
import android.content.res.Resources;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotifyIntervalDurationPickerPreference extends Preference {

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

  public NotifyIntervalDurationPickerPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onAttachedToActivity() {

    super.onAttachedToActivity();
    activity = (MainActivity)getContext();
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    return View.inflate(getContext(), R.layout.notify_interval_duration_picker, null);
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    if(MainEditFragment.notifyInterval.getWhich_setted() == (1 << 7)) {
      //hour_pickerの実装
      hour_picker = view.findViewById(R.id.hour);
      hour_picker.setDisplayedValues(null);
      hour_picker.setMaxValue(23);
      hour_picker.setMinValue(0);
      hour_picker.setValue(MainEditFragment.notifyInterval.getHour());
      hour_picker.setDisplayedValues(hour_list.toArray(new String[0]));
      hour_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
        @Override
        public void onScrollStateChange(NumberPicker view, int scrollState) {
          switch(scrollState) {
            case SCROLL_STATE_IDLE: {
              if(hour_picker.getValue() == 0 && minute_picker.getValue() == 0) {
                MainEditFragment.notifyInterval.setHour(24);
              }
              else MainEditFragment.notifyInterval.setHour(hour_picker.getValue());

              NotifyInterval interval = MainEditFragment.notifyInterval;
              if(interval.getOrg_time() != 0) {
                Resources res = activity.getResources();
                String summary;
                if(locale.equals(Locale.JAPAN)) {
                  summary = activity.getString(R.string.unless_complete_task);
                  if(interval.getHour() != 0) {
                    summary += res.getQuantityString(R.plurals.hour, interval.getHour(), interval.getHour());
                  }
                  if(interval.getMinute() != 0) {
                    summary += res.getQuantityString(R.plurals.minute, interval.getMinute(), interval.getMinute());
                  }
                  summary += activity.getString(R.string.per);
                  if(interval.getOrg_time() == -1) {
                    summary += activity.getString(R.string.infinite_times_notify);
                  }
                  else {
                    summary += res.getQuantityString(R.plurals.times_notify, interval.getOrg_time(),
                        interval.getOrg_time());
                  }
                }
                else {
                  summary = "Notify every ";
                  if(interval.getHour() != 0) {
                    summary += res.getQuantityString(R.plurals.hour, interval.getHour(), interval.getHour());
                    if(!locale.equals(Locale.JAPAN)) summary += " ";
                  }
                  if(interval.getMinute() != 0) {
                    summary += res.getQuantityString(R.plurals.minute, interval.getMinute(), interval.getMinute());
                    if(!locale.equals(Locale.JAPAN)) summary += " ";
                  }
                  if(interval.getOrg_time() != -1) {
                    summary += res.getQuantityString(R.plurals.times_notify, interval.getOrg_time(),
                        interval.getOrg_time()) + " ";
                  }
                  summary += activity.getString(R.string.unless_complete_task);
                }
                
                NotifyIntervalEditFragment.label.setSummary(summary);

                MainEditFragment.notifyInterval.setLabel(summary);
              }
              else {
                NotifyIntervalEditFragment.label.setSummary(activity.getString(R.string.non_notify));

                MainEditFragment.notifyInterval.setLabel(activity.getString(R.string.none));
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
      minute_picker.setValue(MainEditFragment.notifyInterval.getMinute());
      minute_picker.setDisplayedValues(minute_list.toArray(new String[0]));
      minute_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
        @Override
        public void onScrollStateChange(NumberPicker view, int scrollState) {
          switch(scrollState) {
            case SCROLL_STATE_IDLE: {
              if(hour_picker.getValue() == 0 && minute_picker.getValue() == 0) {
                MainEditFragment.notifyInterval.setHour(24);
              }
              else MainEditFragment.notifyInterval.setHour(hour_picker.getValue());
              MainEditFragment.notifyInterval.setMinute(minute_picker.getValue());

              NotifyInterval interval = MainEditFragment.notifyInterval;
              if(interval.getOrg_time() != 0) {
                Resources res = activity.getResources();
                String summary;
                if(locale.equals(Locale.JAPAN)) {
                  summary = activity.getString(R.string.unless_complete_task);
                  if(interval.getHour() != 0) {
                    summary += res.getQuantityString(R.plurals.hour, interval.getHour(), interval.getHour());
                  }
                  if(interval.getMinute() != 0) {
                    summary += res.getQuantityString(R.plurals.minute, interval.getMinute(), interval.getMinute());
                  }
                  summary += activity.getString(R.string.per);
                  if(interval.getOrg_time() == -1) {
                    summary += activity.getString(R.string.infinite_times_notify);
                  }
                  else {
                    summary += res.getQuantityString(R.plurals.times_notify, interval.getOrg_time(),
                        interval.getOrg_time());
                  }
                }
                else {
                  summary = "Notify every ";
                  if(interval.getHour() != 0) {
                    summary += res.getQuantityString(R.plurals.hour, interval.getHour(), interval.getHour());
                    if(!locale.equals(Locale.JAPAN)) summary += " ";
                  }
                  if(interval.getMinute() != 0) {
                    summary += res.getQuantityString(R.plurals.minute, interval.getMinute(), interval.getMinute());
                    if(!locale.equals(Locale.JAPAN)) summary += " ";
                  }
                  if(interval.getOrg_time() != -1) {
                    summary += res.getQuantityString(R.plurals.times_notify, interval.getOrg_time(),
                        interval.getOrg_time()) + " ";
                  }
                  summary += activity.getString(R.string.unless_complete_task);
                }

                NotifyIntervalEditFragment.label.setSummary(summary);

                MainEditFragment.notifyInterval.setLabel(summary);
              }
              else {
                NotifyIntervalEditFragment.label.setSummary(activity.getString(R.string.non_notify));

                MainEditFragment.notifyInterval.setLabel(activity.getString(R.string.none));
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
}