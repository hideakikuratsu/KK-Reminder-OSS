package com.example.hideaki.reminder;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyListAdapter extends BaseAdapter implements Filterable {

  static List<Item> itemList;
  private static long has_panel; //コントロールパネルがvisibleであるItemのid値を保持する
  private boolean is_control_panel_locked;
  private final NonScheduledItemComparator nonScheduledItemComparator = new NonScheduledItemComparator();
  private Context context;
  private MainActivity activity;

  MyListAdapter(List<Item> itemList, Context context) {

    MyListAdapter.itemList = itemList;
    this.context = context;
    this.activity = (MainActivity)context;
  }

  private static class ViewHolder {

    CardView item_card;
    ImageView order_icon;
    TextView detail;
    CheckBox checkBox;
    TableLayout control_panel;
    Item item;
  }

  private class MyOnClickListener implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private int position;
    private Item item;
    private View convertView;
    private ViewHolder viewHolder;

    MyOnClickListener(int position, Item item, View convertView, ViewHolder viewHolder) {

      this.position = position;
      this.item = item;
      this.convertView = convertView;
      this.viewHolder = viewHolder;
    }

    @Override
    public void onClick(View v) {

      activity.actionBarFragment.searchView.clearFocus();
      switch(v.getId()) {
        case R.id.item_card:
          if(viewHolder.control_panel.getVisibility() == View.GONE) {
            has_panel = item.getId();
            if(!is_control_panel_locked) {
              viewHolder.control_panel.setVisibility(View.VISIBLE);
              notifyDataSetChanged();
            }
          }
          else viewHolder.control_panel.setVisibility(View.GONE);
          break;
        case R.id.edit:
          activity.listView.clearTextFilter();
          activity.showMainEditFragment(item);
          viewHolder.control_panel.setVisibility(View.GONE);
          break;
        case R.id.notes:
          activity.listView.clearTextFilter();
          activity.showNotesFragment(item);
          break;
      }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

      if(isChecked) {

        viewHolder.checkBox.jumpDrawablesToCurrentState();
        activity.actionBarFragment.searchView.clearFocus();
        itemList.remove(position);
        activity.deleteDB(item, MyDatabaseHelper.TODO_TABLE);

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

        Snackbar.make(convertView, context.getResources().getString(R.string.complete), Snackbar.LENGTH_LONG)
            .addCallback(new Snackbar.Callback() {
              @Override
              public void onShown(Snackbar sb) {

                super.onShown(sb);
                if(viewHolder.control_panel.getVisibility() == View.VISIBLE) {
                  viewHolder.control_panel.setVisibility(View.GONE);
                }
                is_control_panel_locked = true;
              }

              @Override
              public void onDismissed(Snackbar transientBottomBar, int event) {

                super.onDismissed(transientBottomBar, event);
                is_control_panel_locked = false;
              }
            })
            .setAction(R.string.undo, new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                itemList.add(item);
                Collections.sort(itemList, nonScheduledItemComparator);
                notifyDataSetChanged();

                activity.insertDB(item, MyDatabaseHelper.TODO_TABLE);
              }
            })
            .show();
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
      viewHolder.item_card = convertView.findViewById(R.id.item_card);
      viewHolder.order_icon = convertView.findViewById(R.id.order_icon);
      viewHolder.detail = convertView.findViewById(R.id.detail);
      viewHolder.checkBox = convertView.findViewById(R.id.checkBox);
      viewHolder.control_panel = convertView.findViewById(R.id.control_panel);
      viewHolder.item = (Item)getItem(position);

      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder)convertView.getTag();
    }

    Item item = (Item)getItem(position);
    MyOnClickListener listener = new MyOnClickListener(position, item, convertView, viewHolder);

    viewHolder.detail.setText(item.getDetail());

    viewHolder.item_card.setOnClickListener(listener);
    viewHolder.checkBox.setOnCheckedChangeListener(listener);

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
    if(item.getId() != has_panel && viewHolder.control_panel.getVisibility() == View.VISIBLE
        && viewHolder.item.getId() != has_panel) {
      viewHolder.control_panel.setVisibility(View.GONE);
    }

    //チェックが入っている場合、チェックを外す
    if(viewHolder.checkBox.isChecked()) {
      viewHolder.checkBox.setChecked(false);
      viewHolder.checkBox.jumpDrawablesToCurrentState();
    }

    return convertView;
  }
}
