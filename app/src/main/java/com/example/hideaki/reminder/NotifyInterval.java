package com.example.hideaki.reminder;

import java.io.Serializable;

public class NotifyInterval implements Serializable, Cloneable {

  private static final long serialVersionUID = 8844386229395430923L;
  private String label = null;
  private int hour;
  private int minute;
  private int time;
  private int org_time;
  private int which_setted;

  public String getLabel() {
    return label;
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

  public int getWhich_setted() {
    return which_setted;
  }

  public void setLabel(String label) {
    this.label = label;
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

  public void addOrgTime(int org_time) {
    this.org_time += org_time;
  }

  public void setWhich_setted(int which_setted) {
    this.which_setted = which_setted;
  }

  @Override
  public NotifyInterval clone() {

    NotifyInterval notifyInterval = null;

    try {
      notifyInterval = (NotifyInterval)super.clone();
    } catch(CloneNotSupportedException e) {
      e.printStackTrace();
    }

    return notifyInterval;
  }
}
