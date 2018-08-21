package com.example.hideaki.reminder;

import android.content.Context;
import android.preference.Preference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

public class MinuteRepeatCountPickerPreference extends Preference {

  private EditText count;
  private ImageView plus;
  private ImageView minus;

  public MinuteRepeatCountPickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    View view = View.inflate(getContext(), R.layout.minute_repeat_count_picker, null);
    return view;
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    count = view.findViewById(R.id.count);
    count.setText(Integer.toString(MainEditFragment.minuteRepeat.getOrg_count()));
    count.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {}

      @Override
      public void afterTextChanged(Editable s) {
        if(!s.toString().equals("")) {
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
    });

    plus = view.findViewById(R.id.plus);
    plus.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        MainEditFragment.minuteRepeat.addOrg_count(1);
        count.setText(Integer.toString(MainEditFragment.minuteRepeat.getOrg_count()));

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

    minus = view.findViewById(R.id.minus);
    minus.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(MainEditFragment.minuteRepeat.getOrg_count() > 1) {
          MainEditFragment.minuteRepeat.addOrg_count(-1);
          count.setText(Integer.toString(MainEditFragment.minuteRepeat.getOrg_count()));

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