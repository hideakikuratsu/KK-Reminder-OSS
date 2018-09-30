package com.example.hideaki.reminder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.hideaki.reminder.UtilClass.LINE_SEPARATOR;
import static com.example.hideaki.reminder.UtilClass.NON_SCHEDULED_ITEM_COMPARATOR;
import static com.example.hideaki.reminder.UtilClass.SCHEDULED_ITEM_COMPARATOR;

public class MyExpandableListAdapter extends BaseExpandableListAdapter implements Filterable {

  private static Calendar tmp;
  private static boolean[] display_groups = new boolean[5];
  static List<String> groups = new ArrayList<>();
  static List<List<Item>> children;
  static long has_panel; //コントロールパネルがvisibleであるItemのid値を保持する
  private boolean is_control_panel_locked;
  private MainActivity activity;
  ActionMode actionMode = null;
  static int checked_item_num;
  private static boolean manually_checked;
  private List<List<Item>> filteredList;
  ColorStateList colorStateList;

  MyExpandableListAdapter(List<List<Item>> children, MainActivity activity) {

    MyExpandableListAdapter.children = children;

    //groupsの初期化
    groups.clear();
    Calendar now = Calendar.getInstance();
    Calendar tomorrow_cal = (Calendar)now.clone();
    tomorrow_cal.add(Calendar.DAY_OF_MONTH, 1);
    CharSequence today = DateFormat.format(" - yyyy年M月d日(E)", now);
    CharSequence tomorrow = DateFormat.format(" - yyyy年M月d日(E)", tomorrow_cal);
    groups.add(activity.getString(R.string.past));
    groups.add(activity.getString(R.string.today) + today);
    groups.add(activity.getString(R.string.tomorrow) + tomorrow);
    groups.add(activity.getString(R.string.this_week));
    groups.add(activity.getString(R.string.future));

    this.activity = activity;
  }

  private static class ChildViewHolder {

    CardView child_card;
    ImageView clock_image;
    TextView time;
    TextView detail;
    TextView repeat;
    CheckBox checkBox;
    ImageView tagPallet;
    TableLayout control_panel;
  }

