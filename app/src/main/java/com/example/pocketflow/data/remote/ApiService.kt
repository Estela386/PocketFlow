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
    suspend fun registerUser(@Body request: RegisterRequest): Response<RegisterResponse>

    // NUEVO: Login de usuario
    @POST("/api/login/auth")
    suspend fun loginUser(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/ingresos")
    suspend fun registrarIngreso(@Body ingreso: IngresoRequest): Response<Void>

    @POST("/api/egresos")
    suspend fun registrarEgreso(@Body egreso: EgresoRequest): Response<Void>
}
