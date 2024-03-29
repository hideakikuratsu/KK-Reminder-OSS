package com.hideaki.kk_reminder;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class SingleChoiceItemsAdapter extends BaseAdapter {

  private final List<String> items;
  static int checkedPosition;
  private static boolean isFirst;
  private static boolean isManuallyChecked;

  SingleChoiceItemsAdapter(String[] items) {

    this.items = Arrays.asList(items);
    checkedPosition = 0;
    isFirst = true;
  }

  private static class ViewHolder {

    ConstraintLayout item;
    AnimCheckBox checkBox;
    TextView title;
  }

  private class MyOnClickListener
    implements View.OnClickListener, AnimCheckBox.OnCheckedChangeListener {

    private final int position;
    private final ViewHolder viewHolder;

    MyOnClickListener(int position, ViewHolder viewHolder) {

      this.position = position;
      this.viewHolder = viewHolder;
    }

    @Override
    public void onClick(View v) {

      viewHolder.checkBox.setChecked(!viewHolder.checkBox.isChecked());
    }

    @Override
    public void onChange(AnimCheckBox view, boolean checked) {

      if(checked && isManuallyChecked) {
        isFirst = false;
        checkedPosition = position;
        notifyDataSetChanged();
      }
      else if(!checked && isManuallyChecked) {
        isFirst = false;
        notifyDataSetChanged();
      }
    }
  }

  @Override
  public int getCount() {

    return items.size();
  }

  @Override
  public Object getItem(int position) {

    return items.get(position);
  }

  @Override
  public long getItemId(int position) {

    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    final ViewHolder viewHolder;

    if(convertView == null || convertView.getTag() == null) {
      convertView = View.inflate(parent.getContext(), R.layout.single_choice_items_layout, null);

      viewHolder = new ViewHolder();
      viewHolder.item = convertView.findViewById(R.id.item);
      viewHolder.checkBox = convertView.findViewById(R.id.checkBox);
      viewHolder.title = convertView.findViewById(R.id.title);

      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder)convertView.getTag();
    }


    // 現在のビュー位置でのtitleの取得とリスナーの初期化
    String title = (String)getItem(position);
    MyOnClickListener listener = new MyOnClickListener(position, viewHolder);

    // リスナーの設定
    viewHolder.item.setOnClickListener(listener);
    viewHolder.checkBox.setOnCheckedChangeListener(listener);

    // 各種表示処理
    viewHolder.title.setText(title);

    // チェック状態の初期化
    if(position != checkedPosition) {
      isManuallyChecked = false;
      if(isFirst) {
        viewHolder.checkBox.setChecked(false, false);
      }
      else {
        viewHolder.checkBox.setChecked(false);
      }
    }
    else {
      isManuallyChecked = false;
      if(isFirst) {
        viewHolder.checkBox.setChecked(true, false);
      }
      else {
        viewHolder.checkBox.setChecked(true);
      }
    }
    isManuallyChecked = true;

    return convertView;
  }
}
