package com.hideaki.kk_reminder;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AdViewPreference extends Preference {

  private MainActivity activity;

  public AdViewPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
    setLayoutResource(R.layout.ad_view_layout);
    activity = (MainActivity)((ContextWrapper)context).getBaseContext();
  }

  @Override
  public void onBindViewHolder(PreferenceViewHolder holder) {

    super.onBindViewHolder(holder);

    AdView adView = (AdView)holder.findViewById(R.id.adView);
    if(activity.is_premium) {
      adView.setVisibility(View.GONE);
    }
    else {
      AdRequest adRequest = new AdRequest.Builder().build();
      adView.loadAd(adRequest);
    }
  }
}