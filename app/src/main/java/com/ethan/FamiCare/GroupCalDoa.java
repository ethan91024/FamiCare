package com.ethan.FamiCare;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GroupCalDoa {//1

    @Insert
    long insertGroupCal(GroupCal groupCal);

    @Delete
    void deleteGroupCal(GroupCal groupCal);

    @Update
    void updateGroupCal(GroupCal groupCal);

    @Query("SELECT * FROM groupcal")
    List<GroupCal> getGroupCal();

    @Query("SELECT * FROM groupcal WHERE id LIKE :groupCal_id")
    GroupCal getGroupCalById(int groupCal_id);

    @Query("DELETE FROM groupcal WHERE id = :groupCal_id")
    void deleteGroupCalById(int groupCal_id);
}

