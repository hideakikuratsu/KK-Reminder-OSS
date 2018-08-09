package com.example.hideaki.reminder;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import java.util.Calendar;

public class RepeatCustomPickerPreference extends Preference {

  private final String[] SCALE_LIST = {"日", "週", "月", "年"};

  static boolean day = false;
  static boolean week = false;
  static boolean month = false;
  static boolean year = false;
  private NumberPicker interval;
  private NumberPicker scale;
  private int mask_num;

  public RepeatCustomPickerPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    View view = View.inflate(getContext(), R.layout.repeat_custom_picker, null);
    return view;
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    //intervalの実装
    interval = view.findViewById(R.id.interval);
    interval.setDisplayedValues(null);
    interval.setMaxValue(1000);
    interval.setMinValue(1);
    if(MainEditFragment.repeat.getInterval() == 0) {
      MainEditFragment.repeat.setInterval(1);
    }
    interval.setValue(MainEditFragment.repeat.getInterval());
    interval.setOnScrollListener(new NumberPicker.OnScrollListener() {
      @Override
      public void onScrollStateChange(NumberPicker view, int scrollState) {
        switch(scrollState) {
          case SCROLL_STATE_IDLE:
            MainEditFragment.repeat.setInterval(interval.getValue());
        }
      }
    });

    //scaleの実装
    scale = view.findViewById(R.id.scale);
    scale.setDisplayedValues(null);
    scale.setMaxValue(SCALE_LIST.length);
    scale.setMinValue(1);
    if(MainEditFragment.repeat.getScale() == 0) {
      MainEditFragment.repeat.setScale(1);
      MainEditFragment.repeat.setDay(true);
      day = true;
      week = false;
      month = false;
      year = false;
    }
    scale.setValue(MainEditFragment.repeat.getScale());
    scale.setDisplayedValues(SCALE_LIST);
    if(MainEditFragment.repeat.getScale() == 2) RepeatCustomPickerFragment.addWeekPreference();
    else if(MainEditFragment.repeat.getScale() == 3) {
      RepeatCustomPickerFragment.addDaysOfMonthPreference();
      RepeatCustomPickerFragment.addOnTheMonthPreference();
    }
    else if(MainEditFragment.repeat.getScale() == 4) RepeatCustomPickerFragment.addYearPreference();
    scale.setOnScrollListener(new NumberPicker.OnScrollListener() {
      @Override
      public void onScrollStateChange(NumberPicker view, int scrollState) {
        switch(scrollState) {
          case SCROLL_STATE_IDLE:
            MainEditFragment.repeat.setScale(scale.getValue());
            if(MainEditFragment.repeat.getScale() == 1) {

              MainEditFragment.repeat.setDay(true);

              if(week) {
                RepeatCustomPickerFragment.removeWeekPreference();
                week = false;
              }
              else if(month) {
                RepeatCustomPickerFragment.removeDaysOfMonthPreference();
                RepeatCustomPickerFragment.removeOnTheMonthPreference();
                month = false;
              }
              else if(year) {
                RepeatCustomPickerFragment.removeYearPreference();
                year = false;
              }

              day = true;
            }
            else if(MainEditFragment.repeat.getScale() == 2) {

              if(day) day = false;
              else if(month) {
                RepeatCustomPickerFragment.removeDaysOfMonthPreference();
                RepeatCustomPickerFragment.removeOnTheMonthPreference();
                month = false;
              }
              else if(year) {
                RepeatCustomPickerFragment.removeYearPreference();
                year = false;
              }

              RepeatCustomPickerFragment.addWeekPreference();
              week = true;
            }
            else if(MainEditFragment.repeat.getScale() == 3) {

              if(day) day = false;
              else if(week) {
                RepeatCustomPickerFragment.removeWeekPreference();
                week = false;
              }
              else if(year) {
                RepeatCustomPickerFragment.removeYearPreference();
                year = false;
              }

              RepeatCustomPickerFragment.addDaysOfMonthPreference();
              RepeatCustomPickerFragment.addOnTheMonthPreference();
              month = true;

              if(!MainEditFragment.repeat.isMonth_setted()) {
                mask_num = MainEditFragment.final_cal.get(Calendar.DAY_OF_MONTH);
                RepeatCustomDaysOfMonthPickerPreference.days_of_month |= (1 << (mask_num - 1));
                MainEditFragment.repeat.setDays_of_month(
                    RepeatCustomDaysOfMonthPickerPreference.days_of_month
                );
                MainEditFragment.repeat.setMonth_setted(true);
                RepeatCustomPickerFragment.days_of_month.setChecked(true);
                RepeatCustomPickerFragment.is_days_of_month = true;
                RepeatCustomPickerFragment.is_on_the_month = false;
              }
            }
            else if(MainEditFragment.repeat.getScale() == 4) {

              if(day) day = false;
              else if(week) {
                RepeatCustomPickerFragment.removeWeekPreference();
                week = false;
              }
              else if(month) {
                RepeatCustomPickerFragment.removeDaysOfMonthPreference();
                RepeatCustomPickerFragment.removeOnTheMonthPreference();
                month = false;
              }

              RepeatCustomPickerFragment.addYearPreference();
              year = true;
            }
        }
      }
    });
  }
//
//  private static class SavedState extends BaseSavedState {
//
//    int parcel_interval_value;
//    int parcel_scale_value;
//
//    public SavedState(Parcelable superState) {
//      super(superState);
//    }
//
//    public SavedState(Parcel source) {
//      super(source);
//      parcel_interval_value = source.readInt();
//      parcel_scale_value = source.readInt();
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//      super.writeToParcel(dest, flags);
//      dest.writeInt(parcel_interval_value);
//      dest.writeInt(parcel_scale_value);
//    }
//
//    public static final Parcelable.Creator<SavedState> CREATOR =
//        new Parcelable.Creator<SavedState>() {
//
//          public SavedState createFromParcel(Parcel in) {
//            return new SavedState(in);
//          }
//
//          public SavedState[] newArray(int size) {
//            return new SavedState[size];
//          }
//        };
//  }
//
//  @Override
//  protected Parcelable onSaveInstanceState() {
//
//    final Parcelable superState = super.onSaveInstanceState();
//
//    if (isPersistent()) {
//      return superState;
//    }
//
//    final SavedState myState = new SavedState(superState);
//
//    myState.parcel_interval_value = interval_value;
//    myState.parcel_scale_value = scale_value;
//    return myState;
//  }
//
//  @Override
//  protected void onRestoreInstanceState(Parcelable state) {
//
//    if (state == null || !state.getClass().equals(SavedState.class)) {
//      super.onRestoreInstanceState(state);
//      return;
//    }
//
//    SavedState myState = (SavedState) state;
//    super.onRestoreInstanceState(myState.getSuperState());
//
//    interval.setValue(myState.parcel_interval_value);
//    scale.setValue(myState.parcel_scale_value);
//  }
}
