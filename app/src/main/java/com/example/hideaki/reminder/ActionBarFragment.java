package com.example.hideaki.reminder;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


public class ActionBarFragment extends Fragment {

  private MainActivity activity;
  SearchView searchView;
  private int order;
  private ActionBar actionBar;
  private MenuItem addItem;
  private MenuItem search_item;
  private boolean is_searching;
  private MenuItem tagSearchItem;
  private MenuItem nextGroupExpand;
  private MenuItem sortItem;
  static long checked_tag;
  static List<List<Item>> filteredLists;
  static List<Item> filteredList;
  static List<NonScheduledList> nonScheduledLists;
  static String filteredText;

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

    //タグ検索を行うメニューボタンの実装
    tagSearchItem = menu.findItem(R.id.tag_search);
    Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_pallet_24dp);
    checkNotNull(drawable);
    drawable = drawable.mutate();
    drawable.setColorFilter(activity.menu_item_color, PorterDuff.Mode.SRC_IN);
    tagSearchItem.setIcon(drawable);
    if((order == 0 || order == 1) && is_searching) {
      tagSearchItem.setVisible(true);
    }
    else tagSearchItem.setVisible(false);

    //次の親グループを展開するメニューボタンの実装
    nextGroupExpand = menu.findItem(R.id.next_group_expand);
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
    sortItem = menu.findItem(R.id.sort);
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

    //検索メニューの実装
    search_item = menu.findItem(R.id.search_item);
    searchView = (SearchView)search_item.getActionView();

    //色の設定
    drawable = ContextCompat.getDrawable(activity, R.drawable.ic_search_white_24dp);
    checkNotNull(drawable);
    drawable = drawable.mutate();
    drawable.setColorFilter(activity.menu_item_color, PorterDuff.Mode.SRC_IN);
    ImageView searchIcon = searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
    searchIcon.setImageDrawable(drawable);

    searchView.setIconifiedByDefault(true);
    searchView.setSubmitButtonEnabled(false);

    //検索アイコン押下時の処理
    searchView.setOnSearchClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        is_searching = true;
        nextGroupExpand.setVisible(false);
        sortItem.setVisible(false);
        addItem.setVisible(false);

        tagSearchItem.setVisible(true);
        checked_tag = -1;

        searchView.requestFocus();
      }
    });

    searchView.setOnCloseListener(new SearchView.OnCloseListener() {
      @Override
      public boolean onClose() {

        is_searching = false;
        if(order == 0) nextGroupExpand.setVisible(true);
        else if(order == 1 || order == 3) sortItem.setVisible(true);
        addItem.setVisible(true);

        tagSearchItem.setVisible(false);

        if(checked_tag != -1) {
          if(order == 0) {
            MyExpandableListAdapter.children = activity.getChildren(MyDatabaseHelper.TODO_TABLE);
          }
          else if(order == 1) {
            MyListAdapter.itemList = activity.getNonScheduledItem(MyDatabaseHelper.TODO_TABLE);
          }
          else if(order == 3) {
            ManageListAdapter.nonScheduledLists = new ArrayList<>(activity.generalSettings.getNonScheduledLists());
          }
        }
        return false;
      }
    });

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(String text) {
        if(text == null || text.equals("")) {
          if(order == 0) {
            activity.expandableListView.clearTextFilter();
          }
          else if(order == 1 || order == 3) {
            activity.listView.clearTextFilter();
          }

          checked_tag = -1;
          filteredText = null;
        }
        else {
          if(order == 0) {
            activity.expandableListView.setFilterText(text);
          }
          else if(order == 1 || order == 3) {
            activity.listView.setFilterText(text);
          }

          filteredText = text;
        }

        return false;
      }
    });
  }

  @SuppressLint("RestrictedApi")
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch(item.getItemId()) {
      case R.id.tag_search: {

        View tagSearchView = activity.findViewById(R.id.tag_search);
        PopupMenu popupMenu = new PopupMenu(activity, tagSearchView);
        final Menu menu = popupMenu.getMenu();

        for(final Tag tag : activity.generalSettings.getTagList()) {
          Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_pallet_24dp);
          checkNotNull(drawable);
          drawable = drawable.mutate();
          if(tag.getPrimary_color() != 0) {
            drawable.setColorFilter(tag.getPrimary_color(), PorterDuff.Mode.SRC_IN);
          }
          else {
            drawable.setColorFilter(ContextCompat.getColor(activity, R.color.icon_gray), PorterDuff.Mode.SRC_IN);
          }

          MenuItem tagItem = menu.add(Menu.NONE, Menu.NONE, tag.getOrder(), tag.getName())
              .setIcon(drawable).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                  if(tag.getId() != checked_tag) {
                    int size = menu.size();
                    for(int i = 0; i < size; i++) {
                      MenuItem menuItem = menu.getItem(i);
                      if(menuItem.isChecked()) menuItem.setChecked(false);
                    }
                    item.setChecked(true);
                    checked_tag = tag.getId();

                    if(order == 0) {

                      MyExpandableListAdapter.children = activity.getChildren(MyDatabaseHelper.TODO_TABLE);

                      filteredLists = new ArrayList<>();
                      for(List<Item> itemList : MyExpandableListAdapter.children) {
                        List<Item> filteredList = new ArrayList<>();
                        for(Item filteredItem : itemList) {
                          if(filteredItem.getWhich_tag_belongs() == tag.getId()) {
                            filteredList.add(filteredItem);
                          }
                        }
                        filteredLists.add(filteredList);
                      }

                      MyExpandableListAdapter.children = filteredLists;
                      activity.expandableListAdapter.notifyDataSetChanged();

                      if(filteredText != null) {
                        activity.expandableListView.setFilterText(filteredText);
                      }
                    }
                    else if(order == 1) {

                      MyListAdapter.itemList = activity.getNonScheduledItem(MyDatabaseHelper.TODO_TABLE);

                      filteredList = new ArrayList<>();
                      for(Item nonScheduledItem : MyListAdapter.itemList) {
                        if(nonScheduledItem.getWhich_tag_belongs() == tag.getId()) {
                          filteredList.add(nonScheduledItem);
                        }
                      }

                      MyListAdapter.itemList = filteredList;
                      activity.listAdapter.notifyDataSetChanged();

                      if(filteredText != null) {
                        activity.listView.setFilterText(filteredText);
                      }
                    }
                    else if(order == 3) {

                      ManageListAdapter.nonScheduledLists = new ArrayList<>(
                          activity.generalSettings.getNonScheduledLists()
                      );

                      nonScheduledLists = new ArrayList<>();
                      for(NonScheduledList list : ManageListAdapter.nonScheduledLists) {
                        if(list.getWhich_tag_belongs() == tag.getId()) {
                          nonScheduledLists.add(list);
                        }
                      }

                      ManageListAdapter.nonScheduledLists = nonScheduledLists;
                      activity.manageListAdapter.notifyDataSetChanged();

                      if(filteredText != null) {
                        activity.listView.setFilterText(filteredText);
                      }
                    }
                  }

                  return false;
                }
              });

          if(tag.getId() == checked_tag) {
            SpannableString spannable = new SpannableString(tag.getName());
            spannable.setSpan(new ForegroundColorSpan(Color.RED), 0, tag.getName().length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tagItem.setTitle(spannable);
            tagItem.setChecked(true);
          }
        }

        MenuPopupHelper popupHelper = new MenuPopupHelper(activity, (MenuBuilder)popupMenu.getMenu(), tagSearchView);
        popupHelper.setForceShowIcon(true);

        popupHelper.show();
        return true;
      }
      case R.id.add_item: {

        if(order == 3) activity.showMainEditFragmentForList();
        else activity.showMainEditFragment();
        return true;
      }
      case R.id.sort: {

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

            activity.generalSettings.setNonScheduledLists(new ArrayList<>(ManageListAdapter.nonScheduledLists));
            int size = ManageListAdapter.nonScheduledLists.size();
            boolean is_updated = false;
            for(int i = 0; i < size; i++) {
              NonScheduledList list = activity.generalSettings.getNonScheduledLists().get(i);
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
              for(NonScheduledList list : activity.generalSettings.getNonScheduledLists()) {
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
    }

    return false;
  }
}
