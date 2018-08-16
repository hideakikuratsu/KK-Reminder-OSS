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

public class RepeatCustomYearPickerPreference extends Preference implements CompoundButton.OnCheckedChangeListener {

  private TableLayout year_table;
  private TableRow tableRow;
  private CheckBox checkBox;
  private int mask_num;
  static int year;
  private int cal_month;

  public RepeatCustomYearPickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    View view = View.inflate(getContext(), R.layout.repeat_custom_year_picker, null);
    return view;
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    year = MainEditFragment.repeat.getYear();

    year_table = view.findViewById(R.id.year_table);
    if(year != 0) {
      for(int i = 0; i < year_table.getChildCount(); i++) {
        tableRow = (TableRow)year_table.getChildAt(i);
        for(int j = 0; j < tableRow.getChildCount(); j++) {
          checkBox = (CheckBox)tableRow.getChildAt(j);
          if((year & (1 << i)) != 0) checkBox.setChecked(true);
          checkBox.setOnCheckedChangeListener(this);
        }
      }
    }
    else {
      year = 0;
      cal_month = MainEditFragment.final_cal.get(Calendar.MONTH);

      for(int i = 0; i < year_table.getChildCount(); i++) {
        tableRow = (TableRow)year_table.getChildAt(i);
        for(int j = 0; j < tableRow.getChildCount(); j++) {
          checkBox = (CheckBox)tableRow.getChildAt(j);
          mask_num = Integer.parseInt(checkBox.getTag().toString());

          if(mask_num == cal_month) {
            year |= (1 << mask_num);
            checkBox.setChecked(true);
          }

          checkBox.setOnCheckedChangeListener(this);
        }
      }

      MainEditFragment.repeat.setYear(year);
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

    MainEditFragment.repeat.setYear(year);
  }
}
