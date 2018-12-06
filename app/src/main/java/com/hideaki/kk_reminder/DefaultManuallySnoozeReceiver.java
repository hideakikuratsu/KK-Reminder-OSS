package com.hideaki.kk_reminder;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_SNOOZE;
import static com.hideaki.kk_reminder.UtilClass.HOUR;
import static com.hideaki.kk_reminder.UtilClass.ITEM;
import static com.hideaki.kk_reminder.UtilClass.LIFECYCLE_COUNT;
import static com.hideaki.kk_reminder.UtilClass.MINUTE;
import static com.hideaki.kk_reminder.UtilClass.NOTIFICATION_ID;
import static com.hideaki.kk_reminder.UtilClass.SCHEDULED_ITEM_COMPARATOR;
import static com.hideaki.kk_reminder.UtilClass.STARTED;
import static com.hideaki.kk_reminder.UtilClass.STOPPED;
import static com.hideaki.kk_reminder.UtilClass.deserialize;
import static com.hideaki.kk_reminder.UtilClass.serialize;

public class DefaultManuallySnoozeReceiver extends BroadcastReceiver {

  private DBAccessor accessor = null;
  private Context context;
  private GeneralSettings generalSettings;

  @Override
  public void onReceive(Context context, Intent intent) {

    this.context = context;
    accessor = new DBAccessor(context);
    Item item = (Item)deserialize(intent.getByteArrayExtra(ITEM));
    generalSettings = querySettingsDB();

    //通知を既読する
    int notification_id = intent.getIntExtra(NOTIFICATION_ID, -1);
    NotificationManager manager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
    checkNotNull(manager);
    manager.cancel(notification_id);

    long default_snooze = generalSettings.getSnooze_default_hour() * HOUR
        + generalSettings.getSnooze_default_minute() * MINUTE;

    if(item.getTime_altered() == 0) {
      item.setOrg_date((Calendar)item.getDate().clone());
    }
    item.getDate().setTimeInMillis(System.currentTimeMillis() + default_snooze);
    item.addTime_altered(default_snooze);

    //更新
    deleteAlarm(item);
    setAlarm(item);
    updateDB(item, MyDatabaseHelper.TODO_TABLE);

    SharedPreferences preferences = context.getSharedPreferences(LIFECYCLE_COUNT, MODE_PRIVATE);
    int started = preferences.getInt(STARTED, -1);
    int stopped = preferences.getInt(STOPPED, -1);
    if(started > stopped) {
      context.sendBroadcast(new Intent(DEFAULT_SNOOZE));
    }
    else {
      generalSettings.setChange_in_notification(true);
      updateSettingsDB();
    }
  }

  public void setAlarm(Item item) {

    if(item.getDate().getTimeInMillis() > System.currentTimeMillis() && item.getWhich_list_belongs() == 0) {
      item.getNotify_interval().setTime(item.getNotify_interval().getOrg_time());
      Intent intent = new Intent(context, AlarmReceiver.class);
      byte[] ob_array = serialize(item);
      intent.putExtra(ITEM, ob_array);
      PendingIntent sender = PendingIntent.getBroadcast(
          context, (int)item.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

      AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
      checkNotNull(alarmManager);

      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        alarmManager.setAlarmClock(
            new AlarmManager.AlarmClockInfo(item.getDate().getTimeInMillis(), null), sender);
      }
      else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, item.getDate().getTimeInMillis(), sender);
      }
      else {
        alarmManager.set(AlarmManager.RTC_WAKEUP, item.getDate().getTimeInMillis(), sender);
      }
    }
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

  public List<List<Item>> getChildren(String table) {

    List<Item> past_list = new ArrayList<>();
    List<Item> today_list = new ArrayList<>();
    List<Item> tomorrow_list = new ArrayList<>();
    List<Item> week_list = new ArrayList<>();
    List<Item> future_list = new ArrayList<>();

    Calendar now = Calendar.getInstance();
    Calendar tomorrow = (Calendar)now.clone();
    tomorrow.add(Calendar.DAY_OF_MONTH, 1);

    for(Item item : queryAllDB(table)) {

      if(item.getWhich_list_belongs() == 0) {
        deleteAlarm(item);
        if(!item.isAlarm_stopped()) setAlarm(item);

        int spec_day = item.getDate().get(Calendar.DAY_OF_MONTH);
        long sub_time = item.getDate().getTimeInMillis() - now.getTimeInMillis();
        long sub_day = sub_time / (1000 * 60 * 60 * 24);

        if(sub_time < 0) {
          past_list.add(item);
        }
        else if(sub_day < 1 && spec_day == now.get(Calendar.DAY_OF_MONTH)) {
          today_list.add(item);
        }
        else if(sub_day < 2 && spec_day == tomorrow.get(Calendar.DAY_OF_MONTH)) {
          tomorrow_list.add(item);
        }
        else if(sub_day < 8) {
          week_list.add(item);
        }
        else {
          future_list.add(item);
        }
      }
    }

    List<List<Item>> children = new ArrayList<>();
    children.add(past_list);
    children.add(today_list);
    children.add(tomorrow_list);
    children.add(week_list);
    children.add(future_list);

    for(List<Item> itemList : children) {
      Collections.sort(itemList, SCHEDULED_ITEM_COMPARATOR);
    }
    return children;
  }

  public void updateDB(Item item, String table) {

    accessor.executeUpdate(item.getId(), serialize(item), table);
  }

  //指定されたテーブルからオブジェクトのバイト列をすべて取り出し、デシリアライズしてオブジェクトのリストで返す。
  public List<Item> queryAllDB(String table) {

    List<Item> itemList = new ArrayList<>();

    for(byte[] stream : accessor.executeQueryAll(table)) {
      itemList.add((Item)deserialize(stream));
    }

    return itemList;
  }

  public GeneralSettings querySettingsDB() {

    return (GeneralSettings)deserialize(accessor.executeQueryById(1, MyDatabaseHelper.SETTINGS_TABLE));
  }

  public void updateSettingsDB() {

    accessor.executeUpdate(1, serialize(generalSettings), MyDatabaseHelper.SETTINGS_TABLE);
  }
}
