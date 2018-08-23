package com.example.hideaki.reminder;

import java.io.Serializable;

public class DayRepeat implements Serializable, Cloneable {

  private static final long serialVersionUID = 4430799135714799779L;
  private String label = null;
  private int interval = 0;
  private int scale = 0;
  private boolean day = false;
  private int week = 0; //月～日曜日の7個のパラメータの設定状況のフラグをビットで表す
  private int days_of_month = 0; //1日～月末までの最大31個のパラメータの設定状況のフラグをビットで表す
  private int ordinal_number = 0;
  private Week on_the_month = null;
  //一度ordinal_numberに一致する週数の月曜日を見つけたら、この変数が5になるまで(金曜日になるまで)Repeatを
  //毎日に固定する
  private int weekday_num = 0;
  private boolean days_of_month_setted = true;
  private int year = 0; //1～12月の12個のパラメータの設定状況のフラグをビットで表す
  private int day_of_month_of_year = 0;
  private int setted = 0; //day, week, month, yearの4つについて設定状況のフラグをビットで表す
  private int which_template = 0;

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

  public int getWeekday_num() {
    return weekday_num;
  }

  public boolean isDays_of_month_setted() {
    return days_of_month_setted;
  }

  public int getYear() {
    return year;
  }

  public int getDay_of_month_of_year() {
    return day_of_month_of_year;
  }

  public int getSetted() {
    return setted;
  }

  public int getWhich_template() {
    return which_template;
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

  public void setWeekday_num(int weekday_num) {
    this.weekday_num = weekday_num;
  }

  public void setDays_of_month_setted(boolean days_of_month_setted) {
    this.days_of_month_setted = days_of_month_setted;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public void setDay_of_month_of_year(int day_of_month_of_year) {
    this.day_of_month_of_year = day_of_month_of_year;
  }

  public void setSetted(int setted) {
    this.setted = setted;
  }

  public void setWhich_template(int which_template) {
    this.which_template = which_template;
  }

  public DayRepeat clone() {

    DayRepeat dayRepeat = new DayRepeat();

    dayRepeat.setLabel(this.label);
    dayRepeat.setInterval(this.interval);
    dayRepeat.setScale(this.scale);
    dayRepeat.setDay(this.day);
    dayRepeat.setWeek(this.week);
    dayRepeat.setDays_of_month(this.days_of_month);
    dayRepeat.setOrdinal_number(this.ordinal_number);
    dayRepeat.setOn_the_month(this.on_the_month);
    dayRepeat.setWeekday_num(this.weekday_num);
    dayRepeat.setDays_of_month_setted(this.days_of_month_setted);
    dayRepeat.setYear(this.year);
    dayRepeat.setDay_of_month_of_year(this.day_of_month_of_year);
    dayRepeat.setSetted(this.setted);
    dayRepeat.setWhich_template(this.which_template);

    return dayRepeat;
  }

  public void clear() {

    this.label = null;
    this.interval = 0;
    this.scale = 0;
    this.day = false;
    this.week = 0;
    this.days_of_month = 0;
    this.ordinal_number = 0;
    this.on_the_month = null;
    this.weekday_num = 0;
    this.days_of_month_setted = true;
    this.year = 0;
    this.day_of_month_of_year = 0;
    this.setted = 0;
    this.which_template = 0;
  }

  public void dayClear() {

    this.week = 0;
    this.days_of_month = 0;
    this.ordinal_number = 0;
    this.on_the_month = null;
    this.days_of_month_setted = true;
    this.year = 0;
    this.day_of_month_of_year = 0;
  }

  public void weekClear() {

    this.day = false;
    this.days_of_month = 0;
    this.ordinal_number = 0;
    this.on_the_month = null;
    this.days_of_month_setted = true;
    this.year = 0;
    this.day_of_month_of_year = 0;
  }

  public void daysOfMonthClear() {

    this.day = false;
    this.week = 0;
    this.ordinal_number = 0;
    this.on_the_month = null;
    this.year = 0;
    this.day_of_month_of_year = 0;
  }

  public void onTheMonthClear() {

    this.day = false;
    this.week = 0;
    this.days_of_month = 0;
    this.year = 0;
    this.day_of_month_of_year = 0;
  }

  public void yearClear() {

    this.day = false;
    this.week = 0;
    this.days_of_month = 0;
    this.ordinal_number = 0;
    this.on_the_month = null;
    this.days_of_month_setted = true;
  }
}
