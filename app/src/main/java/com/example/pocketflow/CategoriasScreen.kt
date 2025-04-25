package com.example.pocketflow

import android.app.Application
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pocketflow.ui.theme.AnimatedWaveBackground
import com.example.pocketflow.ui.theme.BottomNavigationBar
import com.example.pocketflow.ui.theme.CategoriasViewModel
import com.example.pocketflow.ui.theme.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriasScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel: CategoriasViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return CategoriasViewModel(context.applicationContext as Application) as T
            }
        }
    )

    val categorias = viewModel.categorias.value

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    var nombreCategoria by remember { mutableStateOf("") }
    var descripcionCategoria by remember { mutableStateOf("") }
    var clasificacionCategoria by remember { mutableStateOf("Gastos") }

    var idCategoriaSeleccionada by remember { mutableStateOf<String?>(null) }

    val opciones = listOf("Gastos", "Ingresos")

    val sharedPreferences = context.getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE)
    val uidUsuario = sharedPreferences.getString("uid_usuario", "") ?: ""

    LaunchedEffect(Unit) {
        viewModel.cargarCategorias()
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(55.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                Text(
                    text = "Mis Categorías",
                    fontSize = 35.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 8.dp, top = 8.dp, bottom = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            if (idCategoriaSeleccionada != null) {
                                showDeleteDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8AB4CC)),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Eliminar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Eliminar")
                    }

                    Button(
                        onClick = {
                            nombreCategoria = ""
                            descripcionCategoria = ""
                            clasificacionCategoria = "Gastos"
                            showAddDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8AB4CC)),
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agregar")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                LazyColumn {
                    items(categorias) { categoria ->
                        CategoriaItem(
                            nombre = categoria.categoria,
                            descripcion = "${categoria.descripcion ?: "Sin descripción"}\nClasificación: ${categoria.clasificacion}",
                            onClick = {
                                idCategoriaSeleccionada = categoria.id
                                nombreCategoria = categoria.categoria
                                descripcionCategoria = categoria.descripcion ?: ""
                                clasificacionCategoria = categoria.clasificacion
                                showEditDialog = true
                            }
                        )
                    }
                }

                //Diálogo Agregar Categoría
                if (showAddDialog) {
                    AlertDialog(
                        onDismissRequest = { showAddDialog = false },
                        title = { Text("Agregar Categoría") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = nombreCategoria,
                                    onValueChange = { nombreCategoria = it },
                                    label = { Text("Nombre") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = descripcionCategoria,
                                    onValueChange = { descripcionCategoria = it },
                                    label = { Text("Descripción") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                var expanded by remember { mutableStateOf(false) }

                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded }
                                ) {
                                    OutlinedTextField(
                                        value = clasificacionCategoria,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Clasificación") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                        },
                                        modifier = Modifier.menuAnchor()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        opciones.forEach { opcion ->
                                            DropdownMenuItem(
                                                text = { Text(opcion) },
                                                onClick = {
                                                    clasificacionCategoria = opcion
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.crearCategoria(
                                    nombreCategoria,
                                    descripcionCategoria,
                                    clasificacionCategoria
                                )
                                showAddDialog = false
                            }) {
                                Text("Guardar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAddDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }

                //Diálogo Editar Categoría
                if (showEditDialog) {
                    AlertDialog(
                        onDismissRequest = { showEditDialog = false },
                        title = { Text("Editar Categoría") },
                        text = {
                            Column {
                                OutlinedTextField(
                                    value = nombreCategoria,
                                    onValueChange = { nombreCategoria = it },
                                    label = { Text("Nombre") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = descripcionCategoria,
                                    onValueChange = { descripcionCategoria = it },
                                    label = { Text("Descripción") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                var expanded by remember { mutableStateOf(false) }

                                ExposedDropdownMenuBox(
                                    expanded = expanded,
                                    onExpandedChange = { expanded = !expanded }
                                ) {
                                    OutlinedTextField(
                                        value = clasificacionCategoria,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("Clasificación") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                        },
                                        modifier = Modifier.menuAnchor()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        opciones.forEach { opcion ->
                                            DropdownMenuItem(
                                                text = { Text(opcion) },
                                                onClick = {
                                                    clasificacionCategoria = opcion
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                idCategoriaSeleccionada?.let { id ->
                                    viewModel.editarCategoria(
                                        id,
                                        nombreCategoria,
                                        descripcionCategoria,
                                        clasificacionCategoria
                                    )
                                }
                                showEditDialog = false
                            }) {
                                Text("Actualizar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showEditDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }

                //Diálogo Confirmar Eliminación
                if (showDeleteDialog) {
                    AlertDialog(
                        onDismissRequest = { showDeleteDialog = false },
                        title = { Text("Confirmar eliminación") },
                        text = { Text("¿Estás seguro de que deseas eliminar esta categoría?") },
                        confirmButton = {
                            TextButton(onClick = {
                                idCategoriaSeleccionada?.let { id ->
                                    viewModel.eliminarCategoria(uidUsuario, id)
                                    idCategoriaSeleccionada = null
                                }
                                showDeleteDialog = false
                            }) {
                                Text("Eliminar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDeleteDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }

            }
        }
    }
}
