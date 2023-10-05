package com.wasim.notesapp.features.notes.ui.viewmodel.states

import com.wasim.notesapp.data.model.NotesResponse

data class NoteStates (
    val data: List<NotesResponse> = emptyList(),
    val error: String = "",
    val isLoading:Boolean =false
)