package com.hideaki.kk_reminder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.hideaki.kk_reminder.UtilClass.LOCALE;

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

    int year = MainEditFragment.final_cal.get(Calendar.YEAR);
    int month = MainEditFragment.final_cal.get(Calendar.MONTH);
    int day = MainEditFragment.final_cal.get(Calendar.DAY_OF_MONTH);

    MainActivity activity = (MainActivity)getActivity();
    checkNotNull(activity);

    return new DatePickerDialog(activity, this, year, month, day);
  }

  @Override
  public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    MainEditFragment.final_cal.set(Calendar.YEAR, year);
    MainEditFragment.final_cal.set(Calendar.MONTH, month);
    MainEditFragment.final_cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    if(LOCALE.equals(Locale.JAPAN)) {
      MainEditFragment.datePicker.setTitle(DateFormat.format("yyyy年M月d日(E)", MainEditFragment.final_cal));
    }
    else {
      MainEditFragment.datePicker.setTitle(DateFormat.format("yyyy/M/d (E)", MainEditFragment.final_cal));
    }
  }
}
