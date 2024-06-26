package com.hideaki.kk_reminder;

import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ActionMode;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import static com.hideaki.kk_reminder.UtilClass.LINE_SEPARATOR;
import static com.hideaki.kk_reminder.UtilClass.LOCALE;
import static com.hideaki.kk_reminder.UtilClass.getNow;
import static java.util.Objects.requireNonNull;

public class DoneListAdapter extends BaseAdapter implements Filterable {

  static List<ItemAdapter> itemList;
  private final MainActivity activity;
  ActionMode actionMode = null;
  static int checkedItemNum;
  private static boolean isManuallyChecked;
  private List<ItemAdapter> filteredItem;
  static int order;
  static boolean isScrolling;

  DoneListAdapter(MainActivity activity) {

    this.activity = activity;
  }

  private static class ViewHolder {

    CardView itemCard;
    ImageView orderIcon;
    ImageView clockImage;
    TextView time;
    TextView detail;
    TextView repeat;
    AnimCheckBox checkBox;
    ImageView tagPallet;
  }

  private class MyOnClickListener
    implements View.OnClickListener, AnimCheckBox.OnCheckedChangeListener,
    View.OnLongClickListener, ActionMode.Callback {

    private final int position;
    private final ItemAdapter item;
    private final ViewHolder viewHolder;
    private int whichList;
    private List<ItemAdapter> itemListToMove;
    Calendar tmp;

    MyOnClickListener(int position, ItemAdapter item, ViewHolder viewHolder) {

      this.position = position;
      this.item = item;
      this.viewHolder = viewHolder;
    }

    @Override
    public void onClick(View v) {

      activity.actionBarFragment.searchView.clearFocus();
      int id = v.getId();
      if(id == R.id.child_card || id == R.id.item_card) {
        if(actionMode == null) {
          final String[] items = new String[3];
          if(order == 0) {
            Calendar now = getNow();

            tmp = (Calendar)now.clone();
            tmp.set(Calendar.HOUR_OF_DAY, item.getDate().get(Calendar.HOUR_OF_DAY));
            tmp.set(Calendar.MINUTE, item.getDate().get(Calendar.MINUTE));
            if(tmp.before(now)) {
              tmp.add(Calendar.DAY_OF_MONTH, 1);
            }
            if(LOCALE.equals(Locale.JAPAN)) {
              items[0] = DateFormat.format("yyyy年M月d日(E) k時m分", tmp).toString() +
                  "に" + activity.getString(R.string.recycle_task);
            }
            else {
              items[0] = activity.getString(R.string.recycle_task) +
                  " on " + DateFormat.format("E, MMM d, yyyy 'at' h:mma", tmp).toString();
            }
          }
          else if(order == 1) {
            items[0] = activity.getString(R.string.recycle_task);
          }
          items[1] = activity.getString(R.string.recycle_and_edit_task);
          items[2] = activity.getString(R.string.delete_task);

          final SingleChoiceItemsAdapter adapter = new SingleChoiceItemsAdapter(items);
          final AlertDialog dialog = new AlertDialog.Builder(activity)
              .setTitle(R.string.done_task_click_title)
              .setSingleChoiceItems(adapter, 0, (dialog13, which) -> {})
              .setPositiveButton(R.string.determine, (dialog12, which) -> {

                whichList = SingleChoiceItemsAdapter.checkedPosition;

                itemList.remove(position);
                notifyDataSetChanged();
                activity.deleteDB(item, MyDatabaseHelper.DONE_TABLE);

                if(whichList == 0) {
                  if(order == 0) {
                    item.setDate((Calendar)tmp.clone());
                    item.setAlarmStopped(false);
                    activity.setAlarm(item);
                  }
                  activity.insertDB(item, MyDatabaseHelper.TODO_TABLE);
                }
                else if(whichList == 1) {
                  activity.insertDB(item, MyDatabaseHelper.TODO_TABLE);
                  activity.showMainEditFragment(item);
                }
              })
              .setNeutralButton(R.string.cancel, (dialog1, which) -> {})
              .create();

          dialog.setOnShowListener(dialogInterface -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.accentColor);
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(activity.accentColor);
          });

          dialog.show();
        }
        else {
          viewHolder.checkBox.setChecked(!viewHolder.checkBox.isChecked());
        }
      }
    }

    @Override
    public void onChange(AnimCheckBox view, boolean checked) {

      if(checked && actionMode != null && isManuallyChecked) {
        item.setSelected(true);
        notifyDataSetChanged();
        checkedItemNum++;
        actionMode.setTitle(Integer.toString(checkedItemNum));
      }
      else if(actionMode != null && isManuallyChecked) {
        item.setSelected(false);
        notifyDataSetChanged();
        checkedItemNum--;
        actionMode.setTitle(Integer.toString(checkedItemNum));
        if(checkedItemNum == 0) {
          actionMode.finish();
        }
      }
    }

    @Override
    public boolean onLongClick(View v) {

      if(actionMode != null) {

        viewHolder.checkBox.setChecked(!viewHolder.checkBox.isChecked());

      }
      else {
        actionMode = activity.startSupportActionMode(this);
        viewHolder.checkBox.setChecked(true);
      }
      return true;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

      actionMode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);

      // ActionMode時のみツールバーとステータスバーの色を設定
      Window window = activity.getWindow();
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(ContextCompat.getColor(activity, R.color.darkerGrey));

      return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {

      MenuItem cloneItem = menu.findItem(R.id.clone);
      cloneItem.setVisible(false);

      return true;
    }

    @Override
    public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem) {

      int itemId = menuItem.getItemId();
      if(itemId == R.id.delete) {
        itemListToMove = new ArrayList<>();
        for(ItemAdapter item : itemList) {
          if(item.isSelected()) {
            itemListToMove.add(0, item);
          }
        }

        String message = activity.getResources().getQuantityString(R.plurals.cab_delete_message,
            itemListToMove.size(), itemListToMove.size()
        ) + " (" + activity.getString(R.string.delete_dialog_message) + ")";
        final AlertDialog dialog = new AlertDialog.Builder(activity)
            .setTitle(R.string.cab_delete)
            .setMessage(message)
            .setPositiveButton(R.string.delete, (dialog16, which) -> {

              for(ItemAdapter item : itemListToMove) {
                activity.deleteDB(item, MyDatabaseHelper.DONE_TABLE);
              }
              itemList = activity.getDoneItem();
              notifyDataSetChanged();

              actionMode.finish();
            })
            .setNeutralButton(R.string.cancel, (dialog15, which) -> {

            })
            .create();

        dialog.setOnShowListener(dialogInterface -> {

          dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.accentColor);
          dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(activity.accentColor);
        });

        dialog.show();

        return true;
      }
      else if(itemId == R.id.move_task_between_list) {
        itemListToMove = new ArrayList<>();
        for(ItemAdapter item : itemList) {
          if(item.isSelected()) {
            itemListToMove.add(0, item);
          }
        }

        String message =
            activity.getResources().getQuantityString(R.plurals.cab_recycle_task_message,
                itemListToMove.size(), itemListToMove.size()
            );
        final AlertDialog dialog = new AlertDialog.Builder(activity)
            .setTitle(R.string.cab_recycle_task_title)
            .setMessage(message)
            .setPositiveButton(R.string.determine, (dialog13, which) -> {

              if(order == 0) {
                for(ItemAdapter item : itemListToMove) {

                  item.setSelected(false);

                  activity.insertDB(item, MyDatabaseHelper.TODO_TABLE);
                  activity.deleteDB(item, MyDatabaseHelper.DONE_TABLE);
                }
              }
              else if(order == 1) {
                for(ItemAdapter item : itemListToMove) {

                  item.setSelected(false);

                  MyListAdapter.itemList.add(0, item);
                  activity.insertDB(item, MyDatabaseHelper.TODO_TABLE);
                  activity.deleteDB(item, MyDatabaseHelper.DONE_TABLE);
                }

                int size = MyListAdapter.itemList.size();
                for(int i = 0; i < size; i++) {
                  ItemAdapter item = MyListAdapter.itemList.get(i);
                  item.setOrder(i);
                  activity.updateDB(item, MyDatabaseHelper.TODO_TABLE);
                }
              }

              itemList = activity.getDoneItem();
              notifyDataSetChanged();

              actionMode.finish();
            })
            .setNeutralButton(R.string.cancel, (dialog14, which) -> {

            })
            .create();

        dialog.setOnShowListener(dialogInterface -> {

          dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.accentColor);
          dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(activity.accentColor);
        });

        dialog.show();

        return true;
      }
      else if(itemId == R.id.share) {
        itemListToMove = new ArrayList<>();
        for(ItemAdapter item : itemList) {
          if(item.isSelected()) {
            itemListToMove.add(0, item);
          }
        }

        String message = activity.getResources().getQuantityString(R.plurals.cab_share_message,
            itemListToMove.size(), itemListToMove.size()
        );
        final AlertDialog dialog = new AlertDialog.Builder(activity)
            .setTitle(R.string.cab_share)
            .setMessage(message)
            .setPositiveButton(R.string.yes, (dialog12, which) -> {

              for(ItemAdapter item : itemListToMove) {
                String sendContent = "";
                if(order == 0) {
                  String dueDate;
                  if(LOCALE.equals(Locale.JAPAN)) {
                    dueDate = (String)DateFormat.format(
                        "yyyy/M/d k:mm", item.getDate()
                    );
                  }
                  else {
                    dueDate = (String)DateFormat.format(
                        "E, MMM d, yyyy 'at' h:mma", item.getDate()
                    );
                  }
                  sendContent = activity.getString(R.string.due_date) + ": "
                      + dueDate
                      + LINE_SEPARATOR
                      + activity.getString(R.string.detail) + ": " + item.getDetail()
                      + LINE_SEPARATOR
                      + activity.getString(R.string.memo) + ": " + item.getNotesString();
                }
                else if(order == 1) {
                  sendContent = activity.getString(R.string.detail) + ": " + item.getDetail()
                      + LINE_SEPARATOR
                      + activity.getString(R.string.memo) + ": " + item.getNotesString();
                }

                Intent intent = new Intent()
                    .setAction(Intent.ACTION_SEND)
                    .setType("text/plain")
                    .putExtra(Intent.EXTRA_TEXT, sendContent);
                activity.startActivity(intent);
              }

              actionMode.finish();
            })
            .setNeutralButton(R.string.cancel, (dialog1, which) -> {

            })
            .create();

        dialog.setOnShowListener(dialogInterface -> {

          dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.accentColor);
          dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(activity.accentColor);
        });

        dialog.show();

        return true;
      }
      actionMode.finish();
      return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

      Window window = activity.getWindow();
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(activity.statusBarColor);

      DoneListAdapter.this.actionMode = null;
      for(ItemAdapter item : itemList) {
        if(item.isSelected()) {
          item.setSelected(false);
        }
      }

      checkedItemNum = 0;
      notifyDataSetChanged();
    }
  }

  @Override
  public Filter getFilter() {

    return new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence constraint) {

        // 入力文字列が大文字を含むかどうか調べる
        boolean isUpper = false;
        for(int i = 0; i < constraint.length(); i++) {
          if(Character.isUpperCase(constraint.charAt(i))) {
            isUpper = true;
            break;
          }
        }

        // 検索処理
        if(activity.actionBarFragment.checkedTag == -1) {
          itemList = activity.getDoneItem();
        }
        else {
          itemList = activity.actionBarFragment.filteredList;
        }

        filteredItem = new ArrayList<>();
        for(ItemAdapter item : itemList) {
          if(item.getDetail() != null) {
            String detail = item.getDetail();

            if(!isUpper) {
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

        itemList = (List<ItemAdapter>)results.values;
        if(itemList == null) {
          itemList = new ArrayList<>();
        }

        // リストの表示更新
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

    if(convertView == null || convertView.getTag() == null) {
      if(order == 0) {
        convertView = View.inflate(parent.getContext(), R.layout.child_layout, null);
      }
      else if(order == 1) {
        convertView = View.inflate(parent.getContext(), R.layout.non_sheduled_item_layout, null);
      }

      viewHolder = new ViewHolder();
      requireNonNull(convertView);
      if(order == 0) {
        viewHolder.clockImage = convertView.findViewById(R.id.clock_image);
        viewHolder.time = convertView.findViewById(R.id.date);
        viewHolder.repeat = convertView.findViewById(R.id.repeat);
        viewHolder.itemCard = convertView.findViewById(R.id.child_card);
      }
      else if(order == 1) {
        viewHolder.itemCard = convertView.findViewById(R.id.item_card);
        viewHolder.orderIcon = convertView.findViewById(R.id.order_icon);
      }
      viewHolder.detail = convertView.findViewById(R.id.detail);
      viewHolder.checkBox = convertView.findViewById(R.id.checkBox);
      viewHolder.tagPallet = convertView.findViewById(R.id.tag_pallet);

      convertView.setTag(viewHolder);
    }
    else {
      viewHolder = (ViewHolder)convertView.getTag();
    }

    // 現在のビュー位置でのitemの取得とリスナーの初期化
    ItemAdapter item = (ItemAdapter)getItem(position);
    MyOnClickListener listener = new MyOnClickListener(position, item, viewHolder);

    // 各リスナーの設定
    viewHolder.itemCard.setOnClickListener(listener);
    viewHolder.checkBox.setOnCheckedChangeListener(listener);

    viewHolder.itemCard.setOnLongClickListener(listener);
    viewHolder.checkBox.setOnLongClickListener(listener);

    // 各種表示処理
    if(activity.isDarkMode) {
      viewHolder.itemCard.setBackgroundColor(activity.backgroundFloatingMaterialDarkColor);
    }
    viewHolder.detail.setText(item.getDetail());
    viewHolder.detail.setTextSize(activity.textSize);
    viewHolder.detail.setTextColor(Color.GRAY);
    if(item.getWhichTagBelongs() == 0) {
      viewHolder.tagPallet.setVisibility(View.GONE);
    }
    else {
      viewHolder.tagPallet.setVisibility(View.VISIBLE);
      viewHolder.tagPallet.setColorFilter(
        activity.generalSettings.getTagById(item.getWhichTagBelongs()).getPrimaryColor()
      );
    }

    // チェックが入っている場合、チェックを外す
    if(viewHolder.checkBox.isChecked() && !item.isSelected()) {
      isManuallyChecked = false;
      viewHolder.checkBox.setChecked(false);
    }
    else if(!viewHolder.checkBox.isChecked() && item.isSelected()) {
      isManuallyChecked = false;
      viewHolder.checkBox.setChecked(true);
    }
    isManuallyChecked = true;

    // 選択モードでない場合、チェックボックスを無効にする
    viewHolder.checkBox.setEnabled(actionMode != null);

    // 個別レイアウト
    if(order == 0) {
      viewHolder.clockImage.setColorFilter(Color.GRAY);

      Calendar now = Calendar.getInstance();
      String setTime;
      if(now.get(Calendar.YEAR) == item.getDate().get(Calendar.YEAR)) {
        if(LOCALE.equals(Locale.JAPAN)) {
          setTime = (String)DateFormat.format("M月d日(E) k:mm", item.getDate());
        }
        else {
          setTime = (String)DateFormat.format("E, MMM d 'at' h:mma", item.getDate());
        }
      }
      else {
        if(LOCALE.equals(Locale.JAPAN)) {
          setTime = (String)DateFormat.format("yyyy年M月d日(E) k:mm", item.getDate());
        }
        else {
          setTime = (String)DateFormat.format("E, MMM d, yyyy 'at' h:mma", item.getDate());
        }
      }
      viewHolder.time.setText(setTime);
      viewHolder.time.setTextColor(Color.GRAY);

      displayRepeat(viewHolder, item);
      viewHolder.repeat.setTextColor(Color.GRAY);
    }
    else if(order == 1) {
      viewHolder.orderIcon.setVisibility(View.GONE);
    }

    // CardViewが横から流れてくるアニメーション
    if(isScrolling && activity.isPlaySlideAnimation) {
      Animation animation = AnimationUtils.loadAnimation(activity, R.anim.listview_motion);
      convertView.startAnimation(animation);
    }

    return convertView;
  }

  // リピートを表示する処理
  private void displayRepeat(ViewHolder viewHolder, ItemAdapter item) {

    String repeatStr = "";
    String extractedStr = null;
    String tmp = item.getDayRepeat().getLabel();
    if(tmp != null && !tmp.isEmpty() && !activity.getString(R.string.none).equals(tmp)) {
      if(!LOCALE.equals(Locale.JAPAN)) {
        repeatStr += "Repeat ";
      }
      repeatStr += tmp;
      int scale = item.getDayRepeat().getScale();
      int template = item.getDayRepeat().getWhichTemplate();
      if(LOCALE.equals(Locale.JAPAN)) {
        if(item.getDayRepeat().getTimeLimit() != null) {
          String targetString = " \\(.*まで\\)$";
          Pattern pattern = Pattern.compile(targetString);
          Matcher matcher = pattern.matcher(repeatStr);
          if(matcher.find()) {
            extractedStr = matcher.group();
            repeatStr = repeatStr.replaceAll(targetString, "");
          }
        }
        if(template > 0 && template < 1 << 5) {
          if(template > 1) {
            repeatStr += "に";
          }
        }
        else if(scale > 1) {
          repeatStr += "に";
        }
      }
    }

    tmp = item.getMinuteRepeat().getLabel();
    if(tmp != null && !tmp.isEmpty() && !activity.getString(R.string.none).equals(tmp)) {
      if(!LOCALE.equals(Locale.JAPAN) && !repeatStr.isEmpty()) {
        repeatStr += " and ";
      }
      repeatStr += tmp;
    }

    String day = item.getDayRepeat().getLabel();
    String minute = item.getMinuteRepeat().getLabel();
    if(LOCALE.equals(Locale.JAPAN) && day != null && !day.isEmpty() &&
        !activity.getString(R.string.none).equals(day)
        &&
        (minute == null || minute.isEmpty() || activity.getString(R.string.none).equals(minute))) {
      repeatStr += "繰り返す";
    }

    if(repeatStr.isEmpty()) {
      viewHolder.repeat.setText(R.string.non_repeat);
    }
    else {
      if(extractedStr != null) {
        repeatStr += extractedStr;
      }
      viewHolder.repeat.setText(repeatStr);
    }
  }
}
