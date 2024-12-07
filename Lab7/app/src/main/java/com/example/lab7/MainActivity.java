package com.example.lab7;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Note> notes; // Теперь это поле класса
    private FragmentNoteList noteListFragment; // Поле для хранения ссылки на фрагмент списка

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Настраиваем отступы для системных панелей
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Создаем список заметок
        notes = Arrays.asList( // Поле инициализируется в onCreate
                new Note(1, "Note 1", "Описание заметки 1"),
                new Note(2, "Note 2", "Описание заметки 2"),
                new Note(3, "Note 3", "Описание заметки 3")
        );

        // Создаем фрагмент списка заметок
        noteListFragment = new FragmentNoteList(); // Сохраняем в поле
        noteListFragment.setNotes(notes);

        // Инициализируем FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (findViewById(R.id.detailsFragmentContainer) != null) {
            // Планшетный режим
            noteListFragment.setOnNoteClickListener(note -> {
                FragmentNoteDetails detailsFragment = new FragmentNoteDetails();

                // Передача данных заметки
                Bundle bundle = new Bundle();
                bundle.putParcelable("note", note);
                detailsFragment.setArguments(bundle);

                // Обновление состояния заметки через Listener
                detailsFragment.setOnNoteUpdatedListener(updatedNote -> {
                    // Обновляем заметку в списке
                    int index = notes.indexOf(updatedNote);
                    if (index != -1) {
                        notes.set(index, updatedNote);
                        noteListFragment.getAdapter().notifyItemChanged(index);
                    }
                });

                // Заменяем фрагмент деталей
                fragmentManager.beginTransaction()
                        .replace(R.id.detailsFragmentContainer, detailsFragment)
                        .commit();
            });

            // Отображение списка
            fragmentManager.beginTransaction()
                    .replace(R.id.listFragmentContainer, noteListFragment)
                    .commit();
        } else {
            // Телефонный режим
            noteListFragment.setOnNoteClickListener(note -> {
                Intent intent = new Intent(this, NoteDetailsActivity.class);
                intent.putExtra("note", note);
                startActivityForResult(intent, 100); // Используем startActivityForResult для передачи данных обратно
            });

            fragmentManager.beginTransaction()
                    .replace(R.id.mainFragmentContainer, noteListFragment)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("MainActivity", "onActivityResult called");
        Log.d("MainActivity", "requestCode: " + requestCode + ", resultCode: " + resultCode);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Note updatedNote = data.getParcelableExtra("updatedNote");
            if (updatedNote != null) {
                Log.d("MainActivity", "Updated note received: " + updatedNote.getTitle());

                int index = notes.indexOf(updatedNote);
                if (index != -1) {
                    notes.set(index, updatedNote);
                    Log.d("MainActivity", "Note updated in list at index: " + index);

                    NoteAdapter adapter = noteListFragment.getAdapter();
                    if (adapter != null) {
                        adapter.notifyItemChanged(index);
                        Log.d("MainActivity", "Adapter notified for index: " + index);
                    } else {
                        Log.e("MainActivity", "Adapter is null");
                    }
                } else {
                    Log.e("MainActivity", "Updated note not found in list");
                }
            } else {
                Log.e("MainActivity", "Updated note is null");
            }
        } else {
            Log.e("MainActivity", "Intent data is null or result code is not RESULT_OK");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            Toast.makeText(this, "Кнопка добавить нажата", Toast.LENGTH_SHORT).show();
            Log.d("MainActivity", "Кнопка добавить нажата");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
