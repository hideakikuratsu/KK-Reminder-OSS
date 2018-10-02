package com.example.hideaki.reminder;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


public class ActionBarFragment extends Fragment {

  static final String TAG = ActionBarFragment.class.getSimpleName();
  private MainActivity activity;
  SearchView searchView;
  private int order;
  private ActionBar actionBar;
  private MenuItem addItem;
  private MenuItem searchItem;
  private MenuItem tagSearchItem;
  private MenuItem sortItem;
  long checked_tag;
  List<List<Item>> filteredLists;
  List<Item> filteredList;
  List<NonScheduledList> nonScheduledLists;
  private String filteredText;
  private MenuItem toggleItem;
  private TextView todo;
  private TextView done;
  private GradientDrawable todoDrawable;
  private GradientDrawable doneDrawable;

  public static ActionBarFragment newInstance() {

    return new ActionBarFragment();
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
    order = activity.order;
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
    if(order == 3) {
      actionBar.setTitle(R.string.manage_lists_title);
    }
    else actionBar.setTitle(null);
    return inflater.inflate(R.layout.fragment_search, container, false);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.reminder_menu, menu);

    //未完了と完了済みを切り替えるタグルボタンの実装
    toggleItem = menu.findItem(R.id.todo_done_toggle);
    initToggleItem();

    //タグ検索を行うメニューボタンの実装
    tagSearchItem = menu.findItem(R.id.tag_search);
    initTagSearchItem();

    //タスクの並び替えを行うメニューボタンの実装
    sortItem = menu.findItem(R.id.sort);
    initSortItem();

    //新しくアイテムを追加するメニューボタンの実装
    addItem = menu.findItem(R.id.add_item);
    initAddItem();

    //検索メニューの実装
    searchItem = menu.findItem(R.id.search_item);
    searchView = (SearchView)searchItem.getActionView();
    initSearchItem();

    //各アイテムの表示処理
    toggleItem.setVisible(false);
    tagSearchItem.setVisible(false);
    sortItem.setVisible(false);
    addItem.setVisible(false);

    if(order == 0) {
      toggleItem.setVisible(true);
      if(activity.generalSettings.isExpandable_todo()) {
        addItem.setVisible(true);
      }
    }
    else if(order == 1) {
      toggleItem.setVisible(true);
      if(activity.generalSettings.getNonScheduledLists().get(activity.which_menu_open - 1).isTodo()) {
        addItem.setVisible(true);
        sortItem.setVisible(true);
      }
    }
    else if(order == 3) {
      addItem.setVisible(true);
      sortItem.setVisible(true);
    }
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
              .setIcon(drawable)
              .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
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

                      if(activity.generalSettings.isExpandable_todo()) {
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
                      else {
                        DoneListAdapter.itemList = activity.getDoneItem();

                        filteredList = new ArrayList<>();
                        for(Item filteredItem : DoneListAdapter.itemList) {
                          if(filteredItem.getWhich_tag_belongs() == tag.getId()) {
                            filteredList.add(filteredItem);
                          }
                        }

                        DoneListAdapter.itemList = filteredList;
                        activity.doneListAdapter.notifyDataSetChanged();

                        if(filteredText != null) {
                          activity.listView.setFilterText(filteredText);
                        }
                      }
                    }
                    else if(order == 1) {

                      if(activity.generalSettings.getNonScheduledLists().get(activity.which_menu_open - 1).isTodo()) {
                        MyListAdapter.itemList = activity.getNonScheduledItem(MyDatabaseHelper.TODO_TABLE);

                        filteredList = new ArrayList<>();
                        for(Item filteredItem : MyListAdapter.itemList) {
                          if(filteredItem.getWhich_tag_belongs() == tag.getId()) {
                            filteredList.add(filteredItem);
                          }
                        }

                        MyListAdapter.itemList = filteredList;
                        activity.listAdapter.notifyDataSetChanged();

                        if(filteredText != null) {
                          activity.listView.setFilterText(filteredText);
                        }
                      }
                      else {
                        DoneListAdapter.itemList = activity.getDoneItem();

                        filteredList = new ArrayList<>();
                        for(Item filteredItem : DoneListAdapter.itemList) {
                          if(filteredItem.getWhich_tag_belongs() == tag.getId()) {
                            filteredList.add(filteredItem);
                          }
                        }

                        DoneListAdapter.itemList = filteredList;
                        activity.doneListAdapter.notifyDataSetChanged();

                        if(filteredText != null) {
                          activity.listView.setFilterText(filteredText);
                        }
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
//            tagItem.setChecked(true);
          }
        }

