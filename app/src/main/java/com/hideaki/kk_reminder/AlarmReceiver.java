package com.hideaki.kk_reminder;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.hideaki.kk_reminder.UtilClass.BOOT_FROM_NOTIFICATION;
import static com.hideaki.kk_reminder.UtilClass.HOUR;
import static com.hideaki.kk_reminder.UtilClass.INT_GENERAL;
import static com.hideaki.kk_reminder.UtilClass.ITEM;
import static com.hideaki.kk_reminder.UtilClass.LOCALE;
import static com.hideaki.kk_reminder.UtilClass.MINUTE;
import static com.hideaki.kk_reminder.UtilClass.NOTIFICATION_ID;
import static com.hideaki.kk_reminder.UtilClass.SNOOZE_DEFAULT_HOUR;
import static com.hideaki.kk_reminder.UtilClass.SNOOZE_DEFAULT_MINUTE;
import static com.hideaki.kk_reminder.UtilClass.currentTimeMinutes;
import static com.hideaki.kk_reminder.UtilClass.deserialize;
import static com.hideaki.kk_reminder.UtilClass.serialize;

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
        context, (int)System.currentTimeMillis(), open_activity, PendingIntent.FLAG_UPDATE_CURRENT
    );

    NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "kk_reminder_01")
        .setContentTitle("KK Reminder")
        .setContentText(item.getDetail())
        .setSmallIcon(R.mipmap.ic_launcher)
        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
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
    SharedPreferences intPreferences = context.getSharedPreferences(INT_GENERAL, MODE_PRIVATE);
    int hour = intPreferences.getInt(SNOOZE_DEFAULT_HOUR, 0);
    int minute = intPreferences.getInt(SNOOZE_DEFAULT_MINUTE, 15);
    String summary = "";
    if(hour != 0) {
      summary += context.getResources().getQuantityString(R.plurals.hour, hour, hour);
      if(!LOCALE.equals(Locale.JAPAN)) summary += " ";
    }
    if(minute != 0) {
      summary += context.getResources().getQuantityString(R.plurals.minute, minute, minute);
      if(!LOCALE.equals(Locale.JAPAN)) summary += " ";
    }
    summary += context.getString(R.string.snooze);

    //完了
    Intent doneIntent = new Intent(context, DoneReceiver.class);
    doneIntent.putExtra(ITEM, serialize(item));
    doneIntent.putExtra(NOTIFICATION_ID, id);
    PendingIntent doneSender = PendingIntent.getBroadcast(
        context, (int)item.getId(), doneIntent, PendingIntent.FLAG_UPDATE_CURRENT
    );
    builder.addAction(R.mipmap.done, context.getString(R.string.done), doneSender);

    //デフォルトスヌーズ
    Intent defaultSnoozeIntent = new Intent(context, DefaultManuallySnoozeReceiver.class);
    defaultSnoozeIntent.putExtra(ITEM, serialize(item));
    defaultSnoozeIntent.putExtra(NOTIFICATION_ID, id);
    PendingIntent defaultSnoozeSender = PendingIntent.getBroadcast(
        context, (int)item.getId(), defaultSnoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT
    );
    builder.addAction(R.mipmap.update, summary, defaultSnoozeSender);

    //細かいスヌーズ
    Intent advancedSnoozeIntent = new Intent(context, ManuallySnoozeActivity.class);
    advancedSnoozeIntent.putExtra(ITEM, serialize(item));
    advancedSnoozeIntent.putExtra(NOTIFICATION_ID, id);
    PendingIntent snoozeSender = PendingIntent.getActivity(
        context, (int)item.getId(), advancedSnoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT
    );
    builder.addAction(R.mipmap.snooze, context.getString(R.string.more_advanced_snooze), snoozeSender);

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
      item.getNotify_interval().setTime(time - 1);
      Intent recursive_alarm = new Intent(context, AlarmReceiver.class);
      recursive_alarm.putExtra(ITEM, serialize(item));
      PendingIntent recursive_sender = PendingIntent.getBroadcast(
          context, (int)item.getId(), recursive_alarm, PendingIntent.FLAG_UPDATE_CURRENT);

      long reset_schedule = currentTimeMinutes()
          + item.getNotify_interval().getHour() * HOUR
          + item.getNotify_interval().getMinute() * MINUTE;

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
}
