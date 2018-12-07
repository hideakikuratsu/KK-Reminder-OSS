package com.hideaki.kk_reminder;

import android.content.Context;
import android.os.Handler;
import android.preference.Preference;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.hideaki.kk_reminder.UtilClass.DEFAULT_QUICK_PICKER1;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_QUICK_PICKER2;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_QUICK_PICKER3;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_QUICK_PICKER4;

public class DefaultQuickPickerPreference extends Preference implements View.OnClickListener {

  private MainActivity activity;
  private static List<String> hour_list = new ArrayList<>();
  private static List<String> minute_list = new ArrayList<>();
  private NumberPicker hour_picker;
  private NumberPicker minute_picker;
  private int which_picker_selected;
  private TextView abovePicker1;
  private TextView abovePicker2;
  private TextView abovePicker3;
  private TextView abovePicker4;
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

  public DefaultQuickPickerPreference(Context context, AttributeSet attrs) {
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
    return View.inflate(getContext(), R.layout.default_quick_picker_layout, null);
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    //quick_time_pickerの初期化

    TableLayout above_quick_time_picker = view.findViewById(R.id.above_quick_time_picker);
    int above_quick_time_picker_size = above_quick_time_picker.getChildCount();
    for(int i = 0; i < above_quick_time_picker_size; i++) {
      TableRow tableRow = (TableRow)above_quick_time_picker.getChildAt(i);
      tableRow.setBackgroundColor(activity.accent_color);
    }

    abovePicker1 = view.findViewById(R.id.above_picker1);
    abovePicker2 = view.findViewById(R.id.above_picker2);
    abovePicker3 = view.findViewById(R.id.above_picker3);
    abovePicker4 = view.findViewById(R.id.above_picker4);

    abovePicker1.setOnClickListener(this);
    abovePicker2.setOnClickListener(this);
    abovePicker3.setOnClickListener(this);
    abovePicker4.setOnClickListener(this);

    abovePicker1.setText(activity.defaultQuickPicker1);
    abovePicker2.setText(activity.defaultQuickPicker2);
    abovePicker3.setText(activity.defaultQuickPicker3);
    abovePicker4.setText(activity.defaultQuickPicker4);

    abovePicker1.setBackgroundColor(activity.accent_color);
    which_picker_selected = 0;

    Calendar now = Calendar.getInstance();

    //hour_pickerの実装
    hour_picker = view.findViewById(R.id.hour);
    hour_picker.setDisplayedValues(null);
    hour_picker.setMaxValue(23);
    hour_picker.setMinValue(0);
    hour_picker.setValue(now.get(Calendar.HOUR_OF_DAY));
    hour_picker.setDisplayedValues(hour_list.toArray(new String[0]));
    hour_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
      @Override
      public void onValueChange(NumberPicker picker, int oldVal, final int newVal) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {

            if(newVal == hour_picker.getValue()) {

              String time = String.format(Locale.US, "%d:%02d", hour_picker.getValue(), minute_picker.getValue());
              switch(which_picker_selected) {

                case 0: {

                  activity.setStringGeneralInSharedPreferences(DEFAULT_QUICK_PICKER1, time);
                  abovePicker1.setText(time);
                  break;
                }
                case 1: {

                  activity.setStringGeneralInSharedPreferences(DEFAULT_QUICK_PICKER2, time);
                  abovePicker2.setText(time);
                  break;
                }
                case 2: {

                  activity.setStringGeneralInSharedPreferences(DEFAULT_QUICK_PICKER3, time);
                  abovePicker3.setText(time);
                  break;
                }
                case 3: {

                  activity.setStringGeneralInSharedPreferences(DEFAULT_QUICK_PICKER4, time);
                  abovePicker4.setText(time);
                  break;
                }
              }
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
    minute_picker.setValue(now.get(Calendar.MINUTE));
    minute_picker.setDisplayedValues(minute_list.toArray(new String[0]));
    minute_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
      @Override
      public void onValueChange(NumberPicker picker, int oldVal, final int newVal) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {

            if(newVal == minute_picker.getValue()) {

              String time = String.format(Locale.US, "%d:%02d", hour_picker.getValue(), minute_picker.getValue());
              switch(which_picker_selected) {

                case 0: {

                  activity.setStringGeneralInSharedPreferences(DEFAULT_QUICK_PICKER1, time);
                  abovePicker1.setText(time);
                  break;
                }
                case 1: {

                  activity.setStringGeneralInSharedPreferences(DEFAULT_QUICK_PICKER2, time);
                  abovePicker2.setText(time);
                  break;
                }
                case 2: {

                  activity.setStringGeneralInSharedPreferences(DEFAULT_QUICK_PICKER3, time);
                  abovePicker3.setText(time);
                  break;
                }
                case 3: {

                  activity.setStringGeneralInSharedPreferences(DEFAULT_QUICK_PICKER4, time);
                  abovePicker4.setText(time);
                  break;
                }
              }
              activity.updateSettingsDB();
            }
          }
        }, 100);
      }
    });
  }

  @Override
  public void onClick(View v) {

    abovePicker1.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));
    abovePicker2.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));
    abovePicker3.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));
    abovePicker4.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));

    switch(v.getId()) {

      case R.id.above_picker1: {

        which_picker_selected = 0;
        break;
      }
      case R.id.above_picker2: {

        which_picker_selected = 1;
        break;
      }
      case R.id.above_picker3: {

        which_picker_selected = 2;
        break;
      }
      case R.id.above_picker4: {

        which_picker_selected = 3;
        break;
      }
    }

    TextView quick_time = (TextView)v;
    quick_time.setBackgroundColor(activity.accent_color);
    String quick_time_str = quick_time.getText().toString();
    hour_picker.setValue(Integer.parseInt(quick_time_str.substring(0, quick_time_str.indexOf(':'))));
    minute_picker.setValue(Integer.parseInt(quick_time_str.substring(quick_time_str.indexOf(':') + 1)));
  }
}