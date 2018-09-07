package com.example.hideaki.reminder;

import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import static com.google.common.base.Preconditions.checkNotNull;


public class ActionBarFragment extends Fragment {

  private MainActivity activity;
  SearchView searchView;

  public static ActionBarFragment newInstance() {

    return new ActionBarFragment();
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
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

//    Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_search_white_24dp);
//    checkNotNull(drawable);
//    drawable = drawable.mutate();
//    drawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
//    ImageView searchIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
//    searchIcon.setImageDrawable(drawable);

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
