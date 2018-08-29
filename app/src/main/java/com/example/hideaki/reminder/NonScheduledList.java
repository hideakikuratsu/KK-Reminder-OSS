package com.example.hideaki.reminder;

import java.io.Serializable;
import java.util.Calendar;

public class NonScheduledList implements Serializable {

  private static final long serialVersionUID = -7354125076757233178L;
  private final long id = Calendar.getInstance().getTimeInMillis();
  private String title;

  NonScheduledList() {}

  NonScheduledList(String title) {
    this.title = title;
  }

  public long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
