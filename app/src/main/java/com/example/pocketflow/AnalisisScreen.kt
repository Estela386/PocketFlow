package com.example.pocketflow

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.pocketflow.data.local.UserPreferences
import com.example.pocketflow.data.remote.ApiService
import com.example.pocketflow.data.remote.models.EgresoRequest
import com.example.pocketflow.data.remote.models.IngresoRequest
import com.example.pocketflow.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random
//import org.jetbrains.compose.charts.pie.PieChart
//import org.jetbrains.compose.charts.pie.PieChartData
//import org.jetbrains.compose.charts.pie.PieChartStyle

@Composable
fun AnalisisScreen(
    navController: NavHostController,
    apiService: ApiService,
    context: Context
) {
    val scope = rememberCoroutineScope()
    val ingresos = remember { mutableStateListOf<IngresoRequest>() }
    val egresos = remember { mutableStateListOf<EgresoRequest>() }
    var periodo by remember { mutableStateOf("Mensual") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val userPreferences = remember { UserPreferences(context) }
    val uid = userPreferences.getUid() ?: ""

    LaunchedEffect(uid) {
        scope.launch {
            try {
                ingresos.clear()
                egresos.clear()
                val ingresosResponse = apiService.getIngresos(uid)
                val egresosResponse = apiService.getEgresos(uid)
                ingresos.addAll(ingresosResponse)
                egresos.addAll(egresosResponse)
                Log.d("AnalisisScreen", "Ingresos cargados: ${ingresos.size}")
                Log.d("AnalisisScreen", "Egresos cargados: ${egresos.size}")
            } catch (e: Exception) {
                errorMsg = "Error al cargar datos: ${e.message}"
                Log.e("AnalisisScreen", "Excepción: ${e.message}")
            }
        }
    }

    val ingresosFiltrados = filtrarPorPeriodoIngreso(ingresos, periodo)
    val egresosFiltrados = filtrarPorPeriodoEgreso(egresos, periodo)
    val ingresosPorCategoria = agruparIngresosPorCategoria(ingresosFiltrados)
    val egresosPorCategoria = agruparEgresosPorCategoria(egresosFiltrados)

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedWaveBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            TopBar(navController)

            Text(
                text = "Análisis",
                modifier = Modifier
                    .padding(top = 32.dp, start = 16.dp, bottom = 8.dp),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = AzulOscuro
            )

            PeriodSelector(periodo) { periodo = it }

            errorMsg?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (ingresosPorCategoria.isNotEmpty()) {
                Text(
                    text = "Gráfica de Ingresos",
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                //PieChartView(ingresosPorCategoria)

                AnalisisCard("Ingresos", ingresosPorCategoria)
            } else {
                Text(
                    text = "No hay ingresos para mostrar.",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (egresosPorCategoria.isNotEmpty()) {
                Text(
                    text = "Gráfica de Egresos",
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                //PieChartView(egresosPorCategoria)

                AnalisisCard("Egresos", egresosPorCategoria)
            } else {
                Text(
                    text = "No hay egresos para mostrar.",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

/*@Composable
fun PieChartView(data: Map<String, Float>) {
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.error,
        MaterialTheme.colorScheme.outline
    )

    val entries = data.entries.toList()
    val chartData = PieChartData(
        slices = entries.mapIndexed { index, entry ->
            PieChartData.Slice(
                value = entry.value.toDouble(),
                label = entry.key,
                color = colors[index % colors.size]
            )
        }
    )

    Box(modifier = Modifier.fillMaxWidth().height(250.dp).padding(16.dp)) {
        PieChart(data = chartData, style = PieChartStyle())
    }
}*/


// Funciones de filtro y agrupación (igual que antes)

fun filtrarPorPeriodoIngreso(lista: List<IngresoRequest>, periodo: String): List<IngresoRequest> {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val limite = Calendar.getInstance().apply {
        when (periodo) {
            "Semanal" -> add(Calendar.DAY_OF_YEAR, -7)
            "Mensual" -> add(Calendar.MONTH, -1)
            "Anual" -> add(Calendar.YEAR, -1)
        }
    }
    return lista.filter {
        val fecha = sdf.parse(it.fecha)
        fecha != null && fecha.after(limite.time)
    }
}

fun filtrarPorPeriodoEgreso(lista: List<EgresoRequest>, periodo: String): List<EgresoRequest> {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val limite = Calendar.getInstance().apply {
        when (periodo) {
            "Semanal" -> add(Calendar.DAY_OF_YEAR, -7)
            "Mensual" -> add(Calendar.MONTH, -1)
            "Anual" -> add(Calendar.YEAR, -1)
        }
    }
    return lista.filter {
        val fecha = sdf.parse(it.fecha)
        fecha != null && fecha.after(limite.time)
    }
}

fun agruparIngresosPorCategoria(lista: List<IngresoRequest>): Map<String, Float> {
    return lista.groupBy { it.motivo }
        .mapValues { (_, items) ->
            items.map { it.cantidad }.sum()
        }
}

fun agruparEgresosPorCategoria(lista: List<EgresoRequest>): Map<String, Float> {
    return lista.groupBy { it.motivo }
        .mapValues { (_, items) ->
            items.map { it.cantidad }.sum()
        }
}

@Composable
fun AnalisisCard(titulo: String, datos: Map<String, Float>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AzulClaro)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = titulo,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            datos.forEach { (categoria, total) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = categoria, color = Color.White)
                    Text(text = "$${"%.2f".format(total)}", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun PeriodSelector(selected: String, onPeriodChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        listOf("Semanal", "Mensual", "Anual").forEach { label ->
            Button(
                onClick = { onPeriodChange(label) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selected == label) AmarilloMostaza else Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(label, color = if (selected == label) Color.White else AzulOscuro)
            }
        }
    }
}





