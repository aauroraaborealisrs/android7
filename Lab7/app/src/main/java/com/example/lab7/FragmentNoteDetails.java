package com.example.lab7;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentNoteDetails extends Fragment {
    private Note note;
    private OnNoteUpdatedListener listener;

    // Интерфейс для обработки обновлений заметки
    public interface OnNoteUpdatedListener {
        void onNoteUpdated(Note updatedNote);
    }

    public void setOnNoteUpdatedListener(OnNoteUpdatedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Получаем заметку из аргументов
        if (getArguments() != null) {
            note = getArguments().getParcelable("note");
        }

        if (note == null) {
            Log.e("FragmentNoteDetails", "Note is null");
            return;
        }

        // Инициализация UI-элементов
        TextView noteTitle = view.findViewById(R.id.noteTitle);
        noteTitle.setText(note.getTitle());

        TextView noteContent = view.findViewById(R.id.noteContent);
        noteContent.setText(note.getContent());

        CheckBox noteCheckbox = view.findViewById(R.id.noteCheckbox);
        noteCheckbox.setChecked(note.isCompleted());

        // Обработка изменения состояния чекбокса
        noteCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            note.setCompleted(isChecked);
            if (listener != null) {
                listener.onNoteUpdated(note); // Сообщаем слушателю об изменении
            }
        });
    }

    // Метод для получения обновлённой заметки
    public Note getUpdatedNote() {
        return note;
    }
}
