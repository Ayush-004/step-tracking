
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
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NOTES = "Notes";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOTE = "note";

    public NotesDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NOTES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOTE + " TEXT"
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For simplicity, drop and recreate table on upgrade
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    public void insertNote(String note){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE, note);
        db.insert(TABLE_NOTES, null, values);
        db.close();
    }
    public void deleteNote(String note){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, COLUMN_NOTE + " = ?", new String[]{note});
        db.close();
    }


    /**
     * Retrieves all notes from the database.
     * @return List of all saved notes.
     */
    public List<String> getAllNotes(){
        List<String> notesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NOTES,
                new String[]{COLUMN_NOTE},
                null, null, null, null, COLUMN_ID + " DESC"); // Order by latest first

        if(cursor != null){
            if(cursor.moveToFirst()){
                do{
                    String note = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOTE));
                    notesList.add(note);
                } while(cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
        return notesList;
    }
}
