package com.example.hideaki.reminder;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class RepeatEditFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

  private ActionBar actionBar;
  private CheckBoxPreference never;
  private CheckBoxPreference everyday;
  private CheckBoxPreference weekday;
  private CheckBoxPreference everyweek;
  private CheckBoxPreference everymonth;
  private CheckBoxPreference everyyear;
  private CheckBoxPreference custom;

  public static RepeatEditFragment newInstance() {

    return new RepeatEditFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.repeat_edit);
    setHasOptionsMenu(true);

    actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
    actionBar.setTitle(getResources().getString(R.string.repeat));

    never = (CheckBoxPreference)findPreference("never");
    everyday = (CheckBoxPreference)findPreference("everyday");
    weekday = (CheckBoxPreference)findPreference("weekday");
    everyweek = (CheckBoxPreference)findPreference("everyweek");
    everymonth = (CheckBoxPreference)findPreference("everymonth");
    everyyear = (CheckBoxPreference)findPreference("everyyear");
    custom = (CheckBoxPreference)findPreference("custom");
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

  private void transitionFragment(PreferenceFragment next) {
    getFragmentManager()
        .beginTransaction()
        .replace(android.R.id.content, next)
        .addToBackStack(null)
        .commit();
  }

  @Override
  public boolean onPreferenceClick(Preference preference) {

    switch(preference.getKey()) {
      case "never":
        return true;
      case "everyday":
        return true;
      case "weekday":
        return true;
      case "everyweek":
        return true;
      case "everymonth":
        return true;
      case "everyyear":
        return true;
      case "custom":
        transitionFragment(RepeatCustomPickerFragment.newInstance());
        return true;
    }
    return false;
  }
}
