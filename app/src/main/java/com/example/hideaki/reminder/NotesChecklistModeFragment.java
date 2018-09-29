package com.example.hideaki.reminder;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.hideaki.reminder.UtilClass.ITEM;
import static com.example.hideaki.reminder.UtilClass.NOTES_COMPARATOR;
import static com.google.common.base.Preconditions.checkNotNull;

public class NotesChecklistModeFragment extends Fragment {

  static final String TAG = NotesChecklistModeFragment.class.getSimpleName();
  static Item item = null;
  private MainActivity activity;
  NotesTodoListAdapter notesTodoListAdapter;
  NotesDoneListAdapter notesDoneListAdapter;
  SortableListView sortableListView;
  View todoHeader;
  ListView listView;
  View doneHeader;
  private MenuItem editModeItem;
  private ActionBar actionBar;
  MenuItem deleteItem;
  MenuItem unselectItem;

  public static NotesChecklistModeFragment newInstance(Item item) {

    NotesChecklistModeFragment fragment = new NotesChecklistModeFragment();

    Bundle args = new Bundle();
    args.putSerializable(ITEM, item);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    this.setHasOptionsMenu(true);

    Bundle args = getArguments();
    item = (Item)args.getSerializable(ITEM);

    notesTodoListAdapter = new NotesTodoListAdapter(activity, this);
    notesDoneListAdapter = new NotesDoneListAdapter(activity, this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    if(MainEditFragment.is_popping) {
      getFragmentManager().popBackStack();
    }
    View view = inflater.inflate(R.layout.notes_checklist_layout, container, false);
    view.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));
    view.setFocusableInTouchMode(true);
    view.requestFocus();
    view.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {

          if(NotesTodoListAdapter.is_sorting) {
            new AlertDialog.Builder(activity)
                .setTitle(R.string.is_sorting_title)
                .setMessage(R.string.is_sorting_message)
                .show();

            return true;
          }

          MainEditFragment.is_popping = true;
        }

