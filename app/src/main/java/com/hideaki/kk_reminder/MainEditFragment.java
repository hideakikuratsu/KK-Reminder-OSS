package com.hideaki.kk_reminder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.takisoft.fix.support.v7.preference.EditTextPreference;
import com.takisoft.fix.support.v7.preference.PreferenceCategory;
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.hideaki.kk_reminder.UtilClass.ITEM;
import static com.hideaki.kk_reminder.UtilClass.LIST;
import static com.hideaki.kk_reminder.UtilClass.LOCALE;
import static com.hideaki.kk_reminder.UtilClass.REQUEST_CODE_RINGTONE_PICKER;

public class MainEditFragmentCompat extends BasePreferenceFragmentCompat implements Preference.OnPreferenceClickListener,
    Preference.OnPreferenceChangeListener {

  static final String TAG = MainEditFragmentCompat.class.getSimpleName();

  private EditTextPreference detail;
  static PreferenceScreen datePicker;
  static PreferenceScreen timePicker;
  private PreferenceScreen tag;
  private PreferenceScreen intervalItem;
  private PreferenceScreen dayRepeatItem;
  private PreferenceScreen minuteRepeatItem;
  private Preference pickAlarm;
  private PreferenceScreen notes;
  static Item item;
  static String detail_str;
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
  static boolean is_notes_popping; //Notesフラグメントで戻るボタンを押したときに一気にもとの画面に戻るために使う
  static boolean is_main_popping;
  private boolean next_edit_exists;
  private boolean is_destroyed;

  public static MainEditFragmentCompat newInstance() {

    MainEditFragmentCompat fragment = new MainEditFragmentCompat();

    Item item = new Item();
    is_edit = false;
    detail_str = "";
    notifyInterval = new NotifyInterval();
    dayRepeat = new DayRepeat();
    minuteRepeat = new MinuteRepeat();
    final_cal = Calendar.getInstance();
    final_cal.set(Calendar.SECOND, 0);
    final_cal.set(Calendar.MILLISECOND, 0);
    Bundle args = new Bundle();
    args.putSerializable(ITEM, item);
    fragment.setArguments(args);

    return fragment;
  }

  public static MainEditFragmentCompat newInstance(String detail) {

    MainEditFragmentCompat fragment = new MainEditFragmentCompat();

    Item item = new Item();
    is_edit = false;
    detail_str = detail;
    notifyInterval = new NotifyInterval();
    dayRepeat = new DayRepeat();
    minuteRepeat = new MinuteRepeat();
    final_cal = Calendar.getInstance();
    final_cal.set(Calendar.SECOND, 0);
    final_cal.set(Calendar.MILLISECOND, 0);
    Bundle args = new Bundle();
    args.putSerializable(ITEM, item);
    fragment.setArguments(args);

    return fragment;
  }

  public static MainEditFragmentCompat newInstance(Item item) {

    MainEditFragmentCompat fragment = new MainEditFragmentCompat();

    is_edit = true;
    detail_str = item.getDetail();
    notifyInterval = item.getNotify_interval().clone();
    dayRepeat = item.getDayRepeat().clone();
    minuteRepeat = item.getMinuteRepeat().clone();
    final_cal = (Calendar)item.getDate().clone();
    Bundle args = new Bundle();
    args.putSerializable(ITEM, item);
    fragment.setArguments(args);

    return fragment;
  }

  public static MainEditFragmentCompat newInstanceForList() {

    MainEditFragmentCompat fragment = new MainEditFragmentCompat();

    NonScheduledList list = new NonScheduledList();
    is_edit = false;
    detail_str = "";
    Bundle args = new Bundle();
    args.putSerializable(LIST, list);
    fragment.setArguments(args);

    return fragment;
  }
  
  public static MainEditFragmentCompat newInstanceForList(NonScheduledList list) {

    MainEditFragmentCompat fragment = new MainEditFragmentCompat();

    is_edit = true;
    detail_str = list.getTitle();
    Bundle args = new Bundle();
    args.putSerializable(LIST, list);
    fragment.setArguments(args);

    return fragment;
  }

  @TargetApi(23)
  @Override
  public void onAttach(Context context) {

    super.onAttach(context);
    onAttachToContext(context);
  }

  //API 23(Marshmallow)未満においてはこっちのonAttachが呼ばれる
  @SuppressWarnings("deprecation")
  @Override
  public void onAttach(Activity activity) {

    super.onAttach(activity);
    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      onAttachToContext(activity);
    }
  }

  //2つのonAttachの共通処理部分
  protected void onAttachToContext(Context context) {

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
  public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {

    if(activity.generalSettings != null) {
      is_destroyed = false;
      addPreferencesFromResource(R.xml.main_edit);
      setHasOptionsMenu(true);

      Bundle args = getArguments();
      checkNotNull(args);
      if(order == 3) {
        list = (NonScheduledList)args.getSerializable(LIST);
        checkNotNull(list);
      }
      else {
        item = (Item)args.getSerializable(ITEM);
        checkNotNull(item);
      }

      //GeneralSettingsに影響を受ける部分の設定の初期化
      if(!is_edit && order == 0) {
        Item defaultItem = activity.generalSettings.getItem();
        item.setWhich_tag_belongs(defaultItem.getWhich_tag_belongs());
        notifyInterval = defaultItem.getNotify_interval().clone();
        dayRepeat = defaultItem.getDayRepeat().clone();
        minuteRepeat = defaultItem.getMinuteRepeat().clone();
        item.setSoundUri(defaultItem.getSoundUri());
      }
      else if(!is_edit && order == 1) {
        Item defaultItem = activity.generalSettings.getItem();
        item.setWhich_tag_belongs(defaultItem.getWhich_tag_belongs());
      }

      //各プリファレンスの初期化
      PreferenceScreen rootPreferenceScreen = getPreferenceScreen();

      PreferenceCategory title = (PreferenceCategory)findPreference("title");
      detail = (EditTextPreference)findPreference("detail");
      detail.setText(detail_str);
      detail.setTitle(detail_str);
      detail.setOnPreferenceChangeListener(this);

      PreferenceCategory schedule = (PreferenceCategory)findPreference("schedule");
      datePicker = (PreferenceScreen)findPreference("date_picker");
      timePicker = (PreferenceScreen)findPreference("time_picker");
      datePicker.setOnPreferenceClickListener(this);
      timePicker.setOnPreferenceClickListener(this);

      PreferenceCategory colorCategory = (PreferenceCategory)findPreference("color");
      PreferenceScreen primaryColor = (PreferenceScreen)findPreference("primary_color");
      primaryColor.setOnPreferenceClickListener(this);
      PreferenceScreen secondaryColor = (PreferenceScreen)findPreference("secondary_color");
      secondaryColor.setOnPreferenceClickListener(this);

      PreferenceCategory tagCategory = (PreferenceCategory)findPreference("tag_category");
      tag = (PreferenceScreen)findPreference("tag");
      tag.setOnPreferenceClickListener(this);

      intervalItem = (PreferenceScreen)findPreference("interval");
      intervalItem.setOnPreferenceClickListener(this);

      dayRepeatItem = (PreferenceScreen)findPreference("repeat_day_unit");
      dayRepeatItem.setOnPreferenceClickListener(this);
      minuteRepeatItem = (PreferenceScreen)findPreference("repeat_minute_unit");
      minuteRepeatItem.setOnPreferenceClickListener(this);

      pickAlarm = findPreference("pick_alarm");
      pickAlarm.setOnPreferenceClickListener(this);
      if(!activity.is_premium) {
        pickAlarm.setTitle(pickAlarm.getTitle() + " (" + getString(R.string.premium_account_promotion) + ")");
      }

      PreferenceCategory notes_category = (PreferenceCategory)findPreference("notes_category");
      notes = (PreferenceScreen)findPreference("notes");
      notes.setOnPreferenceClickListener(this);

      if(order == 0 || is_moving_task) {
        rootPreferenceScreen.removePreference(colorCategory);
      }
      else if(order == 1) {
        rootPreferenceScreen.removeAll();
        rootPreferenceScreen.addPreference(title);
        rootPreferenceScreen.addPreference(tagCategory);
        rootPreferenceScreen.addPreference(notes_category);
      }
      else if(order == 3) {
        rootPreferenceScreen.removeAll();
        rootPreferenceScreen.addPreference(title);
        rootPreferenceScreen.addPreference(tagCategory);
        rootPreferenceScreen.addPreference(colorCategory);
      }
      else if(order == 4) {
        rootPreferenceScreen.removePreference(title);
        rootPreferenceScreen.removePreference(schedule);
        rootPreferenceScreen.removePreference(colorCategory);
        rootPreferenceScreen.removePreference(notes_category);
      }
    }
    else {
      is_destroyed = true;
      FragmentManager manager = getFragmentManager();
      checkNotNull(manager);
      manager
          .beginTransaction()
          .remove(this)
          .commit();
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    if(!is_destroyed) {
      if(MainEditFragmentCompat.is_main_popping) {
        FragmentManager manager = getFragmentManager();
        checkNotNull(manager);
        manager.popBackStack();
      }
      View view = super.onCreateView(inflater, container, savedInstanceState);
      checkNotNull(view);

      view.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));
      view.setFocusableInTouchMode(true);
      view.requestFocus();
      view.setOnKeyListener(new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

          if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {

            next_edit_exists = false;
            if((is_moving_task || is_cloning_task) && checked_item_num > 0) {
              activity.showMainEditFragment(nextItem.clone());
              next_edit_exists = true;
            }
            else if(is_cloning_task) {
              is_cloning_task = false;
            }

            if(!next_edit_exists) {
              is_main_popping = true;
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

      //タイトルが未入力の場合、ヒントを表示
      if(detail_str == null || detail_str.equals("")) {
        detail.setSummary(R.string.detail_hint);
      }

      //設定項目間の区切り線の非表示
//      ListView listView = view.findViewById(android.R.id.list);
//      listView.setDivider(null);

      //タスクの期限のラベルの初期化
      if(order == 0) {
        if(LOCALE.equals(Locale.JAPAN)) {
          datePicker.setTitle(DateFormat.format("yyyy年M月d日(E)", final_cal));
        }
        else {
          datePicker.setTitle(DateFormat.format("yyyy/M/d (E)", final_cal));
        }
        timePicker.setTitle(DateFormat.format("kk:mm", final_cal));
      }

      //メモのラベルの初期化
      if(order == 0 || order == 1 || is_moving_task) {
        if(item.getNotesList().size() == 0) {
          notes.setSummary(R.string.none);
        }
        else notes.setSummary(item.getNotesString());
      }

      //Tagのラベルの初期化
      if(order == 0 || order == 1 || order == 4 || is_moving_task) {
        if(item.getWhich_tag_belongs() == 0) {
          tag.setSummary(getString(R.string.none));
        }
        else {
          tag.setSummary(activity.generalSettings.getTagById(item.getWhich_tag_belongs()).getName());
        }
      }
      else if(order == 3) {
        if(list.getWhich_tag_belongs() == 0) {
          tag.setSummary(getString(R.string.none));
        }
        else {
          tag.setSummary(activity.generalSettings.getTagById(list.getWhich_tag_belongs()).getName());
        }
      }

      //Repeat、NotifyInterval、AlarmSoundのラベルの初期化
      if(order == 0 || order == 4 || is_moving_task) {
        if(notifyInterval.getLabel() == null) intervalItem.setSummary(R.string.none);
        else intervalItem.setSummary(notifyInterval.getLabel());

        if(dayRepeat.getLabel() == null) dayRepeatItem.setSummary(R.string.none);
        else dayRepeatItem.setSummary(dayRepeat.getLabel());

        if(minuteRepeat.getLabel() == null) minuteRepeatItem.setSummary(R.string.none);
        else minuteRepeatItem.setSummary(minuteRepeat.getLabel());

        String uriString = item.getSoundUri();
        if(uriString == null) pickAlarm.setSummary(R.string.none);
        else {
          Ringtone ringtone = RingtoneManager.getRingtone(activity, Uri.parse(uriString));
          pickAlarm.setSummary(ringtone.getTitle(activity));
        }
      }

      return view;
    }
    else return null;
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
    if(is_edit && !is_moving_task && !is_cloning_task && order != 4) {
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
            && MainEditFragmentCompat.item.getDate().getTimeInMillis() != final_cal.getTimeInMillis()
            && (MainEditFragmentCompat.dayRepeat.getSetted() != 0 || MainEditFragmentCompat.minuteRepeat.getWhich_setted() != 0)) {

          new AlertDialog.Builder(activity)
              .setMessage(R.string.repeat_conflict_dialog_message)
              .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                  if(MainEditFragmentCompat.item.getTime_altered() == 0) {
                    MainEditFragmentCompat.item.setOrg_date((Calendar)MainEditFragmentCompat.item.getDate().clone());
                  }
                  long altered_time = final_cal.getTimeInMillis() - MainEditFragmentCompat.item.getDate().getTimeInMillis();
                  MainEditFragmentCompat.item.addTime_altered(altered_time);

                  registerItem();
                }
              })
              .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                  MainEditFragmentCompat.item.setOrg_date((Calendar)final_cal.clone());
                  MainEditFragmentCompat.item.setTime_altered(0);

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

                  activity.deleteDB(MainEditFragmentCompat.item, MyDatabaseHelper.TODO_TABLE);
                  MyExpandableListAdapter.children = activity.getChildren(MyDatabaseHelper.TODO_TABLE);
                  activity.deleteAlarm(MainEditFragmentCompat.item);
                  activity.expandableListAdapter.notifyDataSetChanged();
                }
                else if(order == 1) {
                  activity.deleteDB(MainEditFragmentCompat.item, MyDatabaseHelper.TODO_TABLE);
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

                FragmentManager manager = getFragmentManager();
                checkNotNull(manager);
                manager.popBackStack();
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

        next_edit_exists = false;
        if((is_moving_task || is_cloning_task) && checked_item_num > 0) {
          activity.showMainEditFragment(nextItem.clone());
          next_edit_exists = true;
        }
        else if(is_cloning_task) {
          is_cloning_task = false;
        }

        if(!next_edit_exists) {
          is_main_popping = true;
          FragmentManager manager = getFragmentManager();
          checkNotNull(manager);
          manager.popBackStack();
        }

        return true;
      }
      default: {
        return super.onOptionsItemSelected(item);
      }
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {

    if(requestCode == REQUEST_CODE_RINGTONE_PICKER) {
      if(resultCode == RESULT_OK) {

        Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
        if(uri == null) {
          item.setSoundUri(null);
          pickAlarm.setSummary(getString(R.string.none));
        }
        else {
          item.setSoundUri(uri.toString());
          Ringtone ringtone = RingtoneManager.getRingtone(activity, uri);
          pickAlarm.setSummary(ringtone.getTitle(activity));
        }
      }
    }
  }

  @Override
  public boolean onPreferenceClick(Preference preference) {

    switch(preference.getKey()) {

      case "date_picker": {

        DatePickerDialogFragment dialog = new DatePickerDialogFragment();
        dialog.show(activity.getSupportFragmentManager(), "date_picker");
        return true;
      }
      case "time_picker": {

        TimePickerDialogFragment dialog = new TimePickerDialogFragment();
        dialog.show(activity.getSupportFragmentManager(), "time_picker");
        return true;
      }
      case "primary_color": {

        activity.showColorPickerListViewFragment();
        return true;
      }
      case "secondary_color": {

        list.setColor_primary(false);
        activity.showColorPickerListViewFragment();
        return true;
      }
      case "tag": {

        if(order == 3) ColorPickerListAdapter.from_list_tag_edit = true;
        activity.showTagEditListViewFragment();
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
      case "pick_alarm": {

        if(activity.is_premium) {
          Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
          intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.pick_alarm));
          intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
          intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
          intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
          intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Uri.parse(activity.generalSettings.getItem().getSoundUri()));
          String uriString = item.getSoundUri();
          if(uriString != null) {
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(uriString));
          }
          startActivityForResult(intent, REQUEST_CODE_RINGTONE_PICKER);
        }
        else activity.promotionDialog.show();
        return true;
      }
      case "notes": {

        activity.showNotesFragment(item);
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

        //タイトルが未入力の場合、ヒントを表示
        if(detail_str == null || detail_str.equals("")) {
          detail.setSummary(R.string.detail_hint);
        }
        else detail.setSummary(null);
        detail.setTitle(detail_str);

        return true;
      }
    }

    return false;
  }

  private void transitionFragment(PreferenceFragmentCompat next) {

    FragmentManager manager = getFragmentManager();
    checkNotNull(manager);
    manager
        .beginTransaction()
        .remove(this)
        .add(R.id.content, next)
        .addToBackStack(null)
        .commit();
  }

  private void registerItem() {

    if(order == 0 || order == 1 || order == 4 || is_moving_task) {

      if(detail_str == null || detail_str.equals("")) {
        detail_str = getString(R.string.default_detail);
      }
      item.setDetail(detail_str);

      if(order == 0 || order == 4 || is_moving_task) {

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
        else dayRepeat.clear();
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
        else minuteRepeat.clear();
        item.setMinuteRepeat(minuteRepeat.clone());

        item.setAlarm_stopped(false);

        if(is_moving_task) item.setWhich_list_belongs(0);

        if(order != 4) {
          if(is_edit && !is_cloning_task) {

            activity.updateDB(item, MyDatabaseHelper.TODO_TABLE);
            if(is_moving_task) {
              MyExpandableListAdapter.children = activity.getChildren(MyDatabaseHelper.TODO_TABLE);
              MyListAdapter.itemList = activity.getNonScheduledItem(MyDatabaseHelper.TODO_TABLE);
              activity.listAdapter.notifyDataSetChanged();
            }
            activity.expandableListAdapter.notifyDataSetChanged();
          }
          else activity.addChildren(item, MyDatabaseHelper.TODO_TABLE);

          activity.setUpdatedItemPosition(item.getId());

          activity.deleteAlarm(item);
          activity.setAlarm(item);
        }
        else {
          activity.generalSettings.setItem(item.copy());
          activity.updateSettingsDB();
        }
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

      if(detail_str == null || detail_str.equals("")) {
        detail_str = getString(R.string.default_list);
      }
      list.setTitle(detail_str);

      if(!is_edit) {
        //GeneralSettingsとManageListAdapterへの反映
        activity.generalSettings.getNonScheduledLists().add(0, list);
        int size = activity.generalSettings.getNonScheduledLists().size();
        for(int i = 0; i < size; i++) {
          activity.generalSettings.getNonScheduledLists().get(i).setOrder(i);
        }
      }
      else {
        activity.generalSettings.setNonScheduledList(list);
      }
      ManageListAdapter.nonScheduledLists = new ArrayList<>(activity.generalSettings.getNonScheduledLists());
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

    next_edit_exists = false;
    if((is_moving_task || is_cloning_task) && checked_item_num > 0) {
      activity.showMainEditFragment(nextItem.clone());
      next_edit_exists = true;
    }
    else if(is_cloning_task) {
      is_cloning_task = false;
    }

    if(!next_edit_exists) {
      is_main_popping = true;
      FragmentManager manager = getFragmentManager();
      checkNotNull(manager);
      manager.popBackStack();
    }
  }
}