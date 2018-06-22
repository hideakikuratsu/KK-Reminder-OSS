package com.example.hideaki.reminder;

import java.io.Serializable;

public class Tag implements Serializable {
  private static final long serialVersionUID = 1046101888928744478L;
  String name;
  TagColor color;

  public Tag(String name, TagColor color) {
    this.name = name;
    this.color = color;
  }

  public String getName() {
    return name;
  }

  public TagColor getColor() {
    return color;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setColor(TagColor color) {
    this.color = color;
  }
}
