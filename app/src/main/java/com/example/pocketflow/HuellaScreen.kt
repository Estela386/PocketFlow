package com.example.pocketflow

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pocketflow.ui.theme.AnimatedWaveBackground
import com.example.pocketflow.ui.theme.AzulClaro
import com.example.pocketflow.ui.theme.BottomNavigationBar
import com.example.pocketflow.ui.theme.TopBar

@Composable
fun HuellaScreen(navController: NavHostController) {
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
                    .padding(55.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Impacto Ambiental",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = AzulClaro,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp, bottom = 16.dp)
                )
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

@Composable
fun CircularImpactChart(proportions: List<Float>) {
    val colors = listOf(Color.Red, Color.Yellow, Color.Green, Color(0xFF6BFF9A))

    Canvas(modifier = Modifier.size(180.dp)) {
        var startAngle = -90f
        proportions.forEachIndexed { index, proportion ->
            val sweep = proportion * 360f
            drawArc(
                color = colors[index],
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = 20f, cap = StrokeCap.Round)
            )
            startAngle += sweep
        }
    }
}

@Composable
fun GastoItem(label: String, borderColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(4.dp, borderColor, CircleShape)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(label)
    }
}

fun getColorFor(value: Float): Color {
    return when {
        value > 0.3f -> Color.Red
        value > 0.2f -> Color.Yellow
        else -> Color.Green
    }
}
