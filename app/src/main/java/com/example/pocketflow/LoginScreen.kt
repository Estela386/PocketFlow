package com.example.pocketflow

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pocketflow.data.local.UserPreferences
import com.example.pocketflow.data.remote.ApiService
import com.example.pocketflow.data.remote.models.LoginRequest
import com.example.pocketflow.ui.theme.AmarilloMostaza
import com.example.pocketflow.ui.theme.AnimatedWaveBackground
import com.example.pocketflow.ui.theme.AzulClaro
import com.example.pocketflow.ui.theme.AzulOscuro
import com.example.pocketflow.ui.theme.Blanco
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPrefs = remember { UserPreferences(context) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedWaveBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AzulOscuro) // <- fondo aquí
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(250.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Login",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = AzulClaro
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                shape = RoundedCornerShape(40.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Blanco,
                    unfocusedTextColor = Blanco,
                    focusedBorderColor = AmarilloMostaza,
                    unfocusedBorderColor = Blanco,
                    focusedLabelColor = AmarilloMostaza,
                    unfocusedLabelColor = Blanco,
                    cursorColor = AmarilloMostaza,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(40.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Blanco,
                    unfocusedTextColor = Blanco,
                    focusedBorderColor = AmarilloMostaza,
                    unfocusedBorderColor = Blanco,
                    focusedLabelColor = AmarilloMostaza,
                    unfocusedLabelColor = Blanco,
                    cursorColor = AmarilloMostaza,
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    scope.launch {
                        val retrofit = Retrofit.Builder()
                            .baseUrl("http://10.0.2.2:8000/") // ⚠️ Cambia esto si usas un backend en
//                            .baseUrl("http://127.0.0.1:8000/") // Cambia por tu IP local si hace falta
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()

                        val api = retrofit.create(ApiService::class.java)
                        val response = api.loginUser(LoginRequest(email, password))

                        if (response.isSuccessful) {
                            val loginResponse = response.body()
                            val token = loginResponse?.access_token ?: ""
                            // Aquí deberías decodificar el JWT o recibir uid y nombre directamente del backend
                            val uid = loginResponse?.uid ?: "" // <-- REEMPLAZA por valor real si lo envías
                            val nombre = loginResponse?.nombre ?: ""

                            userPrefs.saveUserSession(token, uid, nombre)

                            Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                            navController.navigate("inicio") // <-- Reemplaza por tu pantalla principal
                        } else {
                            Toast.makeText(context, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF))
            ) {
                Icon(Icons.Default.Login, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Iniciar Sesión", fontSize = 18.sp, color = Color.Black)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = {
                navController.navigate("registro")
            }) {
                Text("¿No tienes cuenta? Regístrate", color = AmarilloMostaza, fontSize = 15.sp,)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = {
                navController.navigate("recuperar")
            }) {
                Text("¿Olvidaste tu contraseña?", color = AmarilloMostaza, fontSize = 15.sp,)
            }
        }
    }
}