        MenuPopupHelper popupHelper = new MenuPopupHelper(activity, (MenuBuilder)popupMenu.getMenu(), tagSearchView);
        popupHelper.setForceShowIcon(true);
        popupHelper.show();

        return true;
      }
      case R.id.add_item: {

        if(order == 0) activity.showMainEditFragment(ExpandableListViewFragment.TAG);
        else if(order == 1) activity.showMainEditFragment(ListViewFragment.TAG);
        else if(order == 3) activity.showMainEditFragmentForList(ManageListViewFragment.TAG);
        return true;
      }
      case R.id.sort: {

        if(order == 1) {
          MyListAdapter.is_sorting = !MyListAdapter.is_sorting;
          if(MyListAdapter.is_sorting) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            searchItem.setVisible(false);
            addItem.setVisible(false);
            toggleItem.setVisible(false);
          }
          else {
            actionBar.setDisplayHomeAsUpEnabled(true);
            searchItem.setVisible(true);
            addItem.setVisible(true);
            toggleItem.setVisible(true);

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
            searchItem.setVisible(false);
            addItem.setVisible(false);
          }
          else {
            actionBar.setDisplayHomeAsUpEnabled(true);
            searchItem.setVisible(true);
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
                if(list.getColor() != 0) {
                  drawable.setColorFilter(list.getColor(), PorterDuff.Mode.SRC_IN);
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

  private void initToggleItem() {

    ConstraintLayout toggleLayout = (ConstraintLayout)toggleItem.getActionView();
    todo = toggleLayout.findViewById(R.id.todo);
    done = toggleLayout.findViewById(R.id.done);

    todoDrawable = (GradientDrawable)todo.getBackground();
    todoDrawable = (GradientDrawable)todoDrawable.mutate();
    doneDrawable = (GradientDrawable)done.getBackground();
    doneDrawable = (GradientDrawable)doneDrawable.mutate();

    if(order == 0) {
      if(activity.generalSettings.isExpandable_todo()) {
        setTodoPushedColor();
      }
      else setDonePushedColor();
    }
    else if(order == 1) {
      if(activity.generalSettings.getNonScheduledLists().get(activity.which_menu_open - 1).isTodo()) {
        setTodoPushedColor();
      }
      else setDonePushedColor();
    }
    todoDrawable.setCornerRadius(8.0f);
    doneDrawable.setCornerRadius(8.0f);

    todo.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if(order == 0) {
          if(!activity.generalSettings.isExpandable_todo()) {
            addItem.setVisible(true);
            activity.generalSettings.setExpandable_todo(true);
            activity.updateSettingsDB();
            activity.showExpandableListViewFragment(DoneListViewFragment.TAG);

            setTodoPushedColor();
          }
        }
        else if(order == 1) {
          if(!activity.generalSettings.getNonScheduledLists().get(activity.which_menu_open - 1).isTodo()) {
            addItem.setVisible(true);
            sortItem.setVisible(true);
            activity.generalSettings.getNonScheduledLists().get(activity.which_menu_open - 1).setTodo(true);
            activity.updateSettingsDB();
            activity.showListViewFragment(DoneListViewFragment.TAG);

            setTodoPushedColor();
          }
        }
      }
    });

    done.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if(order == 0) {
          if(activity.generalSettings.isExpandable_todo()) {
            addItem.setVisible(false);
            activity.generalSettings.setExpandable_todo(false);
            activity.updateSettingsDB();
            activity.showDoneListViewFragment(ExpandableListViewFragment.TAG);

            setDonePushedColor();
          }
        }
        else if(order == 1) {
          if(activity.generalSettings.getNonScheduledLists().get(activity.which_menu_open - 1).isTodo()) {
            addItem.setVisible(false);
            sortItem.setVisible(false);
            activity.generalSettings.getNonScheduledLists().get(activity.which_menu_open - 1).setTodo(false);
            activity.updateSettingsDB();
            activity.showDoneListViewFragment(ListViewFragment.TAG);

            setDonePushedColor();
          }
        }
      }
    });
  }

  private void setTodoPushedColor() {

    todoDrawable.setColor(activity.status_bar_color);
    doneDrawable.setColor(activity.menu_background_color);
    todoDrawable.setStroke(3, activity.accent_color);
    doneDrawable.setStroke(3, activity.menu_item_color);
    todo.setTextColor(activity.accent_color);
    done.setTextColor(activity.menu_item_color);
  }

  private void setDonePushedColor() {

    todoDrawable.setColor(activity.menu_background_color);
    doneDrawable.setColor(activity.status_bar_color);
    todoDrawable.setStroke(3, activity.menu_item_color);
    doneDrawable.setStroke(3, activity.accent_color);
    todo.setTextColor(activity.menu_item_color);
    done.setTextColor(activity.accent_color);
  }

  private void initTagSearchItem() {

    Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_pallet_24dp);
    checkNotNull(drawable);
    drawable = drawable.mutate();
    drawable.setColorFilter(activity.menu_item_color, PorterDuff.Mode.SRC_IN);
    tagSearchItem.setIcon(drawable);
  }

  private void initSortItem() {

    Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_sort_24dp);
    checkNotNull(drawable);
    drawable = drawable.mutate();
    drawable.setColorFilter(activity.menu_item_color, PorterDuff.Mode.SRC_IN);
    sortItem.setIcon(drawable);
  }

  private void initAddItem() {

    Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_add_circle_24dp);
    checkNotNull(drawable);
    drawable = drawable.mutate();
    drawable.setColorFilter(activity.menu_item_color, PorterDuff.Mode.SRC_IN);
    addItem.setIcon(drawable);
  }

  private void initSearchItem() {

    Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_search_white_24dp);
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

        toggleItem.setVisible(false);
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

        //各アイテムの表示処理
        if(order == 0) {
          toggleItem.setVisible(true);
          if(activity.generalSettings.isExpandable_todo()) {
            addItem.setVisible(true);
          }
        }
        else if(order == 1) {
          toggleItem.setVisible(true);
          if(activity.generalSettings.getNonScheduledLists().get(activity.which_menu_open - 1).isTodo()) {
            addItem.setVisible(true);
            sortItem.setVisible(true);
          }
        }
        else if(order == 3) {
          addItem.setVisible(true);
          sortItem.setVisible(true);
        }
        tagSearchItem.setVisible(false);

        if(checked_tag != -1) {
          if(order == 0) {
            if(activity.generalSettings.isExpandable_todo()) {
              MyExpandableListAdapter.children = activity.getChildren(MyDatabaseHelper.TODO_TABLE);
            }
            else {
              DoneListAdapter.itemList = activity.getDoneItem();
            }
          }
          else if(order == 1) {
            if(activity.generalSettings.getNonScheduledLists().get(activity.which_menu_open - 1).isTodo()) {
              MyListAdapter.itemList = activity.getNonScheduledItem(MyDatabaseHelper.TODO_TABLE);
            }
            else {
              DoneListAdapter.itemList = activity.getDoneItem();
            }
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
          if(order == 0 && activity.generalSettings.isExpandable_todo()) {
            activity.expandableListView.clearTextFilter();
          }
          else activity.listView.clearTextFilter();

          checked_tag = -1;
          filteredText = null;
        }
        else {
          if(order == 0 && activity.generalSettings.isExpandable_todo()) {
            activity.expandableListView.setFilterText(text);
          }
          else activity.listView.setFilterText(text);

          filteredText = text;
        }

        return false;
      }
    });
  }
}
