package com.hideaki.kk_reminder;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class MyPreferenceCategory extends PreferenceCategory {

  MainActivity activity;

  public MyPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {

    super(context, attrs, defStyleAttr);
    activity = (MainActivity)context;
  }

  public MyPreferenceCategory(Context context, AttributeSet attrs) {

    super(context, attrs);
    activity = (MainActivity)context;
  }

  public MyPreferenceCategory(Context context) {

    super(context);
    activity = (MainActivity)context;
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);
    TextView titleView = view.findViewById(android.R.id.title);
    titleView.setTextColor(activity.accent_color);
  }
}