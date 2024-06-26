package com.hideaki.kk_reminder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import static com.hideaki.kk_reminder.UtilClass.LOCALE;
import static java.util.Objects.requireNonNull;

public class DatePickerDialogFragment extends DialogFragment
  implements DatePickerDialog.OnDateSetListener {

  private final MainEditFragment mainEditFragment;

  DatePickerDialogFragment(MainEditFragment mainEditFragment) {

    this.mainEditFragment = mainEditFragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

    int year = MainEditFragment.finalCal.get(Calendar.YEAR);
    int month = MainEditFragment.finalCal.get(Calendar.MONTH);
    int day = MainEditFragment.finalCal.get(Calendar.DAY_OF_MONTH);

    MainActivity activity = (MainActivity)getActivity();
    requireNonNull(activity);

    if(activity.isDarkMode) {
      return new DatePickerDialog(activity, this, year, month, day);
    }
    else {
      return new DatePickerDialog(
        activity,
        activity.dialogStyleId,
        this,
        year,
        month,
        day
      );
    }
  }

  @Override
  public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    MainEditFragment.finalCal.set(Calendar.YEAR, year);
    MainEditFragment.finalCal.set(Calendar.MONTH, month);
    MainEditFragment.finalCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    if(LOCALE.equals(Locale.JAPAN)) {
      mainEditFragment.datePicker.setTitle(DateFormat.format(
        "yyyy年M月d日(E)",
        MainEditFragment.finalCal
      ));
    }
    else {
      mainEditFragment.datePicker.setTitle(DateFormat.format(
        "E, MMM d, yyyy",
        MainEditFragment.finalCal
      ));
    }
  }
}
