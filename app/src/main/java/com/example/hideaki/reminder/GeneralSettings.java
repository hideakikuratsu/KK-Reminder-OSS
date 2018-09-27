package com.example.hideaki.reminder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class GeneralSettings implements Serializable {

  private static final long serialVersionUID = 4618925430276629981L;
  private List<NonScheduledList> nonScheduledLists = new ArrayList<>();
  private List<Tag> tagList = new ArrayList<>();
  private boolean expandable_todo = true;
  private NotifyInterval notifyInterval;
  private String soundUri;
  private int snooze_default_hour;
  private int snooze_default_minute;

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

  NotifyInterval getNotifyInterval() {
    return notifyInterval;
  }

  String getSoundUri() {
    return soundUri;
  }

  public int getSnooze_default_hour() {
    return snooze_default_hour;
  }

  public int getSnooze_default_minute() {
    return snooze_default_minute;
  }

  void setNonScheduledLists(List<NonScheduledList> nonScheduledLists) {
    this.nonScheduledLists = nonScheduledLists;
  }

  void setTagList(List<Tag> tagList) {
    this.tagList = tagList;
  }

  void setExpandable_todo(boolean expandable_todo) {
    this.expandable_todo = expandable_todo;
  }

  void setNotifyInterval(NotifyInterval notifyInterval) {
    this.notifyInterval = notifyInterval;
  }

  void setSoundUri(String soundUri) {
    this.soundUri = soundUri;
  }

  public void setSnooze_default_hour(int snooze_default_hour) {
    this.snooze_default_hour = snooze_default_hour;
  }

  public void setSnooze_default_minute(int snooze_default_minute) {
    this.snooze_default_minute = snooze_default_minute;
  }
}
