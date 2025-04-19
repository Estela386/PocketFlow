package com.example.pocketflow.data.remote

import com.example.pocketflow.data.remote.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Ya existente
    @GET("/api/analisis")
    suspend fun getAnalisis(): Response<AnalisisData>

    // NUEVO: Registro de usuario
    @POST("/api/registro")
    suspend fun registerUser(@Body request: RegisterRequest): Response<GenericResponse>

    // NUEVO: Login de usuario
    @POST("/api/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>
}
