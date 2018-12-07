package com.hideaki.kk_reminder;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AdViewPreference extends Preference {

  private MainActivity activity;

  public AdViewPreference(Context context, AttributeSet attrs) {

    super(context, attrs);
    activity = (MainActivity)getContext();
  }

  @Override
  protected View onCreateView(ViewGroup parent) {

    super.onCreateView(parent);
    return View.inflate(activity, R.layout.ad_view_layout, null);
  }

  @Override
  protected void onBindView(View view) {

    super.onBindView(view);

    AdView adView = view.findViewById(R.id.adView);
    if(activity.is_premium) {
      adView.setVisibility(View.GONE);
    }
    else {
      AdRequest adRequest = new AdRequest.Builder().build();
      adView.loadAd(adRequest);
    }
  }
}