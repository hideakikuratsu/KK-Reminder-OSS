package com.hideaki.kk_reminder;

import android.content.Context;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.EditText;

import com.takisoft.fix.support.v7.preference.EditTextPreference;

public class MyEditTextPreference extends EditTextPreference {

  public MyEditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public MyEditTextPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MyEditTextPreference(Context context) {
    super(context);
  }

  @Override
  public void onBindViewHolder(PreferenceViewHolder holder) {

    super.onBindViewHolder(holder);

    EditText editText = getEditText();
    if(editText != null) {
      editText.setHint(R.string.detail_hint);
      editText.requestFocus();
      editText.setSelection(editText.getText().length());
    }
  }
}