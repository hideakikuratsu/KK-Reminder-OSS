package com.hideaki.kk_reminder;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.hideaki.kk_reminder.UtilClass.LOCALE;

public class NotifyIntervalDurationPickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

  private MainActivity activity;

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

    int hour = MainEditFragment.notifyInterval.getHour();
    int minute = MainEditFragment.notifyInterval.getMinute();

    activity = (MainActivity)getActivity();
    checkNotNull(activity);

    return new TimePickerDialog(activity, this, hour, minute, true);
  }

  @Override
  public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    if(hourOfDay == 0 && minute == 0) {
      MainEditFragment.notifyInterval.setHour(24);
    }
    else MainEditFragment.notifyInterval.setHour(hourOfDay);
    MainEditFragment.notifyInterval.setMinute(minute);

    NotifyInterval interval = MainEditFragment.notifyInterval;
    if(interval.getOrg_time() != 0) {
      Resources res = activity.getResources();
      String summary;
      String title = "";
      if(LOCALE.equals(Locale.JAPAN)) {
        summary = activity.getString(R.string.unless_complete_task);
        if(interval.getHour() != 0) {
          String tmp = res.getQuantityString(R.plurals.hour, interval.getHour(), interval.getHour());
          summary += tmp;
          title += tmp;
        }
        if(interval.getMinute() != 0) {
          String tmp = res.getQuantityString(R.plurals.minute, interval.getMinute(), interval.getMinute());
          summary += tmp;
          title += tmp;
        }
        summary += activity.getString(R.string.per);
        if(interval.getOrg_time() == -1) {
          summary += activity.getString(R.string.infinite_times_notify);
        }
        else {
          summary += res.getQuantityString(R.plurals.times_notify, interval.getOrg_time(),
              interval.getOrg_time());
        }
      }
      else {
        summary = "Notify every ";
        if(interval.getHour() != 0) {
          String tmp = res.getQuantityString(R.plurals.hour, interval.getHour(), interval.getHour()) + " ";
          summary += tmp;
          title += tmp;
        }
        if(interval.getMinute() != 0) {
          String tmp = res.getQuantityString(R.plurals.minute, interval.getMinute(), interval.getMinute()) + " ";
          summary += tmp;
          title += tmp;
        }
        if(interval.getOrg_time() != -1) {
          summary += res.getQuantityString(R.plurals.times_notify, interval.getOrg_time(),
              interval.getOrg_time()) + " ";
        }
        summary += activity.getString(R.string.unless_complete_task);
      }

      NotifyIntervalEditFragment.label.setSummary(summary);
      NotifyIntervalEditFragment.duration.setTitle(title);

      MainEditFragment.notifyInterval.setLabel(summary);
    }
    else {
      NotifyIntervalEditFragment.label.setSummary(activity.getString(R.string.non_notify));

      MainEditFragment.notifyInterval.setLabel(activity.getString(R.string.none));
    }
  }
}
