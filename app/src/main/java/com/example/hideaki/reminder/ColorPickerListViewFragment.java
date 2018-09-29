package com.example.hideaki.reminder;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import static com.google.common.base.Preconditions.checkNotNull;

public class ColorPickerListViewFragment extends Fragment {

  static final String TAG = ColorPickerListViewFragment.class.getSimpleName();
  private MainActivity activity;
  static int order;
  static int tag_position;

  public static ColorPickerListViewFragment newInstance() {

    return new ColorPickerListViewFragment();
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
    order = activity.order;
    ColorPickerListAdapter.order = order;
    activity.colorPickerListAdapter.adapterTag = TagEditListAdapter.tagList.get(tag_position);
    activity.colorPickerListAdapter.orgTag = activity.generalSettings.getTagList().get(tag_position);
    if(order == 0 || order == 1 || order == 4) {
      ColorPickerListAdapter.checked_position = activity.colorPickerListAdapter.adapterTag.getColor_order_group();
    }
    else if(order == 3) {
      ColorPickerListAdapter.checked_position = MainEditFragment.list.getColorGroup();
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    this.setHasOptionsMenu(true);
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
          MainEditFragment.list.setColor_primary(true);
        }

        return false;
      }
    });

    activity.listView = view.findViewById(R.id.listView);
    activity.listView.setAdapter(activity.colorPickerListAdapter);

    Toolbar toolbar = activity.findViewById(R.id.toolbar_layout);
    activity.setSupportActionBar(toolbar);
    ActionBar actionBar = activity.getSupportActionBar();
    checkNotNull(actionBar);

    activity.drawerToggle.setDrawerIndicatorEnabled(false);
    actionBar.setHomeAsUpIndicator(activity.upArrow);
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(R.string.pick_color);

    return view;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch(item.getItemId()) {

      case android.R.id.home: {
        if(order == 3) MainEditFragment.list.setColor_primary(true);
        getFragmentManager().popBackStack();
        return true;
      }
      default: {
        return super.onOptionsItemSelected(item);
      }
    }
  }
}
