package com.example.hideaki.reminder;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private DatabaseHelper helper = null;
  public static ExpandableListView elv = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    helper = new DatabaseHelper(this);
    SQLiteDatabase db = helper.getWritableDatabase();

    elv = findViewById(R.id.listView);
    elv.setAdapter(new ExpandableListAdapter(createGroups(), createChildren(), this));

    elv.setTextFilterEnabled(true);

    FragmentManager manager = getSupportFragmentManager();
    Fragment fragment = manager.findFragmentByTag("SearchFragment");
    if(fragment == null) {
      fragment = new SearchFragment();
      FragmentTransaction transaction = manager.beginTransaction();
      transaction.add(R.id.content, fragment, "SearchFragment");
      transaction.commit();
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    helper.close();
  }

  private List<List<Item>> createChildren() {
    List<Item> child1 = new ArrayList<>();
    child1.add(new Item("テスト1", new Date(), "毎日"));
    child1.add(new Item("テスト2", new Date(), "毎日"));
    child1.add(new Item("テスト3", new Date(), "毎日"));

    List<Item> child2 = new ArrayList<>();
    child2.add(new Item("テスト1", new Date(), "毎日"));
    child2.add(new Item("テスト2", new Date(), "毎日"));
    child2.add(new Item("テスト3", new Date(), "毎日"));

    List<List<Item>> child = new ArrayList<>();
    child.add(child1);
    child.add(child2);

    return child;
  }

  private List<String> createGroups() {
    List<String> groups = new ArrayList<>();
    groups.add("今日");
    groups.add("明日");

    return groups;
  }
}