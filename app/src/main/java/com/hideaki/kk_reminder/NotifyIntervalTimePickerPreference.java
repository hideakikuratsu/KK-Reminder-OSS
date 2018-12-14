package com.hideaki.kk_reminder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.preference.Preference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.Locale;

import static com.hideaki.kk_reminder.UtilClass.LOCALE;

public class NotifyIntervalTimePickerPreference extends Preference implements TextWatcher {

  private MainActivity activity;
  private EditText time;
  private String oldString = "";

  public NotifyIntervalTimePickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
    activity = (MainActivity)context;
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    return View.inflate(getContext(), R.layout.notify_interval_time_picker, null);
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    if(MainEditFragment.notifyInterval.getWhich_setted() == (1 << 7)) {
      time = view.findViewById(R.id.time);
      time.requestFocus();
      time.setText(String.valueOf(MainEditFragment.notifyInterval.getOrg_time()));
      time.setSelection(time.getText().length());
      time.addTextChangedListener(this);

      ImageView plus = view.findViewById(R.id.plus);
      plus.setColorFilter(activity.accent_color);
      GradientDrawable drawable = (GradientDrawable)plus.getBackground();
      drawable = (GradientDrawable)drawable.mutate();
      drawable.setStroke(3, activity.accent_color);
      drawable.setCornerRadius(8.0f);

      plus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          MainEditFragment.notifyInterval.addOrgTime(1);
          time.setText(String.valueOf(MainEditFragment.notifyInterval.getOrg_time()));

          NotifyInterval interval = MainEditFragment.notifyInterval;
          if(interval.getOrg_time() != 0) {
            Resources res = activity.getResources();
            String summary;
            if(LOCALE.equals(Locale.JAPAN)) {
              summary = activity.getString(R.string.unless_complete_task);
              if(interval.getHour() != 0) {
                summary += res.getQuantityString(R.plurals.hour, interval.getHour(), interval.getHour());
              }
              if(interval.getMinute() != 0) {
                summary += res.getQuantityString(R.plurals.minute, interval.getMinute(), interval.getMinute());
              }
              summary += activity.getString(R.string.per);
              if(interval.getOrg_time() == -1) {
                summary += activity.getString(R.string.infinite_times_notify);
              }
              else {
                summary += res.getQuantityString(R.plurals.times_notify, interval.getOrg_time(),
                    interval.getOrg_time());
              }
            }
            else {
              summary = "Notify every ";
              if(interval.getHour() != 0) {
                summary += res.getQuantityString(R.plurals.hour, interval.getHour(), interval.getHour());
                if(!LOCALE.equals(Locale.JAPAN)) summary += " ";
              }
              if(interval.getMinute() != 0) {
                summary += res.getQuantityString(R.plurals.minute, interval.getMinute(), interval.getMinute());
                if(!LOCALE.equals(Locale.JAPAN)) summary += " ";
              }
              if(interval.getOrg_time() != -1) {
                summary += res.getQuantityString(R.plurals.times_notify, interval.getOrg_time(),
                    interval.getOrg_time()) + " ";
              }
              summary += activity.getString(R.string.unless_complete_task);
            }

            NotifyIntervalEditFragment.label.setSummary(summary);

            MainEditFragment.notifyInterval.setLabel(summary);
          }
          else {
            NotifyIntervalEditFragment.label.setSummary(activity.getString(R.string.non_notify));

            MainEditFragment.notifyInterval.setLabel(activity.getString(R.string.none));
          }
        }
      });

      ImageView minus = view.findViewById(R.id.minus);
      minus.setColorFilter(activity.accent_color);
      drawable = (GradientDrawable)minus.getBackground();
      drawable = (GradientDrawable)drawable.mutate();
      drawable.setStroke(3, activity.accent_color);
      drawable.setCornerRadius(8.0f);

      minus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if(MainEditFragment.notifyInterval.getOrg_time() > -1) {
            MainEditFragment.notifyInterval.addOrgTime(-1);
            time.setText(String.valueOf(MainEditFragment.notifyInterval.getOrg_time()));

            NotifyInterval interval = MainEditFragment.notifyInterval;
            if(interval.getOrg_time() != 0) {
              Resources res = activity.getResources();
              String summary;
              if(LOCALE.equals(Locale.JAPAN)) {
                summary = activity.getString(R.string.unless_complete_task);
                if(interval.getHour() != 0) {
                  summary += res.getQuantityString(R.plurals.hour, interval.getHour(), interval.getHour());
                }
                if(interval.getMinute() != 0) {
                  summary += res.getQuantityString(R.plurals.minute, interval.getMinute(), interval.getMinute());
                }
                summary += activity.getString(R.string.per);
                if(interval.getOrg_time() == -1) {
                  summary += activity.getString(R.string.infinite_times_notify);
                }
                else {
                  summary += res.getQuantityString(R.plurals.times_notify, interval.getOrg_time(),
                      interval.getOrg_time());
                }
              }
              else {
                summary = "Notify every ";
                if(interval.getHour() != 0) {
                  summary += res.getQuantityString(R.plurals.hour, interval.getHour(), interval.getHour());
                  if(!LOCALE.equals(Locale.JAPAN)) summary += " ";
                }
                if(interval.getMinute() != 0) {
                  summary += res.getQuantityString(R.plurals.minute, interval.getMinute(), interval.getMinute());
                  if(!LOCALE.equals(Locale.JAPAN)) summary += " ";
                }
                if(interval.getOrg_time() != -1) {
                  summary += res.getQuantityString(R.plurals.times_notify, interval.getOrg_time(),
                      interval.getOrg_time()) + " ";
                }
                summary += activity.getString(R.string.unless_complete_task);
              }

              NotifyIntervalEditFragment.label.setSummary(summary);

              MainEditFragment.notifyInterval.setLabel(summary);
            }
            else {
              NotifyIntervalEditFragment.label.setSummary(activity.getString(R.string.non_notify));

              MainEditFragment.notifyInterval.setLabel(activity.getString(R.string.none));
            }
          }
        }
      });
    }
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {}

  @Override
  public void afterTextChanged(Editable s) {

    if(!s.toString().equals("") && !s.toString().equals("-") && !oldString.equals(s.toString())
        && NotifyIntervalEditFragment.custom.isChecked()) {
      oldString = s.toString();
      int time = Integer.parseInt(s.toString());
      if(time > -2) {
        MainEditFragment.notifyInterval.setOrg_time(time);

        NotifyInterval interval = MainEditFragment.notifyInterval;
        if(interval.getOrg_time() != 0) {
          Resources res = activity.getResources();
          String summary;
          if(LOCALE.equals(Locale.JAPAN)) {
            summary = activity.getString(R.string.unless_complete_task);
            if(interval.getHour() != 0) {
              summary += res.getQuantityString(R.plurals.hour, interval.getHour(), interval.getHour());
            }
            if(interval.getMinute() != 0) {
              summary += res.getQuantityString(R.plurals.minute, interval.getMinute(), interval.getMinute());
            }
            summary += activity.getString(R.string.per);
            if(interval.getOrg_time() == -1) {
              summary += activity.getString(R.string.infinite_times_notify);
            }
            else {
              summary += res.getQuantityString(R.plurals.times_notify, interval.getOrg_time(),
                  interval.getOrg_time());
            }
          }
          else {
            summary = "Notify every ";
            if(interval.getHour() != 0) {
              summary += res.getQuantityString(R.plurals.hour, interval.getHour(), interval.getHour());
              if(!LOCALE.equals(Locale.JAPAN)) summary += " ";
            }
            if(interval.getMinute() != 0) {
              summary += res.getQuantityString(R.plurals.minute, interval.getMinute(), interval.getMinute());
              if(!LOCALE.equals(Locale.JAPAN)) summary += " ";
            }
            if(interval.getOrg_time() != -1) {
              summary += res.getQuantityString(R.plurals.times_notify, interval.getOrg_time(),
                  interval.getOrg_time()) + " ";
            }
            summary += activity.getString(R.string.unless_complete_task);
          }

          NotifyIntervalEditFragment.label.setSummary(summary);

          MainEditFragment.notifyInterval.setLabel(summary);
        }
        else {
          NotifyIntervalEditFragment.label.setSummary(activity.getString(R.string.non_notify));

          MainEditFragment.notifyInterval.setLabel(activity.getString(R.string.none));
        }
      }
    }
  }
}