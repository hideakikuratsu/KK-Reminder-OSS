package com.hideaki.kk_reminder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import static com.google.common.base.Preconditions.checkNotNull;

public class HelpAndFeedbackFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

  static final String TAG = HelpAndFeedbackFragment.class.getSimpleName();
  private MainActivity activity;

  public static HelpAndFeedbackFragment newInstance() {

    return new HelpAndFeedbackFragment();
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
    activity.drawerLayout.closeDrawer(GravityCompat.START);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.help_and_feedback);

    PreferenceScreen contact = (PreferenceScreen)findPreference("contact");
    PreferenceScreen feedback = (PreferenceScreen)findPreference("feedback");
    PreferenceScreen request = (PreferenceScreen)findPreference("request");

    contact.setOnPreferenceClickListener(this);
    feedback.setOnPreferenceClickListener(this);
    request.setOnPreferenceClickListener(this);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    checkNotNull(view);

    view.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));

    Toolbar toolbar = activity.findViewById(R.id.toolbar_layout);
    activity.setSupportActionBar(toolbar);
    ActionBar actionBar = activity.getSupportActionBar();
    checkNotNull(actionBar);

    activity.drawerToggle.setDrawerIndicatorEnabled(true);
    actionBar.setTitle(R.string.nav_help_and_feedback);

    //設定項目間の区切り線の非表示
    ListView listView = view.findViewById(android.R.id.list);
    listView.setDivider(null);

    return view;
  }

  @Override
  public boolean onPreferenceClick(Preference preference) {

    switch(preference.getKey()) {
      case "contact":
      case "request": {

        Intent intent = new Intent()
            .setAction(Intent.ACTION_SENDTO)
            .setType("text/plain")
            .setData(Uri.parse("mailto:bisigness100@gmail.com"));
        if(preference.getKey().equals("contact")) {
          intent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.contact));
        }
        else {
          intent.putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.request));
        }
        activity.startActivity(intent);
        return true;
      }
      case "feedback": {

        //TODO: 公開したアプリの評価画面に飛ぶ
        return true;
      }
    }
    return false;
  }
}