package com.example.pocketflow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pocketflow.ui.theme.AnimatedWaveBackground
import com.example.pocketflow.ui.theme.BottomNavigationBar
import com.example.pocketflow.ui.theme.TopBar
import com.example.pocketflow.Producto


data class ProductoRecibo(
    var nombre: String,
    var precio: String
)


@Composable
fun ReciboScreen(navController: NavHostController, scanViewModel: ScanViewModel) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val listaItems by scanViewModel.lineasTexto.collectAsState()

    // Convertimos el texto a lista de productos
    var productos by remember {
        mutableStateOf(
            listaItems.map { linea ->
                val partes = linea.split(Regex("\\s+(?=\\$)")) // Separar nombre y precio
                Producto(
                    nombre = partes.getOrNull(0)?.trim() ?: "",
                    precio = partes.getOrNull(1)?.trim() ?: "$0.00"
                )
            }
        )
    }
    var modoEdicion by remember { mutableStateOf(false) }

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
                    .padding(horizontal = 16.dp)
            ) {
                // Botón Repetir
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    TextButton(onClick = {
                        navController.popBackStack() // regresar para volver a escanear
                    }) {
                        Text("Repetir")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Título + Botón Editar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tu Recibo:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    TextButton(onClick = {
                        modoEdicion = !modoEdicion
                    }) {
                        Text(if (modoEdicion) "Guardar" else "Editar")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Lista de productos
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    itemsIndexed(productos) { index, producto ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (modoEdicion) {
                                var nombreEditable by remember { mutableStateOf(producto.nombre) }
                                var precioEditable by remember { mutableStateOf(producto.precio) }

                                BasicTextField(
                                    value = nombreEditable,
                                    onValueChange = { nuevoNombre ->
                                        nombreEditable = nuevoNombre
                                        productos = productos.toMutableList().also {
                                            it[index] = it[index].copy(nombre = nuevoNombre)
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                BasicTextField(
                                    value = precioEditable,
                                    onValueChange = { nuevoPrecio ->
                                        precioEditable = nuevoPrecio
                                        productos = productos.toMutableList().also {
                                            it[index] = it[index].copy(precio = nuevoPrecio)
                                        }
                                    },
                                    modifier = Modifier.width(80.dp)
                                )
                            } else {
                                Text(text = producto.nombre, modifier = Modifier.weight(1f))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = producto.precio)
                            }
                        }
                    }

                    // Total
                    item {
                        Spacer(modifier = Modifier.height(12.dp))

                        val total = productos.sumOf {
                            it.precio.replace("$", "").toDoubleOrNull() ?: 0.0
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "TOTAL $${"%.2f".format(total)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .background(Color.LightGray, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botones abajo
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = {
                        navController.navigate("menu") // Regresa al menú
                    }) {
                        Text("Cancelar")
                    }
                    Button(onClick = {
                        // Lógica para registrar (por ahora no hace nada)
                    }) {
                        Text("Registrar")
                    }
                }
            }
        }
    }
}
