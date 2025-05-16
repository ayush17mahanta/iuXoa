package com.iuxoa.datadrop;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "browsing_entries")
public class BrowsingEntry {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String domain;
    public long timestamp;

    public BrowsingEntry(String domain, long timestamp) {
        this.domain = domain;
        this.timestamp = timestamp;
    }
}
