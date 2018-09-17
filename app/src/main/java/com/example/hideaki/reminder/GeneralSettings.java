package com.example.hideaki.reminder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GeneralSettings implements Serializable {

  private static final long serialVersionUID = 7547092849110283632L;
  private List<NonScheduledList> nonScheduledLists = new ArrayList<>();

  public NonScheduledList getNonScheduledList(int position) {
    return nonScheduledLists.get(position);
  }

  public List<NonScheduledList> getNonScheduledLists() {
    return new ArrayList<>(nonScheduledLists);
  }

  public List<NonScheduledList> getOrgNonScheduledLists() {
    return nonScheduledLists;
  }

  public void setNonScheduledLists(List<NonScheduledList> nonScheduledLists) {
    this.nonScheduledLists = new ArrayList<>(nonScheduledLists);
  }

  public void addNonScheduledList(NonScheduledList nonScheduledList) {
    this.nonScheduledLists.add(0, nonScheduledList);
  }
}
