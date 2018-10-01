package com.example.hideaki.reminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import static com.example.hideaki.reminder.UtilClass.BOOT_FROM_NOTIFICATION;
import static com.example.hideaki.reminder.UtilClass.ITEM;
import static com.example.hideaki.reminder.UtilClass.NOTIFICATION_ID;
import static com.example.hideaki.reminder.UtilClass.deserialize;
import static com.example.hideaki.reminder.UtilClass.serialize;
import static com.google.common.base.Preconditions.checkNotNull;

public class AlarmReceiver extends BroadcastReceiver {

  DBAccessor accessor = null;

  @Override
  public void onReceive(Context context, Intent intent) {

    accessor = new DBAccessor(context);
    Item item = (Item)deserialize(intent.getByteArrayExtra(ITEM));

    int time = item.getNotify_interval().getTime();
    int id;
    if(time > 0) id = (int)item.getId() * (time + 2);
    else if(time < 0) id = (int)(-(item.getId() * (time - 2)));
    else id = (int)item.getId();

    Intent open_activity = new Intent(context, MainActivity.class);
    open_activity.setAction(BOOT_FROM_NOTIFICATION);
    PendingIntent sender = PendingIntent.getActivity(
        context, 0, open_activity, PendingIntent.FLAG_UPDATE_CURRENT
    );

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reminder_01")
        .setContentTitle("Reminder")
        .setContentText(item.getDetail())
        .setSmallIcon(R.mipmap.ic_launcher_round)
        .setWhen(System.currentTimeMillis())
        .setContentIntent(sender)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setLights(Color.GREEN, 2000, 1000)
        .setVibrate(new long[]{0, 500});

    //通知音の設定
    String uriString = item.getSoundUri();
    if(uriString != null) {
      builder.setSound(Uri.parse(uriString));
    }

    //手動スヌーズ用のIntentの設定

    //デフォルトスヌーズ
    GeneralSettings generalSettings = querySettingsDB();
    int hour = generalSettings.getSnooze_default_hour();
    int minute = generalSettings.getSnooze_default_minute();
    String summary = "";
    if(hour != 0) {
      summary += hour + context.getString(R.string.hour);
    }
    if(minute != 0) {
      summary += minute + context.getString(R.string.minute);
    }
    summary += context.getString(R.string.snooze);

    Intent defaultSnoozeIntent = new Intent(context, DefaultManuallySnoozeReceiver.class);
    defaultSnoozeIntent.putExtra(ITEM, serialize(item));
    defaultSnoozeIntent.putExtra(NOTIFICATION_ID, id);
    PendingIntent defaultSnoozeSender = PendingIntent.getBroadcast(
        context, (int)item.getId(), defaultSnoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT
    );
    builder.addAction(R.drawable.ic_update_24dp, summary, defaultSnoozeSender);

    //細かいスヌーズ
    Intent advancedSnoozeIntent = new Intent(context, ManuallySnoozeActivity.class);
    advancedSnoozeIntent.putExtra(ITEM, serialize(item));
    advancedSnoozeIntent.putExtra(NOTIFICATION_ID, id);
    PendingIntent snoozeSender = PendingIntent.getActivity(
        context, (int)item.getId(), advancedSnoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT
    );
    builder.addAction(R.drawable.ic_snooze_24dp, context.getString(R.string.more_advanced_snooze), snoozeSender);

    //通知を発行
    NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    checkNotNull(manager);
    manager.notify(id, builder.build());

    //ロックされている場合、画面を点ける
    PowerManager powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
    checkNotNull(powerManager);
    @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
        PowerManager.FULL_WAKE_LOCK |
            PowerManager.ACQUIRE_CAUSES_WAKEUP |
            PowerManager.ON_AFTER_RELEASE, "Notification");
    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
      if(!powerManager.isScreenOn()) {
        wakeLock.acquire(10000);
      }
    }
    else {
      if(!powerManager.isInteractive()) {
        wakeLock.acquire(10000);
      }
    }

    //再帰通知処理
    if(time != 0) {
      item.getNotify_interval().setTime(--time);
      Intent recursive_alarm = new Intent(context, AlarmReceiver.class);
      recursive_alarm.putExtra(ITEM, serialize(item));
      PendingIntent recursive_sender = PendingIntent.getBroadcast(
          context, (int)item.getId(), recursive_alarm, PendingIntent.FLAG_UPDATE_CURRENT);

      long reset_schedule = System.currentTimeMillis()
          + item.getNotify_interval().getHour() * 60 * 60 * 1000
          + item.getNotify_interval().getMinute() * 60 * 1000;

      AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
      checkNotNull(alarmManager);

      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        alarmManager.setAlarmClock(
            new AlarmManager.AlarmClockInfo(reset_schedule, null), recursive_sender);
      }
      else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reset_schedule, recursive_sender);
      }
      else {
        alarmManager.set(AlarmManager.RTC_WAKEUP, reset_schedule, recursive_sender);
      }

      updateDB(item, MyDatabaseHelper.TODO_TABLE);
    }
  }

  public void updateDB(Item item, String table) {
    accessor.executeUpdate(item.getId(), serialize(item), table);
  }

  public GeneralSettings querySettingsDB() {

    return (GeneralSettings)deserialize(accessor.executeQueryById(1, MyDatabaseHelper.SETTINGS_TABLE));
  }
}
