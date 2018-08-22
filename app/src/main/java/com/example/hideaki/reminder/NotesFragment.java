package com.example.hideaki.reminder;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class NotesFragment extends Fragment {

  private ActionBar actionBar;
  private Item item = null;
  private EditText notes;

  public static NotesFragment newInstance(Item item) {

    NotesFragment fragment = new NotesFragment();

    Bundle args = new Bundle();
    args.putSerializable(MainEditFragment.ITEM, item);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    this.setHasOptionsMenu(true);

    Bundle args = getArguments();
    item = (Item)args.getSerializable(MainEditFragment.ITEM);

    actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(R.string.notes);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.notes, container, false);
    view.setBackgroundColor(getResources().getColor(android.R.color.background_light));
    view.setFocusableInTouchMode(true);
    view.requestFocus();
    view.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
          actionBar.setDisplayHomeAsUpEnabled(false);
          actionBar.setTitle(R.string.app_name);
        }
        return false;
      }
    });
    notes = view.findViewById(R.id.notes);

    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.notes_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    actionBar.setDisplayHomeAsUpEnabled(false);
    actionBar.setTitle(R.string.app_name);
    getFragmentManager().popBackStack();

    switch(item.getItemId()) {
      case R.id.done:
//        this.item.setNotes();
        return true;
    }
    return false;
  }
}
