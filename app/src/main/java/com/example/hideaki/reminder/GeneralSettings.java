package com.example.hideaki.reminder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GeneralSettings implements Serializable {

  private static final long serialVersionUID = 5141214717476464740L;
  private List<NonScheduledList> nonScheduledLists = new ArrayList<>();
  private List<Tag> tagList = new ArrayList<>();

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

  public void setNonScheduledLists(List<NonScheduledList> nonScheduledLists) {
    this.nonScheduledLists = nonScheduledLists;
  }

  public void setTagList(List<Tag> tagList) {
    this.tagList = tagList;
  }
}
