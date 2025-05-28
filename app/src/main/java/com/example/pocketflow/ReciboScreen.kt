package com.example.pocketflow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pocketflow.ui.theme.AnimatedWaveBackground
import com.example.pocketflow.ui.theme.BottomNavigationBar
import com.example.pocketflow.ui.theme.TopBar

@Composable
fun ReciboScreen(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    var showTips by remember { mutableStateOf(false) }
    var selectedPeriod by remember { mutableStateOf("Mes") }

    val monthlyData = listOf(0.17f, 0.33f, 0.34f, 0.16f) // Datos de impacto mensual
    val weeklyData = listOf(0.25f, 0.30f, 0.30f, 0.15f) // Datos de impacto semanal

    val tips = listOf(
        "Usa transporte público o bicicleta cuando sea posible.",
        "Reduce el consumo de carne roja.",
        "Evita el uso de plásticos desechables.",
        "Desconecta aparatos que no utilices.",
        "Ahorra agua con duchas más cortas."
    )

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
            AnimatedWaveBackground() // Fondo animado
            TopBar(navController)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                // Título principal
                Text("Tu impacto Ambiental", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                Box(contentAlignment = Alignment.Center) {
                    CircularImpactChart(proportions = if (selectedPeriod == "Mes") monthlyData else weeklyData)
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.Blue) // Cambia por tu imagen si deseas
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Botones para mostrar tips o gastos
                    Button(onClick = { showTips = !showTips }, shape = CircleShape) {
                        Text(if (showTips) "Gastos" else "Tips")
                    }
                    Button(
                        onClick = {
                            selectedPeriod = if (selectedPeriod == "Mes") "Semana" else "Mes"
                        },
                        shape = CircleShape
                    ) {
                        Text(selectedPeriod)
                    }
                }

                // Mostrar los tips si la opción está activa
                if (showTips) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        tips.forEach { tip ->
                            Text("• $tip", fontSize = 14.sp)
                        }
                    }
                } else {
                    // Mostrar los gastos si los tips están ocultos
                    Text("Mis gastos (${selectedPeriod.lowercase()})", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val selectedData = if (selectedPeriod == "Mes") monthlyData else weeklyData
                        GastoItem("Transporte", getColorFor(selectedData[0]))
                        GastoItem("Comida", getColorFor(selectedData[1]))
                        GastoItem("Compras", getColorFor(selectedData[2]))
                    }
                }
            }
        }
    }
}
