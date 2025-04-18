package com.example.pocketflow.data.remote

import com.example.pocketflow.data.remote.models.AnalisisData
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("analisis/")
    suspend fun getAnalisis(): Response<AnalisisData>
}