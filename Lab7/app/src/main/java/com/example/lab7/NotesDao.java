package com.example.lab7;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NotesDao {

    // Вставить запись
    @Insert
    void insert(Note note);

    // Обновить запись
    @Update
    void update(Note note);

    // Удалить запись
    @Delete
    void delete(Note note);

    // Получить все записи
    @Query("SELECT * FROM notes")
    List<Note> getAllNotes();

    // Получить запись по ID
    @Query("SELECT * FROM notes WHERE id = :id")
    Note getNoteById(int id);

    // Удалить все записи
    @Query("DELETE FROM notes")
    void deleteAllNotes();
}
