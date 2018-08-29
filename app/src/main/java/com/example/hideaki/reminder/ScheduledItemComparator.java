package com.example.hideaki.reminder;

import java.util.Comparator;

public class ScheduledItemComparator implements Comparator<Item> {

  @Override
  public int compare(Item o1, Item o2) {

    if(o1.getDate().getTimeInMillis() < o2.getDate().getTimeInMillis()) {
      return -1;
    }
    else if(o1.getDate().getTimeInMillis() == o2.getDate().getTimeInMillis()) {
      return 0;
    }
    else return 1;
  }
}
