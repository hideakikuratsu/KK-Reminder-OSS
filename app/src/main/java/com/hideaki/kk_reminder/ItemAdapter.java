package com.hideaki.kk_reminder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;

class ItemAdapter implements Cloneable {

  private Item3 item;

  ItemAdapter() {

    item = new Item3();
  }

  ItemAdapter(Object obj) {

    if(obj instanceof Item3) {
      item = (Item3)obj;
    }
    else if(obj instanceof Item2) {
      Item2 oldItem = (Item2)obj;
      item = new Item3();
      item.setId(oldItem.getId());
      item.setDetail(oldItem.getDetail());
      item.setDate(oldItem.getDate());
      item.setOrgDate(oldItem.getOrgDate());
      item.setOrgDate2(oldItem.getOrgDate2());
      item.setWhichTagBelongs(oldItem.getWhichTagBelongs());
      item.setNotifyInterval(
        new NotifyIntervalAdapter(oldItem.getNotifyInterval()).getNotifyInterval()
      );
      item.setDayRepeat(new DayRepeatAdapter(oldItem.getDayRepeat()).getDayRepeat());
      item.setMinuteRepeat(new MinuteRepeatAdapter(oldItem.getMinuteRepeat()).getMinuteRepeat());
      item.setSoundUri(oldItem.getSoundUri());

      List<Notes2> oldNotesList = oldItem.getNotesList();
      List<Notes2> newNotesList = item.getNotesList();
      for(Notes2 oldNotes : oldNotesList) {
        newNotesList.add(new NotesAdapter(oldNotes).getNotes());
      }

      item.setIsChecklistMode(oldItem.isChecklistMode());
      item.setAlteredTime(oldItem.getAlteredTime());
      item.setOrgAlteredTime(oldItem.getOrgAlteredTime());
      item.setIsAlarmStopped(oldItem.isAlarmStopped());
      item.setIsOrgAlarmStopped(oldItem.isOrgAlarmStopped());
      item.setWhichListBelongs(oldItem.getWhichListBelongs());
      item.setOrder(oldItem.getOrder());
      item.setIsSelected(oldItem.isSelected());
      item.setDoneDate(oldItem.getDoneDate());
    }
    else {
      throw new IllegalArgumentException("Arg item is not instance of Item Class");
    }
  }

  Item3 getItem() {

    return item;
  }

  long getId() {

    return item.getId();
  }

  String getDetail() {

    return item.getDetail();
  }

  Calendar getDate() {

    return item.getDate();
  }

  Calendar getOrgDate() {

    return item.getOrgDate();
  }

  Calendar getOrgDate2() {

    return item.getOrgDate2();
  }

  long getWhichTagBelongs() {

    return item.getWhichTagBelongs();
  }

  NotifyIntervalAdapter getNotifyInterval() {

    return new NotifyIntervalAdapter(item.getNotifyInterval());
  }

  DayRepeatAdapter getDayRepeat() {

    return new DayRepeatAdapter(item.getDayRepeat());
  }

  MinuteRepeatAdapter getMinuteRepeat() {

    return new MinuteRepeatAdapter(item.getMinuteRepeat());
  }

  String getSoundUri() {

    return item.getSoundUri();
  }

  List<NotesAdapter> getNotesList() {

    List<NotesAdapter> notesList = new ArrayList<>();
    for(Notes2 notes : item.getNotesList()) {
      notesList.add(new NotesAdapter(notes));
    }

    return notesList;
  }

  String getNotesString() {

    return item.getNotesString();
  }

  boolean isChecklistMode() {

    return item.isChecklistMode();
  }

  long getAlteredTime() {

    return item.getAlteredTime();
  }

  long getOrgAlteredTime() {

    return item.getOrgAlteredTime();
  }

  boolean isAlarmStopped() {

    return item.isAlarmStopped();
  }

  boolean isOrgIsAlarmStopped() {

    return item.isOrgAlarmStopped();
  }

  long getWhichListBelongs() {

    return item.getWhichListBelongs();
  }

  int getOrder() {

    return item.getOrder();
  }

  boolean isSelected() {

    return item.isSelected();
  }

  Calendar getDoneDate() {

    return item.getDoneDate();
  }

  String getVibrationPattern() {

    return item.getVibrationPattern();
  }

  void setId(long id) {

    item.setId(id);
  }

  void setDetail(String detail) {

    item.setDetail(detail);
  }

  void setDate(Calendar date) {

    item.setDate(date);
  }

  void setOrgDate(Calendar orgDate) {

    item.setOrgDate(orgDate);
  }

  void setOrgDate2(Calendar orgDate2) {

    item.setOrgDate2(orgDate2);
  }

  void setWhichTagBelongs(long whichTagBelongs) {

    item.setWhichTagBelongs(whichTagBelongs);
  }

  void setNotifyInterval(NotifyIntervalAdapter notifyInterval) {

    item.setNotifyInterval(notifyInterval.getNotifyInterval());
  }

  void setDayRepeat(DayRepeatAdapter dayRepeat) {

    item.setDayRepeat(dayRepeat.getDayRepeat());
  }

  void setMinuteRepeat(MinuteRepeatAdapter minuteRepeat) {

    item.setMinuteRepeat(minuteRepeat.getMinuteRepeat());
  }

  void setSoundUri(String soundUri) {

    item.setSoundUri(soundUri);
  }

  void setNotesList(List<NotesAdapter> notesList) {

    List<Notes2> notes2List = new ArrayList<>();
    for(NotesAdapter notes : notesList) {
      notes2List.add(notes.getNotes());
    }
    item.setNotesList(notes2List);
  }

  void addNotes(NotesAdapter notes) {

    item.getNotesList().add(notes.getNotes());
  }

  void clearNotesList() {

    item.getNotesList().clear();
  }

  void setChecklistMode(boolean checklistMode) {

    item.setIsChecklistMode(checklistMode);
  }

  void addAlteredTime(long alteredTime) {

    item.addAlteredTime(alteredTime);
  }

  void setAlteredTime(long alteredTime) {

    item.setAlteredTime(alteredTime);
  }

  void setOrgAlteredTime(long orgAlteredTime) {

    item.setOrgAlteredTime(orgAlteredTime);
  }

  void setAlarmStopped(boolean alarmStopped) {

    item.setIsAlarmStopped(alarmStopped);
  }

  void setOrgIsAlarmStopped(boolean orgIsAlarmStopped) {

    item.setIsOrgAlarmStopped(orgIsAlarmStopped);
  }

  void setWhichListBelongs(long whichListBelongs) {

    item.setWhichListBelongs(whichListBelongs);
  }

  void setOrder(int order) {

    item.setOrder(order);
  }

  void setSelected(boolean selected) {

    item.setIsSelected(selected);
  }

  void setDoneDate(Calendar doneDate) {

    item.setDoneDate(doneDate);
  }

  void setVibrationPattern(String vibrationPattern) {

    item.setVibrationPattern(vibrationPattern);
  }

  @NonNull
  @Override
  public ItemAdapter clone() {

    try {
      ItemAdapter item = (ItemAdapter)super.clone();
      item.item = this.item.clone();

      return item;
    }
    catch(CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }
}
