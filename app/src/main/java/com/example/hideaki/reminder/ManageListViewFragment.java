package com.example.hideaki.reminder;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ManageListViewFragment extends Fragment {

  private MainActivity activity;

  public static ManageListViewFragment newInstance() {

    return new ManageListViewFragment();
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

    View view = inflater.inflate(R.layout.listview, container, false);
    activity.listView = view.findViewById(R.id.listView);
    activity.listView.setAdapter(activity.manageListAdapter);
    activity.listView.setTextFilterEnabled(true);

    return view;
  }
}
