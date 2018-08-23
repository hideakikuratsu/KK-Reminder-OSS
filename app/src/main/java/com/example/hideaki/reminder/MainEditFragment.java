package com.example.hideaki.reminder;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.Calendar;

public class MainEditFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener,
    Preference.OnPreferenceChangeListener {

  public static final String ITEM = "ITEM";

  static OnFragmentInteractionListener mListener;
  static EditTextPreference detail;
  static EditTextPreference notes;
  static PreferenceScreen day_repeat_item;
  static PreferenceScreen minute_repeat_item;
  static Item item;
  static String detail_str;
  static String notes_str;
  static ActionBar actionBar;
  static Context direct_boot_context;
  static Calendar final_cal;
  static DayRepeat dayRepeat;
  static MinuteRepeat minuteRepeat;
  static boolean is_edit;
  static android.app.FragmentManager fragmentManager;
  private DialogFragment dialog = new DateAlterDialogFragment();

  public static MainEditFragment newInstance() {

    MainEditFragment fragment = new MainEditFragment();

    Item item = new Item();
    is_edit = false;
    detail_str = "";
    notes_str = "";
    dayRepeat = new DayRepeat();
    minuteRepeat = new MinuteRepeat();
    final_cal = Calendar.getInstance();
    Bundle args = new Bundle();
    args.putSerializable(ITEM, item);
    fragment.setArguments(args);

    return fragment;
  }

  public static MainEditFragment newInstance(Item item) {

    MainEditFragment fragment = new MainEditFragment();

    is_edit = true;
    detail_str = item.getDetail();
    notes_str = item.getNotes();
    dayRepeat = item.getDayRepeat().clone();
    minuteRepeat = item.getMinuteRepeat().clone();
    final_cal = (Calendar)item.getDate().clone();
    Bundle args = new Bundle();
    args.putSerializable(ITEM, item);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.main_edit);
    setHasOptionsMenu(true);

    Bundle args = getArguments();
    item = (Item)args.getSerializable(ITEM);

    actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(R.string.edit);

    findPreference("tag").setOnPreferenceClickListener(this);
    findPreference("interval").setOnPreferenceClickListener(this);

    detail = (EditTextPreference)findPreference("detail");
    detail.setTitle(detail_str);
    detail.setOnPreferenceChangeListener(this);
    notes = (EditTextPreference)findPreference("notes");
    notes.setTitle(notes_str);
    notes.setOnPreferenceChangeListener(this);
    day_repeat_item = (PreferenceScreen)findPreference("repeat_day_unit");
    day_repeat_item.setOnPreferenceClickListener(this);
    minute_repeat_item = (PreferenceScreen)findPreference("repeat_minute_unit");
    minute_repeat_item.setOnPreferenceClickListener(this);

    fragmentManager = getFragmentManager();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    view.setBackgroundColor(getResources().getColor(android.R.color.background_light));
    view.setFocusableInTouchMode(true);
    view.requestFocus();
    view.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
          actionBar.setDisplayHomeAsUpEnabled(false);
          actionBar.setTitle(R.string.app_name);
        }
        return false;
      }
    });

    if(dayRepeat.getLabel() == null) day_repeat_item.setSummary(R.string.none);
    else day_repeat_item.setSummary(dayRepeat.getLabel());

    if(minuteRepeat.getLabel() == null) minute_repeat_item.setSummary(R.string.none);
    else minute_repeat_item.setSummary(minuteRepeat.getLabel());

    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.main_edit_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch(item.getItemId()) {
      case R.id.done:
        if(is_edit && this.item.getDate().getTimeInMillis() != final_cal.getTimeInMillis()) {
          dialog.show(((MainActivity)getActivity()).getSupportFragmentManager(), "date_alter_dialog");
        }
        else {
          actionBar.setDisplayHomeAsUpEnabled(false);
          actionBar.setTitle(R.string.app_name);
          fragmentManager.popBackStack();

          this.item.setDetail(detail_str);
          this.item.setDate((Calendar)final_cal.clone());
          this.item.setNotes(notes_str);
          if(dayRepeat.getSetted() != 0) {
            if(dayRepeat.getSetted() == (1 << 0)) dayRepeat.dayClear();
            else if(dayRepeat.getSetted() == (1 << 1)) dayRepeat.weekClear();
            else if(dayRepeat.getSetted() == (1 << 2)) {
              if(dayRepeat.isDays_of_month_setted()) dayRepeat.daysOfMonthClear();
              else dayRepeat.onTheMonthClear();
            }
            else if(dayRepeat.getSetted() == (1 << 3)) dayRepeat.yearClear();
          }
          else {
            dayRepeat.clear();
          }
          this.item.setDayRepeat(dayRepeat.clone());
          minuteRepeat.setCount(minuteRepeat.getOrg_count());
          minuteRepeat.setDuration(minuteRepeat.getOrgDuration());
          this.item.setMinuteRepeat(minuteRepeat.clone());
          if(this.item.isAlarm_stopped()) this.item.setAlarm_stopped(false);

          if(mListener.isItemExists(this.item, MyDatabaseHelper.TODO_TABLE)) {
            mListener.notifyDataSetChanged();
            try {
              mListener.updateDB(this.item, MyDatabaseHelper.TODO_TABLE);
            } catch(IOException e) {
              e.printStackTrace();
            }
          }
          else {
            mListener.addChildren(this.item);
            mListener.notifyDataSetChanged();

            try {
              mListener.insertDB(this.item, MyDatabaseHelper.TODO_TABLE);
            } catch(IOException e) {
              e.printStackTrace();
            }
          }

          //データベースに挿入を行ったら、そのデータベースを端末暗号化ストレージへコピーする
          if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            direct_boot_context = getActivity().createDeviceProtectedStorageContext();
            direct_boot_context.moveDatabaseFrom(getActivity(), MyDatabaseHelper.TODO_TABLE);
          }

          mListener.deleteAlarm(this.item);
          mListener.setAlarm(this.item);
        }
        return true;
      case android.R.id.home:
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(R.string.app_name);
        fragmentManager.popBackStack();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    if(context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener)context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {

    super.onDetach();
    mListener = null;
  }

  @Override
  public boolean onPreferenceClick(Preference preference) {

    switch(preference.getKey()) {
      case "tag":
        transitionFragment(TagEditFragment.newInstance());
        return true;
      case "interval":
        transitionFragment(IntervalEditFragment.newInstance());
        return true;
      case "repeat_day_unit":
        transitionFragment(DayRepeatEditFragment.newInstance());
        return true;
      case "repeat_minute_unit":
        transitionFragment(MinuteRepeatEditFragment.newInstance());
        return true;
    }
    return false;
  }

  @Override
  public boolean onPreferenceChange(Preference preference, Object newValue) {

    switch(preference.getKey()) {
      case "detail":
        detail_str = (String)newValue;
        detail.setTitle((String)newValue);
        return true;
      case "notes":
        notes_str = (String)newValue;
        notes.setTitle((String)newValue);
        return true;
    }
    return false;
  }

  private void transitionFragment(PreferenceFragment next) {

    getFragmentManager()
        .beginTransaction()
        .replace(android.R.id.content, next)
        .addToBackStack(null)
        .commit();
  }

  public interface OnFragmentInteractionListener {

    void insertDB(Item item, String table) throws IOException;
    void updateDB(Item item, String table) throws IOException;
    boolean isItemExists(Item item, String table);
    void addChildren(Item item);
    void setAlarm(Item item);
    void deleteAlarm(Item item);
    void notifyDataSetChanged();
  }
}
