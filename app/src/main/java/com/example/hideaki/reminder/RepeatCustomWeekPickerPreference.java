package com.example.hideaki.reminder;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

public class RepeatCustomWeekPickerPreference extends Preference implements CompoundButton.OnCheckedChangeListener {

  private int mask_num;
  private int mask;

  public RepeatCustomWeekPickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    View view = View.inflate(getContext(), R.layout.repeat_custom_week_picker, null);
    return view;
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    mask = 1;
    switch(buttonView.getId()) {
      case R.id.monday:
        mask_num = isChecked ? 1 : -1;
        break;
      case R.id.tuesday:
        mask_num = isChecked ? 2 : -2;
        break;
      case R.id.wednesday:
        mask_num = isChecked ? 3 : -3;
        break;
      case R.id.thursday:
        mask_num = isChecked ? 4 : -4;
        break;
      case R.id.friday:
        mask_num = isChecked ? 5 : -5;
        break;
      case R.id.saturday:
        mask_num = isChecked ? 6 : -6;
        break;
      case R.id.sunday:
        mask_num = isChecked ? 7 : -7;
        break;
    }

    if(mask_num < 0) {
      mask_num = -mask_num;
      mask <<= (mask_num - 1);
      MainEditFragment.repeat.setWeek(MainEditFragment.repeat.getWeek() & ~mask);
    }
    else {
      mask <<= (mask_num - 1);
      MainEditFragment.repeat.setWeek(MainEditFragment.repeat.getWeek() | mask);
    }
  }
}
