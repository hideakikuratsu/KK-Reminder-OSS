package com.example.hideaki.reminder;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

  private OnFragmentInteractionListener mListener;
  private EditTextPreference detail;
  private EditTextPreference notes;
  private Item item = null;
  private String detail_str;
  private String notes_str;
  private ActionBar actionBar;
  private Context direct_boot_context;
  static Calendar final_cal = Calendar.getInstance();
  static Repeat repeat = new Repeat();

  public static MainEditFragment newInstance() {

    MainEditFragment fragment = new MainEditFragment();

    Item item = new Item();
    Bundle args = new Bundle();
    args.putSerializable(ITEM, item);
    fragment.setArguments(args);

    return fragment;
  }

  public static MainEditFragment newInstance(Item item) {

    MainEditFragment fragment = new MainEditFragment();

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
    actionBar.setTitle(getResources().getString(R.string.edit));

    findPreference("tag").setOnPreferenceClickListener(this);
    findPreference("interval").setOnPreferenceClickListener(this);
    findPreference("repeat").setOnPreferenceClickListener(this);

    detail = (EditTextPreference)getPreferenceManager().findPreference("detail");
    notes = (EditTextPreference)getPreferenceManager().findPreference("notes");

    detail.setOnPreferenceChangeListener(this);
    notes.setOnPreferenceChangeListener(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = super.onCreateView(inflater, container, savedInstanceState);
    view.setBackgroundColor(getResources().getColor(android.R.color.background_light));

    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.main_edit_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    actionBar.setDisplayHomeAsUpEnabled(false);
    actionBar.setTitle(R.string.app_name);
    getFragmentManager().popBackStack();
    switch(item.getItemId()) {
      case R.id.done:
        this.item.setDetail(detail_str);
        this.item.setDate((Calendar)final_cal.clone());
        this.item.setNotes(notes_str);
        try {
          mListener.insertDB(this.item, MyDatabaseHelper.TODO_TABLE);
        } catch(IOException e) {
          e.printStackTrace();
        }

        //データベースに挿入を行ったら、そのデータベースを端末暗号化ストレージへコピーする
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
          direct_boot_context = getActivity().createDeviceProtectedStorageContext();
          direct_boot_context.moveDatabaseFrom(getActivity(), MyDatabaseHelper.TODO_TABLE);
        }

        mListener.addChildren(this.item);
        mListener.notifyDataSetChanged();
        mListener.setAlarm(this.item);
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
      case "repeat":
        transitionFragment(RepeatEditFragment.newInstance());
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
    void insertDB(Object data, String table) throws IOException;
    void addChildren(Item item);
    void setAlarm(Item item);
    void notifyDataSetChanged();
  }
}
