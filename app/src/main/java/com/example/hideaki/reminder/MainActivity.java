package com.example.hideaki.reminder;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActionBarFragment.OnFragmentInteractionListener,
  MainEditFragment.OnFragmentInteractionListener {

  public static ExpandableListView elv = null;
  private static DBAccessor accessor = null;
  private FragmentManager manager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    accessor = new DBAccessor(this);

    elv = findViewById(R.id.expandable_list);

    try {
      elv.setAdapter(new MyExpandableListAdapter(getGroups(), getChildren(MyDatabaseHelper.TODO_TABLE), this));
    } catch(IOException e) {
      e.printStackTrace();
    } catch(ClassNotFoundException e) {
      e.printStackTrace();
    }

    elv.setTextFilterEnabled(true);

    showActionBar();
  }

  public static List<String> getGroups() {

    List<String> groups = new ArrayList<>();
    groups.add("過去");
    groups.add("今日");
    groups.add("明日");
    groups.add("一週間");
    groups.add("一週間以上");

    return groups;
  }

  public static List<List<Item>> getChildren(String table) throws IOException, ClassNotFoundException {

    List<Item> past_list = new ArrayList<>();
    List<Item> today_list = new ArrayList<>();
    List<Item> tomorrow_list = new ArrayList<>();
    List<Item> week_list = new ArrayList<>();
    List<Item> future_list = new ArrayList<>();

    Calendar cal = Calendar.getInstance();
    Calendar now = Calendar.getInstance();
    Calendar tomorrow = (Calendar)now.clone();

    tomorrow.add(Calendar.DAY_OF_MONTH, 1);

    for(Item item : queryAllDB(table)) {

      Date date = item.getDate();
      cal.setTime(date);
      int spec_day = cal.get(Calendar.DAY_OF_MONTH);
      long sub_time = date.getTime() - now.getTimeInMillis();
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

    List<List<Item>> child = new ArrayList<>();
    child.add(past_list);
    child.add(today_list);
    child.add(tomorrow_list);
    child.add(week_list);
    child.add(future_list);

    return child;
  }

  //受け取ったオブジェクトをシリアライズしてデータベースへ挿入
  public void insertDB(long id, Object data, String table) throws IOException {
    byte[] stream = null;

    stream = serialize(data);
    accessor.executeInsert(id, stream, table);
  }

  //指定されたテーブルからオブジェクトのバイト列をすべて取り出し、デシリアライズしてオブジェクトのリストで返す。
  public static List<Item> queryAllDB(String table) throws IOException, ClassNotFoundException {
    List<Item> itemList = new ArrayList<>();

    for(byte[] stream : accessor.executeQueryAll(table)) {
      itemList.add((Item)deserialize(stream));
    }

    return itemList;
  }

  //シリアライズメソッド
  private byte[] serialize(Object data) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);

    oos.writeObject(data);
    oos.flush();
    oos.close();
    return baos.toByteArray();
  }

  //デシリアライズメソッド
  private static Object deserialize(byte[] stream) throws IOException, ClassNotFoundException {
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
  public void showEditFragment() {
    getFragmentManager()
        .beginTransaction()
        .replace(android.R.id.content, MainEditFragment.newInstance())
        .addToBackStack(null)
        .commit();
  }

  public void showEditFragment(Item item) {
    getFragmentManager()
        .beginTransaction()
        .replace(android.R.id.content, MainEditFragment.newInstance(item))
        .addToBackStack(null)
        .commit();
  }
}