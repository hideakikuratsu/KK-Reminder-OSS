package com.example.hideaki.reminder;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MyPreference extends Preference {

  public MyPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
    setLayoutResource(R.layout.date_picker);
  }

  public MyPreference(Context context) {
    super(context);
  }

  @Override
  protected void onBindView(View view) {
    super.onBindView(view);

    List<String> list = new ArrayList<>();
    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(Calendar.YEAR, 2018);
    for(int i = 0; i < 12; i++) {
      cal.set(Calendar.MONTH, i);
      for(int j = 1; j <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); j++) {
        list.add("2018年" + (i+1) + "月" + j + "日");
      }
    }

    NumberPicker picker = view.findViewById(R.id.day);
    picker.setDisplayedValues(null);
    picker.setMaxValue(364);
    picker.setMinValue(0);
    picker.setDisplayedValues(list.toArray(new String[list.size()]));
  }
}
