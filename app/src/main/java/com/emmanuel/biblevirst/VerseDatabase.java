package com.emmanuel.biblevirst;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Verse.class}, version = 1)
public abstract class VerseDatabase extends RoomDatabase {
    public abstract VerseDao verseDao();
}
