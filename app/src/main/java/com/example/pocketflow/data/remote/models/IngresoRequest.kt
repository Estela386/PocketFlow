package com.example.pocketflow.data.remote.models

data class IngresoRequest(
    val id_usuario: String,
    val fecha: String,  // Formato "YYYY-MM-DD"
    val cantidad: Float,
    val motivo: String
)
