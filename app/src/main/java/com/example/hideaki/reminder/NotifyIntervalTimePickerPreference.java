package com.example.hideaki.reminder;

import android.content.Context;
import android.preference.Preference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

public class NotifyIntervalTimePickerPreference extends Preference implements TextWatcher {

  private MainActivity activity;
  private EditText time;
  private String oldString = "";

  public NotifyIntervalTimePickerPreference(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    activity = (MainActivity)getContext();
    super.onCreateView(parent);
    return View.inflate(getContext(), R.layout.notify_interval_time_picker, null);
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    if(MainEditFragment.notifyInterval.getWhich_setted() == (1 << 7)) {
      time = view.findViewById(R.id.time);
      time.setText(String.valueOf(MainEditFragment.notifyInterval.getOrg_time()));
      time.addTextChangedListener(this);

      ImageView plus = view.findViewById(R.id.plus);
      plus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          MainEditFragment.notifyInterval.addOrgTime(1);
          time.setText(String.valueOf(MainEditFragment.notifyInterval.getOrg_time()));

          NotifyInterval interval = MainEditFragment.notifyInterval;
          if(interval.getOrg_time() != 0) {
            String summary = activity.getString(R.string.unless_complete_task);
            if(interval.getHour() != 0) {
              summary += interval.getHour() + activity.getString(R.string.hour);
            }
            if(interval.getMinute() != 0) {
              summary += interval.getMinute() + activity.getString(R.string.minute);
            }
            summary += activity.getString(R.string.per);
            if(interval.getOrg_time() == -1) {
              summary += activity.getString(R.string.infinite_times_notify);
            }
            else {
              summary += activity.getString(R.string.max) + interval.getOrg_time()
                  + activity.getString(R.string.times_notify);
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
      minus.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if(MainEditFragment.notifyInterval.getOrg_time() > -1) {
            MainEditFragment.notifyInterval.addOrgTime(-1);
            time.setText(String.valueOf(MainEditFragment.notifyInterval.getOrg_time()));

            NotifyInterval interval = MainEditFragment.notifyInterval;
            if(interval.getOrg_time() != 0) {
              String summary = activity.getString(R.string.unless_complete_task);
              if(interval.getHour() != 0) {
                summary += interval.getHour() + activity.getString(R.string.hour);
              }
              if(interval.getMinute() != 0) {
                summary += interval.getMinute() + activity.getString(R.string.minute);
              }
              summary += activity.getString(R.string.per);
              if(interval.getOrg_time() == -1) {
                summary += activity.getString(R.string.infinite_times_notify);
              }
              else {
                summary += activity.getString(R.string.max) + interval.getOrg_time()
                    + activity.getString(R.string.times_notify);
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
          String summary = activity.getString(R.string.unless_complete_task);
          if(interval.getHour() != 0) {
            summary += interval.getHour() + activity.getString(R.string.hour);
          }
          if(interval.getMinute() != 0) {
            summary += interval.getMinute() + activity.getString(R.string.minute);
          }
          summary += activity.getString(R.string.per);
          if(interval.getOrg_time() == -1) {
            summary += activity.getString(R.string.infinite_times_notify);
          }
          else {
            summary += activity.getString(R.string.max) + interval.getOrg_time()
                + activity.getString(R.string.times_notify);
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