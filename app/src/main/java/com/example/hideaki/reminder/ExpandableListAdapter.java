package com.example.hideaki.reminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

  private List<String> groups;
  private List<List<Item>> children;
  private Context context;
  private boolean date_minus_or_not = false;
  private int date_long_judge;

  private static class ChildViewHolder {
    TextView time;
    TextView detail;
    TextView repeat;
  }

  public ExpandableListAdapter(List<String> groups, List<List<Item>> children, Context context) {
    this.groups = groups;
    this.children = children;
    this.context = context;
  }

  @Override
  public int getGroupCount() {
    return groups.size();
  }

  @Override
  public int getChildrenCount(int i) {
    return children.get(i).size();
  }

  @Override
  public Object getGroup(int i) {
    return groups.get(i);
  }

  @Override
  public Object getChild(int i, int i1) {
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
      viewHolder.time = convertView.findViewById(R.id.time);
      viewHolder.detail = convertView.findViewById(R.id.detail);
      viewHolder.repeat = convertView.findViewById(R.id.repeat);

      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ChildViewHolder)convertView.getTag();
    }

    Item item = (Item)getChild(i, i1);

    String set_time = new SimpleDateFormat("yyyy年M月d日(E)H:m").format(item.getDate());
    long date_sub = System.currentTimeMillis() - item.getDate().getTime();
    if(date_sub < 0) {
      date_sub = Math.abs(date_sub);
      date_minus_or_not = true;
    }

    long how_far_minutes = date_sub / (1000 * 60);
    long how_far_hours = date_sub / (1000 * 60 * 60);
    long how_far_days = date_sub / (1000 * 60 * 60 * 24);
    long how_far_weeks = date_sub / (1000 * 60 * 60 * 24 * 7);

    Calendar now = Calendar.getInstance();
    int day_of_month = now.getActualMaximum(Calendar.DAY_OF_MONTH);
    int how_far_months = 0;
    while(day_of_month <= how_far_days) {
      how_far_days -= day_of_month;
      Calendar tmp = now.getInstance();
      tmp.add(Calendar.MONTH, how_far_months + 1);
      day_of_month = tmp.getActualMaximum(Calendar.DAY_OF_MONTH);
      how_far_months++;
    }

    boolean uruu_year_or_not = false;
    int year = now.get(Calendar.YEAR);
    if (year%4 == 0) {
      if (year%100 == 0) {
        if (year%400 == 0) uruu_year_or_not = true;
      }
      else uruu_year_or_not = true;
    }

    long how_far_years = date_sub / (1000 * 60 * 60 * 24 * 365);
    if(uruu_year_or_not) {
      how_far_years = date_sub / (1000 * 60 * 60 * 24 * 366);
    }

    if(how_far_years != 0) {
      viewHolder.time.setText(set_time + "(" + how_far_years + "年" + how_far_months + "ヶ月"
      + how_far_weeks + "週間)");
    }
    else if(how_far_months != 0) {
      viewHolder.time.setText(set_time + "(" + how_far_months + "ヶ月" + how_far_weeks + "週間)");
    }
    else if(how_far_weeks != 0) {
      viewHolder.time.setText(set_time + "(" + how_far_weeks + "週間" + how_far_days + "日)");
    }
    else if(how_far_days != 0) {
      viewHolder.time.setText(set_time + "(" + how_far_days + "日)");
    }
    else if(how_far_hours != 0) {
      viewHolder.time.setText(set_time + "(" + how_far_hours + "時間" + how_far_minutes + "分)");
    }
    else if(how_far_minutes != 0) {
      viewHolder.time.setText(set_time + "(" + how_far_minutes + "分)");
    }
    else {
      viewHolder.time.setText(set_time + "(<< 1分 >>)");
    }

    viewHolder.detail.setText(item.getDetail());
    viewHolder.repeat.setText(item.getRepeat());

    return convertView;
  }

  @Override
  public boolean isChildSelectable(int i, int i1) {
    return true;
  }
}
