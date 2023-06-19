package com.emmanuel.biblevirst;

import android.widget.ListView;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.RoomDatabase;

import java.util.List;

@Dao
public interface VerseDao {
    String v = Home.rVerse;
    //DAO stands for data access object
    @Query("INSERT INTO verses (contents) VALUES (:v)")
    void create(String v);

    @Query("SELECT * FROM verses")
    List<Verse> getAllVerses();

    @Query("SELECT id FROM verses")
    int verseId();

    @Query("UPDATE verses SET contents = :contents WHERE id = :id")
    void save(String contents, int id);

    @Query("DELETE FROM verses WHERE id = :id")
    void delete(int id);

    @Query("DELETE FROM verses")
    void deleteAll();
}
