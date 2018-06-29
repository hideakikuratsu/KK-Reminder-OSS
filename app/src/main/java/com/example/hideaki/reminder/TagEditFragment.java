package com.example.hideaki.reminder;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class TagEditFragment extends PreferenceFragment {

  private OnFragmentInteractionListener mListener;

  public static TagEditFragment newInstance() {
    return new TagEditFragment();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.tag_edit);
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
