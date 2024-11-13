package com.example.step_tracking;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NotesActivity extends AppCompatActivity {

    private EditText noteEditText;
    private Button saveNoteButton;
    private NotesDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        noteEditText = findViewById(R.id.noteEditText);
        saveNoteButton = findViewById(R.id.saveNoteButton);
        dbHelper = new NotesDatabaseHelper(this);

        saveNoteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String note = noteEditText.getText().toString().trim();
                if(!note.isEmpty()){
                    dbHelper.insertNote(note);
                    Toast.makeText(NotesActivity.this, "Note Saved!", Toast.LENGTH_SHORT).show();
                    noteEditText.setText("");
                } else {
                    Toast.makeText(NotesActivity.this, "Please enter a note.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

