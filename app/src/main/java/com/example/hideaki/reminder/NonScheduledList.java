package com.example.hideaki.reminder;

import java.io.Serializable;
import java.util.Calendar;

public class NonScheduledList implements Serializable {

  private static final long serialVersionUID = 9172991234359085705L;
  private final long id = Calendar.getInstance().getTimeInMillis();
  private String title;
  private String notes;
  private int primary_color;
  private int primary_light_color;
  private int primary_dark_color;
  private int primary_text_color;
  private int color_order_group = -1;
  private int color_order_child = 5;
  private int order;
  private long which_tag_belongs;
  private boolean todo = true;

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

  public int getPrimary_color() {
    return primary_color;
  }

  public int getPrimary_light_color() {
    return primary_light_color;
  }

  public int getPrimary_dark_color() {
    return primary_dark_color;
  }

  public int getPrimary_text_color() {
    return primary_text_color;
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

  public long getWhich_tag_belongs() {
    return which_tag_belongs;
  }

  public boolean isTodo() {
    return todo;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public void setPrimary_color(int primary_color) {
    this.primary_color = primary_color;
  }

  public void setPrimary_light_color(int primary_light_color) {
    this.primary_light_color = primary_light_color;
  }

  public void setPrimary_dark_color(int primary_dark_color) {
    this.primary_dark_color = primary_dark_color;
  }

  public void setPrimary_text_color(int primary_text_color) {
    this.primary_text_color = primary_text_color;
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

  public void setWhich_tag_belongs(long which_tag_belongs) {
    this.which_tag_belongs = which_tag_belongs;
  }

  public void setTodo(boolean todo) {
    this.todo = todo;
  }
}
