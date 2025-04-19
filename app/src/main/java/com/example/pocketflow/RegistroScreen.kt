package com.example.pocketflow

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(onRegisterSuccess: () -> Unit = {}) {
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
                            if (response.isSuccessful && response.body()?.success == true) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                    onRegisterSuccess()
                                }
                            } else {
                                CoroutineScope(Dispatchers.Main).launch {
                                    Toast.makeText(context, response.body()?.message ?: "Error al registrar", Toast.LENGTH_SHORT).show()
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B263A)),
                modifier = Modifier
                    .width(210.dp)
                    .height(62.dp)
            ) {
                Text(if (isLoading) "Registrando..." else "Registrar", fontSize = 20.sp, color = Color.White)
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
            color = Color(0xFF1C2D44),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(hint, color = Color.Gray) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White.copy(alpha = 0.8f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.8f),
                disabledContainerColor = Color.LightGray,
                errorContainerColor = Color.White,
                cursorColor = Color.Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(47.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(label: String, date: String, onDateSelected: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        DatePickerDialog(
            context,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val dayFormatted = selectedDay.toString().padStart(2, '0')
                val monthFormatted = (selectedMonth + 1).toString().padStart(2, '0')
                val isoDate = "$selectedYear-$monthFormatted-$dayFormatted"
                onDateSelected(isoDate)
                showDialog = false
            },
            year,
            month,
            day
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
            show()
        }
    }

    // Mostrar al usuario la fecha en formato dd/MM/yyyy
    val displayDate = if (date.isNotEmpty()) {
        val parts = date.split("-")
        if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else "02/03/2000"
    } else {
        "02/03/2000"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        Text(
            text = label,
            color = Color(0xFF1C2D44),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(47.dp)
                .clickable { showDialog = true }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = displayDate,
                color = if (date.isNotEmpty()) Color.Black else Color.Gray,
                fontSize = 16.sp
            )
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = Color.Gray,
            thickness = 1.dp
        )
    }
}



