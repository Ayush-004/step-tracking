
package com.example.step_tracking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class ViewNotesActivity extends AppCompatActivity {

    private static final String TAG = "ViewNotesActivity";
    private RecyclerView recyclerView;
    private NotesAdapter notesAdapter;
    private NotesDatabaseHelper dbHelper;
    private List<Note> notesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notes);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize Database Helper
        dbHelper = new NotesDatabaseHelper(this);

        // Fetch notes
        fetchAndDisplayNotes();
    }

    private void fetchAndDisplayNotes(){
        notesList = dbHelper.getAllNotes();
        if(notesList.isEmpty()){
            Toast.makeText(this, "No notes available.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "No notes found in the database.");
        } else {
            notesAdapter = new NotesAdapter(this, notesList);
            recyclerView.setAdapter(notesAdapter);
            Log.d(TAG, "Notes loaded and displayed.");
        }
    }
}