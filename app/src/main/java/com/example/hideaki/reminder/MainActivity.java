package com.example.hideaki.reminder;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.hideaki.reminder.UtilClass.*;
import static com.google.common.base.Preconditions.checkNotNull;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

  Timer timer;
  TimerTask timerTask;
  private Handler handler = new Handler();
  private DBAccessor accessor = null;
  private Intent intent;
  private PendingIntent sender;
  private AlarmManager alarmManager;
  private int group_changed; //groupの変化があったかどうかのフラグをビットで表す
  private int group_changed_num; //groupの変化があったchildの個数を保持する
  public int which_menu_open;
  private int which_submenu_open;
  ExpandableListView expandableListView;
  MyExpandableListAdapter expandableListAdapter;
  SortableListView listView;
  MyListAdapter listAdapter;
  ManageListAdapter manageListAdapter;
  ColorPickerListAdapter colorPickerListAdapter;
  TagEditListAdapter tagEditListAdapter;
  DoneListAdapter doneListAdapter;
  DrawerLayout drawerLayout;
  NavigationView navigationView;
  ActionBarDrawerToggle drawerToggle;
  Drawable upArrow;
  Menu menu;
  MenuItem menuItem;
  MenuItem oldMenuItem;
  GeneralSettings generalSettings;
  ActionBarFragment actionBarFragment;
  Toolbar toolbar;
  int menu_item_color;
  int menu_background_color;
  int status_bar_color;
  int accent_color;
  int order;
  String detail;
  private int which_list;
  private boolean is_in_on_create;
  private static String BASE_FRAGMENT_TAG;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    accessor = new DBAccessor(this);

    //ToolbarをActionBarに互換を持たせて設定
    toolbar = findViewById(R.id.toolbar_layout);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    checkNotNull(actionBar);

    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowHomeEnabled(true);

    //NavigationDrawerの設定
    drawerLayout = findViewById(R.id.drawer_layout);
    drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
        R.string.drawer_open, R.string.drawer_close
    );
    drawerToggle.setDrawerIndicatorEnabled(true);
    drawerLayout.addDrawerListener(drawerToggle);

    navigationView = findViewById(R.id.nav_view);
    menu = navigationView.getMenu();
    navigationView.setItemIconTintList(null);
    navigationView.setNavigationItemSelectedListener(this);

    //前回開いていたFragmentのインデックスを取得する
    SharedPreferences preferences = getSharedPreferences(SAVED_DATA, MODE_PRIVATE);
    which_menu_open = preferences.getInt(MENU_POSITION, 0);
    which_submenu_open = preferences.getInt(SUBMENU_POSITION, 0);

    //共通設定と新しく追加したリストのリストア
    generalSettings = querySettingsDB();
    if(generalSettings == null) {
      generalSettings = new GeneralSettings();

      //データベースを新たに作成する場合、基本的な一般設定を追加しておく

      //タグのデフォルト設定
      //タグなし
      Tag tag = new Tag(0);
      tag.setName(getString(R.string.none));
      tag.setOrder(0);
      generalSettings.getTagList().add(tag);

      //家事タグ
      tag = new Tag(1);
      tag.setName(getString(R.string.home));
      tag.setPrimary_color(Color.parseColor("#4caf50"));
      tag.setPrimary_light_color(Color.parseColor("#80e27e"));
      tag.setPrimary_dark_color(Color.parseColor("#087f23"));
      tag.setPrimary_text_color(Color.parseColor("#000000"));
      tag.setColor_order_group(9);
      tag.setColor_order_child(5);
      tag.setOrder(1);
      generalSettings.getTagList().add(tag);

      //仕事タグ
      tag = new Tag(2);
      tag.setName(getString(R.string.work));
      tag.setPrimary_color(Color.parseColor("#2196f3"));
      tag.setPrimary_light_color(Color.parseColor("#6ec6ff"));
      tag.setPrimary_dark_color(Color.parseColor("#0069c0"));
      tag.setPrimary_text_color(Color.parseColor("#000000"));
      tag.setColor_order_group(5);
      tag.setColor_order_child(5);
      tag.setOrder(2);
      generalSettings.getTagList().add(tag);

      //ショッピングタグ
      tag = new Tag(3);
      tag.setName(getString(R.string.shopping));
      tag.setPrimary_color(Color.parseColor("#f44336"));
      tag.setPrimary_light_color(Color.parseColor("#ff7961"));
      tag.setPrimary_dark_color(Color.parseColor("#ba000d"));
      tag.setPrimary_text_color(Color.parseColor("#000000"));
      tag.setColor_order_group(0);
      tag.setColor_order_child(5);
      tag.setOrder(3);
      generalSettings.getTagList().add(tag);

      //NotifyIntervalのデフォルト設定
      NotifyInterval notifyInterval = new NotifyInterval();
      notifyInterval.setHour(0);
      notifyInterval.setMinute(5);
      notifyInterval.setOrg_time(6);
      notifyInterval.setWhich_setted(1);

      if(notifyInterval.getOrg_time() != 0) {
        String summary = getString(R.string.unless_complete_task);
        if(notifyInterval.getHour() != 0) {
          summary += notifyInterval.getHour() + getString(R.string.hour);
        }
        if(notifyInterval.getMinute() != 0) {
          summary += notifyInterval.getMinute() + getString(R.string.minute);
        }
        summary += getString(R.string.per);
        if(notifyInterval.getOrg_time() == -1) {
          summary += getString(R.string.infinite_times_notify);
        }
        else {
          summary += getString(R.string.max) + notifyInterval.getOrg_time()
              + getString(R.string.times_notify);
        }

        notifyInterval.setLabel(summary);
      }
      else {
        notifyInterval.setLabel(getString(R.string.none));
      }
      generalSettings.setNotifyInterval(notifyInterval);

      //AlarmSoundのデフォルト設定
      generalSettings.setSoundUri(DEFAULT_URI_SOUND.toString());

      //手動スヌーズ時間のデフォルト設定
      generalSettings.setSnooze_default_hour(0);
      generalSettings.setSnooze_default_minute(15);

      insertSettingsDB();
    }

    for(NonScheduledList list : generalSettings.getNonScheduledLists()) {
      Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_my_list_24dp);
      checkNotNull(drawable);
      drawable = drawable.mutate();
      if(list.getColor() != 0) {
        drawable.setColorFilter(list.getColor(), PorterDuff.Mode.SRC_IN);
      }
      else {
        drawable.setColorFilter(ContextCompat.getColor(this, R.color.icon_gray), PorterDuff.Mode.SRC_IN);
      }
      menu.add(R.id.reminder_list, Menu.NONE, 1, list.getTitle())
          .setIcon(drawable)
          .setCheckable(true);
    }

    //Adapterの初期化
    expandableListAdapter = new MyExpandableListAdapter(this);
    listAdapter = new MyListAdapter(this);
    manageListAdapter = new ManageListAdapter(new ArrayList<>(generalSettings.getNonScheduledLists()), this);
    colorPickerListAdapter = new ColorPickerListAdapter(this);
    tagEditListAdapter = new TagEditListAdapter(new ArrayList<>(generalSettings.getTagList()), this);
    doneListAdapter = new DoneListAdapter(this);

    //Intentが送られている場合はonNewIntent()に渡す(送られていない場合は通常の初期化処理を行う)
    is_in_on_create = true;
    onNewIntent(getIntent());

    //Notificationチャネルの作成
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
      checkNotNull(notificationManager);
      NotificationChannel notificationChannel = new NotificationChannel("reminder_01",
          getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);

      notificationManager.createNotificationChannel(notificationChannel);
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {

    super.onNewIntent(intent);

    //テキストがACTION_SENDで送られてきたときに、詳細の項目にそのテキストをセットした状態で編集画面を表示する
    detail = null;
    if(Intent.ACTION_SEND.equals(intent.getAction())) {
      Bundle extras = intent.getExtras();
      if(extras != null) {
        detail = extras.getString(Intent.EXTRA_TEXT);
      }
    }

    if(detail == null) {
      if(BOOT_FROM_NOTIFICATION.equals(intent.getAction())) {
        which_menu_open = 0;
        which_submenu_open = 0;
      }

      showList();
      is_in_on_create = false;
    }
    else {

      int size = generalSettings.getNonScheduledLists().size();
      String[] items = new String[size + 1];
      items[0] = menu.findItem(R.id.scheduled_list).getTitle().toString();
      for(int i = 0; i < size; i++) {
        items[i + 1] = generalSettings.getNonScheduledLists().get(i).getTitle();
      }

      new AlertDialog.Builder(this)
          .setTitle(R.string.action_send_booted_dialog_title)
          .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              which_list = which;
            }
          })
          .setPositiveButton(R.string.determine, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

              which_menu_open = which_list;
              which_submenu_open = 0;

              showList();
              is_in_on_create = false;
            }
          })
          .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

              detail = null;
              if(is_in_on_create) showList();
              is_in_on_create = false;
            }
          })
          .setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

              detail = null;
              if(is_in_on_create) showList();
              is_in_on_create = false;
            }
          })
          .show();
    }
  }

  private void showList() {

    //前回開いていたNavigationDrawer上のメニューをリストアする
    if(menuItem == null) {
      menuItem = menu.getItem(which_menu_open);
      if(menuItem.hasSubMenu()) {
        menuItem = menuItem.getSubMenu().getItem(which_submenu_open);
      }
      oldMenuItem = menuItem;
    }
    else {
      oldMenuItem = menuItem;
      menuItem = menu.getItem(which_menu_open);
      if(menuItem.hasSubMenu()) {
        menuItem = menuItem.getSubMenu().getItem(which_submenu_open);
      }
    }

    //選択状態のリストア
    menuItem.setChecked(true);

    createAndSetFragmentColor();

    if(order == 0) {
      if(generalSettings.isExpandable_todo()) {
        showExpandableListViewFragment(BASE_FRAGMENT_TAG);
      }
      else showDoneListViewFragment(BASE_FRAGMENT_TAG);
    }
    else if(order == 1) {
      if(generalSettings.getNonScheduledLists().get(which_menu_open - 1).isTodo()) {
        showListViewFragment(BASE_FRAGMENT_TAG);
      }
      else showDoneListViewFragment(BASE_FRAGMENT_TAG);
    }
    else if(order == 3) showManageListViewFragment(BASE_FRAGMENT_TAG);
  }

  @Override
  protected void onResume() {

    super.onResume();

    if(timer != null) {
      timer.cancel();
      timer = null;
    }
    timer = new Timer();
    timerTask = new UpdateListTimerTask();
    timer.schedule(timerTask, 0, 1000);

    //すべての通知を既読する
    NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    checkNotNull(manager);
    manager.cancelAll();
  }

  @Override
  protected void onPause() {

    super.onPause();

    if(timer != null) {
      timer.cancel();
      timer = null;
    }

    SharedPreferences preferences = getSharedPreferences(SAVED_DATA, MODE_PRIVATE);
    SharedPreferences.Editor editor = preferences.edit();
    editor.putInt(MENU_POSITION, which_menu_open);
    editor.putInt(SUBMENU_POSITION, which_submenu_open);
    editor.apply();

    //すべての通知を既読する
    NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    checkNotNull(manager);
    manager.cancelAll();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {

    if(drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START);
    }
    else super.onBackPressed();
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {

    super.onPostCreate(savedInstanceState);
    drawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {

    super.onConfigurationChanged(newConfig);
    drawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

    if(menuItem.getItemId() != R.id.add_list) {

      //選択されたメニューアイテム以外のチェックを外す
      if(!menuItem.isChecked()) {

        for(int i = 0; i < menu.size(); i++) {

          if(menu.getItem(i) == menuItem) {
            which_menu_open = i;
          }

          if(menu.getItem(i).hasSubMenu()) {
            SubMenu subMenu = menu.getItem(i).getSubMenu();
            for(int j = 0; j < subMenu.size(); j++) {
              if(subMenu.getItem(j) == menuItem) {
                which_menu_open = i;
                which_submenu_open = j;
              }
              subMenu.getItem(j).setChecked(false);
            }
          }
          else {
            menu.getItem(i).setChecked(false);
          }
        }
        menuItem.setChecked(true);

        //選択されたmenuItemに対応するフラグメントを表示
        oldMenuItem = this.menuItem;
        this.menuItem = menuItem;
        createAndSetFragmentColor();
        switch(order) {
          case 0: {
            if(generalSettings.isExpandable_todo()) {
              showExpandableListViewFragment(BASE_FRAGMENT_TAG);
            }
            else showDoneListViewFragment(BASE_FRAGMENT_TAG);
            break;
          }
          case 1: {
            if(generalSettings.getNonScheduledLists().get(which_menu_open - 1).isTodo()) {
              showListViewFragment(BASE_FRAGMENT_TAG);
            }
            else showDoneListViewFragment(BASE_FRAGMENT_TAG);
            break;
          }
          case 3: {
            showManageListViewFragment(BASE_FRAGMENT_TAG);
            break;
          }
          case 4: {
            break;
          }
          case 5: {
            break;
          }
          case 6: {
            break;
          }
        }
      }
    }
    else {
      //ダイアログに表示するEditTextの設定
      LinearLayout linearLayout = new LinearLayout(MainActivity.this);
      linearLayout.setOrientation(LinearLayout.VERTICAL);
      final EditText editText = new EditText(MainActivity.this);
      editText.setLayoutParams(new LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.MATCH_PARENT,
          LinearLayout.LayoutParams.WRAP_CONTENT
      ));
      linearLayout.addView(editText);
      int paddingDp = 20; //dpを指定
      float scale = getResources().getDisplayMetrics().density; //画面のdensityを指定
      int paddingPx = (int) (paddingDp * scale + 0.5f); //dpをpxに変換
      linearLayout.setPadding(paddingPx, 0, paddingPx, 0);

      //新しいリストの名前を設定するダイアログを表示
      final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
          .setTitle(R.string.add_list)
          .setView(linearLayout)
          .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

              //GeneralSettingsとManageListAdapterへの反映
              String name = editText.getText().toString();
              if(name.equals("")) {
                name = getString(R.string.default_list);
              }
              generalSettings.getNonScheduledLists().add(0, new NonScheduledList(name));
              int size = generalSettings.getNonScheduledLists().size();
              for(int i = 0; i < size; i++) {
                generalSettings.getNonScheduledLists().get(i).setOrder(i);
              }
              ManageListAdapter.nonScheduledLists = new ArrayList<>(generalSettings.getNonScheduledLists());
              manageListAdapter.notifyDataSetChanged();

              //一旦reminder_listグループ内のアイテムをすべて消してから元に戻すことで新しく追加したリストの順番を追加した順に並び替える

              //デフォルトアイテムのリストア
              menu.removeGroup(R.id.reminder_list);
              menu.add(R.id.reminder_list, R.id.scheduled_list, 0, R.string.nav_scheduled_item)
                  .setIcon(R.drawable.ic_time)
                  .setCheckable(true);
              menu.add(R.id.reminder_list, R.id.add_list, 2, R.string.add_list)
                  .setIcon(R.drawable.ic_add_24dp)
                  .setCheckable(false);

              //新しく追加したリストのリストア
              for(NonScheduledList list : generalSettings.getNonScheduledLists()) {
                Drawable drawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_my_list_24dp);
                checkNotNull(drawable);
                drawable = drawable.mutate();
                if(list.getColor() != 0) {
                  drawable.setColorFilter(list.getColor(), PorterDuff.Mode.SRC_IN);
                }
                else {
                  drawable.setColorFilter(
                      ContextCompat.getColor(MainActivity.this, R.color.icon_gray), PorterDuff.Mode.SRC_IN
                  );
                }
                menu.add(R.id.reminder_list, Menu.NONE, 1, list.getTitle())
                    .setIcon(drawable)
                    .setCheckable(true);
              }

              //データベースへの反映
              updateSettingsDB();
            }
          })
          .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
          })
          .show();

      //ダイアログ表示時にソフトキーボードを自動で表示
      editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
          if(hasFocus) {
            Window dialogWindow = dialog.getWindow();
            checkNotNull(dialogWindow);

            dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
          }
        }
      });
    }

    return false;
  }

  public class UpdateListTimerTask extends TimerTask {

    @Override
    public void run() {
      handler.post(new Runnable() {
        @Override
        public void run() {

          group_changed_num = 0;
          for(int group_count = 0; group_count < MyExpandableListAdapter.groups.size(); group_count++) {

            group_changed = 0;
            List<Item> itemList = MyExpandableListAdapter.children.get(group_count);

            for(int child_count = 0; child_count < itemList.size() - group_changed_num; child_count++) {

              Item item = itemList.get(child_count);
              Calendar now = Calendar.getInstance();
              Calendar tomorrow = (Calendar)now.clone();
              tomorrow.add(Calendar.DAY_OF_MONTH, 1);

              int spec_day = item.getDate().get(Calendar.DAY_OF_MONTH);
              long sub_time = item.getDate().getTimeInMillis() - now.getTimeInMillis();
              long sub_day = sub_time / (1000 * 60 * 60 * 24);

              if(sub_time < 0) {
                if(group_count != 0) {
                  MyExpandableListAdapter.children.get(0).add(item);
                  group_changed |= (1 << child_count);
                }
              }
              else if(sub_day < 1 && spec_day == now.get(Calendar.DAY_OF_MONTH)) {
                if(group_count != 1) {
                  MyExpandableListAdapter.children.get(1).add(item);
                  group_changed |= (1 << child_count);
                }
              }
              else if(sub_day < 2 && spec_day == tomorrow.get(Calendar.DAY_OF_MONTH)) {
                if(group_count != 2) {
                  MyExpandableListAdapter.children.get(2).add(item);
                  group_changed |= (1 << child_count);
                }
              }
              else if(sub_day < 8) {
                if(group_count != 3) {
                  MyExpandableListAdapter.children.get(3).add(item);
                  group_changed |= (1 << child_count);
                }
              }
              else {
                if(group_count != 4) {
                  MyExpandableListAdapter.children.get(4).add(item);
                  group_changed |= (1 << child_count);
                }
              }
            }

            group_changed_num = 0;
            for(int i = 0; i <= group_changed; i++) {
              if((group_changed & (1 << i)) != 0) {
                if(MyExpandableListAdapter.children.get(group_count).size() > i) {
                  MyExpandableListAdapter.children.get(group_count).remove(i);
                }
                group_changed_num++;
              }
            }
          }

          for(List<Item> itemList : MyExpandableListAdapter.children) {
            Collections.sort(itemList, scheduledItemComparator);
          }
          expandableListAdapter.notifyDataSetChanged();
        }
      });
    }
  }

  public void setAlarm(Item item) {

    if(item.getDate().getTimeInMillis() > System.currentTimeMillis() && item.getWhich_list_belongs() == 0) {
      item.getNotify_interval().setTime(item.getNotify_interval().getOrg_time());
      intent = new Intent(this, AlarmReceiver.class);
      byte[] ob_array = serialize(item);
      intent.putExtra(ITEM, ob_array);
      sender = PendingIntent.getBroadcast(
          this, (int)item.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

      alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        alarmManager.setAlarmClock(
            new AlarmManager.AlarmClockInfo(item.getDate().getTimeInMillis(), null), sender);
      }
      else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, item.getDate().getTimeInMillis(), sender);
      }
      else {
        alarmManager.set(AlarmManager.RTC_WAKEUP, item.getDate().getTimeInMillis(), sender);
      }
    }
  }

  public void deleteAlarm(Item item) {

    if(isAlarmSetted(item)) {
      intent = new Intent(this, AlarmReceiver.class);
      sender = PendingIntent.getBroadcast(
          this, (int)item.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

      alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
      checkNotNull(alarmManager);

      alarmManager.cancel(sender);
      sender.cancel();
    }
  }

  public boolean isAlarmSetted(Item item) {

    intent = new Intent(this, AlarmReceiver.class);
    sender = PendingIntent.getBroadcast(
        this, (int)item.getId(), intent, PendingIntent.FLAG_NO_CREATE);

    return sender != null;
  }

  public List<List<Item>> getChildren(String table) {

    List<Item> past_list = new ArrayList<>();
    List<Item> today_list = new ArrayList<>();
    List<Item> tomorrow_list = new ArrayList<>();
    List<Item> week_list = new ArrayList<>();
    List<Item> future_list = new ArrayList<>();

    Calendar now = Calendar.getInstance();
    Calendar tomorrow = (Calendar)now.clone();
    tomorrow.add(Calendar.DAY_OF_MONTH, 1);

    for(Item item : queryAllDB(table)) {

      if(item.getWhich_list_belongs() == 0) {
        if(item.getNotify_interval().getTime() == item.getNotify_interval().getOrg_time()) {
          deleteAlarm(item);
        }
        if(!isAlarmSetted(item) && !item.isAlarm_stopped()) {
          setAlarm(item);
        }

        int spec_day = item.getDate().get(Calendar.DAY_OF_MONTH);
        long sub_time = item.getDate().getTimeInMillis() - now.getTimeInMillis();
        long sub_day = sub_time / (1000 * 60 * 60 * 24);

        if(sub_time < 0) {
          past_list.add(item);
        }
        else if(sub_day < 1 && spec_day == now.get(Calendar.DAY_OF_MONTH)) {
          today_list.add(item);
        }
        else if(sub_day < 2 && spec_day == tomorrow.get(Calendar.DAY_OF_MONTH)) {
          tomorrow_list.add(item);
        }
        else if(sub_day < 8) {
          week_list.add(item);
        }
        else {
          future_list.add(item);
        }
      }
    }

    List<List<Item>> children = new ArrayList<>();
    children.add(past_list);
    children.add(today_list);
    children.add(tomorrow_list);
    children.add(week_list);
    children.add(future_list);

    for(List<Item> itemList : children) {
      Collections.sort(itemList, scheduledItemComparator);
    }
    return children;
  }

  public void addChildren(Item item, String table) {

    Calendar now = Calendar.getInstance();
    Calendar tomorrow = (Calendar)now.clone();
    tomorrow.add(Calendar.DAY_OF_MONTH, 1);

    int spec_day = item.getDate().get(Calendar.DAY_OF_MONTH);
    long sub_time = item.getDate().getTimeInMillis() - now.getTimeInMillis();
    long sub_day = sub_time / (1000 * 60 * 60 * 24);

    if(sub_time < 0) {
      MyExpandableListAdapter.children.get(0).add(item);
    }
    else if(sub_day < 1 && spec_day == now.get(Calendar.DAY_OF_MONTH)) {
      MyExpandableListAdapter.children.get(1).add(item);
    }
    else if(sub_day < 2 && spec_day == tomorrow.get(Calendar.DAY_OF_MONTH)) {
      MyExpandableListAdapter.children.get(2).add(item);
    }
    else if(sub_day < 8) {
      MyExpandableListAdapter.children.get(3).add(item);
    }
    else {
      MyExpandableListAdapter.children.get(4).add(item);
    }

    for(List<Item> itemList : MyExpandableListAdapter.children) {
      Collections.sort(itemList, scheduledItemComparator);
    }
    expandableListAdapter.notifyDataSetChanged();
    insertDB(item, table);
  }

  public List<Item> getNonScheduledItem(String table) {

    long list_id = generalSettings.getNonScheduledLists().get(which_menu_open - 1).getId();
    List<Item> itemList = new ArrayList<>();
    for(Item item : queryAllDB(table)) {
      if(item.getWhich_list_belongs() == list_id) {
        itemList.add(item);
      }
    }
    Collections.sort(itemList, nonScheduledItemComparator);

    return itemList;
  }

  public List<Item> getDoneItem() {

    long list_id = which_menu_open == 0 ? 0 : generalSettings.getNonScheduledLists().get(which_menu_open - 1).getId();
    List<Item> itemList = new ArrayList<>();
    for(Item item : queryAllDB(MyDatabaseHelper.DONE_TABLE)) {
      if(item.getWhich_list_belongs() == list_id) {
        itemList.add(item);
      }
    }
    Collections.sort(itemList, doneItemComparator);

    return itemList;
  }

  //受け取ったオブジェクトをシリアライズしてデータベースへ挿入
  public void insertDB(Item item, String table) {

    accessor.executeInsert(item.getId(), serialize(item), table);
  }

  public void updateDB(Item item, String table) {

    accessor.executeUpdate(item.getId(), serialize(item), table);
  }

  public void deleteDB(Item item, String table) {

    accessor.executeDelete(item.getId(), table);
  }

  //指定されたテーブルからオブジェクトのバイト列をすべて取り出し、デシリアライズしてオブジェクトのリストで返す。
  public List<Item> queryAllDB(String table) {

    List<Item> itemList = new ArrayList<>();

    for(byte[] stream : accessor.executeQueryAll(table)) {
      itemList.add((Item)deserialize(stream));
    }

    return itemList;
  }

  public boolean isItemExists(Item item, String table) {

    return accessor.executeQueryById(item.getId(), table) != null;
  }

  public void insertSettingsDB() {

    accessor.executeInsert(1, serialize(generalSettings), MyDatabaseHelper.SETTINGS_TABLE);
  }

  public void updateSettingsDB() {

    accessor.executeUpdate(1, serialize(generalSettings), MyDatabaseHelper.SETTINGS_TABLE);
  }

  public GeneralSettings querySettingsDB() {

    return (GeneralSettings)deserialize(accessor.executeQueryById(1, MyDatabaseHelper.SETTINGS_TABLE));
  }

  private void createAndSetFragmentColor() {

    //開いているFragmentに応じた色を作成
    order = menuItem.getOrder();
    if(order == 1) {
      NonScheduledList list = generalSettings.getNonScheduledLists().get(which_menu_open - 1);
      if(list.getColor() != 0) {
        menu_item_color = list.getTextColor();
        menu_background_color = list.getColor();
        status_bar_color = list.getDarkColor();
        list.setColor_primary(false);
        accent_color = list.getColor();
        list.setColor_primary(true);
      }
      else {
        menu_item_color = Color.WHITE;
        menu_background_color = ContextCompat.getColor(this, R.color.colorPrimary);
        status_bar_color = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        accent_color = ContextCompat.getColor(this, R.color.colorAccent);
      }
    }
    else {
      menu_item_color = Color.WHITE;
      menu_background_color = ContextCompat.getColor(this, R.color.colorPrimary);
      status_bar_color = ContextCompat.getColor(this, R.color.colorPrimaryDark);
      accent_color = ContextCompat.getColor(this, R.color.colorAccent);
    }

    //ハンバーガーアイコンの色を指定
    drawerToggle.getDrawerArrowDrawable().setColor(menu_item_color);
    //DisplayHomeAsUpEnabledに指定する戻るボタンの色を指定
    upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
    checkNotNull(upArrow);
    upArrow.setColorFilter(menu_item_color, PorterDuff.Mode.SRC_IN);
    //ツールバーとステータスバーの色を指定
    toolbar.setTitleTextColor(menu_item_color);
    toolbar.setBackgroundColor(menu_background_color);
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Window window = getWindow();
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(status_bar_color);
    }
  }

  public void showDoneListViewFragment(String TAG) {

    actionBarFragment = ActionBarFragment.newInstance();
    FragmentManager manager = getFragmentManager();
    Fragment fragmentToRemove = manager.findFragmentByTag(TAG);

    if(fragmentToRemove == null) {
      manager
          .beginTransaction()
          .add(R.id.content, DoneListViewFragment.newInstance(), DoneListViewFragment.TAG)
          .add(R.id.content, actionBarFragment, ActionBarFragment.TAG)
          .commit();
    }
    else {
      manager
          .beginTransaction()
          .remove(fragmentToRemove)
          .remove(manager.findFragmentByTag(ActionBarFragment.TAG))
          .add(R.id.content, DoneListViewFragment.newInstance(), DoneListViewFragment.TAG)
          .add(R.id.content, actionBarFragment, ActionBarFragment.TAG)
          .commit();
    }

    BASE_FRAGMENT_TAG = DoneListViewFragment.TAG;
  }

  public void showTagEditListViewFragment(String TAG) {

    FragmentManager manager = getFragmentManager();
    Fragment fragmentToRemove = manager.findFragmentByTag(TAG);
    checkNotNull(fragmentToRemove);

    manager
        .beginTransaction()
        .remove(fragmentToRemove)
        .add(R.id.content, TagEditListViewFragment.newInstance(), TagEditListViewFragment.TAG)
        .addToBackStack(null)
        .commit();
  }

  public void showColorPickerListViewFragment(String TAG) {

    FragmentManager manager = getFragmentManager();
    Fragment fragmentToRemove = manager.findFragmentByTag(TAG);
    checkNotNull(fragmentToRemove);

    manager
        .beginTransaction()
        .remove(fragmentToRemove)
        .add(R.id.content, ColorPickerListViewFragment.newInstance(), ColorPickerListViewFragment.TAG)
        .addToBackStack(null)
        .commit();
  }

  public void showColorPickerListViewFragment(int tag_position, String TAG) {

    ColorPickerListViewFragment.tag_position = tag_position;
    FragmentManager manager = getFragmentManager();
    Fragment fragmentToRemove = manager.findFragmentByTag(TAG);
    checkNotNull(fragmentToRemove);

    manager
        .beginTransaction()
        .remove(fragmentToRemove)
        .add(R.id.content, ColorPickerListViewFragment.newInstance(), ColorPickerListViewFragment.TAG)
        .addToBackStack(null)
        .commit();
  }

  public void showManageListViewFragment(String TAG) {

    actionBarFragment = ActionBarFragment.newInstance();
    FragmentManager manager = getFragmentManager();
    Fragment fragmentToRemove = manager.findFragmentByTag(TAG);

    if(fragmentToRemove == null) {
      manager
          .beginTransaction()
          .add(R.id.content, ManageListViewFragment.newInstance(), ManageListViewFragment.TAG)
          .add(R.id.content, actionBarFragment, ActionBarFragment.TAG)
          .commit();
    }
    else {
      manager
          .beginTransaction()
          .remove(fragmentToRemove)
          .remove(manager.findFragmentByTag(ActionBarFragment.TAG))
          .add(R.id.content, ManageListViewFragment.newInstance(), ManageListViewFragment.TAG)
          .add(R.id.content, actionBarFragment, ActionBarFragment.TAG)
          .commit();
    }

    BASE_FRAGMENT_TAG = ManageListViewFragment.TAG;
  }

  public void showListViewFragment(String TAG) {

    actionBarFragment = ActionBarFragment.newInstance();
    FragmentManager manager = getFragmentManager();
    Fragment fragmentToRemove = manager.findFragmentByTag(TAG);

    if(fragmentToRemove == null) {
      manager
          .beginTransaction()
          .add(R.id.content, ListViewFragment.newInstance(), ListViewFragment.TAG)
          .add(R.id.content, actionBarFragment, ActionBarFragment.TAG)
          .commit();
    }
    else {
      manager
          .beginTransaction()
          .remove(fragmentToRemove)
          .remove(manager.findFragmentByTag(ActionBarFragment.TAG))
          .add(R.id.content, ListViewFragment.newInstance(), ListViewFragment.TAG)
          .add(R.id.content, actionBarFragment, ActionBarFragment.TAG)
          .commit();
    }

    BASE_FRAGMENT_TAG = ListViewFragment.TAG;
  }

  public void showExpandableListViewFragment(String TAG) {

    actionBarFragment = ActionBarFragment.newInstance();
    FragmentManager manager = getFragmentManager();
    Fragment fragmentToRemove = manager.findFragmentByTag(TAG);

    if(fragmentToRemove == null) {
      manager
          .beginTransaction()
          .add(R.id.content, ExpandableListViewFragment.newInstance(), ExpandableListViewFragment.TAG)
          .add(R.id.content, actionBarFragment, ActionBarFragment.TAG)
          .commit();
    }
    else {
      manager
          .beginTransaction()
          .remove(fragmentToRemove)
          .remove(manager.findFragmentByTag(ActionBarFragment.TAG))
          .add(R.id.content, ExpandableListViewFragment.newInstance(), ExpandableListViewFragment.TAG)
          .add(R.id.content, actionBarFragment, ActionBarFragment.TAG)
          .commit();
    }

    BASE_FRAGMENT_TAG = ExpandableListViewFragment.TAG;
  }

  //編集画面を表示(引数にitemを渡すとそのitemの情報が入力された状態で表示)
  public void showMainEditFragment(String TAG) {

    FragmentManager manager = getFragmentManager();
    Fragment fragmentToRemove = manager.findFragmentByTag(TAG);
    checkNotNull(fragmentToRemove);

    manager
        .beginTransaction()
        .remove(fragmentToRemove)
        .remove(manager.findFragmentByTag(ActionBarFragment.TAG))
        .add(R.id.content, MainEditFragment.newInstance(), MainEditFragment.TAG)
        .addToBackStack(null)
        .commit();
  }

  public void showMainEditFragment(String detail, String TAG) {

    FragmentManager manager = getFragmentManager();
    Fragment fragmentToRemove = manager.findFragmentByTag(TAG);
    checkNotNull(fragmentToRemove);

    manager
        .beginTransaction()
        .remove(fragmentToRemove)
        .remove(manager.findFragmentByTag(ActionBarFragment.TAG))
        .add(R.id.content, MainEditFragment.newInstance(detail), MainEditFragment.TAG)
        .addToBackStack(null)
        .commit();
  }

  public void showMainEditFragment(Item item, String TAG) {

    FragmentManager manager = getFragmentManager();
    Fragment fragmentToRemove = manager.findFragmentByTag(TAG);
    checkNotNull(fragmentToRemove);

    manager
        .beginTransaction()
        .remove(fragmentToRemove)
        .remove(manager.findFragmentByTag(ActionBarFragment.TAG))
        .add(R.id.content, MainEditFragment.newInstance(item), MainEditFragment.TAG)
        .addToBackStack(null)
        .commit();
  }

  public void showMainEditFragmentForList(String TAG) {

    FragmentManager manager = getFragmentManager();
    Fragment fragmentToRemove = manager.findFragmentByTag(TAG);
    checkNotNull(fragmentToRemove);

    manager
        .beginTransaction()
        .remove(fragmentToRemove)
        .remove(manager.findFragmentByTag(ActionBarFragment.TAG))
        .add(R.id.content, MainEditFragment.newInstanceForList(), MainEditFragment.TAG)
        .addToBackStack(null)
        .commit();
  }

  public void showMainEditFragmentForList(NonScheduledList list, String TAG) {

    FragmentManager manager = getFragmentManager();
    Fragment fragmentToRemove = manager.findFragmentByTag(TAG);
    checkNotNull(fragmentToRemove);

    manager
        .beginTransaction()
        .remove(fragmentToRemove)
        .remove(manager.findFragmentByTag(ActionBarFragment.TAG))
        .add(R.id.content, MainEditFragment.newInstanceForList(list), MainEditFragment.TAG)
        .addToBackStack(null)
        .commit();
  }

  public void showNotesFragment(Item item) {

    getFragmentManager()
        .beginTransaction()
        .replace(R.id.content, NotesFragment.newInstance(item))
        .addToBackStack(null)
        .commit();
  }

//  private void showFragment()
}