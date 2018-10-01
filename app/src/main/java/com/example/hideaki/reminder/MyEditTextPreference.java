package com.example.hideaki.reminder;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

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
  protected void onBindDialogView(View view) {

    super.onBindDialogView(view);

    EditText editText = view.findViewById(android.R.id.edit);
    editText.setSelection(editText.getText().length());
  }
}