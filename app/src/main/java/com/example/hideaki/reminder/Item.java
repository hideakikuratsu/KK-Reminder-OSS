package com.example.hideaki.reminder;

import java.util.Date;

public class Item {
  private int id;
  private String detail;
  private Date date;
  private String repeat;

  public Item(String detail, Date date, String repeat) {
    this.detail = detail;
    this.date = date;
    this.repeat = repeat;
  }

  public int getId() {
    return id;
  }

  public String getDetail() {
    return detail;
  }

  public Date getDate() {
    return date;
  }

  public String getRepeat() {
    return repeat;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public void setRepeat(String repeat) {
    this.repeat = repeat;
  }
}
