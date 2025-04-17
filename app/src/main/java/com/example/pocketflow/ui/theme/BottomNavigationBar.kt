package com.example.pocketflow.ui.theme

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pocketflow.R

@Composable
fun BottomNavigationBar(navController: NavController, currentRoute: String?) {
    val items = listOf("ingreso", "analisis", "escaner", "huella", "egreso")
    val selectedIndex = items.indexOf(currentRoute ?: "ingreso")

    // Movimiento animado del circulito amarillo
    val indicatorOffset by animateDpAsState(
        targetValue = (selectedIndex * 64).dp, // espacio entre items
        label = "IndicatorOffset"
    )

    Box {
        // Círculo amarillo que se mueve
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset + 16.dp, y = 8.dp) // ajustar centrado
                .size(48.dp)
                .clip(MaterialTheme.shapes.large)
                .background(Color(0xFFFFC107))
                .align(Alignment.TopStart)
        )

        NavigationBar(
            containerColor = Color(0xFFF2F2F2),
            tonalElevation = 8.dp,
            modifier = Modifier.height(80.dp)
        ) {
            items.forEach { route ->
                NavigationBarItem(
                    icon = {
                        val iconId = when (route) {
                            "ingreso" -> R.drawable.ingresos
                            "analisis" -> R.drawable.analisis
                            "escaner" -> R.drawable.camara
                            "huella" -> R.drawable.huella
                            "egreso" -> R.drawable.gastos
                            else -> R.drawable.ic_launcher_foreground
                        }

                        Icon(
                            painter = painterResource(id = iconId),
                            contentDescription = route,
                            modifier = Modifier.size(32.dp),
                            tint = Color.Black
                        )
                    },
                    selected = currentRoute == route,
                    onClick = { navController.navigate(route) }
                )
            }
        }
    }
}
