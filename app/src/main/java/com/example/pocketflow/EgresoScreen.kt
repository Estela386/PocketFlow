package com.example.pocketflow

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
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
import com.example.pocketflow.data.remote.models.EgresoRequest
import com.example.pocketflow.data.remote.models.Categoria
import com.example.pocketflow.ui.theme.*
import kotlinx.coroutines.delay
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
    var motivos by remember { mutableStateOf(listOf<Categoria>()) }

    var isLoading by remember { mutableStateOf(false) }
    var resultSuccess by remember { mutableStateOf<Boolean?>(null) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(Unit) {
        val uid = userPrefs.getUid()
        try {
            val response = RetrofitClient.api.getCategoriasGastos(uid ?: "")
            motivos = response.categorias.map { Categoria(it.categoria, it.descripcion, "") }
        } catch (e: Exception) {
            snackbarHostState.showSnackbar("Error al cargar categorías: ${e.message}")
        }
    }

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
                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    "Registrar Gastos",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C2D44),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cantidad
                Text("Cantidad", color = Color(0xFF1C2D44), fontWeight = FontWeight.Bold)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color(0xFFA3CFE3))
                        .padding(start = 12.dp)
                ) {
                    Text("$", color = Color.DarkGray, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = cantidad,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() || char == '.' }) cantidad = it
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Motivo
                Text("Motivo", color = AzulClaro, fontWeight = FontWeight.Bold)
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
                        motivos.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria.categoria) },
                                onClick = {
                                    motivoSeleccionado = categoria.categoria
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

                        isLoading = true
                        resultSuccess = null

                        val egreso = EgresoRequest(
                            id_usuario = uid,
                            cantidad = cantidad.toFloat(),
                            motivo = motivoSeleccionado,
                            fecha = fecha
                        )

                        scope.launch {
                            try {
                                val response = RetrofitClient.api.registrarEgreso(egreso)
                                if (response.isSuccessful) {
                                    resultSuccess = true
                                    cantidad = ""
                                    motivoSeleccionado = ""
                                    fecha = ""
                                } else {
                                    resultSuccess = false
                                }
                            } catch (e: Exception) {
                                resultSuccess = false
                            } finally {
                                delay(1500)
                                isLoading = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmarilloMostaza),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                ) {
                    Text("Registrar", color = Color.White, fontSize = 18.sp)
                }

                AnimatedVisibility(
                    visible = isLoading,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier.padding(top = 20.dp)
                ) {
                    CircularProgressIndicator(color = AzulClaro)
                }

                AnimatedVisibility(
                    visible = resultSuccess != null && !isLoading,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.padding(top = 20.dp)
                ) {
                    if (resultSuccess == true) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gasto registrado correctamente", color = Color.Green)
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Registro erróneo", color = Color.Red)
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

