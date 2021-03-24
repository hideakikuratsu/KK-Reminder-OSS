package com.hideaki.kk_reminder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class GeneralSettings3 implements Serializable {

  private static final long serialVersionUID = 2775810413830040755L;
  private List<NonScheduledList2> nonScheduledLists = new ArrayList<>();
  private List<Tag2> tagList = new ArrayList<>();
  private Item3 item = new Item3();
  private MyTheme2 theme = new MyTheme2();

  List<NonScheduledList2> getNonScheduledLists() {

    return nonScheduledLists;
  }

  List<Tag2> getTagList() {

    return tagList;
  }

  Item3 getItem() {

    return item;
  }

  MyTheme2 getTheme() {

    return theme;
  }

  void setNonScheduledLists(List<NonScheduledList2> nonScheduledLists) {

    this.nonScheduledLists = nonScheduledLists;
  }

  void setTagList(List<Tag2> tagList) {

    this.tagList = tagList;
  }

  void setItem(Item3 item) {

    this.item = item;
  }

  void setTheme(MyTheme2 theme) {

    this.theme = theme;
  }
}
