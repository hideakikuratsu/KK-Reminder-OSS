package com.example.hideaki.reminder;

import java.io.Serializable;
import java.util.Calendar;

public class Tag implements Serializable {

  private static final long serialVersionUID = 5561553425579996094L;
  private final long id;
  private String name;
  private int primary_color;
  private int primary_light_color;
  private int primary_dark_color;
  private int primary_text_color;
  private int color_order_group = -1;
  private int color_order_child = 5;
  private int order;

  Tag() {
    this.id = Calendar.getInstance().getTimeInMillis();
  }

  Tag(long id) {
    this.id = id;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
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

  public void setName(String name) {
    this.name = name;
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
}
