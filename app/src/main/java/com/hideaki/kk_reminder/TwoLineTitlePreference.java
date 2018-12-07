package com.hideaki.kk_reminder;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
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
  protected void onBindView(View view) {

    super.onBindView(view);
    TextView textView = view.findViewById(android.R.id.title);
    if(textView != null) textView.setSingleLine(false);
  }
}
