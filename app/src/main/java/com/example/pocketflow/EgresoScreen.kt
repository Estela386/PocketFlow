package com.example.pocketflow

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pocketflow.ui.theme.AnimatedWaveBackground
import com.example.pocketflow.ui.theme.BottomNavigationBar
import com.example.pocketflow.ui.theme.TopBar
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EgresoScreen(navController: NavHostController) {
    val context = LocalContext.current
    var fecha by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var motivoSeleccionado by remember { mutableStateOf("") }

    val motivos = listOf("Pago", "Comida", "Transporte", "Entretenimiento", "Otro")
    val calendar = Calendar.getInstance()
    val showDatePicker = remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (showDatePicker.value) {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                val formattedDate = String.format("%02d/%02d/%d", day, month + 1, year)
                fecha = formattedDate
                showDatePicker.value = false
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.maxDate = System.currentTimeMillis()
            show()
        }
    }

    Scaffold(
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

            // Calculamos el tamaño del texto con LocalDensity
            val density = LocalDensity.current.density
            val fontSize = with(LocalDensity.current) { 22.sp.toPx() / density } // Convertimos sp a px
            val fontSizeTextField = with(LocalDensity.current) { 16.sp.toPx() / density } // Para campos de texto

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Registrar Egreso",
                    fontSize = 30.sp,  // Usamos un tamaño de fuente adaptativo
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C2D44),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Fecha
                Text("Fecha", color = Color(0xFF1C2D44), fontWeight = FontWeight.Bold, fontSize = 25.sp)
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = fecha,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("dd/mm/aaaa", color = Color.Gray) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFFCFCFC),
                        unfocusedContainerColor = Color(0xFFFCFCFC),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clickable { showDatePicker.value = true }
                        .background(
                            color = Color(0xFFF0F4F8), shape = RoundedCornerShape(12.dp)
                        )
                        .shadow(8.dp, shape = RoundedCornerShape(12.dp)),
                    textStyle = TextStyle(fontSize = fontSizeTextField.sp)  // Ajustamos el tamaño de texto en los TextField
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Cantidad
                Text("Cantidad", color = Color(0xFF1C2D44), fontWeight = FontWeight.Bold, fontSize = 25.sp)
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    value = cantidad,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() || char == '.' }) cantidad = it
                    },
                    placeholder = { Text("$0.00", color = Color.Gray) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFFCFCFC),
                        unfocusedContainerColor = Color(0xFFFCFCFC),
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        cursorColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(
                            color = Color(0xFFF0F4F8), shape = RoundedCornerShape(12.dp)
                        )
                        .shadow(8.dp, shape = RoundedCornerShape(12.dp)),
                    textStyle = TextStyle(fontSize = fontSizeTextField.sp)  // Ajustamos el tamaño de texto en los TextField
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Motivo Dropdown
                Text("Motivo", color = Color(0xFF1C2D44), fontWeight = FontWeight.Bold, fontSize = 25.sp)
                Spacer(modifier = Modifier.height(10.dp))
                var expanded by remember { mutableStateOf(false) }

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
                            focusedContainerColor = Color(0xFFFCFCFC),
                            unfocusedContainerColor = Color(0xFFFCFCFC),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(
                                color = Color(0xFFF0F4F8), shape = RoundedCornerShape(12.dp)
                            )
                            .shadow(8.dp, shape = RoundedCornerShape(12.dp)),
                        textStyle = TextStyle(fontSize = fontSizeTextField.sp)  // Ajustamos el tamaño de texto en los TextField
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

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        // Lógica de registro
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA3CFE3)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .shadow(12.dp, shape = RoundedCornerShape(12.dp))
                ) {
                    Text("Registrar", color = Color.Black, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(24.dp)) // Añadí un poco de espacio debajo del botón
            }
        }
    }
}

