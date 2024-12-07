
/*package com.example.lab7;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private final List<Note> notes;
    private final OnNoteClickListener listener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }

    public NoteAdapter(List<Note> notes, OnNoteClickListener listener) {
        this.notes = notes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);

        // Устанавливаем заголовок и картинку
        holder.title.setText(note.getTitle());
        holder.image.setImageResource(R.drawable.ic_note);

        // Устанавливаем состояние чекбокса, но блокируем взаимодействие
        holder.checkbox.setChecked(note.isCompleted());
        holder.checkbox.setEnabled(false); // Блокируем нажатие

        // Устанавливаем слушатель клика на элемент списка
        holder.itemView.setOnClickListener(v -> listener.onNoteClick(note));
    }


    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        CheckBox checkbox;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.noteImage);
            title = itemView.findViewById(R.id.noteTitle);
            checkbox = itemView.findViewById(R.id.noteCheckbox);
        }
    }
}*/

package com.example.lab7;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private final List<Note> notes = new ArrayList<>();
    private final OnNoteClickListener listener;

    public interface OnNoteClickListener {
        void onNoteClick(Note note);
    }

    public NoteAdapter(List<Note> initialNotes, OnNoteClickListener listener) {
        this.notes.addAll(initialNotes);
        this.listener = listener;
    }

    public void setNotes(List<Note> newNotes) {
        this.notes.clear();
        this.notes.addAll(newNotes);
        notifyDataSetChanged(); // Обновляем интерфейс
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);

        // Устанавливаем заголовок и картинку
        holder.title.setText(note.getTitle());
        holder.image.setImageResource(R.drawable.ic_note);

        // Устанавливаем состояние чекбокса, но блокируем взаимодействие
        holder.checkbox.setChecked(note.isCompleted());
        holder.checkbox.setEnabled(false); // Блокируем нажатие

        // Устанавливаем слушатель клика на элемент списка
        holder.itemView.setOnClickListener(v -> listener.onNoteClick(note));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        CheckBox checkbox;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.noteImage);
            title = itemView.findViewById(R.id.noteTitle);
            checkbox = itemView.findViewById(R.id.noteCheckbox);
        }
    }
}
