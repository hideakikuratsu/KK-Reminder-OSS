package com.example.hideaki.reminder;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class MainEditFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener,
    Preference.OnPreferenceChangeListener {

  public static final String ITEM = "ITEM";
  public static final String LIST = "LIST";
  static final String TAG = MainEditFragment.class.getSimpleName();

  private EditTextPreference detail;
  private PreferenceScreen tag;
  private EditTextPreference notes;
  private PreferenceScreen interval_item;
  private PreferenceScreen day_repeat_item;
  private PreferenceScreen minute_repeat_item;
  static Item item;
  static String detail_str;
  static String notes_str;
  static Calendar final_cal;
  static NotifyInterval notifyInterval;
  static DayRepeat dayRepeat;
  static MinuteRepeat minuteRepeat;
  static boolean is_edit;
  static int color;
  static int order;
  static NonScheduledList list;
  private MainActivity activity;
  static List<Item> itemListToMove;
  static int checked_item_num;
  private static boolean is_moving_task;
  static boolean is_cloning_task;
  private Item nextItem;

  public static MainEditFragment newInstance() {

    MainEditFragment fragment = new MainEditFragment();

    Item item = new Item();
    is_edit = false;
    detail_str = "";
    notes_str = "";
    notifyInterval = new NotifyInterval();
    dayRepeat = new DayRepeat();
    minuteRepeat = new MinuteRepeat();
    final_cal = Calendar.getInstance();
    Bundle args = new Bundle();
    args.putSerializable(ITEM, item);
    fragment.setArguments(args);

    return fragment;
  }

  public static MainEditFragment newInstance(String detail) {

    MainEditFragment fragment = new MainEditFragment();

    Item item = new Item();
    is_edit = false;
    detail_str = detail;
    notes_str = "";
    notifyInterval = new NotifyInterval();
    dayRepeat = new DayRepeat();
    minuteRepeat = new MinuteRepeat();
    final_cal = Calendar.getInstance();
    Bundle args = new Bundle();
    args.putSerializable(ITEM, item);
    fragment.setArguments(args);

    return fragment;
  }

  public static MainEditFragment newInstance(Item item) {

    MainEditFragment fragment = new MainEditFragment();

    is_edit = true;
    detail_str = item.getDetail();
    notes_str = item.getNotes();
    notifyInterval = item.getNotify_interval().clone();
    dayRepeat = item.getDayRepeat().clone();
    minuteRepeat = item.getMinuteRepeat().clone();
    final_cal = (Calendar)item.getDate().clone();
    Bundle args = new Bundle();
    args.putSerializable(ITEM, item);
    fragment.setArguments(args);

    return fragment;
  }

  public static MainEditFragment newInstanceForList() {

    MainEditFragment fragment = new MainEditFragment();

    NonScheduledList list = new NonScheduledList();
    is_edit = false;
    detail_str = "";
    notes_str = "";
    Bundle args = new Bundle();
    args.putSerializable(LIST, list);
    fragment.setArguments(args);

    return fragment;
  }
  
  public static MainEditFragment newInstanceForList(NonScheduledList list) {

    MainEditFragment fragment = new MainEditFragment();

    is_edit = true;
    detail_str = list.getTitle();
    notes_str = list.getNotes();
    Bundle args = new Bundle();
    args.putSerializable(LIST, list);
    fragment.setArguments(args);

    return fragment;
  }

  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;

    checkArgument(checked_item_num >= 0);
    is_moving_task = false;

    if(checked_item_num == 0) {
      order = activity.order;
    }
    else {
      checked_item_num--;

      if(is_cloning_task) {
        order = activity.order;
        if(checked_item_num > 0) {
          nextItem = itemListToMove.get(checked_item_num - 1).copy();
        }
      }
      else {
        order = -1;
        is_moving_task = true;
        if(checked_item_num > 0) {
          nextItem = itemListToMove.get(checked_item_num - 1);
        }
      }
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.main_edit);
    setHasOptionsMenu(true);

    Bundle args = getArguments();
    if(order == 3) {
      list = (NonScheduledList)args.getSerializable(LIST);
      checkNotNull(list);
    }
    else {
      item = (Item)args.getSerializable(ITEM);
      checkNotNull(item);
    }

    if(!is_edit) {
      notifyInterval = activity.generalSettings.getNotifyInterval().clone();
    }

    //各プリファレンスの初期化
    PreferenceScreen rootPreferenceScreen = getPreferenceScreen();

    detail = (EditTextPreference)findPreference("detail");
    detail.setTitle(detail_str);
    detail.setOnPreferenceChangeListener(this);

    PreferenceCategory colorCategory = (PreferenceCategory)findPreference("color");
    PreferenceScreen primaryColor = (PreferenceScreen)findPreference("primary_color");
    primaryColor.setOnPreferenceClickListener(this);
    PreferenceScreen secondaryColor = (PreferenceScreen)findPreference("secondary_color");
    secondaryColor.setOnPreferenceClickListener(this);

    tag = (PreferenceScreen)findPreference("tag");
    tag.setOnPreferenceClickListener(this);

    interval_item = (PreferenceScreen)findPreference("interval");
    interval_item.setOnPreferenceClickListener(this);

    day_repeat_item = (PreferenceScreen)findPreference("repeat_day_unit");
    day_repeat_item.setOnPreferenceClickListener(this);
    minute_repeat_item = (PreferenceScreen)findPreference("repeat_minute_unit");
    minute_repeat_item.setOnPreferenceClickListener(this);

    PreferenceCategory notes_category = (PreferenceCategory)findPreference("notes_category");
    notes = (EditTextPreference)findPreference("notes");
    notes.setTitle(notes_str);
    notes.setOnPreferenceChangeListener(this);

    if(order == 0 || is_moving_task) {
      rootPreferenceScreen.removePreference(colorCategory);
    }
    else if(order == 1) {
      rootPreferenceScreen.removeAll();
      rootPreferenceScreen.addPreference(detail);
      rootPreferenceScreen.addPreference(tag);
      rootPreferenceScreen.addPreference(notes_category);
    }
    else if(order == 3) {
      rootPreferenceScreen.removeAll();
      rootPreferenceScreen.addPreference(detail);
      rootPreferenceScreen.addPreference(tag);
      rootPreferenceScreen.addPreference(colorCategory);
      rootPreferenceScreen.addPreference(notes_category);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    checkNotNull(view);

    view.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));
    view.setFocusableInTouchMode(true);
    view.requestFocus();
    view.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
          if((is_moving_task || is_cloning_task) && checked_item_num > 0) {
            activity.showMainEditFragment(nextItem, TAG);
          }
          else if(is_cloning_task) {
            is_cloning_task = false;
          }
        }
        return false;
      }
    });

    Toolbar toolbar = activity.findViewById(R.id.toolbar_layout);
    activity.setSupportActionBar(toolbar);
    ActionBar actionBar = activity.getSupportActionBar();
    checkNotNull(actionBar);

    activity.drawerToggle.setDrawerIndicatorEnabled(false);
    actionBar.setHomeAsUpIndicator(activity.upArrow);
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(R.string.edit);

    //Tagのラベルの初期化
    if(order == 0 || order == 1 || is_moving_task) {
      if(item.getWhich_tag_belongs() == 0) {
        tag.setSummary(activity.getString(R.string.non_tag));
      }
      else {
        tag.setSummary(activity.generalSettings.getTagById(item.getWhich_tag_belongs()).getName());
      }
    }
    else if(order == 3) {
      if(list.getWhich_tag_belongs() == 0) {
        tag.setSummary(activity.getString(R.string.non_tag));
      }
      else {
        tag.setSummary(activity.generalSettings.getTagById(list.getWhich_tag_belongs()).getName());
      }
    }

    //Repeatのラベルの初期化
    if(order == 0 || is_moving_task) {
      if(notifyInterval.getLabel() == null) interval_item.setSummary(R.string.none);
      else interval_item.setSummary(notifyInterval.getLabel());

      if(dayRepeat.getLabel() == null) day_repeat_item.setSummary(R.string.none);
      else day_repeat_item.setSummary(dayRepeat.getLabel());

      if(minuteRepeat.getLabel() == null) minute_repeat_item.setSummary(R.string.none);
      else minute_repeat_item.setSummary(minuteRepeat.getLabel());
    }

    return view;
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.main_edit_menu, menu);

    //完了メニューの実装
    MenuItem done_item = menu.findItem(R.id.done);
    Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_check_circle_24dp);
    checkNotNull(drawable);
    drawable = drawable.mutate();
    drawable.setColorFilter(activity.menu_item_color, PorterDuff.Mode.SRC_IN);
    done_item.setIcon(drawable);

    //削除メニューの実装
    MenuItem delete_item = menu.findItem(R.id.delete);
    if(is_edit && !is_moving_task && !is_cloning_task) {
      drawable = ContextCompat.getDrawable(activity, R.drawable.ic_delete_24dp);
      checkNotNull(drawable);
      drawable = drawable.mutate();
      drawable.setColorFilter(activity.menu_item_color, PorterDuff.Mode.SRC_IN);
      delete_item.setIcon(drawable);
      delete_item.setVisible(true);
    }
    else delete_item.setVisible(false);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    switch(item.getItemId()) {
      case R.id.done: {
        if((order == 0 || is_moving_task) && is_edit
            && MainEditFragment.item.getDate().getTimeInMillis() != final_cal.getTimeInMillis()
            && (MainEditFragment.dayRepeat.getSetted() != 0 || MainEditFragment.minuteRepeat.getWhich_setted() != 0)) {

          new AlertDialog.Builder(activity)
              .setMessage(R.string.repeat_conflict_dialog_message)
              .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                  if(MainEditFragment.item.getTime_altered() == 0) {
                    MainEditFragment.item.setOrg_date((Calendar)MainEditFragment.item.getDate().clone());
                  }
                  long altered_time = (final_cal.getTimeInMillis()
                      - MainEditFragment.item.getDate().getTimeInMillis()) / (1000 * 60);
                  MainEditFragment.item.addTime_altered(altered_time * 60 * 1000);

                  registerItem();
                }
              })
              .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                  MainEditFragment.item.setOrg_date((Calendar)final_cal.clone());
                  MainEditFragment.item.setTime_altered(0);

                  registerItem();
                }
              })
              .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
              })
              .show();
        }
        else registerItem();
        return true;
      }
      case R.id.delete: {

        new AlertDialog.Builder(activity)
            .setTitle(R.string.delete_dialog_title)
            .setMessage(R.string.delete_dialog_message)
            .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                if(order == 0) {
                  activity.deleteDB(MainEditFragment.item, MyDatabaseHelper.TODO_TABLE);
                  MyExpandableListAdapter.children = activity.getChildren(MyDatabaseHelper.TODO_TABLE);
                  activity.deleteAlarm(MainEditFragment.item);
                  activity.expandableListAdapter.notifyDataSetChanged();
                }
                else if(order == 1) {
                  activity.deleteDB(MainEditFragment.item, MyDatabaseHelper.TODO_TABLE);
                  MyListAdapter.itemList = activity.getNonScheduledItem(MyDatabaseHelper.TODO_TABLE);
                  activity.listAdapter.notifyDataSetChanged();
                }
                else if(order == 3) {
                  //GeneralSettingsとManageListAdapterへの反映
                  activity.generalSettings.getNonScheduledLists().remove(list.getOrder());
                  int size = activity.generalSettings.getNonScheduledLists().size();
                  for(int i = 0; i < size; i++) {
                    activity.generalSettings.getNonScheduledLists().get(i).setOrder(i);
                  }
                  ManageListAdapter.nonScheduledLists = new ArrayList<>(activity.generalSettings.getNonScheduledLists());
                  activity.manageListAdapter.notifyDataSetChanged();

                  long id = list.getId();
                  for(Item itemInList : activity.queryAllDB(MyDatabaseHelper.TODO_TABLE)) {
                    if(itemInList.getWhich_list_belongs() == id) {
                      activity.deleteDB(itemInList, MyDatabaseHelper.TODO_TABLE);
                    }
                  }

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

                  //データベースへの反映
                  activity.updateSettingsDB();
                }

                //データベースを更新したら、そのデータベースを端末暗号化ストレージへコピーする
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                  Context direct_boot_context = activity.createDeviceProtectedStorageContext();
                  direct_boot_context.moveDatabaseFrom(activity, MyDatabaseHelper.TODO_TABLE);
                }

                getFragmentManager().popBackStack();
              }
            })
            .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
              }
            })
            .show();

        return true;
      }
      case android.R.id.home: {

        getFragmentManager().popBackStack();

        if((is_moving_task || is_cloning_task) && checked_item_num > 0) {
          activity.showMainEditFragment(nextItem, TAG);
        }
        else if(is_cloning_task) {
          is_cloning_task = false;
        }
        return true;
      }
      default: {
        return super.onOptionsItemSelected(item);
      }
    }
  }

  @Override
  public boolean onPreferenceClick(Preference preference) {

    switch(preference.getKey()) {
      case "primary_color": {
        activity.showColorPickerListViewFragment(TAG);
        return true;
      }
      case "secondary_color": {
        list.setColor_primary(false);
        activity.showColorPickerListViewFragment(TAG);
        return true;
      }
      case "tag": {
        activity.showTagEditListViewFragment(TAG);
        return true;
      }
      case "interval": {
        transitionFragment(NotifyIntervalEditFragment.newInstance());
        return true;
      }
      case "repeat_day_unit": {
        transitionFragment(DayRepeatEditFragment.newInstance());
        return true;
      }
      case "repeat_minute_unit": {
        transitionFragment(MinuteRepeatEditFragment.newInstance());
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean onPreferenceChange(Preference preference, Object newValue) {

    switch(preference.getKey()) {
      case "detail": {
        detail_str = (String)newValue;
        detail.setTitle((String)newValue);
        return true;
      }
      case "notes": {
        notes_str = (String)newValue;
        notes.setTitle((String)newValue);
        return true;
      }
    }

    return false;
  }

  private void transitionFragment(PreferenceFragment next) {

    FragmentManager manager = getFragmentManager();
    manager
        .beginTransaction()
        .remove(this)
        .add(R.id.content, next)
        .addToBackStack(null)
        .commit();
  }

  private void registerItem() {

    if(order == 0 || order == 1 || is_moving_task) {

      item.setDetail(detail_str);
      item.setNotes(notes_str);

      if(order == 0 || is_moving_task) {

        item.setDate((Calendar)final_cal.clone());

        //notifyIntervalの登録
        notifyInterval.setTime(notifyInterval.getOrg_time());
        item.setNotify_interval(notifyInterval.clone());

        //dayRepeatの登録
        if(dayRepeat.getSetted() != 0) {
          if(dayRepeat.getSetted() == 1) dayRepeat.dayClear();
          else if(dayRepeat.getSetted() == (1 << 1)) dayRepeat.weekClear();
          else if(dayRepeat.getSetted() == (1 << 2)) {
            if(dayRepeat.isDays_of_month_setted()) dayRepeat.daysOfMonthClear();
            else dayRepeat.onTheMonthClear();
          }
          else if(dayRepeat.getSetted() == (1 << 3)) dayRepeat.yearClear();
        }
        else {
          dayRepeat.clear();
        }
        item.setDayRepeat(dayRepeat.clone());

        //minuteRepeatの登録
        if(minuteRepeat.getWhich_setted() != 0) {
          if(minuteRepeat.getWhich_setted() == 1) {
            minuteRepeat.setCount(minuteRepeat.getOrg_count());
            minuteRepeat.countClear();
          }
          else if(minuteRepeat.getWhich_setted() == (1 << 1)) {
            minuteRepeat.setDuration(minuteRepeat.getOrgDuration());
            minuteRepeat.durationClear();
          }
        }
        else {
          minuteRepeat.clear();
        }
        item.setMinuteRepeat(minuteRepeat.clone());

        item.setAlarm_stopped(false);

        if(is_moving_task) {
          item.setWhich_list_belongs(0);
        }

        if(is_edit && !is_cloning_task) {

          activity.updateDB(item, MyDatabaseHelper.TODO_TABLE);
          if(is_moving_task) {
            MyExpandableListAdapter.children = activity.getChildren(MyDatabaseHelper.TODO_TABLE);
            MyListAdapter.itemList = activity.getNonScheduledItem(MyDatabaseHelper.TODO_TABLE);
            activity.listAdapter.notifyDataSetChanged();
          }
          activity.expandableListAdapter.notifyDataSetChanged();
        }
        else {
          activity.addChildren(item, MyDatabaseHelper.TODO_TABLE);
        }

        activity.deleteAlarm(item);
        activity.setAlarm(item);
      }
      else if(order == 1) {

        //リストのIDをitemに登録する
        item.setWhich_list_belongs(
            activity.generalSettings.getNonScheduledLists().get(activity.which_menu_open - 1).getId()
        );

        if(is_edit && !is_cloning_task) {

          activity.listAdapter.notifyDataSetChanged();
          activity.updateDB(item, MyDatabaseHelper.TODO_TABLE);
        }
        else {

          MyListAdapter.itemList.add(0, item);
          int size = MyListAdapter.itemList.size();
          for(int i = 0; i < size; i++) {
            Item item = MyListAdapter.itemList.get(i);
            item.setOrder(i);
            if(i == 0) activity.insertDB(item, MyDatabaseHelper.TODO_TABLE);
            else activity.updateDB(item, MyDatabaseHelper.TODO_TABLE);
          }

          activity.listAdapter.notifyDataSetChanged();
        }
      }
    }
    else if(order == 3) {

      list.setTitle(detail_str);
      list.setNotes(notes_str);

      if(!is_edit) {
        //GeneralSettingsとManageListAdapterへの反映
        activity.generalSettings.getNonScheduledLists().add(0, list);
        int size = activity.generalSettings.getNonScheduledLists().size();
        for(int i = 0; i < size; i++) {
          activity.generalSettings.getNonScheduledLists().get(i).setOrder(i);
        }
        ManageListAdapter.nonScheduledLists = new ArrayList<>(activity.generalSettings.getNonScheduledLists());
      }
      activity.manageListAdapter.notifyDataSetChanged();

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

      //データベースへの反映
      activity.updateSettingsDB();
    }

    //データベースを更新したら、そのデータベースを端末暗号化ストレージへコピーする
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      Context direct_boot_context = activity.createDeviceProtectedStorageContext();
      direct_boot_context.moveDatabaseFrom(activity, MyDatabaseHelper.TODO_TABLE);
    }

    getFragmentManager().popBackStack();

    if((is_moving_task || is_cloning_task) && checked_item_num > 0) {
      activity.showMainEditFragment(nextItem, TAG);
    }
    else if(is_cloning_task) {
      is_cloning_task = false;
    }
  }
}
