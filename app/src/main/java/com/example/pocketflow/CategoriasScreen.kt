package com.example.pocketflow

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.*
import com.example.pocketflow.ui.theme.AnimatedWaveBackground

@Composable
fun CategoriasScreen() {

    var showDialog by remember { mutableStateOf(false) }
    var nombreCategoria by remember { mutableStateOf("") }
    var descripcionCategoria by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedWaveBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(55.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Fila superior con botón de volver
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* Volver */ }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Título
            Text(
                text = "Mis Categorías",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A237E),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 8.dp, top = 8.dp, bottom = 16.dp)
            )

            // Botones Agregar y Eliminar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { /* Acción eliminar */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8AB4CC)),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "Eliminar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar")
                }

                Button(
                    onClick = { showDialog = true }, // Mostrar el modal al presionar
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8AB4CC)),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lista de categorías
            val categorias = listOf("Casa", "Personal", "Trabajo", "Seguro", "Colegiatura", "Mascotas")
            categorias.forEach { CategoriaItem(it) }

            Spacer(modifier = Modifier.weight(1f))

            // Aquí podrías colocar el menú
        }

        // Modal de formulario
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(text = "Nueva Categoría") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = nombreCategoria,
                            onValueChange = { nombreCategoria = it },
                            label = { Text("Nombre de la categoría") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = descripcionCategoria,
                            onValueChange = { descripcionCategoria = it },
                            label = { Text("Descripción breve") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Aquí puedes guardar la categoría en tu lista o base de datos
                            println("Nombre: $nombreCategoria")
                            println("Descripción: $descripcionCategoria")

                            // Limpia los campos y cierra el dialog
                            nombreCategoria = ""
                            descripcionCategoria = ""
                            showDialog = false
                        }
                    ) {
                        Text("Agregar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            // Limpiar y cerrar
                            nombreCategoria = ""
                            descripcionCategoria = ""
                            showDialog = false
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}


