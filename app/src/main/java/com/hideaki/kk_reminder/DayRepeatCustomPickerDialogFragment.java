package com.hideaki.kk_reminder;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
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

public class DayRepeatCustomPickerDialogFragment extends DialogFragment {

  private final String[] SCALE_LIST_JA = {"日", "週", "月", "年"};
  private final String[] SCALE_LIST_EN = {"Day", "Week", "Month", "Year"};

  private NumberPicker interval;
  private NumberPicker scale;

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

    View view = View.inflate(getContext(), R.layout.repeat_custom_picker, null);
    MainActivity activity = (MainActivity)getActivity();
    checkNotNull(activity);

    //intervalの実装
    interval = view.findViewById(R.id.interval);
    interval.setDisplayedValues(null);
    interval.setMaxValue(1000);
    interval.setMinValue(1);
    interval.setValue(MainEditFragment.dayRepeat.getInterval());

    //scaleの実装
    scale = view.findViewById(R.id.scale);
    scale.setDisplayedValues(null);
    scale.setMaxValue(SCALE_LIST_JA.length);
    scale.setMinValue(1);
    scale.setValue(MainEditFragment.dayRepeat.getScale());
    if(LOCALE.equals(Locale.JAPAN)) {
      scale.setDisplayedValues(SCALE_LIST_JA);
    }
    else scale.setDisplayedValues(SCALE_LIST_EN);

    return new AlertDialog.Builder(activity)
        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

            int interval_val = interval.getValue();
            int scale_val = scale.getValue();

            MainEditFragment.dayRepeat.setInterval(interval_val);
            MainEditFragment.dayRepeat.setScale(scale_val);

            //Scaleの表示処理
            String intervalLabel = "";
            Resources res = getResources();
            DayRepeatCustomPickerFragment.rootPreferenceScreen.removeAll();
            DayRepeatCustomPickerFragment.rootPreferenceScreen.addPreference(DayRepeatCustomPickerFragment.picker);
            switch(MainEditFragment.dayRepeat.getScale()) {

              case 1: {

                if(LOCALE.equals(Locale.JAPAN) && interval_val == 1) {
                  intervalLabel = getString(R.string.everyday);
                }
                else if(LOCALE.equals(Locale.JAPAN) && interval_val > 1) {
                  intervalLabel = res.getQuantityString(R.plurals.per_day, interval_val - 1, interval_val - 1);
                }
                else {
                  intervalLabel = res.getQuantityString(R.plurals.per_day, interval_val, interval_val);
                }

                break;
              }
              case 2: {

                DayRepeatCustomPickerFragment.rootPreferenceScreen.addPreference(DayRepeatCustomPickerFragment.week);

                if(LOCALE.equals(Locale.JAPAN) && interval_val == 1) {
                  intervalLabel = getString(R.string.everyweek);
                }
                else if(LOCALE.equals(Locale.JAPAN) && interval_val > 1) {
                  intervalLabel = res.getQuantityString(R.plurals.per_week, interval_val - 1, interval_val - 1);
                }
                else {
                  intervalLabel = res.getQuantityString(R.plurals.per_week, interval_val, interval_val);
                }

                break;
              }
              case 3: {

                DayRepeatCustomPickerFragment.rootPreferenceScreen.addPreference(DayRepeatCustomPickerFragment.days_of_month);
                DayRepeatCustomPickerFragment.rootPreferenceScreen.addPreference(DayRepeatCustomPickerFragment.on_the_month);
                if(MainEditFragment.dayRepeat.isDays_of_month_setted()) {
                  DayRepeatCustomPickerFragment.rootPreferenceScreen.addPreference(DayRepeatCustomPickerFragment.days_of_month_picker);
                }
                else {
                  DayRepeatCustomPickerFragment.rootPreferenceScreen.addPreference(DayRepeatCustomPickerFragment.onTheMonthPicker);
                }

                if(LOCALE.equals(Locale.JAPAN) && interval_val == 1) {
                  intervalLabel = getString(R.string.everymonth);
                }
                else if(LOCALE.equals(Locale.JAPAN) && interval_val > 1) {
                  intervalLabel = res.getQuantityString(R.plurals.per_month, interval_val - 1, interval_val - 1);
                }
                else {
                  intervalLabel = res.getQuantityString(R.plurals.per_month, interval_val, interval_val);
                }

                //onTheMonthPickerのラベルを初期化
                String label = "";
                if(!MainEditFragment.dayRepeat.isDays_of_month_setted()) {
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

                break;
              }
              case 4: {

                DayRepeatCustomPickerFragment.rootPreferenceScreen.addPreference(DayRepeatCustomPickerFragment.year);

                if(LOCALE.equals(Locale.JAPAN) && interval_val == 1) {
                  intervalLabel = getString(R.string.everyyear);
                }
                else if(LOCALE.equals(Locale.JAPAN) && interval_val > 1) {
                  intervalLabel = res.getQuantityString(R.plurals.per_year, interval_val - 1, interval_val - 1);
                }
                else {
                  intervalLabel = res.getQuantityString(R.plurals.per_year, interval_val, interval_val);
                }

                break;
              }
            }

            //Pickerのラベルを更新
            DayRepeatCustomPickerFragment.picker.setTitle(intervalLabel);
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
