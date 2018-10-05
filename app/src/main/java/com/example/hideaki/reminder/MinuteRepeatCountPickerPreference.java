package com.example.hideaki.reminder;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.preference.Preference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Locale;

public class MinuteRepeatCountPickerPreference extends Preference implements TextWatcher {

  private MainActivity activity;
  private EditText count;
  private String oldString = "";
  private static Locale locale = Locale.getDefault();

  public MinuteRepeatCountPickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
    activity = (MainActivity)context;
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    return View.inflate(getContext(), R.layout.minute_repeat_count_picker, null);
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    if(MainEditFragment.minuteRepeat.getWhich_setted() == 1) {
      count = view.findViewById(R.id.count);
      count.requestFocus();
      count.setText(String.valueOf(MainEditFragment.minuteRepeat.getOrg_count()));
      count.setSelection(count.getText().length());
      count.addTextChangedListener(this);

      ImageView plus = view.findViewById(R.id.plus);
      plus.setColorFilter(activity.accent_color);
      GradientDrawable drawable = (GradientDrawable)plus.getBackground();
      drawable = (GradientDrawable)drawable.mutate();
      drawable.setStroke(3, activity.accent_color);
      drawable.setCornerRadius(8.0f);

      plus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          MainEditFragment.minuteRepeat.addOrg_count(1);
          count.setText(String.valueOf(MainEditFragment.minuteRepeat.getOrg_count()));

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
          int count = MainEditFragment.minuteRepeat.getOrg_count();
          String label = activity.getResources().getQuantityString(R.plurals.repeat_minute_count_format,
              count, interval, count);
          MinuteRepeatEditFragment.label_str = label;
          MinuteRepeatEditFragment.label.setSummary(label);

          MainEditFragment.minuteRepeat.setLabel(label);
        }
      });

      ImageView minus = view.findViewById(R.id.minus);
      minus.setColorFilter(activity.accent_color);
      drawable = (GradientDrawable)minus.getBackground();
      drawable = (GradientDrawable)drawable.mutate();
      drawable.setStroke(3, activity.accent_color);
      drawable.setCornerRadius(8.0f);

      minus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if(MainEditFragment.minuteRepeat.getOrg_count() > 1) {
            MainEditFragment.minuteRepeat.addOrg_count(-1);
            count.setText(String.valueOf(MainEditFragment.minuteRepeat.getOrg_count()));

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
            int count = MainEditFragment.minuteRepeat.getOrg_count();
            String label = activity.getResources().getQuantityString(R.plurals.repeat_minute_count_format,
                count, interval, count);
            MinuteRepeatEditFragment.label_str = label;
            MinuteRepeatEditFragment.label.setSummary(label);

            MainEditFragment.minuteRepeat.setLabel(label);
          }
        }
      });
    }
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {}

  @Override
  public void afterTextChanged(Editable s) {

    if(!s.toString().equals("") && !oldString.equals(s.toString()) && MinuteRepeatEditFragment.count.isChecked()) {
      oldString = s.toString();
      if(Integer.parseInt(s.toString()) != 0) {
        MainEditFragment.minuteRepeat.setOrg_count(Integer.parseInt(s.toString()));

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
        int count = MainEditFragment.minuteRepeat.getOrg_count();
        String label = activity.getResources().getQuantityString(R.plurals.repeat_minute_count_format,
            count, interval, count);
        MinuteRepeatEditFragment.label_str = label;
        MinuteRepeatEditFragment.label.setSummary(label);

        MainEditFragment.minuteRepeat.setLabel(label);
      }
    }
  }
}