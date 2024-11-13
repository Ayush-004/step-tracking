// app/src/main/java/com/example/step_tracking/NotesAdapter.java

package com.example.step_tracking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<String> notesList;

    public NotesAdapter(List<String> notes) {
        this.notesList = notes;
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
        String note = notesList.get(position);
        holder.textViewNote.setText(note);

        // Optional: Handle click events on individual notes
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Note: " + note, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    // ViewHolder class for each note item
    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNote = itemView.findViewById(R.id.textViewNote);
        }
    }
}
