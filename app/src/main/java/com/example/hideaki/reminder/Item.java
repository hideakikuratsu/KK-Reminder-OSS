package com.example.hideaki.reminder;

import java.io.Serializable;
import java.util.Calendar;

public class Item implements Serializable {

  private static final long serialVersionUID = -3106249767115010510L;
  private final long id = Calendar.getInstance().getTimeInMillis();
  private String detail;
  private Calendar date = Calendar.getInstance();
  //子ビューのコントロールパネルで時間を変えたときだけ時間を元に戻すのに用いる
  private Calendar org_date = (Calendar)date.clone();
  private Calendar org_date2 = (Calendar)date.clone(); //MinuteRepeatで元の時間を保持しておくのに用いる
  private Tag tag = null;
  private NotifyInterval notify_interval = new NotifyInterval();
  private DayRepeat dayRepeat = new DayRepeat();
  private MinuteRepeat minuteRepeat = new MinuteRepeat();
  private String notes;
  private long time_altered; //子ビューのコントロールパネルで時間を変えたとき、変えた総時間を保持する
  private long org_time_altered; //リピート設定による変更をスナックバーから元に戻すのに用いる
  private boolean alarm_stopped;
  private boolean org_alarm_stopped; //リピート設定による変更をスナックバーから元に戻すのに用いる
  private long which_list_belongs;
  private int order = -1;
  private boolean selected;

  Item() {}

  public long getId() {
    return id;
  }

  public String getDetail() {
    return detail;
  }

  public Calendar getDate() {
    return date;
  }

  public Calendar getOrg_date() {
    return org_date;
  }

  public Calendar getOrg_date2() {
    return org_date2;
  }

  public Tag getTag() {
    return tag;
  }

  public NotifyInterval getNotify_interval() {
    return notify_interval;
  }

  public DayRepeat getDayRepeat() {
    return dayRepeat;
  }

  public MinuteRepeat getMinuteRepeat() {
    return minuteRepeat;
  }

  public String getNotes() {
    return notes;
  }

  public long getTime_altered() {
    return time_altered;
  }

  public long getOrg_time_altered() {
    return org_time_altered;
  }

  public boolean isAlarm_stopped() {
    return alarm_stopped;
  }

  public boolean isOrg_alarm_stopped() {
    return org_alarm_stopped;
  }

  public long getWhich_list_belongs() {
    return which_list_belongs;
  }

  public int getOrder() {
    return order;
  }

  public boolean isSelected() {
    return selected;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }

  public void setDate(Calendar date) {
    this.date = date;
  }

  public void setOrg_date(Calendar org_date) {
    this.org_date = org_date;
  }

  public void setOrg_date2(Calendar org_date2) {
    this.org_date2 = org_date2;
  }

  public void setTag(Tag tag) {
    this.tag = tag;
  }

  public void setNotify_interval(NotifyInterval notify_interval) {
    this.notify_interval = notify_interval;
  }

  public void setDayRepeat(DayRepeat dayRepeat) {
    this.dayRepeat = dayRepeat;
  }

  public void setMinuteRepeat(MinuteRepeat minuteRepeat) {
    this.minuteRepeat = minuteRepeat;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public void addTime_altered(long time_altered) {
    this.time_altered += time_altered;
  }

  public void setTime_altered(long time_altered) {
    this.time_altered = time_altered;
  }

  public void setOrg_time_altered(long org_time_altered) {
    this.org_time_altered = org_time_altered;
  }

  public void setAlarm_stopped(boolean alarm_stopped) {
    this.alarm_stopped = alarm_stopped;
  }

  public void setOrg_alarm_stopped(boolean org_alarm_stopped) {
    this.org_alarm_stopped = org_alarm_stopped;
  }

  public void setWhich_list_belongs(long which_list_belongs) {
    this.which_list_belongs = which_list_belongs;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }

  public Item copy() {

    Item item = new Item();

    item.setDetail(this.detail);
    item.setDate((Calendar)this.date.clone());
    item.setOrg_date((Calendar)this.org_date.clone());
    item.setOrg_date2((Calendar)this.org_date2.clone());
    item.setTag(this.tag); //TODO: TagクラスにCloneableを実装する
    item.setNotify_interval(this.notify_interval); //TODO: NotifyIntervalクラスにCloneableを実装する
    item.setDayRepeat(this.dayRepeat.clone());
    item.setMinuteRepeat(this.minuteRepeat.clone());
    item.setNotes(this.notes);
    item.setTime_altered(this.time_altered);
    item.setOrg_time_altered(this.org_time_altered);
    item.setAlarm_stopped(this.alarm_stopped);
    item.setOrg_alarm_stopped(this.org_alarm_stopped);
    item.setWhich_list_belongs(this.which_list_belongs);
    item.setOrder(this.order);
    item.setSelected(this.selected);

    return item;
  }
}