package com.example.hideaki.reminder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ActionBarFragment.OnFragmentInteractionListener{

  public static ExpandableListView elv = null;
  private static DBAccessor accessor = null;
  private FragmentManager manager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    accessor = new DBAccessor(this);

    elv = findViewById(R.id.listView);
    elv.setAdapter(new MyExpandableListAdapter(createGroups(), createChildren(), this));

    elv.setTextFilterEnabled(true);

    showActionBar();
  }

  //受け取ったオブジェクトをシリアライズしてデータベースへ挿入
  public void insertDB(long id, Object data, String table) throws IOException {
    byte[] stream = null;

    stream = serialize(data);
    accessor.executeInsert(id, stream, table);
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
  private Object deserialize(byte[] stream) throws IOException, ClassNotFoundException {
    ByteArrayInputStream bais = new ByteArrayInputStream(stream);
    ObjectInputStream ois = new ObjectInputStream(bais);

    Object data = ois.readObject();
    ois.close();

    return data;
  }

  public static List<List<Item>> createChildren() {
    List<Item> child1 = new ArrayList<>();
    child1.add(new Item("テスト1", new Date(), "毎日"));
    child1.add(new Item("テスト2", new Date(), "毎日"));
    child1.add(new Item("テスト3", new Date(), "毎日"));

    List<Item> child2 = new ArrayList<>();
    child2.add(new Item("テスト4", new Date(), "毎日"));
    child2.add(new Item("テスト5", new Date(), "毎日"));
    child2.add(new Item("テスト6", new Date(), "毎日"));

    List<List<Item>> child = new ArrayList<>();
    child.add(child1);
    child.add(child2);

    return child;
  }

  public List<String> createGroups() {
    List<String> groups = new ArrayList<>();
    groups.add("今日");
    groups.add("明日");

    return groups;
  }

  private void showActionBar() {
    manager = getSupportFragmentManager();
    Fragment fragment = manager.findFragmentByTag("ActionBarFragment");
    if(fragment == null) {
      fragment = new ActionBarFragment();
      FragmentTransaction transaction = manager.beginTransaction();
      transaction.add(R.id.content, fragment, "ActionBarFragment");
      transaction.commit();
    }
  }

  public void showEditFragment() {
    android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
    transaction.replace(R.id.content, new EditFragment());
    transaction.commit();
  }
}