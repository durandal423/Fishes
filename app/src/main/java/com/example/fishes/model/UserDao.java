package com.example.fishes.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDao {
    private UserDbHelper dbHelper;

    public UserDao(Context context) {
        dbHelper = new UserDbHelper(context);
    }

    public static class User {
        public long id;
        public String username;
        public String password;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    // 插入新用户
    public void insertUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDbHelper.COLUMN_USERNAME, user.username);
        values.put(UserDbHelper.COLUMN_PASSWORD, user.password);
        db.insert(UserDbHelper.TABLE_NAME, null, values);
        db.close();
    }

    // 查找用户
    public User findByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                UserDbHelper.COLUMN_ID,
                UserDbHelper.COLUMN_USERNAME,
                UserDbHelper.COLUMN_PASSWORD
        };
        String selection = UserDbHelper.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = { username };

        Cursor cursor = db.query(
                UserDbHelper.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_USERNAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_PASSWORD))
            );
            user.id = cursor.getLong(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_ID));
        }

        cursor.close();
        db.close();
        return user;
    }

    // 验证用户登录
    public User login(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                UserDbHelper.COLUMN_ID,
                UserDbHelper.COLUMN_USERNAME,
                UserDbHelper.COLUMN_PASSWORD
        };
        String selection = UserDbHelper.COLUMN_USERNAME + " = ? AND " + UserDbHelper.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = { username, password };

        Cursor cursor = db.query(
                UserDbHelper.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        User user = null;
        if (cursor.moveToFirst()) {
            user = new User(
                cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_USERNAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_PASSWORD))
            );
            user.id = cursor.getLong(cursor.getColumnIndexOrThrow(UserDbHelper.COLUMN_ID));
        }

        cursor.close();
        db.close();
        return user;
    }
}
