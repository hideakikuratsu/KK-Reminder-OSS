package com.example.hideaki.reminder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyListAdapter extends BaseAdapter implements Filterable {

  static List<Item> itemList;
  private Context context;
  private MainActivity activity;

  public MyListAdapter(List<Item> itemList, Context context) {

    MyListAdapter.itemList = itemList;
    this.context = context;
    this.activity = (MainActivity)context;
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
        itemList = activity.getNonScheduledItem(MyDatabaseHelper.TODO_TABLE);

        List<Item> filteredItem = new ArrayList<>();
        for(Item item : itemList) {
          if(item.getDetail() != null) {
            String detail = item.getDetail();

            if(!is_upper) {
              detail = detail.toLowerCase();
            }

            Pattern pattern = Pattern.compile(constraint.toString());
            Matcher matcher = pattern.matcher(detail);

            if(matcher.find()) {
              filteredItem.add(item);
            }
          }
        }

        FilterResults results = new FilterResults();
        results.count = filteredItem.size();
        results.values = filteredItem;

        return results;
      }

      @Override
      @SuppressWarnings("unchecked")
      protected void publishResults(CharSequence constraint, FilterResults results) {

        itemList = (List<Item>)results.values;

        //リストの表示更新
        notifyDataSetChanged();
      }
    };
  }

  private static class ViewHolder {

    ImageView order_icon;
    TextView detail;
    CheckBox checkBox;
  }

  @Override
  public int getCount() {

    return itemList.size();
  }

  @Override
  public Object getItem(int position) {

    return itemList.get(position);
  }

  @Override
  public long getItemId(int position) {

    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    final ViewHolder viewHolder;

    if(convertView == null) {
      convertView = View.inflate(parent.getContext(), R.layout.non_sheduled_item_layout, null);

      viewHolder = new ViewHolder();
      viewHolder.order_icon = convertView.findViewById(R.id.order_icon);
      viewHolder.detail = convertView.findViewById(R.id.detail);
      viewHolder.checkBox = convertView.findViewById(R.id.checkBox);

      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder)convertView.getTag();
    }

    Item item = (Item)getItem(position);

    viewHolder.detail.setText(item.getDetail());

    return convertView;
  }
}
