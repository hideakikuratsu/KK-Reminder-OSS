package com.hideaki.kk_reminder;

import android.annotation.SuppressLint;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceGroup;
import android.support.v7.preference.PreferenceGroupAdapter;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.RecyclerView;

public abstract class BasePreferenceFragment extends PreferenceFragmentCompat {

  private void setAllPreferencesToAvoidHavingExtraSpace(Preference preference) {

    preference.setIconSpaceReserved(false);
    if(preference instanceof PreferenceGroup) {
      for(int i = 0; i < ((PreferenceGroup)preference).getPreferenceCount(); i++) {
        setAllPreferencesToAvoidHavingExtraSpace(((PreferenceGroup)preference).getPreference(i));
      }
    }
  }

  @Override
  public void setPreferenceScreen(PreferenceScreen preferenceScreen) {

    if(preferenceScreen != null) {
      setAllPreferencesToAvoidHavingExtraSpace(preferenceScreen);
    }
    super.setPreferenceScreen(preferenceScreen);
  }

  @Override
  protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {

    return new PreferenceGroupAdapter(preferenceScreen) {

      @SuppressLint("RestrictedApi")
      @Override
      public void onPreferenceHierarchyChange(Preference preference) {
        if(preference != null) {
          setAllPreferencesToAvoidHavingExtraSpace(preference);
        }
        super.onPreferenceHierarchyChange(preference);
      }
    };
  }
}
