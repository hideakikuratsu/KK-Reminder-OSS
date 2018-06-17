package com.example.hideaki.reminder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

  static final String CREATE_TODO_TABLE =
      "create table todo(_id integer primary key, detail text, date text, repeat text)";
  static final String CREATE_DONE_TABLE =
      "create table done(_id integer primary key, detail text, date text, repeat text)";

  public DatabaseHelper(Context context) {
    super(context, "reminder.db", null, 1);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_TODO_TABLE);
    db.execSQL(CREATE_DONE_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}
