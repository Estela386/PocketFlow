package com.example.pocketflow.ui.theme

import android.app.Application
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.example.pocketflow.data.local.UserPreferences
import com.example.pocketflow.data.remote.ApiService
import com.example.pocketflow.data.remote.models.CategoriaRequest
import com.example.pocketflow.data.remote.models.CategoriaResponse
import com.example.pocketflow.data.remote.models.CategoriasWrapper
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CategoriasViewModel(application: Application) : AndroidViewModel(application) {

    private val _categorias = mutableStateOf<List<CategoriaResponse>>(emptyList())
    val categorias: State<List<CategoriaResponse>> = _categorias

    private val userPreferences = UserPreferences(application)

    private val apiService: ApiService = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000") // Reemplaza con tu URL real
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    fun cargarCategorias() {
        val uid = userPreferences.getUid() ?: return

        viewModelScope.launch {
            try {
                val response = apiService.getCategorias(uid)
                if (response.categorias.isNotEmpty()) {
                    Log.d("API", "Respuesta exitosa: ${response.categorias}")
                    _categorias.value = response.categorias ?: emptyList()
                } else {
                    Log.e("CategoriasViewModel", "Error en la respuesta: ${response}")
                }
            } catch (e: Exception) {
                Log.e("CategoriasViewModel", "Error al cargar categorías", e)
            }
        }
    }

    fun crearCategoria(
        categoria: String,
        descripcion: String,
        clasificacion: String
    ) {
        val uid = userPreferences.getUid() ?: return // Obtiene el UID desde preferencias

        val request = CategoriaRequest(
            categoria = categoria,
            descripcion = descripcion,
            clasificacion = clasificacion,
            uid_usuario = uid
        )

        println("Datos enviados: $request")

        viewModelScope.launch {
            try {
                val response = apiService.agregarCategoria(request)
                if (response.isSuccessful) {
                    Log.d("CREAR", "Categoría creada correctamente")
                    cargarCategorias() // Opcional: Recargar categorías tras crear una nueva
                } else {
                    Log.e("CREAR", "Error ${response.code()}: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("CREAR", "Excepción: ${e.localizedMessage}")
            }
        }
    }



    fun editarCategoria(id: Int, nombre: String, descripcion: String, clasificacion: String) {
        val uid = userPreferences.getUid() ?: return

        val categoriaEditada = CategoriaRequest(
            categoria = nombre,
            descripcion = descripcion,
            clasificacion = clasificacion,
            uid_usuario = uid
        )

        viewModelScope.launch {
            try {
                val response = apiService.editarCategoria(id, categoriaEditada)
                if (response.isSuccessful) {
                    Log.d("EditarCategoria", "Categoría actualizada: ${response.body()}")
                    cargarCategorias()
                } else {
                    Log.e("EditarCategoria", "Error en respuesta: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("EditarCategoria", "Error al editar categoría", e)
            }
        }
    }

}
