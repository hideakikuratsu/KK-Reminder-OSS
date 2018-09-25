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

public class DoneListViewFragment extends Fragment {

  static final String TAG = DoneListViewFragment.class.getSimpleName();
  private MainActivity activity;

  public static DoneListViewFragment newInstance() {

    return new DoneListViewFragment();
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
    activity.drawerLayout.closeDrawer(GravityCompat.START);
    if(activity.detail != null) {
      activity.showMainEditFragment(activity.detail, TAG);
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

          if(activity.doneListAdapter.actionMode != null) {
            activity.doneListAdapter.actionMode.finish();
          }
        }

        return false;
      }
    });

    DoneListAdapter.itemList = activity.getDoneItem();
    DoneListAdapter.checked_item_num = 0;
    DoneListAdapter.order = activity.order;
    activity.listView = view.findViewById(R.id.listView);
    activity.listView.setAdapter(activity.doneListAdapter);
    activity.listView.setTextFilterEnabled(true);

    return view;
  }
}
