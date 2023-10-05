package com.wasim.notesapp.data.network

import com.wasim.notesapp.data.model.Notes
import com.wasim.notesapp.data.model.NotesResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    companion object{
        val BASE_URL="http:/18.235.233.99/api/"
    }

    @GET("todo")
    suspend fun getNotes(): List<NotesResponse>

    @POST("todo/")
    suspend fun addNotes(
        @Body notes:Notes
    ):NotesResponse

    @DELETE("todo/{id}/")
    suspend fun deleteNotes(@Path("id") id:Int):NotesResponse

    @PUT("todo/{id}/")
    suspend fun updateNotes(
        @Path("id") id:Int,
        @Body notes:Notes
    ):NotesResponse
}