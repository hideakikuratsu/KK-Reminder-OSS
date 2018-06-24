package com.example.hideaki.reminder;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

public class DBAccessor {
  private final SQLiteDatabase sdb;
  private String state_str;
  private SQLiteStatement statement;

  public DBAccessor(Context context) {
    MyDatabaseHelper helper = new MyDatabaseHelper(context);
    this.sdb = helper.getWritableDatabase();
  }

  public void executeInsert(long id, byte[] stream, String table) {
    state_str = "insert into " + table + "(_id, serial) values(?, ?)";
    sdb.beginTransaction();
    try {
      statement = sdb.compileStatement(state_str);
      statement.bindLong(1, id);
      statement.bindBlob(2, stream);

      statement.executeInsert();

      sdb.setTransactionSuccessful();
    }
    catch(SQLException e) {
      e.printStackTrace();
    }
    finally {
      sdb.endTransaction();
      sdb.close();
    }
  }

  public void executeDeleteAll(String table) {
    state_str = "delete from " + table;
    sdb.beginTransaction();
    try {
      statement = sdb.compileStatement(state_str);

      statement.executeUpdateDelete();

      sdb.setTransactionSuccessful();
    }
    catch(SQLException e) {
      e.printStackTrace();
    }
    finally {
      sdb.endTransaction();
      sdb.close();
    }
  }

  public List<byte[]> executeQueryAll(String table) {
    Cursor cursor = null;
    state_str = "select * from " + table;
    try {
      cursor = sdb.rawQuery(state_str, null);
      return readCursor(cursor);
    }
    finally {
      if(cursor != null) cursor.close();
      sdb.close();
    }
  }

  private List<byte[]> readCursor(Cursor cursor) {
    List<byte[]> list = new ArrayList<>();
    int indexSerial;

    indexSerial = cursor.getColumnIndex("serial");

    while(cursor.moveToNext()) {
      list.add(cursor.getBlob(indexSerial));
    }

    return list;
  }
}
