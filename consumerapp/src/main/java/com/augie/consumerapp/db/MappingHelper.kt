package com.augie.consumerapp.db

import android.database.Cursor
import com.augie.consumerapp.db.DatabaseContract.NoteColumns
import com.augie.consumerapp.entity.Note

object MappingHelper {
    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<Note> {
        val notesList = ArrayList<Note>()

        notesCursor?.apply {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(NoteColumns._ID))
                val title = getString(getColumnIndexOrThrow(NoteColumns.TITLE))
                val description = getString(getColumnIndexOrThrow(NoteColumns.DESCRIPTION))
                val date = getString(getColumnIndexOrThrow(NoteColumns.DATE))
                notesList.add(Note(id, title, description, date))
            }
        }
        return notesList
    }

    fun mapCursorToObject(notesCursor: Cursor?): Note {
        var note = Note()
        notesCursor?.apply {
            moveToFirst()
            val id = getInt(getColumnIndexOrThrow(NoteColumns._ID))
            val title = getString(getColumnIndexOrThrow(NoteColumns.TITLE))
            val description =
                getString(getColumnIndexOrThrow(NoteColumns.DESCRIPTION))
            val date = getString(getColumnIndexOrThrow(NoteColumns.DATE))
            note = Note(id, title, description, date)
        }
        return note
    }
}