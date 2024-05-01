package com.hideaki.kk_reminder;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.preference.CheckBoxPreference;
import androidx.preference.PreferenceViewHolder;

public class MyCheckBoxPreference extends CheckBoxPreference {

  private AnimCheckBox animCheckBox;
  private boolean isManuallyChecked;
  private MyCheckBoxPreferenceCheckedChangeListener listener = null;
  private boolean isFromOnClick;
  private boolean isChecked;
  private TextView mySummary;
  private final MainActivity activity;

  MyCheckBoxPreference(Context context) {

    super(context);
    activity = (MainActivity)((ContextWrapper)context).getBaseContext();
    setLayoutResource(R.layout.my_checkbox_preference_layout);
  }

  public MyCheckBoxPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
    activity = (MainActivity)((ContextWrapper)context).getBaseContext();
    setLayoutResource(R.layout.my_checkbox_preference_layout);
  }

  public MyCheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr) {

    super(context, attrs, defStyleAttr);
    activity = (MainActivity)((ContextWrapper)context).getBaseContext();
    setLayoutResource(R.layout.my_checkbox_preference_layout);
  }

  @Override
  public void onAttached() {

    super.onAttached();
    if(listener == null) {
      throw new RuntimeException(
        getKey() + " must call setOnMyCheckBoxPreferenceCheckedChangeListener");
    }
    isChecked = super.isChecked();
  }

  @Override
  public void onBindViewHolder(PreferenceViewHolder holder) {

    super.onBindViewHolder(holder);

    TextView title = (TextView)holder.findViewById(R.id.item_name);
    mySummary = (TextView)holder.findViewById(R.id.my_summary);
    animCheckBox = (AnimCheckBox)holder.findViewById(R.id.checkBox);
    animCheckBox.setOnCheckedChangeListener(null);
    animCheckBox.setChecked(isChecked, false);
    animCheckBox.setOnCheckedChangeListener((view, checked) -> {

      isChecked = checked;
      if(isManuallyChecked) {
        listener.onCheckedChange(getKey(), checked);
      }
    });
    title.setText(getTitle());
  }

  @Override
  protected void onClick() {

    isFromOnClick = true;
    super.onClick();
  }

  @Override
  public boolean isChecked() {

    return isChecked;
  }

  @Override
  public void setChecked(boolean checked) {

    if(!isFromOnClick) {
      isManuallyChecked = false;
    }
    else {
      isFromOnClick = false;
    }

    super.setChecked(checked);
    if(animCheckBox != null) {
      animCheckBox.setChecked(checked);
    }

    isManuallyChecked = true;
  }

  void setOnMyCheckBoxPreferenceCheckedChangeListener(MyCheckBoxPreferenceCheckedChangeListener listener) {

    this.listener = listener;
  }

  public interface MyCheckBoxPreferenceCheckedChangeListener {

    void onCheckedChange(String key, boolean checked);
  }

  TextView getMySummary() {

    return mySummary;
  }

  void setMySummary(final String summary) {

    if(mySummary != null) {
      mySummary.setText(summary);
    }
    else {
      new Thread(() -> {

        while(mySummary == null) {
          try {
            //noinspection BusyWait
            Thread.sleep(10);
          }
          catch(InterruptedException e) {
            Log.e("MyCheckBoxPreference#setMySummary", Log.getStackTraceString(e));
          }
        }
        new Handler(Looper.getMainLooper()).post(() -> mySummary.setText(summary));
      }).start();
    }
  }

  void setMySummary(final int stringId) {

    if(mySummary != null) {
      mySummary.setText(activity.getString(stringId));
    }
    else {
      new Thread(() -> {

        while(mySummary == null) {
          try {
            //noinspection BusyWait
            Thread.sleep(10);
          }
          catch(InterruptedException e) {
            Log.e("MyCheckBoxPreference#setMySummary", Log.getStackTraceString(e));
          }
        }
        new Handler(Looper.getMainLooper()).post(
            () -> mySummary.setText(activity.getString(stringId))
        );
      }).start();
    }
  }

  void showMySummary() {

    if(mySummary != null) {
      mySummary.setVisibility(View.VISIBLE);
    }
    else {
      new Thread(() -> {

        while(mySummary == null) {
          try {
            //noinspection BusyWait
            Thread.sleep(10);
          }
          catch(InterruptedException e) {
            Log.e("MyCheckBoxPreference#showMySummary", Log.getStackTraceString(e));
          }
        }
        new Handler(Looper.getMainLooper()).post(() -> mySummary.setVisibility(View.VISIBLE));
      }).start();
    }
  }

  void hideMySummary() {

    if(mySummary != null) {
      mySummary.setVisibility(View.GONE);
    }
    else {
      new Thread(() -> {

        while(mySummary == null) {
          try {
            //noinspection BusyWait
            Thread.sleep(10);
          }
          catch(InterruptedException e) {
            Log.e("MyCheckBoxPreference#hideMySummary", Log.getStackTraceString(e));
          }
        }
        new Handler(Looper.getMainLooper()).post(() -> mySummary.setVisibility(View.GONE));
      }).start();
    }
  }
}