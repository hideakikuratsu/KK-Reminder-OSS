package com.hideaki.kk_reminder;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import static com.hideaki.kk_reminder.UtilClass.LOCALE;
import static com.hideaki.kk_reminder.UtilClass.setCursorDrawableColor;
import static java.util.Objects.requireNonNull;

public class MinuteRepeatCountPickerDialogFragment extends DialogFragment {

  private EditText count;
  private final MinuteRepeatEditFragment minuteRepeatEditFragment;

  MinuteRepeatCountPickerDialogFragment(MinuteRepeatEditFragment minuteRepeatEditFragment) {

    this.minuteRepeatEditFragment = minuteRepeatEditFragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

    View view = View.inflate(getContext(), R.layout.minute_repeat_count_picker, null);
    final MainActivity activity = (MainActivity)getActivity();
    requireNonNull(activity);

    count = view.findViewById(R.id.count);
    setCursorDrawableColor(activity, count);
    count.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(
      activity.accentColor,
      PorterDuff.Mode.SRC_IN
    ));
    count.setText(String.valueOf(MainEditFragment.minuteRepeat.getOrgCount()));
    count.setSelection(count.getText().length());

    ImageView plus = view.findViewById(R.id.plus);
    plus.setColorFilter(activity.accentColor);
    GradientDrawable drawable = (GradientDrawable)plus.getBackground();
    drawable = (GradientDrawable)drawable.mutate();
    if(activity.isDarkMode) {
      drawable.setColor(Color.parseColor("#424242"));
    }
    else {
      drawable.setColor(Color.WHITE);
    }
    drawable.setStroke(3, activity.accentColor);
    drawable.setCornerRadius(8.0f);

    plus.setOnClickListener(v -> {

      if(count.getText().toString().isEmpty()) {
        count.setText(String.valueOf(MainEditFragment.minuteRepeat.getOrgCount()));
      }
      if(Integer.parseInt(count.getText().toString()) < 999) {
        count.setText(String.valueOf(Integer.parseInt(count.getText().toString()) + 1));
        count.setSelection(count.getText().length());
      }
    });

    ImageView minus = view.findViewById(R.id.minus);
    minus.setColorFilter(activity.accentColor);
    drawable = (GradientDrawable)minus.getBackground();
    drawable = (GradientDrawable)drawable.mutate();
    if(activity.isDarkMode) {
      drawable.setColor(Color.parseColor("#424242"));
    }
    else {
      drawable.setColor(Color.WHITE);
    }
    drawable.setStroke(3, activity.accentColor);
    drawable.setCornerRadius(8.0f);

    minus.setOnClickListener(v -> {

      if(count.getText().toString().isEmpty()) {
        count.setText(String.valueOf(MainEditFragment.minuteRepeat.getOrgCount()));
      }
      if(Integer.parseInt(count.getText().toString()) > 1) {
        count.setText(String.valueOf(Integer.parseInt(count.getText().toString()) - 1));
        count.setSelection(count.getText().length());
      }
    });

    final AlertDialog dialog = new AlertDialog.Builder(activity)
      .setPositiveButton(R.string.ok, (dialog1, which) -> {

        if(count.getText().toString().isEmpty()) {
          count.setText(String.valueOf(MainEditFragment.minuteRepeat.getOrgCount()));
        }
        int setCount = Integer.parseInt(count.getText().toString());
        if(setCount > 0) {
          MainEditFragment.minuteRepeat.setOrgCount(setCount);

          String interval = "";
          int hour = MainEditFragment.minuteRepeat.getHour();
          if(hour != 0) {
            interval += activity.getResources().getQuantityString(R.plurals.hour, hour, hour);
            if(!LOCALE.equals(Locale.JAPAN)) {
              interval += " ";
            }
          }
          int minute = MainEditFragment.minuteRepeat.getMinute();
          if(minute != 0) {
            interval +=
              activity.getResources().getQuantityString(R.plurals.minute, minute, minute);
            if(!LOCALE.equals(Locale.JAPAN)) {
              interval += " ";
            }
          }
          int count = MainEditFragment.minuteRepeat.getOrgCount();
          String label =
            activity.getResources().getQuantityString(R.plurals.repeat_minute_count_format,
              count, interval, count
            );
          MinuteRepeatEditFragment.labelStr = label;
          minuteRepeatEditFragment.label.setSummary(label);

          MainEditFragment.minuteRepeat.setLabel(label);

          // 項目のタイトル部に現在の設定値を表示
          minuteRepeatEditFragment.countPicker.setTitle(getResources().getQuantityString(
            R.plurals.times,
            count,
            count
          ));
        }
      })
      .setNeutralButton(R.string.cancel, (dialog12, which) -> {

      })
      .setView(view)
      .create();

    dialog.setOnShowListener(dialogInterface -> {

      dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.accentColor);
      dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(activity.accentColor);
    });

    // ダイアログ表示時にソフトキーボードを自動で表示
    count.setOnFocusChangeListener((v, hasFocus) -> {

      if(hasFocus) {
        Window dialogWindow = dialog.getWindow();
        requireNonNull(dialogWindow);

        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
      }
    });
    count.requestFocus();

    return dialog;
  }
}
