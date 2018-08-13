package com.example.hideaki.reminder;

import java.io.Serializable;

public class NotifyInterval implements Serializable {
  private static final long serialVersionUID = 2239628138775849215L;
  private int hour = 0;
  private int minute = 1;
  private int time = 5;
  private int org_time = time;

  public NotifyInterval() {
  }

  public NotifyInterval(int hour, int minute, int time) {
    this.hour = hour;
    this.minute = minute;
    this.time = time;
    this.org_time = this.time;
  }

  public int getHour() {
    return hour;
  }

  public int getMinute() {
    return minute;
  }

  public int getTime() {
    return time;
  }

  public int getOrg_time() {
    return org_time;
  }

  public void setHour(int hour) {
    this.hour = hour;
  }

  public void setMinute(int minute) {
    this.minute = minute;
  }

  public void setTime(int time) {
    this.time = time;
  }

  public void setOrg_time(int org_time) {
    this.org_time = org_time;
  }
}
