package com.example.pocketflow

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.pocketflow.ui.theme.AnimatedWaveBackground
import com.example.pocketflow.ui.theme.AmarilloMostaza
import com.example.pocketflow.ui.theme.AzulOscuro
import com.example.pocketflow.data.remote.RetrofitClient
import com.example.pocketflow.data.remote.models.CambiarContrasenaRequest
import com.example.pocketflow.data.remote.models.VerificarCorreoRequest

@Composable
fun RecuperacionScreen(navController: NavController) {
    val context = LocalContext.current

    var correo by remember { mutableStateOf("") }
    var nuevaContrasena by remember { mutableStateOf("") }

    var correoError by remember { mutableStateOf(false) }
    var contrasenaError by remember { mutableStateOf(false) }

    var correoValidado by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var successState by remember { mutableStateOf<Boolean?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedWaveBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Recuperar Contraseña",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro,
                modifier = Modifier.padding(vertical = 32.dp)
            )

            InputField(
                label = "CORREO",
                value = correo,
                onValueChange = {
                    if (!correoValidado) {
                        correo = it
                        correoError = it.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
                    }
                },
                hint = "ejemplo@correo.com",
                isError = correoError,
                errorMessage = "Correo inválido"
            )

            if (!correoValidado) {
                Button(
                    onClick = {
                        correoError = correo.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()
                        if (correoError) return@Button

                        isLoading = true
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response = RetrofitClient.api.verificarCorreo(
                                    VerificarCorreoRequest(correo = correo)
                                )
                                isLoading = false
                                if (response.isSuccessful && response.body()?.get("existe") == true) {
                                    successState = true
                                    correoValidado = true
                                } else {
                                    successState = false
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                successState = false
                            }
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = AmarilloMostaza),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .height(52.dp)
                ) {
                    if (isLoading) CircularProgressIndicator(color = AzulOscuro, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
                    else Text("Validar Correo", fontSize = 18.sp, color = AzulOscuro)
                }
            }

            successState?.let {
                Icon(
                    imageVector = if (it) Icons.Default.CheckCircle else Icons.Default.Error,
                    contentDescription = null,
                    tint = if (it) Color.Green else Color.Red,
                    modifier = Modifier.padding(top = 12.dp).size(36.dp)
                )
            }

            if (correoValidado) {
                InputField(
                    label = "NUEVA CONTRASEÑA",
                    value = nuevaContrasena,
                    onValueChange = {
                        nuevaContrasena = it
                        contrasenaError = it.isNotEmpty() && it.length < 6
                    },
                    hint = "******",
                    isPassword = true,
                    isError = contrasenaError,
                    errorMessage = "Mínimo 6 caracteres"
                )

                Button(
                    onClick = {
                        contrasenaError = nuevaContrasena.length < 6
                        if (contrasenaError) return@Button

                        isLoading = true
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response = RetrofitClient.api.cambiarContrasena(
                                    CambiarContrasenaRequest(correo = correo, nueva_contrasena = nuevaContrasena)
                                )
                                isLoading = false
                                successState = response.isSuccessful
                                if (response.isSuccessful) {
                                    CoroutineScope(Dispatchers.Main).launch {
                                        Toast.makeText(context, "Contraseña actualizada", Toast.LENGTH_SHORT).show()
                                        navController.navigate("login") {
                                            popUpTo("recuperacion") { inclusive = true }
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                successState = false
                            }
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = AmarilloMostaza),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .height(52.dp)
                ) {
                    if (isLoading) CircularProgressIndicator(color = AzulOscuro, strokeWidth = 3.dp, modifier = Modifier.size(24.dp))
                    else Text("Cambiar Contraseña", fontSize = 18.sp, color = AzulOscuro)
                }
            }
        }
    }
}
