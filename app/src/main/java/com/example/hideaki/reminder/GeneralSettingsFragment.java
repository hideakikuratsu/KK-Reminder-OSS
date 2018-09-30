package com.example.hideaki.reminder;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import static com.google.common.base.Preconditions.checkNotNull;

public class GeneralSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

  static final String TAG = GeneralSettingsFragment.class.getSimpleName();

  private MainActivity activity;
  private PreferenceScreen rootPreferenceScreen;
  private PreferenceScreen defaultNewTask;
  private PreferenceScreen defaultQuickPicker;
  private PreferenceScreen primaryColor;
  private PreferenceScreen secondaryColor;
  private PreferenceScreen backup;
  private PreferenceScreen about;

  public static GeneralSettingsFragment newInstance() {

    return new GeneralSettingsFragment();
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
    activity.drawerLayout.closeDrawer(GravityCompat.START);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.general_settings_edit);

    rootPreferenceScreen = getPreferenceScreen();
    defaultNewTask = (PreferenceScreen)findPreference("new_task");
    defaultQuickPicker = (PreferenceScreen)findPreference("quick_picker");
    primaryColor = (PreferenceScreen)findPreference("primary_color");
    secondaryColor = (PreferenceScreen)findPreference("secondary_color");
    backup = (PreferenceScreen)findPreference("backup");
    about = (PreferenceScreen)findPreference("this_app");

    defaultNewTask.setOnPreferenceClickListener(this);
    defaultQuickPicker.setOnPreferenceClickListener(this);
    primaryColor.setOnPreferenceClickListener(this);
    secondaryColor.setOnPreferenceClickListener(this);
    backup.setOnPreferenceClickListener(this);
    about.setOnPreferenceClickListener(this);
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

    activity.drawerToggle.setDrawerIndicatorEnabled(true);
    actionBar.setTitle(R.string.settings);

    ListView list = view.findViewById(android.R.id.list);
    list.setDivider(null);

    return view;
  }

  @Override
  public boolean onPreferenceClick(Preference preference) {

    switch(preference.getKey()) {

      case "new_task": {

        activity.showMainEditFragment(activity.generalSettings.getItem(), TAG);
        return true;
      }
      case "quick_picker": {

        transitionFragment(DefaultQuickPickerFragment.newInstance());
        return true;
      }
      case "primary_color": {

        ColorPickerListAdapter.is_general_settings = true;
        activity.showColorPickerListViewFragment(TAG);
        return true;
      }
      case "secondary_color": {

        ColorPickerListAdapter.is_general_settings = true;
        activity.generalSettings.getTheme().setColor_primary(false);
        activity.showColorPickerListViewFragment(TAG);
        return true;
      }
      case "backup": {

        transitionFragment(BackupAndRestoreFragment.newInstance());
        return true;
      }
      case "this_app": {
        return true;
      }

    }
    return false;
  }

  private void transitionFragment(PreferenceFragment next) {

    FragmentManager manager = getFragmentManager();
    manager
        .beginTransaction()
        .remove(this)
        .add(R.id.content, next)
        .addToBackStack(null)
        .commit();
  }
}