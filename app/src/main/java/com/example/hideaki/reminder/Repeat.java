package com.example.hideaki.reminder;

import java.io.Serializable;

public class Repeat implements Serializable, Cloneable {
  private static final long serialVersionUID = 4983350086185199626L;
  private String label = null;
  private int interval = 1;
  private int day = 0;
  private int week = 0;
  private int days_of_month = 0;
  private int ordinal_number = 0;
  private Week on_the_month = null;
  private int year = 0;
  private boolean is_setted = false;

  public String getLabel() {
    return label;
  }

  public int getInterval() {
    return interval;
  }

  public int getDay() {
    return day;
  }

  public int getWeek() {
    return week;
  }

  public int getDays_of_month() {
    return days_of_month;
  }

  public int getOrdinal_number() {
    return ordinal_number;
  }

  public Week getOn_the_month() {
    return on_the_month;
  }

  public int getYear() {
    return year;
  }

  public boolean isIs_setted() {
    return is_setted;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public void setInterval(int interval) {
    this.interval = interval;
  }

  public void setDay(int day) {
    this.day = day;
  }

  public void setWeek(int week) {
    this.week = week;
  }

  public void setDays_of_month(int days_of_month) {
    this.days_of_month = days_of_month;
  }

  public void setOrdinal_number(int ordinal_number) {
    this.ordinal_number = ordinal_number;
  }

  public void setOn_the_month(Week on_the_month) {
    this.on_the_month = on_the_month;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public void setIs_setted(boolean is_setted) {
    this.is_setted = is_setted;
  }

  @Override
  protected Object clone() {

    Repeat repeat = new Repeat();

    repeat.setLabel(this.label);
    repeat.setInterval(this.interval);
    repeat.setDay(this.day);
    repeat.setWeek(this.week);
    repeat.setDays_of_month(this.days_of_month);
    repeat.setOrdinal_number(this.ordinal_number);
    repeat.setOn_the_month(this.on_the_month);
    repeat.setYear(this.year);
    repeat.setIs_setted(this.is_setted);

    return repeat;
  }

  public void clear() {

    this.label = null;
    this.interval = 1;
    this.day = 0;
    this.week = 0;
    this.days_of_month = 0;
    this.ordinal_number = 0;
    this.on_the_month = null;
    this.year = 0;
    this.is_setted = false;
  }

  public void dayClear() {

    this.week = 0;
    this.days_of_month = 0;
    this.ordinal_number = 0;
    this.on_the_month = null;
    this.year = 0;
  }

  public void weekClear() {

    this.day = 0;
    this.days_of_month = 0;
    this.ordinal_number = 0;
    this.on_the_month = null;
    this.year = 0;
  }

  public void monthClear() {

    this.day = 0;
    this.week = 0;
    this.year = 0;
  }

  public void yearClear() {

    this.day = 0;
    this.week = 0;
    this.days_of_month = 0;
    this.ordinal_number = 0;
    this.on_the_month = null;
  }
}
