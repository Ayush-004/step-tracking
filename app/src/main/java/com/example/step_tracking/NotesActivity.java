package com.example.step_tracking;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
public class NotesActivity extends AppCompatActivity {

    private EditText noteEditText;
    private Button saveNoteButton;
    private NotesDatabaseHelper dbHelper;
    private int notificationId = -1; // Default invalid ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        noteEditText = findViewById(R.id.noteEditText);
        saveNoteButton = findViewById(R.id.saveNoteButton);
        dbHelper = new NotesDatabaseHelper(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("notification_id")) {
            notificationId = intent.getIntExtra("notification_id", -1);
            Log.d("NotesActivity", "Received notification ID: " + notificationId);
        }

        saveNoteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String note = noteEditText.getText().toString().trim();
                if(!note.isEmpty()){
                    dbHelper.insertNote(note, notificationId); // Modify insertNote to accept notificationId
                    Toast.makeText(NotesActivity.this, "Note Saved!", Toast.LENGTH_SHORT).show();
                    noteEditText.setText("");
                    finish(); // Close activity after saving
                } else {
                    Toast.makeText(NotesActivity.this, "Please enter a note.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}