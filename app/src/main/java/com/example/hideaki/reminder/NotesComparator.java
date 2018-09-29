package com.example.hideaki.reminder;

import java.util.Comparator;

public class NotesComparator implements Comparator<Notes> {

  @Override
  public int compare(Notes o1, Notes o2) {

    if(o1.getOrder() < o2.getOrder()) {
      return -1;
    }
    else if(o1.getOrder() == o2.getOrder()) {
      return 0;
    }
    else return 1;
  }
}
