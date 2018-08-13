package com.example.hideaki.reminder;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyExpandableListAdapter extends BaseExpandableListAdapter implements Filterable {

  private static String set_time;
  private static Calendar tmp;
  private static boolean[] display_groups = new boolean[5];
  static final List<String> groups;
  public static List<List<Item>> children;
  private static List<List<Item>> org_children;
  private Context context;
  private OnFragmentInteractionListener mListener;
  private Pattern pattern;
  private Matcher matcher;
  private TableRow tableRow;
  private TextView panel_item;
  private boolean date_is_minus;
  private Item item;
  private MyOnClickListener listener;

  static {
    groups = new ArrayList<>();

    groups.add("過去");
    groups.add("今日");
    groups.add("明日");
    groups.add("一週間");
    groups.add("一週間以上");
  }

  public MyExpandableListAdapter(List<List<Item>> children, Context context) {
    this.children = children;
    this.context = context;

    if(context instanceof OnFragmentInteractionListener) {
      mListener = (OnFragmentInteractionListener)context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public Filter getFilter() {
    Filter filter = new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        List<List<Item>> filteredList = new ArrayList<>();

        //入力文字列が大文字を含むかどうか調べる
        boolean is_upper = false;
        for(int i = 0; i < constraint.length(); i++) {
          if(Character.isUpperCase(constraint.charAt(i))) {
            is_upper = true;
            break;
          }
        }

        //検索処理
        if(org_children == null) org_children = children;
        else children = org_children;

        for(List<Item> itemList : children) {
          List<Item> filteredItem = new ArrayList<>();

          for(Item item : itemList) {
            if(item.getDetail() != null) {
              String detail = item.getDetail();

              if(!is_upper) {
                detail = detail.toLowerCase();
              }

              pattern = Pattern.compile(constraint.toString());
              matcher = pattern.matcher(detail);

              if(matcher.find()) {
                filteredItem.add(item);
              }
            }
          }

          filteredList.add(filteredItem);
        }

        results.count = filteredList.size();
        results.values = filteredList;

        return results;
      }

      @Override
      protected void publishResults(CharSequence constraint, FilterResults results) {
        children = (List<List<Item>>)results.values;

        //リストの表示更新
        notifyDataSetChanged();
      }
    };

    return filter;
  }

  private static class ChildViewHolder {

    TextView time;
    TextView detail;
    TextView repeat;
    ImageView clock_image;
    CardView child_card;
    TableLayout control_panel;
  }

  private class MyOnClickListener implements View.OnClickListener {

    private ChildViewHolder viewHolder;
    private Item item;

    public MyOnClickListener(ChildViewHolder viewHolder, Item item) {

      this.viewHolder = viewHolder;
      this.item = item;
    }

    @Override
    public void onClick(View v) {

      switch(v.getId()) {
        case R.id.child_card:
          if(viewHolder.control_panel.getVisibility() == View.GONE) {
            viewHolder.control_panel.setVisibility(View.VISIBLE);
          }
          else viewHolder.control_panel.setVisibility(View.GONE);
          break;
        case R.id.m5m:
          if(item.getDate().getTimeInMillis() > System.currentTimeMillis() + 5 * 60 * 1000) {
            if(item.getTime_altered() == 0) {
              item.setOrg_date((Calendar)item.getDate().clone());
            }
            item.getDate().setTimeInMillis(item.getDate().getTimeInMillis() + -5 * 60 * 1000);

            item.addTime_altered(-5 * 60 * 1000);
            if(item.isAlarm_stopped()) item.setAlarm_stopped(false);

            if(mListener.isAlarmSetted(item)) {
              mListener.deleteAlarm(item);
            }
            mListener.setAlarm(item);
            displayDate(viewHolder, item);
          }
          break;
        case R.id.m1h:
          if(item.getDate().getTimeInMillis() > System.currentTimeMillis() + 1 * 60 * 60 * 1000) {
            if(item.getTime_altered() == 0) {
              item.setOrg_date((Calendar)item.getDate().clone());
            }
            item.getDate().setTimeInMillis(item.getDate().getTimeInMillis() + -1 * 60 * 60 * 1000);

            item.addTime_altered(-1 * 60 * 60 * 1000);
            if(item.isAlarm_stopped()) item.setAlarm_stopped(false);

            if(mListener.isAlarmSetted(item)) {
              mListener.deleteAlarm(item);
            }
            mListener.setAlarm(item);
            displayDate(viewHolder, item);
          }
          break;
        case R.id.m1d:
          if(item.getDate().getTimeInMillis() > System.currentTimeMillis() + 24 * 60 * 60 * 1000) {
            if(item.getTime_altered() == 0) {
              item.setOrg_date((Calendar)item.getDate().clone());
            }
            item.getDate().setTimeInMillis(item.getDate().getTimeInMillis() + -24 * 60 * 60 * 1000);

            item.addTime_altered(-24 * 60 * 60 * 1000);
            if(item.isAlarm_stopped()) item.setAlarm_stopped(false);

            if(mListener.isAlarmSetted(item)) {
              mListener.deleteAlarm(item);
            }
            mListener.setAlarm(item);
            displayDate(viewHolder, item);
          }
          break;
        case R.id.edit:
          mListener.showMainEditFragment(item);
          break;
        case R.id.p5m:
          if(item.getDate().getTimeInMillis() < System.currentTimeMillis()) {
            if(item.getTime_altered() == 0) {
              item.setOrg_date((Calendar)item.getDate().clone());
            }
            item.getDate().setTimeInMillis(System.currentTimeMillis() + 5 * 60 * 1000);
          }
          else {
            if(item.getTime_altered() == 0) {
              item.setOrg_date((Calendar)item.getDate().clone());
            }
            item.getDate().setTimeInMillis(item.getDate().getTimeInMillis() + 5 * 60 * 1000);
          }

          item.addTime_altered(5 * 60 * 1000);
          if(item.isAlarm_stopped()) item.setAlarm_stopped(false);

          if(mListener.isAlarmSetted(item)) {
            mListener.deleteAlarm(item);
          }
          mListener.setAlarm(item);
          displayDate(viewHolder, item);
          break;
        case R.id.p1h:
          if(item.getDate().getTimeInMillis() < System.currentTimeMillis()) {
            if(item.getTime_altered() == 0) {
              item.setOrg_date((Calendar)item.getDate().clone());
            }
            item.getDate().setTimeInMillis(System.currentTimeMillis() + 1 * 60 * 60 * 1000);
          }
          else {
            if(item.getTime_altered() == 0) {
              item.setOrg_date((Calendar)item.getDate().clone());
            }
            item.getDate().setTimeInMillis(item.getDate().getTimeInMillis() + 1 * 60 * 60 * 1000);
          }

          item.addTime_altered(1 * 60 * 60 * 1000);
          if(item.isAlarm_stopped()) item.setAlarm_stopped(false);

          if(mListener.isAlarmSetted(item)) {
            mListener.deleteAlarm(item);
          }
          mListener.setAlarm(item);
          displayDate(viewHolder, item);
          break;
        case R.id.p1d:
          if(item.getDate().getTimeInMillis() < System.currentTimeMillis()) {
            if(item.getTime_altered() == 0) {
              item.setOrg_date((Calendar)item.getDate().clone());
            }
            item.getDate().setTimeInMillis(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
          }
          else {
            if(item.getTime_altered() == 0) {
              item.setOrg_date((Calendar)item.getDate().clone());
            }
            item.getDate().setTimeInMillis(item.getDate().getTimeInMillis() + 24 * 60 * 60 * 1000);
          }

          item.addTime_altered(24 * 60 * 60 * 1000);
          if(item.isAlarm_stopped()) item.setAlarm_stopped(false);

          if(mListener.isAlarmSetted(item)) {
            mListener.deleteAlarm(item);
          }
          mListener.setAlarm(item);
          displayDate(viewHolder, item);
          break;
        case R.id.notes:
          mListener.showNotesFragment(item);
          break;
        case R.id.clock_image:
          if(item.getTime_altered() == 0 && mListener.isAlarmSetted(item)) {
            item.setAlarm_stopped(true);
            mListener.deleteAlarm(item);
          }
          else if(item.getTime_altered() == 0 && !item.isAlarm_stopped()) {
            item.setAlarm_stopped(true);
          }
          else if(item.getTime_altered() == 0 && item.isAlarm_stopped()) {
            item.setAlarm_stopped(false);
            mListener.setAlarm(item);
          }
          else if(item.getTime_altered() != 0) {
            item.setDate((Calendar)item.getOrg_date().clone());
            item.setTime_altered(0);
            if(item.getDate().getTimeInMillis() < System.currentTimeMillis()) {
              mListener.deleteAlarm(item);
            }
          }
          displayDate(viewHolder, item);
          break;
      }
    }
  }

  @Override
  public int getGroupCount() {

    int count = 0;
    Arrays.fill(display_groups, false);
    for(int i = 0; i < groups.size(); i++) {
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

    int count = 0;
    for(int j = 0; j < groups.size(); j++) {
      if(display_groups[j]) {
        count++;
        if(count == i + 1) return children.get(j).size();
      }
    }

    return children.get(i).size();
  }

  @Override
  public Object getGroup(int i) {

    int count = 0;
    for(int j = 0; j < groups.size(); j++) {
      if(display_groups[j]) {
        count++;
        if(count == i + 1) return groups.get(j);
      }
    }

    return groups.get(i);
  }

  @Override
  public Object getChild(int i, int i1) {

    int count = 0;
    for(int j = 0; j < groups.size(); j++) {
      if(display_groups[j]) {
        count++;
        if(count == i + 1) return children.get(j).get(i1);
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
      convertView = LayoutInflater
          .from(viewGroup.getContext())
          .inflate(R.layout.parent_layout, null);
    }

    ((TextView)convertView.findViewById(R.id.day)).setText(getGroup(i).toString());

    return convertView;
  }

  @Override
  public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {

    final ChildViewHolder viewHolder;

    if(convertView == null) {
      convertView = LayoutInflater
          .from(viewGroup.getContext())
          .inflate(R.layout.child_layout, null);

      viewHolder = new ChildViewHolder();
      viewHolder.time = convertView.findViewById(R.id.date);
      viewHolder.detail = convertView.findViewById(R.id.detail);
      viewHolder.repeat = convertView.findViewById(R.id.repeat);
      viewHolder.clock_image = convertView.findViewById(R.id.clock_image);
      viewHolder.child_card = convertView.findViewById(R.id.child_card);
      viewHolder.control_panel = convertView.findViewById(R.id.control_panel);

      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ChildViewHolder)convertView.getTag();
    }

    item = (Item)getChild(i, i1);
    listener = new MyOnClickListener(viewHolder, item);
    displayDate(viewHolder, item);

    //詳細と、リピート通知のインターバルを表示
    viewHolder.detail.setText(item.getDetail());
    viewHolder.repeat.setText(item.getRepeat().getLabel());

    viewHolder.child_card.setOnClickListener(listener);

    for(int j = 0; j < viewHolder.control_panel.getChildCount(); j++) {
      tableRow = (TableRow)viewHolder.control_panel.getChildAt(j);
      for(int k = 0; k < tableRow.getChildCount(); k++) {
        panel_item = (TextView)tableRow.getChildAt(k);
        panel_item.setOnClickListener(listener);
      }
    }

    viewHolder.clock_image.setOnClickListener(listener);

    return convertView;
  }

  //時間を表示する処理
  private void displayDate(ChildViewHolder viewHolder, Item item) {

    Calendar now = Calendar.getInstance();
    if(now.get(Calendar.YEAR) == item.getDate().get(Calendar.YEAR)) {
      set_time = (String)DateFormat.format("M月d日(E)H:mm", item.getDate());
    }
    else {
      set_time = (String)DateFormat.format("yyyy年M月d日(E)H:mm", item.getDate());
    }
    long date_sub = item.getDate().getTimeInMillis() - now.getTimeInMillis();

    date_is_minus = false;
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

  @Override
  public boolean isChildSelectable(int i, int i1) {
    return true;
  }

  public interface OnFragmentInteractionListener {
    void showMainEditFragment(Item item);
    void showNotesFragment(Item item);
    void setAlarm(Item item);
    void deleteAlarm(Item item);
    boolean isAlarmSetted(Item item);
  }
}
