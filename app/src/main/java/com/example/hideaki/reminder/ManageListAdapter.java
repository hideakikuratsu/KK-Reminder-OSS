package com.example.hideaki.reminder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ManageListAdapter extends BaseAdapter implements Filterable {

  static List<NonScheduledList> nonScheduledLists;
  private static long has_panel; //コントロールパネルがvisibleであるItemのid値を保持する
  private MainActivity activity;

  ManageListAdapter(List<NonScheduledList> nonScheduledLists, Context context) {

    ManageListAdapter.nonScheduledLists = nonScheduledLists;
    this.activity = (MainActivity)context;
  }

  private static class ViewHolder {

    CardView list_card;
    ImageView order_icon;
    ImageView list_icon;
    TextView detail;
    TableLayout control_panel;
  }

  private class MyOnClickListener implements View.OnClickListener {

    private int position;
    private NonScheduledList list;
    private View convertView;
    private ViewHolder viewHolder;

    MyOnClickListener(int position, NonScheduledList list, View convertView, ViewHolder viewHolder) {

      this.position = position;
      this.list = list;
      this.convertView = convertView;
      this.viewHolder = viewHolder;
    }

    @Override
    public void onClick(View v) {

      activity.actionBarFragment.searchView.clearFocus();
      switch(v.getId()) {
        case R.id.list_card:
          if(viewHolder.control_panel.getVisibility() == View.GONE) {
            has_panel = list.getId();
            viewHolder.control_panel.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
          }
          else viewHolder.control_panel.setVisibility(View.GONE);
          break;
        case R.id.edit:
          activity.listView.clearTextFilter();
          activity.showMainEditFragmentForList(list);
          viewHolder.control_panel.setVisibility(View.GONE);
          break;
        case R.id.notes:
          activity.listView.clearTextFilter();
//          activity.showNotesFragment(list);
          break;
      }
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
        nonScheduledLists = activity.generalSettings.getNonScheduledLists();

        List<NonScheduledList> filteredLists = new ArrayList<>();
        for(NonScheduledList list : nonScheduledLists) {
          if(list.getTitle() != null) {
            String detail = list.getTitle();

            if(!is_upper) {
              detail = detail.toLowerCase();
            }

            Pattern pattern = Pattern.compile(constraint.toString());
            Matcher matcher = pattern.matcher(detail);

            if(matcher.find()) {
              filteredLists.add(list);
            }
          }
        }

        FilterResults results = new FilterResults();
        results.count = filteredLists.size();
        results.values = filteredLists;

        return results;
      }

      @Override
      @SuppressWarnings("unchecked")
      protected void publishResults(CharSequence constraint, FilterResults results) {

        nonScheduledLists = (List<NonScheduledList>)results.values;

        //リストの表示更新
        notifyDataSetChanged();
      }
    };
  }

  @Override
  public int getCount() {

    return nonScheduledLists.size();
  }

  @Override
  public Object getItem(int position) {

    return nonScheduledLists.get(position);
  }

  @Override
  public long getItemId(int position) {

    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    final ViewHolder viewHolder;

    if(convertView == null) {
      convertView = View.inflate(parent.getContext(), R.layout.non_scheduled_list_layout, null);

      viewHolder = new ViewHolder();
      viewHolder.list_card = convertView.findViewById(R.id.list_card);
      viewHolder.order_icon = convertView.findViewById(R.id.order_icon);
      viewHolder.list_icon = convertView.findViewById(R.id.list_icon);
      viewHolder.detail = convertView.findViewById(R.id.detail);
      viewHolder.control_panel = convertView.findViewById(R.id.control_panel);

      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder)convertView.getTag();
    }

    NonScheduledList list = (NonScheduledList)getItem(position);
    MyOnClickListener listener = new MyOnClickListener(position, list, convertView, viewHolder);

    viewHolder.detail.setText(list.getTitle());

    //ある子ビューでコントロールパネルを出したとき、他の子ビューのコントロールパネルを閉じる
    if(list.getId() != has_panel && viewHolder.control_panel.getVisibility() == View.VISIBLE) {
      viewHolder.control_panel.setVisibility(View.GONE);
    }

    viewHolder.list_card.setOnClickListener(listener);

    int control_panel_size = viewHolder.control_panel.getChildCount();
    for(int i = 0; i < control_panel_size; i++) {
      TableRow tableRow = (TableRow)viewHolder.control_panel.getChildAt(i);
      int table_row_size = tableRow.getChildCount();
      for(int j = 0; j < table_row_size; j++) {
        TextView panel_item = (TextView)tableRow.getChildAt(j);
        panel_item.setOnClickListener(listener);
      }
    }

    //パレットの色を設定
    if(list.getPrimary_color() != 0) {
      viewHolder.list_icon.setColorFilter(list.getPrimary_color());
    }
    else {
      viewHolder.list_icon.setColorFilter(ContextCompat.getColor(activity, R.color.icon_gray));
    }

    return convertView;
  }
}
