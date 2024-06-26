package com.hideaki.kk_reminder;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class DBAccessor {

  private SQLiteDatabase sdb;
  private final MyDatabaseHelper helper;
  private String stateStr;
  private SQLiteStatement statement;

  DBAccessor(Context context, boolean isDirectBootContext) {

    this.helper = MyDatabaseHelper.getInstance(context, isDirectBootContext);
  }

  private void closeDB() {

    if(sdb != null) {
      sdb.close();
      sdb = null;
    }
  }

  void executeInsert(long id, byte[] stream, String table) {

    sdb = helper.getWritableDatabase();
    stateStr = "INSERT INTO " + table + "(item_id, serial) VALUES(?, ?)";

    sdb.beginTransaction();
    try {
      statement = sdb.compileStatement(stateStr);
      statement.bindLong(1, id);
      statement.bindBlob(2, stream);

      statement.executeInsert();

      sdb.setTransactionSuccessful();
    }
    catch(SQLException e) {
      Log.e("DBAccessor#executeInsert", Log.getStackTraceString(e));
    }
    finally {
      sdb.endTransaction();
      closeDB();
    }
  }

  void executeUpdate(long id, byte[] stream, String table) {

    sdb = helper.getWritableDatabase();
    stateStr = "UPDATE " + table + " SET serial = ? WHERE item_id = ?";

    sdb.beginTransaction();
    try {
      statement = sdb.compileStatement(stateStr);
      statement.bindBlob(1, stream);
      statement.bindLong(2, id);

      statement.executeUpdateDelete();

      sdb.setTransactionSuccessful();
    }
    catch(SQLException e) {
      Log.e("DBAccessor#executeUpdate", Log.getStackTraceString(e));
    }
    finally {
      sdb.endTransaction();
      closeDB();
    }
  }

  void executeDeleteMany(long[] ids, String table) {

    sdb = helper.getWritableDatabase();
    String idsStr = Arrays.stream(ids).mapToObj(Long::toString).collect(Collectors.joining("\",\""));
    stateStr = "DELETE FROM " + table + " WHERE item_id IN (\"" + idsStr + "\")";

    sdb.beginTransaction();
    try {
      statement = sdb.compileStatement(stateStr);

      statement.executeUpdateDelete();

      sdb.setTransactionSuccessful();
    }
    catch(SQLException e) {
      Log.e("DBAccessor#executeDeleteMany", Log.getStackTraceString(e));
    }
    finally {
      sdb.endTransaction();
      closeDB();
    }
  }

  void executeDelete(long id, String table) {

    sdb = helper.getWritableDatabase();
    stateStr = "DELETE FROM " + table + " WHERE item_id = ?";

    sdb.beginTransaction();
    try {
      statement = sdb.compileStatement(stateStr);
      statement.bindLong(1, id);

      statement.executeUpdateDelete();

      sdb.setTransactionSuccessful();
    }
    catch(SQLException e) {
      Log.e("DBAccessor#executeDelete", Log.getStackTraceString(e));
    }
    finally {
      sdb.endTransaction();
      closeDB();
    }
  }

  List<byte[]> executeQueryAll(String table) {

    sdb = helper.getReadableDatabase();

    stateStr = "SELECT * FROM " + table;
    try(Cursor cursor = sdb.rawQuery(stateStr, null)) {
      return readCursorBySerial(cursor);
    }
    finally {
      closeDB();
    }
  }

  byte[] executeQueryById(long id, String table) {

    sdb = helper.getReadableDatabase();

    stateStr = "SELECT * FROM " + table + " WHERE item_id = ?";
    try(Cursor cursor = sdb.rawQuery(stateStr, new String[]{Long.toString(id)})) {
      return readCursorById(cursor);
    }
    finally {
      closeDB();
    }
  }

  private byte[] readCursorById(Cursor cursor) {

    int indexSerial;

    indexSerial = cursor.getColumnIndex("serial");

    if(cursor.moveToNext()) {
      return cursor.getBlob(indexSerial);
    }
    else {
      return null;
    }
  }

  private List<byte[]> readCursorBySerial(Cursor cursor) {

    List<byte[]> list = new ArrayList<>();
    int indexSerial;

    indexSerial = cursor.getColumnIndex("serial");

    while(cursor.moveToNext()) {
      list.add(cursor.getBlob(indexSerial));
    }

    return list;
  }
}
