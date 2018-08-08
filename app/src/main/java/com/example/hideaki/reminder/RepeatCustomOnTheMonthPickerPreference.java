package com.example.hideaki.reminder;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class RepeatCustomOnTheMonthPickerPreference extends Preference {

  public RepeatCustomOnTheMonthPickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    View view = View.inflate(getContext(), R.layout.repeat_custom_on_the_month_picker, null);
    return view;
  }

  @Override
  protected void onBindView(View view) {
    super.onBindView(view);
  }
}
