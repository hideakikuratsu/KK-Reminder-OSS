package com.hideaki.kk_reminder;

import android.content.Context;

import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

import android.util.AttributeSet;
import android.widget.TextView;

import static com.hideaki.kk_reminder.UtilClass.getPxFromDp;

public class MyPreferenceCategory extends PreferenceCategory {

  private final MainActivity activity;

  public MyPreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {

    super(context, attrs, defStyleAttr);
    activity = MainActivity.unwrap(context);
  }

  public MyPreferenceCategory(Context context, AttributeSet attrs) {

    super(context, attrs);
    activity = MainActivity.unwrap(context);
  }

  public MyPreferenceCategory(Context context) {

    super(context);
    activity = MainActivity.unwrap(context);
  }

  @Override
  public void onBindViewHolder(PreferenceViewHolder holder) {

    super.onBindViewHolder(holder);
    TextView titleView = (TextView)holder.findViewById(android.R.id.title);
    if(titleView != null) {
      titleView.setTextColor(activity.accentColor);
      titleView.setPaddingRelative(
        getPxFromDp(activity, 16),
        titleView.getPaddingTop(),
        titleView.getSelectionEnd(),
        titleView.getPaddingBottom()
      );
    }
  }
}