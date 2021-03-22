package com.hideaki.kk_reminder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class NotesPreference extends Preference {

  private MainActivity activity;

  public NotesPreference(Context context, AttributeSet attrs, int defStyleAttr) {

    super(context, attrs, defStyleAttr);
    activity = (MainActivity)((ContextWrapper)context).getBaseContext();
  }

  public NotesPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
    activity = (MainActivity)((ContextWrapper)context).getBaseContext();
  }

  public NotesPreference(Context context) {

    super(context);
    activity = (MainActivity)((ContextWrapper)context).getBaseContext();
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public void onBindViewHolder(PreferenceViewHolder holder) {

    super.onBindViewHolder(holder);
    final TextView textView = (TextView)holder.findViewById(android.R.id.summary);
    if(textView != null) {
      textView.setMovementMethod(new ScrollingMovementMethod());
      textView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          activity.showNotesFragment(MainEditFragment.item);
        }
      });
      textView.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
          if(textView.canScrollVertically(1) || textView.canScrollVertically(-1)) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
          }
          return false;
        }
      });
    }
  }
}
