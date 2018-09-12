package com.example.hideaki.reminder;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import static com.google.common.base.Preconditions.checkNotNull;


public class ActionBarFragment extends Fragment {

  private MainActivity activity;
  SearchView searchView;
  private int order;

  public static ActionBarFragment newInstance() {

    return new ActionBarFragment();
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
    order = activity.menuItem.getOrder();
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    this.setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    Toolbar toolbar = activity.findViewById(R.id.toolbar_layout);
    activity.setSupportActionBar(toolbar);
    ActionBar actionBar = activity.getSupportActionBar();
    checkNotNull(actionBar);

    activity.drawerToggle.setDrawerIndicatorEnabled(true);
    actionBar.setTitle(R.string.app_name);
    return inflater.inflate(R.layout.fragment_search, container, false);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.reminder_menu, menu);

    //検索メニューの実装
    MenuItem search_item = menu.findItem(R.id.search_item);
    searchView = (SearchView)search_item.getActionView();

    //色の設定
    Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_search_white_24dp);
    checkNotNull(drawable);
    drawable = drawable.mutate();
    drawable.setColorFilter(activity.menu_item_color, PorterDuff.Mode.SRC_IN);
    ImageView searchIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
    searchIcon.setImageDrawable(drawable);

    searchView.setIconifiedByDefault(true);
    searchView.setSubmitButtonEnabled(false);

    //検索アイコン押下時の処理
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(String text) {
        if(text == null || text.equals("")) {
          if(activity.menuItem.getOrder() == 0) {
            activity.expandableListView.clearTextFilter();
          }
          else if(activity.menuItem.getOrder() == 1 || activity.menuItem.getOrder() == 3) {
            activity.listView.clearTextFilter();
          }
        }
        else {
          if(activity.menuItem.getOrder() == 0) {
            activity.expandableListView.setFilterText(text);
          }
          else if(activity.menuItem.getOrder() == 1 || activity.menuItem.getOrder() == 3) {
            activity.listView.setFilterText(text);
          }
        }

        return false;
      }
    });

    //次の親グループを展開するメニューボタンの実装
    MenuItem nextGroupExpand = menu.findItem(R.id.next_group_expand);
    if(order == 0) {
      nextGroupExpand.setVisible(true);
      drawable = ContextCompat.getDrawable(activity, R.drawable.ic_next_group_expand_white_24dp);
      checkNotNull(drawable);
      drawable = drawable.mutate();
      drawable.setColorFilter(activity.menu_item_color, PorterDuff.Mode.SRC_IN);
      nextGroupExpand.setIcon(drawable);
    }
    else {
      nextGroupExpand.setVisible(false);
    }

    //新しくアイテムを追加するメニューボタンの実装
    MenuItem addItem = menu.findItem(R.id.add_item);
    drawable = ContextCompat.getDrawable(activity, R.drawable.ic_add_circle_24dp);
    checkNotNull(drawable);
    drawable = drawable.mutate();
    drawable.setColorFilter(activity.menu_item_color, PorterDuff.Mode.SRC_IN);
    addItem.setIcon(drawable);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch(item.getItemId()) {
      case R.id.add_item:
        if(activity.menuItem.getOrder() == 3) {
          activity.showMainEditFragmentForList();
        }
        else {
          activity.showMainEditFragment();
        }
        return true;
    }
    return false;
  }
}
