package com.example.hideaki.reminder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class GeneralSettings implements Serializable {

  private static final long serialVersionUID = 2969579071872650012L;
  private List<NonScheduledList> nonScheduledLists = new ArrayList<>();
  private List<Tag> tagList = new ArrayList<>();
  private boolean expandable_todo = true;
  private Item item = new Item();
  private int snooze_default_hour;
  private int snooze_default_minute;
  private boolean change_in_notification;
  private String defaultQuickPicker1;
  private String defaultQuickPicker2;
  private String defaultQuickPicker3;
  private String defaultQuickPicker4;
  private MyTheme theme = new MyTheme();

  List<NonScheduledList> getNonScheduledLists() {
    return nonScheduledLists;
  }

  List<Tag> getTagList() {
    return tagList;
  }

  Tag getTagById(long id) {

    for(Tag tag : tagList) {
      if(tag.getId() == id) return tag;
    }
    return null;
  }

  boolean isExpandable_todo() {
    return expandable_todo;
  }

  Item getItem() {
    return item;
  }

  int getSnooze_default_hour() {
    return snooze_default_hour;
  }

  int getSnooze_default_minute() {
    return snooze_default_minute;
  }

  boolean isChange_in_notification() {
    return change_in_notification;
  }

  String getDefaultQuickPicker1() {
    return defaultQuickPicker1;
  }

  String getDefaultQuickPicker2() {
    return defaultQuickPicker2;
  }

  String getDefaultQuickPicker3() {
    return defaultQuickPicker3;
  }

  String getDefaultQuickPicker4() {
    return defaultQuickPicker4;
  }

  MyTheme getTheme() {
    return theme;
  }

  void setNonScheduledLists(List<NonScheduledList> nonScheduledLists) {
    this.nonScheduledLists = nonScheduledLists;
  }

  void setNonScheduledList(NonScheduledList list) {

    long id = list.getId();
    int size = nonScheduledLists.size();
    for(int i = 0; i < size; i++) {
      NonScheduledList thisList = nonScheduledLists.get(i);
      if(thisList.getId() == id) {
        nonScheduledLists.set(i, list);
        break;
      }
    }
  }

  void setTagList(List<Tag> tagList) {
    this.tagList = tagList;
  }

  void setExpandable_todo(boolean expandable_todo) {
    this.expandable_todo = expandable_todo;
  }

  void setItem(Item item) {
    this.item = item;
  }

  void setSnooze_default_hour(int snooze_default_hour) {
    this.snooze_default_hour = snooze_default_hour;
  }

  void setSnooze_default_minute(int snooze_default_minute) {
    this.snooze_default_minute = snooze_default_minute;
  }

  void setChange_in_notification(boolean change_in_notification) {
    this.change_in_notification = change_in_notification;
  }

  void setDefaultQuickPicker1(String defaultQuickPicker1) {
    this.defaultQuickPicker1 = defaultQuickPicker1;
  }

  void setDefaultQuickPicker2(String defaultQuickPicker2) {
    this.defaultQuickPicker2 = defaultQuickPicker2;
  }

  void setDefaultQuickPicker3(String defaultQuickPicker3) {
    this.defaultQuickPicker3 = defaultQuickPicker3;
  }

  void setDefaultQuickPicker4(String defaultQuickPicker4) {
    this.defaultQuickPicker4 = defaultQuickPicker4;
  }

  void setTheme(MyTheme theme) {
    this.theme = theme;
  }
}