        return false;
      }
    });

    //NotesTodoListとNotesDoneListの保持するnotesListの初期化
    NotesTodoListAdapter.notesList = new ArrayList<>();
    NotesDoneListAdapter.notesList = new ArrayList<>();
    for(Notes notes : item.getNotesList()) {
      if(notes.isChecked()) NotesDoneListAdapter.notesList.add(notes);
      else NotesTodoListAdapter.notesList.add(notes);
    }
    Collections.sort(NotesTodoListAdapter.notesList, NOTES_COMPARATOR);
    Collections.sort(NotesDoneListAdapter.notesList, NOTES_COMPARATOR);

    //NotesTodoListの初期化
    NotesTodoListAdapter.is_sorting = false;
    sortableListView = view.findViewById(R.id.notes_todo);
    todoHeader = View.inflate(activity, R.layout.notes_todo_list_header, null);
    if(NotesTodoListAdapter.notesList.size() != 0 && sortableListView.getHeaderViewsCount() == 0
        && !NotesTodoListAdapter.is_sorting) {
      sortableListView.addHeaderView(todoHeader);
    }
    sortableListView.setDragListener(notesTodoListAdapter.dragListener);
    sortableListView.setSortable(true);
    sortableListView.setAdapter(notesTodoListAdapter);

    //NotesDoneListの初期化
    listView = view.findViewById(R.id.notes_done);
    doneHeader = View.inflate(activity, R.layout.notes_done_list_header, null);
    if(NotesDoneListAdapter.notesList.size() != 0 && listView.getHeaderViewsCount() == 0) {
      listView.addHeaderView(doneHeader);
    }
    listView.setAdapter(notesDoneListAdapter);

    Toolbar toolbar = activity.findViewById(R.id.toolbar_layout);
    activity.setSupportActionBar(toolbar);
    actionBar = activity.getSupportActionBar();
    checkNotNull(actionBar);

    activity.drawerToggle.setDrawerIndicatorEnabled(false);
    actionBar.setHomeAsUpIndicator(activity.upArrow);
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(R.string.notes);

    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.notes_checklist_mode_menu, menu);
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu) {

    super.onPrepareOptionsMenu(menu);
    editModeItem = menu.findItem(R.id.edit_mode);
    deleteItem = menu.findItem(R.id.delete_checked_items);
    unselectItem = menu.findItem(R.id.unselect_all_items);
    if(NotesDoneListAdapter.notesList.size() != 0) {
      deleteItem.setVisible(true);
      unselectItem.setVisible(true);
    }
    else {
      deleteItem.setVisible(false);
      unselectItem.setVisible(false);
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch(item.getItemId()) {

      case R.id.delete_checked_items: {

        new AlertDialog.Builder(activity)
            .setTitle(R.string.delete_checked_items)
            .setMessage(R.string.delete_checked_items_message)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {

                deleteItem.setVisible(false);
                unselectItem.setVisible(false);

                NotesDoneListAdapter.notesList = new ArrayList<>();
                if(listView.getHeaderViewsCount() != 0) {
                  listView.removeHeaderView(doneHeader);
                }
                notesDoneListAdapter.notifyDataSetChanged();

                NotesChecklistModeFragment.item.setNotesList(new ArrayList<>(NotesTodoListAdapter.notesList));

                if(activity.isItemExists(NotesChecklistModeFragment.item, MyDatabaseHelper.TODO_TABLE)) {
                  activity.updateDB(NotesChecklistModeFragment.item, MyDatabaseHelper.TODO_TABLE);
                }
                else {
                  MainEditFragment.item.setNotesList(new ArrayList<>(NotesChecklistModeFragment.item.getNotesList()));
                }
              }
            })
            .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {}
            })
            .show();

        return true;
      }
      case R.id.unselect_all_items: {

        deleteItem.setVisible(false);
        unselectItem.setVisible(false);

        for(Notes notes : NotesDoneListAdapter.notesList) {
          notes.setChecked(false);
          NotesTodoListAdapter.notesList.add(notes);
        }
        Collections.sort(NotesTodoListAdapter.notesList, NOTES_COMPARATOR);
        NotesDoneListAdapter.notesList = new ArrayList<>();
        notesTodoListAdapter.notifyDataSetChanged();
        notesDoneListAdapter.notifyDataSetChanged();

        if(sortableListView.getHeaderViewsCount() == 0) {
          sortableListView.addHeaderView(todoHeader);
        }

        if(listView.getHeaderViewsCount() != 0) {
          listView.removeHeaderView(doneHeader);
        }

        if(activity.isItemExists(NotesChecklistModeFragment.item, MyDatabaseHelper.TODO_TABLE)) {
          activity.updateDB(NotesChecklistModeFragment.item, MyDatabaseHelper.TODO_TABLE);
        }
        else {
          MainEditFragment.item.setNotesList(new ArrayList<>(NotesChecklistModeFragment.item.getNotesList()));
        }
        return true;
      }
      case R.id.edit_mode: {

        NotesChecklistModeFragment.item.setChecklist_mode(false);
        activity.showNotesFragment(NotesChecklistModeFragment.item, TAG);
        return true;
      }
      case R.id.sort: {

        NotesTodoListAdapter.is_sorting = !NotesTodoListAdapter.is_sorting;
        if(NotesTodoListAdapter.is_sorting) {
          actionBar.setDisplayHomeAsUpEnabled(false);
          editModeItem.setVisible(false);
          deleteItem.setVisible(false);
          unselectItem.setVisible(false);
          if(sortableListView.getHeaderViewsCount() != 0) {
            sortableListView.removeHeaderView(todoHeader);
          }
          listView.setVisibility(View.GONE);
        }
        else {
          actionBar.setDisplayHomeAsUpEnabled(true);
          editModeItem.setVisible(true);
          if(NotesDoneListAdapter.notesList.size() != 0) {
            deleteItem.setVisible(true);
            unselectItem.setVisible(true);
          }
          if(NotesTodoListAdapter.notesList.size() != 0 && sortableListView.getHeaderViewsCount() == 0) {
            sortableListView.addHeaderView(todoHeader);
          }
          listView.setVisibility(View.VISIBLE);


          List<Notes> doneList = NotesDoneListAdapter.notesList;
          List<Integer> doneOrderList = new ArrayList<>();
          for(Notes notes : doneList) {
            doneOrderList.add(notes.getOrder());
          }

          List<Integer> sortedIndex = new ArrayList<>();
          int all_list_size = NotesChecklistModeFragment.item.getNotesList().size();
          for(int i = 0; i < all_list_size; i++) {
            sortedIndex.add(i);
          }

          int done_list_size = doneList.size();
          for(int i = 0; i < done_list_size; i++) {
            sortedIndex.remove(doneOrderList.get(i));
          }
          Collections.sort(sortedIndex);

          List<Notes> notesList = NotesTodoListAdapter.notesList;
          int size = notesList.size();
          boolean is_updated = false;
          for(int i = 0; i < size; i++) {
            Notes notes = notesList.get(i);
            if(notes.getOrder() != sortedIndex.get(i)) {
              notes.setOrder(sortedIndex.get(i));
              is_updated = true;
            }
          }

          notesList = new ArrayList<>(NotesTodoListAdapter.notesList);
          notesList.addAll(NotesDoneListAdapter.notesList);
          Collections.sort(notesList, NOTES_COMPARATOR);

          if(is_updated) {
            NotesChecklistModeFragment.item.setNotesList(new ArrayList<>(notesList));
            if(activity.isItemExists(NotesChecklistModeFragment.item, MyDatabaseHelper.TODO_TABLE)) {
              activity.updateDB(NotesChecklistModeFragment.item, MyDatabaseHelper.TODO_TABLE);
            }
            else {
              MainEditFragment.item.setNotesList(new ArrayList<>(NotesChecklistModeFragment.item.getNotesList()));
            }
          }
        }

        notesTodoListAdapter.notifyDataSetChanged();
        notesDoneListAdapter.notifyDataSetChanged();
        return true;
      }
      case android.R.id.home: {

        MainEditFragment.is_popping = true;
        getFragmentManager().popBackStack();

        return true;
      }
    }
    return false;
  }
}