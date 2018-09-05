package com.example.hideaki.reminder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import petrov.kristiyan.colorpicker.ColorPicker;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class ColorPickerListAdapter extends BaseAdapter {

  private static boolean manually_checked;
  private final List<String> color_name_lists;
  private static int checked_position; //チェックの入っている項目のpositionを保持する
  private MainActivity activity;

  ColorPickerListAdapter(Context context) {

    this.activity = (MainActivity)context;
    color_name_lists = new ArrayList<>(Arrays.asList(activity.getResources().getStringArray(R.array.colors_array)));
  }

  private static class ViewHolder {

    CardView color_list_card;
    CheckBox checkBox;
    TextView color_name;
    ImageView pallet;
  }

  private class MyOnClickListener implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private int position;
    private ViewHolder viewHolder;

    MyOnClickListener(int position, ViewHolder viewHolder) {

      this.position = position;
      this.viewHolder = viewHolder;
    }

    @Override
    public void onClick(View v) {

      if(!viewHolder.checkBox.isChecked()) {

        viewHolder.checkBox.setChecked(true);
      }
      else {
        viewHolder.checkBox.setChecked(false);
        MainEditFragment.list.setColor(0);
        MainEditFragment.list.setColor_order_group(-1);
        checked_position = -1;
        notifyDataSetChanged();
      }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

      if(isChecked && manually_checked) {

        checked_position = position;
        MainEditFragment.list.setColor_order_group(checked_position);
        MainEditFragment.list.setColor_order_child(5);
        notifyDataSetChanged();

        Resources res = activity.getResources();
        List<Integer> colorsList = new ArrayList<>();
        TypedArray typedArraysOfArray = res.obtainTypedArray(R.array.primaryColorsArray);

        int id = typedArraysOfArray.getResourceId(checked_position, -1);
        checkArgument(id != -1);
        TypedArray typedArray = res.obtainTypedArray(id);

        int size = typedArray.length();
        for(int i = 0; i < size; i++) {
          int color = typedArray.getColor(i, -1);

          checkArgument(color != -1);
          colorsList.add(color);
        }
        int default_color = typedArray.getColor(MainEditFragment.list.getColor_order_child(), -1);
        checkArgument(default_color != -1);

        typedArray.recycle();
        typedArraysOfArray.recycle();

        int[] colors_array = new int[size];
        for(int i = 0; i < size; i++) {
          colors_array[i] = colorsList.get(i);
        }

        MainEditFragment.list.setColor(default_color);

        new ColorPicker(activity)
            .setColors(colors_array)
            .setTitle(activity.getString(R.string.pick_color_description))
            .setDefaultColorButton(default_color)
            .setRoundColorButton(true)
            .disableDefaultButtons(true)
            .setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
              @Override
              public void setOnFastChooseColorListener(int position, int color) {
                MainEditFragment.list.setColor(color);
                MainEditFragment.list.setColor_order_child(position);
                viewHolder.pallet.setColorFilter(color);
              }

              @Override
              public void onCancel() {}
            })
            .show();
      }
      else if(position == checked_position && manually_checked) {
        MainEditFragment.list.setColor(0);
        MainEditFragment.list.setColor_order_group(-1);
        checked_position = -1;
        notifyDataSetChanged();
      }
    }
  }

  @Override
  public int getCount() {

    return color_name_lists.size();
  }

  @Override
  public Object getItem(int position) {

    return color_name_lists.get(position);
  }

  @Override
  public long getItemId(int position) {

    return position;
  }

  @SuppressLint("ResourceType")
  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    final ViewHolder viewHolder;

    if(convertView == null) {
      convertView = View.inflate(parent.getContext(), R.layout.color_picker_list_layout, null);

      viewHolder = new ViewHolder();
      viewHolder.color_list_card = convertView.findViewById(R.id.color_list_card);
      viewHolder.checkBox = convertView.findViewById(R.id.checkBox);
      viewHolder.color_name = convertView.findViewById(R.id.color_name);
      viewHolder.pallet = convertView.findViewById(R.id.pallet);

      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder)convertView.getTag();
    }

    String color_name = (String)getItem(position);
    MyOnClickListener listener = new MyOnClickListener(position, viewHolder);

    viewHolder.color_name.setText(color_name);

    viewHolder.color_list_card.setOnClickListener(listener);
    viewHolder.checkBox.setOnCheckedChangeListener(listener);

    checked_position = MainEditFragment.list.getColor_order_group();
    if(position != checked_position) {
      viewHolder.checkBox.setChecked(false);
    }
    else {
      manually_checked = false;
      viewHolder.checkBox.setChecked(true);
    }
    manually_checked = true;

    //パレットの色を設定
    Resources res = activity.getResources();
    TypedArray typedArraysOfArray = res.obtainTypedArray(R.array.primaryColorsArray);

    int id = typedArraysOfArray.getResourceId(position, -1);
    checkArgument(id != -1);
    TypedArray typedArray = res.obtainTypedArray(id);

    int color;
    if(position == checked_position) {
      color = typedArray.getColor(MainEditFragment.list.getColor_order_child(), -1);
    }
    else {
      color = typedArray.getColor(5, -1);
    }
    checkArgument(color != -1);

    typedArray.recycle();
    typedArraysOfArray.recycle();

    viewHolder.pallet.setColorFilter(color);

    return convertView;
  }
}
