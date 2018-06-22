package com.example.hideaki.reminder;

import java.io.Serializable;

public class Repeat implements Serializable {
  private static final long serialVersionUID = 107801905431585756L;
  private String label;
  private int interval;
  private int day;
  private Week week;
  private int days_of_month;
  private Week weeks_of_month;
  private int year;

  public Repeat(String label, int interval, int day, Week week, int days_of_month, Week weeks_of_month, int year) {
    this.label = label;
    this.interval = interval;
    this.day = day;
    this.week = week;
    this.days_of_month = days_of_month;
    this.weeks_of_month = weeks_of_month;
    this.year = year;
  }

  public String getLabel() {
    return label;
  }

  public int getInterval() {
    return interval;
  }

  public int getDay() {
    return day;
  }

  public Week getWeek() {
    return week;
  }

  public int getDays_of_month() {
    return days_of_month;
  }

  public Week getWeeks_of_month() {
    return weeks_of_month;
  }

  public int getYear() {
    return year;
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

  public void setWeek(Week week) {
    this.week = week;
  }

  public void setDays_of_month(int days_of_month) {
    this.days_of_month = days_of_month;
  }

  public void setWeeks_of_month(Week weeks_of_month) {
    this.weeks_of_month = weeks_of_month;
  }

  public void setYear(int year) {
    this.year = year;
  }
}
