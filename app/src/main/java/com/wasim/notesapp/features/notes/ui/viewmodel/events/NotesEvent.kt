package com.wasim.notesapp.features.notes.ui.viewmodel.events

import com.wasim.notesapp.data.model.Notes

sealed class NotesEvent{

    data class AddNoteEvent(val notes: Notes) : NotesEvent()
    object GetNoteEvent : NotesEvent()
    data class DeleteNotes(val id:Int) : NotesEvent()
    data class UpdateNotes(val id:Int,val notes:Notes) : NotesEvent()

}
