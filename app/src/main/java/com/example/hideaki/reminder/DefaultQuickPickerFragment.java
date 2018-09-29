package com.example.hideaki.reminder;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultQuickPickerFragment extends PreferenceFragment {

  private MainActivity activity;

  public static DefaultQuickPickerFragment newInstance() {

    return new DefaultQuickPickerFragment();
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.default_quick_picker_fragment);
    setHasOptionsMenu(true);

  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    checkNotNull(view);

    view.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));

    Toolbar toolbar = activity.findViewById(R.id.toolbar_layout);
    activity.setSupportActionBar(toolbar);
    ActionBar actionBar = activity.getSupportActionBar();
    checkNotNull(actionBar);

    activity.drawerToggle.setDrawerIndicatorEnabled(false);
    actionBar.setHomeAsUpIndicator(activity.upArrow);
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(R.string.default_quick_picker);

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    getFragmentManager().popBackStack();
    return super.onOptionsItemSelected(item);
  }
}
