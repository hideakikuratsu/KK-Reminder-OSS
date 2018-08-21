package com.example.hideaki.reminder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import java.io.IOException;
import java.util.Calendar;

import static com.example.hideaki.reminder.MainEditFragment.actionBar;
import static com.example.hideaki.reminder.MainEditFragment.detail_str;
import static com.example.hideaki.reminder.MainEditFragment.direct_boot_context;
import static com.example.hideaki.reminder.MainEditFragment.final_cal;
import static com.example.hideaki.reminder.MainEditFragment.fragmentManager;
import static com.example.hideaki.reminder.MainEditFragment.item;
import static com.example.hideaki.reminder.MainEditFragment.mListener;
import static com.example.hideaki.reminder.MainEditFragment.minuteRepeat;
import static com.example.hideaki.reminder.MainEditFragment.notes_str;
import static com.example.hideaki.reminder.MainEditFragment.repeat;

public class DateAlterDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

  private long altered_time;

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    return builder
        .setMessage(R.string.repeat_conflict_dialog_message)
        .setPositiveButton(R.string.yes, this)
        .setNegativeButton(R.string.no, this)
        .setNeutralButton(R.string.cancel, this)
        .create();
  }

  @Override
  public void onClick(DialogInterface dialog, int which) {

    switch(which) {
      case DialogInterface.BUTTON_POSITIVE:
        if(item.getTime_altered() == 0) {
          item.setOrg_date((Calendar)item.getDate().clone());
        }
        altered_time = (final_cal.getTimeInMillis()
            - item.getDate().getTimeInMillis()) / (1000 * 60);
        item.addTime_altered(altered_time * 60 * 1000);

        registerItem();
        break;
      case DialogInterface.BUTTON_NEGATIVE:
        item.setOrg_date((Calendar)final_cal.clone());
        item.setTime_altered(0);

        registerItem();
        break;
      case DialogInterface.BUTTON_NEUTRAL:
        break;
    }
  }

  private void registerItem() {

    actionBar.setDisplayHomeAsUpEnabled(false);
    actionBar.setTitle(R.string.app_name);
    fragmentManager.popBackStack();

    item.setDetail(detail_str);
    item.setDate((Calendar)final_cal.clone());
    item.setNotes(notes_str);
    if(repeat.getSetted() != 0) {
      if(repeat.getSetted() == (1 << 0)) repeat.dayClear();
      else if(repeat.getSetted() == (1 << 1)) repeat.weekClear();
      else if(repeat.getSetted() == (1 << 2)) {
        if(repeat.isDays_of_month_setted()) repeat.daysOfMonthClear();
        else repeat.onTheMonthClear();
      }
      else if(repeat.getSetted() == (1 << 3)) repeat.yearClear();
    }
    else {
      repeat.clear();
    }
    item.setRepeat(repeat.clone());
    minuteRepeat.setCount(minuteRepeat.getOrg_count());
    minuteRepeat.setDuration(minuteRepeat.getOrgDuration());
    item.setMinuteRepeat(minuteRepeat.clone());
    if(item.isAlarm_stopped()) item.setAlarm_stopped(false);

    if(mListener.isItemExists(item, MyDatabaseHelper.TODO_TABLE)) {
      mListener.notifyDataSetChanged();
      try {
        mListener.updateDB(item, MyDatabaseHelper.TODO_TABLE);
      } catch(IOException e) {
        e.printStackTrace();
      }
    }
    else {
      mListener.addChildren(item);
      mListener.notifyDataSetChanged();

      try {
        mListener.insertDB(item, MyDatabaseHelper.TODO_TABLE);
      } catch(IOException e) {
        e.printStackTrace();
      }
    }

    //データベースに挿入を行ったら、そのデータベースを端末暗号化ストレージへコピーする
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      direct_boot_context = getActivity().createDeviceProtectedStorageContext();
      direct_boot_context.moveDatabaseFrom(getActivity(), MyDatabaseHelper.TODO_TABLE);
    }

    mListener.deleteAlarm(item);
    mListener.setAlarm(item);
  }
}
