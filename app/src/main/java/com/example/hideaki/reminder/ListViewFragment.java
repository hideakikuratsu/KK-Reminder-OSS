package com.example.hideaki.reminder;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ListViewFragment extends Fragment {

  private MainActivity activity;

  public static ListViewFragment newInstance() {

    return new ListViewFragment();
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
    activity.drawerLayout.closeDrawer(GravityCompat.START);
    if(activity.detail != null) {
      activity.showMainEditFragment(activity.detail);
      activity.detail = null;
    }
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.listview, container, false);
    view.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));
    view.setFocusableInTouchMode(true);
    view.requestFocus();
    view.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
          if(activity.listAdapter.actionMode != null) {
            activity.listAdapter.actionMode.finish();
          }
        }
        return false;
      }
    });
    activity.listAdapter = new MyListAdapter(activity.getNonScheduledItem(MyDatabaseHelper.TODO_TABLE), activity);
    activity.listView = view.findViewById(R.id.listView);
    activity.listView.setDragListener(activity.listAdapter.dragListener);
    activity.listView.setSortable(true);
    activity.listView.setAdapter(activity.listAdapter);
    activity.listView.setTextFilterEnabled(true);

    return view;
  }
}
