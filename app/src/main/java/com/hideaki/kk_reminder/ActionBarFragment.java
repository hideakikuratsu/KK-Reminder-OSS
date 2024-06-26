package com.hideaki.kk_reminder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import static androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT;
import static com.hideaki.kk_reminder.UtilClass.IS_EXPANDABLE_TODO;
import static com.hideaki.kk_reminder.UtilClass.generateUniqueId;
import static com.hideaki.kk_reminder.UtilClass.setCursorDrawableColor;
import static java.util.Objects.requireNonNull;


public class ActionBarFragment extends Fragment {

  static final String TAG = ActionBarFragment.class.getSimpleName();
  private MainActivity activity;
  SearchView searchView;
  private int order;
  private ActionBar actionBar;
  private MenuItem alignTop;
  private MenuItem addItem;
  private MenuItem searchItem;
  private MenuItem tagSearchItem;
  private MenuItem sortItem;
  long checkedTag;
  List<List<ItemAdapter>> filteredLists;
  List<ItemAdapter> filteredList;
  List<NonScheduledListAdapter> nonScheduledLists;
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
  public void onAttach(@NonNull Context context) {

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
  public View onCreateView(
    @NonNull LayoutInflater inflater,
    ViewGroup container,
    Bundle savedInstanceState
  ) {

    Toolbar toolbar = activity.findViewById(R.id.toolbar_layout);
    activity.setSupportActionBar(toolbar);
    actionBar = activity.getSupportActionBar();
    requireNonNull(actionBar);

    activity.drawerToggle.setDrawerIndicatorEnabled(true);
    if(order == 3) {
      actionBar.setTitle(R.string.manage_lists_title);
    }
    else {
      actionBar.setTitle(null);
    }
    return inflater.inflate(R.layout.fragment_search, container, false);
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.reminder_menu, menu);

    // リストの先頭までジャンプするメニューボタンの実装
    alignTop = menu.findItem(R.id.align_top);
    initAlignTopItem();

    // 未完了と完了済みを切り替えるタグルボタンの実装
    toggleItem = menu.findItem(R.id.todo_done_toggle);
    initToggleItem();

    // タグ検索を行うメニューボタンの実装
    tagSearchItem = menu.findItem(R.id.tag_search);
    initTagSearchItem();

    // タスクの並び替えを行うメニューボタンの実装
    sortItem = menu.findItem(R.id.sort);
    initSortItem();

    // 新しくアイテムを追加するメニューボタンの実装
    addItem = menu.findItem(R.id.add_item);
    initAddItem();

    // 検索メニューの実装
    searchItem = menu.findItem(R.id.search_item);
    searchView = (SearchView)searchItem.getActionView();
    initSearchItem();

    // 各アイテムの表示処理
    alignTop.setVisible(false);
    searchItem.setVisible(false);
    toggleItem.setVisible(false);
    tagSearchItem.setVisible(false);
    sortItem.setVisible(false);
    addItem.setVisible(false);

    if(order == 0) {
      toggleItem.setVisible(true);
      searchItem.setVisible(true);
      if(activity.isExpandableTodo) {
        alignTop.setVisible(true);
        addItem.setVisible(true);
      }
    }
    else if(order == 1) {
      toggleItem.setVisible(true);
      searchItem.setVisible(true);
      if(activity.generalSettings.getNonScheduledLists().get(activity.whichMenuOpen - 1).isTodo()) {
        addItem.setVisible(true);
        sortItem.setVisible(true);
      }
    }
    else if(order == 3) {
      addItem.setVisible(true);
      searchItem.setVisible(true);
      sortItem.setVisible(true);
    }
  }

