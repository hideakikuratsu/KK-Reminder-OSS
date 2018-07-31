package com.example.hideaki.reminder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

  static final String TODO_TABLE = "todo";
  static final String DONE_TABLE = "done";
  static final String CREATE_TODO_TABLE =
      "create table todo(_id integer primary key autoincrement not null, serial blob not null)";
  static final String CREATE_DONE_TABLE =
      "create table done(_id integer primary key autoincrement not null, serial blob not null)";

  public MyDatabaseHelper(Context context) {
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
