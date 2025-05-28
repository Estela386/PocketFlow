package com.example.pocketflow

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pocketflow.data.local.UserPreferences
import com.example.pocketflow.data.remote.RetrofitClient
import com.example.pocketflow.data.remote.models.IngresoRequest
import com.example.pocketflow.ui.theme.AnimatedWaveBackground
import com.example.pocketflow.ui.theme.AzulClaro
import com.example.pocketflow.ui.theme.AzulOscuro
import com.example.pocketflow.ui.theme.AmarilloMostaza
import com.example.pocketflow.ui.theme.BottomNavigationBar
import com.example.pocketflow.ui.theme.TopBar
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngresoScreen(navController: NavHostController) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var fecha by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var motivoSeleccionado by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val motivos = listOf("Pago", "Comida", "Transporte", "Entretenimiento", "Ahorro", "Otro")

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = currentRoute)
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedWaveBackground()
            TopBar(navController)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Registrar Ingreso",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AzulClaro,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(24.dp))

                TextField(
                    value = cantidad,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() || char == '.' }) cantidad = it
                    },
                    placeholder = { Text("Cantidad", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = motivoSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Motivo", color = Color.Gray) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .height(55.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        motivos.forEach { motivo ->
                            DropdownMenuItem(
                                text = { Text(motivo) },
                                onClick = {
                                    motivoSeleccionado = motivo
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                DatePickerFieldIngreso("Fecha", fecha) { fecha = it }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val uid = userPrefs.getUid()
                        if (uid.isNullOrEmpty() || cantidad.isBlank() || motivoSeleccionado.isBlank() || fecha.isBlank()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("Por favor completa todos los campos")
                            }
                            return@Button
                        }

                        val ingreso = IngresoRequest(
                            id_usuario = uid,
                            cantidad = cantidad.toFloat(),
                            motivo = motivoSeleccionado,
                            fecha = fecha
                        )

                        scope.launch {
                            try {
                                val response = RetrofitClient.api.registrarIngreso(ingreso)
                                if (response.isSuccessful) {
                                    snackbarHostState.showSnackbar("Ingreso registrado correctamente")
                                    cantidad = ""
                                    motivoSeleccionado = ""
                                    fecha = ""
                                } else {
                                    snackbarHostState.showSnackbar("Error al registrar ingreso")
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error de conexión: ${e.message}")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulClaro),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                ) {
                    Text("Registrar", color = AzulOscuro, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun DatePickerFieldIngreso(label: String, date: String, onDateSelected: (String) -> Unit) {
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
            year, month, day
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
            show()
        }
    }

    val displayDate = if (date.isNotEmpty()) {
        val parts = date.split("-")
        if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else "Seleccionar fecha"
    } else {
        "Seleccionar fecha"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .background(Color.White, shape = RoundedCornerShape(10.dp))
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
}
