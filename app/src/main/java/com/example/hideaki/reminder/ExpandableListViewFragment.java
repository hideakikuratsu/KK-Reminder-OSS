package com.example.hideaki.reminder;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ExpandableListViewFragment extends Fragment {

  private MainActivity activity;

  public static ExpandableListViewFragment newInstance() {

    return new ExpandableListViewFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.expandable_listview, container, false);
    activity.expandableListView = view.findViewById(R.id.expandable_list);
    activity.expandableListView.setAdapter(activity.expandableListAdapter);
    activity.expandableListView.setTextFilterEnabled(true);

    return view;
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
  }
}
