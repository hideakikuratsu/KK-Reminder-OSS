package com.example.hideaki.reminder;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class TagEditFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

  private MainActivity activity;
  private PreferenceScreen rootPreferenceScreen;
  private MyCheckBoxPreference none;
  private Map<String, Long> keyList;

  public static TagEditFragment newInstance() {

    return new TagEditFragment();
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.tag_edit);
    setHasOptionsMenu(true);

    rootPreferenceScreen = getPreferenceScreen();
    none = (MyCheckBoxPreference)findPreference("none");
    none.setOnPreferenceClickListener(this);

    //GeneralSettingsに登録されているTagを表示
    keyList = new HashMap<>();
    keyList.put("none", 0L);
    for(Tag tag : activity.generalSettings.getTagList()) {
      MyCheckBoxPreference checkBoxPreference = new MyCheckBoxPreference(activity);
      checkBoxPreference.setKey(tag.getName());
      keyList.put(tag.getName(), tag.getId());
      checkBoxPreference.setTitle(tag.getName());
      checkBoxPreference.setOnPreferenceClickListener(this);
      rootPreferenceScreen.addPreference(checkBoxPreference);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

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
    actionBar.setTitle(R.string.tag);

    //チェック状態の初期化
    long which_tag_belongs = MainEditFragment.item.getWhich_tag_belongs();
    for(String key : keyList.keySet()) {
      MyCheckBoxPreference checkBoxPreference = (MyCheckBoxPreference)findPreference(key);
      checkBoxPreference.setChecked(false);
      if(which_tag_belongs == keyList.get(key)) {
        System.out.println(key + ": " + which_tag_belongs);
        checkBoxPreference.setChecked(true);
      }
    }

    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.tag_edit_menu, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch(item.getItemId()) {
      case R.id.edit: {
        return true;
      }
      case android.R.id.home: {
        getFragmentManager().popBackStack();
        return true;
      }
      default: {
        return super.onOptionsItemSelected(item);
      }
    }
  }

  @Override
  public boolean onPreferenceClick(Preference preference) {

    for(String key : keyList.keySet()) {
      ((MyCheckBoxPreference)findPreference(key)).setChecked(false);
      if(preference.getKey().equals(key)) {
        MainEditFragment.item.setWhich_tag_belongs(keyList.get(key));
      }
    }
    ((MyCheckBoxPreference)preference).setChecked(true);
    return false;
  }
}
