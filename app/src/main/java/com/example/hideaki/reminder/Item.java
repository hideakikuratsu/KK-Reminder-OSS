package com.example.hideaki.reminder;

import java.io.Serializable;
import java.util.Calendar;

public class Item implements Serializable {
  private static final long serialVersionUID = 4442661995575921496L;
  private final long id = Calendar.getInstance().getTimeInMillis();
  private String detail;
  private Calendar date = Calendar.getInstance();
  private Tag tag = null;
  private NotifyInterval notify_interval = new NotifyInterval();
  private Repeat repeat = null;
  private String notes;

  public Item() {
  }

  public long getId() {
    return id;
  }

  public String getDetail() {
    return detail;
  }

  public Calendar getDate() {
    return date;
  }

  public Tag getTag() {
    return tag;
  }

  public NotifyInterval getNotify_interval() {
    return notify_interval;
  }

  public Repeat getRepeat() {
    return repeat;
  }

  public String getNotes() {
    return notes;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public void setDate(Calendar date) {
    this.date = date;
  }

  public void setTag(Tag tag) {
    this.tag = tag;
  }

  public void setNotify_interval(NotifyInterval notify_interval) {
    this.notify_interval = notify_interval;
  }

  public void setRepeat(Repeat repeat) {
    this.repeat = repeat;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}