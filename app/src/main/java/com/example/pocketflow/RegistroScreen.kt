package com.example.pocketflow

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pocketflow.data.remote.RetrofitClient
import com.example.pocketflow.data.remote.models.RegisterRequest
import com.example.pocketflow.ui.theme.AnimatedWaveBackground
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import androidx.compose.foundation.text.KeyboardOptions
import androidx.navigation.NavController
import com.example.pocketflow.ui.theme.AmarilloMostaza
import com.example.pocketflow.ui.theme.AzulClaro
import com.example.pocketflow.ui.theme.AzulOscuro
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(navController: NavController) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") } // formato yyyy-MM-dd
    var isLoading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedWaveBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 55.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crear Cuenta",
                fontSize = 43.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C2D44),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Spacer(modifier = Modifier.weight(.5f))

            InputField("NOMBRE", name, { name = it }, hint = "Ej: Ana Marmolejo")
            InputField("CORREO", email, { email = it }, hint = "anamarmolejo@hotmail.com")
            InputField("CONTRASEÑA", password, { password = it }, isPassword = true, hint = "******")
            InputField("CONFIRMAR CONTRASEÑA", confirmPassword, { confirmPassword = it }, isPassword = true, hint = "******")
            DatePickerField("FECHA DE NACIMIENTO", birthDate) { birthDate = it }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (password != confirmPassword) {
                        Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (name.isBlank() || email.isBlank() || password.isBlank() || birthDate.isBlank()) {
                        Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = RetrofitClient.api.registerUser(
                                RegisterRequest(
                                    nombre = name,
                                    correo = email,
                                    contrasena = password,
                                    fecha_nacimiento = birthDate // yyyy-MM-dd
                                )
                            )
                            isLoading = false
                            val mensaje = response.body()?.mensaje ?: ""
                            if (response.isSuccessful && mensaje.contains("registrado correctamente", ignoreCase = true)) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                                    navController.navigate("login") {
                                        popUpTo("registro") { inclusive = true }
                                    }
                                }
                            } else {
                                CoroutineScope(Dispatchers.Main).launch {
                                    Toast.makeText(context, mensaje.ifBlank { "Error al registrar" }, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            isLoading = false
                            CoroutineScope(Dispatchers.Main).launch {
                                Toast.makeText(context, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = AmarilloMostaza),

                modifier = Modifier
                    .width(210.dp)
                    .height(62.dp)
            ) {
                Text(if (isLoading) "Registrando..." else "Registrar", fontSize = 20.sp, color = AzulOscuro)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false,
    hint: String = ""
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = label,
            color = AzulClaro,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text(hint, color = Color.Gray) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text
            ),
            singleLine = false,
            shape = RoundedCornerShape(40.dp), // Forma redondeada del borde
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = AmarilloMostaza, // Color AmarilloMostaza
                unfocusedBorderColor = Color.White,
                focusedLabelColor = AmarilloMostaza, // Color AmarilloMostaza
                unfocusedLabelColor = Color.White,
                cursorColor = AmarilloMostaza, // Color AmarilloMostaza
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun DatePickerField(
    label: String,
    date: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        DatePickerDialog(context, { _, y, m, d ->
            val isoDate = "%04d-%02d-%02d".format(y, m + 1, d)
            onDateSelected(isoDate)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).apply {
            datePicker.maxDate = System.currentTimeMillis()
        }.show()
        showDialog = false
    }

    val displayDate = date.takeIf { it.contains("-") }?.split("-")?.let {
        "${it[2]}/${it[1]}/${it[0]}"
    } ?: ""

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = label,
            color = AzulClaro,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        OutlinedTextField(
            value = displayDate,
            onValueChange = {},
            placeholder = { Text("Seleccionar fecha", color = Color.Gray) },
            readOnly = true,
            singleLine = true,
            shape = RoundedCornerShape(40.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = AmarilloMostaza,
                unfocusedBorderColor = Color.White,
                focusedLabelColor = AmarilloMostaza,
                unfocusedLabelColor = Color.White,
                cursorColor = AmarilloMostaza,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true } // Ahora aquí el clic
        )
    }
}



