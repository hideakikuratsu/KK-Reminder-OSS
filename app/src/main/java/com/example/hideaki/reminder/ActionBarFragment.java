package com.example.hideaki.reminder;

import android.app.Fragment;
import android.content.Context;
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
  private ActionBar actionBar;
  private MenuItem addItem;
  private MenuItem search_item;

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
    actionBar = activity.getSupportActionBar();
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
    search_item = menu.findItem(R.id.search_item);
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
    else nextGroupExpand.setVisible(false);

    //タスクの並び替えを行うメニューボタンの実装
    MenuItem sortItem = menu.findItem(R.id.sort);
    if(order == 1 || order == 3) {
      sortItem.setVisible(true);
      drawable = ContextCompat.getDrawable(activity, R.drawable.ic_sort_24dp);
      checkNotNull(drawable);
      drawable = drawable.mutate();
      drawable.setColorFilter(activity.menu_item_color, PorterDuff.Mode.SRC_IN);
      sortItem.setIcon(drawable);
    }
    else sortItem.setVisible(false);


    //新しくアイテムを追加するメニューボタンの実装
    addItem = menu.findItem(R.id.add_item);
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
        if(order == 3) {
          activity.showMainEditFragmentForList();
        }
        else {
          activity.showMainEditFragment();
        }
        return true;
      case R.id.sort:
        if(order == 1) {
          MyListAdapter.is_sorting = !MyListAdapter.is_sorting;
          if(MyListAdapter.is_sorting) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            search_item.setVisible(false);
            addItem.setVisible(false);
          }
          else {
            actionBar.setDisplayHomeAsUpEnabled(true);
            search_item.setVisible(true);
            addItem.setVisible(true);

            int size = MyListAdapter.itemList.size();
            for(int i = 0; i < size; i++) {
              Item tmpItem = MyListAdapter.itemList.get(i);
              if(tmpItem.getOrder() != i) {
                tmpItem.setOrder(i);
                activity.updateDB(tmpItem, MyDatabaseHelper.TODO_TABLE);
              }
            }
          }
          activity.listAdapter.notifyDataSetChanged();
        }
        else if(order == 3) {
          ManageListAdapter.is_sorting = !ManageListAdapter.is_sorting;
          if(ManageListAdapter.is_sorting) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            search_item.setVisible(false);
            addItem.setVisible(false);
          }
          else {
            actionBar.setDisplayHomeAsUpEnabled(true);
            search_item.setVisible(true);
            addItem.setVisible(true);

            activity.generalSettings.setNonScheduledLists(ManageListAdapter.nonScheduledLists);
            int size = ManageListAdapter.nonScheduledLists.size();
            boolean is_updated = false;
            for(int i = 0; i < size; i++) {
              NonScheduledList list = activity.generalSettings.getNonScheduledList(i);
              if(list.getOrder() != i) {
                list.setOrder(i);
                is_updated = true;
              }
            }

            if(is_updated) {

              //一旦reminder_listグループ内のアイテムをすべて消してから元に戻すことで新しく追加したリストの順番を追加した順に並び替える

              //デフォルトアイテムのリストア
              activity.menu.removeGroup(R.id.reminder_list);
              activity.menu.add(R.id.reminder_list, R.id.scheduled_list, 0, R.string.nav_scheduled_item)
                  .setIcon(R.drawable.ic_time)
                  .setCheckable(true);
              activity.menu.add(R.id.reminder_list, R.id.add_list, 2, R.string.add_list)
                  .setIcon(R.drawable.ic_add_24dp)
                  .setCheckable(false);

              //新しく追加したリストのリストア
              for(NonScheduledList list : activity.generalSettings.getOrgNonScheduledLists()) {
                Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_my_list_24dp);
                checkNotNull(drawable);
                drawable = drawable.mutate();
                if(list.getPrimary_color() != 0) {
                  drawable.setColorFilter(list.getPrimary_color(), PorterDuff.Mode.SRC_IN);
                }
                else {
                  drawable.setColorFilter(ContextCompat.getColor(activity, R.color.icon_gray), PorterDuff.Mode.SRC_IN);
                }
                activity.menu.add(R.id.reminder_list, Menu.NONE, 1, list.getTitle())
                    .setIcon(drawable)
                    .setCheckable(true);
              }

              activity.updateSettingsDB();
            }
          }

          activity.manageListAdapter.notifyDataSetChanged();
        }
        return true;
    }
    return false;
  }
}
