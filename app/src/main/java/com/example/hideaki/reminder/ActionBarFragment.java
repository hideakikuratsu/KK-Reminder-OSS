package com.example.hideaki.reminder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;


public class ActionBarFragment extends Fragment {

  private SearchView searchView;
  private ToggleButton button;

  public ActionBarFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_search, container, false);
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.reminder_menu, menu);

    //検索メニューの実装
    MenuItem search_item = menu.findItem(R.id.search_item);
    searchView = (SearchView)search_item.getActionView();

    searchView.setIconifiedByDefault(true);
    searchView.setSubmitButtonEnabled(false);

    //検索アイコン押下時の処理
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(String text) {
        if(text == null || text.equals("")) {
          MainActivity.elv.clearTextFilter();
        }
        else {
          MainActivity.elv.setFilterText(text);
        }

        return false;
      }
    });

    //To-doメニューの実装
    MenuItem toggle_button = menu.findItem(R.id.toggle);
    button = (ToggleButton)toggle_button.getActionView();

    button.setTextOff("To-do");
    button.setTextOn("Done");
    button.setChecked(false);
    button.setBackgroundResource(R.drawable.toggle_button);

    //To-doメニューボタン押下時の処理
    button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
          //TODO: Doneデータベースからデータを読み込み、レイアウト表示をDone用に変える
        }
        else {
          //TODO: To-doデータベースからデータを読み込み、レイアウト表示を元に戻す。
        }
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch(item.getItemId()) {
      case R.id.search_item:
        return true;
    }
    return false;
  }
}
