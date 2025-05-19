package com.example.pocketflow.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        //.baseUrl("http://10.0.2.2:8000/") // Cambia por tu IP local si hace falta
        .baseUrl("http://127.0.0.1:8000/") // Cambia por tu IP local si hace falta
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: ApiService = retrofit.create(ApiService::class.java)
}