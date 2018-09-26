package com.example.hideaki.reminder;

import android.media.RingtoneManager;
import android.net.Uri;

public class UtilClass {

  private UtilClass() {}

  public static final Uri DEFAULT_URI_SOUND = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
}
