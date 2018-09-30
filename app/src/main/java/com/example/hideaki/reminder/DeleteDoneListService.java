package com.example.hideaki.reminder;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import static com.example.hideaki.reminder.UtilClass.ITEM;
import static com.example.hideaki.reminder.UtilClass.deserialize;
import static com.google.common.base.Preconditions.checkNotNull;

public class DeleteDoneListService extends IntentService {

  MainActivity activity;

  public DeleteDoneListService() {

    super(DeleteDoneListService.class.getSimpleName());
    activity = (MainActivity)getApplicationContext();
  }

  public DeleteDoneListService(String name) {

    super(name);
    activity = (MainActivity)getApplicationContext();
  }

  @Override
  protected void onHandleIntent(@Nullable Intent intent) {

    checkNotNull(intent);
    Item item = (Item)deserialize(intent.getByteArrayExtra(ITEM));
    activity.deleteDB(item, MyDatabaseHelper.DONE_TABLE);
  }
}
