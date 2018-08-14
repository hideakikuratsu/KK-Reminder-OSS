package com.example.hideaki.reminder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

  static final String TODO_TABLE = "todo";
  static final String DONE_TABLE = "done";
  static final String DATABASE = "reminder.db";
  static final String CREATE_TODO_TABLE =
      "CREATE TABLE todo(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, item_id INTEGER NOT NULL, serial BLOB NOT NULL)";
  static final String CREATE_DONE_TABLE =
      "CREATE TABLE done(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, item_id INTEGER NOT NULL, serial BLOB NOT NULL)";

  public MyDatabaseHelper(Context context) {
    super(context, DATABASE, null, 1);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(CREATE_TODO_TABLE);
    db.execSQL(CREATE_DONE_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}
}
