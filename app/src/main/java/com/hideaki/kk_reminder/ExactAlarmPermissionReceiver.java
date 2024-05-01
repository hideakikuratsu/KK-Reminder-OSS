package com.hideaki.kk_reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import static com.hideaki.kk_reminder.UtilClass.ITEM;
import static com.hideaki.kk_reminder.UtilClass.deserialize;
import static com.hideaki.kk_reminder.UtilClass.serialize;
import static java.util.Objects.requireNonNull;

public class ExactAlarmPermissionReceiver extends BroadcastReceiver {

  private DBAccessor accessor;

  @Override
  public void onReceive(Context context, Intent intent) {

    if(
        AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED.equals(intent.getAction())
    ) {
      accessor = new DBAccessor(context, false);
      resetAlarm(context);
    }
  }

  private void resetAlarm(Context context) {

    List<byte[]> streamList = accessor.executeQueryAll(MyDatabaseHelper.TODO_TABLE);
    for(byte[] stream : streamList) {

      ItemAdapter item = new ItemAdapter(deserialize(stream));
      requireNonNull(item);

      if(
        item.getDate().getTimeInMillis() > System.currentTimeMillis() &&
          item.getWhichListBelongs() == 0
      ) {
        Intent setAlarm = new Intent(context, AlarmReceiver.class);
        byte[] obArray = serialize(item.getItem());
        setAlarm.putExtra(ITEM, obArray);
        PendingIntent sender = PendingIntent.getBroadcast(
          context, (int)item.getId(), setAlarm,
          PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        requireNonNull(alarmManager);

        alarmManager.setAlarmClock(
          new AlarmManager.AlarmClockInfo(
            item.getDate().getTimeInMillis(),
            null
          ),
          sender
        );
      }
    }
  }
}
