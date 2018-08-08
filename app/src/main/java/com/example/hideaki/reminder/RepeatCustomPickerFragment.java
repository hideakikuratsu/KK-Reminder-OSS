package com.example.hideaki.reminder;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class RepeatCustomPickerFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

  private ActionBar actionBar;
  private static PreferenceScreen rootPreferenceScreen;
  private static Preference picker;
  private static Preference week;
  private static CheckBoxPreference days_of_month;
  private static CheckBoxPreference on_the_month;
  private static Preference year;

  public static RepeatCustomPickerFragment newInstance() {

    return new RepeatCustomPickerFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.repeat_custom_item);
    setHasOptionsMenu(true);

    actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
    actionBar.setTitle(getResources().getString(R.string.repeat));

    rootPreferenceScreen = getPreferenceScreen();
    picker = findPreference("picker");
    week = findPreference("week");
    days_of_month = (CheckBoxPreference)findPreference("days_of_month");
    days_of_month.setChecked(false);
    days_of_month.setOnPreferenceClickListener(this);
    on_the_month = (CheckBoxPreference)findPreference("on_the_month");
    on_the_month.setChecked(false);
    on_the_month.setOnPreferenceClickListener(this);
    year = findPreference("year");
    rootPreferenceScreen.removeAll();
    addPickerPreference();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    view.setBackgroundColor(getResources().getColor(android.R.color.background_light));

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    if(RepeatCustomPickerPreference.week) MainEditFragment.repeat.weekClear();
    else if(RepeatCustomPickerPreference.month) MainEditFragment.repeat.monthClear();
    else if(RepeatCustomPickerPreference.year) MainEditFragment.repeat.yearClear();
    else MainEditFragment.repeat.dayClear();

    getFragmentManager().popBackStack();
    return super.onOptionsItemSelected(item);
  }

  public static void addPickerPreference() {
    rootPreferenceScreen.addPreference(picker);
  }

  public static void addWeekPreference() {
    rootPreferenceScreen.addPreference(week);
  }

  public static void addDaysOfMonthPreference() {
    rootPreferenceScreen.addPreference(days_of_month);
  }

  public static void addOnTheMonthPreference() {
    rootPreferenceScreen.addPreference(on_the_month);
  }

  public static void addYearPreference() {
    rootPreferenceScreen.addPreference(year);
  }

  public static void removeWeekPreference() {
    rootPreferenceScreen.removePreference(week);
  }

  public static void removeDaysOfMonthPreference() {
    rootPreferenceScreen.removePreference(days_of_month);
  }

  public static void removeOnTheMonthPreference() {
    rootPreferenceScreen.removePreference(on_the_month);
  }

  public static void removeYearPreference() {
    rootPreferenceScreen.removePreference(year);
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
      case "days_of_month":
        MainEditFragment.repeat.setOrdinal_number(0);
        MainEditFragment.repeat.setOn_the_month(null);
        if(days_of_month.isChecked()) on_the_month.setChecked(false);
        else days_of_month.setChecked(true);
        transitionFragment(RepeatCustomDaysOfMonthPickerFragment.newInstance());
        return true;
      case "on_the_month":
        MainEditFragment.repeat.setDays_of_month(0);
        if(on_the_month.isChecked()) days_of_month.setChecked(false);
        else on_the_month.setChecked(true);
        transitionFragment(RepeatCustomOnTheMonthPickerFragment.newInstance());
        return true;
    }
    return false;
  }
}