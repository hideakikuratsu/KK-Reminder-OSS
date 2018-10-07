package com.hideaki.kk_reminder;

import android.content.res.ColorStateList;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.CompoundButtonCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.hideaki.kk_reminder.UtilClass.NOTES_COMPARATOR;

public class NotesDoneListAdapter extends BaseAdapter {

  static List<Notes> notesList;
  private MainActivity activity;
  private NotesChecklistModeFragment fragment;
  ColorStateList colorStateList;

  NotesDoneListAdapter(MainActivity activity, NotesChecklistModeFragment fragment) {

    this.activity = activity;
    this.fragment = fragment;
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

      viewHolder.checkBox.setChecked(false);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

      if(!isChecked) {
        viewHolder.checkBox.jumpDrawablesToCurrentState();
        notes.setChecked(false);
        NotesTodoListAdapter.notesList.add(notes);
        Collections.sort(NotesTodoListAdapter.notesList, NOTES_COMPARATOR);
        notesList.remove(position);

        //todoListにおけるheaderの管理
        int todo_list_size = NotesTodoListAdapter.notesList.size();
        if(todo_list_size != 0 && fragment.sortableListView.getHeaderViewsCount() == 0) {
          fragment.sortableListView.addHeaderView(fragment.todoHeader);
          fragment.sortItem.setVisible(true);
        }
        else if(todo_list_size == 0 && fragment.sortableListView.getHeaderViewsCount() != 0) {
          fragment.sortableListView.removeHeaderView(fragment.todoHeader);
          fragment.sortItem.setVisible(false);
        }

        //doneListにおけるheaderの管理
        int done_list_size = notesList.size();
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
        fragment.notesTodoListAdapter.notifyDataSetChanged();

        if(activity.isItemExists(NotesChecklistModeFragment.item, MyDatabaseHelper.TODO_TABLE)) {
          activity.updateDB(NotesChecklistModeFragment.item, MyDatabaseHelper.TODO_TABLE);
        }
        else {
          MainEditFragment.item.setNotesList(new ArrayList<>(NotesChecklistModeFragment.item.getNotesList()));
        }
      }
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
      convertView = View.inflate(parent.getContext(), R.layout.notes_checklist_done_item_layout, null);

      viewHolder = new ViewHolder();
      viewHolder.notesItem = convertView.findViewById(R.id.notes_item);
      viewHolder.checkBox = convertView.findViewById(R.id.checkBox);
      CompoundButtonCompat.setButtonTintList(viewHolder.checkBox, colorStateList);
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
    viewHolder.checkBox.setChecked(true);
    viewHolder.checkBox.jumpDrawablesToCurrentState();

    //各種表示処理
    viewHolder.string.setText(notes.getString());

    return convertView;
  }
}