package com.ethan.FamiCare.Diary;

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

    @Query("SELECT * FROM diaries WHERE id = :diary_id")
    List<Diary> getDiariesById(int diary_id);

    @Query("SELECT * FROM diaries WHERE id = :id AND title = :title")
    Diary getDiaryByIdAndTitle(int id, String title);

    @Query("DELETE FROM diaries WHERE id = :id AND title = :title")
    void deleteDiaryByIdAndTitle(int id, String title);
}