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

    @GET("/api/categorias/categorias/{uid}")
    suspend fun getCategorias(@Path("uid") uid: String): CategoriasWrapper

    @POST("/api/categorias/categorias")
    suspend fun agregarCategoria(@Body categoria: CategoriaRequest): Response<CategoriaResponse>

    @PUT("/api/categorias/categorias/{uid_usuario}/{id_categoria}")
    suspend fun editarCategoria(
        @Path("uid_usuario") uidUsuario: String,
        @Path("id_categoria") idCategoria: String,
        @Body categoria: CategoriaRequest
    ): retrofit2.Response<CategoriaResponse>

    @DELETE("/api/categorias/categorias/{uid}/{id}")
    suspend fun eliminarCategoria(
        @Path("uid") uid: String,
        @Path("id") id: String
    ): Response<Unit>

}
