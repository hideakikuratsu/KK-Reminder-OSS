package com.example.hideaki.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.io.IOException;

public class StartupReceiver extends BroadcastReceiver {

  private DBAccessor accessor;
  private Item item;
  private byte[] ob_array;

  @Override
  public void onReceive(Context context, Intent intent) {

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      if(intent.getAction() != null && intent.getAction().equals(Intent.ACTION_LOCKED_BOOT_COMPLETED)) {
        Context direct_boot_context = context.createDeviceProtectedStorageContext();
        accessor = new DBAccessor(direct_boot_context);
        resetAlarm(context);
      }
      else if(intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
        accessor = new DBAccessor(context);
        resetAlarm(context);
      }
    }
    else {
      if(intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
        accessor = new DBAccessor(context);
        resetAlarm(context);
      }
    }
  }

  private void resetAlarm(Context context) {

    for(byte[] stream : accessor.executeQueryAll(MyDatabaseHelper.TODO_TABLE)) {

      try {
        item = (Item)MainActivity.deserialize(stream);
      } catch(IOException e) {
        e.printStackTrace();
      } catch(ClassNotFoundException e) {
        e.printStackTrace();
      }

      if(item.getDate().getTimeInMillis() > System.currentTimeMillis()) {
        Intent set_alarm = new Intent(context, AlarmReceiver.class);
        try {
          ob_array = MainActivity.serialize(item);
        } catch(IOException e) {
          e.printStackTrace();
        }
        set_alarm.putExtra(MainEditFragment.ITEM, ob_array);
        PendingIntent sender = PendingIntent.getBroadcast(
            context, (int)item.getId(), set_alarm, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          alarmManager.setAlarmClock(
              new AlarmManager.AlarmClockInfo(item.getDate().getTimeInMillis(), null), sender);
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
          alarmManager.setExact(AlarmManager.RTC_WAKEUP, item.getDate().getTimeInMillis(), sender);
        } else {
          alarmManager.set(AlarmManager.RTC_WAKEUP, item.getDate().getTimeInMillis(), sender);
        }
      }
    }
  }
}
