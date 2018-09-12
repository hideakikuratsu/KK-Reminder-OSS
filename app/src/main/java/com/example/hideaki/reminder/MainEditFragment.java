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
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

import static com.google.common.base.Preconditions.checkNotNull;

public class MainEditFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener,
    Preference.OnPreferenceChangeListener, DialogInterface.OnClickListener {

  public static final String ITEM = "ITEM";
  public static final String LIST = "LIST";

  private EditTextPreference detail;
  private EditTextPreference notes;
  private PreferenceScreen day_repeat_item;
  private PreferenceScreen minute_repeat_item;
  static Item item;
  static String detail_str;
  static String notes_str;
  static Calendar final_cal;
  static DayRepeat dayRepeat;
  static MinuteRepeat minuteRepeat;
  static boolean is_edit;
  static int color;
  static int order;
  static NonScheduledList list;
  private MainActivity activity;

  public static MainEditFragment newInstance() {

    MainEditFragment fragment = new MainEditFragment();

    Item item = new Item();
    is_edit = false;
    detail_str = "";
    notes_str = "";
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
    order = activity.menuItem.getOrder();
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

    PreferenceScreen rootPreferenceScreen = getPreferenceScreen();
    detail = (EditTextPreference)findPreference("detail");
    detail.setTitle(detail_str);
    detail.setOnPreferenceChangeListener(this);
    PreferenceScreen color = (PreferenceScreen)findPreference("color");
    color.setOnPreferenceClickListener(this);
    PreferenceScreen tag = (PreferenceScreen)findPreference("tag");
    tag.setOnPreferenceClickListener(this);
    PreferenceScreen interval = (PreferenceScreen)findPreference("interval");
    interval.setOnPreferenceClickListener(this);
    day_repeat_item = (PreferenceScreen)findPreference("repeat_day_unit");
    day_repeat_item.setOnPreferenceClickListener(this);
    minute_repeat_item = (PreferenceScreen)findPreference("repeat_minute_unit");
    minute_repeat_item.setOnPreferenceClickListener(this);
    PreferenceCategory notes_category = (PreferenceCategory)findPreference("notes_category");
    notes = (EditTextPreference)findPreference("notes");
    notes.setTitle(notes_str);
    notes.setOnPreferenceChangeListener(this);

    if(order == 0) {
      rootPreferenceScreen.removePreference(color);
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
      rootPreferenceScreen.addPreference(color);
      rootPreferenceScreen.addPreference(notes_category);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

    View view = super.onCreateView(inflater, container, savedInstanceState);
    checkNotNull(view);

    view.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));

    Toolbar toolbar = activity.findViewById(R.id.toolbar_layout);
    activity.setSupportActionBar(toolbar);
    ActionBar actionBar = activity.getSupportActionBar();
    checkNotNull(actionBar);

    activity.drawerToggle.setDrawerIndicatorEnabled(false);
    actionBar.setHomeAsUpIndicator(activity.upArrow);
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setTitle(R.string.edit);

    if(order == 0) {
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
    if(is_edit) {
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
      case R.id.done:
        if(order == 0 && is_edit && MainEditFragment.item.getDate().getTimeInMillis() != final_cal.getTimeInMillis()
            && (MainEditFragment.dayRepeat.getSetted() != 0 || MainEditFragment.minuteRepeat.getWhich_setted() != 0)) {
          new AlertDialog.Builder(getActivity())
              .setMessage(R.string.repeat_conflict_dialog_message)
              .setPositiveButton(R.string.yes, this)
              .setNegativeButton(R.string.no, this)
              .setNeutralButton(R.string.cancel, this)
              .show();
        }
        else {
          registerItem();
        }
        return true;
      case R.id.delete:
        new AlertDialog.Builder(getActivity())
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
                  activity.generalSettings.getOrgNonScheduledLists().remove(list.getOrder());
                  int size = activity.generalSettings.getOrgNonScheduledLists().size();
                  for(int i = 0; i < size; i++) {
                    activity.generalSettings.getOrgNonScheduledLists().get(i).setOrder(i);
                  }
                  ManageListAdapter.nonScheduledLists = activity.generalSettings.getNonScheduledLists();
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

                  //データベースへの反映
                  activity.updateSettingsDB();
                }

                getFragmentManager().popBackStack();
              }
            })
            .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {}
            })
            .show();
        return true;
      case android.R.id.home:
        getFragmentManager().popBackStack();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public boolean onPreferenceClick(Preference preference) {

    switch(preference.getKey()) {
      case "color":
        activity.showColorPickerListViewFragment();
        return true;
      case "tag":
        transitionFragment(TagEditFragment.newInstance());
        return true;
      case "interval":
        transitionFragment(IntervalEditFragment.newInstance());
        return true;
      case "repeat_day_unit":
        transitionFragment(DayRepeatEditFragment.newInstance());
        return true;
      case "repeat_minute_unit":
        transitionFragment(MinuteRepeatEditFragment.newInstance());
        return true;
    }
    return false;
  }

  @Override
  public boolean onPreferenceChange(Preference preference, Object newValue) {

    switch(preference.getKey()) {
      case "detail":
        detail_str = (String)newValue;
        detail.setTitle((String)newValue);
        return true;
      case "notes":
        notes_str = (String)newValue;
        notes.setTitle((String)newValue);
        return true;
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

  @Override
  public void onClick(DialogInterface dialog, int which) {

    switch(which) {
      case DialogInterface.BUTTON_POSITIVE:
        if(item.getTime_altered() == 0) {
          item.setOrg_date((Calendar)item.getDate().clone());
        }
        long altered_time = (final_cal.getTimeInMillis()
            - item.getDate().getTimeInMillis()) / (1000 * 60);
        item.addTime_altered(altered_time * 60 * 1000);

        registerItem();
        break;
      case DialogInterface.BUTTON_NEGATIVE:
        item.setOrg_date((Calendar)final_cal.clone());
        item.setTime_altered(0);

        registerItem();
        break;
      case DialogInterface.BUTTON_NEUTRAL:
        break;
    }
  }
  
  private void registerItem() {

    if(order == 0 || order == 1) {

      item.setDetail(detail_str);
      item.setNotes(notes_str);

      if(order == 0) {

        item.setDate((Calendar)final_cal.clone());

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

        if(is_edit) {

          activity.expandableListAdapter.notifyDataSetChanged();
          activity.updateDB(item, MyDatabaseHelper.TODO_TABLE);
        }
        else {
          activity.addChildren(item, MyDatabaseHelper.TODO_TABLE);
        }

        activity.deleteAlarm(item);
        activity.setAlarm(item);
      }
      else {

        //リストのIDをitemに登録する
        item.setWhich_list_belongs(
            activity.generalSettings.getNonScheduledList(activity.which_menu_open - 1).getId()
        );

        if(activity.isItemExists(item, MyDatabaseHelper.TODO_TABLE)) {

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
        activity.generalSettings.addNonScheduledList(list);
        int size = activity.generalSettings.getOrgNonScheduledLists().size();
        for(int i = 0; i < size; i++) {
          activity.generalSettings.getOrgNonScheduledLists().get(i).setOrder(i);
        }
        ManageListAdapter.nonScheduledLists = activity.generalSettings.getNonScheduledLists();
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

      //データベースへの反映
      if(activity.querySettingsDB() == null) {
        activity.insertSettingsDB();
      }
      else activity.updateSettingsDB();
    }

    //データベースを更新したら、そのデータベースを端末暗号化ストレージへコピーする
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      Context direct_boot_context = getActivity().createDeviceProtectedStorageContext();
      direct_boot_context.moveDatabaseFrom(getActivity(), MyDatabaseHelper.TODO_TABLE);
    }

    getFragmentManager().popBackStack();
  }
}
