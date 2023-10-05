package com.wasim.notesapp.features.notes.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wasim.notesapp.data.model.NotesResponse
import com.wasim.notesapp.features.notes.domain.repository.NoteRepository
import com.wasim.notesapp.features.notes.ui.viewmodel.events.NotesEvent
import com.wasim.notesapp.features.notes.ui.viewmodel.states.NoteStates
import com.wasim.notesapp.utils.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository
) : ViewModel(){

    private val _addNoteEventFlow = MutableSharedFlow<ApiState<NotesResponse>>()
    val addNoteEventFlow = _addNoteEventFlow.asSharedFlow()

    private val _deleteNoteEventFlow = MutableSharedFlow<ApiState<NotesResponse>>()
    val deleteNoteEventFlow = _deleteNoteEventFlow.asSharedFlow()

    private val _updateNoteEventFlow = MutableSharedFlow<ApiState<NotesResponse>>()
    val updateNoteEventFlow = _updateNoteEventFlow.asSharedFlow()

    private val _getNoteEventFlow = mutableStateOf(NoteStates())
    val getNoteEventFlow  = _getNoteEventFlow

    fun onEvent(event: NotesEvent){
        when(event){
            is NotesEvent.AddNoteEvent -> {
                viewModelScope.launch {
                    noteRepository.addNotes(event.notes)
                        .onStart {
                            _addNoteEventFlow.emit(ApiState.Loading)
                        }
                        .catch {
                            _addNoteEventFlow.emit(ApiState.Failure(it.message ?: "something went wrong"))
                        }
                        .collect{
                            _addNoteEventFlow.emit(ApiState.Success(it))
                        }
                }
            }
            is NotesEvent.DeleteNotes -> {
                viewModelScope.launch {
                    noteRepository.deleteNotes(event.id)
                        .onStart {
                            _deleteNoteEventFlow.emit(ApiState.Loading)
                        }
                        .catch {
                            _deleteNoteEventFlow.emit(ApiState.Failure(it.message ?: "something went wrong"))
                        }
                        .collect{
                            _deleteNoteEventFlow.emit(ApiState.Success(it))
                        }
                }
            }
            NotesEvent.GetNoteEvent -> {
                viewModelScope.launch {
                    noteRepository.getNotes()
                        .onStart {
                            _getNoteEventFlow.value = NoteStates(
                                isLoading = true
                            )
                        }
                        .catch {
                            _getNoteEventFlow.value = NoteStates(
                                error = it.message ?: "something went wrong"
                            )
                        }
                        .collect{
                            _getNoteEventFlow.value = NoteStates(
                                data = it
                            )
                        }
                }
            }
            is NotesEvent.UpdateNotes -> {
                viewModelScope.launch {
                    noteRepository.updateNotes(event.id,event.notes)
                        .onStart {
                            _updateNoteEventFlow.emit(ApiState.Loading)
                        }
                        .catch {
                            _updateNoteEventFlow.emit(ApiState.Failure(it.message ?: "something went wrong"))
                        }
                        .collect{
                            _updateNoteEventFlow.emit(ApiState.Success(it))
                        }
                }
            }
        }
    }
}