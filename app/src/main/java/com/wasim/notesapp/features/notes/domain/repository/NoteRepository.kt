package com.wasim.notesapp.features.notes.domain.repository

import com.wasim.notesapp.data.model.Notes
import com.wasim.notesapp.data.model.NotesResponse
import kotlinx.coroutines.flow.Flow


interface NoteRepository {

    suspend fun addNotes(notes: Notes): Flow<NotesResponse>

    suspend fun getNotes(): Flow<List<NotesResponse>>

    suspend fun deleteNotes(id:Int): Flow<NotesResponse>

    suspend fun updateNotes(id:Int, notes: Notes) : Flow<NotesResponse>
}