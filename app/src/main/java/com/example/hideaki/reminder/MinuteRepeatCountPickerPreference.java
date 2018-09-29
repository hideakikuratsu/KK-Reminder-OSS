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

public class MinuteRepeatCountPickerPreference extends Preference implements TextWatcher {

  private MainActivity activity;
  private EditText count;
  private String oldString = "";

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
      count.setText(String.valueOf(MainEditFragment.minuteRepeat.getOrg_count()));
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
    }
  }
}