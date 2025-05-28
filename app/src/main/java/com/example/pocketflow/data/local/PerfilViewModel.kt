package com.example.pocketflow.data.local

import android.app.Application
import android.util.Log
import androidx.core.view.WindowInsetsAnimationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketflow.data.remote.ApiService
import com.example.pocketflow.data.remote.models.ActualizarCorreoRequest
import com.example.pocketflow.data.remote.models.ActualizarContrasenaRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PerfilViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    private val _correo = MutableStateFlow("")
    val correo: StateFlow<String> = _correo

    private val apiService: ApiService = Retrofit.Builder()
       // .baseUrl("http://10.0.2.2:8000") // Reemplaza por tu IP o URL real si es necesario
        .baseUrl("http://127.0.0.1:8000/") // Cambia por tu IP local si hace falta
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    fun obtenerCorreo() {
        val uid = userPreferences.getUid() ?: return

        viewModelScope.launch {
            try {
                val response = apiService.obtenerCorreo(uid)
                _correo.value = response.correo
                Log.d("PERFIL", "Correo obtenido: ${response.correo}")
            } catch (e: Exception) {
                Log.e("PERFIL", "Error al obtener el correo", e)
            }
        }
    }

    fun actualizarCorreo(nuevoCorreo: String, onResult: (Boolean) -> Unit) {
        val uid = userPreferences.getUid() ?: return

        val request = ActualizarCorreoRequest(
            uid = uid,
            nuevo_correo = nuevoCorreo
        )

        viewModelScope.launch {
            try {
                val response = apiService.actualizarCorreo(request)
                if (response.isSuccessful) {
                    _correo.value = nuevoCorreo
                    Log.d("PERFIL", "Correo actualizado correctamente")
                    onResult(true)
                } else {
                    Log.e("PERFIL", "Error al actualizar correo: ${response.errorBody()?.string()}")
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("PERFIL", "Excepción al actualizar correo", e)
                onResult(false)
            }
        }
    }


    fun actualizarContrasena(actual: String, nueva: String) {
        val uid = userPreferences.getUid() ?: return
        val request = ActualizarContrasenaRequest(
            uid = uid,
            contrasena_actual = actual,
            nueva_contrasena = nueva,
            confirmar_contrasena = nueva
        )

        viewModelScope.launch {
            try {
                val response = apiService.actualizarContrasena(request)
                if (response.isSuccessful) {
                    Log.d("PERFIL", "Contraseña actualizada correctamente")
                } else {
                    Log.e("PERFIL", "Error al actualizar contraseña: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("PERFIL", "Excepción al actualizar contraseña", e)
            }
        }
    }
}