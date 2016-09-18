package com.ani.todo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TodoStore extends SQLiteOpenHelper {
    private static final String TAG = TodoStore.class.getCanonicalName();
    private static TodoStore INSTANCE;

    private static final String DATABASE_NAME = "todo";
    private static final int DATABASE_VERSION = 12;

    public synchronized static TodoStore instance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new TodoStore(context.getApplicationContext());
        }
        return INSTANCE;
    }

    public TodoStore(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE item(" +
                "_id INTEGER PRIMARY KEY," +
                "due_date TEXT," +
                "completed INTEGER," +
                "data BLOB)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS item");
        onCreate(db);
    }

    public void drop() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS item");
    }

    public long insert(TodoItem item) {
        long rowId = -1;
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("due_date", toDateString(item.date()));
            values.put("completed", toInt(item.status()));
            values.put("data", item.toBytes());

            rowId = db.insertOrThrow("item", null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add item to database");
        } finally {
            db.endTransaction();
        }

        return rowId;
    }

    public TodoItem get(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM item WHERE _id = ?",
                new String[]{Long.toString(id)});

        try {
            if (cursor.moveToFirst()) {
                return TodoItem.fromBytes(cursor.getBlob(cursor.getColumnIndexOrThrow("data")));
            } else {
                Log.d(TAG, "Error could not find item in database");
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get item from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return null;
    }

    public Cursor getAll() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM item ORDER BY due_date ASC, completed ASC, _id ASC", null);
    }

    public boolean update(long id, TodoItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("due_date", toDateString(item.date()));
        contentValues.put("completed", toInt(item.status()));
        contentValues.put("data", item.toBytes());

        return db.update("item", contentValues, "_id = ?", new String[]{Long.toString(id)}) == 1;
    }

    public boolean delete(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete("item", "_id = ?", new String[]{Long.toString(id)}) == 1;
    }

    private static String toDateString(Date date) {
        Calendar cal = Calendar.getInstance();

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        formatter.setTimeZone(cal.getTimeZone());
        return formatter.format(date);
    }

    private static int toInt(TodoItem.Status status) {
        return status == TodoItem.Status.COMPLETED ? 1 : 0;
    }
}
