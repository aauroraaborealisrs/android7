package com.example.lab7;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
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
    private Button buttonEdit, buttonSave, buttonCancel, buttonDelete;
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
        buttonDelete = view.findViewById(R.id.buttonDelete); // Инициализируем кнопку "Удалить"

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

        buttonDelete = view.findViewById(R.id.buttonDelete);

        buttonDelete.setOnClickListener(v -> deleteNote());


        noteCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isEditing && currentNote != null) {
                // Обновляем состояние заметки
                currentNote.setCompleted(isChecked);
                saveCheckboxState(); // Сохраняем в базу и обновляем список
            }
        });

    }

    /*private void deleteNote() {
        if (currentNote.getId() == 0) {
            Toast.makeText(requireContext(), "Эту заметку нельзя удалить.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            NotesDatabase db = NotesDatabase.getInstance(requireContext());
            NotesDao dao = db.notesDao();
            dao.delete(currentNote); // Удаление из базы данных

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Заметка удалена!", Toast.LENGTH_SHORT).show();

                if (onNoteUpdatedListener != null) {
                    onNoteUpdatedListener.onNoteUpdated(null); // Уведомляем, что заметка удалена
                }

                clearDetails();


                if (requireActivity() instanceof NoteDetailsActivity) {
                    // Закрываем активность на телефоне
                    ((MainActivity) requireActivity()).loadNotesFromDatabase();

                    requireActivity().finish();
                } else {
                    // Удаляем фрагмент на планшете
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }).start();
    }*/

    private void deleteNote() {
        if (currentNote.getId() == 0) {
            Toast.makeText(requireContext(), "Эту заметку нельзя удалить.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            NotesDatabase db = NotesDatabase.getInstance(requireContext());
            NotesDao dao = db.notesDao();
            dao.delete(currentNote); // Удаление из базы данных

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(requireContext(), "Заметка удалена!", Toast.LENGTH_SHORT).show();

                if (onNoteUpdatedListener != null) {
                    onNoteUpdatedListener.onNoteUpdated(null); // Уведомляем, что заметка удалена
                }

                clearDetails();


                if (requireActivity() instanceof NoteDetailsActivity) {
                    // Закрываем активность на телефоне
                    requireActivity().setResult(requireActivity().RESULT_OK);
                    Intent intent = new Intent();
                    requireActivity().setResult(RESULT_OK, intent); // Без заметки (удалено)
                    requireActivity().finish();
                } else if (requireActivity() instanceof MainActivity) {
                    // Обновляем список заметок на планшете
                    MainActivity mainActivity = (MainActivity) requireActivity();
                    mainActivity.loadNotesFromDatabase(); // Обновляем данные списка
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });
        }).start();
    }


    public void clearDetails() {
        editTextTitle.setText("");
        editTextContent.setText("");
        noteCheckbox.setChecked(false);
        buttonEdit.setVisibility(View.GONE);
        buttonSave.setVisibility(View.GONE);
        buttonCancel.setVisibility(View.GONE);
        Toast.makeText(requireContext(), "Заметка удалена", Toast.LENGTH_SHORT).show();
    }


    private void setEditingMode(boolean editing) {
        isEditing = editing;
        editTextTitle.setEnabled(editing);
        editTextContent.setEnabled(editing);

        buttonEdit.setVisibility(editing ? View.GONE : View.VISIBLE); // Кнопка "Редактировать" скрыта при редактировании
        buttonSave.setVisibility(editing ? View.VISIBLE : View.GONE); // Кнопка "Сохранить" видима только в режиме редактирования
        buttonCancel.setVisibility(editing ? View.VISIBLE : View.GONE); // Кнопка "Отмена" видима только в режиме редактирования
        buttonDelete.setVisibility(!editing ? View.VISIBLE : View.GONE); // Кнопка "Удалить" доступна только в режиме просмотра

        View buttonContainer = requireView().findViewById(R.id.buttonContainer);
        buttonContainer.setVisibility(View.VISIBLE); // Убедитесь, что контейнер для кнопок всегда видим
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
        currentNote.setCompleted(noteCheckbox.isChecked()); // Сохраняем состояние чекбокса

        // Уведомляем слушателя, но не вызываем insert здесь
        if (onNoteUpdatedListener != null) {
            onNoteUpdatedListener.onNoteUpdated(currentNote);
            Log.d("FragmentNoteDetails", "onNoteUpdatedListener вызван для заметки: " + currentNote.getTitle());
        }

        // Если мы на телефоне (в активности NoteDetailsActivity), закрываем активность
        if (requireActivity().getClass().equals(NoteDetailsActivity.class)) {
            requireActivity().finish(); // Закрываем активность
            Log.d("FragmentNoteDetails", "requireActivity().finish(); " + currentNote.getTitle());

        } else {
            // На планшете выходим из режима редактирования
            setEditingMode(false);
            Log.d("FragmentNoteDetails", " setEditingMode(false); " + currentNote.getTitle());

        }

    }


    /*private void saveCheckboxState() {
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
    }*/

    private void saveCheckboxState() {
        new Thread(() -> {
            NotesDatabase db = NotesDatabase.getInstance(requireContext());
            NotesDao dao = db.notesDao();

            dao.update(currentNote); // Обновление состояния заметки

            requireActivity().runOnUiThread(() -> {
                if (!isAdded()) {
                    // Если фрагмент не добавлен, прерываем выполнение
                    return;
                }

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
