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

public class DayRepeatCustomWeekPickerPreference extends Preference implements CompoundButton.OnCheckedChangeListener {

  private CheckBox checkBox;
  private int mask_num;
  static int week;

  public DayRepeatCustomWeekPickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    return View.inflate(getContext(), R.layout.repeat_custom_week_picker, null);
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    week = MainEditFragment.dayRepeat.getWeek();

    TableLayout week_table = view.findViewById(R.id.week_table);
    if(week == 0) {
      int day_of_week = MainEditFragment.final_cal.get(Calendar.DAY_OF_WEEK);
      mask_num = day_of_week == 1 ? day_of_week + 5 : day_of_week - 2;
      week |= (1 << mask_num);
      MainEditFragment.dayRepeat.setWeek(week);
    }
    int week_table_size = week_table.getChildCount();
    for(int i = 0; i < week_table_size; i++) {
      TableRow tableRow = (TableRow)week_table.getChildAt(i);
      int table_row_size = tableRow.getChildCount();
      for(int j = 0; j < table_row_size; j++) {
        checkBox = (CheckBox)tableRow.getChildAt(j);
        if((week & (1 << (i * 5 + j))) != 0) checkBox.setChecked(true);
        checkBox.setOnCheckedChangeListener(this);
      }
    }
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    checkBox = (CheckBox)buttonView;
    checkBox.jumpDrawablesToCurrentState();
    mask_num = Integer.parseInt(checkBox.getTag().toString());

    if(checkBox.isChecked()) week |= (1 << mask_num);
    else {
      week &= ~(1 << mask_num);
      if(week == 0) {
        week |= (1 << mask_num);
        checkBox.setChecked(true);
        checkBox.jumpDrawablesToCurrentState();
      }
    }

    MainEditFragment.dayRepeat.setWeek(week);
  }
}
