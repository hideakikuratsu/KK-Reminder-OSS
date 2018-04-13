package com.example.hideaki.reminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

  private List<String> groups;
  private List<List<Item>> children;
  private Context context;

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
    viewHolder.time.setText();
    viewHolder.detail.setText(item.getDetail());
    viewHolder.repeat.setText(item.getRepeat());

    return convertView;
  }

  @Override
  public boolean isChildSelectable(int i, int i1) {
    return true;
  }
}
