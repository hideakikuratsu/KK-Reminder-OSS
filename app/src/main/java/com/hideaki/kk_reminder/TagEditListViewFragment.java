package com.hideaki.kk_reminder;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static com.hideaki.kk_reminder.UtilClass.getPxFromDp;
import static com.hideaki.kk_reminder.UtilClass.setCursorDrawableColor;
import static java.util.Objects.requireNonNull;

public class TagEditListViewFragment extends Fragment implements View.OnClickListener {

  static final String TAG = TagEditListViewFragment.class.getSimpleName();
  private MainActivity activity;
  private ActionBar actionBar;
  private MenuItem editItem;
  private MenuItem sortItem;
  private View footer;

  public static TagEditListViewFragment newInstance() {

    return new TagEditListViewFragment();
  }

  @Override
  public void onAttach(@NonNull Context context) {

    super.onAttach(context);
    activity = (MainActivity)context;
    if(activity.generalSettings != null) {
      int order = activity.order;
      TagEditListAdapter.order = order;
      if(order == 0 || order == 1 || order == 4) {
        TagEditListAdapter.checkedItemId = MainEditFragment.item.getWhichTagBelongs();
      }
      else if(order == 3) {
        TagEditListAdapter.checkedItemId = MainEditFragment.list.getWhichTagBelongs();
      }
    }
    else {
      FragmentManager manager = requireNonNull(activity.getSupportFragmentManager());
      manager
        .beginTransaction()
        .remove(this)
        .commit();
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    this.setHasOptionsMenu(true);
  }

  @Nullable
  @Override
  public View onCreateView(
    @NonNull LayoutInflater inflater,
    @Nullable ViewGroup container,
    Bundle savedInstanceState
  ) {

    View view = inflater.inflate(R.layout.listview, container, false);
    if(activity.isDarkMode) {
      view.setBackgroundColor(activity.backgroundMaterialDarkColor);
    }
    else {
      view.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.background_light));
    }
    view.setFocusableInTouchMode(true);
    view.requestFocus();
    view.setOnKeyListener((v, keyCode, event) -> {

      if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
        ColorPickerListAdapter.isFromListTagEdit = false;
        if(TagEditListAdapter.isEditing) {
          new AlertDialog.Builder(activity)
            .setTitle(R.string.is_editing_title)
            .setMessage(R.string.is_editing_message)
            .show();

          return true;
        }
        else if(TagEditListAdapter.isSorting) {
          new AlertDialog.Builder(activity)
            .setTitle(R.string.is_sorting_title)
            .setMessage(R.string.is_sorting_message)
            .show();

          return true;
        }
      }

      return false;
    });

    TagEditListAdapter.isFirst = true;
    activity.listView = view.findViewById(R.id.listView);
    activity.listView.setDragListener(activity.tagEditListAdapter.myDragListener);
    footer = View.inflate(activity, R.layout.tag_list_footer, null);
    if(activity.listView.getFooterViewsCount() == 0 && !TagEditListAdapter.isEditing
      && !TagEditListAdapter.isSorting) {
      activity.listView.addFooterView(footer);
    }
    ConstraintLayout addTagItem = footer.findViewById(R.id.add_tag_item);
    addTagItem.setOnClickListener(this);
    activity.listView.setAdapter(activity.tagEditListAdapter);

    Toolbar toolbar = activity.findViewById(R.id.toolbar_layout);
    activity.setSupportActionBar(toolbar);
    actionBar = activity.getSupportActionBar();
    requireNonNull(actionBar);

    activity.drawerToggle.setDrawerIndicatorEnabled(false);
    actionBar.setHomeAsUpIndicator(activity.upArrow);
    actionBar.setDisplayHomeAsUpEnabled(!TagEditListAdapter.isEditing);
    actionBar.setTitle(R.string.tag);

    return view;
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.tag_edit_menu, menu);

    // 編集メニューの実装
    editItem = menu.findItem(R.id.edit);
    Drawable drawable = ContextCompat.getDrawable(activity, R.drawable.ic_edit_24dp);
    requireNonNull(drawable);
    drawable = drawable.mutate();
    drawable.setColorFilter(new PorterDuffColorFilter(
      activity.menuItemColor,
      PorterDuff.Mode.SRC_IN
    ));
    editItem.setIcon(drawable);

    // ソートメニューの実装
    sortItem = menu.findItem(R.id.sort);
    drawable = ContextCompat.getDrawable(activity, R.drawable.ic_sort_24dp);
    requireNonNull(drawable);
    drawable = drawable.mutate();
    drawable.setColorFilter(new PorterDuffColorFilter(
      activity.menuItemColor,
      PorterDuff.Mode.SRC_IN
    ));
    sortItem.setIcon(drawable);
    sortItem.setVisible(!TagEditListAdapter.isEditing);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    int itemId = item.getItemId();
    if(itemId == R.id.edit) {
      TagEditListAdapter.isEditing = !TagEditListAdapter.isEditing;
      if(TagEditListAdapter.isEditing) {
        activity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        actionBar.setDisplayHomeAsUpEnabled(false);
        sortItem.setVisible(false);
        activity.listView.removeFooterView(footer);
      }
      else {
        activity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        actionBar.setDisplayHomeAsUpEnabled(true);
        sortItem.setVisible(true);
        if(activity.listView.getFooterViewsCount() == 0) {
          activity.listView.addFooterView(footer);
        }
      }
      activity.tagEditListAdapter.notifyDataSetChanged();
      return true;
    }
    else if(itemId == R.id.sort) {
      TagEditListAdapter.isSorting = !TagEditListAdapter.isSorting;
      if(TagEditListAdapter.isSorting) {
        activity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        actionBar.setDisplayHomeAsUpEnabled(false);
        editItem.setVisible(false);
        activity.listView.removeFooterView(footer);
      }
      else {
        activity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        actionBar.setDisplayHomeAsUpEnabled(true);
        editItem.setVisible(true);
        if(activity.listView.getFooterViewsCount() == 0) {
          activity.listView.addFooterView(footer);
        }

        int size = TagEditListAdapter.tagList.size();
        boolean isUpdated = false;
        for(int i = 0; i < size; i++) {
          TagAdapter tag = TagEditListAdapter.tagList.get(i);
          if(tag.getOrder() != i) {
            tag.setOrder(i);
            isUpdated = true;
          }
        }

        if(isUpdated) {
          activity.generalSettings.setTagList(new ArrayList<>(TagEditListAdapter.tagList));
          activity.updateSettingsDB();
        }
      }

      return true;
    }
    else if(itemId == android.R.id.home) {
      ColorPickerListAdapter.isFromListTagEdit = false;
      FragmentManager manager = requireNonNull(activity.getSupportFragmentManager());
      manager.popBackStack();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onClick(View v) {

    // ダイアログに表示するEditTextの設定
    LinearLayout linearLayout = new LinearLayout(activity);
    linearLayout.setOrientation(LinearLayout.VERTICAL);
    final EditText editText = new EditText(activity);
    setCursorDrawableColor(activity, editText);
    editText.getBackground().mutate().setColorFilter(new PorterDuffColorFilter(
      activity.accentColor,
      PorterDuff.Mode.SRC_IN
    ));
    editText.setHint(R.string.tag_hint);
    editText.setLayoutParams(new LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.MATCH_PARENT,
      LinearLayout.LayoutParams.WRAP_CONTENT
    ));
    linearLayout.addView(editText);
    int paddingPx = getPxFromDp(activity, 20);
    linearLayout.setPadding(paddingPx, 0, paddingPx, 0);

    final AlertDialog dialog = new AlertDialog.Builder(activity)
      .setTitle(R.string.add_tag)
      .setView(linearLayout)
      .setPositiveButton(R.string.add, (dialog1, which) -> {

        String name = editText.getText().toString();
        if(name.isEmpty()) {
          name = getString(R.string.default_tag);
        }
        TagAdapter tag = new TagAdapter();
        tag.setName(name);
        activity.generalSettings.addTag(1, tag);
        List<TagAdapter> tagAdapterList = activity.generalSettings.getTagList();
        int size = tagAdapterList.size();
        for(int i = 0; i < size; i++) {
          tagAdapterList.get(i).setOrder(i);
        }
        TagEditListAdapter.tagList = new ArrayList<>(tagAdapterList);
        activity.tagEditListAdapter.notifyDataSetChanged();
        activity.updateSettingsDB();
      })
      .setNeutralButton(R.string.cancel, (dialog12, which) -> {

      })
      .create();

    dialog.setOnShowListener(dialogInterface -> {

      dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(activity.accentColor);
      dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(activity.accentColor);
    });

    dialog.show();

    // ダイアログ表示時にソフトキーボードを自動で表示
    editText.setOnFocusChangeListener((v1, hasFocus) -> {

      if(hasFocus) {
        Window dialogWindow = dialog.getWindow();
        requireNonNull(dialogWindow);

        dialogWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
      }
    });
    editText.requestFocus();
  }
}
