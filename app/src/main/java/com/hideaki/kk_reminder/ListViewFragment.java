package com.hideaki.kk_reminder;

import android.app.Fragment;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import static com.hideaki.kk_reminder.UtilClass.getPxFromDp;

public class ListViewFragment extends Fragment {

  static final String TAG = ListViewFragment.class.getSimpleName();
  private MainActivity activity;

  public static ListViewFragment newInstance() {

    return new ListViewFragment();
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

    activity.listAdapter.colorStateList = new ColorStateList(
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

    View view = inflater.inflate(R.layout.listview, container, false);
    view.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));
    view.setFocusableInTouchMode(true);
    view.requestFocus();
    view.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {

          if(MyListAdapter.is_sorting) {
            new AlertDialog.Builder(activity)
                .setTitle(R.string.is_sorting_title)
                .setMessage(R.string.is_sorting_message)
                .show();

            return true;
          }

          if(activity.listAdapter.actionMode != null) {
            activity.listAdapter.actionMode.finish();
          }
        }

        return false;
      }
    });

    MyListAdapter.checked_item_num = 0;
    MyListAdapter.has_panel = 0;
    MyListAdapter.is_sorting = false;
    MyListAdapter.itemList = activity.getNonScheduledItem(MyDatabaseHelper.TODO_TABLE);
    activity.listView = view.findViewById(R.id.listView);
    View emptyView = View.inflate(activity, R.layout.nonscheduled_list_empty_layout, null);
    ((ViewGroup)activity.listView.getParent()).addView(emptyView);
    int paddingPx = getPxFromDp(activity, 150);
    ((ViewGroup)activity.listView.getParent()).setPadding(0, paddingPx, 0, 0);
    activity.listView.setEmptyView(emptyView);
    activity.listView.setDragListener(activity.listAdapter.dragListener);
    activity.listView.setSortable(true);
    activity.listView.setAdapter(activity.listAdapter);
    activity.listView.setTextFilterEnabled(true);

    AdView adView = view.findViewById(R.id.adView);
    if(activity.generalSettings.isPremium()) {
      adView.setVisibility(View.GONE);
    }
    else {
      AdRequest adRequest = new AdRequest.Builder().build();
      adView.loadAd(adRequest);
    }

    return view;
  }
}
