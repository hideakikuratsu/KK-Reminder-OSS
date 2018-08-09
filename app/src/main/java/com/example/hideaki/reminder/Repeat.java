package com.example.hideaki.reminder;

import java.io.Serializable;

public class Repeat implements Serializable, Cloneable {
  private static final long serialVersionUID = 3887166489806671308L;
  private String label = null;
  private int interval = 0;
  private int scale = 0;
  private boolean day = false;
  private int week = 0;
  private int days_of_month = 0;
  private int ordinal_number = 0;
  private Week on_the_month = null;
  private boolean month_setted = false;
  private int year = 0;
  private boolean setted = false;

  public String getLabel() {
    return label;
  }

  public int getInterval() {
    return interval;
  }

  public int getScale() {
    return scale;
  }

  public boolean isDay() {
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

  public boolean isMonth_setted() {
    return month_setted;
  }

  public int getYear() {
    return year;
  }

  public boolean isSetted() {
    return setted;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public void setInterval(int interval) {
    this.interval = interval;
  }

  public void setScale(int scale) {
    this.scale = scale;
  }

  public void setDay(boolean day) {
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

  public void setMonth_setted(boolean month_setted) {
    this.month_setted = month_setted;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public void setSetted(boolean setted) {
    this.setted = setted;
  }

  @Override
  protected Object clone() {

    Repeat repeat = new Repeat();

    repeat.setLabel(this.label);
    repeat.setInterval(this.interval);
    repeat.setScale(this.scale);
    repeat.setDay(this.day);
    repeat.setWeek(this.week);
    repeat.setDays_of_month(this.days_of_month);
    repeat.setOrdinal_number(this.ordinal_number);
    repeat.setOn_the_month(this.on_the_month);
    repeat.setMonth_setted(this.month_setted);
    repeat.setYear(this.year);
    repeat.setSetted(this.setted);

    return repeat;
  }

  public void clear() {

    this.label = null;
    this.interval = 1;
    this.scale = 0;
    this.day = false;
    this.week = 0;
    this.days_of_month = 0;
    this.ordinal_number = 0;
    this.on_the_month = null;
    this.month_setted = false;
    this.year = 0;
    this.setted = false;
  }

  public void dayClear() {

    this.week = 0;
    this.days_of_month = 0;
    this.ordinal_number = 0;
    this.on_the_month = null;
    this.year = 0;
  }

  public void weekClear() {

    this.day = false;
    this.days_of_month = 0;
    this.ordinal_number = 0;
    this.on_the_month = null;
    this.year = 0;
  }

  public void daysOfMonthClear() {

    this.day = false;
    this.week = 0;
    this.ordinal_number = 0;
    this.on_the_month = null;
    this.year = 0;
  }

  public void onTheMonthClear() {

    this.day = false;
    this.week = 0;
    this.days_of_month = 0;
    this.year = 0;
  }

  public void yearClear() {

    this.day = false;
    this.week = 0;
    this.days_of_month = 0;
    this.ordinal_number = 0;
    this.on_the_month = null;
  }
}
