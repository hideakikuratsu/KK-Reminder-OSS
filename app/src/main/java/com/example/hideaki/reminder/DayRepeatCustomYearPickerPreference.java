package com.example.hideaki.reminder;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.Calendar;

public class DayRepeatCustomYearPickerPreference extends Preference implements CompoundButton.OnCheckedChangeListener {

  private CheckBox checkBox;
  private int mask_num;
  static int year;

  public DayRepeatCustomYearPickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    return View.inflate(getContext(), R.layout.repeat_custom_year_picker, null);
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    year = MainEditFragment.dayRepeat.getYear();

    TableLayout year_table = view.findViewById(R.id.year_table);
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
        if((year & (1 << (i * 6 + j))) != 0) checkBox.setChecked(true);
        checkBox.setOnCheckedChangeListener(this);
      }
    }
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    checkBox = (CheckBox)buttonView;
    mask_num = Integer.parseInt(checkBox.getTag().toString());

    if(checkBox.isChecked()) year |= (1 << mask_num);
    else {
      year &= ~(1 << mask_num);
      if(year == 0) {
        year |= (1 << mask_num);
        checkBox.setChecked(true);
      }
    }

    MainEditFragment.dayRepeat.setYear(year);
  }
}
