package com.example.hideaki.reminder;

import android.app.AlarmManager;
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
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

  private static final String SAVED_DATA = "SAVED_DATA";
  private static final String MENU_POSITION = "MENU_POSITION";
  private static final String SUBMENU_POSITION = "SUBMENU_POSITION";

  Timer timer;
  TimerTask timerTask;
  private Handler handler = new Handler();
  private DBAccessor accessor = null;
  private Intent intent;
  private PendingIntent sender;
  private AlarmManager alarmManager;
  private int group_changed; //groupの変化があったかどうかのフラグをビットで表す
  private int group_changed_num; //groupの変化があったchildの個数を保持する
  private final ScheduledItemComparator scheduledItemComparator = new ScheduledItemComparator();
  private final NonScheduledItemComparator nonScheduledItemComparator = new NonScheduledItemComparator();
  public int which_menu_open;
  private int which_submenu_open;
  ExpandableListView expandableListView;
  MyExpandableListAdapter expandableListAdapter;
  ListView listView;
  MyListAdapter listAdapter;
  ManageListAdapter manageListAdapter;
  ColorPickerListAdapter colorPickerListAdapter;
  DrawerLayout drawerLayout;
  NavigationView navigationView;
  ActionBarDrawerToggle drawerToggle;
  Drawable upArrow;
  Menu menu;
  MenuItem menuItem;
  GeneralSettings generalSettings;
  ActionBarFragment actionBarFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    accessor = new DBAccessor(this);

    //DisplayHomeAsUpEnabledに指定する戻るボタンの色を白色に指定
    upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
    assert upArrow != null;
    upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

    //ToolbarをActionBarに互換を持たせて設定
    Toolbar toolbar = findViewById(R.id.toolbar_layout);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    assert actionBar != null;

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

    //共通設定と新しく追加したリストのリストア
    generalSettings = querySettingsDB();
    if(generalSettings == null) generalSettings = new GeneralSettings();
    for(NonScheduledList list : generalSettings.getOrgNonScheduledLists()) {
      Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_my_list_24dp);
      assert drawable != null;
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
    expandableListAdapter = new MyExpandableListAdapter(getChildren(MyDatabaseHelper.TODO_TABLE), this);
    manageListAdapter = new ManageListAdapter(generalSettings.getNonScheduledLists(), this);
    colorPickerListAdapter = new ColorPickerListAdapter(this);

    //前回開いていたNavigationDrawer上のメニューをリストアする
    SharedPreferences preferences = getSharedPreferences(SAVED_DATA, MODE_PRIVATE);
    which_menu_open = preferences.getInt(MENU_POSITION, 0);
    which_submenu_open = preferences.getInt(SUBMENU_POSITION, 0);

    menuItem = navigationView.getMenu().getItem(which_menu_open);
    if(menuItem.hasSubMenu()) {
      menuItem.getSubMenu().getItem(which_submenu_open).setChecked(true);
    }
    else {
      menuItem.setChecked(true);
    }

    if(menuItem.getOrder() == 1) {
      showListViewFragment();
    }
    else showExpandableListViewFragment();
    showActionBar();

    if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
      NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
      assert notificationManager != null;
      NotificationChannel notificationChannel = new NotificationChannel("reminder_01",
          getString(R.string.notification_channel_name), NotificationManager.IMPORTANCE_HIGH);

      notificationManager.createNotificationChannel(notificationChannel);
    }
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
        this.menuItem = menuItem;
        switch(menuItem.getOrder()) {
          case 0:
            showExpandableListViewFragment();
            showActionBar();
            break;
          case 1:
            showListViewFragment();
            showActionBar();
            break;
          case 3:
            showManageListViewFragment();
            showActionBar();
            break;
          case 4:
            break;
          case 5:
            break;
          case 6:
            break;
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
              generalSettings.addNonScheduledList(new NonScheduledList(editText.getText().toString()));
              int size = generalSettings.getOrgNonScheduledLists().size();
              for(int i = 0; i < size; i++) {
                generalSettings.getOrgNonScheduledLists().get(i).setOrder(i);
              }
              ManageListAdapter.nonScheduledLists = generalSettings.getNonScheduledLists();
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
              for(NonScheduledList list : generalSettings.getOrgNonScheduledLists()) {
                Drawable drawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_my_list_24dp);
                assert drawable != null;
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
              if(querySettingsDB() == null) {
                insertSettingsDB();
              }
              else updateSettingsDB();
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
            assert dialogWindow != null;

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
                MyExpandableListAdapter.children.get(group_count).remove(i);
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
      intent.putExtra(MainEditFragment.ITEM, ob_array);
      sender = PendingIntent.getBroadcast(
          this, (int)item.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

      alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        alarmManager.setAlarmClock(
            new AlarmManager.AlarmClockInfo(item.getDate().getTimeInMillis(), null), sender);
      } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, item.getDate().getTimeInMillis(), sender);
      } else {
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
      assert alarmManager != null;

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
        deleteAlarm(item);
        if(!item.isAlarm_stopped()) {
          setAlarm(item);
        }

        int spec_day = item.getDate().get(Calendar.DAY_OF_MONTH);
        long sub_time = item.getDate().getTimeInMillis() - now.getTimeInMillis();
        long sub_day = sub_time / (1000 * 60 * 60 * 24);

        if(sub_time < 0) {
          past_list.add(item);
        } else if(sub_day < 1 && spec_day == now.get(Calendar.DAY_OF_MONTH)) {
          today_list.add(item);
        } else if(sub_day < 2 && spec_day == tomorrow.get(Calendar.DAY_OF_MONTH)) {
          tomorrow_list.add(item);
        } else if(sub_day < 8) {
          week_list.add(item);
        } else {
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

    List<Item> itemList = new ArrayList<>();
    for(Item item : queryAllDB(table)) {
      if(item.getWhich_list_belongs() == generalSettings.getNonScheduledList(which_menu_open - 1).getId()) {
        itemList.add(item);
      }
    }
    Collections.sort(itemList, nonScheduledItemComparator);

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

  //シリアライズメソッド
  public static byte[] serialize(Object data) {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(data);
      oos.flush();
      oos.close();
    } catch(IOException e) {
      e.printStackTrace();
    }

    return baos.toByteArray();
  }

  //デシリアライズメソッド
  public static Object deserialize(byte[] stream) {

    if(stream == null) return null;
    else {
      ByteArrayInputStream bais = new ByteArrayInputStream(stream);
      Object data = null;
      try {
        ObjectInputStream ois = new ObjectInputStream(bais);
        data = ois.readObject();
        ois.close();
      } catch(IOException e) {
        e.printStackTrace();
      } catch(ClassNotFoundException e) {
        e.printStackTrace();
      }

      return data;
    }
  }

  public void showColorPickerListViewFragment() {

    getFragmentManager()
        .beginTransaction()
        .replace(R.id.content, ColorPickerListViewFragment.newInstance())
        .addToBackStack(null)
        .commit();
  }

  private void showManageListViewFragment() {

    getFragmentManager()
        .beginTransaction()
        .replace(R.id.content, ManageListViewFragment.newInstance())
        .commit();
  }

  private void showListViewFragment() {

    getFragmentManager()
        .beginTransaction()
        .replace(R.id.content, ListViewFragment.newInstance())
        .commit();
  }

  private void showExpandableListViewFragment() {

    getFragmentManager()
        .beginTransaction()
        .replace(R.id.content, ExpandableListViewFragment.newInstance())
        .commit();
  }

  //メイン画面のアクションバーを表示
  private void showActionBar() {

    actionBarFragment = ActionBarFragment.newInstance();
    getFragmentManager()
        .beginTransaction()
        .add(R.id.content, actionBarFragment)
        .commit();
  }

  //編集画面を表示(引数にitemを渡すとそのitemの情報が入力された状態で表示)
  public void showMainEditFragment() {

    getFragmentManager()
        .beginTransaction()
        .replace(R.id.content, MainEditFragment.newInstance())
        .addToBackStack(null)
        .commit();
  }

  public void showMainEditFragment(Item item) {

    getFragmentManager()
        .beginTransaction()
        .replace(R.id.content, MainEditFragment.newInstance(item))
        .addToBackStack(null)
        .commit();
  }

  public void showMainEditFragmentForList() {

    getFragmentManager()
        .beginTransaction()
        .replace(R.id.content, MainEditFragment.newInstanceForList())
        .addToBackStack(null)
        .commit();
  }

  public void showMainEditFragmentForList(NonScheduledList list) {

    getFragmentManager()
        .beginTransaction()
        .replace(R.id.content, MainEditFragment.newInstanceForList(list))
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
}