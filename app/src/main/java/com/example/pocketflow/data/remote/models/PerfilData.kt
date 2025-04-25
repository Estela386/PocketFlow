package com.example.pocketflow.data.remote.models

data class PerfilData(
    val perfil: String
)

data class ActualizarCorreoRequest(
    val uid: String,
    val nuevo_correo: String
)

data class ActualizarContrasenaRequest(
    val uid: String,
    val contrasena_actual: String,
    val nueva_contrasena: String,
    val confirmar_contrasena: String
)

data class ObtenerCorreoResponse(
    val correo: String
)

