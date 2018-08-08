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

public class RepeatCustomDaysOfMonthPickerFragment extends PreferenceFragment {

  private ActionBar actionBar;

  public static RepeatCustomDaysOfMonthPickerFragment newInstance() {

    return new RepeatCustomDaysOfMonthPickerFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.repeat_custom_days_of_month_item);
    setHasOptionsMenu(true);

    actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
    actionBar.setTitle(getResources().getString(R.string.days_of_month));
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    view.setBackgroundColor(getResources().getColor(android.R.color.background_light));

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    getFragmentManager().popBackStack();
    return super.onOptionsItemSelected(item);
  }
}