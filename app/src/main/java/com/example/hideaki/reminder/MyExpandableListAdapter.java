package com.example.hideaki.reminder;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MyExpandableListAdapter extends BaseExpandableListAdapter implements Filterable {

  private static String set_time;
  private static Calendar tmp;
  private static boolean[] display_groups = new boolean[5];
  private static List<String> groups;
  public static List<List<Item>> children;
  private static List<List<Item>> org_children;
  private Context context;

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

              if(detail.contains(constraint)) {
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
    ChildViewHolder viewHolder;

    if(convertView == null) {
      convertView = LayoutInflater
          .from(viewGroup.getContext())
          .inflate(R.layout.child_layout, null);

      viewHolder = new ChildViewHolder();
      viewHolder.time = convertView.findViewById(R.id.date);
      viewHolder.detail = convertView.findViewById(R.id.detail);
      viewHolder.repeat = convertView.findViewById(R.id.repeat);

      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ChildViewHolder)convertView.getTag();
    }

    Item item = (Item)getChild(i, i1);

    //時間を表示する処理
    Calendar now = Calendar.getInstance();
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

    if(date_is_minus) {
      viewHolder.time.setTextColor(Color.RED);
    }

    //詳細と、リピート通知のインターバルを表示
    viewHolder.detail.setText(item.getDetail());
    if(item.getRepeat() != null) {
      viewHolder.repeat.setText(item.getRepeat().getLabel());
    }

    return convertView;
  }

  @Override
  public boolean isChildSelectable(int i, int i1) {
    return true;
  }
}
