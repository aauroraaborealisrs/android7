package com.example.lab7;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "notes") // Таблица будет называться "notes"
public class Note implements Parcelable {

    @PrimaryKey(autoGenerate = true) // ID будет автоинкрементироваться
    private int id;

    private String title;           // Поле "title"
    private String content;         // Поле "content"
    private boolean isCompleted;    // Поле "isCompleted"

    // Конструктор
    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.isCompleted = false; // Значение по умолчанию
    }

    // Parcelable методы
    protected Note(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
        isCompleted = in.readByte() != 0;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeByte((byte) (isCompleted ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Переопределяем equals для сравнения заметок по ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return id == note.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
