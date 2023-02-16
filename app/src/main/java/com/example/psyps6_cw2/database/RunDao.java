package com.example.psyps6_cw2.database;


import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/*
Dao for run entries
 */

@Dao
public interface RunDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(Run run);

    @Update
    void update(Run run);

    @Delete
    void delete(Run run);

    @Query("DELETE FROM RUN_TABLE")
    void clearTable();

    @Query("SELECT * FROM RUN_TABLE ORDER BY id DESC")
    LiveData<List<Run>> getAllRuns();

    @Query("SELECT * FROM RUN_TABLE WHERE id = :runID")
    LiveData<Run> getRunById(int runID);

    @Query("SELECT *, MAX(distance) FROM RUN_TABLE")
    LiveData<Run> getHighestDistance();

    @Query("SELECT *, MAX(speed) FROM RUN_TABLE")
    LiveData<Run> getHighestPace();

    @Query("SELECT *, MAX(distance) FROM RUN_TABLE WHERE date = :date")
    LiveData<Run> getHighestDistanceToday(String date);

    @Query("SELECT *, MAX(speed) FROM RUN_TABLE WHERE date = :date")
    LiveData<Run> getHighestPaceToday(String date);
}