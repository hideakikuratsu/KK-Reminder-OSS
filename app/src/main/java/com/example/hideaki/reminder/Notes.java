package com.example.hideaki.reminder;

import java.io.Serializable;

public class Notes implements Serializable, Cloneable {

  private static final long serialVersionUID = 7284140551902180989L;
  private String string;
  private int order;
  private boolean checked;

  public Notes() {}

  Notes(String string, boolean checked, int order) {

    this.string = string;
    this.checked = checked;
    this.order = order;
  }

  public String getString() {
    return string;
  }

  public int getOrder() {
    return order;
  }

  public boolean isChecked() {
    return checked;
  }

  public void setString(String string) {
    this.string = string;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  public void setChecked(boolean checked) {
    this.checked = checked;
  }

  @Override
  public Notes clone() {

    Notes notes = null;

    try {
      notes = (Notes)super.clone();
    } catch(CloneNotSupportedException e) {
      e.printStackTrace();
    }

    return notes;
  }
}
