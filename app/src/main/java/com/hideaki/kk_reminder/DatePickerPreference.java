package com.hideaki.kk_reminder;

import android.content.Context;
import android.os.Handler;
import android.preference.Preference;
import android.support.v4.app.FragmentManager;
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

import static com.hideaki.kk_reminder.UtilClass.HOUR;

public class DatePickerPreference extends Preference implements View.OnClickListener {

  private final String[] DAY_OF_WEEK_LIST_JA = {"日", "月", "火", "水", "木", "金", "土"};
  private final String[] DAY_OF_WEEK_LIST_EN = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
  private MainActivity activity;
  private List<String> day_list = new ArrayList<>();
  private static List<String> hour_list = new ArrayList<>();
  private static List<String> minute_list = new ArrayList<>();
  private static Calendar cal;
  private Calendar norm = Calendar.getInstance();
  private int now_year;
  private int offset;
  private int whole_list_size;
  private int first_list_size;
  private boolean change_to_top = false;
  private boolean change_to_bottom = false;
  private NumberPicker day_picker;
  private NumberPicker hour_picker;
  private NumberPicker minute_picker;
  private View saved_view;
  private FragmentManager manager;
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

  public DatePickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
    manager = ((MainActivity)context).getSupportFragmentManager();
  }

  @Override
  protected void onAttachedToActivity() {

    super.onAttachedToActivity();
    activity = (MainActivity)getContext();
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    return View.inflate(getContext(), R.layout.date_picker, null);
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);
    saved_view = view;

    //quick_time_pickerの初期化
    TableLayout above_quick_time_picker = view.findViewById(R.id.above_quick_time_picker);
    int above_quick_time_picker_size = above_quick_time_picker.getChildCount();
    for(int i = 0; i < above_quick_time_picker_size; i++) {
      TableRow tableRow = (TableRow)above_quick_time_picker.getChildAt(i);
      tableRow.setBackgroundColor(activity.accent_color);
    }

    TextView abovePicker1 = view.findViewById(R.id.above_picker1);
    TextView abovePicker2 = view.findViewById(R.id.above_picker2);
    TextView abovePicker3 = view.findViewById(R.id.above_picker3);
    TextView abovePicker4 = view.findViewById(R.id.above_picker4);

    abovePicker1.setOnClickListener(this);
    abovePicker2.setOnClickListener(this);
    abovePicker3.setOnClickListener(this);
    abovePicker4.setOnClickListener(this);

    abovePicker1.setText(activity.defaultQuickPicker1);
    abovePicker2.setText(activity.defaultQuickPicker2);
    abovePicker3.setText(activity.defaultQuickPicker3);
    abovePicker4.setText(activity.defaultQuickPicker4);

    TableLayout below_quick_time_picker = view.findViewById(R.id.below_quick_time_picker);
    int below_quick_time_picker_size = below_quick_time_picker.getChildCount();
    for(int i = 0; i < below_quick_time_picker_size; i++) {
      TableRow tableRow = (TableRow)below_quick_time_picker.getChildAt(i);
      tableRow.setBackgroundColor(activity.accent_color);
      int table_row_size = tableRow.getChildCount();
      for(int j = 0; j < table_row_size; j++) {
        TextView quick_time = (TextView)tableRow.getChildAt(j);
        quick_time.setOnClickListener(this);
      }
    }

    //day_pickerの実装
    cal = (Calendar)MainEditFragment.final_cal.clone();
    Calendar now = (Calendar)MainEditFragment.final_cal.clone();
    MainEditFragment.final_cal.set(Calendar.SECOND, 0);
    MainEditFragment.final_cal.set(Calendar.MILLISECOND, 0);
    day_list.clear();
    now_year = cal.get(Calendar.YEAR);
    norm.set(now_year, 0, 1, 0, 0, 0);
    long day = (cal.getTimeInMillis() - norm.getTimeInMillis()) / (24 * HOUR);
    setDayList(0);

    day_picker = view.findViewById(R.id.day);
    day_picker.setDisplayedValues(null);
    day_picker.setMaxValue(day_list.size() - 1);
    day_picker.setMinValue(0);
    day_picker.setValue((int)day);
    day_picker.setDisplayedValues(day_list.toArray(new String[0]));
    day_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
      @Override
      public void onValueChange(NumberPicker picker, int oldVal, final int newVal) {

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {

            if((offset = day_picker.getValue()) < 20) {
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
              day_picker.setMaxValue(day_list.size() - 1);
              day_picker.setDisplayedValues(day_list.toArray(new String[0]));
              day_picker.setValue(first_list_size + offset);
            }
            else if((offset = day_picker.getValue()) > day_list.size() - 20) {
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
              day_picker.setDisplayedValues(day_list.toArray(new String[0]));
              day_picker.setValue(offset - (whole_list_size - first_list_size));
            }

            if(newVal == day_picker.getValue()) {

              Calendar tmp = (Calendar)norm.clone();
              tmp.add(Calendar.DAY_OF_MONTH, day_picker.getValue());
              MainEditFragment.final_cal.set(tmp.get(Calendar.YEAR), tmp.get(Calendar.MONTH), tmp.get(Calendar.DAY_OF_MONTH));
            }
          }
        }, 100);
      }
    });

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
              MainEditFragment.final_cal.set(Calendar.HOUR_OF_DAY, hour_picker.getValue());
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
              MainEditFragment.final_cal.set(Calendar.MINUTE, minute_picker.getValue());
            }
          }
        }, 100);
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
        if(locale.equals(Locale.JAPAN)) {
          day_list.add(cal.get(Calendar.YEAR) + "年" + (i + 1) + "月" + j + "日 " +
              DAY_OF_WEEK_LIST_JA[cal.get(Calendar.DAY_OF_WEEK) - 1]);
        }
        else {
          day_list.add(cal.get(Calendar.YEAR) + "/" + (i + 1) + "/" + j + " " +
              DAY_OF_WEEK_LIST_EN[cal.get(Calendar.DAY_OF_WEEK) - 1]);
        }
      }
    }

    now_year = cal.get(Calendar.YEAR);
  }

  @Override
  public void onClick(View v) {

    switch(v.getId()) {
      case R.id.above_picker1:
      case R.id.above_picker2:
      case R.id.above_picker3:
      case R.id.above_picker4: {
        TextView quick_time = (TextView)v;
        String quick_time_str = quick_time.getText().toString();
        MainEditFragment.final_cal.set(Calendar.HOUR_OF_DAY,
            Integer.parseInt(quick_time_str.substring(0, quick_time_str.indexOf(':'))));
        MainEditFragment.final_cal.set(Calendar.MINUTE,
            Integer.parseInt(quick_time_str.substring(quick_time_str.indexOf(':') + 1)));
        break;
      }
      case R.id.below_picker1: {
        MainEditFragment.final_cal = (Calendar)Calendar.getInstance().clone();
        break;
      }
      case R.id.below_picker2: {
        MainEditFragment.final_cal.set(Calendar.HOUR_OF_DAY, MainEditFragment.final_cal.get(Calendar.HOUR_OF_DAY) + 3);
        break;
      }
      case R.id.below_picker3: {
        MainEditFragment.final_cal.add(Calendar.DAY_OF_MONTH, 7);
        break;
      }
      case R.id.below_picker4: {
        DatePickerDialogFragment dialog = new DatePickerDialogFragment();
        dialog.setInstance(this, saved_view);
        dialog.show(manager, "date_picker_fragment");
        break;
      }
    }

    onBindView(saved_view);
  }
}