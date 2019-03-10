package com.hideaki.kk_reminder;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_TEXT_SIZE;
import static com.hideaki.kk_reminder.UtilClass.INT_GENERAL;
import static com.hideaki.kk_reminder.UtilClass.LOCALE;

public class DefaultTextSizeModelPreference extends Preference {

  private MainActivity activity;

  public DefaultTextSizeModelPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
    setLayoutResource(R.layout.child_layout);
    activity = (MainActivity)((ContextWrapper)context).getBaseContext();
  }

  @Override
  public void onBindViewHolder(PreferenceViewHolder holder) {

    super.onBindViewHolder(holder);
    TextView date = (TextView)holder.findViewById(R.id.date);
    TextView detail = (TextView)holder.findViewById(R.id.detail);
    TextView repeat = (TextView)holder.findViewById(R.id.repeat);
    ImageView tag = (ImageView)holder.findViewById(R.id.tag_pallet);

    Calendar now = Calendar.getInstance();
    now.add(Calendar.MINUTE, 1);
    String set_time;
    if(LOCALE.equals(Locale.JAPAN)) {
      set_time = (String)DateFormat.format("M月d日(E) k:mm", now);
    }
    else {
      set_time = (String)DateFormat.format("M/d (E) k:mm", now);
    }

    set_time += " (";
    if(!LOCALE.equals(Locale.JAPAN)) set_time += " ";
    set_time += activity.getString(R.string.within_one_minute);
    if(!LOCALE.equals(Locale.JAPAN)) set_time += " ";
    set_time += ")";

    date.setText(set_time);
    detail.setText(R.string.default_detail);
    detail.setTextSize(activity.text_size);
    repeat.setText(R.string.non_repeat);
    tag.setVisibility(View.GONE);
  }
}