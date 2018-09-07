package com.example.hideaki.reminder;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ExpandableListViewFragment extends Fragment {

  private MainActivity activity;

  public static ExpandableListViewFragment newInstance() {

    return new ExpandableListViewFragment();
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
    activity.drawerLayout.closeDrawer(GravityCompat.START);
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.expandable_listview, container, false);
    view.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));
    activity.expandableListView = view.findViewById(R.id.expandable_list);
    activity.expandableListView.setAdapter(activity.expandableListAdapter);
    activity.expandableListView.setTextFilterEnabled(true);

    return view;
  }
}
