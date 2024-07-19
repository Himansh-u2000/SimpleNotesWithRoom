package com.example.noteroomdb.viewModel

import com.example.noteroomdb.roomDb.Note
import com.example.noteroomdb.roomDb.NoteDatabase

class Repositary(private val db: NoteDatabase) {

    suspend fun upsertNote(note: Note){
        db.dao.upsertNote(note)
    }

    suspend fun deleteNote(note: Note){
        db.dao.deleteNote(note)
    }

    fun getAllNotes() = db.dao.getAllNotes()
}