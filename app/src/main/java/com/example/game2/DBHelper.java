package com.example.game2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "game_stats.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @SuppressLint("SQLiteString")
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE game_statistics (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "start_time INTEGER," +
                        "duration INTEGER," +
                        "score INTEGER)"
        );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS game_statistics");
        onCreate(db);
    }

    public void clearAllGameStats() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("game_statistics", null, null);
        db.close();
    }

    public List<GameStat> getAllGameStats() {
        List<GameStat> gameStats = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query("game_statistics", null, null, null, null, null, "score DESC");
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") long startTime = cursor.getLong(cursor.getColumnIndex("start_time"));
                @SuppressLint("Range") long duration = cursor.getLong(cursor.getColumnIndex("duration"));
                @SuppressLint("Range") int score = cursor.getInt(cursor.getColumnIndex("score"));
                gameStats.add(new GameStat(startTime, duration, score));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return gameStats;
    }

}