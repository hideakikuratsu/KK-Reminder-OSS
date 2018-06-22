package com.example.hideaki.reminder;

import java.io.Serializable;
import java.util.Date;

public class Item implements Serializable {
  private static final long serialVersionUID = 8520136120224971584L;
  private long id;
  private String detail;
  private Date date;
  private Tag tag;
  private NotifyInterval notify_interval;
  private Repeat repeat;

  public Item(long id, String detail, Date date, Tag tag, NotifyInterval notify_interval, Repeat repeat) {
    this.id = id;
    this.detail = detail;
    this.date = date;
    this.tag = tag;
    this.notify_interval = notify_interval;
    this.repeat = repeat;
  }

  public long getId() {
    return id;
  }

  public String getDetail() {
    return detail;
  }

  public Date getDate() {
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

  public void setId(long id) {
    this.id = id;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public void setDate(Date date) {
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
}