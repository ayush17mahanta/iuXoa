package com.iuxoa.datadrop;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BrowsingDao {
    @Insert
    void insert(BrowsingEntry entry);

    @Query("SELECT * FROM browsing_entries ORDER BY timestamp DESC")
    List<BrowsingEntry> getAll();
}
