package com.hideaki.kk_reminder;

import java.util.Comparator;

public class NonScheduledItemComparator implements Comparator<Item> {

  @Override
  public int compare(Item o1, Item o2) {

    if(o1.getOrder() < o2.getOrder()) {
      return -1;
    }
    else if(o1.getOrder() == o2.getOrder()) {
      return 0;
    }
    else return 1;
  }
}
