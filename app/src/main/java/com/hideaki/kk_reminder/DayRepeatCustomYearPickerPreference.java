package com.hideaki.kk_reminder;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.Calendar;

public class DayRepeatCustomYearPickerPreference extends Preference implements CompoundButton.OnCheckedChangeListener {

  private CheckBox checkBox;
  private int mask_num;
  static int year;
  private final ColorStateList colorStateList;

  public DayRepeatCustomYearPickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
    setLayoutResource(R.layout.repeat_custom_year_picker);
    MainActivity activity = (MainActivity)((ContextWrapper)context).getBaseContext();
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
  public void onBindViewHolder(PreferenceViewHolder holder) {

    super.onBindViewHolder(holder);

    year = MainEditFragment.dayRepeat.getYear();

    TableLayout year_table = (TableLayout)holder.findViewById(R.id.year_table);
    if(year == 0) {
      mask_num = MainEditFragment.final_cal.get(Calendar.MONTH);
      year |= (1 << mask_num);
      MainEditFragment.dayRepeat.setYear(year);
    }
    int year_table_size = year_table.getChildCount();
    for(int i = 0; i < year_table_size; i++) {
      TableRow tableRow = (TableRow)year_table.getChildAt(i);
      int table_row_size = tableRow.getChildCount();
      for(int j = 0; j < table_row_size; j++) {
        checkBox = (CheckBox)tableRow.getChildAt(j);
        CompoundButtonCompat.setButtonTintList(checkBox, colorStateList);
        if((year & (1 << (i * 4 + j))) != 0) checkBox.setChecked(true);
        checkBox.setOnCheckedChangeListener(this);
      }
    }
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    checkBox = (CheckBox)buttonView;
    checkBox.jumpDrawablesToCurrentState();
    mask_num = Integer.parseInt(checkBox.getTag().toString());

    if(checkBox.isChecked()) year |= (1 << mask_num);
    else {
      year &= ~(1 << mask_num);
      if(year == 0) {
        year |= (1 << mask_num);
        checkBox.setChecked(true);
        checkBox.jumpDrawablesToCurrentState();
      }
    }

    MainEditFragment.dayRepeat.setYear(year);
  }
}
