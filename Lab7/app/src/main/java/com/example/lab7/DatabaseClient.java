package com.example.lab7;

import android.content.Context;
import androidx.room.Room;

public class DatabaseClient {

    private static DatabaseClient instance;
    private NotesDatabase notesDatabase;

    private DatabaseClient(Context context) {
        notesDatabase = Room.databaseBuilder(context, NotesDatabase.class, "notes_database")
                .fallbackToDestructiveMigration()
                .build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public NotesDatabase getDatabase() {
        return notesDatabase;
    }
}
