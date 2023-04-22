package com.ethan.FamiCare.Diary;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Diary.class, version = 1)

public abstract class DiaryDB extends RoomDatabase {

    public abstract DiaryDoa diaryDoa();

    private static final String DATABASE_NAME = "DiaryDB";
    private static DiaryDB instance;

    public static DiaryDB getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context, DiaryDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();

        return instance;
    }
}