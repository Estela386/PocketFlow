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

    @GET("/api/categorias/categorias/{uid}")
    suspend fun getCategorias(@Path("uid") uid: String): CategoriasWrapper

    @POST("/api/categorias/categorias")
    suspend fun agregarCategoria(@Body categoria: CategoriaRequest): Response<CategoriaResponse>

    @PUT("/api/categorias/{id}/")
    suspend fun editarCategoria(
        @Path("id") id: Int,
        @Body categoria: CategoriaRequest
    ): Response<CategoriaResponse>



}
