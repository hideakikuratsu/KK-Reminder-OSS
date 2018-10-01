package com.example.hideaki.reminder;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static com.example.hideaki.reminder.UtilClass.*;
import static com.google.common.base.Preconditions.checkNotNull;

public class ManuallySnoozeActivity extends AppCompatActivity implements View.OnClickListener {

  GeneralSettings generalSettings;
  private DBAccessor accessor = null;
  TextView title;
  ListView listView;
  View footer;
  private Item item;
  private static final List<String> hour_list = new ArrayList<>();
  private static final List<String> minute_list = new ArrayList<>();
  private NumberPicker hour_picker;
  private NumberPicker minute_picker;
  int custom_hour;
  int custom_minute;
  String summary;

  static {

    for(int i = 0; i < 24; i++) hour_list.add(i + "時間");
    for(int i = 0; i < 60; i++) minute_list.add(i + "分");
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.manually_snooze_layout);
    accessor = new DBAccessor(this);

    //AlarmReceiverからItemとNotificationIDを受け取る
    Intent intent = getIntent();
    item = (Item)deserialize(intent.getByteArrayExtra(ITEM));
    generalSettings = querySettingsDB();

    //通知を既読する
    int notification_id = intent.getIntExtra(NOTIFICATION_ID, -1);
    NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    checkNotNull(manager);
    manager.cancel(notification_id);

    //ListViewの設定
    ManuallySnoozeListAdapter manuallySnoozeListAdapter = new ManuallySnoozeListAdapter(this);
    listView = findViewById(R.id.listView);
    listView.setAdapter(manuallySnoozeListAdapter);

    //customレイアウトとして表示するfooterの設定
    footer = View.inflate(this, R.layout.manually_snooze_custom_layout, null);
    custom_hour = generalSettings.getSnooze_default_hour();
    custom_minute = generalSettings.getSnooze_default_minute();
    initPicker();

    //listView以外のレイアウトの設定
    ImageView backArrow = findViewById(R.id.back_arrow);
    title = findViewById(R.id.title);
    ImageView launchActivity = findViewById(R.id.launch_activity);
    ImageView done = findViewById(R.id.done);

    //タイトルの設定
    summary = "";
    if(custom_hour != 0) {
      summary += custom_hour + getString(R.string.hour);
    }
    if(custom_minute != 0) {
      summary += custom_minute + getString(R.string.minute);
    }
    summary += getString(R.string.snooze);
    title.setText(summary);

    backArrow.setOnClickListener(this);
    launchActivity.setOnClickListener(this);
    done.setOnClickListener(this);
  }

  public void setAlarm(Item item) {

    if(item.getDate().getTimeInMillis() > System.currentTimeMillis() && item.getWhich_list_belongs() == 0) {
      item.getNotify_interval().setTime(item.getNotify_interval().getOrg_time());
      Intent intent = new Intent(this, AlarmReceiver.class);
      byte[] ob_array = serialize(item);
      intent.putExtra(ITEM, ob_array);
      PendingIntent sender = PendingIntent.getBroadcast(
          this, (int)item.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

      AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
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
      Intent intent = new Intent(this, AlarmReceiver.class);
      PendingIntent sender = PendingIntent.getBroadcast(
          this, (int)item.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

      AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
      checkNotNull(alarmManager);

      alarmManager.cancel(sender);
      sender.cancel();
    }
  }

  public boolean isAlarmSetted(Item item) {

    Intent intent = new Intent(this, AlarmReceiver.class);
    PendingIntent sender = PendingIntent.getBroadcast(
        this, (int)item.getId(), intent, PendingIntent.FLAG_NO_CREATE);

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

  @Override
  public void onClick(View v) {

    switch(v.getId()) {

      case R.id.back_arrow: {

        this.finish();
        break;
      }
      case R.id.launch_activity: {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(BOOT_FROM_NOTIFICATION);
        startActivity(intent);
        break;
      }
      case R.id.done: {

        //チェックされた項目に応じた時間スヌーズする
        int checked_position = ManuallySnoozeListAdapter.checked_position;

        long default_snooze = generalSettings.getSnooze_default_hour() * HOUR
            + generalSettings.getSnooze_default_minute() * MINUTE;
        long custom_snooze = custom_hour * HOUR + custom_minute * MINUTE;
        long[] how_long = {default_snooze, 15 * MINUTE, 30 * MINUTE, HOUR, 3 * HOUR, 10 * HOUR, 24 * HOUR, custom_snooze};

        if(item.getTime_altered() == 0) {
          item.setOrg_date((Calendar)item.getDate().clone());
        }
        item.getDate().setTimeInMillis(System.currentTimeMillis() + how_long[checked_position]);
        item.addTime_altered(how_long[checked_position]);

        //更新
        deleteAlarm(item);
        setAlarm(item);
        updateDB(item, MyDatabaseHelper.TODO_TABLE);

        generalSettings.setChange_in_notification(true);
        updateSettingsDB();

        this.finish();
        break;
      }
    }
  }

  private void initPicker() {

    //hour_pickerの実装
    hour_picker = footer.findViewById(R.id.hour);
    hour_picker.setDisplayedValues(null);
    hour_picker.setMaxValue(23);
    hour_picker.setMinValue(0);
    hour_picker.setValue(custom_hour);
    hour_picker.setDisplayedValues(hour_list.toArray(new String[0]));
    hour_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
      @Override
      public void onScrollStateChange(NumberPicker view, int scrollState) {
        switch(scrollState) {
          case SCROLL_STATE_IDLE: {
            if(hour_picker.getValue() == 0 && minute_picker.getValue() == 0) {
              custom_hour = 24;
            }
            else custom_hour = hour_picker.getValue();

            summary = "";
            if(custom_hour != 0) {
              summary += custom_hour + getString(R.string.hour);
            }
            if(custom_minute != 0) {
              summary += custom_minute + getString(R.string.minute);
            }
            summary += getString(R.string.snooze);
            title.setText(summary);

            break;
          }
          case SCROLL_STATE_FLING:
          case SCROLL_STATE_TOUCH_SCROLL: {
            break;
          }
        }
      }
    });

    //minute_pickerの実装
    minute_picker = footer.findViewById(R.id.minute);
    minute_picker.setDisplayedValues(null);
    minute_picker.setMaxValue(59);
    minute_picker.setMinValue(0);
    minute_picker.setValue(custom_minute);
    minute_picker.setDisplayedValues(minute_list.toArray(new String[0]));
    minute_picker.setOnScrollListener(new NumberPicker.OnScrollListener() {
      @Override
      public void onScrollStateChange(NumberPicker view, int scrollState) {
        switch(scrollState) {
          case SCROLL_STATE_IDLE: {
            if(hour_picker.getValue() == 0 && minute_picker.getValue() == 0) {
              custom_hour = 24;
            }
            else custom_hour = hour_picker.getValue();
            custom_minute = minute_picker.getValue();

            summary = "";
            if(custom_hour != 0) {
              summary += custom_hour + getString(R.string.hour);
            }
            if(custom_minute != 0) {
              summary += custom_minute + getString(R.string.minute);
            }
            summary += getString(R.string.snooze);
            title.setText(summary);

            break;
          }
          case SCROLL_STATE_FLING:
          case SCROLL_STATE_TOUCH_SCROLL: {
            break;
          }
        }
      }
    });
  }
}