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
  DragListener dragListener;
  private int draggingPosition = -1;
  static boolean is_sorting;

  ManageListAdapter(List<NonScheduledList> nonScheduledLists, Context context) {

    ManageListAdapter.nonScheduledLists = nonScheduledLists;
    this.activity = (MainActivity)context;
    has_panel = 0;
    dragListener = new DragListener();
    is_sorting = false;
  }

  private static class ViewHolder {

    CardView list_card;
    ImageView order_icon;
    ImageView list_icon;
    TextView detail;
    TableLayout control_panel;
  }

  private class MyOnClickListener implements View.OnClickListener {

    private NonScheduledList list;
    private ViewHolder viewHolder;

    MyOnClickListener(NonScheduledList list, ViewHolder viewHolder) {

      this.list = list;
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
          else {
            has_panel = 0;
            viewHolder.control_panel.setVisibility(View.GONE);
          }
          break;
        case R.id.edit:
          activity.listView.clearTextFilter();
          activity.showMainEditFragmentForList(list);
          has_panel = 0;
          viewHolder.control_panel.setVisibility(View.GONE);
          break;
        case R.id.notes:
          activity.listView.clearTextFilter();
//          activity.showNotesFragment(list);
          break;
      }
    }
  }

  class DragListener extends SortableListView.SimpleDragListener {

    @Override
    public int onStartDrag(int position) {

      draggingPosition = position;
      notifyDataSetChanged();

      return position;
    }

    @Override
    public int onDuringDrag(int positionFrom, int positionTo) {

      if(positionFrom < 0 || positionTo < 0 || positionFrom == positionTo) {
        return positionFrom;
      }

      NonScheduledList list = nonScheduledLists.get(positionFrom);
      nonScheduledLists.remove(positionFrom);
      nonScheduledLists.add(positionTo, list);

      draggingPosition = positionTo;
      notifyDataSetChanged();

      return positionTo;
    }

    @Override
    public boolean onStopDrag(int positionFrom, int positionTo) {

      draggingPosition = -1;
      notifyDataSetChanged();

      return super.onStopDrag(positionFrom, positionTo);
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
    MyOnClickListener listener = new MyOnClickListener(list, viewHolder);

    viewHolder.detail.setText(list.getTitle());

    if(is_sorting) viewHolder.order_icon.setVisibility(View.VISIBLE);
    else viewHolder.order_icon.setVisibility(View.GONE);

    //各リスナーの設定
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

    //ある子ビューでコントロールパネルを出したとき、他の子ビューのコントロールパネルを閉じる
    if(viewHolder.control_panel.getVisibility() == View.VISIBLE && list.getId() != has_panel) {
      viewHolder.control_panel.setVisibility(View.GONE);
    }
    else if(viewHolder.control_panel.getVisibility() == View.GONE && list.getId() == has_panel) {
      viewHolder.control_panel.setVisibility(View.VISIBLE);
    }

    //パレットの色を設定
    if(list.getPrimary_color() != 0) {
      viewHolder.list_icon.setColorFilter(list.getPrimary_color());
    }
    else {
      viewHolder.list_icon.setColorFilter(ContextCompat.getColor(activity, R.color.icon_gray));
    }

    //並び替え中にドラッグしているアイテムが二重に表示されないようにする
    convertView.setVisibility(position == draggingPosition ? View.INVISIBLE : View.VISIBLE);

    return convertView;
  }
}
