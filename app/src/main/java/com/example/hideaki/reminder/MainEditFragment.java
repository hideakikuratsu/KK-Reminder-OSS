package com.example.hideaki.reminder;

import android.content.Context;
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

public class MainEditFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

  public static final String ITEM = "ITEM";
  public static long ID = -1;

  private OnFragmentInteractionListener mListener;
  EditTextPreference detail;
  EditTextPreference notes;
  Item item = null;
  String detail_str;
  String notes_str;
  ActionBar actionBar;

  public static MainEditFragment newInstance() {
    MainEditFragment fragment = new MainEditFragment();

    Item item = new Item();
    Bundle args = new Bundle();
    args.putSerializable(ITEM, item);
    fragment.setArguments(args);

    return fragment;

  }  public static MainEditFragment newInstance(Item item) {
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
    actionBar.setTitle("編集");

    findPreference("tag").setOnPreferenceClickListener(this);
    findPreference("interval").setOnPreferenceClickListener(this);
    findPreference("repeat").setOnPreferenceClickListener(this);

    detail = (EditTextPreference)getPreferenceManager().findPreference("detail");
    notes = (EditTextPreference)getPreferenceManager().findPreference("notes");

    detail.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        detail_str = (String)newValue;
        detail.setTitle((String)newValue);
        return true;
      }
    });

    notes.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        notes_str = (String)newValue;
        notes.setTitle((String)newValue);
        return true;
      }
    });
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
        this.item.setDate((Calendar)MyPreference.final_cal.clone());
        this.item.setNotes(notes_str);
        ID++;
        try {
          mListener.insertDB(ID, this.item, MyDatabaseHelper.TODO_TABLE);
          MyExpandableListAdapter.children = MainActivity.getChildren(MyDatabaseHelper.TODO_TABLE);
        } catch(IOException e) {
          e.printStackTrace();
        } catch(ClassNotFoundException e) {
          e.printStackTrace();
        }
        MainActivity.ela.notifyDataSetChanged();
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
        transitionFragment(new TagEditFragment());
        return true;
      case "interval":
        transitionFragment(new IntervalEditFragment());
        return true;
      case "repeat":
        transitionFragment(new RepeatEditFragment());
        return true;
    }

    return false;

  }

  private void transitionFragment(PreferenceFragment next) {
    getFragmentManager()
        .beginTransaction()
        .replace(R.id.content, next)
        .addToBackStack(null)
        .commit();
  }

  public interface OnFragmentInteractionListener {
    void insertDB(long id, Object data, String table) throws IOException;
  }
}
