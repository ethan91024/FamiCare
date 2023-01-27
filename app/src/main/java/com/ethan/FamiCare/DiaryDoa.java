package com.ethan.FamiCare;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface DiaryDoa {

    @Insert
    long insertDiary(Diary diary);

    @Delete
    void deleteDiary(Diary diary);

    @Update
    void updateDiary(Diary diary);

    @Query("SELECT * FROM diaries")
    List<Diary> getDiaries();

    @Query("SELECT * FROM diaries WHERE id LIKE :diary_id")
    Diary getDiaryById(int diary_id);

    @Query("DELETE FROM diaries WHERE id = :diary_id")
    void deleteDiaryById(int diary_id);
}
