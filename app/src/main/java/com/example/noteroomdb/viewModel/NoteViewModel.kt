package com.example.noteroomdb.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.noteroomdb.roomDb.Note
import kotlinx.coroutines.launch

class NoteViewModel(private val repositary: Repositary): ViewModel() {
    fun getNotes() = repositary.getAllNotes().asLiveData(viewModelScope.coroutineContext)

    fun upsertNote(note: Note){
        viewModelScope.launch {
            repositary.upsertNote(note)
        }
    }

    fun deleteNote(note: Note){
        viewModelScope.launch {
            repositary.deleteNote(note)
        }
    }
}