package com.example.pocketflow

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import android.view.ViewGroup
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.utils.ColorTemplate
import androidx.compose.ui.graphics.toArgb

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
            TopBar(
                navController = navController,
                modifier = Modifier.padding(top = 16.dp) // Puedes ajustar este valor
            )


            Text(
                text = "Análisis",
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
                color = AzulClaro,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 5.dp, bottom = 16.dp)
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
                PieChartView(ingresosPorCategoria)

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
                PieChartView(egresosPorCategoria)

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

@Composable
fun PieChartView(data: Map<String, Float>) {
    val chartColors = listOf(
        Pastel1.toArgb(),
        Pastel2.toArgb(),
        Pastel3.toArgb(),
        Pastel4.toArgb(),
        Pastel5.toArgb(),
        Pastel6.toArgb(),
        Pastel7.toArgb(),
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AzulClaro) // 🔵 Cambiado a AzulClaro
    ) {
        AndroidView(
            factory = { context ->
                PieChart(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        500
                    )
                    description.isEnabled = false
                    isDrawHoleEnabled = true
                    setHoleColor(android.graphics.Color.TRANSPARENT)
                    setUsePercentValues(false)
                    setEntryLabelColor(android.graphics.Color.TRANSPARENT) // ❌ Oculta etiquetas internas
                    setEntryLabelTextSize(0f)

                    legend.apply {
                        isEnabled = true
                        textSize = 14f
                        formSize = 14f
                        xEntrySpace = 10f
                        yEntrySpace = 5f
                    }
                }
            },
            update = { chart ->
                val entries = data.map { PieEntry(it.value, it.key) } // ✅ Usa key en leyenda, pero no en gráfico
                val dataSet = PieDataSet(entries, "").apply {
                    colors = chartColors.shuffled()
                    valueTextSize = 16f
                    valueTextColor = android.graphics.Color.WHITE
                    sliceSpace = 2f
                }

                chart.data = PieData(dataSet).apply {
                    setDrawValues(true)
                }

                chart.invalidate()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(300.dp)
        )
    }
}


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






