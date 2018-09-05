package com.example.hideaki.reminder;

import java.io.Serializable;
import java.util.Calendar;

public class NonScheduledList implements Serializable {

  private static final long serialVersionUID = 3408412079708605304L;
  private final long id = Calendar.getInstance().getTimeInMillis();
  private String title;
  private String notes;
  private int color;
  private int color_order_group = -1;
  private int color_order_child = 5;
  private int order;

  NonScheduledList() {}

  NonScheduledList(String title) {
    this.title = title;
  }

  public long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getNotes() {
    return notes;
  }

  public int getColor() {
    return color;
  }

  public int getColor_order_group() {
    return color_order_group;
  }

  public int getColor_order_child() {
    return color_order_child;
  }

  public int getOrder() {
    return order;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public void setColor(int color) {
    this.color = color;
  }

  public void setColor_order_group(int color_order_group) {
    this.color_order_group = color_order_group;
  }

  public void setColor_order_child(int color_order_child) {
    this.color_order_child = color_order_child;
  }

  public void setOrder(int order) {
    this.order = order;
  }
}
