package com.hideaki.kk_reminder;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class AdjustedSummaryTextSpacingPreference extends Preference {

  public AdjustedSummaryTextSpacingPreference(Context context, AttributeSet attrs, int defStyleAttr) {

    super(context, attrs, defStyleAttr);
  }

  public AdjustedSummaryTextSpacingPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
  }

  public AdjustedSummaryTextSpacingPreference(Context context) {

    super(context);
  }

  @Override
  public void onBindViewHolder(PreferenceViewHolder holder) {

    super.onBindViewHolder(holder);
    TextView summaryView = (TextView)holder.findViewById(android.R.id.summary);
    if(summaryView != null) {
      summaryView.setLineSpacing(0, (float)1.1);
    }
  }
}