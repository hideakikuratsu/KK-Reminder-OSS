package com.example.hideaki.reminder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ExpandableListView elv = findViewById(R.id.listView);
    elv.setAdapter(new ExpandableListAdapter(createGroups(), createChildren(), this));
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