package com.wasim.notesapp.di

import com.wasim.notesapp.data.repository.NoteRepositoryImpl
import com.wasim.notesapp.features.notes.domain.repository.NoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun provideNoteRepository(
        noteRepositoryImpl: NoteRepositoryImpl
    ):NoteRepository
}