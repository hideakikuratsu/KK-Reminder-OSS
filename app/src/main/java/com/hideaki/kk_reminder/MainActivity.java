package com.hideaki.kk_reminder;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryRecord;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchaseHistoryParams;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.Fade;
import androidx.transition.Transition;
import bolts.Continuation;
import bolts.Task;
import bolts.TaskCompletionSource;

import static android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM;
import static com.google.common.base.Preconditions.checkArgument;
import static com.hideaki.kk_reminder.MyDatabaseHelper.TODO_TABLE;
import static com.hideaki.kk_reminder.StartupReceiver.getDynamicContext;
import static com.hideaki.kk_reminder.StartupReceiver.getIsDirectBootContext;
import static com.hideaki.kk_reminder.UtilClass.ACTION_IN_NOTIFICATION;
import static com.hideaki.kk_reminder.UtilClass.BOOLEAN_GENERAL;
import static com.hideaki.kk_reminder.UtilClass.BOOLEAN_GENERAL_COPY;
import static com.hideaki.kk_reminder.UtilClass.BOOT_FROM_NOTIFICATION;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_MINUS_TIME_1_HOUR;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_MINUS_TIME_1_MINUTE;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_MINUS_TIME_2_HOUR;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_MINUS_TIME_2_MINUTE;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_MINUS_TIME_3_HOUR;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_MINUS_TIME_3_MINUTE;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_PLUS_TIME_1_HOUR;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_PLUS_TIME_1_MINUTE;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_PLUS_TIME_2_HOUR;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_PLUS_TIME_2_MINUTE;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_PLUS_TIME_3_HOUR;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_PLUS_TIME_3_MINUTE;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_TEXT_SIZE;
import static com.hideaki.kk_reminder.UtilClass.DEFAULT_URI_SOUND;
import static com.hideaki.kk_reminder.UtilClass.DONE_ITEM_COMPARATOR;
import static com.hideaki.kk_reminder.UtilClass.HOUR;
import static com.hideaki.kk_reminder.UtilClass.INT_GENERAL;
import static com.hideaki.kk_reminder.UtilClass.INT_GENERAL_COPY;
import static com.hideaki.kk_reminder.UtilClass.IS_COPIED_FROM_OLD_VERSION;
import static com.hideaki.kk_reminder.UtilClass.IS_DARK_MODE;
import static com.hideaki.kk_reminder.UtilClass.IS_DARK_THEME_FOLLOW_SYSTEM;
import static com.hideaki.kk_reminder.UtilClass.IS_EXPANDABLE_TODO;
import static com.hideaki.kk_reminder.UtilClass.IS_PREMIUM;
import static com.hideaki.kk_reminder.UtilClass.IS_QUERIED_PURCHASE_HISTORY;
import static com.hideaki.kk_reminder.UtilClass.IS_RECREATED;
import static com.hideaki.kk_reminder.UtilClass.IS_RECREATED_TWICE;
import static com.hideaki.kk_reminder.UtilClass.ITEM;
import static com.hideaki.kk_reminder.UtilClass.LOCALE;
import static com.hideaki.kk_reminder.UtilClass.MENU_POSITION;
import static com.hideaki.kk_reminder.UtilClass.MINUTE;
import static com.hideaki.kk_reminder.UtilClass.NON_SCHEDULED_ITEM_COMPARATOR;
import static com.hideaki.kk_reminder.UtilClass.PLAY_SLIDE_ANIMATION;
import static com.hideaki.kk_reminder.UtilClass.PRODUCT_ID_PREMIUM;
import static com.hideaki.kk_reminder.UtilClass.READ_NOTICE;
import static com.hideaki.kk_reminder.UtilClass.SCHEDULED_ITEM_COMPARATOR;
import static com.hideaki.kk_reminder.UtilClass.SNOOZE_DEFAULT_HOUR;
import static com.hideaki.kk_reminder.UtilClass.SNOOZE_DEFAULT_MINUTE;
import static com.hideaki.kk_reminder.UtilClass.SUBMENU_POSITION;
import static com.hideaki.kk_reminder.UtilClass.copyDatabase;
import static com.hideaki.kk_reminder.UtilClass.copySharedPreferences;
import static com.hideaki.kk_reminder.UtilClass.deserialize;
import static com.hideaki.kk_reminder.UtilClass.generateUniqueId;
import static com.hideaki.kk_reminder.UtilClass.getAdSize;
import static com.hideaki.kk_reminder.UtilClass.getPxFromDp;
import static com.hideaki.kk_reminder.UtilClass.readFileFromAssets;
import static com.hideaki.kk_reminder.UtilClass.serialize;
import static com.hideaki.kk_reminder.UtilClass.setCursorDrawableColor;
import static java.util.Objects.requireNonNull;

