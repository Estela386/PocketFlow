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

    //Ingresos
    @POST("/api/ingresos")
    suspend fun registrarIngreso(@Body ingreso: IngresoRequest): Response<Void>

    @GET("/api/ingresos/{uid}")
    suspend fun getIngresos(@Path("uid") uid: String): List<IngresoRequest>

    //Egresos
    @POST("/api/egresos")
    suspend fun registrarEgreso(@Body egreso: EgresoRequest): Response<Void>

    //Categorias de Gastos
    @GET("/api/categorias/{uid}/filtrar/Gastos")
    suspend fun getCategoriasGastos(@Path("uid") uid: String): CategoriasWrapper

    //Categorias de Ingresos
    @GET("/api/categorias/{uid}/filtrar/Ingresos")
    suspend fun getCategoriasIngresos(@Path("uid") uid: String): CategoriasWrapper

    @GET("/api/egresos/{uid}")
    suspend fun getEgresos(@Path("uid") uid: String): List<EgresoRequest>

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

    @PUT("/api/perfil/actualizar-contrasena")
    suspend fun actualizarContrasena(@Body request: ActualizarContrasenaRequest): Response<Map<String, String>>

    @GET("/api/perfil/correo")
    suspend fun obtenerCorreo(
        @Query("uid") uid: String
    ): ObtenerCorreoResponse

    @PUT("/api/perfil/actualizar-correo")
    suspend fun actualizarCorreo(@Body request: ActualizarCorreoRequest): retrofit2.Response<Void>

    //Predicciones
    @GET("/api/api/predicciones/{id_usuario}")
    suspend fun obtenerPredicciones(
        @Path("id_usuario") idUsuario: String?,
        @Query("periodo") periodo: String // "semana", "mes", "anio"
    ): List<Prediccion>

    // En ApiService.kt
    @POST("api/login/verificar-correo")
    suspend fun verificarCorreo(@Body request: VerificarCorreoRequest): Response<Map<String, Boolean>>

    @POST("api/login/cambiar-contrasena")
    suspend fun cambiarContrasena(@Body request: CambiarContrasenaRequest): Response<Unit>

}
