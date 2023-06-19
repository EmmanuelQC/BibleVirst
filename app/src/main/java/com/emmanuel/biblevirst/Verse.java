package com.emmanuel.biblevirst;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "verses")
public class Verse {

    @PrimaryKey
    public int id;

    @ColumnInfo(name = "contents")
    public String contents;
}
