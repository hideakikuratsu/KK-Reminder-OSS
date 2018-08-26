package com.example.hideaki.reminder;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class IntervalEditFragment extends PreferenceFragment {

  private ActionBar actionBar;

  public static IntervalEditFragment newInstance() {

    return new IntervalEditFragment();
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    MainActivity activity = (MainActivity)context;

    Toolbar toolbar = activity.findViewById(R.id.toolbar_layout);
    activity.setSupportActionBar(toolbar);
    actionBar = activity.getSupportActionBar();
    assert actionBar != null;

    actionBar.setHomeAsUpIndicator(activity.upArrow);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.interval_edit);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    assert view != null;

    view.setBackgroundColor(getResources().getColor(android.R.color.background_light));
    actionBar.setTitle(R.string.interval);

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    getFragmentManager().popBackStack();
    return super.onOptionsItemSelected(item);
  }
}
