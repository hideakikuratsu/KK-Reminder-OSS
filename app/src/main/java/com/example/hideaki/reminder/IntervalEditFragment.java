package com.example.hideaki.reminder;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class IntervalEditFragment extends PreferenceFragment {

  private OnFragmentInteractionListener mListener;

  public static IntervalEditFragment newInstance() {
    return new IntervalEditFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.interval_edit);
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

  public interface OnFragmentInteractionListener {
  }
}
