package com.hideaki.kk_reminder;

import android.annotation.SuppressLint;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceGroupAdapter;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.takisoft.fix.support.v7.preference.EditTextPreference;
import com.takisoft.fix.support.v7.preference.EditTextPreferenceDialogFragmentCompat;
import com.takisoft.fix.support.v7.preference.PreferenceCategory;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BasePreferenceFragmentCompat extends PreferenceFragmentCompat {

  private static final String DIALOG_FRAGMENT_TAG = BasePreferenceFragmentCompat.class.getSimpleName();

  @Override
  protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {

    return new PreferenceGroupAdapter(preferenceScreen) {
      @SuppressLint("RestrictedApi")
      @Override
      public void onBindViewHolder(PreferenceViewHolder holder, int position) {

        super.onBindViewHolder(holder, position);
        Preference preference = getItem(position);
        if(preference instanceof PreferenceCategory) {
          setZeroPaddingToLayoutChildren(holder.itemView);
        }
        else {
          View iconFrame = holder.itemView.findViewById(R.id.icon_frame);
          if(iconFrame != null) {
            iconFrame.setVisibility(preference.getIcon() == null ? View.GONE : View.VISIBLE);
          }
        }
      }
    };
  }

  private void setZeroPaddingToLayoutChildren(View view) {

    if(!(view instanceof ViewGroup)) return;
    ViewGroup viewGroup = (ViewGroup)view;
    int childCount = viewGroup.getChildCount();
    for(int i = 0; i < childCount; i++) {
      setZeroPaddingToLayoutChildren(viewGroup.getChildAt(i));
      viewGroup.setPaddingRelative(0, viewGroup.getPaddingTop(), viewGroup.getPaddingEnd(), viewGroup.getPaddingBottom());
    }
  }

  @Override
  public void onDisplayPreferenceDialog(Preference preference) {

    FragmentManager manager = getFragmentManager();
    checkNotNull(manager);
    if(manager.findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
      return;
    }

    final DialogFragment f;
    if(preference instanceof EditTextPreference) {
      f = EditTextPreferenceDialogFragmentCompat.newInstance(preference.getKey());
      f.setTargetFragment(this, 0);
      f.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);

      getFragmentManager().executePendingTransactions();
      final AlertDialog dialog = (AlertDialog)f.getDialog();

      MainActivity activity = (MainActivity)getActivity();
      checkNotNull(activity);

      dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.accent_color);
      dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(activity.accent_color);
    }
    else super.onDisplayPreferenceDialog(preference);
  }
}
