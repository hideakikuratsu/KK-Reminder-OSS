package com.hideaki.kk_reminder;

import android.content.Context;
import android.content.ContextWrapper;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import static com.hideaki.kk_reminder.UtilClass.LOCALE;

public class DefaultControlTimeModelPreference extends Preference {

  private final MainActivity activity;

  public DefaultControlTimeModelPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
    setLayoutResource(R.layout.child_layout);
    activity = (MainActivity)((ContextWrapper)context).getBaseContext();
  }

  @Override
  public void onBindViewHolder(PreferenceViewHolder holder) {

    super.onBindViewHolder(holder);
    CardView childCard = (CardView)holder.findViewById(R.id.child_card);
    ImageView clockImage = (ImageView)holder.findViewById(R.id.clock_image);
    TextView date = (TextView)holder.findViewById(R.id.date);
    TextView detail = (TextView)holder.findViewById(R.id.detail);
    TextView repeat = (TextView)holder.findViewById(R.id.repeat);
    ImageView tag = (ImageView)holder.findViewById(R.id.tag_pallet);
    CardView controlCard = (CardView)holder.findViewById(R.id.control_card);
    TableLayout controlPanel = (TableLayout)holder.findViewById(R.id.control_panel);
    TextView minusTime1 = (TextView)holder.findViewById(R.id.minus_time1);
    TextView minusTime2 = (TextView)holder.findViewById(R.id.minus_time2);
    TextView minusTime3 = (TextView)holder.findViewById(R.id.minus_time3);
    TextView plusTime1 = (TextView)holder.findViewById(R.id.plus_time1);
    TextView plusTime2 = (TextView)holder.findViewById(R.id.plus_time2);
    TextView plusTime3 = (TextView)holder.findViewById(R.id.plus_time3);

    Calendar now = Calendar.getInstance();
    now.add(Calendar.MINUTE, 1);
    String setTime;
    if(LOCALE.equals(Locale.JAPAN)) {
      setTime = (String)DateFormat.format("M月d日(E) k:mm", now);
    }
    else {
      setTime = (String)DateFormat.format("E, MMM d 'at' h:mma", now);
    }

    setTime += " (" + activity.getString(R.string.within_one_minute) + ")";

    // 各種初期設定
    if(activity.isDarkMode) {
      childCard.setBackgroundColor(activity.backgroundFloatingMaterialDarkColor);
      controlCard.setBackgroundColor(activity.backgroundFloatingMaterialDarkColor);
      clockImage.setColorFilter(ContextCompat.getColor(
        activity,
        R.color.green5PrimaryColor
      ));
      TextView[] textViews = {
        date, detail, repeat, minusTime1, minusTime2, minusTime3, plusTime1, plusTime2, plusTime3
      };
      for(TextView textView : textViews) {
        textView.setTextColor(activity.secondaryTextMaterialDarkColor);
      }
    }
    date.setText(setTime);
    detail.setText(R.string.default_detail);
    detail.setTextSize(activity.textSize);
    repeat.setText(R.string.non_repeat);
    tag.setVisibility(View.GONE);
    controlPanel.setVisibility(View.VISIBLE);

    minusTime1.setText(activity.getControlTimeText(true, 1));
    minusTime2.setText(activity.getControlTimeText(true, 2));
    minusTime3.setText(activity.getControlTimeText(true, 3));
    plusTime1.setText(activity.getControlTimeText(false, 1));
    plusTime2.setText(activity.getControlTimeText(false, 2));
    plusTime3.setText(activity.getControlTimeText(false, 3));

    minusTime1.setOnClickListener(v -> {

      DefaultControlTimePickerDialogFragment dialog =
        new DefaultControlTimePickerDialogFragment();
      dialog.setWhichTimeController((TextView)v, true, 1);
      dialog.show(activity.getSupportFragmentManager(), "minus_control_time_picker1");
    });

    minusTime2.setOnClickListener(v -> {

      DefaultControlTimePickerDialogFragment dialog =
        new DefaultControlTimePickerDialogFragment();
      dialog.setWhichTimeController((TextView)v, true, 2);
      dialog.show(activity.getSupportFragmentManager(), "minus_control_time_picker2");
    });

    minusTime3.setOnClickListener(v -> {

      DefaultControlTimePickerDialogFragment dialog =
        new DefaultControlTimePickerDialogFragment();
      dialog.setWhichTimeController((TextView)v, true, 3);
      dialog.show(activity.getSupportFragmentManager(), "minus_control_time_picker3");
    });

    plusTime1.setOnClickListener(v -> {

      DefaultControlTimePickerDialogFragment dialog =
        new DefaultControlTimePickerDialogFragment();
      dialog.setWhichTimeController((TextView)v, false, 1);
      dialog.show(activity.getSupportFragmentManager(), "plus_control_time_picker1");
    });

    plusTime2.setOnClickListener(v -> {

      DefaultControlTimePickerDialogFragment dialog =
        new DefaultControlTimePickerDialogFragment();
      dialog.setWhichTimeController((TextView)v, false, 2);
      dialog.show(activity.getSupportFragmentManager(), "plus_control_time_picker2");
    });

    plusTime3.setOnClickListener(v -> {

      DefaultControlTimePickerDialogFragment dialog =
        new DefaultControlTimePickerDialogFragment();
      dialog.setWhichTimeController((TextView)v, false, 3);
      dialog.show(activity.getSupportFragmentManager(), "plus_control_time_picker3");
    });
  }
}
