package com.example.hideaki.reminder;

import java.io.Serializable;

public class MinuteRepeat implements Serializable, Cloneable {

  private static final long serialVersionUID = 1481675552108704976L;
  private String label = null;
  private int hour = 0;
  private int minute = 10;
  private int count = 3;
  private int org_count = 3;
  private int org_count2 = 3; //スナックバーから元に戻すときのために値を保持する
  private int duration_hour = 1;
  private int duration_minute = 0;
  private int org_duration_hour = 1;
  private int org_duration_minute = 0;
  private long org_duration2 = 0; //スナックバーから元に戻すときのために値を保持する
  private int which_setted = 0;

  public String getLabel() {
    return label;
  }

  public int getHour() {
    return hour;
  }

  public int getMinute() {
    return minute;
  }

  public long getInterval() {
    return (this.hour * 60 + this.minute) * 60 * 1000;
  }

  public int getCount() {
    return count;
  }

  public int getOrg_count() {
    return org_count;
  }

  public int getOrg_count2() {
    return org_count2;
  }

  public long getDuration() {
    return (this.duration_hour * 60 + this.duration_minute) * 60 * 1000;
  }

  public int getOrg_duration_hour() {
    return org_duration_hour;
  }

  public int getOrg_duration_minute() {
    return org_duration_minute;
  }

  public long getOrgDuration() {
    return (this.org_duration_hour * 60 + this.org_duration_minute) * 60 * 1000;
  }

  public long getOrg_duration2() {
    return org_duration2;
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

  public void setCount(int count) {
    this.count = count;
  }

  public void setOrg_count(int org_count) {
    this.org_count = org_count;
  }

  public void addOrg_count(int org_count) {
    this.org_count += org_count;
  }

  public void setOrg_count2(int org_count2) {
    this.org_count2 = org_count2;
  }

  public void setDuration(long rest) {
    this.duration_hour = (int)(rest / (1000 * 60 * 60));
    this.duration_minute = (int)(rest / (1000 * 60) - this.duration_hour * 60);
  }

  public void setOrg_duration_hour(int org_duration_hour) {
    this.org_duration_hour = org_duration_hour;
  }

  public void setOrg_duration_minute(int org_duration_minute) {
    this.org_duration_minute = org_duration_minute;
  }

  public void setOrg_duration2(long org_duration2) {
    this.org_duration2 = org_duration2;
  }

  public void setWhich_setted(int which_setted) {
    this.which_setted = which_setted;
  }

  @Override
  public MinuteRepeat clone() {

    MinuteRepeat minuteRepeat = null;

    try {
      minuteRepeat = (MinuteRepeat)super.clone();
    } catch(CloneNotSupportedException e) {
      e.printStackTrace();
    }

    return minuteRepeat;
  }

  public void clear() {

    this.label = null;
    this.hour = 0;
    this.minute = 10;
    this.count = 3;
    this.org_count = 3;
    this.org_count2 = 3;
    this.duration_hour = 1;
    this.duration_minute = 0;
    this.org_duration_hour = 1;
    this.org_duration_minute = 0;
    this.org_duration2 = 0;
    this.which_setted = 0;
  }

  public void countClear() {

    this.duration_hour = 1;
    this.duration_minute = 0;
    this.org_duration_hour = 1;
    this.org_duration_minute = 0;
    this.org_duration2 = 0;
  }

  public void durationClear() {

    this.count = 3;
    this.org_count = 3;
    this.org_count2 = 3;
  }
}
