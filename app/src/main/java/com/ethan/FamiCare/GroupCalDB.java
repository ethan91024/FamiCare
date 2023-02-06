package com.ethan.FamiCare;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities =GroupCal.class,version = 1)

public abstract class GroupCalDB extends RoomDatabase {
    public abstract GroupCalDoa groupCalDoa();

    private  static final String DATABASE_NAME = "GroupCalDB";
    private static GroupCalDB instance;

    public static GroupCalDB getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context, GroupCalDB.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build();

        return instance;
    }
}