public class MainActivity extends AppCompatActivity
  implements NavigationView.OnNavigationItemSelectedListener,
  PurchasesUpdatedListener,
  AcknowledgePurchaseResponseListener {

  @SuppressWarnings("FieldNamingConvention")
  static String IS_FIRST_USE = "";

  private Timer timer;
  Handler handler;
  private Runnable runnable;
  private DBAccessor accessor = null;
  int minusTime1Hour;
  int minusTime1Minute;
  int minusTime2Hour;
  int minusTime2Minute;
  int minusTime3Hour;
  int minusTime3Minute;
  int plusTime1Hour;
  int plusTime1Minute;
  int plusTime2Hour;
  int plusTime2Minute;
  int plusTime3Hour;
  int plusTime3Minute;
  int snoozeDefaultHour;
  int snoozeDefaultMinute;
  int whichMenuOpen;
  int whichSubmenuOpen;
  boolean isExpandableTodo;
  boolean isPremium;
  PinnedHeaderExpandableListView expandableListView;
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
  private BadgeDrawerArrowDrawable badgeDrawable;
  Drawable upArrow;
  Menu menu;
  MenuItem menuItem;
  GeneralSettingsAdapter generalSettings;
  ActionBarFragment actionBarFragment;
  ExpandableListViewFragment expandableListViewFragment;
  Toolbar toolbar;
  int menuItemColor;
  int menuBackgroundColor;
  int statusBarColor;
  int accentColor;
  int secondaryTextColor;
  int order;
  private String detail;
  private int whichList;
  boolean isBootFromNotification;
  private int tryCount;
  private BillingClient billingClient;
  public AlertDialog promotionDialog;
  private final BroadcastReceiver defaultSnoozeReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {

      MyExpandableListAdapter.children = getChildren(MyDatabaseHelper.TODO_TABLE);
      expandableListAdapter.notifyDataSetChanged();
    }
  };
  int dialogStyleId;
  int textSize;
  int whichTextSize;
  boolean isPlaySlideAnimation;
  static boolean isScreenOn = false;
  boolean isDarkMode;
  boolean isDarkThemeFollowSystem;
  boolean isFirstUse;
  boolean isQueriedPurchaseHistory;
  int primaryMaterialDarkColor;
  int primaryDarkMaterialDarkColor;
  int backgroundMaterialDarkColor;
  int backgroundFloatingMaterialDarkColor;
  int primaryTextMaterialDarkColor;
  int secondaryTextMaterialDarkColor;
  private AlertDialog updateInfoMessageDialog = null;
  private boolean isRecreated;
  private boolean isRecreatedTwice;
  private List<ProductDetails> productDetailsList = null;
  List<ItemAdapter> todoItemList;
  boolean isCopiedFromOldVersion;
  AdView adView;
  private boolean readNotice;
  private boolean dateSet;
  private static final Map<String, Integer> DAY_OF_WEEK_MAP_JP = new HashMap<>();
  private static final Map<String, Integer> DAY_OF_WEEK_MAP_EN = new HashMap<>();
  private static final Map<String, Integer> MONTH_MAP = new HashMap<>();
  private final ActivityResultLauncher<String> postNotificationPermissionLauncher =
      registerForActivityResult(new RequestPermission(), isPermitted -> {
          if (!isPermitted) {
            processPostNotificationPermissionDenial();
          }
        }
      );

  private void processPostNotificationPermissionDenial() {

    View parentView = findViewById(android.R.id.content);
    requireNonNull(parentView);
    Snackbar
        .make(parentView, R.string.permission_post_notification_denied, Snackbar.LENGTH_LONG)
        .show();
  }

  private void processExactAlarmPermissionDenial() {

    View parentView = findViewById(android.R.id.content);
    requireNonNull(parentView);
    Snackbar
        .make(parentView, R.string.permission_exact_alarm_denied, Snackbar.LENGTH_LONG)
        .show();
  }

  static {
    DAY_OF_WEEK_MAP_JP.put("日", 1);
    DAY_OF_WEEK_MAP_JP.put("月", 2);
    DAY_OF_WEEK_MAP_JP.put("火", 3);
    DAY_OF_WEEK_MAP_JP.put("水", 4);
    DAY_OF_WEEK_MAP_JP.put("木", 5);
    DAY_OF_WEEK_MAP_JP.put("金", 6);
    DAY_OF_WEEK_MAP_JP.put("土", 7);

    DAY_OF_WEEK_MAP_EN.put("Sun", 1);
    DAY_OF_WEEK_MAP_EN.put("Mon", 2);
    DAY_OF_WEEK_MAP_EN.put("Tu", 3);
    DAY_OF_WEEK_MAP_EN.put("Wed", 4);
    DAY_OF_WEEK_MAP_EN.put("Th", 5);
    DAY_OF_WEEK_MAP_EN.put("Fri", 6);
    DAY_OF_WEEK_MAP_EN.put("Sat", 7);

    MONTH_MAP.put("Jan", 0);
    MONTH_MAP.put("Feb", 1);
    MONTH_MAP.put("Mar", 2);
    MONTH_MAP.put("Apr", 3);
    MONTH_MAP.put("May", 4);
    MONTH_MAP.put("Jun", 5);
    MONTH_MAP.put("Jul", 6);
    MONTH_MAP.put("Aug", 7);
    MONTH_MAP.put("Sep", 8);
    MONTH_MAP.put("Oct", 9);
    MONTH_MAP.put("Nov", 10);
    MONTH_MAP.put("Dec", 11);
  }


  @SuppressLint("UnspecifiedRegisterReceiverFlag")
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    if(IS_FIRST_USE.isEmpty()) {
      IS_FIRST_USE = "IS_FIRST_USE_" + getString(R.string.version_value);
    }

    super.onCreate(savedInstanceState);
    System.out.println("onCreate");

    handler = new Handler(getMainLooper());
    accessor = new DBAccessor(this, false);

    // 共通設定と新しく追加したリストのリストア
    generalSettings = querySettingsDB();
    if(generalSettings == null) {
      initSettings();
    }

    // SharedPreferencesの内、intを読み出す
    SharedPreferences intPreferences =
      getDynamicContext(this).getSharedPreferences(
        getIsDirectBootContext(this) ? INT_GENERAL_COPY : INT_GENERAL,
        MODE_PRIVATE
      );
    minusTime1Hour = intPreferences.getInt(DEFAULT_MINUS_TIME_1_HOUR, 0);
    minusTime1Minute = intPreferences.getInt(DEFAULT_MINUS_TIME_1_MINUTE, 5);
    minusTime2Hour = intPreferences.getInt(DEFAULT_MINUS_TIME_2_HOUR, 1);
    minusTime2Minute = intPreferences.getInt(DEFAULT_MINUS_TIME_2_MINUTE, 0);
    minusTime3Hour = intPreferences.getInt(DEFAULT_MINUS_TIME_3_HOUR, 0);
    minusTime3Minute = intPreferences.getInt(DEFAULT_MINUS_TIME_3_MINUTE, 0);
    plusTime1Hour = intPreferences.getInt(DEFAULT_PLUS_TIME_1_HOUR, 0);
    plusTime1Minute = intPreferences.getInt(DEFAULT_PLUS_TIME_1_MINUTE, 5);
    plusTime2Hour = intPreferences.getInt(DEFAULT_PLUS_TIME_2_HOUR, 1);
    plusTime2Minute = intPreferences.getInt(DEFAULT_PLUS_TIME_2_MINUTE, 0);
    plusTime3Hour = intPreferences.getInt(DEFAULT_PLUS_TIME_3_HOUR, 0);
    plusTime3Minute = intPreferences.getInt(DEFAULT_PLUS_TIME_3_MINUTE, 0);
    whichTextSize = intPreferences.getInt(DEFAULT_TEXT_SIZE, 0);
    textSize = 18 + 2 * whichTextSize;
    snoozeDefaultHour = intPreferences.getInt(SNOOZE_DEFAULT_HOUR, 0);
    snoozeDefaultMinute = intPreferences.getInt(SNOOZE_DEFAULT_MINUTE, 15);
    whichMenuOpen = intPreferences.getInt(MENU_POSITION, 0);
    whichSubmenuOpen = intPreferences.getInt(SUBMENU_POSITION, 0);

    // SharedPreferencesの内、booleanを読み出す
    SharedPreferences booleanPreferences =
      getDynamicContext(this).getSharedPreferences(
        getIsDirectBootContext(this) ? BOOLEAN_GENERAL_COPY : BOOLEAN_GENERAL,
        MODE_PRIVATE
      );
    readNotice = booleanPreferences.getBoolean(READ_NOTICE, true);
    isCopiedFromOldVersion =
      booleanPreferences.getBoolean(IS_COPIED_FROM_OLD_VERSION, false);
    isQueriedPurchaseHistory =
      booleanPreferences.getBoolean(IS_QUERIED_PURCHASE_HISTORY, false);
    isRecreated = booleanPreferences.getBoolean(IS_RECREATED, false);
    isRecreatedTwice = booleanPreferences.getBoolean(IS_RECREATED_TWICE, false);
    isFirstUse = booleanPreferences.getBoolean(IS_FIRST_USE, true);
    isDarkMode = booleanPreferences.getBoolean(IS_DARK_MODE, false);
    isDarkThemeFollowSystem =
      booleanPreferences.getBoolean(IS_DARK_THEME_FOLLOW_SYSTEM, true);
    isPlaySlideAnimation = booleanPreferences.getBoolean(PLAY_SLIDE_ANIMATION, true);
    isExpandableTodo = booleanPreferences.getBoolean(IS_EXPANDABLE_TODO, true);
    isPremium = booleanPreferences.getBoolean(IS_PREMIUM, false);

    // ダークモードの設定
    int currentNightMode =
        getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    if(!isDarkThemeFollowSystem) {
      if(isDarkMode && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
        setBooleanGeneralInSharedPreferences(IS_RECREATED, true);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        return;
      }
      else if(!isDarkMode && currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
        setBooleanGeneralInSharedPreferences(IS_RECREATED, true);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        return;
      }
    }
    else {
      if(isDarkMode && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
        setBooleanGeneralInSharedPreferences(IS_RECREATED, true);
        setBooleanGeneralInSharedPreferences(IS_DARK_MODE, false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        recreate();
        return;
      }
      else if(!isDarkMode && currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
        setBooleanGeneralInSharedPreferences(IS_RECREATED, true);
        setBooleanGeneralInSharedPreferences(IS_DARK_MODE, true);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        recreate();
        return;
      }
    }
    if(isRecreated) {
      setBooleanGeneralInSharedPreferences(IS_RECREATED_TWICE, true);
      System.out.println("Recreated");
      return;
    }
    System.out.println("onCreateComplete");

    // 広告読み出し機能のセットアップ
    MobileAds.initialize(this, initializationStatus -> {});
    AdSize adSize = getAdSize(this);
    adView = new AdView(this);
    adView.setAdSize(adSize);
    adView.setAdUnitId(this.getString(R.string.ad_unit_id));
    AdRequest adRequest = new AdRequest.Builder().build();
    adView.loadAd(adRequest);

    if(!isPremium) {

      // ビリングサービスのセットアップ
      setupBillingServices();

      // プロモーション用ダイアログのセットアップ
      createPromotionDialog();
    }

    // テーマの設定
    MyThemeAdapter theme = generalSettings.getTheme();
    if(theme.getColor() != 0) {
      Resources res = getResources();
      TypedArray typedArraysOfArray = res.obtainTypedArray(R.array.colorStylesArray);

      int stylesArrayId = typedArraysOfArray.getResourceId(theme.getColorGroup(), -1);
      checkArgument(stylesArrayId != -1);
      TypedArray typedArray = res.obtainTypedArray(stylesArrayId);

      int styleId = typedArray.getResourceId(theme.getColorChild(), -1);
      checkArgument(styleId != -1);

      typedArray.recycle();
      typedArraysOfArray.recycle();

      setTheme(styleId);
    }

    theme.setIsColorPrimary(false);
    if(theme.getColor() != 0) {
      Resources res = getResources();
      TypedArray typedArraysOfArray = res.obtainTypedArray(R.array.colorDialogStylesArray);

      int dialogStylesArrayId = typedArraysOfArray.getResourceId(
        theme.getColorGroup(),
        -1
      );
      checkArgument(dialogStylesArrayId != -1);
      TypedArray typedArray = res.obtainTypedArray(dialogStylesArrayId);

      dialogStyleId = typedArray.getResourceId(theme.getColorChild(), -1);
      checkArgument(dialogStyleId != -1);

      typedArray.recycle();
      typedArraysOfArray.recycle();
    }
    else {
      dialogStyleId = R.style.BaseDialog_Base;
    }
    theme.setIsColorPrimary(true);

    setContentView(R.layout.activity_main);

    // ToolbarをActionBarに互換を持たせて設定
    toolbar = findViewById(R.id.toolbar_layout);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    requireNonNull(actionBar);

    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowHomeEnabled(true);

    // NavigationDrawerの設定
    drawerLayout = findViewById(R.id.drawer_layout);
    drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
      R.string.drawer_open, R.string.drawer_close
    );
    badgeDrawable = new BadgeDrawerArrowDrawable(getSupportActionBar().getThemedContext());
    drawerToggle.setDrawerIndicatorEnabled(true);
    drawerToggle.setDrawerArrowDrawable(badgeDrawable);
    drawerLayout.addDrawerListener(drawerToggle);

    navigationView = findViewById(R.id.nav_view);
    menu = navigationView.getMenu();
    navigationView.setItemIconTintList(null);
    navigationView.setNavigationItemSelectedListener(this);

    // 各NonScheduledListを読み込む
    for(NonScheduledListAdapter list : generalSettings.getNonScheduledLists()) {
      Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_my_list_24dp);
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
          ContextCompat.getColor(this, R.color.iconGray),
          PorterDuff.Mode.SRC_IN
        ));
      }
      menu.add(R.id.reminder_list, generateUniqueId(), 1, list.getTitle())
        .setIcon(drawable)
        .setCheckable(true);
    }

    MenuItem noticeItem = menu.findItem(R.id.notice);
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU || Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
      badgeDrawable.setEnabled(false);
      noticeItem.setVisible(false);
    }
    else {
      if(readNotice) {
        badgeDrawable.setEnabled(false);
        noticeItem.setIcon(R.drawable.ic_email_read_24dp);
      }
      else {
        badgeDrawable.setEnabled(true);
        badgeDrawable.setBackgroundColor(
            ContextCompat.getColor(this, R.color.red11PrimaryColor)
        );
        noticeItem.setIcon(R.drawable.ic_email_unread_24dp);
      }
    }

    // Adapterの初期化
    todoItemList = queryAllDB(TODO_TABLE);
    expandableListAdapter =
      new MyExpandableListAdapter(getChildren(todoItemList), this);
    listAdapter = new MyListAdapter(this);
    manageListAdapter = new ManageListAdapter(
      new ArrayList<>(generalSettings.getNonScheduledLists()),
      this
    );
    colorPickerListAdapter = new ColorPickerListAdapter(this);
    tagEditListAdapter = new TagEditListAdapter(
      new ArrayList<>(generalSettings.getTagList()),
      this
    );
    doneListAdapter = new DoneListAdapter(this);

    // Intentが送られている場合はonNewIntent()に渡す(送られていない場合は通常の初期化処理を行う)
    onNewIntent(getIntent());

    // 画面がフォアグラウンドの状態におけるDefaultManuallySnoozeReceiverからのインテントを待ち受ける
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      registerReceiver(defaultSnoozeReceiver, new IntentFilter(ACTION_IN_NOTIFICATION), Context.RECEIVER_NOT_EXPORTED);
    }
    else {
      registerReceiver(defaultSnoozeReceiver, new IntentFilter(ACTION_IN_NOTIFICATION));
    }
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {

    super.onPostCreate(savedInstanceState);
    System.out.println("onPostCreate");
    if(!isRecreated) {
      drawerToggle.syncState();
    }
  }

  @Override
  protected void onResume() {

    super.onResume();
    System.out.println("onResume");

    if(!isRecreated) {
      // SDK VER.33以上はアプリの通知送信にPOST_NOTIFICATIONの権限が必要
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
          ActivityCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
          ) == PackageManager.PERMISSION_DENIED
      ) {
        if(shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
          final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
              .setTitle(R.string.permission_post_notification_rationale_title)
              .setMessage(R.string.permission_post_notification_rationale_message)
              .setPositiveButton(R.string.permit, (dialog1, which) ->
                  postNotificationPermissionLauncher.launch(
                      Manifest.permission.POST_NOTIFICATIONS
                  )
              )
              .setNegativeButton(R.string.deny, (dialog12, which) -> processPostNotificationPermissionDenial())
              .setNeutralButton(R.string.cancel, (dialog13, which) -> processPostNotificationPermissionDenial())
              .create();

          dialog.setOnShowListener(dialogInterface -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(MainActivity.this.accentColor);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(MainActivity.this.accentColor);
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(MainActivity.this.accentColor);
          });

          dialog.show();
        }
        else {
          postNotificationPermissionLauncher.launch(
              Manifest.permission.POST_NOTIFICATIONS
          );
        }
      }

      // SDK VER.31以上はアプリ通知の正確な時刻での発火にExact Alarm Permissionの権限が必要
      // Exact Alarm Permissionを求めるダイアログ表示
      AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
      requireNonNull(alarmManager);
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if(!alarmManager.canScheduleExactAlarms()) {
          final AlertDialog dialog = new AlertDialog.Builder(this)
              .setTitle(R.string.permission_exact_alarm_rationale_title)
              .setMessage(R.string.permission_exact_alarm_rationale_message)
              .setPositiveButton(R.string.permit, (dialog15, which12) -> {
                  Intent intent = new Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                      .setData(Uri.parse("package:" + getPackageName()));
                  startActivity(intent);
                }
              )
              .setNegativeButton(R.string.deny, (dialog1, which1) -> processExactAlarmPermissionDenial())
              .setNeutralButton(R.string.cancel, (dialog13, which) -> processExactAlarmPermissionDenial())
              .create();

          dialog.setOnShowListener(dialogInterface -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(MainActivity.this.accentColor);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(MainActivity.this.accentColor);
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(MainActivity.this.accentColor);
          });

          dialog.show();
        }
      }

      // アプリ更新後初めての起動時にアップデート情報を表示
      if(isFirstUse) {
        updateInfoMessageDialog = createUpdateInfoMessageDialog();
        if(updateInfoMessageDialog != null) {
          updateInfoMessageDialog.show();
        }
      }

      // generalSettingsにおいて旧バージョンからの移行処理が行われた場合は、まだデータベース全体の
      // 移行処理が行われていないものとしてデータベースを更新する。
      // ダークモード切り替えによるActivityのrecreate()が発生した場合は処理が中断してしまうので
      // !isRecreatedの条件により、確実に1回だけ実行されるようにしてある。
      if(generalSettings.isCopiedFromOldVersion()) {
        setBooleanGeneralInSharedPreferences(IS_COPIED_FROM_OLD_VERSION, true);
        generalSettings.setIsCopiedFromOldVersion(false);
      }
      if(isCopiedFromOldVersion) {
        CopyFromOldVersionProgressBarDialogFragment dialog =
            new CopyFromOldVersionProgressBarDialogFragment();
        dialog.show(getSupportFragmentManager(), "copy_from_old_version_progress_bar");
      }

      // すべての通知を既読し、通知チャネルを削除する
      clearAllNotification();

      if(!isPremium) {

        // ビリングサービスのセットアップ
        setupBillingServices();
      }

      Fragment mainFragment =
          getSupportFragmentManager().findFragmentByTag(ExpandableListViewFragment.TAG);
      if(mainFragment != null) {
        MyExpandableListAdapter.isLockBlockNotifyChange = true;
        MyExpandableListAdapter.isBlockNotifyChange = true;
        updateListTask(null, -1, true, false);
        setUpdateListTimerTask(true);
      }

      isScreenOn = true;
    }

    // 端末によってはシステム設定と異なるダークモード設定を行うとダークモード切り替え時(アプリ起動時)
    // アプリの再起動が二重で発生してしまう端末が存在する。そのため基本的には二重に再起動させることを
    // 前提とし、二重再起動が発生しない端末については以下のように二重再起動を強制させる。また二重再起動
    // が自然に発生する端末についてはrecreate()を行うかどうかの判定を描画処理キューの最後に置いておく
    // ことで、強制再起動発動までに発生した再起動を優先させる。
    handler.post(() -> {
      if(isRecreatedTwice) {
        recreate();
      }
    });
  }

  @Override
  protected void onPause() {

    super.onPause();
    System.out.println("onPause");

    if(!isRecreated) {
      if(updateInfoMessageDialog != null) {
        updateInfoMessageDialog.cancel();
        updateInfoMessageDialog = null;
      }

      // ExpandableListViewの自動更新を止める
      setUpdateListTimerTask(false);

      // DirectBootモードからMainActivityを起動すると何故かonResume()が呼ばれた後に一度onPause()が
      // 呼ばれ、再びonResume()が呼ばれるが、そうなっても良いようにonPause()側で端末暗号化ストレージ
      // へのコピー処理にgetIsDirectBootContext()を使って条件を設けている。
      if(!getIsDirectBootContext(this)) {
        // データベースを端末暗号化ストレージへコピーする
        copyDatabase(this, false);
        copySharedPreferences(this, false);
      }

      isScreenOn = false;
    }
  }

  @Override
  protected void onDestroy() {

    super.onDestroy();
    System.out.println("onDestroy");
    if(!isRecreated) {
      if(billingClient != null) {
        billingClient.endConnection();
      }
      unregisterReceiver(defaultSnoozeReceiver);
    }

    if(isRecreatedTwice) {
      setBooleanGeneralInSharedPreferences(IS_RECREATED, false);
      setBooleanGeneralInSharedPreferences(IS_RECREATED_TWICE, false);
    }
  }

  private AlertDialog createUpdateInfoMessageDialog() {

    final String updateInfoFileName =
      "update_info_" +
        getString(R.string.version_value) +
        (LOCALE.equals(Locale.JAPAN) ? "_ja.txt" : "_en.txt");

    String message = readFileFromAssets(this, updateInfoFileName);
    if(!message.isEmpty()) {
      TextView customTitle = new TextView(this);
      String title =
        "ver. " + getString(R.string.version_value) + " " + getString(R.string.update_info);
      int leftPadding = getPxFromDp(this, 8);
      int topPadding = getPxFromDp(this, 20);
      int rightPadding = getPxFromDp(this, 8);
      int bottomPadding = getPxFromDp(this, 8);
      customTitle.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
      customTitle.setTextSize(getPxFromDp(this, 8));
      customTitle.setTypeface(Typeface.DEFAULT_BOLD);
      customTitle.setText(title);
      if(isDarkMode) {
        customTitle.setTextColor(
          ContextCompat.getColor(this, R.color.primaryTextMaterialDark)
        );
      }
      else {
        customTitle.setTextColor(ContextCompat.getColor(this, android.R.color.black));
      }
      customTitle.setGravity(Gravity.CENTER);

      final AlertDialog updateInfoMessageDialog = new AlertDialog.Builder(this)
        .setCancelable(false)
        .setCustomTitle(customTitle)
        .setMessage(message)
        .setPositiveButton(R.string.ok, (dialogInterface, i) -> setBooleanGeneralInSharedPreferences(IS_FIRST_USE, false))
        .create();

      updateInfoMessageDialog.setOnShowListener(dialogInterface -> {

        updateInfoMessageDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(accentColor);
        updateInfoMessageDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(accentColor);
      });

      return updateInfoMessageDialog;
    }
    else {
      return null;
    }
  }

  private void createPromotionDialog() {

    promotionDialog = new AlertDialog.Builder(this)
      .setTitle(R.string.disable_ads)
      .setMessage(R.string.disable_ads_promotion)
      .setPositiveButton(R.string.yes, (dialog, which) -> onBuyButtonClicked())
      .setNeutralButton(R.string.cancel, (dialog, which) -> {})
      .create();

    promotionDialog.setOnShowListener(dialogInterface -> {

      promotionDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(accentColor);
      promotionDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(accentColor);
    });
  }

  private void setupBillingServices() {

    if(billingClient == null) {
      tryCount = 0;
      billingClient = BillingClient
        .newBuilder(this)
        .setListener(this)
        .enablePendingPurchases()
        .build();

      billingClient.startConnection(new BillingClientStateListener() {
        @Override
        public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

          if(billingResult.getResponseCode() == BillingResponseCode.OK) {
            // プレミアムアカウントかどうかの確認
            checkIsPremium();
          }
          else {
            tryCount++;
            if(tryCount < 3) {
              billingClient.startConnection(this);
            }
            else {
              Log.e("MainActivity#onBillingSetupFinished", "Cannot start connection");
            }
          }
        }

        @Override
        public void onBillingServiceDisconnected() {

          tryCount++;
          if(tryCount < 3) {
            billingClient.startConnection(this);
          }
          else {
            Log.e("MainActivity#onBillingSetupFinished", "Cannot start connection");
          }
        }
      });
    }
    else {
      checkIsPremium();
    }
  }

  private void checkIsPremium() {

    QueryPurchasesParams params = QueryPurchasesParams.newBuilder()
        .setProductType(BillingClient.ProductType.INAPP)
        .build();
    // 購入履歴を問い合わせる(ローカルのキャッシュデータを参照)
    billingClient.queryPurchasesAsync(
        params, (billingResult, purchaseList) -> {

      int responseCode = billingResult.getResponseCode();
      if(responseCode == BillingResponseCode.OK) {
        Log.i("MainActivity#checkIsPremium", getResponseCodeString(responseCode));
        if(purchaseList.isEmpty()) {
          if(!isQueriedPurchaseHistory) {
            queryPurchaseHistory();
          }
        }
        else {
          for(Purchase purchase : purchaseList) {
            checkPurchaseState(purchase);
          }
        }
      }
      else {
        Log.w("MainActivity#checkIsPremium", getResponseCodeString(responseCode));
      }
    });
  }

  // 購入履歴を問い合わせる(ネットワークアクセス処理)
  private void queryPurchaseHistory() {

    QueryPurchaseHistoryParams params = QueryPurchaseHistoryParams.newBuilder()
        .setProductType(BillingClient.ProductType.INAPP)
        .build();
    billingClient.queryPurchaseHistoryAsync(
      params,
        (billingResult, purchasesList) -> {

          int responseCode = billingResult.getResponseCode();
          if(responseCode == BillingResponseCode.OK) {
            Log.i("MainActivity#queryPurchaseHistory", getResponseCodeString(responseCode));
            handler.post(() ->
                setBooleanGeneralInSharedPreferences(IS_QUERIED_PURCHASE_HISTORY, true)
            );
            if(purchasesList == null || purchasesList.isEmpty()) {
              Log.w("MainActivity#queryPurchaseHistory", "No History");
            }
            else {
              for(PurchaseHistoryRecord purchase : purchasesList) {
                if(purchase.getProducts().contains(PRODUCT_ID_PREMIUM)) {
                  Log.i(
                      "MainActivity#queryPurchaseHistory",
                      purchase.getProducts().get(0) + ": Purchased"
                  );
                  handler.post(() -> {
                    Toast
                        .makeText(
                            MainActivity.this,
                            getString(R.string.succeed_to_upgrade),
                            Toast.LENGTH_LONG
                        )
                        .show();
                    setBooleanGeneralInSharedPreferences(IS_PREMIUM, true);
                    if(expandableListViewFragment != null) {
                      expandableListViewFragment.disableAdView();
                    }
                  });
                }
              }
            }
          }
          else {
            Log.w("MainActivity#queryPurchaseHistory", getResponseCodeString(responseCode));
          }
        }
    );
  }

  @Override
  public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {

    int responseCode = billingResult.getResponseCode();
    if(responseCode == BillingResponseCode.OK) {
      if(purchases == null || purchases.isEmpty()) {
        Log.e("MainActivity#onPurchasesUpdated", "purchases is null or empty");
        handler.post(() ->
            Toast
                .makeText(
                    MainActivity.this,
                    getString(R.string.error_occurred), Toast.LENGTH_LONG
                )
                .show()
        );
      }
      else {
        Log.i("MainActivity#onPurchasesUpdated", getResponseCodeString(responseCode));
        for(Purchase purchase : purchases) {
          checkPurchaseState(purchase);
        }
      }
    }
    else if(responseCode == BillingResponseCode.USER_CANCELED) {
      Log.w("MainActivity#onPurchasesUpdated", getResponseCodeString(responseCode));
      handler.post(() ->
          Toast
              .makeText(
                  MainActivity.this,
                  getString(R.string.cancel_to_buy), Toast.LENGTH_LONG
              )
              .show()
      );
    }
    else {
      Log.e("MainActivity#onPurchasesUpdated", getResponseCodeString(responseCode));
      handler.post(() ->
          Toast
              .makeText(
                  MainActivity.this,
                  getString(R.string.error_occurred), Toast.LENGTH_LONG
              )
              .show()
      );
    }
  }

  private void checkPurchaseState(Purchase purchase) {

    String purchaseState = handlePurchase(purchase);
    if("purchased".equals(purchaseState)) {
      Log.i(
        "MainActivity#checkPurchaseState",
        "Product: " + purchase.getProducts().get(0) + ", State: " + purchaseState
      );
      if(purchase.getProducts().contains(PRODUCT_ID_PREMIUM)) {
        Log.i(
            "MainActivity#checkPurchaseState",
            "Product: " + purchase.getProducts().get(0) + ", set IS_PREMIUM on"
        );
        handler.post(() -> {
          Toast
              .makeText(
                  this,
                  getString(R.string.succeed_to_upgrade),
                  Toast.LENGTH_LONG
              )
              .show();
          setBooleanGeneralInSharedPreferences(IS_PREMIUM, true);
          if(expandableListViewFragment != null) {
            expandableListViewFragment.disableAdView();
          }
        });
      }
    }
    else {
      Log.w(
        "MainActivity#checkPurchaseState",
        "Product: " + purchase.getProducts().get(0) + ", State: " + purchaseState
      );
    }
  }

  // 購入を承認する
  String handlePurchase(Purchase purchase) {

    String stateStr = "error";
    int purchaseState = purchase.getPurchaseState();
    if(purchaseState == Purchase.PurchaseState.PURCHASED) {
      stateStr = "purchased";
      // まだ購入が承認されていない場合は承認を行う
      if(!purchase.isAcknowledged()) {
        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
          .newBuilder()
          .setPurchaseToken(purchase.getPurchaseToken())
          .build();

        billingClient.acknowledgePurchase(
          acknowledgePurchaseParams,
          this
        );
      }
    }
    else if(purchaseState == Purchase.PurchaseState.PENDING) {
      stateStr = "pending";
    }
    else if(purchaseState == Purchase.PurchaseState.UNSPECIFIED_STATE) {
      stateStr = "unspecified";
    }

    return stateStr;
  }

  @Override
  public void onAcknowledgePurchaseResponse(BillingResult billingResult) {

    int responseCode = billingResult.getResponseCode();
    if(responseCode != BillingResponseCode.OK) {
      Log.e("MainActivity#onAcknowledgePurchaseResponse", getResponseCodeString(responseCode));
      handler.post(() ->
          Toast
              .makeText(
                  MainActivity.this,
                  getString(R.string.error_occurred), Toast.LENGTH_LONG
              )
              .show()
      );
    }
  }

  private void onBuyButtonClicked() {

    getProductDetails(PRODUCT_ID_PREMIUM).onSuccess((Continuation<ProductDetails, Void>)task -> {

      ProductDetails productDetails = task.getResult();
      ImmutableList<ProductDetailsParams> productDetailsParamsList = ImmutableList.of(
          ProductDetailsParams.newBuilder()
              .setProductDetails(productDetails)
              .build()
      );

      BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
          .setProductDetailsParamsList(productDetailsParamsList)
          .build();

      BillingResult billingResult =
        billingClient.launchBillingFlow(MainActivity.this, billingFlowParams);

      int responseCode = billingResult.getResponseCode();
      if(responseCode == BillingResponseCode.OK) {
        Log.i("MainActivity#onBuyButtonClicked", getResponseCodeString(responseCode));
      }
      else {
        Log.e("MainActivity#onBuyButtonClicked", getResponseCodeString(responseCode));
      }

      return null;
    });
  }

  @SuppressWarnings("SameParameterValue")
  private Task<ProductDetails> getProductDetails(@NonNull final String productId) {

    // continueWith()もcontinueWithTask()もクライアントの渡したContinuationインスタンス内で定義される
    // then()は非同期で処理されるが、continueWith()と異なりcontinueWithTask()はthen()内でさらなる
    // 非同期処理が行われることを前提としている。例えば、非同期処理を行うメソッドとして、引数に文字列を
    // 受け取り、特定の時間停止した後、受け取った文字列をそのまま返す処理を別スレッドで行うasyncTask()
    // を仮定すると、asyncTask("foo").continueWith()として、then()内で
    // return asyncTask(task.getResult() + "bar")し、さらに.continueWith()を続けると、
    // 前のthen()内の処理であるasyncTask()内でsetResult()が呼び出されていない(処理が完了していない)
    // 限り、そのcontinueWith()内のthen()ではtask.getResult()の値が不定となる。これはcontinueWith()
    // の実装において、単にユーザの渡したContinuationインスタンスのthen()内の処理が完了することが
    // 次の.continue*()メソッドの処理を開始する条件となっているためである。一方continueWithTask()では、
    // Continuationインスタンスのthen()内に非同期処理を行うメソッドが存在する場合、そのメソッドに
    // Task APIを実装することで、そのメソッドが返すTaskが完了することが次の.continue*()メソッド
    // の処理を開始する条件となっているので、非同期で処理されるthen()内にさらに非同期処理が存在して
    // いても処理結果の整合性を保つことができる。onSuccess()とonSuccessTask()も全く同じ関係であり、
    // continueWith()は、前のTaskが完了していればそのTaskがfailed, canceled, succeededのいずれ
    // であってもthen()内の処理を行うが、onSuccess()は、前のTaskがsucceededのときのみthen()
    // 内の処理を行う。
    final TaskCompletionSource<ProductDetails> taskCompletionSource = new TaskCompletionSource<>();
    if(productDetailsList == null) {
      queryProductDetailsList().onSuccess((Continuation<List<ProductDetails>, Void>)task -> {

        productDetailsList = task.getResult();
        getProductDetailsFromListAndSetTaskCompletionSource(
            productId,
            productDetailsList,
            taskCompletionSource
        );
        return null;
      });
    }
    else {
      getProductDetailsFromListAndSetTaskCompletionSource(
          productId,
          productDetailsList,
          taskCompletionSource
      );
    }

    return taskCompletionSource.getTask();
  }

  private void getProductDetailsFromListAndSetTaskCompletionSource(
    String productId,
    List<ProductDetails> productDetailsList,
    TaskCompletionSource<ProductDetails> taskCompletionSource
  ) {

    for(ProductDetails productDetails : productDetailsList) {
      if(productId.equals(productDetails.getProductId())) {
        Log.i(
            "MainActivity#getProductDetailsFromListAndSetTaskCompletionSource",
            productId + " found"
        );
        taskCompletionSource.setResult(productDetails);
        return;
      }
    }

    taskCompletionSource.setError(new IllegalStateException());
    Log.e(
        "MainActivity#getProductDetailsFromListAndSetTaskCompletionSource",
        productId + " not found"
    );
    handler.post(() ->
        Toast
            .makeText(
                this,
                getString(R.string.error_occurred),
                Toast.LENGTH_LONG
            )
            .show()
    );
  }

  private Task<List<ProductDetails>> queryProductDetailsList() {

    final TaskCompletionSource<List<ProductDetails>> taskCompletionSource =
      new TaskCompletionSource<>();

    QueryProductDetailsParams.Product product = QueryProductDetailsParams.Product.newBuilder()
        .setProductType(BillingClient.ProductType.INAPP)
        .setProductId(PRODUCT_ID_PREMIUM)
        .build();
    List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
    productList.add(product);

    QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
        .setProductList(productList)
        .build();

    billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {

      int responseCode = billingResult.getResponseCode();
      if(responseCode == BillingResponseCode.OK) {
        if(productDetailsList.isEmpty()) {
          Log.e("MainActivity#queryProductDetailsAsync", "productDetailsList is null or empty");
          taskCompletionSource.setError(new IllegalStateException());
          handler.post(() ->
              Toast
                  .makeText(
                      MainActivity.this,
                      getString(R.string.error_occurred),
                      Toast.LENGTH_LONG
                  )
                  .show()
          );
        }
        else {
          Log.i("MainActivity#queryProductDetailsAsync", getResponseCodeString(responseCode));
          taskCompletionSource.setResult(productDetailsList);
        }
      }
      else if(responseCode == BillingResponseCode.USER_CANCELED) {
        Log.w("MainActivity#querySkuDetailsList", getResponseCodeString(responseCode));
        taskCompletionSource.setCancelled();
        handler.post(() ->
            Toast
                .makeText(
                    MainActivity.this,
                    getString(R.string.cancel_to_buy),
                    Toast.LENGTH_LONG
                )
                .show()
        );
      }
      else {
        Log.e("MainActivity#querySkuDetailsList", getResponseCodeString(responseCode));
        taskCompletionSource.setError(new IllegalStateException());
        handler.post(() ->
            Toast
                .makeText(
                    MainActivity.this,
                    getString(R.string.error_occurred),
                    Toast.LENGTH_LONG
                )
                .show()
        );
      }
    });

    return taskCompletionSource.getTask();
  }

  private String getResponseCodeString(int responseCode) {

    switch(responseCode) {
      case BillingResponseCode.OK:
        return "OK";
      case BillingResponseCode.USER_CANCELED:
        return "USER_CANCELED";
      case BillingResponseCode.SERVICE_UNAVAILABLE:
        return "SERVICE_UNAVAILABLE";
      case BillingResponseCode.BILLING_UNAVAILABLE:
        return "BILLING_UNAVAILABLE";
      case BillingResponseCode.ITEM_UNAVAILABLE:
        return "ITEM_UNAVAILABLE";
      case BillingResponseCode.DEVELOPER_ERROR:
        return "DEVELOPER_ERROR";
      case BillingResponseCode.ERROR:
        return "ERROR";
      case BillingResponseCode.ITEM_ALREADY_OWNED:
        return "ITEM_ALREADY_OWNED";
      case BillingResponseCode.ITEM_NOT_OWNED:
        return "ITEM_NOT_OWNED";
      case BillingResponseCode.SERVICE_DISCONNECTED:
        return "SERVICE_DISCONNECTED";
      case BillingResponseCode.FEATURE_NOT_SUPPORTED:
        return "FEATURE_NOT_SUPPORTED";
      default:
        throw new IllegalArgumentException("Such a Response Code not exists!");
    }
  }

  @Override
  protected void onNewIntent(Intent intent) {

    super.onNewIntent(intent);

    // テキストがACTION_SENDで送られてきたときに、詳細の項目にそのテキストを
    // セットした状態で編集画面を表示する
    detail = null;
    if(Intent.ACTION_SEND.equals(intent.getAction())) {
      Bundle extras = intent.getExtras();
      if(extras != null) {
        detail = extras.getString(Intent.EXTRA_TEXT);
      }
    }

    if(detail == null) {
      if(BOOT_FROM_NOTIFICATION.equals(intent.getAction())) {

        isBootFromNotification = true;
        setIntGeneralInSharedPreferences(MENU_POSITION, 0);
        setIntGeneralInSharedPreferences(SUBMENU_POSITION, 0);
        int size = menu.size();
        for(int i = 1; i < size; i++) {
          MenuItem menuItem = menu.getItem(i);
          if(menuItem.hasSubMenu()) {
            SubMenu subMenu = menuItem.getSubMenu();
            int subSize = requireNonNull(subMenu).size();
            for(int j = 0; j < subSize; j++) {
              MenuItem subMenuItem = subMenu.getItem(j);
              subMenuItem.setChecked(false);
            }
          }
          menuItem.setChecked(false);
        }
      }
    }
    else {
      List<NonScheduledListAdapter> nonScheduledListList =
        generalSettings.getNonScheduledLists();
      int size = nonScheduledListList.size();
      String[] items = new String[size + 1];
      items[0] = requireNonNull(menu.findItem(R.id.scheduled_list).getTitle()).toString();
      for(int i = 0; i < size; i++) {
        items[i + 1] = nonScheduledListList.get(i).getTitle();
      }

      TextView customTitle = new TextView(this);
      int leftPadding = getPxFromDp(this, 20);
      int topPadding = getPxFromDp(this, 20);
      int rightPadding = getPxFromDp(this, 20);
      int bottomPadding = getPxFromDp(this, 0);
      customTitle.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
      customTitle.setTextSize(getPxFromDp(this, 6));
      customTitle.setText(R.string.action_send_booted_dialog_title);
      if(this.isDarkMode) {
        customTitle.setTextColor(
            ContextCompat.getColor(this, R.color.primaryTextMaterialDark)
        );
      }
      else {
        customTitle.setTextColor(ContextCompat.getColor(this, android.R.color.black));
      }
      customTitle.setGravity(Gravity.CENTER);

      final SingleChoiceItemsAdapter adapter = new SingleChoiceItemsAdapter(items);
      final AlertDialog dialog = new AlertDialog.Builder(this)
        .setCustomTitle(customTitle)
        .setSingleChoiceItems(adapter, 0, (dialog1, which) -> {})
        .setPositiveButton(R.string.determine, (dialog12, which) -> {

          whichList = SingleChoiceItemsAdapter.checkedPosition;

          setIntGeneralInSharedPreferences(MENU_POSITION, whichList);
          setIntGeneralInSharedPreferences(SUBMENU_POSITION, 0);

          int menuSize = menu.size();
          for(int i = 0; i < menuSize; i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setChecked(false);
            if(menuItem.hasSubMenu()) {
              SubMenu subMenu = menuItem.getSubMenu();
              requireNonNull(subMenu);
              int subSize = subMenu.size();
              for(int j = 0; j < subSize; j++) {
                MenuItem subMenuItem = subMenu.getItem(j);
                subMenuItem.setChecked(false);
              }
            }
          }

          showList();
          if(order == 0) {
            Calendar extractedCal = getCalendarFromString();
            if(extractedCal != null) {
              final AlertDialog useDateDialog = new AlertDialog.Builder(this)
                  .setTitle(R.string.date_and_time_detected_title)
                  .setMessage(R.string.date_and_time_detected_message)
                  .setPositiveButton(R.string.yes, (dialog15, which12) -> {
                    handler.post(() -> showMainEditFragment(extractedCal));
                    detail = null;
                  })
                  .setNegativeButton(R.string.no, (dialog1, which1) ->
                      handler.post(() -> showMainEditFragment(detail))
                  )
                  .setOnCancelListener(dialog1 ->
                      handler.post(() -> showMainEditFragment(detail))
                  )
                  .create();

              useDateDialog.setOnShowListener(dialogInterface -> {
                useDateDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(accentColor);
                useDateDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(accentColor);
              });

              useDateDialog.show();
            }
            else {
              handler.post(() -> showMainEditFragment(detail));
            }
          }
          else {
            handler.post(() -> showMainEditFragment(detail));
          }
        })
        .setNeutralButton(R.string.cancel, (dialog13, which) -> detail = null)
        .setOnCancelListener(dialog14 -> detail = null)
        .create();

      dialog.setOnShowListener(dialogInterface -> {

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(accentColor);
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(accentColor);
      });

      dialog.show();
    }

    showList();
  }

  private Calendar getCalendarFromString() {

    dateSet = false;
    String detailOrg = detail;
    Calendar now = Calendar.getInstance();
    now.set(Calendar.HOUR_OF_DAY, 0);
    now.set(Calendar.MINUTE, 0);
    now.set(Calendar.SECOND, 0);
    now.set(Calendar.MILLISECOND, 0);

    setMultiMatchedFieldsInCalender(
        now, new int[]{Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH},
        new String[]{"(\\d{4})[-/.](\\d{1,2})[-/.](\\d{1,2})"}
    );
    setMultiMatchedFieldsInCalender(
        now, new int[]{Calendar.MONTH, Calendar.DAY_OF_MONTH},
        new String[]{"(\\d{1,2})[-/.](\\d{1,2})"}
    );
    setMultiMatchedFieldsInCalender(
        now, new int[]{Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.YEAR},
        new String[]{"(\\d{1,2})[-/.](\\d{1,2})[-/.](\\d{4})"}
    );
    setMultiMatchedFieldsInCalender(
        now, new int[]{Calendar.YEAR, Calendar.MONTH},
        new String[]{"(\\d{4})[-/.](\\d{1,2})"}
    );
    setMultiMatchedFieldsInCalender(
        now, new int[]{Calendar.MONTH, Calendar.YEAR},
        new String[]{"(\\d{1,2})[-/.](\\d{4})"}
    );
    setMultiMatchedFieldsInCalender(
        now, new int[]{Calendar.HOUR_OF_DAY, Calendar.MINUTE}, new String[]{
            "(午[前後]) *(\\d{1,2}):(\\d{1,2})",
            "(am|a\\.m\\.|AM|A\\.M\\.|pm|p\\.m\\.|PM|P\\.M\\.) *(\\d{1,2}):(\\d{1,2})",
            "(\\d{1,2}):(\\d{1,2}) *(am|a\\.m\\.|AM|A\\.M\\.|pm|p\\.m\\.|PM|P\\.M\\.)",
            "(\\d{1,2}):(\\d{1,2})"
        }
    );
    setMatchedFieldInCalender(now, Calendar.YEAR, new String[]{"(\\d{4})年", "(\\d{4})"});
    setMatchedFieldInCalender(now, Calendar.MONTH, new String[]{
        "(\\d{1,2})月", "(Jan)", "(Feb)", "(Mar)", "(Apr)", "(May)", "(Jun)", "(Jul)", "(Aug)",
        "(Sep)", "(Oct)", "(Nov)", "(Dec)"
    });
    setMatchedFieldInCalender(now, Calendar.HOUR_OF_DAY, new String[]{
        "(午[前後]) *(\\d{1,2})時",
        "(am|a\\.m\\.|AM|A\\.M\\.|pm|p\\.m\\.|PM|P\\.M\\.) *(\\d{1,2})時", "(\\d{1,2})時",
        "(\\d{1,2}) *(am|a\\.m\\.|AM|A\\.M\\.|pm|p\\.m\\.|PM|P\\.M\\.)",
        "at *(\\d{1,2})", "(\\d{1,2}) *o'clock"
    });
    setMatchedFieldInCalender(now, Calendar.MINUTE, new String[]{
        "(\\d{1,2})分"
    });
    // DAY_OF_MONTHを最後の方に置いた理由は、"(\\d{1,2})"を、同じ正規表現を含む複合表現
    // ("(\\d{1,2})時"など)の一番後ろに置きたかったため(一番広くマッチするから)。
    setMatchedFieldInCalender(now, Calendar.DAY_OF_MONTH, new String[]{
        "(\\d{1,2})日", "(\\d{1,2})(st|nd|rd|th)", "(\\d{1,2})"
    });
    setMatchedFieldInCalender(now, Calendar.DAY_OF_WEEK, new String[]{
        "(日)", "(月)", "(火)", "(水)", "(木)", "(金)", "(土)",
        "(Sun)", "(Mon)", "(Tu)", "(Wed)", "(Th)", "(Fri)", "(Sat)"
    });

    detail = detailOrg;
    return dateSet? now : null;
  }

  private void setMultiMatchedFieldsInCalender(
      Calendar targetCal, int[] targetFields, String[] targetStrings
  ) {

    for(String targetString : targetStrings) {
      int[] fields = getMultiMatchedFieldsInCalender(
          targetString, ArrayUtils.contains(targetFields, Calendar.HOUR_OF_DAY)
      );
      if(!Arrays.equals(fields, new int[]{-1, -1, -1})) {
        for(int i = 0; i < targetFields.length; i++) {
          int monthOffset = targetFields[i] == Calendar.MONTH? 1 : 0;
          targetCal.set(targetFields[i], fields[i] - monthOffset);
          targetCal.getTime();
        }
        dateSet = true;
        return;
      }
    }
  }

  private int[] getMultiMatchedFieldsInCalender(String targetString, boolean isHour) {

    int[] fields = new int[]{-1, -1, -1};
    Pattern pattern = Pattern.compile(targetString);
    Matcher matcher = pattern.matcher(detail);
    if(matcher.find()) {
      int size = matcher.groupCount();
      int hourAdd = 0;
      int offset = 0;
      for(int i = 0; i < size; i++) {
        int j = i - offset;
        String group = requireNonNull(matcher.group(i + 1));
        if(isHour && Pattern.compile("[午mM]").matcher(group).find()) {
          if(Pattern.compile("[後pP]").matcher(group).find()) {
            hourAdd = 12;
          }
          offset = 1;
          continue;
        }
        fields[j] = Integer.parseInt(group);
        if(hourAdd != 0) {
          fields[j] += hourAdd;
          hourAdd = 0;
        }
      }
      if(hourAdd != 0) {
        fields[0] += hourAdd;
      }
      detail = detail.replace(requireNonNull(matcher.group()), "");
    }

    return fields;
  }

  private void setMatchedFieldInCalender(
      Calendar targetCal, int targetField, String[] targetStrings
  ) {

    for(String targetString : targetStrings) {
      int field = getMatchedFieldInCalender(
          targetString, targetField == Calendar.MONTH,
          targetField == Calendar.HOUR_OF_DAY
      );
      if(field != -1) {
        targetCal.set(targetField, field);
        targetCal.getTime();
        dateSet = true;
        return;
      }
    }
  }

  private int getMatchedFieldInCalender(String targetString, boolean isMonth, boolean isHour) {

    Integer field = -1;
    Pattern pattern = Pattern.compile(targetString);
    Matcher matcher = pattern.matcher(detail);
    if(matcher.find()) {
      String group = requireNonNull(matcher.group(1));
      field = DAY_OF_WEEK_MAP_JP.get(group);
      if(field == null) {
        field = DAY_OF_WEEK_MAP_EN.get(group);
      }
      if(field == null) {
        field = MONTH_MAP.get(group);
      }
      if(field == null) {
        int hourAdd = 0;
        int monthOffset = isMonth? 1 : 0;
        String targetGroup = group;
        if(isHour) {
          int size = matcher.groupCount();
          for(int i = 0; i < size; i++) {
            group = requireNonNull(matcher.group(i + 1));
            if(Pattern.compile("[午mM]").matcher(group).find()) {
              if(Pattern.compile("[後pP]").matcher(group).find()) {
                hourAdd = 12;
              }
            }
            else {
              targetGroup = group;
            }
          }
        }
        field = Integer.parseInt(targetGroup) + hourAdd - monthOffset;
      }
      detail = detail.replace(matcher.group(), "");
    }

    return field;
  }

  private void showList() {

    // 前回開いていたNavigationDrawer上のメニューをリストアする
    try {
      menuItem = menu.getItem(whichMenuOpen);
    }
    catch(IndexOutOfBoundsException e) {
      menuItem = menu.getItem(0);
    }
    if(menuItem.hasSubMenu()) {
      menuItem = requireNonNull(menuItem.getSubMenu()).getItem(whichSubmenuOpen);
    }

    // 選択状態のリストア
    menuItem.setChecked(true);
    navigationView.setCheckedItem(menuItem);

    createAndSetFragmentColor();

    if(order == 0) {
      if(isExpandableTodo) {
        showExpandableListViewFragment();
      }
      else {
        showDoneListViewFragment();
      }
    }
    else if(order == 1) {
      if(generalSettings.getNonScheduledLists().get(whichMenuOpen - 1).isTodo()) {
        showListViewFragment();
      }
      else {
        showDoneListViewFragment();
      }
    }
    else if(order == 3) {
      showManageListViewFragment();
    }
    else if(order == 4) {
      showGeneralSettingsFragment();
    }
    else if(order == 5) {
      showHelpAndFeedbackFragment();
    }
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {

    return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {

    if(drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
      drawerLayout.closeDrawer(GravityCompat.START);
    }
    else {
      super.onBackPressed();
    }
  }

  @Override
  public void onConfigurationChanged(@NonNull Configuration newConfig) {

    super.onConfigurationChanged(newConfig);
    drawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

    if(
        menuItem.getItemId() != R.id.add_list && menuItem.getItemId() != R.id.share &&
        menuItem.getItemId() != R.id.notice
    ) {

      // 選択されたメニューアイテム以外のチェックを外す
      if(!menuItem.isChecked()) {

        int size = menu.size();
        for(int i = 0; i < size; i++) {

          if(menu.getItem(i) == menuItem) {
            setIntGeneralInSharedPreferences(MENU_POSITION, i);
          }

          if(menu.getItem(i).hasSubMenu()) {
            SubMenu subMenu = menu.getItem(i).getSubMenu();
            int subSize = requireNonNull(subMenu).size();
            for(int j = 0; j < subSize; j++) {
              if(subMenu.getItem(j) == menuItem) {
                setIntGeneralInSharedPreferences(MENU_POSITION, i);
                setIntGeneralInSharedPreferences(SUBMENU_POSITION, j);
              }
              subMenu.getItem(j).setChecked(false);
            }
          }
          else {
            menu.getItem(i).setChecked(false);
          }
        }
        menuItem.setChecked(true);
        navigationView.setCheckedItem(menuItem);

        // 選択されたmenuItemに対応するフラグメントを表示
        this.menuItem = menuItem;
        createAndSetFragmentColor();
        switch(order) {
          case 0: {
            if(isExpandableTodo) {
              showExpandableListViewFragment();
            }
            else {
              showDoneListViewFragment();
            }
            break;
          }
          case 1: {
            if(generalSettings.getNonScheduledLists().get(whichMenuOpen - 1).isTodo()) {
              showListViewFragment();
            }
            else {
              showDoneListViewFragment();
            }
            break;
          }
          case 3: {
            showManageListViewFragment();
            break;
          }
          case 4: {
            showGeneralSettingsFragment();
            break;
          }
          case 5: {
            showHelpAndFeedbackFragment();
            break;
          }
        }
      }
      else {
        drawerLayout.closeDrawer(GravityCompat.START);
      }
    }
    else if(menuItem.getItemId() == R.id.add_list) {
      // ダイアログに表示するEditTextの設定
      LinearLayout linearLayout = new LinearLayout(MainActivity.this);
      linearLayout.setOrientation(LinearLayout.VERTICAL);
      final EditText editText = new EditText(MainActivity.this);
      setCursorDrawableColor(this, editText);
      editText.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(
        accentColor,
        PorterDuff.Mode.SRC_IN
      ));
      editText.setHint(R.string.list_hint);
      editText.setLayoutParams(new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
      ));
      linearLayout.addView(editText);
      int paddingPx = getPxFromDp(this, 20);
      linearLayout.setPadding(paddingPx, 0, paddingPx, 0);

      // 新しいリストの名前を設定するダイアログを表示
      final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
        .setTitle(R.string.add_list)
        .setView(linearLayout)
        .setPositiveButton(R.string.add, (dialog1, which) -> {

          // GeneralSettingsとManageListAdapterへの反映
          String name = editText.getText().toString();
          if(name.isEmpty()) {
            name = getString(R.string.default_list);
          }
          generalSettings.addNonScheduledList(0, new NonScheduledListAdapter(name));
          List<NonScheduledListAdapter> nonScheduledListList =
            generalSettings.getNonScheduledLists();
          int size = nonScheduledListList.size();
          for(int i = 0; i < size; i++) {
            nonScheduledListList.get(i).setOrder(i);
          }
          ManageListAdapter.nonScheduledLists =
            new ArrayList<>(nonScheduledListList);
          manageListAdapter.notifyDataSetChanged();

          // 一旦reminder_listグループ内のアイテムをすべて消してから元に戻すことで新しく追加したリストの順番を追加した順に並び替える

          // デフォルトアイテムのリストア
          menu.removeGroup(R.id.reminder_list);
          menu.add(R.id.reminder_list, R.id.scheduled_list, 0, R.string.nav_scheduled_item)
            .setIcon(R.drawable.ic_time)
            .setCheckable(true);
          menu.add(R.id.reminder_list, R.id.add_list, 2, R.string.add_list)
            .setIcon(R.drawable.ic_add_24dp)
            .setCheckable(false);

          // 新しく追加したリストのリストア
          for(NonScheduledListAdapter list : nonScheduledListList) {
            Drawable drawable =
              ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_my_list_24dp);
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
                ContextCompat.getColor(MainActivity.this, R.color.iconGray),
                PorterDuff.Mode.SRC_IN
              ));
            }
            menu.add(R.id.reminder_list, generateUniqueId(), 1, list.getTitle())
              .setIcon(drawable)
              .setCheckable(true);
          }

          if(order != 0) {
            setIntGeneralInSharedPreferences(
              MENU_POSITION,
              whichMenuOpen + 1
            );
            MainActivity.this.menuItem = menu.getItem(whichMenuOpen);
          }
          navigationView.setCheckedItem(MainActivity.this.menuItem);

          // データベースへの反映
          updateSettingsDB();
        })
        .setNeutralButton(R.string.cancel, (dialog12, which) -> {

        })
        .create();

      dialog.setOnShowListener(dialogInterface -> {

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(accentColor);
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(accentColor);
      });

      dialog.show();

      // ダイアログ表示時にソフトキーボードを自動で表示
      editText.setOnFocusChangeListener((v, hasFocus) -> {

        if(hasFocus) {
          Window dialogWindow = dialog.getWindow();
          requireNonNull(dialogWindow);

          dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
      });
      editText.requestFocus();
    }
    else if(menuItem.getItemId() == R.id.share) {
      String title = getString(R.string.app_promotion);
      String content = getString(R.string.app_url);

      Intent intent = new Intent()
        .setAction(Intent.ACTION_SEND)
        .setType("text/plain")
        .putExtra(Intent.EXTRA_SUBJECT, title)
        .putExtra(Intent.EXTRA_TEXT, content);

      startActivity(intent);
    }
    else if(menuItem.getItemId() == R.id.notice) {
      if(!readNotice) {
        setBooleanGeneralInSharedPreferences(READ_NOTICE, true);
        MenuItem noticeItem = menu.findItem(R.id.notice);
        noticeItem.setIcon(R.drawable.ic_email_read_24dp);
        badgeDrawable.setEnabled(false);
      }
      final AlertDialog noticeDialog = new AlertDialog.Builder(this)
          .setTitle(R.string.notice_title)
          .setMessage(R.string.notice_message)
          .setPositiveButton(R.string.ok, (dialog15, which12) -> {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
              Intent intent = new Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                  .setData(Uri.parse("package:" + getPackageName()));
              startActivity(intent);
            }
          })
          .setOnCancelListener(dialog1 -> {})
          .create();

      noticeDialog.setOnShowListener(dialogInterface -> {
        noticeDialog
            .getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(accentColor);
        noticeDialog
            .getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(accentColor);
      });

      noticeDialog.show();
    }

    return false;
  }

  public void updateListTask(
      final ItemAdapter snackBarItem,
      final int groupPosition,
      boolean unlockNotify,
      final boolean exceedsTimeLimit
  ) {

    boolean isUpdated = false;
    int groupsSize = MyExpandableListAdapter.groups.size();
    for(int groupCount = 0; groupCount < groupsSize; groupCount++) {

      int groupChanged = 0;
      List<ItemAdapter> itemList;
      try {
        itemList = MyExpandableListAdapter.children.get(groupCount);
      }
      catch(NullPointerException e) {
        continue;
      }

      int childrenSize = itemList.size();
      for(int childCount = 0; childCount < childrenSize; childCount++) {

        ItemAdapter item = itemList.get(childCount);
        Calendar now = Calendar.getInstance();
        Calendar tomorrow = (Calendar)now.clone();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        int specDay = item.getDate().get(Calendar.DAY_OF_MONTH);
        long subTime = item.getDate().getTimeInMillis() - now.getTimeInMillis();
        long subDay = subTime / (24 * HOUR);

        if(subTime < 0) {
          if(groupCount != 0) {
            MyExpandableListAdapter.children.get(0).add(item);
            groupChanged |= (1 << childCount);
          }
        }
        else if(subDay < 1 && specDay == now.get(Calendar.DAY_OF_MONTH)) {
          if(groupCount != 1) {
            MyExpandableListAdapter.children.get(1).add(item);
            groupChanged |= (1 << childCount);
          }
        }
        else if(subDay < 2 && specDay == tomorrow.get(Calendar.DAY_OF_MONTH)) {
          if(groupCount != 2) {
            MyExpandableListAdapter.children.get(2).add(item);
            groupChanged |= (1 << childCount);
          }
        }
        else if(subDay < 8) {
          if(groupCount != 3) {
            MyExpandableListAdapter.children.get(3).add(item);
            groupChanged |= (1 << childCount);
          }
        }
        else {
          if(groupCount != 4) {
            MyExpandableListAdapter.children.get(4).add(item);
            groupChanged |= (1 << childCount);
          }
        }
      }

      int removeCount = 0;
      int binaryLength = Integer.toBinaryString(groupChanged).length();
      for(int i = 0; i < binaryLength; i++) {
        if((groupChanged & (1 << i)) != 0) {
          MyExpandableListAdapter.children.get(groupCount).remove(i - removeCount);
          removeCount++;
          isUpdated = true;
        }
      }
    }

    if(isUpdated) {
      updateExpandableParentGroups(true);
    }

    if(!MyExpandableListAdapter.isLockBlockNotifyChange || unlockNotify) {
      expandableListAdapter.notifyDataSetChanged();
      MyExpandableListAdapter.isBlockNotifyChange = false;
      MyExpandableListAdapter.isLockBlockNotifyChange = false;

      if(timer != null) {
        if(runnable != null) {
          handler.removeCallbacks(runnable);
        }
        else {
          runnable = () -> {

            updateListTask(
                null, -1, false, false
            );
            runnable = null;
          };
        }
        handler.postDelayed(runnable, 2 * MINUTE);
      }
    }

    if(snackBarItem != null) {
      View parentView = findViewById(android.R.id.content);
      requireNonNull(parentView);
      Snackbar.make(parentView, getResources().getString(R.string.complete), Snackbar.LENGTH_LONG)
        .addCallback(new Snackbar.Callback() {
          @Override
          public void onShown(Snackbar sb) {

            super.onShown(sb);
          }

          @Override
          public void onDismissed(Snackbar transientBottomBar, int event) {

            super.onDismissed(transientBottomBar, event);
            MyExpandableListAdapter.panelLockId = 0;
          }
        })
        .setAction(R.string.undo, v -> {

          MyExpandableListAdapter.isLockBlockNotifyChange = true;
          MyExpandableListAdapter.isBlockNotifyChange = true;

          if(MyExpandableListAdapter.isClosed) {
            MyExpandableListAdapter.hasPanel = snackBarItem.getId();
            MyExpandableListAdapter.isClosed = false;
          }
          if(
              (snackBarItem.getDayRepeat().getWhichSet() != 0 ||
                  snackBarItem.getMinuteRepeat().getWhichSet() != 0) &&
              !exceedsTimeLimit
          ) {
            snackBarItem.setAlarmStopped(snackBarItem.isOrgIsAlarmStopped());
            snackBarItem.setAlteredTime(snackBarItem.getOrgAlteredTime());
            if((snackBarItem.getMinuteRepeat().getWhichSet() & 1) != 0) {
              snackBarItem
                .getMinuteRepeat()
                .setCount(snackBarItem.getMinuteRepeat().getOrgCount2());
            }
            else if((snackBarItem.getMinuteRepeat().getWhichSet() & (1 << 1)) != 0) {
              snackBarItem
                .getMinuteRepeat()
                .setDuration(snackBarItem.getMinuteRepeat().getOrgDuration2());
            }

            snackBarItem.getDate().setTimeInMillis(
                MyExpandableListAdapter.backupDate.getTimeInMillis()
            );

            MyExpandableListAdapter.children.get(groupPosition).sort(SCHEDULED_ITEM_COMPARATOR);
            expandableListAdapter.notifyDataSetChanged();

            deleteAlarm(snackBarItem);
            if(!snackBarItem.isAlarmStopped()) {
              setAlarm(snackBarItem);
            }
            updateDB(snackBarItem, MyDatabaseHelper.TODO_TABLE);
          }
          else {
            if(MyExpandableListAdapter.refilledWhichSetInfo == 1) {
              snackBarItem
                  .getMinuteRepeat()
                  .setCount(snackBarItem.getMinuteRepeat().getOrgCount2());
            }
            else if(MyExpandableListAdapter.refilledWhichSetInfo == 1 << 1) {
              snackBarItem
                  .getMinuteRepeat()
                  .setDuration(snackBarItem.getMinuteRepeat().getOrgDuration2());
            }

            if(MyExpandableListAdapter.deletedWhichSetInfo == 1) {
              snackBarItem.getMinuteRepeat().setWhichSet(1);
            }
            else if(MyExpandableListAdapter.deletedWhichSetInfo == 1 << 1) {
              snackBarItem.getMinuteRepeat().setWhichSet(1 << 1);
            }

            snackBarItem.getDate().setTimeInMillis(
                MyExpandableListAdapter.backupDate.getTimeInMillis()
            );

            MyExpandableListAdapter.children.get(groupPosition).add(snackBarItem);
            for(List<ItemAdapter> itemList : MyExpandableListAdapter.children) {
              itemList.sort(SCHEDULED_ITEM_COMPARATOR);
            }
            expandableListAdapter.notifyDataSetChanged();

            if(!snackBarItem.isAlarmStopped()) {
              setAlarm(snackBarItem);
            }
            insertDB(snackBarItem, MyDatabaseHelper.TODO_TABLE);
            deleteDB(snackBarItem, MyDatabaseHelper.DONE_TABLE);
          }

          updateListTask(
              null, -1, true, false
          );
        })
        .show();
    }
  }

  void updateExpandableParentGroups(boolean sort) {

    Calendar now = Calendar.getInstance();
    Calendar tomorrowCal = (Calendar)now.clone();
    tomorrowCal.add(Calendar.DAY_OF_MONTH, 1);
    CharSequence today;
    CharSequence tomorrow;
    if(LOCALE.equals(Locale.JAPAN)) {
      today = DateFormat.format(" - yyyy年M月d日(E)", now);
      tomorrow = DateFormat.format(" - yyyy年M月d日(E)", tomorrowCal);
    }
    else {
      today = DateFormat.format(" - E, MMM d, yyyy", now);
      tomorrow = DateFormat.format(" - E, MMM d, yyyy", tomorrowCal);
    }
    MyExpandableListAdapter.groups.set(1, getString(R.string.today) + today);
    MyExpandableListAdapter.groups.set(2, getString(R.string.tomorrow) + tomorrow);

    if(sort) {
      for(List<ItemAdapter> itemList : MyExpandableListAdapter.children) {
        itemList.sort(SCHEDULED_ITEM_COMPARATOR);
      }
    }
  }

  private class UpdateListTimerTask extends TimerTask {

    @Override
    public void run() {

      handler.post(() -> {

        int second = Calendar.getInstance().get(Calendar.SECOND);
        if(!MyExpandableListAdapter.isBlockNotifyChange && second == 0) {
          updateListTask(
              null, -1, false, false
          );
        }
      });
    }
  }

  public void setUpdateListTimerTask(boolean isSet) {

    if(timer != null && !isSet) {
      timer.cancel();
      timer = null;
    }
    else if(timer == null && isSet) {
      timer = new Timer();
      TimerTask timerTask = new UpdateListTimerTask();
      timer.schedule(timerTask, 0, 1000);
    }
  }

  public List<List<ItemAdapter>> getChildren(String table) {

    return getChildren(queryAllDB(table));
  }

  public List<List<ItemAdapter>> getChildren(List<ItemAdapter> itemList) {

    List<ItemAdapter> pastList = new ArrayList<>();
    List<ItemAdapter> todayList = new ArrayList<>();
    List<ItemAdapter> tomorrowList = new ArrayList<>();
    List<ItemAdapter> weekList = new ArrayList<>();
    List<ItemAdapter> futureList = new ArrayList<>();

    Calendar now = Calendar.getInstance();
    Calendar tomorrow = (Calendar)now.clone();
    tomorrow.add(Calendar.DAY_OF_MONTH, 1);

    for(ItemAdapter item : itemList) {

      if(item.getWhichListBelongs() == 0) {
        if(item.getNotifyInterval().getTime() == item.getNotifyInterval().getOrgTime()) {
          deleteAlarm(item);
        }
        if(!isAlarmSet(item) && !item.isAlarmStopped()) {
          setAlarm(item);
        }

        int specDay = item.getDate().get(Calendar.DAY_OF_MONTH);
        long subTime = item.getDate().getTimeInMillis() - now.getTimeInMillis();
        long subDay = subTime / (24 * HOUR);

        if(subTime < 0) {
          pastList.add(item);
        }
        else if(subDay < 1 && specDay == now.get(Calendar.DAY_OF_MONTH)) {
          todayList.add(item);
        }
        else if(subDay < 2 && specDay == tomorrow.get(Calendar.DAY_OF_MONTH)) {
          tomorrowList.add(item);
        }
        else if(subDay < 8) {
          weekList.add(item);
        }
        else {
          futureList.add(item);
        }
      }
    }

    List<List<ItemAdapter>> children = new ArrayList<>();
    children.add(pastList);
    children.add(todayList);
    children.add(tomorrowList);
    children.add(weekList);
    children.add(futureList);

    for(List<ItemAdapter> child : children) {
      child.sort(SCHEDULED_ITEM_COMPARATOR);
    }
    return children;
  }

  public void addChildren(ItemAdapter item, String table) {

    Calendar now = Calendar.getInstance();
    Calendar tomorrow = (Calendar)now.clone();
    tomorrow.add(Calendar.DAY_OF_MONTH, 1);

    int specDay = item.getDate().get(Calendar.DAY_OF_MONTH);
    long subTime = item.getDate().getTimeInMillis() - now.getTimeInMillis();
    long subDay = subTime / (24 * HOUR);

    if(subTime < 0) {
      MyExpandableListAdapter.children.get(0).add(item);
    }
    else if(subDay < 1 && specDay == now.get(Calendar.DAY_OF_MONTH)) {
      MyExpandableListAdapter.children.get(1).add(item);
    }
    else if(subDay < 2 && specDay == tomorrow.get(Calendar.DAY_OF_MONTH)) {
      MyExpandableListAdapter.children.get(2).add(item);
    }
    else if(subDay < 8) {
      MyExpandableListAdapter.children.get(3).add(item);
    }
    else {
      MyExpandableListAdapter.children.get(4).add(item);
    }

    for(List<ItemAdapter> itemList : MyExpandableListAdapter.children) {
      itemList.sort(SCHEDULED_ITEM_COMPARATOR);
    }
    expandableListAdapter.notifyDataSetChanged();
    insertDB(item, table);
  }

  public void setUpdatedItemPosition(long id) {

    List<List<ItemAdapter>> children = MyExpandableListAdapter.children;
    int count = 0;
    int iSize = MyExpandableListAdapter.groups.size();
    for(int i = 0; i < iSize && id != 0; i++) {
      if(MyExpandableListAdapter.displayGroups[i]) {
        List<ItemAdapter> itemList = children.get(i);
        int jSize = itemList.size();
        for(int j = 0; j < jSize; j++) {
          ItemAdapter item = itemList.get(j);
          if(item.getId() == id) {
            try {
              ExpandableListViewFragment.position =
                expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(
                  count,
                  j
                ));
              ExpandableListViewFragment.offset = ExpandableListViewFragment.groupHeight;
            }
            catch(NullPointerException e) {
              ExpandableListViewFragment.position = 0;
              ExpandableListViewFragment.offset = 0;
            }
            id = 0;
            break;
          }
        }

        count++;
      }
    }
  }

  public List<ItemAdapter> getNonScheduledItem(String table) {

    if(whichMenuOpen > 0) {
      long listId = generalSettings.getNonScheduledLists().get(whichMenuOpen - 1).getId();
      Log.i("MainActivity#getNonScheduledItem", "listId: " + listId);
      List<ItemAdapter> itemList = new ArrayList<>();
      for(ItemAdapter item : queryAllDB(table)) {
        Log.i(
            "MainActivity#getNonScheduledItem",
            "item.getWhichListBelongs(): " + item.getWhichListBelongs()
        );
        if(item.getWhichListBelongs() == listId) {
          itemList.add(item);
        }
      }
      itemList.sort(NON_SCHEDULED_ITEM_COMPARATOR);

      return itemList;
    }
    else {
      return new ArrayList<>();
    }
  }

  public List<ItemAdapter> getDoneItem() {

    long listId = whichMenuOpen == 0 ? 0 : generalSettings.getNonScheduledLists().get(
      whichMenuOpen - 1).getId();
    List<ItemAdapter> itemList = new ArrayList<>();
    for(ItemAdapter item : queryAllDB(MyDatabaseHelper.DONE_TABLE)) {
      if(item.getWhichListBelongs() == listId) {
        itemList.add(item);
      }
    }
    itemList.sort(DONE_ITEM_COMPARATOR);

    return itemList;
  }

  private void initSettings() {

    generalSettings = new GeneralSettingsAdapter();

    // データベースを新たに作成する場合、基本的な一般設定を追加しておく

    // タグのデフォルト設定
    // タグなし
    TagAdapter tag = new TagAdapter(0);
    tag.setName(getString(R.string.none));
    tag.setOrder(0);
    generalSettings.addTag(tag);

    // 家事タグ
    tag = new TagAdapter(1);
    tag.setName(getString(R.string.home));
    tag.setPrimaryColor(Color.parseColor("#4caf50"));
    tag.setPrimaryLightColor(Color.parseColor("#80e27e"));
    tag.setPrimaryDarkColor(Color.parseColor("#087f23"));
    tag.setPrimaryTextColor(Color.parseColor("#000000"));
    tag.setColorOrderGroup(9);
    tag.setColorOrderChild(5);
    tag.setOrder(1);
    generalSettings.addTag(tag);

    // 仕事タグ
    tag = new TagAdapter(2);
    tag.setName(getString(R.string.work));
    tag.setPrimaryColor(Color.parseColor("#2196f3"));
    tag.setPrimaryLightColor(Color.parseColor("#6ec6ff"));
    tag.setPrimaryDarkColor(Color.parseColor("#0069c0"));
    tag.setPrimaryTextColor(Color.parseColor("#000000"));
    tag.setColorOrderGroup(5);
    tag.setColorOrderChild(5);
    tag.setOrder(2);
    generalSettings.addTag(tag);

    // ショッピングタグ
    tag = new TagAdapter(3);
    tag.setName(getString(R.string.shopping));
    tag.setPrimaryColor(Color.parseColor("#f44336"));
    tag.setPrimaryLightColor(Color.parseColor("#ff7961"));
    tag.setPrimaryDarkColor(Color.parseColor("#ba000d"));
    tag.setPrimaryTextColor(Color.parseColor("#000000"));
    tag.setColorOrderGroup(0);
    tag.setColorOrderChild(5);
    tag.setOrder(3);
    generalSettings.addTag(tag);


    // Itemのデフォルト設定
    ItemAdapter item = generalSettings.getItem();

    // NotifyInterval
    NotifyIntervalAdapter notifyInterval = new NotifyIntervalAdapter();
    notifyInterval.setHour(0);
    notifyInterval.setMinute(5);
    notifyInterval.setOrgTime(6);
    notifyInterval.setWhichSet(1);

    if(notifyInterval.getOrgTime() != 0) {
      String summary;
      if(LOCALE.equals(Locale.JAPAN)) {
        summary = getString(R.string.unless_complete_task);
        if(notifyInterval.getHour() != 0) {
          summary += getResources().getQuantityString(
            R.plurals.hour,
            notifyInterval.getHour(),
            notifyInterval.getHour()
          );
        }
        if(notifyInterval.getMinute() != 0) {
          summary += getResources().getQuantityString(
            R.plurals.minute,
            notifyInterval.getMinute(),
            notifyInterval.getMinute()
          );
        }
        summary += getString(R.string.per);
        if(notifyInterval.getOrgTime() == -1) {
          summary += getString(R.string.infinite_times_notify);
        }
        else {
          summary +=
            getResources().getQuantityString(R.plurals.times_notify, notifyInterval.getOrgTime(),
              notifyInterval.getOrgTime()
            );
        }
      }
      else {
        summary = "Notify every ";
        if(notifyInterval.getHour() != 0) {
          summary += getResources().getQuantityString(
            R.plurals.hour,
            notifyInterval.getHour(),
            notifyInterval.getHour()
          );
          if(!LOCALE.equals(Locale.JAPAN)) {
            summary += " ";
          }
        }
        if(notifyInterval.getMinute() != 0) {
          summary += getResources().getQuantityString(
            R.plurals.minute,
            notifyInterval.getMinute(),
            notifyInterval.getMinute()
          );
          if(!LOCALE.equals(Locale.JAPAN)) {
            summary += " ";
          }
        }
        if(notifyInterval.getOrgTime() != -1) {
          summary +=
            getResources().getQuantityString(R.plurals.times_notify, notifyInterval.getOrgTime(),
              notifyInterval.getOrgTime()
            ) + " ";
        }
        summary += getString(R.string.unless_complete_task);
      }

      notifyInterval.setLabel(summary);
    }
    else {
      notifyInterval.setLabel(getString(R.string.none));
    }
    item.setNotifyInterval(notifyInterval);

    // AlarmSound
    item.setSoundUri(DEFAULT_URI_SOUND.toString());

    insertSettingsDB();
  }

  // 受け取ったオブジェクトをシリアライズしてデータベースへ挿入
  public void insertDB(ItemAdapter item, String table) {

    accessor.executeInsert(item.getId(), serialize(item.getItem()), table);
  }

  public void updateDB(ItemAdapter item, String table) {

    accessor.executeUpdate(item.getId(), serialize(item.getItem()), table);
  }

  public void deleteDB(ItemAdapter item, String table) {

    accessor.executeDelete(item.getId(), table);
  }

  // 指定されたテーブルからオブジェクトのバイト列をすべて取り出し、デシリアライズしてオブジェクトのリストで返す。
  public List<ItemAdapter> queryAllDB(String table) {

    List<ItemAdapter> itemList = new ArrayList<>();

    List<byte[]> streamList = null;
    do {
      try {
        streamList = accessor.executeQueryAll(table);
      }
      catch(SQLiteCantOpenDatabaseException e) {
        try {
          //noinspection BusyWait
          Thread.sleep(10);
        }
        catch(InterruptedException ex) {
          Log.e("MainActivity#queryAllDB", Log.getStackTraceString(ex));
        }
      }
    }
    while(getIsDirectBootContext(this) && streamList == null);

    if(streamList != null) {
      for(byte[] stream : streamList) {
        itemList.add(new ItemAdapter(deserialize(stream)));
      }
    }

    return itemList;
  }

  public boolean isItemExists(ItemAdapter item, String table) {

    return accessor.executeQueryById(item.getId(), table) != null;
  }

  public void insertSettingsDB() {

    accessor.executeInsert(
      1,
      serialize(generalSettings.getGeneralSettings()),
      MyDatabaseHelper.SETTINGS_TABLE
    );
  }

  public void updateSettingsDB() {

    accessor.executeUpdate(
      1,
      serialize(generalSettings.getGeneralSettings()),
      MyDatabaseHelper.SETTINGS_TABLE
    );
  }

  public GeneralSettingsAdapter querySettingsDB() {

    Object generalSettings = null;
    do {
      try {
        generalSettings = deserialize(
          accessor.executeQueryById(1, MyDatabaseHelper.SETTINGS_TABLE)
        );
      }
      catch(SQLiteCantOpenDatabaseException e) {
        try {
          //noinspection BusyWait
          Thread.sleep(10);
        }
        catch(InterruptedException ex) {
          Log.e("MainActivity#querySettingsDB", Log.getStackTraceString(ex));
        }
      }
    }
    while(getIsDirectBootContext(this) && generalSettings == null);

    return generalSettings == null? null : new GeneralSettingsAdapter(generalSettings);
  }

  @SuppressWarnings("MethodParameterNamingConvention")
  public void setIntGeneralInSharedPreferences(String TAG, int value) {

    switch(TAG) {
      case DEFAULT_MINUS_TIME_1_HOUR: {
        minusTime1Hour = value;
        break;
      }
      case DEFAULT_MINUS_TIME_1_MINUTE: {
        minusTime1Minute = value;
        break;
      }
      case DEFAULT_MINUS_TIME_2_HOUR: {
        minusTime2Hour = value;
        break;
      }
      case DEFAULT_MINUS_TIME_2_MINUTE: {
        minusTime2Minute = value;
        break;
      }
      case DEFAULT_MINUS_TIME_3_HOUR: {
        minusTime3Hour = value;
        break;
      }
      case DEFAULT_MINUS_TIME_3_MINUTE: {
        minusTime3Minute = value;
        break;
      }
      case DEFAULT_PLUS_TIME_1_HOUR: {
        plusTime1Hour = value;
        break;
      }
      case DEFAULT_PLUS_TIME_1_MINUTE: {
        plusTime1Minute = value;
        break;
      }
      case DEFAULT_PLUS_TIME_2_HOUR: {
        plusTime2Hour = value;
        break;
      }
      case DEFAULT_PLUS_TIME_2_MINUTE: {
        plusTime2Minute = value;
        break;
      }
      case DEFAULT_PLUS_TIME_3_HOUR: {
        plusTime3Hour = value;
        break;
      }
      case DEFAULT_PLUS_TIME_3_MINUTE: {
        plusTime3Minute = value;
        break;
      }
      case DEFAULT_TEXT_SIZE: {
        whichTextSize = value;
        textSize = 18 + 2 * value;
        break;
      }
      case SNOOZE_DEFAULT_HOUR: {
        snoozeDefaultHour = value;
        break;
      }
      case SNOOZE_DEFAULT_MINUTE: {
        snoozeDefaultMinute = value;
        break;
      }
      case MENU_POSITION: {
        whichMenuOpen = value;
        break;
      }
      case SUBMENU_POSITION: {
        whichSubmenuOpen = value;
        break;
      }
      default: {
        throw new IllegalArgumentException(TAG);
      }
    }

    getDynamicContext(this)
      .getSharedPreferences(
        getIsDirectBootContext(this) ? INT_GENERAL_COPY : INT_GENERAL,
        Context.MODE_PRIVATE
      )
      .edit()
      .putInt(TAG, value)
      .apply();
  }

  @SuppressWarnings("MethodParameterNamingConvention")
  public void setBooleanGeneralInSharedPreferences(String TAG, boolean value) {

    switch(TAG) {
      case READ_NOTICE: {
        readNotice = value;
        break;
      }
      case IS_COPIED_FROM_OLD_VERSION: {
        isCopiedFromOldVersion = value;
        break;
      }
      case IS_QUERIED_PURCHASE_HISTORY: {
        isQueriedPurchaseHistory = value;
        break;
      }
      case IS_RECREATED: {
        isRecreated = value;
        break;
      }
      case IS_RECREATED_TWICE: {
        isRecreatedTwice = value;
        break;
      }
      case IS_DARK_MODE: {
        isDarkMode = value;
        break;
      }
      case IS_DARK_THEME_FOLLOW_SYSTEM: {
        isDarkThemeFollowSystem = value;
        break;
      }
      case PLAY_SLIDE_ANIMATION: {
        isPlaySlideAnimation = value;
        break;
      }
      case IS_EXPANDABLE_TODO: {
        isExpandableTodo = value;
        break;
      }
      case IS_PREMIUM: {
        isPremium = value;
        break;
      }
      default: {
        if(TAG.equals(IS_FIRST_USE)) {
          isFirstUse = value;
          break;
        }
        else {
          throw new IllegalArgumentException(TAG);
        }
      }
    }

    getDynamicContext(this)
      .getSharedPreferences(
        getIsDirectBootContext(this) ? BOOLEAN_GENERAL_COPY : BOOLEAN_GENERAL,
        Context.MODE_PRIVATE
      )
      .edit()
      .putBoolean(TAG, value)
      .apply();
  }

  public void setAlarm(ItemAdapter item) {

    if(
      item.getDate().getTimeInMillis() > System.currentTimeMillis() &&
        item.getWhichListBelongs() == 0
    ) {
      item.getNotifyInterval().setTime(item.getNotifyInterval().getOrgTime());
      Intent intent = new Intent(this, AlarmReceiver.class);
      byte[] obArray = serialize(item.getItem());
      intent.putExtra(ITEM, obArray);
      PendingIntent sender = PendingIntent.getBroadcast(
        this, (int)item.getId(), intent,
        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
      );

      AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
      requireNonNull(alarmManager);

      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if(alarmManager.canScheduleExactAlarms()) {
          alarmManager.setAlarmClock(
              new AlarmManager.AlarmClockInfo(
                  item.getDate().getTimeInMillis(), null
              ), sender
          );
        }
      }
      else {
        alarmManager.setAlarmClock(
            new AlarmManager.AlarmClockInfo(
                item.getDate().getTimeInMillis(), null
            ), sender
        );
      }
    }
  }

  public void deleteAlarm(ItemAdapter item) {

    if(isAlarmSet(item)) {
      Intent intent = new Intent(this, AlarmReceiver.class);
      PendingIntent sender = PendingIntent.getBroadcast(
        this, (int)item.getId(), intent,
        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
      );

      AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
      requireNonNull(alarmManager);

      alarmManager.cancel(sender);
      sender.cancel();
    }
  }

  public boolean isAlarmSet(ItemAdapter item) {

    Intent intent = new Intent(this, AlarmReceiver.class);
    PendingIntent sender = PendingIntent.getBroadcast(
      this, (int)item.getId(), intent,
      PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_NO_CREATE
    );

    return sender != null;
  }

  String getControlTimeText(boolean isMinus, int which) {

    String controlTimeText = "";
    int hour;
    int minute;
    switch(which) {
      case 1: {
        hour = isMinus ? minusTime1Hour : plusTime1Hour;
        minute = isMinus ? minusTime1Minute : plusTime1Minute;
        break;
      }
      case 2: {
        hour = isMinus ? minusTime2Hour : plusTime2Hour;
        minute = isMinus ? minusTime2Minute : plusTime2Minute;
        break;
      }
      case 3: {
        hour = isMinus ? minusTime3Hour : plusTime3Hour;
        minute = isMinus ? minusTime3Minute : plusTime3Minute;
        break;
      }
      default: {
        throw new IllegalStateException("Such a control num not exists! : " + which);
      }
    }
    if(hour != 0) {
      controlTimeText += hour + getString(R.string.control_time_hour);
    }
    if(minute != 0) {
      controlTimeText += minute + getString(R.string.control_time_minute);
    }
    if(hour == 0 && minute == 0) {
      controlTimeText = isMinus ? getString(R.string.minus1d) : getString(R.string.plus1d);
    }
    else {
      controlTimeText = isMinus ? "-" + controlTimeText : "+" + controlTimeText;
    }

    return controlTimeText;
  }

  void clearAllNotification() {

    NotificationManager manager =
      (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    requireNonNull(manager);
    manager.cancelAll();

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      List<NotificationChannel> channelList = manager.getNotificationChannels();
      if(channelList != null) {
        int size = channelList.size();
        for(int i = 0; i < size; i++) {
          manager.deleteNotificationChannel(channelList.get(i).getId());
        }
      }
    }
  }

  private void createAndSetFragmentColor() {

    // 開いているFragmentに応じた色を作成
    order = menuItem.getOrder();
    if(order == 1) {
      NonScheduledListAdapter list = generalSettings.getNonScheduledLists().get(whichMenuOpen - 1);
      if(list.getColor() == 0) {
        setDefaultColor();
      }
      else {
        menuItemColor = list.getTextColor();
        menuBackgroundColor = list.getColor();
        statusBarColor = list.getDarkColor();
        list.setIsColorPrimary(false);
        if(list.getColor() == 0) {
          accentColor = ContextCompat.getColor(this, R.color.colorAccent);
          secondaryTextColor = ContextCompat.getColor(this, android.R.color.black);
        }
        else {
          accentColor = list.getColor();
          secondaryTextColor = list.getTextColor();
        }
        list.setIsColorPrimary(true);
      }
    }
    else {
      setDefaultColor();
    }

    if(isDarkMode) {
      primaryMaterialDarkColor =
        ContextCompat.getColor(this, R.color.primaryMaterialDark);
      primaryDarkMaterialDarkColor =
        ContextCompat.getColor(this, R.color.primaryDarkMaterialDark);
      backgroundMaterialDarkColor =
        ContextCompat.getColor(this, R.color.backgroundMaterialDark);
      backgroundFloatingMaterialDarkColor =
        ContextCompat.getColor(this, R.color.backgroundFloatingMaterialDark);
      primaryTextMaterialDarkColor =
        ContextCompat.getColor(this, R.color.primaryTextMaterialDark);
      secondaryTextMaterialDarkColor =
        ContextCompat.getColor(this, R.color.secondaryTextMaterialDark);
      menuItemColor = primaryTextMaterialDarkColor;
      menuBackgroundColor = primaryMaterialDarkColor;
      statusBarColor = primaryDarkMaterialDarkColor;
    }

    // ハンバーガーアイコンの色を指定
    drawerToggle.getDrawerArrowDrawable().setColor(menuItemColor);
    // DisplayHomeAsUpEnabledに指定する戻るボタンの色を指定
    upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
    requireNonNull(upArrow);
    upArrow.setColorFilter(new PorterDuffColorFilter(
      menuItemColor,
      PorterDuff.Mode.SRC_IN
    ));
    // ツールバーとステータスバーの色を指定
    toolbar.setTitleTextColor(menuItemColor);
    toolbar.setBackgroundColor(menuBackgroundColor);
    Window window = getWindow();
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
    window.setStatusBarColor(statusBarColor);
  }

  private void setDefaultColor() {

    MyThemeAdapter theme = generalSettings.getTheme();
    if(theme.getColor() == 0) {
      menuItemColor = Color.WHITE;
      menuBackgroundColor = ContextCompat.getColor(this, R.color.colorPrimary);
      statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
    }
    else {
      menuItemColor = theme.getTextColor();
      menuBackgroundColor = theme.getColor();
      statusBarColor = theme.getDarkColor();
    }
    theme.setIsColorPrimary(false);
    if(theme.getColor() == 0) {
      accentColor = ContextCompat.getColor(this, R.color.colorAccent);
      secondaryTextColor = ContextCompat.getColor(this, android.R.color.black);
    }
    else {
      accentColor = theme.getColor();
      secondaryTextColor = theme.getTextColor();
    }
    theme.setIsColorPrimary(true);
  }

  public void showDoneListViewFragment() {

    actionBarFragment = ActionBarFragment.newInstance();
    showFragment(DoneListViewFragment.TAG, DoneListViewFragment.newInstance(),
      ActionBarFragment.TAG, actionBarFragment, false
    );
  }

  public void showTagEditListViewFragment() {

    showFragment(TagEditListViewFragment.TAG, TagEditListViewFragment.newInstance(),
      null, null, true
    );
  }

  public void showColorPickerListViewFragment() {

    showFragment(ColorPickerListViewFragment.TAG, ColorPickerListViewFragment.newInstance(),
      null, null, true
    );
  }

  public void showManageListViewFragment() {

    actionBarFragment = ActionBarFragment.newInstance();
    showFragment(ManageListViewFragment.TAG, ManageListViewFragment.newInstance(),
      ActionBarFragment.TAG, actionBarFragment, false
    );
  }

  public void showListViewFragment() {

    actionBarFragment = ActionBarFragment.newInstance();
    showFragment(ListViewFragment.TAG, ListViewFragment.newInstance(),
      ActionBarFragment.TAG, actionBarFragment, false
    );
  }

  public void showExpandableListViewFragment() {

    actionBarFragment = ActionBarFragment.newInstance();
    expandableListViewFragment = ExpandableListViewFragment.newInstance();
    showFragment(ExpandableListViewFragment.TAG, expandableListViewFragment,
      ActionBarFragment.TAG, actionBarFragment, false
    );
  }

  // 編集画面を表示(引数にitemを渡すとそのitemの情報が入力された状態で表示)
  public void showMainEditFragment() {

    MainEditFragment.isMainPopping = false;
    showFragment(MainEditFragment.TAG, MainEditFragment.newInstance(),
      null, null, true
    );
  }

  public void showMainEditFragment(Calendar cal) {

    MainEditFragment.isMainPopping = false;
    showFragment(MainEditFragment.TAG, MainEditFragment.newInstance(cal),
        null, null, true
    );
  }

  public void showMainEditFragment(String detail) {

    MainEditFragment.isMainPopping = false;
    showFragment(MainEditFragment.TAG, MainEditFragment.newInstance(detail),
      null, null, true
    );
    this.detail = null;
  }

  public void showMainEditFragment(ItemAdapter item) {

    MainEditFragment.isMainPopping = false;
    showFragment(MainEditFragment.TAG, MainEditFragment.newInstance(item.clone()),
      null, null, true
    );
  }

  public void showMainEditFragmentForList() {

    MainEditFragment.isMainPopping = false;
    showFragment(MainEditFragment.TAG, MainEditFragment.newInstanceForList(),
      null, null, true
    );
  }

  public void showMainEditFragmentForList(NonScheduledListAdapter list) {

    MainEditFragment.isMainPopping = false;
    showFragment(MainEditFragment.TAG, MainEditFragment.newInstanceForList(list.clone()),
      null, null, true
    );
  }

  public void showNotesFragment(ItemAdapter item) {

    Fragment nextFragment;
    String nextFragmentTAG;
    if(item.isChecklistMode()) {
      nextFragment = NotesChecklistModeFragment.newInstance(item);
      nextFragmentTAG = NotesChecklistModeFragment.TAG;
    }
    else {
      nextFragment = NotesEditModeFragment.newInstance(item);
      nextFragmentTAG = NotesEditModeFragment.TAG;
    }

    MainEditFragment.isNotesPopping = false;
    showFragment(nextFragmentTAG, nextFragment, null, null, true);
  }

  public void showGeneralSettingsFragment() {

    showFragment(GeneralSettingsFragment.TAG, GeneralSettingsFragment.newInstance(),
      null, null, false
    );
  }

  public void showHelpAndFeedbackFragment() {

    showFragment(HelpAndFeedbackFragment.TAG, HelpAndFeedbackFragment.newInstance(),
      null, null, false
    );
  }

  public void showAboutThisAppFragment() {

    showFragment(AboutThisAppFragment.TAG, AboutThisAppFragment.newInstance(),
      null, null, true
    );
  }

  private void commitFragment(
    Fragment remove1, Fragment remove2, String add1, Fragment addFragment1,
    String add2, Fragment addFragment2, boolean backStack
  ) {

    Transition transition = new Fade()
      .setDuration(300);
    FragmentManager manager = getSupportFragmentManager();

    FragmentTransaction transaction = manager.beginTransaction();
    if(remove1 != null) {
      remove1.setExitTransition(transition);
      transaction.remove(remove1);
    }
    if(remove2 != null) {
      remove2.setExitTransition(transition);
      transaction.remove(remove2);
    }
    if(add1 != null) {
      addFragment1.setEnterTransition(transition);
      transaction.add(R.id.content, addFragment1, add1);
    }
    if(add2 != null) {
      addFragment2.setEnterTransition(transition);
      transaction.add(R.id.content, addFragment2, add2);
    }
    if(backStack) {
      transaction.addToBackStack(null);
    }
    transaction.commit();
  }

  private void showFragment(
    String add1,
    Fragment addFragment1,
    String add2,
    Fragment addFragment2,
    boolean backStack
  ) {

    FragmentManager manager = getSupportFragmentManager();
    Fragment rmFragment = manager.findFragmentById(R.id.content);

    if(rmFragment == null) {
      commitFragment(null, null, add1, addFragment1, add2, addFragment2, backStack);
    }
    else if(rmFragment instanceof ActionBarFragment) {

      String[] mainTAGs = {
        ExpandableListViewFragment.TAG, ListViewFragment.TAG,
        ManageListViewFragment.TAG, DoneListViewFragment.TAG
      };
      boolean isFoundMatchFragment = false;
      for(String mainTAG : mainTAGs) {
        Fragment mainFragment = manager.findFragmentByTag(mainTAG);
        if(mainFragment != null && mainFragment.isVisible()) {
          isFoundMatchFragment = true;
          commitFragment(
            mainFragment,
            rmFragment,
            add1,
            addFragment1,
            add2,
            addFragment2,
            backStack
          );
          break;
        }
      }
      if(!isFoundMatchFragment) {
        commitFragment(rmFragment, null, add1, addFragment1, add2, addFragment2, backStack);
      }
    }
    else if(rmFragment instanceof ExpandableListViewFragment ||
      rmFragment instanceof ListViewFragment
      || rmFragment instanceof ManageListViewFragment ||
      rmFragment instanceof DoneListViewFragment) {

      commitFragment(rmFragment, manager.findFragmentByTag(ActionBarFragment.TAG),
        add1, addFragment1, add2, addFragment2, backStack
      );
    }
    else {
      commitFragment(rmFragment, null, add1, addFragment1, add2, addFragment2, backStack);
    }
  }
}