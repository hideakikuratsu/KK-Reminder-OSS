package com.hideaki.kk_reminder;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.hideaki.kk_reminder.UtilClass.ACTION_IN_NOTIFICATION;
import static com.hideaki.kk_reminder.UtilClass.CREATED;
import static com.hideaki.kk_reminder.UtilClass.DESTROYED;
import static com.hideaki.kk_reminder.UtilClass.INT_GENERAL;
import static com.hideaki.kk_reminder.UtilClass.ITEM;
import static com.hideaki.kk_reminder.UtilClass.NOTIFICATION_ID;
import static com.hideaki.kk_reminder.UtilClass.deserialize;
import static com.hideaki.kk_reminder.UtilClass.serialize;

public class DoneReceiver extends BroadcastReceiver {

  private DBAccessor accessor = null;
  private Context context;

  @Override
  public void onReceive(Context context, Intent intent) {

    this.context = context;
    accessor = new DBAccessor(context);
    Item item = (Item)deserialize(intent.getByteArrayExtra(ITEM));

    //通知を既読する
    int notification_id = intent.getIntExtra(NOTIFICATION_ID, -1);
    NotificationManager manager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
    checkNotNull(manager);
    manager.cancel(notification_id);

    item.setDoneDate(Calendar.getInstance());
    deleteAlarm(item);
    deleteDB(item, MyDatabaseHelper.TODO_TABLE);
    insertDB(item, MyDatabaseHelper.DONE_TABLE);

    SharedPreferences intPreferences = context.getSharedPreferences(INT_GENERAL, MODE_PRIVATE);
    int created = intPreferences.getInt(CREATED, -1);
    int destroyed = intPreferences.getInt(DESTROYED, -1);
    if(created > destroyed) context.sendBroadcast(new Intent(ACTION_IN_NOTIFICATION));
  }

  public void deleteAlarm(Item item) {

    if(isAlarmSetted(item)) {
      Intent intent = new Intent(context, AlarmReceiver.class);
      PendingIntent sender = PendingIntent.getBroadcast(
          context, (int)item.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

      AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
      checkNotNull(alarmManager);

      alarmManager.cancel(sender);
      sender.cancel();
    }
  }

  public boolean isAlarmSetted(Item item) {

    Intent intent = new Intent(context, AlarmReceiver.class);
    PendingIntent sender = PendingIntent.getBroadcast(
        context, (int)item.getId(), intent, PendingIntent.FLAG_NO_CREATE);

    return sender != null;
  }

  public void insertDB(Item item, String table) {

    accessor.executeInsert(item.getId(), serialize(item), table);
  }

  public void deleteDB(Item item, String table) {

    accessor.executeDelete(item.getId(), table);
  }
}
