package com.hideaki.kk_reminder;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
  private List<NonScheduledList> filteredLists;

  ManageListAdapter(List<NonScheduledList> nonScheduledLists, MainActivity activity) {

    ManageListAdapter.nonScheduledLists = nonScheduledLists;
    this.activity = activity;
    has_panel = 0;
    dragListener = new DragListener();
    is_sorting = false;
  }

  private static class ViewHolder {

    LinearLayout linearLayout;
    CardView list_card;
    ImageView order_icon;
    ImageView list_icon;
    TextView detail;
    TextView edit;
    ImageView tagPallet;
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
        case R.id.list_card: {
          if(viewHolder.edit.getVisibility() == View.GONE) {
            has_panel = list.getId();
            viewHolder.edit.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
          }
          else {
            has_panel = 0;
            viewHolder.edit.setVisibility(View.GONE);
          }
          break;
        }
        case R.id.edit: {
          activity.listView.clearTextFilter();
          activity.showMainEditFragmentForList(list, ManageListViewFragment.TAG);
          has_panel = 0;
          viewHolder.edit.setVisibility(View.GONE);
          break;
        }
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
        if(activity.actionBarFragment.checked_tag == -1) {
          nonScheduledLists = new ArrayList<>(activity.generalSettings.getNonScheduledLists());
        }
        else {
          nonScheduledLists = activity.actionBarFragment.nonScheduledLists;
        }

        filteredLists = new ArrayList<>();
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
      viewHolder.linearLayout = convertView.findViewById(R.id.linearLayout);
      viewHolder.list_card = convertView.findViewById(R.id.list_card);
      viewHolder.order_icon = convertView.findViewById(R.id.order_icon);
      viewHolder.list_icon = convertView.findViewById(R.id.list_icon);
      viewHolder.detail = convertView.findViewById(R.id.detail);
      viewHolder.edit = convertView.findViewById(R.id.edit);
      viewHolder.tagPallet = convertView.findViewById(R.id.tag_pallet);

      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder)convertView.getTag();
    }

    //現在のビュー位置でのlistの取得とリスナーの初期化
    NonScheduledList list = (NonScheduledList)getItem(position);
    MyOnClickListener listener = new MyOnClickListener(list, viewHolder);

    //各リスナーの設定
    viewHolder.linearLayout.setOnClickListener(null);
    viewHolder.list_card.setOnClickListener(listener);
    viewHolder.edit.setOnClickListener(listener);

    //各種表示処理
    viewHolder.detail.setText(list.getTitle());
    if(list.getWhich_tag_belongs() == 0) {
      viewHolder.tagPallet.setVisibility(View.GONE);
    }
    else {
      viewHolder.tagPallet.setVisibility(View.VISIBLE);
      int color = activity.generalSettings.getTagById(list.getWhich_tag_belongs()).getPrimary_color();
      if(color != 0) {
        viewHolder.tagPallet.setColorFilter(color);
      }
      else {
        viewHolder.tagPallet.setColorFilter(ContextCompat.getColor(activity, R.color.icon_gray));
      }
    }

    //ある子ビューでコントロールパネルを出したとき、他の子ビューのコントロールパネルを閉じる
    if(viewHolder.edit.getVisibility() == View.VISIBLE && list.getId() != has_panel) {
      viewHolder.edit.setVisibility(View.GONE);
    }
    else if(viewHolder.edit.getVisibility() == View.GONE && list.getId() == has_panel) {
      viewHolder.edit.setVisibility(View.VISIBLE);
    }

    //パレットの色を設定
    if(list.getColor() != 0) {
      viewHolder.list_icon.setColorFilter(list.getColor());
    }
    else {
      viewHolder.list_icon.setColorFilter(ContextCompat.getColor(activity, R.color.icon_gray));
    }

    if(is_sorting) viewHolder.order_icon.setVisibility(View.VISIBLE);
    else viewHolder.order_icon.setVisibility(View.GONE);

    //並び替え中にドラッグしているアイテムが二重に表示されないようにする
    convertView.setVisibility(position == draggingPosition ? View.INVISIBLE : View.VISIBLE);

    return convertView;
  }
}
