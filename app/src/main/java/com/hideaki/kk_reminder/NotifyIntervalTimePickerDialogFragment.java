package com.hideaki.kk_reminder;

import android.app.Dialog;
import android.content.res.Resources;
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

public class NotifyIntervalTimePickerDialogFragment extends DialogFragment {

  private EditText time;
  private final NotifyIntervalEditFragment notifyIntervalEditFragment;

  NotifyIntervalTimePickerDialogFragment(NotifyIntervalEditFragment notifyIntervalEditFragment) {

    this.notifyIntervalEditFragment = notifyIntervalEditFragment;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

    View view = View.inflate(getContext(), R.layout.notify_interval_time_picker, null);
    final MainActivity activity = (MainActivity)getActivity();
    requireNonNull(activity);

    time = view.findViewById(R.id.time);
    setCursorDrawableColor(activity, time);
    time.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(
      activity.accentColor,
      PorterDuff.Mode.SRC_IN
    ));
    time.setText(String.valueOf(MainEditFragment.notifyInterval.getOrgTime()));
    time.setSelection(time.getText().length());

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

      if(time.getText().toString().isEmpty()) {
        time.setText(String.valueOf(MainEditFragment.notifyInterval.getOrgTime()));
      }
      if(Integer.parseInt(time.getText().toString()) < 999) {
        time.setText(String.valueOf(Integer.parseInt(time.getText().toString()) + 1));
        time.setSelection(time.getText().length());
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

      if(time.getText().toString().isEmpty()) {
        time.setText(String.valueOf(MainEditFragment.notifyInterval.getOrgTime()));
      }
      if(Integer.parseInt(time.getText().toString()) > -1) {
        time.setText(String.valueOf(Integer.parseInt(time.getText().toString()) - 1));
        time.setSelection(time.getText().length());
      }
    });

    final AlertDialog dialog = new AlertDialog.Builder(activity)
      .setPositiveButton(R.string.ok, (dialog1, which) -> {

        if(time.getText().toString().isEmpty()) {
          time.setText(String.valueOf(MainEditFragment.notifyInterval.getOrgTime()));
        }
        int setTime = Integer.parseInt(time.getText().toString());
        if(setTime > -2) {
          NotifyIntervalAdapter interval = MainEditFragment.notifyInterval;

          interval.setOrgTime(setTime);

          if(interval.getOrgTime() != 0) {
            Resources res = activity.getResources();
            String summary;
            if(LOCALE.equals(Locale.JAPAN)) {
              summary = activity.getString(R.string.unless_complete_task);
              if(interval.getHour() != 0) {
                summary += res.getQuantityString(
                  R.plurals.hour,
                  interval.getHour(),
                  interval.getHour()
                );
              }
              if(interval.getMinute() != 0) {
                summary += res.getQuantityString(
                  R.plurals.minute,
                  interval.getMinute(),
                  interval.getMinute()
                );
              }
              summary += activity.getString(R.string.per);
              if(interval.getOrgTime() == -1) {
                summary += activity.getString(R.string.infinite_times_notify);
              }
              else {
                summary += res.getQuantityString(R.plurals.times_notify, interval.getOrgTime(),
                  interval.getOrgTime()
                );
              }
            }
            else {
              summary = "Notify every ";
              if(interval.getHour() != 0) {
                summary += res.getQuantityString(
                  R.plurals.hour,
                  interval.getHour(),
                  interval.getHour()
                );
                if(!LOCALE.equals(Locale.JAPAN)) {
                  summary += " ";
                }
              }
              if(interval.getMinute() != 0) {
                summary += res.getQuantityString(
                  R.plurals.minute,
                  interval.getMinute(),
                  interval.getMinute()
                );
                if(!LOCALE.equals(Locale.JAPAN)) {
                  summary += " ";
                }
              }
              if(interval.getOrgTime() != -1) {
                summary += res.getQuantityString(R.plurals.times_notify, interval.getOrgTime(),
                  interval.getOrgTime()
                ) + " ";
              }
              summary += activity.getString(R.string.unless_complete_task);
            }

            notifyIntervalEditFragment.label.setSummary(summary);

            MainEditFragment.notifyInterval.setLabel(summary);
          }
          else {
            notifyIntervalEditFragment.label.setSummary(activity.getString(R.string.non_notify));

            MainEditFragment.notifyInterval.setLabel(activity.getString(R.string.none));
          }

          // 項目のタイトル部に現在の設定値を表示
          int orgTime = interval.getOrgTime();
          notifyIntervalEditFragment.time.setTitle(getResources().getQuantityString(
            R.plurals.times,
            orgTime,
            orgTime
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
    time.setOnFocusChangeListener((v, hasFocus) -> {

      if(hasFocus) {
        Window dialogWindow = dialog.getWindow();
        requireNonNull(dialogWindow);

        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
      }
    });
    time.requestFocus();

    return dialog;
  }
}
