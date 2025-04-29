package com.example.pocketflow.data.remote.models

data class LoginResponse(
    val access_token: String,
    val token_type: String = "bearer",
    val uid: String,
    val nombre: String
)
