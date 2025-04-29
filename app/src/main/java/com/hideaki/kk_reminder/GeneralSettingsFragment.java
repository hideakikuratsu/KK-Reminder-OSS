package com.hideaki.kk_reminder;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import androidx.transition.Fade;
import androidx.transition.Transition;

import static com.hideaki.kk_reminder.UtilClass.IS_DARK_MODE;
import static com.hideaki.kk_reminder.UtilClass.IS_DARK_THEME_FOLLOW_SYSTEM;
import static com.hideaki.kk_reminder.UtilClass.PLAY_SLIDE_ANIMATION;
import static com.hideaki.kk_reminder.UtilClass.setViewPaddingBasedOnCutout;
import static java.util.Objects.requireNonNull;

public class GeneralSettingsFragment extends BasePreferenceFragmentCompat
  implements Preference.OnPreferenceClickListener,
  MyCheckBoxPreference.MyCheckBoxPreferenceCheckedChangeListener {

  static final String TAG = GeneralSettingsFragment.class.getSimpleName();

  private MainActivity activity;
  private CheckBoxPreference animation;
  private CheckBoxPreference darkTheme;
  private CheckBoxPreference darkThemeFollowSystem;

  public static GeneralSettingsFragment newInstance() {

    return new GeneralSettingsFragment();
  }

  @Override
  public void onAttach(@NonNull Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
    if(activity.drawerLayout != null) {
      activity.drawerLayout.closeDrawer(GravityCompat.START);
    }
    else {
      FragmentManager manager = requireNonNull(activity.getSupportFragmentManager());
      manager
        .beginTransaction()
        .remove(this)
        .commit();
    }
  }

  @Override
  public void onCreatePreferences(@Nullable Bundle savedInstanceState, String rootKey) {

    addPreferencesFromResource(R.xml.general_settings_edit);

    PreferenceScreen defaultControlTime = findPreference("control_time");
    PreferenceScreen defaultTextSize = findPreference("text_size");
    PreferenceScreen defaultNewTask = findPreference("new_task");
    PreferenceScreen setAll = findPreference("set_all");
    PreferenceScreen manuallySnooze = findPreference("manually_snooze");
    animation = findPreference("animation");
    PreferenceCategory adsCategory = findPreference("ads_category");
    PreferenceScreen disableAds = findPreference("disable_ads");
//    PreferenceScreen primaryColor = findPreference("primary_color");
    PreferenceScreen secondaryColor = findPreference("secondary_color");
    darkTheme = findPreference("dark_theme");
    darkThemeFollowSystem = findPreference("dark_theme_follow_system");
    PreferenceScreen backup = findPreference("backup");
    PreferenceScreen about = findPreference("this_app");

    defaultControlTime.setOnPreferenceClickListener(this);
    defaultTextSize.setOnPreferenceClickListener(this);
    defaultNewTask.setOnPreferenceClickListener(this);
    setAll.setOnPreferenceClickListener(this);
    manuallySnooze.setOnPreferenceClickListener(this);
    ((MyCheckBoxPreference)animation).setOnMyCheckBoxPreferenceCheckedChangeListener(this);
    disableAds.setOnPreferenceClickListener(this);
//    primaryColor.setOnPreferenceClickListener(this);
    secondaryColor.setOnPreferenceClickListener(this);
    ((MyCheckBoxPreference)darkTheme).setOnMyCheckBoxPreferenceCheckedChangeListener(this);
    ((MyCheckBoxPreference)darkThemeFollowSystem)
      .setOnMyCheckBoxPreferenceCheckedChangeListener(this);
    backup.setOnPreferenceClickListener(this);
    about.setOnPreferenceClickListener(this);

    if(activity.isPremium) {
      getPreferenceScreen().removePreference(adsCategory);
    }
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    super.onViewCreated(view, savedInstanceState);

    // 設定項目間の区切り線の非表示
    setDivider(new ColorDrawable(Color.TRANSPARENT));
    setDividerHeight(0);
  }

  @NonNull
  @Override
  public View onCreateView(
    @NonNull LayoutInflater inflater,
    @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState
  ) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    requireNonNull(view);

    setViewPaddingBasedOnCutout(view);

    if(activity.isDarkMode) {
      view.setBackgroundColor(activity.backgroundMaterialDarkColor);
    }
    else {
      view.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));
    }

    Toolbar toolbar = activity.findViewById(R.id.toolbar_layout);
    activity.setSupportActionBar(toolbar);
    ActionBar actionBar = activity.getSupportActionBar();
    requireNonNull(actionBar);

    activity.drawerToggle.setDrawerIndicatorEnabled(true);
    actionBar.setTitle(R.string.settings);

    // チェック状態の初期化
    animation.setChecked(activity.isPlaySlideAnimation);
    new Handler(Looper.getMainLooper()).post(() -> {

      ((MyCheckBoxPreference)animation).setMySummary(
          activity.getString(R.string.play_slide_animation_summary)
      );
      ((MyCheckBoxPreference)animation).showMySummary();
    });

    darkTheme.setChecked(activity.isDarkMode);

    darkThemeFollowSystem.setChecked(activity.isDarkThemeFollowSystem);

    return view;
  }

  @Override
  public boolean onPreferenceClick(Preference preference) {

    switch(preference.getKey()) {

      case "control_time": {
        transitionFragment(DefaultControlTimeEditFragment.newInstance());
        return true;
      }
      case "text_size": {
        transitionFragment(DefaultTextSizeEditFragment.newInstance());
        return true;
      }
      case "new_task": {
        activity.showMainEditFragment(activity.generalSettings.getItem());
        return true;
      }
      case "set_all": {
        transitionFragment(SetAllFragment.newInstance());
        return true;
      }
      case "manually_snooze": {
        transitionFragment(DefaultManuallySnoozeFragment.newInstance());
        return true;
      }
      case "disable_ads": {
        activity.promotionDialog.show();
        return true;
      }
      case "primary_color": {
        ColorPickerListAdapter.isGeneralSettings = true;
        activity.showColorPickerListViewFragment();
        return true;
      }
      case "secondary_color": {
        ColorPickerListAdapter.isGeneralSettings = true;
        activity.generalSettings.getTheme().setIsColorPrimary(false);
        activity.showColorPickerListViewFragment();
        return true;
      }
      case "backup": {
        BackupAndRestoreFragment backupAndRestoreFragment =
            BackupAndRestoreFragment.newInstance();
        transitionFragment(backupAndRestoreFragment);

        return true;
      }
      case "this_app": {
        activity.showAboutThisAppFragment();
        return true;
      }

    }
    return false;
  }

  private void transitionFragment(PreferenceFragmentCompat next) {

    Transition transition = new Fade()
      .setDuration(300);
    this.setExitTransition(transition);
    next.setEnterTransition(transition);
    FragmentManager manager = requireNonNull(activity.getSupportFragmentManager());
    manager
      .beginTransaction()
      .remove(this)
      .add(R.id.content, next)
      .addToBackStack(null)
      .commit();
  }

  @Override
  public void onCheckedChange(String key, boolean checked) {

    switch(key) {
      case "animation": {
        animation.setChecked(checked);
        if(activity.isPlaySlideAnimation != checked) {
          activity.setBooleanGeneralInSharedPreferences(
            PLAY_SLIDE_ANIMATION, checked
          );
        }
        break;
      }
      case "dark_theme": {
        darkTheme.setChecked(checked);
        if(activity.isDarkMode != checked) {
          activity.setBooleanGeneralInSharedPreferences(IS_DARK_MODE, checked);
        }
        initDarkMode();
        break;
      }
      case "dark_theme_follow_system": {
        darkThemeFollowSystem.setChecked(checked);
        if(activity.isDarkThemeFollowSystem != checked) {
          activity.setBooleanGeneralInSharedPreferences(IS_DARK_THEME_FOLLOW_SYSTEM, checked);
        }
        initDarkMode();
        break;
      }
      default: {
        throw new IllegalStateException("Such a key not exist!: " + key);
      }
    }
  }

  private void initDarkMode() {

    int currentNightMode =
      getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    if(!activity.isDarkThemeFollowSystem) {
      if(activity.isDarkMode && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
      }
      else if(!activity.isDarkMode && currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
      }
    }
    else {
      if(activity.isDarkMode && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
        activity.setBooleanGeneralInSharedPreferences(IS_DARK_MODE, false);
      }
      else if(!activity.isDarkMode && currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
        activity.setBooleanGeneralInSharedPreferences(IS_DARK_MODE, true);
      }
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
      activity.recreate();
    }
  }
}