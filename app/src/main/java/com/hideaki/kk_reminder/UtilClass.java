package com.hideaki.kk_reminder;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

class UtilClass {

  private UtilClass() {}

  private static final AtomicInteger uniqueId = new AtomicInteger(1);
  static final ScheduledItemComparator SCHEDULED_ITEM_COMPARATOR = new ScheduledItemComparator();
  static final NonScheduledItemComparator NON_SCHEDULED_ITEM_COMPARATOR = new NonScheduledItemComparator();
  static final DoneItemComparator DONE_ITEM_COMPARATOR = new DoneItemComparator();
  static final NotesComparator NOTES_COMPARATOR = new NotesComparator();
  static final Uri DEFAULT_URI_SOUND = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
  static final String NOTIFICATION_ID = "NOTIFICATION_ID";
  static final String SNOOZE = "SNOOZE";
  static final String BOOT_FROM_NOTIFICATION = "BOOT_FROM_NOTIFICATION";
  static final int REQUEST_CODE_RINGTONE_PICKER = 0;
  static final String ITEM = "ITEM";
  static final String LIST = "LIST";
  static final String INT_GENERAL = "INT_GENERAL";
  static final String SNOOZE_DEFAULT_HOUR = "SNOOZE_DEFAULT_HOUR";
  static final String SNOOZE_DEFAULT_MINUTE = "SNOOZE_DEFAULT_MINUTE";
  static final String MENU_POSITION = "MENU_POSITION";
  static final String SUBMENU_POSITION = "SUBMENU_POSITION";
  static final String CREATED = "CREATED";
  static final String DESTROYED = "DESTROYED";
//  static final String RESUMED = "RESUMED";
//  static final String PAUSED = "PAUSED";
//  static final String STARTED = "STARTED";
//  static final String STOPPED = "STOPPED";
  static final String BOOLEAN_GENERAL = "BOOLEAN_GENERAL";
  static final String IS_EXPANDABLE_TODO = "IS_EXPANDABLE_TODO";
  static final String IS_PREMIUM = "IS_PREMIUM";
  static final String STRING_GENERAL = "STRING_GENERAL";
  static final String DEFAULT_QUICK_PICKER1 = "DEFAULT_QUICK_PICKER1";
  static final String DEFAULT_QUICK_PICKER2 = "DEFAULT_QUICK_PICKER2";
  static final String DEFAULT_QUICK_PICKER3 = "DEFAULT_QUICK_PICKER3";
  static final String DEFAULT_QUICK_PICKER4 = "DEFAULT_QUICK_PICKER4";
  static final String LINE_SEPARATOR = System.getProperty("line.separator");
  static final String PRODUCT_ID_PREMIUM = "com.hideaki.premium";
  static final int RC_SIGN_IN = 1;
  static long MINUTE = 60 * 1000;
  static long HOUR = 60 * 60 * 1000;

  static long currentTimeMinutes() {

    Calendar now = Calendar.getInstance();
    if(now.get(Calendar.SECOND) < 30) {
      now.set(Calendar.SECOND, 0);
      now.set(Calendar.MILLISECOND, 0);
    }
    else {
      now.add(Calendar.MINUTE, 1);
      now.set(Calendar.SECOND, 0);
      now.set(Calendar.MILLISECOND, 0);
    }

    return now.getTimeInMillis();
  }

  static int getPxFromDp(Context context, int dp) {

    float scale = context.getResources().getDisplayMetrics().density; //画面のdensityを指定
    return (int)(dp * scale + 0.5f);
  }

  //int型のユニークIDを取得するメソッド
  static int generateUniqueId() {

    for(;;) {
      final int result = uniqueId.get();
      int new_value = result + 1;
      if(new_value == Integer.MAX_VALUE) new_value = 1;
      if(uniqueId.compareAndSet(result, new_value)) {
        return result;
      }
    }
  }

  //シリアライズメソッド
  static byte[] serialize(Object data) {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(data);
      oos.flush();
      oos.close();
    }
    catch(IOException e) {
      e.printStackTrace();
    }

    return baos.toByteArray();
  }

  //デシリアライズメソッド
  static Object deserialize(byte[] stream) {

    if(stream == null) return null;
    else {
      ByteArrayInputStream bais = new ByteArrayInputStream(stream);
      Object data = null;
      try {
        ObjectInputStream ois = new ObjectInputStream(bais);
        data = ois.readObject();
        ois.close();
      } catch(IOException e) {
        e.printStackTrace();
      } catch(ClassNotFoundException e) {
        e.printStackTrace();
      }

      return data;
    }
  }
}