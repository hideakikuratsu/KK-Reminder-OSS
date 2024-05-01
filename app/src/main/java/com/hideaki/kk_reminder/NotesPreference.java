package com.hideaki.kk_reminder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class NotesPreference extends Preference {

  private final MainActivity activity;

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
      textView.setOnClickListener(v -> activity.showNotesFragment(MainEditFragment.item));
      textView.setOnTouchListener((v, event) -> {
        if(textView.canScrollVertically(1) || textView.canScrollVertically(-1)) {
          v.getParent().requestDisallowInterceptTouchEvent(true);
        }
        return false;
      });
    }
  }
}