  @SuppressLint("RestrictedApi")
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    int itemId = item.getItemId();
    if(itemId == R.id.align_top) {
      activity.expandableListView.smoothScrollToPosition(0);
      return true;
    }
    else if(itemId == R.id.tag_search) {
      View tagSearchView = activity.findViewById(R.id.tag_search);
      PopupMenu popupMenu = new PopupMenu(activity, tagSearchView);
      final Menu menu = popupMenu.getMenu();

      for(final TagAdapter tag : activity.generalSettings.getTagList()) {

        Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_pallet_24dp);
        requireNonNull(drawable);
        drawable = drawable.mutate();
        if(tag.getPrimaryColor() != 0) {
          drawable.setColorFilter(new PorterDuffColorFilter(
              tag.getPrimaryColor(),
              PorterDuff.Mode.SRC_IN
          ));
        }
        else {
          drawable.setColorFilter(new PorterDuffColorFilter(
              ContextCompat.getColor(activity, R.color.iconGray),
              PorterDuff.Mode.SRC_IN
          ));
        }

        MenuItem tagItem = menu.add(Menu.NONE, Menu.NONE, tag.getOrder(), tag.getName())
            .setIcon(drawable)
            .setOnMenuItemClickListener(item1 -> {

              if(tag.getId() != checkedTag) {
                int size = menu.size();
                for(int i = 0; i < size; i++) {
                  MenuItem menuItem = menu.getItem(i);
                  if(menuItem.isChecked()) {
                    menuItem.setChecked(false);
                  }
                }
                item1.setChecked(true);
                checkedTag = tag.getId();

                if(order == 0) {

                  if(activity.isExpandableTodo) {
                    MyExpandableListAdapter.children =
                        activity.getChildren(MyDatabaseHelper.TODO_TABLE);

                    filteredLists = new ArrayList<>();
                    for(List<ItemAdapter> itemList : MyExpandableListAdapter.children) {
                      List<ItemAdapter> filteredList = new ArrayList<>();
                      for(ItemAdapter filteredItem : itemList) {
                        if(filteredItem.getWhichTagBelongs() == tag.getId()) {
                          filteredList.add(filteredItem);
                        }
                      }
                      filteredLists.add(filteredList);
                    }

                    MyExpandableListAdapter.children = filteredLists;
                    activity.expandableListAdapter.notifyDataSetChanged();

                    if(filteredText != null) {
//                          activity.expandableListView.setFilterText(filteredText);
                      activity.expandableListAdapter.getFilter().filter(filteredText);
                    }
                  }
                  else {
                    DoneListAdapter.itemList = activity.getDoneItem();

                    filteredList = new ArrayList<>();
                    for(ItemAdapter filteredItem : DoneListAdapter.itemList) {
                      if(filteredItem.getWhichTagBelongs() == tag.getId()) {
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

                  if(
                      activity.generalSettings
                          .getNonScheduledLists()
                          .get(activity.whichMenuOpen - 1)
                          .isTodo()
                  ) {
                    MyListAdapter.itemList =
                        activity.getNonScheduledItem(MyDatabaseHelper.TODO_TABLE);

                    filteredList = new ArrayList<>();
                    for(ItemAdapter filteredItem : MyListAdapter.itemList) {
                      if(filteredItem.getWhichTagBelongs() == tag.getId()) {
                        filteredList.add(filteredItem);
                      }
                    }

                    MyListAdapter.itemList = filteredList;
                    activity.listAdapter.notifyDataSetChanged();
                  }
                  else {
                    DoneListAdapter.itemList = activity.getDoneItem();

                    filteredList = new ArrayList<>();
                    for(ItemAdapter filteredItem : DoneListAdapter.itemList) {
                      if(filteredItem.getWhichTagBelongs() == tag.getId()) {
                        filteredList.add(filteredItem);
                      }
                    }

                    DoneListAdapter.itemList = filteredList;
                    activity.doneListAdapter.notifyDataSetChanged();
                  }
                  if(filteredText != null) {
                    activity.listView.setFilterText(filteredText);
                  }
                }
                else if(order == 3) {

                  ManageListAdapter.nonScheduledLists = new ArrayList<>(
                      activity.generalSettings.getNonScheduledLists()
                  );

                  nonScheduledLists = new ArrayList<>();
                  for(NonScheduledListAdapter list : ManageListAdapter.nonScheduledLists) {
                    if(list.getWhichTagBelongs() == tag.getId()) {
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
            });

        if(tag.getId() == checkedTag) {
          SpannableString spannable = new SpannableString(tag.getName());
          spannable.setSpan(
              new ForegroundColorSpan(Color.RED),
              0,
              tag.getName().length(),
              Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
          );
          tagItem.setTitle(spannable);
//            tagItem.setChecked(true);
        }
      }

      MenuPopupHelper popupHelper =
          new MenuPopupHelper(activity, (MenuBuilder)popupMenu.getMenu(), tagSearchView);
      popupHelper.setForceShowIcon(true);
      popupHelper.show();

      return true;
    }
    else if(itemId == R.id.add_item) {
      if(order == 0) {
        activity.showMainEditFragment();
      }
      else if(order == 1) {
        activity.showMainEditFragment();
      }
      else if(order == 3) {
        activity.showMainEditFragmentForList();
      }
      return true;
    }
    else if(itemId == R.id.sort) {
      if(order == 1) {
        MyListAdapter.isSorting = !MyListAdapter.isSorting;
        if(MyListAdapter.isSorting) {
          activity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
          actionBar.setDisplayHomeAsUpEnabled(false);
          searchItem.setVisible(false);
          addItem.setVisible(false);
          toggleItem.setVisible(false);
        }
        else {
          activity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
          actionBar.setDisplayHomeAsUpEnabled(true);
          searchItem.setVisible(true);
          addItem.setVisible(true);
          toggleItem.setVisible(true);

          int size = MyListAdapter.itemList.size();
          for(int i = 0; i < size; i++) {
            ItemAdapter tmpItem = MyListAdapter.itemList.get(i);
            if(tmpItem.getOrder() != i) {
              tmpItem.setOrder(i);
              activity.updateDB(tmpItem, MyDatabaseHelper.TODO_TABLE);
            }
          }
        }

        activity.listAdapter.notifyDataSetChanged();
      }
      else if(order == 3) {
        ManageListAdapter.isSorting = !ManageListAdapter.isSorting;
        if(ManageListAdapter.isSorting) {
          activity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
          actionBar.setDisplayHomeAsUpEnabled(false);
          searchItem.setVisible(false);
          addItem.setVisible(false);
        }
        else {
          activity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
          actionBar.setDisplayHomeAsUpEnabled(true);
          searchItem.setVisible(true);
          addItem.setVisible(true);

          activity.generalSettings.setNonScheduledLists(new ArrayList<>(ManageListAdapter.nonScheduledLists));
          int size = ManageListAdapter.nonScheduledLists.size();
          boolean isUpdated = false;
          for(int i = 0; i < size; i++) {
            NonScheduledListAdapter list = activity.generalSettings.getNonScheduledLists().get(i);
            if(list.getOrder() != i) {
              list.setOrder(i);
              isUpdated = true;
            }
          }

          if(isUpdated) {

            // 一旦reminder_listグループ内のアイテムをすべて消してから元に戻すことで新しく追加したリストの順番を追加した順に並び替える

            // デフォルトアイテムのリストア
            activity.menu.removeGroup(R.id.reminder_list);
            activity.menu
                .add(R.id.reminder_list, R.id.scheduled_list, 0, R.string.nav_scheduled_item)
                .setIcon(R.drawable.ic_time)
                .setCheckable(true);
            activity.menu.add(R.id.reminder_list, R.id.add_list, 2, R.string.add_list)
                .setIcon(R.drawable.ic_add_24dp)
                .setCheckable(false);

            // 新しく追加したリストのリストア
            for(NonScheduledListAdapter list : activity.generalSettings.getNonScheduledLists()) {
              Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_my_list_24dp);
              requireNonNull(drawable);
              drawable = drawable.mutate();
              if(list.getColor() != 0) {
                drawable.setColorFilter(new PorterDuffColorFilter(
                    list.getColor(),
                    PorterDuff.Mode.SRC_IN
                ));
              }
              else {
                drawable.setColorFilter(new PorterDuffColorFilter(
                    ContextCompat.getColor(activity, R.color.iconGray),
                    PorterDuff.Mode.SRC_IN
                ));
              }
              activity.menu.add(R.id.reminder_list, generateUniqueId(), 1, list.getTitle())
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

  private void initToggleItem() {

    ConstraintLayout toggleLayout = (ConstraintLayout)toggleItem.getActionView();
    requireNonNull(toggleLayout);
    todo = toggleLayout.findViewById(R.id.todo);
    done = toggleLayout.findViewById(R.id.done);

    todoDrawable = (GradientDrawable)todo.getBackground();
    todoDrawable = (GradientDrawable)todoDrawable.mutate();
    doneDrawable = (GradientDrawable)done.getBackground();
    doneDrawable = (GradientDrawable)doneDrawable.mutate();

    if(order == 0) {
      if(activity.isExpandableTodo) {
        setTodoPushedColor();
      }
      else {
        setDonePushedColor();
      }
    }
    else if(order == 1) {
      if(activity.generalSettings.getNonScheduledLists().get(activity.whichMenuOpen - 1).isTodo()) {
        setTodoPushedColor();
      }
      else {
        setDonePushedColor();
      }
    }
    todoDrawable.setCornerRadius(8.0f);
    doneDrawable.setCornerRadius(8.0f);

    todo.setOnClickListener(v -> {

      if(order == 0) {
        if(!activity.isExpandableTodo) {
          alignTop.setVisible(true);
          addItem.setVisible(true);
          activity.setBooleanGeneralInSharedPreferences(
            IS_EXPANDABLE_TODO, true
          );
          activity.showExpandableListViewFragment();

          setTodoPushedColor();
        }
      }
      else if(order == 1) {
        if(
          !activity.generalSettings
            .getNonScheduledLists()
            .get(activity.whichMenuOpen - 1)
            .isTodo()
        ) {
          addItem.setVisible(true);
          sortItem.setVisible(true);
          activity.generalSettings
            .getNonScheduledLists()
            .get(activity.whichMenuOpen - 1)
            .setIsTodo(true);
          activity.updateSettingsDB();
          activity.showListViewFragment();

          setTodoPushedColor();
        }
      }
    });

    done.setOnClickListener(v -> {

      if(order == 0) {
        if(activity.isExpandableTodo) {
          alignTop.setVisible(false);
          addItem.setVisible(false);
          activity.setBooleanGeneralInSharedPreferences(
            IS_EXPANDABLE_TODO, false
          );
          activity.showDoneListViewFragment();

          setDonePushedColor();
        }
      }
      else if(order == 1) {
        if(
          activity.generalSettings
            .getNonScheduledLists()
            .get(activity.whichMenuOpen - 1)
            .isTodo()
        ) {
          addItem.setVisible(false);
          sortItem.setVisible(false);
          activity.generalSettings
            .getNonScheduledLists()
            .get(activity.whichMenuOpen - 1)
            .setIsTodo(false);
          activity.updateSettingsDB();
          activity.showDoneListViewFragment();

          setDonePushedColor();
        }
      }
    });
  }

  private void setTodoPushedColor() {

    todoDrawable.setColor(activity.statusBarColor);
    doneDrawable.setColor(activity.menuBackgroundColor);
    todoDrawable.setStroke(3, activity.accentColor);
    doneDrawable.setStroke(3, activity.menuItemColor);
    todo.setTextColor(activity.accentColor);
    done.setTextColor(activity.menuItemColor);
  }

  private void setDonePushedColor() {

    todoDrawable.setColor(activity.menuBackgroundColor);
    doneDrawable.setColor(activity.statusBarColor);
    todoDrawable.setStroke(3, activity.menuItemColor);
    doneDrawable.setStroke(3, activity.accentColor);
    todo.setTextColor(activity.menuItemColor);
    done.setTextColor(activity.accentColor);
  }

  private void initTagSearchItem() {

    Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_pallet_24dp);
    requireNonNull(drawable);
    drawable = drawable.mutate();
    drawable.setColorFilter(new PorterDuffColorFilter(
      activity.menuItemColor,
      PorterDuff.Mode.SRC_IN
    ));
    tagSearchItem.setIcon(drawable);
  }

  private void initSortItem() {

    Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_sort_24dp);
    requireNonNull(drawable);
    drawable = drawable.mutate();
    drawable.setColorFilter(new PorterDuffColorFilter(
      activity.menuItemColor,
      PorterDuff.Mode.SRC_IN
    ));
    sortItem.setIcon(drawable);
  }

  private void initAddItem() {

    Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_add_circle_24dp);
    requireNonNull(drawable);
    drawable = drawable.mutate();
    drawable.setColorFilter(new PorterDuffColorFilter(
      activity.menuItemColor,
      PorterDuff.Mode.SRC_IN
    ));
    addItem.setIcon(drawable);
  }

  private void initAlignTopItem() {

    Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_vertical_align_top_24dp);
    requireNonNull(drawable);
    drawable = drawable.mutate();
    drawable.setColorFilter(new PorterDuffColorFilter(
      activity.menuItemColor,
      PorterDuff.Mode.SRC_IN
    ));
    alignTop.setIcon(drawable);
  }

  private void initSearchItem() {

    int searchColor;
    if(activity.menuItemColor == Color.parseColor("#000000")) {
      searchColor = Color.parseColor("#595757");
    }
    else if(activity.menuItemColor == Color.parseColor("#FFFFFF")) {
      //noinspection SpellCheckingInspection
      searchColor = Color.parseColor("#C9CACA");
    }
    else {
      searchColor = activity.menuItemColor;
    }

    // アイコンの色
    Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_search_white_24dp);
    requireNonNull(drawable);
    drawable = drawable.mutate();
    drawable.setColorFilter(new PorterDuffColorFilter(
      activity.menuItemColor,
      PorterDuff.Mode.SRC_IN
    ));
    ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_button);
    searchIcon.setImageDrawable(drawable);

    EditText searchTextView = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
    // カーソルの色
    setCursorDrawableColor(activity, searchTextView);
    // 検索中文字の色
    searchTextView.setTextColor(activity.menuItemColor);
    // 閉じるボタンの色
    ImageView searchClose = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
    searchClose.setColorFilter(searchColor);
    // ヒントの色
    String strColor = String.format("#%06X", 0xFFFFFF & searchColor);
    searchView.setQueryHint(HtmlCompat.fromHtml(
      "<font color = " +
        strColor +
        ">" +
        getResources().getString(R.string.search_hint) +
        "</font>", FROM_HTML_MODE_COMPACT)
    );
    searchView.setIconifiedByDefault(true);
    searchView.setSubmitButtonEnabled(false);

    // 検索アイコン押下時の処理
    searchView.setOnSearchClickListener(v -> {

      alignTop.setVisible(false);
      toggleItem.setVisible(false);
      sortItem.setVisible(false);
      addItem.setVisible(false);
      tagSearchItem.setVisible(true);

      checkedTag = -1;
      searchView.requestFocus();
    });

    searchView.setOnCloseListener(() -> {

      // 各アイテムの表示処理
      if(order == 0) {
        toggleItem.setVisible(true);
        if(activity.isExpandableTodo) {
          alignTop.setVisible(true);
          addItem.setVisible(true);
        }
      }
      else if(order == 1) {
        toggleItem.setVisible(true);
        if(
          activity.generalSettings
            .getNonScheduledLists()
            .get(activity.whichMenuOpen - 1)
            .isTodo()
        ) {
          addItem.setVisible(true);
          sortItem.setVisible(true);
        }
      }
      else if(order == 3) {
        addItem.setVisible(true);
        sortItem.setVisible(true);
      }
      tagSearchItem.setVisible(false);

      if(checkedTag != -1) {
        if(order == 0) {
          if(activity.isExpandableTodo) {
            MyExpandableListAdapter.children = activity.getChildren(MyDatabaseHelper.TODO_TABLE);
            activity.expandableListAdapter.notifyDataSetChanged();
          }
          else {
            DoneListAdapter.itemList = activity.getDoneItem();
            activity.doneListAdapter.notifyDataSetChanged();
          }
        }
        else if(order == 1) {
          if(
            activity.generalSettings
              .getNonScheduledLists()
              .get(activity.whichMenuOpen - 1)
              .isTodo()
          ) {
            MyListAdapter.itemList = activity.getNonScheduledItem(MyDatabaseHelper.TODO_TABLE);
            activity.listAdapter.notifyDataSetChanged();
          }
          else {
            DoneListAdapter.itemList = activity.getDoneItem();
            activity.doneListAdapter.notifyDataSetChanged();
          }
        }
        else if(order == 3) {
          ManageListAdapter.nonScheduledLists =
            new ArrayList<>(activity.generalSettings.getNonScheduledLists());
          activity.manageListAdapter.notifyDataSetChanged();
        }
      }

      return false;
    });

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {

        return false;
      }

      @Override
      public boolean onQueryTextChange(String text) {

        if(text == null || text.isEmpty()) {
          if(order == 0 && activity.isExpandableTodo) {
            activity.expandableListView.clearTextFilter();
            activity.expandableListAdapter.getFilter().filter("");
          }
          else {
            activity.listView.clearTextFilter();
          }

          checkedTag = -1;
          filteredText = null;
        }
        else {
          if(order == 0 && activity.isExpandableTodo) {
//            activity.expandableListView.setFilterText(text);
            activity.expandableListAdapter.getFilter().filter(text);
          }
          else {
            activity.listView.setFilterText(text);
          }

          filteredText = text;
        }

        return false;
      }
    });
  }
}
