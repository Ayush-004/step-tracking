package com.example.step_tracking;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NotesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notesDB";
    private static final int DATABASE_VERSION = 3; // Incremented for schema change
    private static final String TABLE_NOTES = "Notes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOTE = "note";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_NOTIFICATION_ID = "notification_id"; // New column

    public NotesDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOTE + " TEXT,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + COLUMN_NOTIFICATION_ID + " INTEGER"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add the notification_id column if upgrading from version 2 to 3
        if (oldVersion < 3) {
            String ADD_NOTIFICATION_ID = "ALTER TABLE " + TABLE_NOTES + " ADD COLUMN " + COLUMN_NOTIFICATION_ID + " INTEGER";
            db.execSQL(ADD_NOTIFICATION_ID);
        }
    }

    public void insertNote(String note, int notificationId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_NOTIFICATION_ID, notificationId);
        db.insert(TABLE_NOTES, null, values);
        db.close();
    }


    /**
     * Retrieves all notes along with their timestamps from the database.
     * @return List of Note objects containing note content and timestamp.
     */
    public List<Note> getAllNotes(){
        List<Note> notesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES,
                new String[]{COLUMN_NOTE, COLUMN_TIMESTAMP},
                null, null, null, null, COLUMN_ID + " DESC"); // Order by latest first

        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    String noteContent = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE));
                    String timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                    notesList.add(new Note(noteContent, timestamp));
                } while(cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return notesList;
    }

    /**
     * Deletes a specific note from the database based on its content and timestamp.
     * @param noteContent The content of the note to delete.
     * @param timestamp The timestamp of the note to delete.
     * @return Number of rows affected.
     */
    public int deleteNote(String noteContent, String timestamp){
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsAffected = db.delete(TABLE_NOTES,
                COLUMN_NOTE + " = ? AND " + COLUMN_TIMESTAMP + " = ?",
                new String[]{noteContent, timestamp});
        db.close();
        return rowsAffected;
    }
}