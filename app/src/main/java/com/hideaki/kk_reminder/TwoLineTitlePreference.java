package com.hideaki.kk_reminder;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;

public class TwoLineTitlePreference extends Preference {

  public TwoLineTitlePreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public TwoLineTitlePreference(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public TwoLineTitlePreference(Context context) {
    super(context);
  }

  @Override
  public void onBindViewHolder(PreferenceViewHolder holder) {

    super.onBindViewHolder(holder);
    TextView textView = (TextView)holder.findViewById(android.R.id.title);
    if(textView != null) textView.setSingleLine(false);
  }
}
