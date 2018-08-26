package com.example.hideaki.reminder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

  private DatePickerPreference datePickerPreference;
  private View view;

  public void setInstance(DatePickerPreference datePickerPreference, View view) {

    this.datePickerPreference = datePickerPreference;
    this.view = view;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

    int year = MainEditFragment.final_cal.get(Calendar.YEAR);
    int month = MainEditFragment.final_cal.get(Calendar.MONTH);
    int day = MainEditFragment.final_cal.get(Calendar.DAY_OF_MONTH);

    MainActivity activity = (MainActivity)getActivity();
    assert activity != null;

    return new DatePickerDialog(activity, this, year, month, day);
  }

  @Override
  public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    MainEditFragment.final_cal.set(Calendar.YEAR, year);
    MainEditFragment.final_cal.set(Calendar.MONTH, month);
    MainEditFragment.final_cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    datePickerPreference.onBindView(this.view);
  }
}
