package com.example.pocketflow.data.remote.models

data class CambiarContrasenaRequest(
    val correo: String,
    val nueva_contrasena: String
)