package com.example.hideaki.reminder;

import android.content.Context;
import android.graphics.Color;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.Calendar;

public class RepeatCustomDaysOfMonthPickerPreference extends Preference implements View.OnClickListener {

  private TableRow last_table_row;
  private TableLayout calendar_table;
  private int max_days_of_month;
  private TableRow tableRow;
  private CheckableTextView day;
  private int mask_num;
  static int days_of_month;

  public RepeatCustomDaysOfMonthPickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    View view = View.inflate(getContext(), R.layout.repeat_custom_days_of_month_picker, null);
    return view;
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    //その月の最大日数に応じた日にちの表示処理
    days_of_month = MainEditFragment.repeat.getDays_of_month();
    last_table_row = view.findViewById(R.id.last_table_row);
    max_days_of_month = MainEditFragment.final_cal.getActualMaximum(Calendar.DAY_OF_MONTH);

    last_table_row.setVisibility(View.VISIBLE);
    for(int i = 0; i < 3; i++) {
      day = (CheckableTextView)last_table_row.getChildAt(i);
      day.setVisibility(View.VISIBLE);
    }

    if(max_days_of_month <= 28) last_table_row.setVisibility(View.GONE);
    else {
      for(int i = 3 - (31 - max_days_of_month); i < 3; i++) {
        day = (CheckableTextView)last_table_row.getChildAt(i);
        day.setVisibility(View.GONE);
      }
    }

    calendar_table = view.findViewById(R.id.calendar_table);
    for(int i = 0; i < calendar_table.getChildCount(); i++) {
      tableRow = (TableRow)calendar_table.getChildAt(i);
      for(int j = 0; j < tableRow.getChildCount(); j++) {
        day = (CheckableTextView)tableRow.getChildAt(j);
        if((days_of_month & (1 << (i * 7 + j))) != 0) {
          day.setBackgroundColor(Color.RED);
          day.setChecked(true);
        }
        day.setOnClickListener(this);
      }
    }
  }

  @Override
  public void onClick(View v) {

    day = (CheckableTextView)v;
    day.setChecked(!day.isChecked());
    mask_num = Integer.parseInt(day.getText().toString());

    if(day.isChecked()) {
      days_of_month |= (1 << (mask_num - 1));
      if(mask_num == max_days_of_month && mask_num != 31) days_of_month |= (1 << 30);
      day.setBackgroundColor(Color.RED);
    }
    else {
      days_of_month &= ~(1 << (mask_num - 1));
      if(mask_num == max_days_of_month && mask_num != 31) days_of_month &= ~(1 << 30);
      if(days_of_month == 0) {
        days_of_month |= (1 << (mask_num - 1));
        if(mask_num == max_days_of_month && mask_num != 31) days_of_month |= (1 << 30);
        day.setChecked(true);
      }
      else day.setBackgroundColor(getContext().getResources().getColor(android.R.color.background_light));
    }

    MainEditFragment.repeat.setDays_of_month(days_of_month);
  }
}
