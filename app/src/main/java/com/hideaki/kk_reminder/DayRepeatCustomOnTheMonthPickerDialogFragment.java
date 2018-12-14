package com.hideaki.kk_reminder;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.NumberPicker;

import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.hideaki.kk_reminder.UtilClass.LOCALE;

public class DayRepeatCustomOnTheMonthPickerDialogFragment extends DialogFragment {

  static final String[] ORDINAL_NUMBER_LIST_JA = {"第一", "第二", "第三", "第四", "最終"};
  static final String[] ORDINAL_NUMBER_LIST_EN = {"1st", "2nd", "3rd", "4th", "Last"};
  static final String[] DAY_OF_WEEK_LIST_JA = {"月曜日", "火曜日", "水曜日", "木曜日", "金曜日",
      "土曜日", "日曜日", "平日", "週末"};
  static final String[] DAY_OF_WEEK_LIST_EN = {"Mon", "Tue", "Wed", "Thu", "Fri",
      "Sat", "Sun", "Weekday", "Weekend Day"};

  private NumberPicker ordinal_number;
  private NumberPicker day_of_week;

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

    View view = View.inflate(getContext(), R.layout.repeat_custom_on_the_month_picker, null);
    MainActivity activity = (MainActivity)getActivity();
    checkNotNull(activity);

    //ordinal_numberの実装
    ordinal_number = view.findViewById(R.id.ordinal_number);
    ordinal_number.setDisplayedValues(null);
    ordinal_number.setMaxValue(ORDINAL_NUMBER_LIST_JA.length);
    ordinal_number.setMinValue(1);
    ordinal_number.setValue(MainEditFragment.dayRepeat.getOrdinal_number());
    if(LOCALE.equals(Locale.JAPAN)) {
      ordinal_number.setDisplayedValues(ORDINAL_NUMBER_LIST_JA);
    }
    else ordinal_number.setDisplayedValues(ORDINAL_NUMBER_LIST_EN);

    //day_on_weekの実装
    day_of_week = view.findViewById(R.id.day_on_week);
    day_of_week.setDisplayedValues(null);
    day_of_week.setMaxValue(DAY_OF_WEEK_LIST_JA.length - 1);
    day_of_week.setMinValue(0);
    day_of_week.setValue(MainEditFragment.dayRepeat.getOn_the_month().ordinal());
    if(LOCALE.equals(Locale.JAPAN)) {
      day_of_week.setDisplayedValues(DAY_OF_WEEK_LIST_JA);
    }
    else day_of_week.setDisplayedValues(DAY_OF_WEEK_LIST_EN);

    return new AlertDialog.Builder(activity)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

            MainEditFragment.dayRepeat.setOrdinal_number(ordinal_number.getValue());
            MainEditFragment.dayRepeat.setOn_the_month(Week.values()[day_of_week.getValue()]);

            //onTheMonthPickerのラベルを更新
            String label = "";
            if(MainEditFragment.dayRepeat.getOrdinal_number() < 5) {
              if(LOCALE.equals(Locale.JAPAN)) {
                label += "第" + MainEditFragment.dayRepeat.getOrdinal_number();
              }
              else {
                int ordinal_num = MainEditFragment.dayRepeat.getOrdinal_number();
                String ordinal_str = ordinal_num + "";
                if(ordinal_num == 1) ordinal_str += "st";
                else if(ordinal_num == 2) ordinal_str += "nd";
                else if(ordinal_num == 3) ordinal_str += "rd";
                else ordinal_str += "th";
                label += ordinal_str + " ";
              }
            }
            else {
              if(LOCALE.equals(Locale.JAPAN)) label += "最終週の";
              else label += "Last ";
            }

            if(MainEditFragment.dayRepeat.getOn_the_month().ordinal() < 7) {
              if(LOCALE.equals(Locale.JAPAN)) {
                label += DayRepeatCustomPickerFragment
                    .DAY_OF_WEEK_LIST_JA[MainEditFragment.dayRepeat.getOn_the_month().ordinal()] + "曜日";
              }
              else {
                label += DayRepeatCustomPickerFragment
                    .DAY_OF_WEEK_LIST_EN[MainEditFragment.dayRepeat.getOn_the_month().ordinal()];
              }
            }
            else if(MainEditFragment.dayRepeat.getOn_the_month().ordinal() == 7) {
              if(LOCALE.equals(Locale.JAPAN)) label += "平日";
              else label += "Weekday";
            }
            else if(MainEditFragment.dayRepeat.getOn_the_month().ordinal() == 8) {
              if(LOCALE.equals(Locale.JAPAN)) label += "週末";
              else label += "Weekend day";
            }

            DayRepeatCustomPickerFragment.onTheMonthPicker.setTitle(label);
          }
        })
        .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {}
        })
        .setView(view)
        .create();
  }
}
