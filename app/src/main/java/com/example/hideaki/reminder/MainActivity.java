package com.example.hideaki.reminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity
    extends AppCompatActivity
    implements ActionBarFragment.OnFragmentInteractionListener, MainEditFragment.OnFragmentInteractionListener,
    MyExpandableListAdapter.OnFragmentInteractionListener {

  private byte[] ob_array;
  private Timer timer = new Timer();
  private TimerTask timerTask = new UpdateList();
  private Handler handler = new Handler();
  private ExpandableListView expandableListView;
  private DBAccessor accessor = null;
  private MyExpandableListAdapter expandableListAdapter;
  private Intent intent;
  private PendingIntent sender;
  private AlarmManager alarmManager;
  private int group_changed; //groupの変化があったかどうかのフラグをビットで表す
  private int group_changed_num; //groupの変化があったchildの個数を保持する
  private final MyComparator comparator = new MyComparator();

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    accessor = new DBAccessor(this);

    try {
      expandableListAdapter = new MyExpandableListAdapter(getChildren(MyDatabaseHelper.TODO_TABLE), this);
    } catch(IOException e) {
      e.printStackTrace();
    } catch(ClassNotFoundException e) {
      e.printStackTrace();
    }

    expandableListView = findViewById(R.id.expandable_list);
    expandableListView.setAdapter(expandableListAdapter);
    expandableListView.setTextFilterEnabled(true);

    showActionBar();
    timer.schedule(timerTask, 0, 1000);
  }

  private class UpdateList extends TimerTask {

    @Override
    public void run() {
      handler.post(new Runnable() {
        @Override
        public void run() {

          group_changed_num = 0;
          for(int group_count = 0; group_count < MyExpandableListAdapter.groups.size(); group_count++) {

            group_changed = 0;
            List<Item> itemList = MyExpandableListAdapter.children.get(group_count);

            for(int child_count = 0; child_count < itemList.size() - group_changed_num; child_count++) {

              Item item = itemList.get(child_count);
              Calendar now = Calendar.getInstance();
              Calendar tomorrow = (Calendar)now.clone();
              tomorrow.add(Calendar.DAY_OF_MONTH, 1);

              int spec_day = item.getDate().get(Calendar.DAY_OF_MONTH);
              long sub_time = item.getDate().getTimeInMillis() - now.getTimeInMillis();
              long sub_day = sub_time / (1000 * 60 * 60 * 24);

              if(sub_time < 0) {
                if(group_count != 0) {
                  MyExpandableListAdapter.children.get(0).add(item);
                  group_changed |= (1 << child_count);
                }
              }
              else if(sub_day < 1 && spec_day == now.get(Calendar.DAY_OF_MONTH)) {
                if(group_count != 1) {
                  MyExpandableListAdapter.children.get(1).add(item);
                  group_changed |= (1 << child_count);
                }
              }
              else if(sub_day < 2 && spec_day == tomorrow.get(Calendar.DAY_OF_MONTH)) {
                if(group_count != 2) {
                  MyExpandableListAdapter.children.get(2).add(item);
                  group_changed |= (1 << child_count);
                }
              }
              else if(sub_day < 8) {
                if(group_count != 3) {
                  MyExpandableListAdapter.children.get(3).add(item);
                  group_changed |= (1 << child_count);
                }
              }
              else {
                if(group_count != 4) {
                  MyExpandableListAdapter.children.get(4).add(item);
                  group_changed |= (1 << child_count);
                }
              }
            }

            group_changed_num = 0;
            for(int i = 0; i <= group_changed; i++) {
              if((group_changed & (1 << i)) != 0) {
                MyExpandableListAdapter.children.get(group_count).remove(i);
                group_changed_num++;
              }
            }
          }

          for(List<Item> itemList : MyExpandableListAdapter.children) {
            Collections.sort(itemList, comparator);
          }
          expandableListAdapter.notifyDataSetChanged();
        }
      });
    }
  }

  private class MyComparator implements Comparator<Item> {

    @Override
    public int compare(Item o1, Item o2) {
      if(o1.getDate().getTimeInMillis() < o2.getDate().getTimeInMillis()) {
        return -1;
      }
      else if(o1.getDate().getTimeInMillis() == o2.getDate().getTimeInMillis()) {
        return 0;
      }
      else return 1;
    }
  }

  public void setAlarm(Item item) {

    if(item.getDate().getTimeInMillis() > System.currentTimeMillis()) {
      item.getNotify_interval().setTime(item.getNotify_interval().getOrg_time());
      intent = new Intent(this, AlarmReceiver.class);
      try {
        ob_array = serialize(item);
      } catch(IOException e) {
        e.printStackTrace();
      }
      intent.putExtra(MainEditFragment.ITEM, ob_array);
      sender = PendingIntent.getBroadcast(
          this, (int)item.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

      alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
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

  public void deleteAlarm(Item item) {

    if(isAlarmSetted(item)) {
      intent = new Intent(this, AlarmReceiver.class);
      sender = PendingIntent.getBroadcast(
          this, (int)item.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
      alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

      alarmManager.cancel(sender);
      sender.cancel();
    }
  }

  public boolean isAlarmSetted(Item item) {

    intent = new Intent(this, AlarmReceiver.class);
    sender = PendingIntent.getBroadcast(
        this, (int)item.getId(), intent, PendingIntent.FLAG_NO_CREATE);

    if(sender == null) return false;
    else return true;
  }

  private List<List<Item>> getChildren(String table) throws IOException, ClassNotFoundException {

    List<Item> past_list = new ArrayList<>();
    List<Item> today_list = new ArrayList<>();
    List<Item> tomorrow_list = new ArrayList<>();
    List<Item> week_list = new ArrayList<>();
    List<Item> future_list = new ArrayList<>();

    Calendar now = Calendar.getInstance();
    Calendar tomorrow = (Calendar)now.clone();
    tomorrow.add(Calendar.DAY_OF_MONTH, 1);

    for(Item item : queryAllDB(table)) {

      if(!isAlarmSetted(item) && !item.isAlarm_stopped()) {
        setAlarm(item);
      }

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

    List<List<Item>> children = new ArrayList<>();
    children.add(past_list);
    children.add(today_list);
    children.add(tomorrow_list);
    children.add(week_list);
    children.add(future_list);

    for(List<Item> itemList : children) {
      Collections.sort(itemList, comparator);
    }
    return children;
  }

  public void addChildren(Item item) {

    Calendar now = Calendar.getInstance();
    Calendar tomorrow = (Calendar)now.clone();
    tomorrow.add(Calendar.DAY_OF_MONTH, 1);

    int spec_day = item.getDate().get(Calendar.DAY_OF_MONTH);
    long sub_time = item.getDate().getTimeInMillis() - now.getTimeInMillis();
    long sub_day = sub_time / (1000 * 60 * 60 * 24);

    if(sub_time < 0) {
      MyExpandableListAdapter.children.get(0).add(item);
    }
    else if(sub_day < 1 && spec_day == now.get(Calendar.DAY_OF_MONTH)) {
      MyExpandableListAdapter.children.get(1).add(item);
    }
    else if(sub_day < 2 && spec_day == tomorrow.get(Calendar.DAY_OF_MONTH)) {
      MyExpandableListAdapter.children.get(2).add(item);
    }
    else if(sub_day < 8) {
      MyExpandableListAdapter.children.get(3).add(item);
    }
    else {
      MyExpandableListAdapter.children.get(4).add(item);
    }
  }

  //受け取ったオブジェクトをシリアライズしてデータベースへ挿入
  public void insertDB(Item item, String table) throws IOException {

    accessor.executeInsert(item.getId(), serialize(item), table);
  }

  public void updateDB(Item item, String table) throws IOException {

    accessor.executeUpdate(item.getId(), serialize(item), table);
  }

  public void deleteDB(Item item, String table) throws IOException {

    accessor.executeDelete(item.getId(), table);
  }

  //指定されたテーブルからオブジェクトのバイト列をすべて取り出し、デシリアライズしてオブジェクトのリストで返す。
  public List<Item> queryAllDB(String table) throws IOException, ClassNotFoundException {

    List<Item> itemList = new ArrayList<>();

    for(byte[] stream : accessor.executeQueryAll(table)) {
      itemList.add((Item)deserialize(stream));
    }

    return itemList;
  }

  public boolean isItemExists(Item item, String table) {

    if(accessor.executeQueryById(item.getId(), table) != null) {
      return true;
    }
    else return false;
  }

  //シリアライズメソッド
  public static byte[] serialize(Object data) throws IOException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);

    oos.writeObject(data);
    oos.flush();
    oos.close();
    return baos.toByteArray();
  }

  //デシリアライズメソッド
  public static Object deserialize(byte[] stream) throws IOException, ClassNotFoundException {

    ByteArrayInputStream bais = new ByteArrayInputStream(stream);
    ObjectInputStream ois = new ObjectInputStream(bais);

    Object data = ois.readObject();
    ois.close();

    return data;
  }

  //メイン画面のアクションバーを表示
  private void showActionBar() {
    getFragmentManager()
        .beginTransaction()
        .replace(android.R.id.content, ActionBarFragment.newInstance())
        .commit();
  }

  //編集画面を表示(引数にitemを渡すとそのitemの情報が入力された状態で表示)
  public void showMainEditFragment() {
    getFragmentManager()
        .beginTransaction()
        .replace(android.R.id.content, MainEditFragment.newInstance())
        .addToBackStack(null)
        .commit();
  }

  public void showMainEditFragment(Item item) {
    getFragmentManager()
        .beginTransaction()
        .replace(android.R.id.content, MainEditFragment.newInstance(item))
        .addToBackStack(null)
        .commit();
  }

  public void showNotesFragment(Item item) {
    getFragmentManager()
        .beginTransaction()
        .replace(android.R.id.content, NotesFragment.newInstance(item))
        .addToBackStack(null)
        .commit();
  }

  public void showDateAlterDialogFragment() {

    DialogFragment dialog = new DateAlterDialogFragment();
    dialog.show(getSupportFragmentManager(), "dialog");
  }

  public void notifyDataSetChanged() {
    expandableListAdapter.notifyDataSetChanged();
  }

  public void clearTextFilter() {
    expandableListView.clearTextFilter();
  }

  public void setFilterText(String text) {
    expandableListView.setFilterText(text);
  }
}