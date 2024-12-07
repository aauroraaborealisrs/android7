package com.example.lab7;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentNoteDetails extends Fragment {

    private EditText editTextTitle, editTextContent;
    private Button buttonEdit, buttonSave, buttonCancel;
    private Note currentNote;

    private CheckBox checkbox;
    private boolean isEditing = false;

    private OnNoteUpdatedListener onNoteUpdatedListener;

    public interface OnNoteUpdatedListener {
        void onNoteUpdated(Note note);
    }

    public void setOnNoteUpdatedListener(OnNoteUpdatedListener listener) {
        this.onNoteUpdatedListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Инициализация элементов интерфейса
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextContent = view.findViewById(R.id.editTextContent);
        buttonEdit = view.findViewById(R.id.buttonEdit);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        checkbox = view.findViewById(R.id.noteCheckbox);

// Устанавливаем текущее состояние чекбокса
        if (currentNote != null) {
            checkbox.setChecked(currentNote.isCompleted());
        }

// Слушатель для чекбокса
        checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentNote != null) {
                currentNote.setCompleted(isChecked); // Обновляем состояние в заметке
            }
        });



        // Получение заметки из аргументов
        if (getArguments() != null) {
            currentNote = getArguments().getParcelable("note");
        }

        if (currentNote != null) {
            // Отображение данных заметки
            editTextTitle.setText(currentNote.getTitle());
            editTextContent.setText(currentNote.getContent());
            setEditingMode(false); // Режим просмотра
        } else {
            // Создание новой заметки
            currentNote = new Note("", "");
            setEditingMode(true); // Режим редактирования
        }

        // Кнопка "Редактировать"
        buttonEdit.setOnClickListener(v -> setEditingMode(true));

        // Кнопка "Сохранить"
        buttonSave.setOnClickListener(v -> saveNote());

        // Кнопка "Отмена"
        buttonCancel.setOnClickListener(v -> {
            if (currentNote.getId() == 0) {
                // Если создаётся новая заметка, просто закрыть фрагмент
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                // Вернуться к режиму просмотра
                setEditingMode(false);
            }
        });
    }



    private void setEditingMode(boolean editing) {
        isEditing = editing;
        editTextTitle.setEnabled(editing);
        editTextContent.setEnabled(editing);

        buttonEdit.setVisibility(editing ? View.GONE : View.VISIBLE);
        buttonSave.setVisibility(editing ? View.VISIBLE : View.GONE);
        buttonCancel.setVisibility(editing ? View.VISIBLE : View.GONE);

        View buttonContainer = requireView().findViewById(R.id.buttonContainer);
        buttonContainer.setVisibility(editing ? View.VISIBLE : View.GONE);

        // Логирование для отладки
        Log.d("FragmentNoteDetails", "Editing mode: " + editing);
        Log.d("FragmentNoteDetails", "buttonEdit visibility: " + (buttonEdit.getVisibility() == View.VISIBLE ? "VISIBLE" : "GONE"));
        Log.d("FragmentNoteDetails", "buttonSave visibility: " + (buttonSave.getVisibility() == View.VISIBLE ? "VISIBLE" : "GONE"));
        Log.d("FragmentNoteDetails", "buttonCancel visibility: " + (buttonCancel.getVisibility() == View.VISIBLE ? "VISIBLE" : "GONE"));
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
            Log.e("FragmentNoteDetails", "Поля заголовка или содержимого пусты.");
            return;
        }

        currentNote.setTitle(title);
        currentNote.setContent(content);
        currentNote.setCompleted(checkbox.isChecked()); // Сохраняем состояние чекбокса

        new Thread(() -> {
            NotesDatabase db = NotesDatabase.getInstance(requireContext());
            NotesDao dao = db.notesDao();

            if (currentNote.getId() == 0) {
                // Новая заметка
                dao.insert(currentNote);
                Log.d("FragmentNoteDetails", "Добавлена новая заметка: " + currentNote.getTitle());
            } else {
                // Обновление существующей заметки
                dao.update(currentNote);
                Log.d("FragmentNoteDetails", "Обновлена заметка: " + currentNote.getTitle());
            }

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Заметка сохранена!", Toast.LENGTH_SHORT).show();

                // Уведомляем слушателя об обновлении
                if (onNoteUpdatedListener != null) {
                    onNoteUpdatedListener.onNoteUpdated(currentNote);
                    Log.d("FragmentNoteDetails", "Слушатель уведомлен об обновлении заметки.");
                }

                // Выходим из режима редактирования
                setEditingMode(false);
            });
        }).start();
    }


    public Note getUpdatedNote() {
        return currentNote;
    }
}
