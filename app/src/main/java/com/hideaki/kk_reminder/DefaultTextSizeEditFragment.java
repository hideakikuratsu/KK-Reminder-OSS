package com.hideaki.kk_reminder;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_TEXT_SIZE;
import static com.hideaki.kk_reminder.UtilClass.INT_GENERAL;

public class DefaultTextSizeEditFragment extends BasePreferenceFragmentCompat implements MyCheckBoxPreference.MyCheckBoxPreferenceCheckedChangeListener {

  private MainActivity activity;
  private PreferenceScreen rootPreferenceScreen;
  private CheckBoxPreference small;
  private CheckBoxPreference medium;
  private CheckBoxPreference large;
  private Preference model;

  public static DefaultTextSizeEditFragment newInstance() {

    return new DefaultTextSizeEditFragment();
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
  }

  @Override
  public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {

    addPreferencesFromResource(R.xml.default_text_size_edit);
    setHasOptionsMenu(true);

    rootPreferenceScreen = getPreferenceScreen();
    small = (CheckBoxPreference)findPreference("small");
    medium = (CheckBoxPreference)findPreference("medium");
    large = (CheckBoxPreference)findPreference("large");
    model = findPreference("model");

    ((MyCheckBoxPreference)small).setOnMyCheckBoxPreferenceCheckedChangeListener(this);
    ((MyCheckBoxPreference)medium).setOnMyCheckBoxPreferenceCheckedChangeListener(this);
    ((MyCheckBoxPreference)large).setOnMyCheckBoxPreferenceCheckedChangeListener(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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
    actionBar.setTitle(R.string.default_detail_text_size);

    //チェック状態の初期化
    small.setChecked(false);
    medium.setChecked(false);
    large.setChecked(false);

    switch(activity.which_text_size) {

      case 0: {

        small.setChecked(true);
        break;
      }
      case 1: {

        medium.setChecked(true);
        break;
      }
      case 2: {

        large.setChecked(true);
        break;
      }
    }

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    FragmentManager manager = getFragmentManager();
    checkNotNull(manager);
    manager.popBackStack();
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onCheckedChange(String key, boolean checked) {

    small.setChecked(false);
    medium.setChecked(false);
    large.setChecked(false);

    int which_set = activity.which_text_size;
    switch(key) {

      case "small": {

        small.setChecked(true);
        if(which_set != 0) {

          resetDefaultTextSizeModelPreference();
          activity.setIntGeneralInSharedPreferences(DEFAULT_TEXT_SIZE, 0);
        }

        break;
      }
      case "medium": {

        medium.setChecked(true);
        if(which_set != 1) {

          resetDefaultTextSizeModelPreference();
          activity.setIntGeneralInSharedPreferences(DEFAULT_TEXT_SIZE, 1);
        }

        break;
      }
      case "large": {

        large.setChecked(true);
        if(which_set != 2) {

          resetDefaultTextSizeModelPreference();
          activity.setIntGeneralInSharedPreferences(DEFAULT_TEXT_SIZE, 2);
        }

        break;
      }
    }
  }

  private void resetDefaultTextSizeModelPreference() {

    rootPreferenceScreen.removePreference(model);
    rootPreferenceScreen.addPreference(model);
  }
}