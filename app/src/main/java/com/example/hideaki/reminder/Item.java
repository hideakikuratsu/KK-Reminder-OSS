package com.example.hideaki.reminder;

import java.io.Serializable;
import java.util.Calendar;

public class Item implements Serializable {

  private static final long serialVersionUID = 7512800091169383255L;
  private final long id = Calendar.getInstance().getTimeInMillis();
  private String detail;
  private Calendar date = Calendar.getInstance();
  //子ビューのコントロールパネルで時間を変えたときだけ時間を元に戻すのに用いる
  private Calendar org_date = (Calendar)date.clone();
  private Calendar org_date2 = (Calendar)date.clone(); //MinuteRepeatで元の時間を保持しておくのに用いる
  private long which_tag_belongs;
  private NotifyInterval notify_interval = new NotifyInterval();
  private DayRepeat dayRepeat = new DayRepeat();
  private MinuteRepeat minuteRepeat = new MinuteRepeat();
  private String soundUri;
  private String notes;
  private long time_altered; //子ビューのコントロールパネルで時間を変えたとき、変えた総時間を保持する
  private long org_time_altered; //リピート設定による変更をスナックバーから元に戻すのに用いる
  private boolean alarm_stopped;
  private boolean org_alarm_stopped; //リピート設定による変更をスナックバーから元に戻すのに用いる
  private long which_list_belongs;
  private int order = -1;
  private boolean selected;
  private Calendar doneDate = Calendar.getInstance();

  Item() {}

  long getId() {
    return id;
  }

  String getDetail() {
    return detail;
  }

  Calendar getDate() {
    return date;
  }

  Calendar getOrg_date() {
    return org_date;
  }

  Calendar getOrg_date2() {
    return org_date2;
  }

  long getWhich_tag_belongs() {
    return which_tag_belongs;
  }

  NotifyInterval getNotify_interval() {
    return notify_interval;
  }

  DayRepeat getDayRepeat() {
    return dayRepeat;
  }

  MinuteRepeat getMinuteRepeat() {
    return minuteRepeat;
  }

  String getSoundUri() {
    return soundUri;
  }

  String getNotes() {
    return notes;
  }

  long getTime_altered() {
    return time_altered;
  }

  long getOrg_time_altered() {
    return org_time_altered;
  }

  boolean isAlarm_stopped() {
    return alarm_stopped;
  }

  boolean isOrg_alarm_stopped() {
    return org_alarm_stopped;
  }

  long getWhich_list_belongs() {
    return which_list_belongs;
  }

  int getOrder() {
    return order;
  }

  boolean isSelected() {
    return selected;
  }

  Calendar getDoneDate() {
    return doneDate;
  }

  void setDetail(String detail) {
    this.detail = detail;
  }

  void setDate(Calendar date) {
    this.date = date;
  }

  void setOrg_date(Calendar org_date) {
    this.org_date = org_date;
  }

  void setOrg_date2(Calendar org_date2) {
    this.org_date2 = org_date2;
  }

  void setWhich_tag_belongs(long which_tag_belongs) {
    this.which_tag_belongs = which_tag_belongs;
  }

  void setNotify_interval(NotifyInterval notify_interval) {
    this.notify_interval = notify_interval;
  }

  void setDayRepeat(DayRepeat dayRepeat) {
    this.dayRepeat = dayRepeat;
  }

  void setMinuteRepeat(MinuteRepeat minuteRepeat) {
    this.minuteRepeat = minuteRepeat;
  }

  void setSoundUri(String soundUri) {
    this.soundUri = soundUri;
  }

  void setNotes(String notes) {
    this.notes = notes;
  }

  void addTime_altered(long time_altered) {
    this.time_altered += time_altered;
  }

  void setTime_altered(long time_altered) {
    this.time_altered = time_altered;
  }

  void setOrg_time_altered(long org_time_altered) {
    this.org_time_altered = org_time_altered;
  }

  void setAlarm_stopped(boolean alarm_stopped) {
    this.alarm_stopped = alarm_stopped;
  }

  void setOrg_alarm_stopped(boolean org_alarm_stopped) {
    this.org_alarm_stopped = org_alarm_stopped;
  }

  void setWhich_list_belongs(long which_list_belongs) {
    this.which_list_belongs = which_list_belongs;
  }

  void setOrder(int order) {
    this.order = order;
  }

  void setSelected(boolean selected) {
    this.selected = selected;
  }

  void setDoneDate(Calendar doneDate) {
    this.doneDate = doneDate;
  }

  Item copy() {

    Item item = new Item();

    item.setDetail(this.detail);
    item.setDate((Calendar)this.date.clone());
    item.setOrg_date((Calendar)this.org_date.clone());
    item.setOrg_date2((Calendar)this.org_date2.clone());
    item.setWhich_tag_belongs(this.which_tag_belongs);
    item.setNotify_interval(this.notify_interval.clone());
    item.setDayRepeat(this.dayRepeat.clone());
    item.setMinuteRepeat(this.minuteRepeat.clone());
    item.setSoundUri(this.soundUri);
    item.setNotes(this.notes);
    item.setTime_altered(this.time_altered);
    item.setOrg_time_altered(this.org_time_altered);
    item.setAlarm_stopped(this.alarm_stopped);
    item.setOrg_alarm_stopped(this.org_alarm_stopped);
    item.setWhich_list_belongs(this.which_list_belongs);
    item.setOrder(this.order);
    item.setSelected(this.selected);
    item.setDoneDate(this.doneDate);

    return item;
  }
}