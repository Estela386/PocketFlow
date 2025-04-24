package com.example.pocketflow.data.remote.models

data class Categoria(
    val categoria: String,
    val descripcion: String,
    val uid_usuario: String
)

data class CategoriaResponse(
    val id: String,
    val categoria: String,
    val descripcion: String,
    val clasificacion: String
)

data class CategoriasWrapper(
    val categorias: List<CategoriaResponse>
)

data class CategoriaRequest(
    val categoria: String,
    val descripcion: String,
    val clasificacion: String,
    val uid_usuario: String // o UID si es necesario asociar con el usuario
)
