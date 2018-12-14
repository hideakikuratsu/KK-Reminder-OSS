package com.hideaki.kk_reminder;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

import static com.google.common.base.Preconditions.checkNotNull;

public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

    int hour = MainEditFragment.final_cal.get(Calendar.HOUR_OF_DAY);
    int minute = MainEditFragment.final_cal.get(Calendar.MINUTE);

    MainActivity activity = (MainActivity)getActivity();
    checkNotNull(activity);

    return new TimePickerDialog(activity, this, hour, minute, true);
  }

  @Override
  public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    MainEditFragment.final_cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
    MainEditFragment.final_cal.set(Calendar.MINUTE, minute);
    MainEditFragment.timePicker.setTitle(DateFormat.format("kk:mm", MainEditFragment.final_cal));
  }
}
