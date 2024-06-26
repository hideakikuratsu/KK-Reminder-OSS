package com.hideaki.kk_reminder;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceScreen;

import static com.hideaki.kk_reminder.UtilClass.DEFAULT_VIBRATION_PATTERN;
import static com.hideaki.kk_reminder.UtilClass.getPxFromDp;
import static com.hideaki.kk_reminder.UtilClass.getRegularizedVibrationStr;
import static com.hideaki.kk_reminder.UtilClass.getVibrationPattern;
import static com.hideaki.kk_reminder.UtilClass.setCursorDrawableColor;
import static java.util.Objects.requireNonNull;

public class VibrationEditFragment extends BasePreferenceFragmentCompat {

  private MainActivity activity;
  private PreferenceScreen label;
  private String vibrationStr;

  public static VibrationEditFragment newInstance() {

    return new VibrationEditFragment();
  }

  @Override
  public void onAttach(@NonNull Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
  }

  @Override
  public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {

    addPreferencesFromResource(R.xml.vibration);
    setHasOptionsMenu(true);

    vibrationStr = MainEditFragment.item.getVibrationPattern();
    label = (PreferenceScreen)findPreference("label");
    label.setOnPreferenceClickListener(preference -> {

      // ダイアログに表示するEditTextの設定
      LinearLayout linearLayout = new LinearLayout(activity);
      linearLayout.setOrientation(LinearLayout.VERTICAL);
      final EditText editText = new EditText(activity);
      setCursorDrawableColor(activity, editText);
      editText.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(
        activity.accentColor,
        PorterDuff.Mode.SRC_IN
      ));
      editText.setText(vibrationStr);
      editText.setSelection(editText.getText().length());
      editText.setHint(R.string.vibration_hint);
      editText.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
      editText.setLayoutParams(new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
      ));
      linearLayout.addView(editText);
      int paddingPx = getPxFromDp(activity, 20);
      linearLayout.setPadding(paddingPx, 0, paddingPx, 0);

      // バイブレーションのパターンを設定するダイアログを表示
      final AlertDialog dialog = new AlertDialog.Builder(activity)
        .setTitle(R.string.default_vibration)
        .setView(linearLayout)
        .setPositiveButton(R.string.ok, (dialog1, which) -> {

          vibrationStr = editText.getText().toString();
          vibrationStr = getRegularizedVibrationStr(vibrationStr);
          label.setTitle(vibrationStr);
          if(vibrationStr.equals(DEFAULT_VIBRATION_PATTERN)) {
            label.setSummary(R.string.default_vibration_hint);
          }
          else {
            label.setSummary(R.string.custom_vibration_hint);
          }
          long[] vibrationPattern = getVibrationPattern(vibrationStr);
          Vibrator vibrator = (Vibrator)activity.getSystemService(Context.VIBRATOR_SERVICE);
          requireNonNull(vibrator);
          if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect effect =
              VibrationEffect.createWaveform(vibrationPattern, -1);
            vibrator.vibrate(effect);
          }
          else {
            vibrator.vibrate(vibrationPattern, -1);
          }

          MainEditFragment.item.setVibrationPattern(vibrationStr);
        })
        .setNeutralButton(R.string.cancel, (dialog12, which) -> {

        })
        .create();

      dialog.setOnShowListener(dialogInterface -> {

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.accentColor);
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(activity.accentColor);
      });

      dialog.show();

      // ダイアログ表示時にソフトキーボードを自動で表示
      editText.setOnFocusChangeListener((v, hasFocus) -> {

        if(hasFocus) {
          Window dialogWindow = dialog.getWindow();
          requireNonNull(dialogWindow);

          dialogWindow.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
          );
        }
      });
      editText.requestFocus();

      return true;
    });
  }

  @Override
  public View onCreateView(
    LayoutInflater inflater,
    ViewGroup container,
    Bundle savedInstanceState
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
    actionBar.setTitle(R.string.default_vibration);

    // ラベル表示の初期化
    label.setTitle(vibrationStr);
    if(vibrationStr.equals(DEFAULT_VIBRATION_PATTERN)) {
      label.setSummary(R.string.default_vibration_hint);
    }
    else {
      label.setSummary(R.string.custom_vibration_hint);
    }

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {

    FragmentManager manager = requireNonNull(activity.getSupportFragmentManager());
    manager.popBackStack();
    return super.onOptionsItemSelected(item);
  }
}
