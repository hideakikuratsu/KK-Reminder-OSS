package com.example.hideaki.reminder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GeneralSettings implements Serializable {

  private static final long serialVersionUID = 7787440574803207906L;
  private List<NonScheduledList> nonScheduledLists = new ArrayList<>();
  private List<Tag> tagList = new ArrayList<>();
  private boolean expandable_todo = true;
  private NotifyInterval notifyInterval;
  private String soundUri;

  public List<NonScheduledList> getNonScheduledLists() {
    return nonScheduledLists;
  }

  public List<Tag> getTagList() {
    return tagList;
  }

  public Tag getTagById(long id) {

    for(Tag tag : tagList) {
      if(tag.getId() == id) return tag;
    }
    return null;
  }

  public boolean isExpandable_todo() {
    return expandable_todo;
  }

  public NotifyInterval getNotifyInterval() {
    return notifyInterval;
  }

  public String getSoundUri() {
    return soundUri;
  }

  public void setNonScheduledLists(List<NonScheduledList> nonScheduledLists) {
    this.nonScheduledLists = nonScheduledLists;
  }

  public void setTagList(List<Tag> tagList) {
    this.tagList = tagList;
  }

  public void setExpandable_todo(boolean expandable_todo) {
    this.expandable_todo = expandable_todo;
  }

  public void setNotifyInterval(NotifyInterval notifyInterval) {
    this.notifyInterval = notifyInterval;
  }

  public void setSoundUri(String soundUri) {
    this.soundUri = soundUri;
  }
}
