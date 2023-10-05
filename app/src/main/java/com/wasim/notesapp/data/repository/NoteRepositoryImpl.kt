package com.wasim.notesapp.data.repository

import com.wasim.notesapp.data.model.Notes
import com.wasim.notesapp.data.model.NotesResponse
import com.wasim.notesapp.data.network.ApiService
import com.wasim.notesapp.features.notes.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) :NoteRepository {

    override suspend fun addNotes(notes: Notes): Flow<NotesResponse> = flow{
        emit(apiService.addNotes(notes))
    }

    override suspend fun getNotes(): Flow<List<NotesResponse>>  = flow{
        emit(apiService.getNotes())
    }

    override suspend fun deleteNotes(id: Int): Flow<NotesResponse> = flow{
        emit(apiService.deleteNotes(id))
    }

    override suspend fun updateNotes(id: Int, notes: Notes): Flow<NotesResponse> = flow{
        emit(apiService.updateNotes(id,notes))
    }

}