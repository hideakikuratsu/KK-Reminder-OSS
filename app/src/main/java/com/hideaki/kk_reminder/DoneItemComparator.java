package com.hideaki.kk_reminder;

import java.util.Comparator;

public class DoneItemComparator implements Comparator<Item> {

  @Override
  public int compare(Item o1, Item o2) {

    if(o1.getDoneDate().getTimeInMillis() > o2.getDoneDate().getTimeInMillis()) {
      return -1;
    }
    else if(o1.getDoneDate().getTimeInMillis() == o2.getDoneDate().getTimeInMillis()) {
      return 0;
    }
    else return 1;
  }
}
