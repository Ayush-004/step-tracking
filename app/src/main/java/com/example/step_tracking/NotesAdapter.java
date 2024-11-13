package com.example.step_tracking;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notesList;
    private NotesDatabaseHelper dbHelper;
    private Context context;

    public NotesAdapter(Context context, List<Note> notes) {
        this.notesList = notes;
        this.context = context;
        this.dbHelper = new NotesDatabaseHelper(context);
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
        Note note = notesList.get(position);
        holder.textViewNoteContent.setText(note.getContent());
        holder.textViewNoteTimestamp.setText(note.getTimestamp());

        // Handle delete button click
        holder.buttonDeleteNote.setOnClickListener(v -> {
            // Confirm deletion
            new AlertDialog.Builder(context)
                    .setTitle("Delete Note")
                    .setMessage("Are you sure you want to delete this note?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        int rowsAffected = dbHelper.deleteNote(note.getContent(), note.getTimestamp());
                        if (rowsAffected > 0) {
                            notesList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, notesList.size());
                            Toast.makeText(context, "Note deleted.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Error deleting note.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // Optional: Handle item click to view details or edit
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Note: " + note.getContent(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    // ViewHolder class for each note item
    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNoteContent;
        TextView textViewNoteTimestamp;
        ImageButton buttonDeleteNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNoteContent = itemView.findViewById(R.id.textViewNoteContent);
            textViewNoteTimestamp = itemView.findViewById(R.id.textViewNoteTimestamp);
            buttonDeleteNote = itemView.findViewById(R.id.buttonDeleteNote);
        }
    }
}