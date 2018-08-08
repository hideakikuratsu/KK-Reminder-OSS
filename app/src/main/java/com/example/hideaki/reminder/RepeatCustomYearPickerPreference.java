package com.example.hideaki.reminder;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

public class RepeatCustomYearPickerPreference extends Preference implements CompoundButton.OnCheckedChangeListener {

  private int mask_num;
  private int mask;

  public RepeatCustomYearPickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    View view = View.inflate(getContext(), R.layout.repeat_custom_year_picker, null);
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
      case R.id.january:
        mask_num = isChecked ? 1 : -1;
        break;
      case R.id.february:
        mask_num = isChecked ? 2 : -2;
        break;
      case R.id.march:
        mask_num = isChecked ? 3 : -3;
        break;
      case R.id.april:
        mask_num = isChecked ? 4 : -4;
        break;
      case R.id.may:
        mask_num = isChecked ? 5 : -5;
        break;
      case R.id.june:
        mask_num = isChecked ? 6 : -6;
        break;
      case R.id.july:
        mask_num = isChecked ? 7 : -7;
        break;
      case R.id.august:
        mask_num = isChecked ? 8 : -8;
        break;
      case R.id.september:
        mask_num = isChecked ? 9 : -9;
        break;
      case R.id.october:
        mask_num = isChecked ? 10 : -10;
        break;
      case R.id.november:
        mask_num = isChecked ? 11 : -11;
        break;
      case R.id.december:
        mask_num = isChecked ? 12 : -12;
        break;
    }

    if(mask_num < 0) {
      mask_num = -mask_num;
      mask <<= (mask_num - 1);
      MainEditFragment.repeat.setYear(MainEditFragment.repeat.getYear() & ~mask);
    }
    else {
      mask <<= (mask_num - 1);
      MainEditFragment.repeat.setYear(MainEditFragment.repeat.getYear() | mask);
    }
  }
}
