package com.hideaki.kk_reminder;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import static com.hideaki.kk_reminder.UtilClass.LOCALE;
import static com.hideaki.kk_reminder.UtilClass.appendTimeLimitLabelOfDayRepeat;
import static java.util.Objects.requireNonNull;

public class DayRepeatTimeLimitPickerDialogFragment extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {

  private final DayRepeatEditFragment dayRepeatEditFragment;
  private boolean isProcessed;

  DayRepeatTimeLimitPickerDialogFragment(DayRepeatEditFragment dayRepeatEditFragment) {

    this.dayRepeatEditFragment = dayRepeatEditFragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

    isProcessed = false;
    Calendar tomorrow = Calendar.getInstance();
    tomorrow.set(Calendar.HOUR_OF_DAY, 0);
    tomorrow.set(Calendar.MINUTE, 0);
    tomorrow.set(Calendar.SECOND, 0);
    tomorrow.set(Calendar.MILLISECOND, 0);
    tomorrow.add(Calendar.DAY_OF_MONTH, 1);
    int year, month, day;
    Calendar tmpTimeLimit = MainEditFragment.tmpTimeLimit;
    if(tmpTimeLimit == null) {
      year = tomorrow.get(Calendar.YEAR);
      month = tomorrow.get(Calendar.MONTH);
      day = tomorrow.get(Calendar.DAY_OF_MONTH);
    }
    else {
      year = tmpTimeLimit.get(Calendar.YEAR);
      month = tmpTimeLimit.get(Calendar.MONTH);
      day = tmpTimeLimit.get(Calendar.DAY_OF_MONTH);
    }

    MainActivity activity = (MainActivity)getActivity();
    requireNonNull(activity);

    DatePickerDialog dialog;
    if(activity.isDarkMode) {
      dialog = new DatePickerDialog(activity, this, year, month, day);
    }
    else {
      dialog = new DatePickerDialog(
          activity,
          activity.dialogStyleId,
          this,
          year,
          month,
          day
      );
    }
    dialog.getDatePicker().setMinDate(tomorrow.getTimeInMillis());

    return dialog;
  }

  @Override
  public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    isProcessed = true;
    Calendar timeLimit = Calendar.getInstance();
    timeLimit.set(Calendar.YEAR, year);
    timeLimit.set(Calendar.MONTH, month);
    timeLimit.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    timeLimit.set(Calendar.HOUR_OF_DAY, 0);
    timeLimit.set(Calendar.MINUTE, 0);
    timeLimit.set(Calendar.SECOND, 0);
    timeLimit.set(Calendar.MILLISECOND, 0);
    MainEditFragment.tmpTimeLimit = (Calendar)timeLimit.clone();
    MainEditFragment.dayRepeat.setTimeLimit((Calendar)timeLimit.clone());
    if(!dayRepeatEditFragment.never.isChecked()) {
      String label = appendTimeLimitLabelOfDayRepeat(
          MainEditFragment.dayRepeat.getTimeLimit(),
          dayRepeatEditFragment.label.getSummary().toString()
      );
      dayRepeatEditFragment.label.setSummary(label);
      MainEditFragment.dayRepeat.setLabel(label);
    }
    if(LOCALE.equals(Locale.JAPAN)) {
      dayRepeatEditFragment.timeLimit.setTitle(DateFormat.format(
          "yyyy年M月d日(E)",
          timeLimit
      ));
    }
    else {
      dayRepeatEditFragment.timeLimit.setTitle(DateFormat.format(
          "E, MMM d, yyyy",
          timeLimit
      ));
    }
  }

  @Override
  public void onCancel(@NonNull DialogInterface dialog) {

    super.onCancel(dialog);
    isProcessed = true;
    dayRepeatEditFragment.timeLimit.setChecked(false);
  }

  @Override
  public void onDismiss(@NonNull DialogInterface dialog) {

    super.onDismiss(dialog);
    if(!isProcessed) {
      dayRepeatEditFragment.timeLimit.setChecked(false);
    }
  }
}
