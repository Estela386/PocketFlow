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
import com.example.pocketflow.data.remote.models.IngresoRequest
import com.example.pocketflow.data.remote.RetrofitClient
import com.example.pocketflow.data.remote.models.Categoria
import com.example.pocketflow.ui.theme.AmarilloMostaza
import com.example.pocketflow.ui.theme.AnimatedWaveBackground
import com.example.pocketflow.ui.theme.AzulClaro
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
    var isLoading by remember { mutableStateOf(false) }
    var categoriasList by remember { mutableStateOf<List<String>>(emptyList()) }
    var motivos by remember { mutableStateOf(listOf<Categoria>()) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val uid = userPrefs.getUid()

    LaunchedEffect(Unit) {
        val uid = userPrefs.getUid()
        try {
            val response = RetrofitClient.api.getCategoriasIngresos(uid ?: "")
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
                    "Registrar Ingreso",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C2D44),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cantidad con símbolo $
                Text("Cantidad", color = Color(0xFF1C2D44), fontWeight = FontWeight.Bold)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color(0xFFA3CFE3))
                        .padding(horizontal = 16.dp)
                ) {
                    Text("$", fontSize = 18.sp, color = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = cantidad,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() || char == '.' }) cantidad = it
                        },
                        placeholder = { Text("0.00", color = Color.Gray) },
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

                // Motivo dropdown
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

                // Fecha personalizada
                DatePickerFieldIn("Fecha", fecha) { fecha = it }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
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
                            isLoading = true
                            try {
                                val response = RetrofitClient.api.registrarIngreso(ingreso)
                                if (response.isSuccessful) {
                                    snackbarHostState.showSnackbar("Ingreso registrado correctamente")
                                    cantidad = ""
                                    motivoSeleccionado = ""
                                    fecha = ""
                                } else {
                                    snackbarHostState.showSnackbar("Error al registrar ingreso: ${response.code()}")
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Error de conexión: ${e.message}")
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AmarilloMostaza),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Registrar", color = Color.White, fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerFieldIn(label: String, date: String, onDateSelected: (String) -> Unit) {
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
            color = AzulClaro,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color(0xFFA3CFE3))
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
}

