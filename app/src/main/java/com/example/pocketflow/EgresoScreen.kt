package com.example.pocketflow

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.pocketflow.data.remote.models.EgresoRequest
import com.example.pocketflow.data.remote.RetrofitClient
import com.example.pocketflow.ui.theme.AnimatedWaveBackground
import com.example.pocketflow.ui.theme.BottomNavigationBar
import com.example.pocketflow.ui.theme.TopBar
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EgresoScreen(navController: NavHostController) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var fecha by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var motivoSeleccionado by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val motivos = listOf("Renta", "Servicios", "Transporte", "Salud", "Educación", "Otro")

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
                    .padding(24.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Registrar Egreso",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C2D44),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cantidad
                Text("Cantidad", color = Color(0xFF1C2D44), fontWeight = FontWeight.Bold)
                TextField(
                    value = cantidad,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() || char == '.' }) cantidad = it
                    },
                    placeholder = { Text("$0.00", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFA3CFE3),
                        unfocusedContainerColor = Color(0xFFA3CFE3),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Motivo dropdown
                Text("Motivo", color = Color(0xFF1C2D44), fontWeight = FontWeight.Bold)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = motivoSeleccionado,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Seleccionar", color = Color.Gray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFA3CFE3),
                            unfocusedContainerColor = Color(0xFFA3CFE3),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .height(50.dp)
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

                Spacer(modifier = Modifier.height(24.dp))

                // Fecha
                DatePickerFieldIn("Fecha", fecha) { fecha = it }

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

                        val egreso = EgresoRequest(
                            id_usuario = uid,
                            cantidad = cantidad.toFloat(),
                            motivo = motivoSeleccionado,
                            fecha = fecha
                        )

                        scope.launch {
                            try {
                                val response = RetrofitClient.api.xregistrarEgreso(egreso)
                                if (response.isSuccessful) {
                                    snackbarHostState.showSnackbar("Egreso registrado correctamente")
                                    cantidad = ""
                                    motivoSeleccionado = ""
                                    fecha = ""
                                } else {
                                    snackbarHostState.showSnackbar("Error al registrar egreso: ${response.code()}")
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error de conexión: ${e.message}")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1C2D44)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                ) {
                    Text("Registrar", color = Color.White, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
