package com.example.pocketflow.data.remote.models

data class RegisterRequest(
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val fecha_nacimiento: String
)