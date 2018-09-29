package com.example.hideaki.reminder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.preference.CheckBoxPreference;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class MyCheckBoxPreference extends CheckBoxPreference {

  private MainActivity activity;
  private ColorStateList colorStateList;

  MyCheckBoxPreference(Context context) {

    super(context);
    activity = (MainActivity)context;
    colorStateList = new ColorStateList(
        new int[][] {
            new int[]{-android.R.attr.state_checked}, // unchecked
            new int[]{android.R.attr.state_checked} // checked
        },
        new int[] {
            ContextCompat.getColor(activity, R.color.icon_gray),
            activity.accent_color
        }
    );
  }

  public MyCheckBoxPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
    activity = (MainActivity)context;
    colorStateList = new ColorStateList(
        new int[][] {
            new int[]{-android.R.attr.state_checked}, // unchecked
            new int[]{android.R.attr.state_checked} // checked
        },
        new int[] {
            ContextCompat.getColor(activity, R.color.icon_gray),
            activity.accent_color
        }
    );
  }

  public MyCheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr) {

    super(context, attrs, defStyleAttr);
    activity = (MainActivity)context;
    colorStateList = new ColorStateList(
        new int[][] {
            new int[]{-android.R.attr.state_checked}, // unchecked
            new int[]{android.R.attr.state_checked} // checked
        },
        new int[] {
            ContextCompat.getColor(activity, R.color.icon_gray),
            activity.accent_color
        }
    );
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    CheckBox checkBox = view.findViewById(android.R.id.checkbox);
    CompoundButtonCompat.setButtonTintList(checkBox, colorStateList);
    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        buttonView.jumpDrawablesToCurrentState();
      }
    });
  }
}
