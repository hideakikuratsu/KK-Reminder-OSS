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

public class RepeatCustomWeekPickerPreference extends Preference implements CompoundButton.OnCheckedChangeListener {

  private TableLayout week_table;
  private TableRow tableRow;
  private CheckBox checkBox;
  private int mask_num;
  static int week;
  private static Repeat repeat;
  private int cal_day_of_week;

  public RepeatCustomWeekPickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    View view = View.inflate(getContext(), R.layout.repeat_custom_week_picker, null);
    return view;
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    week = MainEditFragment.repeat.getWeek();

    week_table = view.findViewById(R.id.week_table);
    if(repeat == MainEditFragment.repeat || week != 0) {
      for(int i = 0; i < week_table.getChildCount(); i++) {
        tableRow = (TableRow)week_table.getChildAt(i);
        for(int j = 0; j < tableRow.getChildCount(); j++) {
          checkBox = (CheckBox)tableRow.getChildAt(j);
          if((week & (1 << i)) != 0) checkBox.setChecked(true);
          checkBox.setOnCheckedChangeListener(this);
        }
      }
    }
    else {
      week = 0;
      cal_day_of_week = MainEditFragment.final_cal.get(Calendar.DAY_OF_WEEK);

      for(int i = 0; i < week_table.getChildCount(); i++) {
        tableRow = (TableRow)week_table.getChildAt(i);
        for(int j = 0; j < tableRow.getChildCount(); j++) {
          checkBox = (CheckBox)tableRow.getChildAt(j);
          mask_num = Integer.parseInt(checkBox.getTag().toString());

          if(mask_num + 2 == cal_day_of_week || mask_num - 5 == cal_day_of_week) {
            week |= (1 << mask_num);
            checkBox.setChecked(true);
          }

          checkBox.setOnCheckedChangeListener(this);
        }
      }

      MainEditFragment.repeat.setWeek(week);
      repeat = MainEditFragment.repeat;
    }
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    checkBox = (CheckBox)buttonView;
    mask_num = Integer.parseInt(checkBox.getTag().toString());

    if(checkBox.isChecked()) week |= (1 << mask_num);
    else {
      week &= ~(1 << mask_num);
      if(week == 0) {
        week |= (1 << mask_num);
        checkBox.setChecked(true);
      }
    }

    MainEditFragment.repeat.setWeek(week);
  }
}
