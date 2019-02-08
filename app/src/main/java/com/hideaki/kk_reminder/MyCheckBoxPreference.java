package com.hideaki.kk_reminder;

import android.content.Context;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.TextView;

public class MyCheckBoxPreference extends CheckBoxPreference {

  private AnimCheckBox animCheckBox;

  MyCheckBoxPreference(Context context) {

    super(context);
    setLayoutResource(R.layout.my_checkbox_preference_layout);
  }

  public MyCheckBoxPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
    setLayoutResource(R.layout.my_checkbox_preference_layout);
  }

  public MyCheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr) {

    super(context, attrs, defStyleAttr);
    setLayoutResource(R.layout.my_checkbox_preference_layout);
  }

  @Override
  public void onBindViewHolder(PreferenceViewHolder holder) {

    super.onBindViewHolder(holder);

    TextView title = (TextView)holder.findViewById(R.id.item_name);
    animCheckBox = (AnimCheckBox)holder.findViewById(R.id.checkBox);
    animCheckBox.setChecked(isChecked(), false);
    title.setText(getTitle());
  }

  @Override
  public void setChecked(boolean checked) {

    super.setChecked(checked);
    if(animCheckBox != null) animCheckBox.setChecked(checked);
  }
}