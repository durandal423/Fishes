package com.example.fishes.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardDao {
    private LeaderboardDbHelper dbHelper;

    public LeaderboardDao(Context context) {
        dbHelper = new LeaderboardDbHelper(context);
    }

    // 插入分数
    public void insertScore(String playerName, int score) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LeaderboardDbHelper.COLUMN_PLAYER_NAME, playerName);
        values.put(LeaderboardDbHelper.COLUMN_SCORE, score);
        db.insert(LeaderboardDbHelper.TABLE_NAME, null, values);
        db.close();
    }

    // 查询排行榜（按分数降序，最多前10名）
    public List<PlayerScore> getLeaderboard() {
        List<PlayerScore> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                LeaderboardDbHelper.TABLE_NAME,
                null, null, null, null, null,
                LeaderboardDbHelper.COLUMN_SCORE + " DESC",
                "10"
        );
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(LeaderboardDbHelper.COLUMN_PLAYER_NAME));
            int score = cursor.getInt(cursor.getColumnIndexOrThrow(LeaderboardDbHelper.COLUMN_SCORE));
            list.add(new PlayerScore(name, score));
        }
        cursor.close();
        db.close();
        return list;
    }

    // 分数实体类
    public static class PlayerScore {
        public String playerName;
        public int score;
        public PlayerScore(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }
    }
} 