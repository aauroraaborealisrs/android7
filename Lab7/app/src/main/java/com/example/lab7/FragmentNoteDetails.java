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
    private CheckBox noteCheckbox; // Используем одну переменную для CheckBox
    private Button buttonEdit, buttonSave, buttonCancel;
    private Note currentNote;
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
        noteCheckbox = view.findViewById(R.id.noteCheckbox);
        buttonEdit = view.findViewById(R.id.buttonEdit);
        buttonSave = view.findViewById(R.id.buttonSave);
        buttonCancel = view.findViewById(R.id.buttonCancel);

        // Получение заметки и флага режима редактирования из аргументов
        boolean isEditMode = false;
        if (getArguments() != null) {
            currentNote = getArguments().getParcelable("note");
            isEditMode = getArguments().getBoolean("isEditMode", false);
        }

        if (currentNote != null) {
            // Отображаем данные заметки
            editTextTitle.setText(currentNote.getTitle());
            editTextContent.setText(currentNote.getContent());
            noteCheckbox.setChecked(currentNote.isCompleted());
        } else {
            // Если заметка отсутствует, создаем новую
            currentNote = new Note("", "");
        }

        // Устанавливаем начальный режим (редактирование или просмотр)
        setEditingMode(isEditMode);

        // Обработка кнопок
        buttonEdit.setOnClickListener(v -> setEditingMode(true));
        buttonSave.setOnClickListener(v -> saveNote());
        buttonCancel.setOnClickListener(v -> {
            if (currentNote.getId() == 0) {
                // Если это новая заметка, просто закрываем фрагмент
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                // Возврат в режим просмотра
                setEditingMode(false);
            }
        });
    }


    private void setEditingMode(boolean editing) {
        isEditing = editing;
        editTextTitle.setEnabled(editing);
        editTextContent.setEnabled(editing);
        noteCheckbox.setEnabled(editing); // Включение/отключение CheckBox

        buttonEdit.setVisibility(editing ? View.GONE : View.VISIBLE);
        buttonSave.setVisibility(editing ? View.VISIBLE : View.GONE);
        buttonCancel.setVisibility(editing ? View.VISIBLE : View.GONE);

        View buttonContainer = requireView().findViewById(R.id.buttonContainer);
        buttonContainer.setVisibility(editing ? View.VISIBLE : View.GONE);
    }


    private boolean isSaving = false; // Флаг для предотвращения повторного вызова

    /*private void saveNote() {
        if (isSaving) return; // Прерываем, если сохранение уже запущено
        isSaving = true;

        String title = editTextTitle.getText().toString().trim();
        String content = editTextContent.getText().toString().trim();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
            isSaving = false;
            return;
        }

        currentNote.setTitle(title);
        currentNote.setContent(content);
        currentNote.setCompleted(noteCheckbox.isChecked());

        new Thread(() -> {
            NotesDatabase db = NotesDatabase.getInstance(requireContext());
            NotesDao dao = db.notesDao();

            if (currentNote.getId() == 0) {
                dao.insert(currentNote);
                Log.d("депрессия", " 129 insert called with note: " + currentNote.getTitle());
            } else {
                dao.update(currentNote);
            }

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Заметка сохранена!", Toast.LENGTH_SHORT).show();
                if (onNoteUpdatedListener != null) {
                    onNoteUpdatedListener.onNoteUpdated(currentNote);
                }
                setEditingMode(false);
                isSaving = false; // Сбрасываем флаг после завершения
            });
        }).start();
    }*/

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
        currentNote.setCompleted(noteCheckbox.isChecked()); // Сохраняем состояние чекбокса

        // Уведомляем слушателя, но не вызываем insert здесь
        if (onNoteUpdatedListener != null) {
            onNoteUpdatedListener.onNoteUpdated(currentNote);
            Log.d("FragmentNoteDetails", "onNoteUpdatedListener вызван для заметки: " + currentNote.getTitle());
        }

        setEditingMode(false); // Выход из режима редактирования
    }





    private void saveCheckboxState() {
        new Thread(() -> {
            NotesDatabase db = NotesDatabase.getInstance(requireContext());
            NotesDao dao = db.notesDao();

            dao.update(currentNote); // Обновление состояния заметки

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Состояние обновлено!", Toast.LENGTH_SHORT).show();

                // Уведомляем слушателя об обновлении
                if (onNoteUpdatedListener != null) {
                    onNoteUpdatedListener.onNoteUpdated(currentNote);
                }
            });
        }).start();
    }

    public Note getUpdatedNote() {
        return currentNote;
    }
}
