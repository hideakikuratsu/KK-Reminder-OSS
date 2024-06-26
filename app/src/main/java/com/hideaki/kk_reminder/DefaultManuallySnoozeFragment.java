package com.hideaki.kk_reminder;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceScreen;

import static com.hideaki.kk_reminder.UtilClass.LOCALE;
import static java.util.Objects.requireNonNull;

public class DefaultManuallySnoozeFragment extends BasePreferenceFragmentCompat {

  private MainActivity activity;
  PreferenceScreen label;

  public static DefaultManuallySnoozeFragment newInstance() {

    return new DefaultManuallySnoozeFragment();
  }

  @Override
  public void onAttach(@NonNull Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
  }

  @Override
  public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {

    addPreferencesFromResource(R.xml.default_manually_snooze);
    setHasOptionsMenu(true);

    label = (PreferenceScreen)findPreference("label");
    label.setOnPreferenceClickListener(preference -> {

      DefaultManuallySnoozePickerDialogFragment dialog =
        new DefaultManuallySnoozePickerDialogFragment(
          DefaultManuallySnoozeFragment.this
        );
      dialog.show(activity.getSupportFragmentManager(), "default_manually_snooze_picker");
      return true;
    });
  }

  @Override
  public View onCreateView(
    LayoutInflater inflater,
    @Nullable ViewGroup container,
    @Nullable Bundle savedInstanceState
  ) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    requireNonNull(view);

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

    activity.drawerToggle.setDrawerIndicatorEnabled(false);
    actionBar.setHomeAsUpIndicator(activity.upArrow);
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(R.string.default_manually_snooze);

    // 時間表示の初期化
    int hour = activity.snoozeDefaultHour;
    int minute = activity.snoozeDefaultMinute;
    String summary = "";
    if(hour != 0) {
      summary += getResources().getQuantityString(R.plurals.hour, hour, hour);
      if(!LOCALE.equals(Locale.JAPAN)) {
        summary += " ";
      }
    }
    if(minute != 0) {
      summary += getResources().getQuantityString(R.plurals.minute, minute, minute);
      if(!LOCALE.equals(Locale.JAPAN)) {
        summary += " ";
      }
    }
    label.setTitle(summary);

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {

    FragmentManager manager = requireNonNull(activity.getSupportFragmentManager());
    manager.popBackStack();
    return super.onOptionsItemSelected(item);
  }
}