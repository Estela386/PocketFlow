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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pocketflow.data.remote.RetrofitClient
import com.example.pocketflow.data.remote.models.RegisterRequest
import com.example.pocketflow.ui.theme.AnimatedWaveBackground
import com.example.pocketflow.ui.theme.AmarilloMostaza
import com.example.pocketflow.ui.theme.AzulClaro
import com.example.pocketflow.ui.theme.AzulOscuro
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(navController: NavController) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

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
                color = AzulOscuro,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Spacer(modifier = Modifier.weight(0.5f))

            InputField(
                label = "NOMBRE",
                value = name,
                onValueChange = {
                    name = it
                    nameError = it.isNotEmpty() && !Regex("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+$").matches(it)
                },
                hint = "Ej: Ana Marmolejo",
                isError = nameError,
                errorMessage = "El nombre no debe tener números ni símbolos"
            )

            InputField(
                label = "CORREO",
                value = email,
                onValueChange = {
                    email = it
                    emailError = it.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
                },
                hint = "anamarmolejo@hotmail.com",
                isError = emailError,
                errorMessage = "Correo inválido"
            )

            InputField(
                label = "CONTRASEÑA",
                value = password,
                onValueChange = {
                    password = it
                    passwordError = it.isNotEmpty() && it.length < 6
                },
                isPassword = true,
                hint = "******",
                isError = passwordError,
                errorMessage = "Mínimo 6 caracteres"
            )

            DatePickerField("FECHA DE NACIMIENTO", birthDate) { birthDate = it }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    nameError = name.isBlank() || !Regex("^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+$").matches(name)
                    emailError = email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                    passwordError = password.isBlank() || password.length < 6

                    if (nameError || emailError || passwordError || birthDate.isBlank()) {
                        Toast.makeText(context, "Revisa los campos marcados", Toast.LENGTH_SHORT).show()
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
                                    fecha_nacimiento = birthDate
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
    hint: String = "",
    isError: Boolean = false,
    errorMessage: String = ""
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
            placeholder = { Text(hint, color = Color.Gray) },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text
            ),
            singleLine = true,
            shape = RoundedCornerShape(40.dp),
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = if (isError) Color.Red else AmarilloMostaza,
                unfocusedBorderColor = if (isError) Color.Red else Color.White,
                focusedLabelColor = AmarilloMostaza,
                unfocusedLabelColor = Color.White,
                cursorColor = AmarilloMostaza,
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        if (isError && errorMessage.isNotBlank()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 8.dp, top = 2.dp)
            )
        }
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

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        val dialog = DatePickerDialog(
            context,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val formattedDate = "%04d-%02d-%02d".format(selectedYear, selectedMonth + 1, selectedDay)
                onDateSelected(formattedDate)
                showDialog = false
            },
            year,
            month,
            day
        )
        dialog.datePicker.maxDate = System.currentTimeMillis()
        dialog.show()
    }

    val displayDate = if (date.isNotEmpty()) {
        val parts = date.split("-")
        if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else ""
    } else {
        ""
    }

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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(
                    width = 1.dp,
                    color = AmarilloMostaza,
                    shape = RoundedCornerShape(40.dp)
                )
                .background(Color.Transparent, shape = RoundedCornerShape(40.dp))
                .clickable { showDialog = true }
                .padding(start = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = if (displayDate.isNotEmpty()) displayDate else "Seleccionar fecha",
                color = if (displayDate.isNotEmpty()) Color.White else Color.Gray,
                fontSize = 16.sp
            )
        }
    }
}





