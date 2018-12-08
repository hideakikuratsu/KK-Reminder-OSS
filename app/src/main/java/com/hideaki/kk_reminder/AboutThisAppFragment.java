package com.hideaki.kk_reminder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.util.Locale;

import static com.google.common.base.Preconditions.checkNotNull;

public class AboutThisAppFragment extends Fragment {

  static final String TAG = AboutThisAppFragment.class.getSimpleName();
  private MainActivity activity;
  private static Locale locale = Locale.getDefault();
  private WebView webView;

  public static AboutThisAppFragment newInstance() {

    return new AboutThisAppFragment();
  }

  @TargetApi(23)
  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    onAttachToContext(context);
  }

  //API 23(Marshmallow)未満においてはこっちのonAttachが呼ばれる
  @SuppressWarnings("deprecation")
  @Override
  public void onAttach(Activity activity) {

    super.onAttach(activity);
    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      onAttachToContext(activity);
    }
  }

  //2つのonAttachの共通処理部分
  protected void onAttachToContext(Context context) {

    activity = (MainActivity)context;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    this.setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.about_this_app_layout, container, false);
    view.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));

    webView = view.findViewById(R.id.webView);
    if(locale.equals(Locale.JAPAN)) {
      webView.loadUrl("file:///android_asset/about_this_app_ja.html");
    }
    else {
      webView.loadUrl("file:///android_asset/about_this_app_en.html");
    }

    Toolbar toolbar = activity.findViewById(R.id.toolbar_layout);
    activity.setSupportActionBar(toolbar);
    ActionBar actionBar = activity.getSupportActionBar();
    checkNotNull(actionBar);

    activity.drawerToggle.setDrawerIndicatorEnabled(false);
    actionBar.setHomeAsUpIndicator(activity.upArrow);
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(R.string.about_this_app);

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    getFragmentManager().popBackStack();
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onDestroyView() {

    super.onDestroyView();
    webView.stopLoading();
    webView.setWebChromeClient(null);
    webView.setWebViewClient(null);
    webView.destroy();
    webView = null;
  }
}