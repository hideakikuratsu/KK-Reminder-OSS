package com.hideaki.kk_reminder;

import android.app.Fragment;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import static com.hideaki.kk_reminder.UtilClass.getPxFromDp;

public class ExpandableListViewFragment extends Fragment {

  static final String TAG = ExpandableListViewFragment.class.getSimpleName();
  private MainActivity activity;

  public static ExpandableListViewFragment newInstance() {

    return new ExpandableListViewFragment();
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
    activity.drawerLayout.closeDrawer(GravityCompat.START);
    if(activity.detail != null) {
      activity.showMainEditFragment(activity.detail, TAG);
      activity.detail = null;
    }

    activity.expandableListAdapter.colorStateList = new ColorStateList(
        new int[][] {
            new int[]{-android.R.attr.state_checked}, // unchecked
            new int[]{android.R.attr.state_checked} // checked
        },
        new int[] {
            ContextCompat.getColor(activity, R.color.icon_gray),
            activity.accent_color
        }
    );
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.expandable_listview, container, false);
    view.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));
    view.setFocusableInTouchMode(true);
    view.requestFocus();
    view.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
          if(activity.expandableListAdapter.actionMode != null) {
            activity.expandableListAdapter.actionMode.finish();
          }
        }
        return false;
      }
    });

    MyExpandableListAdapter.has_panel = 0;
    MyExpandableListAdapter.checked_item_num = 0;
    MyExpandableListAdapter.children = activity.getChildren(MyDatabaseHelper.TODO_TABLE);
    activity.expandableListView = view.findViewById(R.id.expandable_list);
    LinearLayout linearLayout = new LinearLayout(activity);
    linearLayout.setOrientation(LinearLayout.VERTICAL);
    LinearLayout.LayoutParams layoutParams =
        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    layoutParams.gravity = Gravity.CENTER;
    View emptyView = View.inflate(activity, R.layout.expandable_list_empty_layout, null);
    emptyView.setLayoutParams(layoutParams);
    linearLayout.addView(emptyView);
    int paddingPx = getPxFromDp(activity, 75);
    linearLayout.setPadding(0, 0, 0, paddingPx);
    ((ViewGroup)activity.expandableListView.getParent()).addView(linearLayout, layoutParams);
    activity.expandableListView.setEmptyView(linearLayout);
    activity.expandableListView.setAdapter(activity.expandableListAdapter);
    activity.expandableListView.setTextFilterEnabled(true);

    AdView adView = view.findViewById(R.id.adView);
    //TODO: 広告テスト。テストが終わったら元に戻すこと
//    if(activity.generalSettings.isPremium()) {
//      adView.setVisibility(View.GONE);
//    }
//    else {
//      AdRequest adRequest = new AdRequest.Builder().build();
//      adView.loadAd(adRequest);
//    }
    AdRequest adRequest = new AdRequest.Builder().build();
    adView.loadAd(adRequest);

    return view;
  }
}
