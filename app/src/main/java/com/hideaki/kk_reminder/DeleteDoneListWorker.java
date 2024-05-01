package com.hideaki.kk_reminder;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.hideaki.kk_reminder.UtilClass.ITEM_IDS;

public class DeleteDoneListWorker extends Worker {

  DBAccessor accessor;

  public DeleteDoneListWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {

    super(context, workerParams);
    accessor = new DBAccessor(context, false);
  }

  public void deleteDB(long[] ids, String table) {

    accessor.executeDeleteMany(ids, table);
  }

  @NonNull
  @Override
  public Result doWork() {
    long[] idsToBeDeleted = getInputData().getLongArray(ITEM_IDS);
    deleteDB(idsToBeDeleted, MyDatabaseHelper.DONE_TABLE);
    return Result.success();
  }
}
