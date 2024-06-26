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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import static com.hideaki.kk_reminder.UtilClass.ITEM_IDS;
import static com.hideaki.kk_reminder.UtilClass.getPxFromDp;
import static java.util.Objects.requireNonNull;

public class DoneListViewFragment extends Fragment {

  static final String TAG = DoneListViewFragment.class.getSimpleName();
  private MainActivity activity;
  private static int expandableListPosition;
  private static int expandableListOffset;
  @SuppressLint("UseSparseArrays")
  private static final Map<Long, Integer> LIST_POSITION = new HashMap<>();
  @SuppressLint("UseSparseArrays")
  private static final Map<Long, Integer> LIST_OFFSET = new HashMap<>();
  private ListView oldListView;
  private int order;
  private long id;

  public static DoneListViewFragment newInstance() {

    return new DoneListViewFragment();
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
    switch(order) {

      case 0: {

        expandableListPosition = oldListView.getFirstVisiblePosition();
        View child = oldListView.getChildAt(0);
        if(child != null) {
          expandableListOffset = child.getTop();
        }
        break;
      }
      case 1: {

        LIST_POSITION.put(id, oldListView.getFirstVisiblePosition());
        View child = oldListView.getChildAt(0);
        if(child != null) {
          LIST_OFFSET.put(id, child.getTop());
        }
        break;
      }
    }
  }

  @Nullable
  @Override
  public View onCreateView(
    @NonNull LayoutInflater inflater,
    @Nullable ViewGroup container,
    Bundle savedInstanceState
  ) {

    order = activity.order;
    if(order == 1) {
      id = activity.generalSettings.getNonScheduledLists().get(activity.whichMenuOpen - 1).getId();
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

        if(activity.doneListAdapter.actionMode != null) {
          activity.doneListAdapter.actionMode.finish();
        }
      }

      return false;
    });

    List<ItemAdapter> itemList = activity.getDoneItem();
    int size = itemList.size();
    if(size > 100) {

      List<ItemAdapter> nonDeleteItemList = new ArrayList<>();
      for(int i = 0; i < size / 2; i++) {
        nonDeleteItemList.add(itemList.get(i));
      }

      DoneListAdapter.itemList = nonDeleteItemList;

      List<Long> idListToBeDeleted = new ArrayList<>();
      for(int i = size / 2; i < size; i++) {
        idListToBeDeleted.add(itemList.get(i).getId());
      }
      WorkRequest workRequest = new OneTimeWorkRequest.Builder(DeleteDoneListWorker.class)
          .setInputData(
              new Data.Builder()
                  .putLongArray(ITEM_IDS, idListToBeDeleted.stream().mapToLong(l -> l).toArray())
                  .build()
          )
          .build();
      WorkManager.getInstance(activity)
          .enqueue(workRequest);
    }
    else {
      DoneListAdapter.itemList = itemList;
    }
    DoneListAdapter.checkedItemNum = 0;
    DoneListAdapter.order = order;
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
    View emptyView;
    if(order == 0) {
      emptyView = View.inflate(activity, R.layout.expandable_list_empty_layout, null);
    }
    else {
      emptyView = View.inflate(activity, R.layout.nonscheduled_list_empty_layout, null);
    }
    emptyView.setLayoutParams(layoutParams);
    linearLayout.addView(emptyView);
    int paddingPx = getPxFromDp(activity, 75);
    linearLayout.setPadding(0, 0, 0, paddingPx);
    ((ViewGroup)activity.listView.getParent()).addView(linearLayout, 0, layoutParams);
    activity.listView.setEmptyView(linearLayout);
    activity.listView.post(() -> {

      switch(order) {

        case 0: {

          activity.listView.setSelectionFromTop(expandableListPosition, expandableListOffset);
          break;
        }
        case 1: {

          Integer listPosition = DoneListViewFragment.LIST_POSITION.get(id);
          Integer listOffset = DoneListViewFragment.LIST_OFFSET.get(id);
          if(listPosition == null) {
            listPosition = 0;
          }
          if(listOffset == null) {
            listOffset = 0;
          }
          activity.listView.setSelectionFromTop(listPosition, listOffset);
          break;
        }
      }
    });
    activity.listView.setAdapter(activity.doneListAdapter);
    activity.listView.setTextFilterEnabled(true);
    activity.listView.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {

        switch(scrollState) {

          case SCROLL_STATE_IDLE: {

            DoneListAdapter.isScrolling = false;
            break;
          }
          case SCROLL_STATE_FLING:
          case SCROLL_STATE_TOUCH_SCROLL: {

            DoneListAdapter.isScrolling = true;
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
