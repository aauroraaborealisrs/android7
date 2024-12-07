package com.example.lab7;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FragmentNoteList extends Fragment {
    private List<Note> notes = new ArrayList<>();
    private NoteAdapter.OnNoteClickListener onNoteClickListener;
    private NoteAdapter adapter;

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public void setOnNoteClickListener(NoteAdapter.OnNoteClickListener listener) {
        this.onNoteClickListener = listener;
    }

    public NoteAdapter getAdapter() {
        return adapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_note_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Инициализируем адаптер и передаем его RecyclerView
        adapter = new NoteAdapter(notes, onNoteClickListener);
        recyclerView.setAdapter(adapter);
    }
}
