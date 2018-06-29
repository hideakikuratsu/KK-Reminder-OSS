package com.example.hideaki.reminder;

import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainEditFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

  private static final String ITEM = "ITEM";

  private OnFragmentInteractionListener mListener;
  EditTextPreference detail;
  EditTextPreference notes;
  Item item = null;

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

    Bundle args = getArguments();
    item = (Item)args.getSerializable(ITEM);

    findPreference("tag").setOnPreferenceClickListener(this);
    findPreference("interval").setOnPreferenceClickListener(this);
    findPreference("repeat").setOnPreferenceClickListener(this);

    detail = (EditTextPreference)getPreferenceManager().findPreference("detail");
    notes = (EditTextPreference)getPreferenceManager().findPreference("notes");

    detail.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        item.setDetail((String)newValue);
        detail.setTitle((String)newValue);
        return true;
      }
    });

    notes.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
      @Override
      public boolean onPreferenceChange(Preference preference, Object newValue) {
        item.setNotes((String)newValue);
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
  }
}
