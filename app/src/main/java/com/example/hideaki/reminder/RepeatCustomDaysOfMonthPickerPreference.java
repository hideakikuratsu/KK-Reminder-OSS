package com.example.hideaki.reminder;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class RepeatCustomDaysOfMonthPickerPreference extends Preference {

  public RepeatCustomDaysOfMonthPickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    View view = View.inflate(getContext(), R.layout.repeat_custom_days_of_month_picker, null);
    return view;
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);


  }
}
