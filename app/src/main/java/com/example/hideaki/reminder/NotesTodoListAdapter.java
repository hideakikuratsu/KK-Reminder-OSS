package com.example.hideaki.reminder;

import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.hideaki.reminder.UtilClass.NOTES_COMPARATOR;
import static com.google.common.base.Preconditions.checkNotNull;

public class NotesTodoListAdapter extends BaseAdapter {

  static List<Notes> notesList;
  private MainActivity activity;
  private NotesChecklistModeFragment fragment;
  DragListener dragListener;
  private int draggingPosition = -1;
  static boolean is_sorting;

  NotesTodoListAdapter(MainActivity activity, NotesChecklistModeFragment fragment) {

    this.activity = activity;
    this.fragment = fragment;
    dragListener = new DragListener();
    is_sorting = false;
  }

  private static class ViewHolder {

    ConstraintLayout notesItem;
    CheckBox checkBox;
    TextView string;
  }

  private class MyOnClickListener implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private int position;
    private Notes notes;
    private ViewHolder viewHolder;

    MyOnClickListener(int position, Notes notes, ViewHolder viewHolder) {

      this.position = position;
      this.notes = notes;
      this.viewHolder = viewHolder;
    }

    @Override
    public void onClick(View v) {

      viewHolder.checkBox.setChecked(true);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

      if(isChecked) {
        viewHolder.checkBox.jumpDrawablesToCurrentState();
        notes.setChecked(true);
        NotesDoneListAdapter.notesList.add(notes);
        Collections.sort(NotesDoneListAdapter.notesList, NOTES_COMPARATOR);
        notesList.remove(position);

        //todoListにおけるheaderの管理
        int todo_list_size = notesList.size();
        if(todo_list_size != 0 && fragment.sortableListView.getHeaderViewsCount() == 0) {
          fragment.sortableListView.addHeaderView(fragment.todoHeader);
        }
        else if(todo_list_size == 0 && fragment.sortableListView.getHeaderViewsCount() != 0) {
          fragment.sortableListView.removeHeaderView(fragment.todoHeader);
        }

        //doneListにおけるheaderの管理
        int done_list_size = NotesDoneListAdapter.notesList.size();
        if(done_list_size != 0 && fragment.listView.getHeaderViewsCount() == 0) {
          fragment.listView.addHeaderView(fragment.doneHeader);
          fragment.deleteItem.setVisible(true);
          fragment.unselectItem.setVisible(true);
        }
        else if(done_list_size == 0 && fragment.listView.getHeaderViewsCount() != 0) {
          fragment.listView.removeHeaderView(fragment.doneHeader);
          fragment.deleteItem.setVisible(false);
          fragment.unselectItem.setVisible(false);
        }

        notifyDataSetChanged();
        fragment.notesDoneListAdapter.notifyDataSetChanged();

        if(activity.isItemExists(NotesChecklistModeFragment.item, MyDatabaseHelper.TODO_TABLE)) {
          activity.updateDB(NotesChecklistModeFragment.item, MyDatabaseHelper.TODO_TABLE);
        }
        else {
          MainEditFragment.item.setNotesList(new ArrayList<>(NotesChecklistModeFragment.item.getNotesList()));
        }
      }
    }
  }

  class DragListener extends SortableListView.SimpleDragListener {

    @Override
    public int onStartDrag(int position) {

      draggingPosition = position;
      notifyDataSetChanged();

      return position;
    }

    @Override
    public int onDuringDrag(int positionFrom, int positionTo) {

      if(positionFrom < 0 || positionTo < 0 || positionFrom == positionTo) {
        return positionFrom;
      }

      Notes notes = notesList.get(positionFrom);
      notesList.remove(positionFrom);
      notesList.add(positionTo, notes);

      draggingPosition = positionTo;
      notifyDataSetChanged();

      return positionTo;
    }

    @Override
    public boolean onStopDrag(int positionFrom, int positionTo) {

      draggingPosition = -1;
      notifyDataSetChanged();

      return super.onStopDrag(positionFrom, positionTo);
    }
  }

  @Override
  public int getCount() {
    return notesList.size();
  }

  @Override
  public Object getItem(int position) {
    return notesList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    final ViewHolder viewHolder;

    if(convertView == null) {
      convertView = View.inflate(parent.getContext(), R.layout.notes_checklist_todo_item_layout, null);

      viewHolder = new ViewHolder();
      viewHolder.notesItem = convertView.findViewById(R.id.notes_item);
      viewHolder.checkBox = convertView.findViewById(R.id.checkBox);
      viewHolder.string = convertView.findViewById(R.id.string);

      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder)convertView.getTag();
    }

    //現在のビュー位置でのtagの取得とリスナーの初期化
    Notes notes = (Notes)getItem(position);
    MyOnClickListener listener = new MyOnClickListener(position, notes, viewHolder);

    //リスナーの設定
    viewHolder.notesItem.setOnClickListener(listener);
    viewHolder.checkBox.setOnCheckedChangeListener(listener);

    //チェック状態の初期化
    viewHolder.checkBox.setChecked(false);
    viewHolder.checkBox.jumpDrawablesToCurrentState();

    //各種表示処理
    viewHolder.string.setText(notes.getString());

    //タグの左にあるアイコンの表示設定
    if(is_sorting) {
      Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_hamburger_icon_24dp);
      checkNotNull(drawable);
      drawable = drawable.mutate();
      viewHolder.checkBox.setButtonDrawable(drawable);
    }
    else {
      TypedValue value = new TypedValue();
      activity.getTheme().resolveAttribute(android.R.attr.listChoiceIndicatorMultiple, value, true);
      viewHolder.checkBox.setButtonDrawable(value.resourceId);
    }

    //並び替え中にドラッグしているアイテムが二重に表示されないようにする
    convertView.setVisibility(position == draggingPosition ? View.INVISIBLE : View.VISIBLE);

    return convertView;
  }
}
