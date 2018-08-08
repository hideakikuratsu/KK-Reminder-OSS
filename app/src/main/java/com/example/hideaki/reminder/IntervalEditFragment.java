package com.example.hideaki.reminder;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.interval_edit);
    setHasOptionsMenu(true);

    actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
    actionBar.setTitle(getResources().getString(R.string.interval));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    view.setBackgroundColor(getResources().getColor(android.R.color.background_light));

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    actionBar.setTitle(R.string.edit);
    getFragmentManager().popBackStack();
    return super.onOptionsItemSelected(item);
  }
}
