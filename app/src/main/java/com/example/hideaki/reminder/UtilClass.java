package com.example.hideaki.reminder;

import android.media.RingtoneManager;
import android.net.Uri;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.atomic.AtomicInteger;

class UtilClass {

  private UtilClass() {}

  private static final AtomicInteger uniqueId = new AtomicInteger(1);
  static final ScheduledItemComparator scheduledItemComparator = new ScheduledItemComparator();
  static final NonScheduledItemComparator nonScheduledItemComparator = new NonScheduledItemComparator();
  static final DoneItemComparator doneItemComparator = new DoneItemComparator();
  static final Uri DEFAULT_URI_SOUND = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
  static final String NOTIFICATION_ID = "NOTIFICATION_ID";
  static final String BOOT_FROM_NOTIFICATION = "BOOT_FROM_NOTIFICATION";
  static final String ITEM = "ITEM";
  static final String LIST = "LIST";
  static final String SAVED_DATA = "SAVED_DATA";
  static final String MENU_POSITION = "MENU_POSITION";
  static final String SUBMENU_POSITION = "SUBMENU_POSITION";
  static long MINUTE = 60 * 1000;
  static long HOUR = 60 * 60 * 1000;

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
