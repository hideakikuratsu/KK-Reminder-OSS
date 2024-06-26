package com.hideaki.kk_reminder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static com.hideaki.kk_reminder.UtilClass.getPxFromDp;
import static java.util.Objects.requireNonNull;

public class ListViewFragment extends Fragment {

  static final String TAG = ListViewFragment.class.getSimpleName();
  private MainActivity activity;
  @SuppressLint("UseSparseArrays")
  private static final Map<Long, Integer> LIST_POSITION = new HashMap<>();
  @SuppressLint("UseSparseArrays")
  private static final Map<Long, Integer> LIST_OFFSET = new HashMap<>();
  private ListView oldListView;
  private long id;

  public static ListViewFragment newInstance() {

    return new ListViewFragment();
  }

  @Override
  public void onAttach(@NonNull Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
    if(activity.drawerLayout != null) {
      activity.drawerLayout.closeDrawer(GravityCompat.START);
    }
    else {
      FragmentManager manager = requireNonNull(activity.getSupportFragmentManager());
      manager
        .beginTransaction()
        .remove(this)
        .commit();
    }
  }

  @Override
  public void onDestroyView() {

    super.onDestroyView();
    LIST_POSITION.put(id, oldListView.getFirstVisiblePosition());
    View child = oldListView.getChildAt(0);
    if(child != null) {
      LIST_OFFSET.put(id, child.getTop());
    }
  }

  @Nullable
  @Override
  public View onCreateView(
    @NonNull LayoutInflater inflater,
    @Nullable ViewGroup container,
    Bundle savedInstanceState
  ) {

    if(activity.whichMenuOpen > 0) {
      id = activity.generalSettings.getNonScheduledLists().get(activity.whichMenuOpen - 1).getId();
    }
    else {
      id = 0;
    }

    View view = inflater.inflate(R.layout.listview, container, false);
    if(activity.isDarkMode) {
      view.setBackgroundColor(activity.backgroundMaterialDarkColor);
    }
    else {
      view.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));
    }
    view.setFocusableInTouchMode(true);
    view.requestFocus();
    view.setOnKeyListener((v, keyCode, event) -> {

      if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {

        if(MyListAdapter.isSorting) {
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
    });

    MyListAdapter.checkedItemNum = 0;
    MyListAdapter.hasPanel = 0;
    MyListAdapter.isSorting = false;
    MyListAdapter.itemList = activity.getNonScheduledItem(MyDatabaseHelper.TODO_TABLE);
    activity.listView = view.findViewById(R.id.listView);
    oldListView = activity.listView;
    LinearLayout linearLayout = new LinearLayout(activity);
    linearLayout.setOrientation(LinearLayout.VERTICAL);
    LinearLayout.LayoutParams layoutParams =
      new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT
      );
    layoutParams.gravity = Gravity.CENTER;
    layoutParams.weight = 1;
    layoutParams.height = 0;
    View emptyView = View.inflate(activity, R.layout.nonscheduled_list_empty_layout, null);
    emptyView.setLayoutParams(layoutParams);
    linearLayout.addView(emptyView);
    int paddingPx = getPxFromDp(activity, 75);
    linearLayout.setPadding(0, 0, 0, paddingPx);
    ((ViewGroup)activity.listView.getParent()).addView(linearLayout, 0, layoutParams);
    activity.listView.setEmptyView(linearLayout);
    activity.listView.setDragListener(activity.listAdapter.myDragListener);
    activity.listView.post(() -> {

      Integer listPosition = ListViewFragment.LIST_POSITION.get(id);
      Integer listOffset = ListViewFragment.LIST_OFFSET.get(id);
      if(listPosition == null) {
        listPosition = 0;
      }
      if(listOffset == null) {
        listOffset = 0;
      }
      activity.listView.setSelectionFromTop(listPosition, listOffset);
    });
    activity.listView.setAdapter(activity.listAdapter);
    activity.listView.setTextFilterEnabled(true);
    activity.listView.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {

        switch(scrollState) {

          case SCROLL_STATE_IDLE: {

            MyListAdapter.isScrolling = false;
            break;
          }
          case SCROLL_STATE_FLING:
          case SCROLL_STATE_TOUCH_SCROLL: {

            MyListAdapter.isScrolling = true;
            break;
          }
        }
      }

      @Override
      public void onScroll(
        AbsListView view,
        int firstVisibleItem,
        int visibleItemCount,
        int totalItemCount
      ) {

      }
    });

    if(!activity.isPremium) {
      LinearLayout adContainer = view.findViewById(R.id.ad_mob_view);
      if(activity.adView.getParent() != null) {
        ((ViewGroup)activity.adView.getParent()).removeView(activity.adView);
      }
      adContainer.addView(activity.adView);
    }

    return view;
  }
}