  private class MyOnClickListener implements View.OnClickListener, CompoundButton.OnCheckedChangeListener,
      View.OnLongClickListener, ActionMode.Callback {

    private int group_position;
    private int child_position;
    private Item item;
    private View convertView;
    private ChildViewHolder viewHolder;
    private int which_list;
    private List<Item> itemListToMove;

    private MyOnClickListener(int group_position, int child_position, Item item, View convertView,
                             ChildViewHolder viewHolder) {

      this.group_position = group_position;
      this.child_position = child_position;
      this.item = item;
      this.convertView = convertView;
      this.viewHolder = viewHolder;
    }

    @Override
    public void onClick(View v) {

      boolean is_database_updated = false;
      activity.actionBarFragment.searchView.clearFocus();
      switch(v.getId()) {
        case R.id.child_card: {

          if(actionMode == null) {
            if(viewHolder.control_panel.getVisibility() == View.GONE) {
              if(!is_control_panel_locked) {
                has_panel = item.getId();
                viewHolder.control_panel.setVisibility(View.VISIBLE);
                notifyDataSetChanged();
              }
            }
            else {
              has_panel = 0;
              viewHolder.control_panel.setVisibility(View.GONE);
            }
          }
          else if(viewHolder.checkBox.isChecked()) {
            viewHolder.checkBox.setChecked(false);
          }
          else viewHolder.checkBox.setChecked(true);
          break;
        }
        case R.id.clock_image: {

          if(actionMode == null) {
            if(item.getTime_altered() == 0 && activity.isAlarmSetted(item)) {
              item.setAlarm_stopped(true);
              activity.deleteAlarm(item);
            }
            else if(item.getTime_altered() == 0 && !item.isAlarm_stopped()) {
              item.setAlarm_stopped(true);
            }
            else if(item.getTime_altered() == 0 && item.isAlarm_stopped()) {
              item.setAlarm_stopped(false);
              activity.setAlarm(item);
            }
            else if(item.getTime_altered() != 0) {
              item.setDate((Calendar)item.getOrg_date().clone());
              item.setTime_altered(0);
              activity.deleteAlarm(item);
              activity.setAlarm(item);
            }

            activity.updateDB(item, MyDatabaseHelper.TODO_TABLE);
            is_database_updated = true;

            displayDate(viewHolder, item);
          }
          else if(viewHolder.checkBox.isChecked()) {
            viewHolder.checkBox.setChecked(false);
          }
          else viewHolder.checkBox.setChecked(true);
          break;
        }
        case R.id.m5m: {
          if(item.getDate().getTimeInMillis() > System.currentTimeMillis() + 5 * 60 * 1000) {
            if(item.getTime_altered() == 0) {
              item.setOrg_date((Calendar)item.getDate().clone());
            }
            item.getDate().setTimeInMillis(item.getDate().getTimeInMillis() + -5 * 60 * 1000);

            item.addTime_altered(-5 * 60 * 1000);
            if(item.isAlarm_stopped()) item.setAlarm_stopped(false);

            activity.deleteAlarm(item);
            activity.setAlarm(item);
            activity.updateDB(item, MyDatabaseHelper.TODO_TABLE);
            is_database_updated = true;

            displayDate(viewHolder, item);
          }
          break;
        }
        case R.id.m1h: {
          if(item.getDate().getTimeInMillis() > System.currentTimeMillis() + 60 * 60 * 1000) {
            if(item.getTime_altered() == 0) {
              item.setOrg_date((Calendar)item.getDate().clone());
            }
            item.getDate().setTimeInMillis(item.getDate().getTimeInMillis() + -1 * 60 * 60 * 1000);

            item.addTime_altered(-1 * 60 * 60 * 1000);
            if(item.isAlarm_stopped()) item.setAlarm_stopped(false);

            activity.deleteAlarm(item);
            activity.setAlarm(item);
            activity.updateDB(item, MyDatabaseHelper.TODO_TABLE);
            is_database_updated = true;

            displayDate(viewHolder, item);
          }
          break;
        }
        case R.id.m1d: {
          if(item.getDate().getTimeInMillis() > System.currentTimeMillis() + 24 * 60 * 60 * 1000) {
            if(item.getTime_altered() == 0) {
              item.setOrg_date((Calendar)item.getDate().clone());
            }
            item.getDate().setTimeInMillis(item.getDate().getTimeInMillis() + -24 * 60 * 60 * 1000);

            item.addTime_altered(-24 * 60 * 60 * 1000);
            if(item.isAlarm_stopped()) item.setAlarm_stopped(false);

            activity.deleteAlarm(item);
            activity.setAlarm(item);
            activity.updateDB(item, MyDatabaseHelper.TODO_TABLE);
            is_database_updated = true;

            displayDate(viewHolder, item);
          }
          break;
        }
        case R.id.edit: {
          activity.expandableListView.clearTextFilter();
          activity.showMainEditFragment(item, ExpandableListViewFragment.TAG);
          has_panel = 0;
          viewHolder.control_panel.setVisibility(View.GONE);
          break;
        }
        case R.id.p5m: {
          if(item.getTime_altered() == 0) {
            item.setOrg_date((Calendar)item.getDate().clone());
          }

          if(item.getDate().getTimeInMillis() < System.currentTimeMillis()) {
            item.getDate().setTimeInMillis(System.currentTimeMillis() + 5 * 60 * 1000);
          }
          else {
            item.getDate().setTimeInMillis(item.getDate().getTimeInMillis() + 5 * 60 * 1000);
          }

          item.addTime_altered(5 * 60 * 1000);
          if(item.isAlarm_stopped()) item.setAlarm_stopped(false);

          activity.deleteAlarm(item);
          activity.setAlarm(item);
          activity.updateDB(item, MyDatabaseHelper.TODO_TABLE);
          is_database_updated = true;

          displayDate(viewHolder, item);
          break;
        }
        case R.id.p1h: {
          if(item.getTime_altered() == 0) {
            item.setOrg_date((Calendar)item.getDate().clone());
          }

          if(item.getDate().getTimeInMillis() < System.currentTimeMillis()) {
            item.getDate().setTimeInMillis(System.currentTimeMillis() + 60 * 60 * 1000);
          }
          else {
            item.getDate().setTimeInMillis(item.getDate().getTimeInMillis() + 60 * 60 * 1000);
          }

          item.addTime_altered(60 * 60 * 1000);
          if(item.isAlarm_stopped()) item.setAlarm_stopped(false);

          activity.deleteAlarm(item);
          activity.setAlarm(item);
          activity.updateDB(item, MyDatabaseHelper.TODO_TABLE);
          is_database_updated = true;

          displayDate(viewHolder, item);
          break;
        }
        case R.id.p1d: {
          if(item.getTime_altered() == 0) {
            item.setOrg_date((Calendar)item.getDate().clone());
          }

          if(item.getDate().getTimeInMillis() < System.currentTimeMillis()) {
            item.getDate().setTimeInMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
          }
          else {
            item.getDate().setTimeInMillis(item.getDate().getTimeInMillis() + 24 * 60 * 60 * 1000);
          }

          item.addTime_altered(24 * 60 * 60 * 1000);
          if(item.isAlarm_stopped()) item.setAlarm_stopped(false);

          activity.deleteAlarm(item);
          activity.setAlarm(item);
          activity.updateDB(item, MyDatabaseHelper.TODO_TABLE);
          is_database_updated = true;

          displayDate(viewHolder, item);
          break;
        }
        case R.id.notes: {
          activity.expandableListView.clearTextFilter();
          activity.showNotesFragment(item, ExpandableListViewFragment.TAG);
          break;
        }
      }

      if(is_database_updated) {

        //データベースを更新したら、そのデータベースを端末暗号化ストレージへコピーする
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
          Context direct_boot_context = activity.createDeviceProtectedStorageContext();
          direct_boot_context.moveDatabaseFrom(activity, MyDatabaseHelper.TODO_TABLE);
        }
      }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

      if(isChecked && actionMode == null && manually_checked) {

        viewHolder.checkBox.jumpDrawablesToCurrentState();
        activity.actionBarFragment.searchView.clearFocus();
        if(item.getTime_altered() == 0) {
          item.setOrg_date((Calendar)item.getDate().clone());
        }
        else {
          item.setDate((Calendar)item.getOrg_date().clone());
        }

        if((item.getMinuteRepeat().getWhich_setted() & 1) != 0
            && item.getMinuteRepeat().getCount() == 0) {

          item.setDate((Calendar)item.getOrg_date2().clone());
        }
        else if((item.getMinuteRepeat().getWhich_setted() & (1 << 1)) != 0
            && item.getMinuteRepeat().getInterval() > item.getMinuteRepeat().getDuration()) {

          item.setDate((Calendar)item.getOrg_date2().clone());
        }

        boolean in_minute_repeat = false;
        if((item.getMinuteRepeat().getWhich_setted() & 1) != 0) {
          item.getMinuteRepeat().setOrg_count2(item.getMinuteRepeat().getCount());
        }
        else if((item.getMinuteRepeat().getWhich_setted() & (1 << 1)) != 0) {
          item.getMinuteRepeat().setOrg_duration2(item.getMinuteRepeat().getDuration());
        }

        Calendar now;
        int day_of_week;
        int month;

        if((item.getMinuteRepeat().getWhich_setted() & 1) != 0 && item.getMinuteRepeat().getCount() != 0) {

          //countリピート設定時
          in_minute_repeat = true;
          now = Calendar.getInstance();

          if(item.getMinuteRepeat().getCount() == item.getMinuteRepeat().getOrg_count()) {
            item.setOrg_date2((Calendar)item.getOrg_date().clone());
          }

          if(item.getDate().getTimeInMillis() > now.getTimeInMillis()) {
            tmp = (Calendar)item.getDate().clone();
          }
          else {
            tmp = (Calendar)now.clone();
          }
          tmp.set(Calendar.HOUR_OF_DAY, tmp.get(Calendar.HOUR_OF_DAY) + item.getMinuteRepeat().getHour());
          tmp.add(Calendar.MINUTE, item.getMinuteRepeat().getMinute());

          item.setOrg_alarm_stopped(item.isAlarm_stopped());
          item.setOrg_time_altered(item.getTime_altered());
          item.setTime_altered(0);
          item.setAlarm_stopped(false);
          item.getMinuteRepeat().setCount(item.getMinuteRepeat().getCount() - 1);
        }
        else if((item.getMinuteRepeat().getWhich_setted() & (1 << 1)) != 0
            && item.getMinuteRepeat().getInterval() <= item.getMinuteRepeat().getDuration()) {

          //durationリピート設定時
          in_minute_repeat = true;
          now = Calendar.getInstance();

          if(item.getMinuteRepeat().getDuration() == item.getMinuteRepeat().getOrgDuration()) {
            item.setOrg_date2((Calendar)item.getOrg_date().clone());
          }

          if(item.getDate().getTimeInMillis() > now.getTimeInMillis()) {
            tmp = (Calendar)item.getDate().clone();
          }
          else {
            tmp = (Calendar)now.clone();
          }
          tmp.set(Calendar.HOUR_OF_DAY, tmp.get(Calendar.HOUR_OF_DAY) + item.getMinuteRepeat().getHour());
          tmp.add(Calendar.MINUTE, item.getMinuteRepeat().getMinute());
          item.setOrg_alarm_stopped(item.isAlarm_stopped());
          item.setOrg_time_altered(item.getTime_altered());
          item.setTime_altered(0);
          item.setAlarm_stopped(false);
          item.getMinuteRepeat().setDuration(
              item.getMinuteRepeat().getDuration() - item.getMinuteRepeat().getInterval()
          );
        }
        else if((item.getDayRepeat().getSetted() & 1) != 0) {

          //Dayリピート設定時
          now = Calendar.getInstance();

          if(item.getDate().getTimeInMillis() > now.getTimeInMillis()) {
            tmp = (Calendar)item.getDate().clone();
            tmp.add(Calendar.DAY_OF_MONTH, item.getDayRepeat().getInterval());
          }
          else {
            tmp = (Calendar)now.clone();
            tmp.set(Calendar.HOUR_OF_DAY, item.getDate().get(Calendar.HOUR_OF_DAY));
            tmp.set(Calendar.MINUTE, item.getDate().get(Calendar.MINUTE));
            tmp.set(Calendar.SECOND, 0);

            if(tmp.before(now)) tmp.add(Calendar.DAY_OF_MONTH, item.getDayRepeat().getInterval());
          }
        }
        else if((item.getDayRepeat().getSetted() & (1 << 1)) != 0) {

          //Weekリピート設定時
          now = Calendar.getInstance();
          int day_of_week_last;
          
          if(item.getDate().getTimeInMillis() > now.getTimeInMillis()) {
            tmp = (Calendar)item.getDate().clone();
            day_of_week = item.getDate().get(Calendar.DAY_OF_WEEK) < 2 ?
                item.getDate().get(Calendar.DAY_OF_WEEK) + 5 : item.getDate().get(Calendar.DAY_OF_WEEK) - 2;

            //intervalの処理
            day_of_week_last = Integer.toBinaryString(item.getDayRepeat().getWeek()).length() - 1;
            if(day_of_week >= day_of_week_last) {
              tmp.add(Calendar.DAY_OF_MONTH, (item.getDayRepeat().getInterval() - 1) * 7);
            }

            int i = 1;
            while(i < 7 - day_of_week + 1) {
              if((item.getDayRepeat().getWeek() & (1 << (day_of_week + i))) != 0) {
                tmp.add(Calendar.DAY_OF_MONTH, i);

                break;
              }
              i++;

              if(i >= 7 - day_of_week + 1) {
                i = 0;
                tmp.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                if(day_of_week != 6) {
                  tmp.add(Calendar.DAY_OF_MONTH, 7);
                }
                day_of_week = 0;
              }
            }
          }
          else {
            tmp = (Calendar)now.clone();
            tmp.set(Calendar.HOUR_OF_DAY, item.getDate().get(Calendar.HOUR_OF_DAY));
            tmp.set(Calendar.MINUTE, item.getDate().get(Calendar.MINUTE));
            tmp.set(Calendar.SECOND, 0);
            day_of_week = now.get(Calendar.DAY_OF_WEEK) < 2 ?
                now.get(Calendar.DAY_OF_WEEK) + 5 : now.get(Calendar.DAY_OF_WEEK) - 2;

            //intervalの処理
            day_of_week_last = Integer.toBinaryString(item.getDayRepeat().getWeek()).length() - 1;
            if(day_of_week > day_of_week_last) {
              tmp.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
              if(tmp.after(now)) {
                tmp.add(Calendar.DAY_OF_MONTH, -7);
              }
              tmp.add(Calendar.DAY_OF_MONTH, (item.getDayRepeat().getInterval()) * 7);
              day_of_week = 0;
            }
            else if(day_of_week == day_of_week_last) {
              if(tmp.before(now)) {
                tmp.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                if(tmp.after(now)) {
                  tmp.add(Calendar.DAY_OF_MONTH, -7);
                }
                tmp.add(Calendar.DAY_OF_MONTH, (item.getDayRepeat().getInterval()) * 7);
                day_of_week = 0;
              }
            }

            int i = 0;
            while(i < 7 - day_of_week) {
              if((item.getDayRepeat().getWeek() & (1 << (day_of_week + i))) != 0) {
                tmp.add(Calendar.DAY_OF_MONTH, i);

                if(tmp.after(now)) {
                  break;
                }
              }
              i++;

              if(i >= 7 - day_of_week) {
                i = 0;
                tmp.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                if(tmp.after(now)) {
                  tmp.add(Calendar.DAY_OF_MONTH, -7);
                }
                tmp.add(Calendar.DAY_OF_MONTH, (item.getDayRepeat().getInterval()) * 7);
                day_of_week = 0;
              }
            }
          }
        }
        else if((item.getDayRepeat().getSetted() & (1 << 2)) != 0) {

          //Monthリピート設定時
          now = Calendar.getInstance();

          if(item.getDayRepeat().isDays_of_month_setted()) {

            //DaysOfMonthリピート設定時
            int day_of_month;
            int day_of_month_last;
            
            if(item.getDate().getTimeInMillis() > now.getTimeInMillis()) {
              tmp = (Calendar)item.getDate().clone();
              day_of_month = item.getDate().get(Calendar.DAY_OF_MONTH);

              //intervalの処理
              day_of_month_last = Integer.toBinaryString(item.getDayRepeat().getDays_of_month()).length();
              if(day_of_month_last > tmp.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                day_of_month_last = tmp.getActualMaximum(Calendar.DAY_OF_MONTH);
              }
              if(tmp.get(Calendar.DAY_OF_MONTH) >= day_of_month_last) {
                tmp.set(Calendar.DAY_OF_MONTH, 1);
                tmp.add(Calendar.MONTH, item.getDayRepeat().getInterval());
                day_of_month = 1;
              }

              int i = 0;
              while(i < 31 - day_of_month + 1) {
                if((item.getDayRepeat().getDays_of_month() & (1 << (day_of_month - 1 + i))) != 0) {
                  if((day_of_month - 1 + i) >= tmp.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    tmp.set(Calendar.DAY_OF_MONTH, tmp.getActualMaximum(Calendar.DAY_OF_MONTH));
                  }
                  else {
                    tmp.add(Calendar.DAY_OF_MONTH, i);
                  }

                  if(tmp.after(item.getDate())) {
                    break;
                  }
                }
                i++;

                if(i >= 31 - day_of_month + 1) {
                  i = 0;
                  tmp.set(Calendar.DAY_OF_MONTH, 1);
                  tmp.add(Calendar.MONTH, item.getDayRepeat().getInterval());
                  day_of_month = 1;
                }
              }
            }
            else {
              tmp = (Calendar)now.clone();
              tmp.set(Calendar.HOUR_OF_DAY, item.getDate().get(Calendar.HOUR_OF_DAY));
              tmp.set(Calendar.MINUTE, item.getDate().get(Calendar.MINUTE));
              tmp.set(Calendar.SECOND, 0);
              day_of_month = now.get(Calendar.DAY_OF_MONTH);

              //intervalの処理
              day_of_month_last = Integer.toBinaryString(item.getDayRepeat().getDays_of_month()).length();
              if(day_of_month_last > tmp.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                day_of_month_last = tmp.getActualMaximum(Calendar.DAY_OF_MONTH);
              }
              if(tmp.get(Calendar.DAY_OF_MONTH) > day_of_month_last) {
                tmp.set(Calendar.DAY_OF_MONTH, 1);
                tmp.add(Calendar.MONTH, item.getDayRepeat().getInterval());
                day_of_month = 1;
              }
              else if(tmp.get(Calendar.DAY_OF_MONTH) == day_of_month_last) {
                if(tmp.before(now)) {
                  tmp.set(Calendar.DAY_OF_MONTH, 1);
                  tmp.add(Calendar.MONTH, item.getDayRepeat().getInterval());
                  day_of_month = 1;
                }
              }

              int i = 0;
              while(i < 31 - day_of_month + 1) {
                if((item.getDayRepeat().getDays_of_month() & (1 << (day_of_month - 1 + i))) != 0) {
                  if((day_of_month - 1 + i) >= tmp.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                    tmp.set(Calendar.DAY_OF_MONTH, tmp.getActualMaximum(Calendar.DAY_OF_MONTH));
                  }
                  else {
                    tmp.add(Calendar.DAY_OF_MONTH, i);
                  }

                  if(tmp.after(now)) {
                    break;
                  }
                }
                i++;

                if(i >= 31 - day_of_month + 1) {
                  i = 0;
                  tmp.set(Calendar.DAY_OF_MONTH, 1);
                  tmp.add(Calendar.MONTH, item.getDayRepeat().getInterval());
                  day_of_month = 1;
                }
              }
            }
          }
          else {

            //OnTheMonthリピート設定時
            boolean match_to_ordinal_num;
            Calendar tmp2;
            Calendar tmp3;
            now = Calendar.getInstance();
            if(item.getDayRepeat().getOn_the_month().ordinal() < 6) {
              day_of_week = item.getDayRepeat().getOn_the_month().ordinal() + 2;
            }
            else if(item.getDayRepeat().getOn_the_month().ordinal() == 6) {
              day_of_week = 1;
            }
            else {
              day_of_week = item.getDayRepeat().getOn_the_month().ordinal() + 1;
            }

            if(item.getDate().getTimeInMillis() > now.getTimeInMillis()) {
              //clone()で渡して不具合が出る場合はsetTimeInMillis()を使う
              tmp = (Calendar)item.getDate().clone();

              if(day_of_week < 8) {
                month = tmp.get(Calendar.MONTH);
                tmp2 = (Calendar)tmp.clone();
                tmp2.set(Calendar.DAY_OF_WEEK, day_of_week);
                tmp3 = (Calendar)tmp2.clone();
                tmp2.add(Calendar.MONTH, 1);
                tmp3.add(Calendar.MONTH, -1);
                if(tmp2.get(Calendar.MONTH) == month) {
                  tmp.add(Calendar.DAY_OF_MONTH, 7);
                }
                else if(tmp3.get(Calendar.MONTH) == month) {
                  tmp.add(Calendar.DAY_OF_MONTH, -7);
                }

                tmp.set(Calendar.DAY_OF_WEEK, day_of_week);

                while(true) {

                  //intervalの処理
                  if(tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) > item.getDayRepeat().getOrdinal_number()) {
                    tmp.set(Calendar.DAY_OF_MONTH, 1);
                    tmp.add(Calendar.MONTH, item.getDayRepeat().getInterval());
                    tmp2 = (Calendar)tmp.clone();

                    tmp.set(Calendar.DAY_OF_WEEK, day_of_week);
                    if(tmp.before(tmp2)) {
                      tmp.add(Calendar.DAY_OF_MONTH, 7);
                    }
                  }

                  while(tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) < item.getDayRepeat().getOrdinal_number()
                      && tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) < tmp.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH)) {
                    tmp.add(Calendar.DAY_OF_MONTH, 7);
                  }

                  if(tmp.after(item.getDate())) break;
                  else {
                    tmp.set(Calendar.DAY_OF_MONTH, 1);
                    tmp.add(Calendar.MONTH, item.getDayRepeat().getInterval());
                    tmp2 = (Calendar)tmp.clone();

                    tmp.set(Calendar.DAY_OF_WEEK, day_of_week);
                    if(tmp.before(tmp2)) {
                      tmp.add(Calendar.DAY_OF_MONTH, 7);
                    }
                  }
                }
              }
              else if(day_of_week == 8) {
                month = tmp.get(Calendar.MONTH);
                tmp2 = (Calendar)tmp.clone();
                tmp2.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                tmp3 = (Calendar)tmp2.clone();
                tmp2.add(Calendar.MONTH, 1);
                tmp3.add(Calendar.MONTH, -1);
                if(tmp2.get(Calendar.MONTH) == month) {
                  tmp.add(Calendar.DAY_OF_MONTH, 7);
                }
                else if(tmp3.get(Calendar.MONTH) == month) {
                  tmp.add(Calendar.DAY_OF_MONTH, -7);
                }

                while(true) {
                  tmp.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

                  //intervalの処理
                  if(tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) > item.getDayRepeat().getOrdinal_number()) {
                    tmp.set(Calendar.DAY_OF_MONTH, 1);
                    tmp.add(Calendar.MONTH, item.getDayRepeat().getInterval());
                    tmp2 = (Calendar)tmp.clone();

                    tmp.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    if(tmp.before(tmp2)) {
                      tmp.add(Calendar.DAY_OF_MONTH, 7);
                    }

                    month = tmp.get(Calendar.MONTH);
                  }

                  while(tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) < item.getDayRepeat().getOrdinal_number()
                      && tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) < tmp.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH)) {
                    tmp.add(Calendar.DAY_OF_MONTH, 7);
                  }

                  tmp.add(Calendar.DAY_OF_MONTH, item.getDayRepeat().getWeekday_num());
                  item.getDayRepeat().setWeekday_num(item.getDayRepeat().getWeekday_num() + 1);

                  if(tmp.after(item.getDate()) && month == tmp.get(Calendar.MONTH)) {
                    item.getDayRepeat().setWeekday_num(0);
                    break;
                  }
                  else if(item.getDayRepeat().getWeekday_num() > 4 || month != tmp.get(Calendar.MONTH)) {
                    tmp.add(Calendar.DAY_OF_MONTH, -item.getDayRepeat().getWeekday_num() + 1);
                    tmp.set(Calendar.DAY_OF_MONTH, 1);
                    tmp.add(Calendar.MONTH, item.getDayRepeat().getInterval());
                    tmp2 = (Calendar)tmp.clone();

                    tmp.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    if(tmp.before(tmp2)) {
                      tmp.add(Calendar.DAY_OF_MONTH, 7);
                    }

                    month = tmp.get(Calendar.MONTH);
                    item.getDayRepeat().setWeekday_num(0);
                  }
                }
              }
              else if(day_of_week == 9) {
                month = tmp.get(Calendar.MONTH);
                tmp2 = (Calendar)tmp.clone();
                tmp2.add(Calendar.DAY_OF_MONTH, 1);

                match_to_ordinal_num = false;
                if(item.getDayRepeat().getOrdinal_number() == 5) {
                  if(tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) == tmp.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH)) {
                    match_to_ordinal_num = true;
                  }
                }
                else {
                  if(tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) == item.getDayRepeat().getOrdinal_number()) {
                    match_to_ordinal_num = true;
                  }
                }

                if(match_to_ordinal_num && tmp.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                    && tmp2.get(Calendar.MONTH) == month) {
                  tmp.add(Calendar.DAY_OF_MONTH, 1);
                }
                else {
                  month = tmp.get(Calendar.MONTH);
                  tmp2 = (Calendar)tmp.clone();
                  tmp2.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                  tmp3 = (Calendar)tmp2.clone();
                  tmp2.add(Calendar.MONTH, 1);
                  tmp3.add(Calendar.MONTH, -1);
                  if(tmp2.get(Calendar.MONTH) == month) {
                    tmp.add(Calendar.DAY_OF_MONTH, 7);
                  }
                  else if(tmp3.get(Calendar.MONTH) == month) {
                    tmp.add(Calendar.DAY_OF_MONTH, -7);
                  }

                  tmp.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

                  while(true) {

                    //intervalの処理
                    if(tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) > item.getDayRepeat().getOrdinal_number()) {
                      tmp.set(Calendar.DAY_OF_MONTH, 1);
                      tmp.add(Calendar.MONTH, item.getDayRepeat().getInterval());
                      tmp2 = (Calendar)tmp.clone();

                      tmp.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                      if(tmp.before(tmp2)) {
                        tmp.add(Calendar.DAY_OF_MONTH, 7);
                      }
                    }

                    while(tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) < item.getDayRepeat().getOrdinal_number()
                        && tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) < tmp.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH)) {
                      tmp.add(Calendar.DAY_OF_MONTH, 7);
                    }

                    if(tmp.after(item.getDate())) break;
                    else {
                      tmp.set(Calendar.DAY_OF_MONTH, 1);
                      tmp.add(Calendar.MONTH, item.getDayRepeat().getInterval());
                      tmp2 = (Calendar)tmp.clone();

                      tmp.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                      if(tmp.before(tmp2)) {
                        tmp.add(Calendar.DAY_OF_MONTH, 7);
                      }
                    }
                  }
                }
              }
            }
            else {
              tmp = (Calendar)now.clone();
              tmp.set(Calendar.HOUR_OF_DAY, item.getDate().get(Calendar.HOUR_OF_DAY));
              tmp.set(Calendar.MINUTE, item.getDate().get(Calendar.MINUTE));
              tmp.set(Calendar.SECOND, 0);

              if(day_of_week < 8) {
                month = tmp.get(Calendar.MONTH);
                tmp2 = (Calendar)tmp.clone();
                tmp2.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                tmp3 = (Calendar)tmp2.clone();
                tmp2.add(Calendar.MONTH, 1);
                tmp3.add(Calendar.MONTH, -1);
                if(tmp2.get(Calendar.MONTH) == month) {
                  tmp.add(Calendar.DAY_OF_MONTH, 7);
                }
                else if(tmp3.get(Calendar.MONTH) == month) {
                  tmp.add(Calendar.DAY_OF_MONTH, -7);
                }

                tmp.set(Calendar.DAY_OF_WEEK, day_of_week);

                while(true) {

                  //intervalの処理
                  if(tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) > item.getDayRepeat().getOrdinal_number()) {
                    tmp.set(Calendar.DAY_OF_MONTH, 1);
                    tmp.add(Calendar.MONTH, item.getDayRepeat().getInterval());
                    tmp2 = (Calendar)tmp.clone();

                    tmp.set(Calendar.DAY_OF_WEEK, day_of_week);
                    if(tmp.before(tmp2)) {
                      tmp.add(Calendar.DAY_OF_MONTH, 7);
                    }
                  }

                  while(tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) < item.getDayRepeat().getOrdinal_number()
                      && tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) < tmp.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH)) {
                    tmp.add(Calendar.DAY_OF_MONTH, 7);
                  }

                  if(tmp.after(now)) break;
                  else {
                    tmp.set(Calendar.DAY_OF_MONTH, 1);
                    tmp.add(Calendar.MONTH, item.getDayRepeat().getInterval());
                    tmp2 = (Calendar)tmp.clone();

                    tmp.set(Calendar.DAY_OF_WEEK, day_of_week);
                    if(tmp.before(tmp2)) {
                      tmp.add(Calendar.DAY_OF_MONTH, 7);
                    }
                  }
                }
              }
              else if(day_of_week == 8) {
                month = tmp.get(Calendar.MONTH);
                tmp2 = (Calendar)tmp.clone();
                tmp2.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                tmp3 = (Calendar)tmp2.clone();
                tmp2.add(Calendar.MONTH, 1);
                tmp3.add(Calendar.MONTH, -1);
                if(tmp2.get(Calendar.MONTH) == month) {
                  tmp.add(Calendar.DAY_OF_MONTH, 7);
                }
                else if(tmp3.get(Calendar.MONTH) == month) {
                  tmp.add(Calendar.DAY_OF_MONTH, -7);
                }

                while(true) {
                  tmp.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

                  //intervalの処理
                  if(tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) > item.getDayRepeat().getOrdinal_number()) {
                    tmp.set(Calendar.DAY_OF_MONTH, 1);
                    tmp.add(Calendar.MONTH, item.getDayRepeat().getInterval());
                    tmp2 = (Calendar)tmp.clone();

                    tmp.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    if(tmp.before(tmp2)) {
                      tmp.add(Calendar.DAY_OF_MONTH, 7);
                    }

                    month = tmp.get(Calendar.MONTH);
                  }

                  while(tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) < item.getDayRepeat().getOrdinal_number()
                      && tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) < tmp.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH)) {
                    tmp.add(Calendar.DAY_OF_MONTH, 7);
                  }

                  tmp.add(Calendar.DAY_OF_MONTH, item.getDayRepeat().getWeekday_num());
                  item.getDayRepeat().setWeekday_num(item.getDayRepeat().getWeekday_num() + 1);

                  if(tmp.after(now) && month == tmp.get(Calendar.MONTH)) {
                    item.getDayRepeat().setWeekday_num(0);
                    break;
                  }
                  else if(item.getDayRepeat().getWeekday_num() > 4 || month != tmp.get(Calendar.MONTH)) {
                    tmp.add(Calendar.DAY_OF_MONTH, -item.getDayRepeat().getWeekday_num() + 1);
                    tmp.set(Calendar.DAY_OF_MONTH, 1);
                    tmp.add(Calendar.MONTH, item.getDayRepeat().getInterval());
                    tmp2 = (Calendar)tmp.clone();

                    tmp.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    if(tmp.before(tmp2)) {
                      tmp.add(Calendar.DAY_OF_MONTH, 7);
                    }

                    month = tmp.get(Calendar.MONTH);
                    item.getDayRepeat().setWeekday_num(0);
                  }
                }
              }
              else if(day_of_week == 9) {
                boolean sunday_match = false;
                
                if(tmp.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY && tmp.after(now)) {
                  tmp2 = (Calendar)tmp.clone();
                  tmp2.add(Calendar.DAY_OF_MONTH, -1);

                  if(tmp2.get(Calendar.DAY_OF_WEEK_IN_MONTH) == item.getDayRepeat().getOrdinal_number()) {
                    sunday_match = true;
                  }
                }

                if(!sunday_match) {

                  month = tmp.get(Calendar.MONTH);
                  tmp2 = (Calendar)tmp.clone();
                  tmp2.add(Calendar.DAY_OF_MONTH, 1);

                  match_to_ordinal_num = false;
                  if(item.getDayRepeat().getOrdinal_number() == 5) {
                    if(tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) == tmp.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH)) {
                      match_to_ordinal_num = true;
                    }
                  }
                  else {
                    if(tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) == item.getDayRepeat().getOrdinal_number()) {
                      match_to_ordinal_num = true;
                    }
                  }

                  if(match_to_ordinal_num && tmp.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                      && tmp2.get(Calendar.MONTH) == month && tmp.before(now)) {
                    tmp.add(Calendar.DAY_OF_MONTH, 1);
                  }
                  else {
                    month = tmp.get(Calendar.MONTH);
                    tmp2 = (Calendar)tmp.clone();
                    tmp2.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                    tmp3 = (Calendar)tmp2.clone();
                    tmp2.add(Calendar.MONTH, 1);
                    tmp3.add(Calendar.MONTH, -1);
                    if(tmp2.get(Calendar.MONTH) == month) {
                      tmp.add(Calendar.DAY_OF_MONTH, 7);
                    }
                    else if(tmp3.get(Calendar.MONTH) == month) {
                      tmp.add(Calendar.DAY_OF_MONTH, -7);
                    }

                    tmp.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

                    while(true) {

                      //intervalの処理
                      if(tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) > item.getDayRepeat().getOrdinal_number()) {
                        tmp.set(Calendar.DAY_OF_MONTH, 1);
                        tmp.add(Calendar.MONTH, item.getDayRepeat().getInterval());
                        tmp2 = (Calendar)tmp.clone();

                        tmp.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                        if(tmp.before(tmp2)) {
                          tmp.add(Calendar.DAY_OF_MONTH, 7);
                        }
                      }

                      while(tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) < item.getDayRepeat().getOrdinal_number()
                          && tmp.get(Calendar.DAY_OF_WEEK_IN_MONTH) < tmp.getActualMaximum(Calendar.DAY_OF_WEEK_IN_MONTH)) {
                        tmp.add(Calendar.DAY_OF_MONTH, 7);
                      }

                      if(tmp.after(now)) break;
                      else {
                        tmp.set(Calendar.DAY_OF_MONTH, 1);
                        tmp.add(Calendar.MONTH, item.getDayRepeat().getInterval());
                        tmp2 = (Calendar)tmp.clone();

                        tmp.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                        if(tmp.before(tmp2)) {
                          tmp.add(Calendar.DAY_OF_MONTH, 7);
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
        else if((item.getDayRepeat().getSetted() & (1 << 3)) != 0) {

          //Yearリピート設定時
          now = Calendar.getInstance();
          int month_last;
          
          if(item.getDate().getTimeInMillis() > now.getTimeInMillis()) {
            tmp = (Calendar)item.getDate().clone();
            month = item.getDate().get(Calendar.MONTH);

            //intervalの処理
            month_last = Integer.toBinaryString(item.getDayRepeat().getYear()).length() - 1;
            if(month >= month_last) {
              tmp.set(Calendar.MONTH, 0);
              tmp.add(Calendar.YEAR, item.getDayRepeat().getInterval());
              month = 0;
            }

            int i = 0;
            while(i < 12 - month) {
              if((item.getDayRepeat().getYear() & (1 << (month + i))) != 0) {
                tmp.add(Calendar.MONTH, i);
                if(tmp.get(Calendar.DAY_OF_MONTH) < item.getDayRepeat().getDay_of_month_of_year()
                    && item.getDayRepeat().getDay_of_month_of_year() <= tmp.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                  tmp.set(Calendar.DAY_OF_MONTH, item.getDayRepeat().getDay_of_month_of_year());
                }

                if(tmp.after(item.getDate())) {
                  break;
                }
              }
              i++;

              if(i >= 12 - month) {
                i = 0;
                tmp.set(Calendar.MONTH, 0);
                tmp.add(Calendar.YEAR, item.getDayRepeat().getInterval());
                month = 0;
              }
            }
          }
          else {
            tmp = (Calendar)now.clone();
            //itemに登録されている日にちが今月の日にちの最大値を超えている場合、今月の日にちの最大値を設定する
            if(tmp.getActualMaximum(Calendar.DAY_OF_MONTH) < item.getDate().get(Calendar.DAY_OF_MONTH)) {
              tmp.set(Calendar.DAY_OF_MONTH, tmp.getActualMaximum(Calendar.DAY_OF_MONTH));
            }
            else {
              tmp.set(Calendar.DAY_OF_MONTH, item.getDate().get(Calendar.DAY_OF_MONTH));
            }
            tmp.set(Calendar.HOUR_OF_DAY, item.getDate().get(Calendar.HOUR_OF_DAY));
            tmp.set(Calendar.MINUTE, item.getDate().get(Calendar.MINUTE));
            tmp.set(Calendar.SECOND, 0);
            month = now.get(Calendar.MONTH);

            //intervalの処理
            month_last = Integer.toBinaryString(item.getDayRepeat().getYear()).length() - 1;
            if(month > month_last) {
              tmp.set(Calendar.MONTH, 0);
              tmp.add(Calendar.YEAR, item.getDayRepeat().getInterval());
              month = 0;
            }
            else if(month == month_last) {
              if(tmp.before(now)) {
                tmp.set(Calendar.MONTH, 0);
                tmp.add(Calendar.YEAR, item.getDayRepeat().getInterval());
                month = 0;
              }
            }

            int i = 0;
            while(i < 12 - month) {
              if((item.getDayRepeat().getYear() & (1 << (month + i))) != 0) {
                tmp.add(Calendar.MONTH, i);
                if(tmp.get(Calendar.DAY_OF_MONTH) < item.getDayRepeat().getDay_of_month_of_year()
                    && item.getDayRepeat().getDay_of_month_of_year() <= tmp.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                  tmp.set(Calendar.DAY_OF_MONTH, item.getDayRepeat().getDay_of_month_of_year());
                }

                if(tmp.after(now)) {
                  break;
                }
              }
              i++;

              if(i >= 12 - month) {
                i = 0;
                tmp.set(Calendar.MONTH, 0);
                tmp.add(Calendar.YEAR, item.getDayRepeat().getInterval());
                month = 0;
              }
            }
          }
        }

        if(!in_minute_repeat) {
          if((item.getMinuteRepeat().getWhich_setted() & 1) != 0
              && item.getMinuteRepeat().getCount() == 0) {

            if(item.getDayRepeat().getSetted() != 0) {
              item.getMinuteRepeat().setCount(item.getMinuteRepeat().getOrg_count());
            }
            else {
              item.getMinuteRepeat().setWhich_setted(0);
            }
          }
          else if((item.getMinuteRepeat().getWhich_setted() & (1 << 1)) != 0
              && item.getMinuteRepeat().getInterval() > item.getMinuteRepeat().getDuration()) {

            if(item.getDayRepeat().getSetted() != 0) {
              item.getMinuteRepeat().setDuration(item.getMinuteRepeat().getOrgDuration());
            }
            else {
              item.getMinuteRepeat().setWhich_setted(0);
            }
          }
        }


        //tmp設定後の処理
        if(item.getDayRepeat().getSetted() != 0 || item.getMinuteRepeat().getWhich_setted() != 0) {
          if(!in_minute_repeat) {
            item.setOrg_alarm_stopped(item.isAlarm_stopped());
            item.setOrg_time_altered(item.getTime_altered());
            if(item.isAlarm_stopped()) item.setAlarm_stopped(false);
            if(item.getTime_altered() != 0) item.setTime_altered(0);
          }
          item.setDate((Calendar)tmp.clone());

          activity.deleteAlarm(item);
          activity.setAlarm(item);
          activity.updateDB(item, MyDatabaseHelper.TODO_TABLE);

          Timer timer = new Timer();
          final Handler handler = new Handler();
          timer.schedule(new TimerTask() {
            @Override
            public void run() {
              handler.post(new Runnable() {
                @Override
                public void run() {
                  notifyDataSetChanged();
                }
              });
            }
          }, 400);
        }
        else {
          children.get(group_position).remove(child_position);
          item.setDoneDate(Calendar.getInstance());

          activity.deleteAlarm(item);
          activity.deleteDB(item, MyDatabaseHelper.TODO_TABLE);
          activity.insertDB(item, MyDatabaseHelper.DONE_TABLE);

          Timer timer = new Timer();
          final Handler handler = new Handler();
          timer.schedule(new TimerTask() {
            @Override
            public void run() {
              handler.post(new Runnable() {
                @Override
                public void run() {
                  notifyDataSetChanged();
                }
              });
            }
          }, 400);
        }

        Snackbar.make(convertView, activity.getResources().getString(R.string.complete), Snackbar.LENGTH_LONG)
            .addCallback(new Snackbar.Callback() {
              @Override
              public void onShown(Snackbar sb) {

                super.onShown(sb);
                is_control_panel_locked = true;
                if(viewHolder.control_panel.getVisibility() == View.VISIBLE) {
                  viewHolder.control_panel.setVisibility(View.GONE);
                }
              }

              @Override
              public void onDismissed(Snackbar transientBottomBar, int event) {

                super.onDismissed(transientBottomBar, event);
                is_control_panel_locked = false;
                notifyDataSetChanged();

                //データベースを更新したら、そのデータベースを端末暗号化ストレージへコピーする
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                  Context direct_boot_context = activity.createDeviceProtectedStorageContext();
                  direct_boot_context.moveDatabaseFrom(activity, MyDatabaseHelper.TODO_TABLE);
                }
              }
            })
            .setAction(R.string.undo, new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                if(item.getDayRepeat().getSetted() != 0 || item.getMinuteRepeat().getWhich_setted() != 0) {
                  item.setAlarm_stopped(item.isOrg_alarm_stopped());
                  item.setTime_altered(item.getOrg_time_altered());
                  if((item.getMinuteRepeat().getWhich_setted() & 1) != 0) {
                    item.getMinuteRepeat().setCount(item.getMinuteRepeat().getOrg_count2());
                  }
                  else if((item.getMinuteRepeat().getWhich_setted() & (1 << 1)) != 0) {
                    item.getMinuteRepeat().setDuration(item.getMinuteRepeat().getOrg_duration2());
                  }
                  item.getDate().setTimeInMillis(item.getOrg_date().getTimeInMillis() + item.getTime_altered());
                  notifyDataSetChanged();

                  activity.deleteAlarm(item);
                  if(!item.isAlarm_stopped()) {
                    activity.setAlarm(item);
                  }
                  activity.updateDB(item, MyDatabaseHelper.TODO_TABLE);
                }
                else {
                  item.getDate().setTimeInMillis(item.getOrg_date().getTimeInMillis() + item.getTime_altered());
                  children.get(group_position).add(item);
                  for(List<Item> itemList : children) {
                    Collections.sort(itemList, SCHEDULED_ITEM_COMPARATOR);
                  }
                  notifyDataSetChanged();

                  if(!item.isAlarm_stopped()) {
                    activity.setAlarm(item);
                  }
                  activity.insertDB(item, MyDatabaseHelper.TODO_TABLE);
                  activity.deleteDB(item, MyDatabaseHelper.DONE_TABLE);
                }
              }
            })
            .show();
      }
      else if(isChecked && manually_checked) {
        viewHolder.checkBox.jumpDrawablesToCurrentState();
        item.setSelected(true);
        notifyDataSetChanged();
        checked_item_num++;
        actionMode.setTitle(Integer.toString(checked_item_num));
      }
      else if(actionMode != null && manually_checked) {
        viewHolder.checkBox.jumpDrawablesToCurrentState();
        item.setSelected(false);
        notifyDataSetChanged();
        checked_item_num--;
        actionMode.setTitle(Integer.toString(checked_item_num));
        if(checked_item_num == 0) actionMode.finish();
      }
    }

    @Override
    public boolean onLongClick(View v) {

      if(actionMode != null) {

        if(viewHolder.checkBox.isChecked()) {
          viewHolder.checkBox.setChecked(false);
        }
        else viewHolder.checkBox.setChecked(true);

        return true;
      }
      else {
        actionMode = activity.startSupportActionMode(this);
        viewHolder.checkBox.setChecked(true);
        return true;
      }
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

      actionMode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);

      //ActionMode時のみツールバーとステータスバーの色を設定
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.darker_grey));
      }

      return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {

      MenuItem moveTaskItem = menu.findItem(R.id.move_task_between_list);
      if(ManageListAdapter.nonScheduledLists.size() > 0) {
        moveTaskItem.setVisible(true);
      }
      else moveTaskItem.setVisible(false);
      return true;
    }

    @Override
    public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem) {

      switch(menuItem.getItemId()) {
        case R.id.delete: {

          itemListToMove = new ArrayList<>();
          for(List<Item> itemList : children) {
            for(Item item : itemList) {
              if(item.isSelected()) {
                itemListToMove.add(0, item);
              }
            }
          }

          String message = itemListToMove.size() + activity.getString(R.string.cab_delete_message)
              + "(" + activity.getString(R.string.delete_dialog_message) + ")";
          new AlertDialog.Builder(activity)
              .setTitle(R.string.cab_delete)
              .setMessage(message)
              .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                  for(List<Item> itemList : children) {
                    for(Item item : itemList) {
                      if(item.isSelected()) {
                        activity.deleteDB(item, MyDatabaseHelper.TODO_TABLE);
                        MyExpandableListAdapter.children = activity.getChildren(MyDatabaseHelper.TODO_TABLE);
                        activity.deleteAlarm(item);
                      }
                    }
                  }

                  //データベースを更新したら、そのデータベースを端末暗号化ストレージへコピーする
                  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Context direct_boot_context = activity.createDeviceProtectedStorageContext();
                    direct_boot_context.moveDatabaseFrom(activity, MyDatabaseHelper.TODO_TABLE);
                  }

                  actionMode.finish();
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
        case R.id.move_task_between_list: {

          itemListToMove = new ArrayList<>();
          for(List<Item> itemList : children) {
            for(Item item : itemList) {
              if(item.isSelected()) {
                itemListToMove.add(0, item);
              }
            }
          }

          int size = ManageListAdapter.nonScheduledLists.size();
          String[] items = new String[size];
          for(int i = 0; i < size; i++) {
            items[i] = ManageListAdapter.nonScheduledLists.get(i).getTitle();
          }

          String title = itemListToMove.size() + activity.getString(R.string.cab_selected_task_num)
              + activity.getString(R.string.cab_move_task_message);
          new AlertDialog.Builder(activity)
              .setTitle(title)
              .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  which_list = which;
                }
              })
              .setPositiveButton(R.string.determine, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                  long list_id = activity.generalSettings.getNonScheduledLists().get(which_list).getId();
                  MyListAdapter.itemList = new ArrayList<>();
                  for(Item item : activity.queryAllDB(MyDatabaseHelper.TODO_TABLE)) {
                    if(item.getWhich_list_belongs() == list_id) {
                      MyListAdapter.itemList.add(item);
                    }
                  }
                  Collections.sort(MyListAdapter.itemList, NON_SCHEDULED_ITEM_COMPARATOR);

                  for(Item item : itemListToMove) {

                    item.setSelected(false);
                    item.setDate(Calendar.getInstance());
                    item.setNotify_interval(new NotifyInterval());
                    item.setDayRepeat(new DayRepeat());
                    item.setMinuteRepeat(new MinuteRepeat());

                    //リストのIDをitemに登録する
                    item.setWhich_list_belongs(list_id);

                    MyListAdapter.itemList.add(0, item);
                    activity.deleteAlarm(item);
                  }

                  int size = MyListAdapter.itemList.size();
                  for(int i = 0; i < size; i++) {
                    Item item = MyListAdapter.itemList.get(i);
                    item.setOrder(i);
                    activity.updateDB(item, MyDatabaseHelper.TODO_TABLE);
                  }

                  children = activity.getChildren(MyDatabaseHelper.TODO_TABLE);

                  //データベースを更新したら、そのデータベースを端末暗号化ストレージへコピーする
                  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Context direct_boot_context = activity.createDeviceProtectedStorageContext();
                    direct_boot_context.moveDatabaseFrom(activity, MyDatabaseHelper.TODO_TABLE);
                  }

                  actionMode.finish();
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
        case R.id.clone: {

          itemListToMove = new ArrayList<>();
          for(List<Item> itemList : children) {
            for(Item item : itemList) {
              if(item.isSelected()) {
                itemListToMove.add(0, item);
              }
            }
          }

          String message = itemListToMove.size() + activity.getString(R.string.cab_clone_message);
          new AlertDialog.Builder(activity)
              .setTitle(R.string.cab_clone)
              .setMessage(message)
              .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                  MainEditFragment.checked_item_num = checked_item_num;
                  MainEditFragment.itemListToMove = new ArrayList<>(itemListToMove);
                  MainEditFragment.is_cloning_task = true;
                  itemListToMove.get(itemListToMove.size() - 1).setSelected(false);
                  activity.showMainEditFragment(
                      itemListToMove.get(itemListToMove.size() - 1).copy(), ExpandableListViewFragment.TAG
                  );

                  actionMode.finish();
                }
              })
              .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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
        case R.id.share: {

          itemListToMove = new ArrayList<>();
          for(List<Item> itemList : children) {
            for(Item item : itemList) {
              if(item.isSelected()) {
                itemListToMove.add(0, item);
              }
            }
          }

          String message = itemListToMove.size() + activity.getString(R.string.cab_share_message);
          new AlertDialog.Builder(activity)
              .setTitle(R.string.cab_share)
              .setMessage(message)
              .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                  for(Item item : itemListToMove) {
                    String send_content = activity.getString(R.string.days_of_month) + ": "
                        + DateFormat.format("yyyy/MM/dd HH:mm", item.getDate())
                        + LINE_SEPARATOR
                        + activity.getString(R.string.detail) + ": " + item.getDetail()
                        + LINE_SEPARATOR
                        + activity.getString(R.string.memo) + ": " + item.getNotesString();

                    Intent intent = new Intent()
                        .setAction(Intent.ACTION_SEND)
                        .setType("text/plain")
                        .putExtra(Intent.EXTRA_TEXT, send_content);
                    activity.startActivity(intent);
                  }

                  actionMode.finish();
                }
              })
              .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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
        default: {
          actionMode.finish();
          return true;
        }
      }
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.status_bar_color);
      }

      MyExpandableListAdapter.this.actionMode = null;
      for(List<Item> itemList : children) {
        for(Item item : itemList) {
          if(item.isSelected()) {
            item.setSelected(false);
          }
        }
      }

      checked_item_num = 0;
      notifyDataSetChanged();
    }
  }

  @Override
  public Filter getFilter() {

    return new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence constraint) {

        //入力文字列が大文字を含むかどうか調べる
        boolean is_upper = false;
        for(int i = 0; i < constraint.length(); i++) {
          if(Character.isUpperCase(constraint.charAt(i))) {
            is_upper = true;
            break;
          }
        }

        //検索処理
        if(activity.actionBarFragment.checked_tag == -1) {
          children = activity.getChildren(MyDatabaseHelper.TODO_TABLE);
        }
        else {
          children = activity.actionBarFragment.filteredLists;
        }

        filteredList = new ArrayList<>();
        for(List<Item> itemList : children) {
          List<Item> filteredItem = new ArrayList<>();

          for(Item item : itemList) {
            if(item.getDetail() != null) {
              String detail = item.getDetail();

              if(!is_upper) detail = detail.toLowerCase();

              Pattern pattern = Pattern.compile(constraint.toString());
              Matcher matcher = pattern.matcher(detail);

              if(matcher.find()) filteredItem.add(item);
            }
          }

          filteredList.add(filteredItem);
        }

        FilterResults results = new FilterResults();
        results.count = filteredList.size();
        results.values = filteredList;

        return results;
      }

      @Override
      @SuppressWarnings("unchecked")
      protected void publishResults(CharSequence constraint, FilterResults results) {

        children = (List<List<Item>>)results.values;

        //リストの表示更新
        notifyDataSetChanged();
      }
    };
  }

  @Override
  public int getGroupCount() {

    //表示するgroupsのサイズを返す。
    int count = 0;
    Arrays.fill(display_groups, false);
    int size = groups.size();
    for(int i = 0; i < size; i++) {
      if(children.get(i).size() != 0) {
        display_groups[i] = true;
        count++;
      }
    }

    return count;
//    return groups.size();
  }

  @Override
  public int getChildrenCount(int i) {

    //getChildrenCount()はgetGroupCount()によって返されるgroupsのサイズ分だけ(サイズが3なら3回)呼ばれる。
    //iはgetChildrenCount()の呼ばれた回数を表す。すなわちiは呼び出しを3回とすると1回目の呼び出しにおいて、
    //表示するgroupsの0番目を表す。2回目では1番目、3回目では2番目である。
    int count = 0;
    int size = groups.size();
    for(int j = 0; j < size; j++) {
      if(display_groups[j]) {
        //単に return children.get(j).size() とすると、表示するgroupsの1番目だけを返し続けてしまうので、
        //if(count == i) と条件を付けることで、getChildrenCount()の呼び出された回数に応じて表示する
        //groupsの対応するgroupのみ返すようにしている。
        if(count == i) return children.get(j).size();
        count++;
      }
    }

    return children.get(i).size();
  }

  @Override
  public Object getGroup(int i) {

    //getGroupCount()によって返されるgroupsのサイズ分だけ呼ばれる。引数のiに関してもgetChildrenCount()と同じ。
    int count = 0;
    int size = groups.size();
    for(int j = 0; j < size; j++) {
      if(display_groups[j]) {
        if(count == i) return groups.get(j);
        count++;
      }
    }

    return groups.get(i);
  }

  @Override
  public Object getChild(int i, int i1) {

    //getChildrenCount()によって返されるchildrenのサイズ分×getGroupCount()によって返されるgroupsのサイズ分
    //だけ呼ばれる。あとは他のメソッドと同じ。
    int count = 0;
    int size = groups.size();
    for(int j = 0; j < size; j++) {
      if(display_groups[j]) {
        if(count == i) return children.get(j).get(i1);
        count++;
      }
    }

    return children.get(i).get(i1);
  }

  @Override
  public long getGroupId(int i) {
    return i;
  }

  @Override
  public long getChildId(int i, int i1) {
    return i1;
  }

  @Override
  public boolean hasStableIds() {
    return true;
  }

  @Override
  public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {

    if(convertView == null) {
      convertView = View.inflate(viewGroup.getContext(), R.layout.parent_layout, null);

      ((ExpandableListView)viewGroup).expandGroup(i);
    }

    ((TextView)convertView.findViewById(R.id.day)).setText(getGroup(i).toString());

    return convertView;
  }

  @Override
  public View getChildView(int i, int i1, boolean b, View convertView, final ViewGroup viewGroup) {

    final ChildViewHolder viewHolder;

    if(convertView == null) {
      convertView = View.inflate(viewGroup.getContext(), R.layout.child_layout, null);

      viewHolder = new ChildViewHolder();
      viewHolder.child_card = convertView.findViewById(R.id.child_card);
      viewHolder.clock_image = convertView.findViewById(R.id.clock_image);
      viewHolder.time = convertView.findViewById(R.id.date);
      viewHolder.detail = convertView.findViewById(R.id.detail);
      viewHolder.repeat = convertView.findViewById(R.id.repeat);
      viewHolder.checkBox = convertView.findViewById(R.id.checkBox);
      CompoundButtonCompat.setButtonTintList(viewHolder.checkBox, colorStateList);
      viewHolder.tagPallet = convertView.findViewById(R.id.tag_pallet);
      viewHolder.control_panel = convertView.findViewById(R.id.control_panel);

      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ChildViewHolder)convertView.getTag();
    }

    //現在のビュー位置でのitemの取得とリスナーの初期化
    Item item = (Item)getChild(i, i1);
    int count = 0;
    MyOnClickListener listener = null;
    int size = groups.size();
    for(int j = 0; j < size; j++) {
      if(display_groups[j]) {
        if(count == i) {
          listener = new MyOnClickListener(j, i1, item, convertView, viewHolder);
          break;
        }
        count++;
      }
    }

    //各リスナーの設定
    viewHolder.child_card.setOnClickListener(listener);
    viewHolder.clock_image.setOnClickListener(listener);
    viewHolder.checkBox.setOnCheckedChangeListener(listener);

    viewHolder.child_card.setOnLongClickListener(listener);
    viewHolder.clock_image.setOnLongClickListener(listener);
    viewHolder.checkBox.setOnLongClickListener(listener);

    int control_panel_size = viewHolder.control_panel.getChildCount();
    for(int j = 0; j < control_panel_size; j++) {
      TableRow tableRow = (TableRow)viewHolder.control_panel.getChildAt(j);
      int table_row_size = tableRow.getChildCount();
      for(int k = 0; k < table_row_size; k++) {
        TextView panel_item = (TextView)tableRow.getChildAt(k);
        panel_item.setOnClickListener(listener);
      }
    }

    //各種表示処理
    displayDate(viewHolder, item);
    viewHolder.detail.setText(item.getDetail());
    displayRepeat(viewHolder, item);
    if(item.getWhich_tag_belongs() == 0) {
      viewHolder.tagPallet.setVisibility(View.GONE);
    }
    else {
      viewHolder.tagPallet.setVisibility(View.VISIBLE);
      int color = activity.generalSettings.getTagById(item.getWhich_tag_belongs()).getPrimary_color();
      if(color != 0) {
        viewHolder.tagPallet.setColorFilter(color);
      }
      else {
        viewHolder.tagPallet.setColorFilter(ContextCompat.getColor(activity, R.color.icon_gray));
      }
    }

    //ある子ビューでコントロールパネルを出したとき、他の子ビューのコントロールパネルを閉じる
    if(viewHolder.control_panel.getVisibility() == View.VISIBLE
        && (item.getId() != has_panel || actionMode != null)) {
      viewHolder.control_panel.setVisibility(View.GONE);
    }
    else if(viewHolder.control_panel.getVisibility() == View.GONE && item.getId() == has_panel
        && !is_control_panel_locked && actionMode == null) {
      viewHolder.control_panel.setVisibility(View.VISIBLE);
    }

    //チェックが入っている場合、チェックを外す
    if(viewHolder.checkBox.isChecked() && !item.isSelected()) {
      manually_checked = false;
      viewHolder.checkBox.setChecked(false);
      viewHolder.checkBox.jumpDrawablesToCurrentState();
    }
    else if(!viewHolder.checkBox.isChecked() && item.isSelected()) {
      manually_checked = false;
      viewHolder.checkBox.setChecked(true);
      viewHolder.checkBox.jumpDrawablesToCurrentState();
    }
    manually_checked = true;

    return convertView;
  }

  //時間を表示する処理
  private void displayDate(ChildViewHolder viewHolder, Item item) {

    Calendar now = Calendar.getInstance();
    String set_time;
    if(now.get(Calendar.YEAR) == item.getDate().get(Calendar.YEAR)) {
      set_time = (String)DateFormat.format("M月d日(E)H:mm", item.getDate());
    }
    else {
      set_time = (String)DateFormat.format("yyyy年M月d日(E)H:mm", item.getDate());
    }
    long date_sub = item.getDate().getTimeInMillis() - now.getTimeInMillis();

    boolean date_is_minus = false;
    if(date_sub < 0) {
      date_sub = -date_sub;
      date_is_minus = true;
    }

    int how_far_years = 0;
    tmp = (Calendar)now.clone();
    if(date_is_minus) {
      tmp.add(Calendar.YEAR, -1);
      while(tmp.after(item.getDate())) {
        tmp.add(Calendar.YEAR, -1);
        how_far_years++;
      }
    }
    else {
      tmp.add(Calendar.YEAR, 1);
      while(tmp.before(item.getDate())) {
        tmp.add(Calendar.YEAR, 1);
        how_far_years++;
      }
    }

    int how_far_months = 0;
    tmp = (Calendar)now.clone();
    if(how_far_years != 0) tmp.add(Calendar.YEAR, how_far_years);
    if(date_is_minus) {
      tmp.add(Calendar.MONTH, -1);
      while(tmp.after(item.getDate())) {
        tmp.add(Calendar.MONTH, -1);
        how_far_months++;
      }
    }
    else {
      tmp.add(Calendar.MONTH, 1);
      while(tmp.before(item.getDate())) {
        tmp.add(Calendar.MONTH, 1);
        how_far_months++;
      }
    }

    int how_far_weeks = 0;
    tmp = (Calendar)now.clone();
    if(how_far_years != 0) tmp.add(Calendar.YEAR, how_far_years);
    if(how_far_months != 0) tmp.add(Calendar.MONTH, how_far_months);
    if(date_is_minus) {
      tmp.add(Calendar.DAY_OF_WEEK_IN_MONTH, -1);
      while(tmp.after(item.getDate())) {
        tmp.add(Calendar.DAY_OF_WEEK_IN_MONTH, -1);
        how_far_weeks++;
      }
    }
    else {
      tmp.add(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
      while(tmp.before(item.getDate())) {
        tmp.add(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
        how_far_weeks++;
      }
    }

    long how_far_days = date_sub / (1000 * 60 * 60 * 24);
    long how_far_hours = date_sub / (1000 * 60 * 60);
    long how_far_minutes = date_sub / (1000 * 60);


    String display_date = set_time + " (";
    if(how_far_years != 0) {
      display_date += how_far_years + "年";
      if(how_far_months != 0) display_date += how_far_months + "ヶ月";
      if(how_far_weeks != 0) display_date += how_far_weeks + "週間";
    }
    else if(how_far_months != 0) {
      display_date += how_far_months + "ヶ月";
      if(how_far_weeks != 0) display_date += how_far_weeks + "週間";
    }
    else if(how_far_weeks != 0) {
      display_date += how_far_weeks + "週間";
      how_far_days -= 7 * how_far_weeks;
      if(how_far_days != 0) display_date += how_far_days + "日";
    }
    else if(how_far_days != 0) {
      display_date += how_far_days + "日";
    }
    else if(how_far_hours != 0) {
      display_date += how_far_hours + "時間";
      how_far_minutes -= 60 * how_far_hours;
      if(how_far_minutes != 0) display_date += how_far_minutes + "分";
    }
    else if(how_far_minutes != 0) {
      display_date += how_far_minutes + "分";
    }
    else {
      display_date += "<< 1分 >>";
    }
    display_date += ")";

    viewHolder.time.setText(display_date);

    if(item.isAlarm_stopped()) viewHolder.time.setTextColor(Color.GRAY);
    else if(date_is_minus) viewHolder.time.setTextColor(Color.RED);
    else viewHolder.time.setTextColor(Color.BLACK);

    if(item.isAlarm_stopped()) viewHolder.clock_image.setColorFilter(Color.GRAY);
    else if(item.getTime_altered() != 0) viewHolder.clock_image.setColorFilter(Color.BLUE);
    else viewHolder.clock_image.setColorFilter(0xFF09C858);
  }

  //リピートを表示する処理
  private void displayRepeat(ChildViewHolder viewHolder, Item item) {

    String repeat_str = "";
    if(item.getDayRepeat().getLabel() != null
        && !item.getDayRepeat().getLabel().equals(activity.getString(R.string.none))) {
      repeat_str += item.getDayRepeat().getLabel();
      if(!item.getDayRepeat().getLabel().equals(activity.getString(R.string.everyday))) {
        repeat_str += "に";
      }
    }
    if(item.getMinuteRepeat().getLabel() != null
        && !item.getMinuteRepeat().getLabel().equals(activity.getString(R.string.none))) {
      repeat_str += item.getMinuteRepeat().getLabel();
    }
    if(item.getDayRepeat().getLabel() != null
        && !item.getDayRepeat().getLabel().equals(activity.getString(R.string.none))
        && (item.getMinuteRepeat().getLabel() == null
        || item.getMinuteRepeat().getLabel().equals(activity.getString(R.string.none)))) {
      repeat_str += "繰り返す";
    }
    if(repeat_str.equals("")) {
      viewHolder.repeat.setText(R.string.non_repeat);
    }
    else {
      viewHolder.repeat.setText(repeat_str);
    }
  }

  @Override
  public boolean isChildSelectable(int i, int i1) {
    return true;
  }
}
