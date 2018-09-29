package com.example.hideaki.reminder;

import java.io.Serializable;
import java.util.Calendar;

public class NonScheduledList implements Serializable {

  private static final long serialVersionUID = 4154734183539710182L;
  private final long id = Calendar.getInstance().getTimeInMillis();
  private String title;
  private boolean color_primary = true;
  private int primary_color;
  private int primary_light_color;
  private int primary_dark_color;
  private int primary_text_color;
  private int primary_color_group = -1;
  private int primary_color_child = 5;
  private int secondary_color;
  private int secondary_light_color;
  private int secondary_dark_color;
  private int secondary_text_color;
  private int secondary_color_group = -1;
  private int secondary_color_child = 5;
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

  public boolean isColor_primary() {
    return color_primary;
  }

  public int getColor() {
    return color_primary ? primary_color : secondary_color;
  }

  public int getLightColor() {
    return color_primary ? primary_light_color : secondary_light_color;
  }

  public int getDarkColor() {
    return color_primary ? primary_dark_color : secondary_dark_color;
  }

  public int getTextColor() {
    return color_primary ? primary_text_color : secondary_text_color;
  }

  public int getColorGroup() {
    return color_primary ? primary_color_group : secondary_color_group;
  }

  public int getColorChild() {
    return color_primary ? primary_color_child : secondary_color_child;
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

  public void setColor_primary(boolean color_primary) {
    this.color_primary = color_primary;
  }

  public void setColor(int color) {

    if(color_primary) this.primary_color = color;
    else this.secondary_color = color;
  }

  public void setLightColor(int light_color) {

    if(color_primary) this.primary_light_color = light_color;
    else this.secondary_light_color = light_color;
  }

  public void setDarkColor(int dark_color) {

    if(color_primary) this.primary_dark_color = dark_color;
    else this.secondary_dark_color = dark_color;
  }

  public void setTextColor(int text_color) {

    if(color_primary) this.primary_text_color = text_color;
    else this.secondary_text_color = text_color;
  }

  public void setColorGroup(int color_group) {

    if(color_primary) this.primary_color_group = color_group;
    else this.secondary_color_group = color_group;
  }

  public void setColorChild(int color_child) {

    if(color_primary) this.primary_color_child = color_child;
    else this.secondary_color_child = color_child;
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
