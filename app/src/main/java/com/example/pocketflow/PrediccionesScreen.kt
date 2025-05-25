package com.example.pocketflow

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.example.pocketflow.data.remote.RetrofitClient
import com.example.pocketflow.data.remote.models.Prediccion
import com.example.pocketflow.ui.theme.*
import com.example.pocketflow.data.local.UserPreferences
import kotlinx.coroutines.launch
import androidx.compose.ui.viewinterop.AndroidView
import java.util.*

@Composable
fun PrediccionesScreen(navController: NavController) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }

    var periodoSeleccionado by remember { mutableStateOf("semana") }
    var datosPorMotivo by remember { mutableStateOf(listOf<Prediccion>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(periodoSeleccionado) {
        isLoading = true
        val uid = userPrefs.getUid()
        try {
            val response = RetrofitClient.api.obtenerPredicciones(uid, periodoSeleccionado)
            datosPorMotivo = response
        } catch (e: Exception) {
            Toast.makeText(context, "Error al obtener predicciones", Toast.LENGTH_LONG).show()
        }
        isLoading = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedWaveBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            TopBar(navController)

            Text(
                text = "Predicciones",
                fontSize = 28.sp,
                color = AzulClaro,
                modifier = Modifier.padding(start = 16.dp, top = 32.dp, bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("semana", "mes", "anio").forEach { label ->
                    Button(
                        onClick = { periodoSeleccionado = label },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (periodoSeleccionado == label) AzulClaro else Color.LightGray
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Text(
                            label.replaceFirstChar { it.titlecase(Locale.ROOT) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 40.dp)
                )
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Comparación por Categoría",
                            fontSize = 18.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        BarChartPredicciones(datosPorMotivo)
                    }
                }

                // Card con los datos detallados
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Datos Detallados",
                            fontSize = 18.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Column(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            datosPorMotivo.forEach { pred ->
                                Text(
                                    text = "${pred.motivo}: Real \$${pred.cantidad_real} - Predicho \$${pred.cantidad_predicha}",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun BarChartPredicciones(data: List<Prediccion>) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        factory = { context ->
            val chart = BarChart(context)

            val realEntries = ArrayList<BarEntry>()
            val predEntries = ArrayList<BarEntry>()
            val labels = ArrayList<String>()

            data.forEachIndexed { index, item ->
                realEntries.add(BarEntry(index.toFloat(), item.cantidad_real))
                predEntries.add(BarEntry(index.toFloat(), item.cantidad_predicha))
                labels.add(item.motivo)
            }

            val dataSetReal = BarDataSet(realEntries, "Gasto real").apply {
                color = AzulClaro.toArgb()
            }

            val dataSetPred = BarDataSet(predEntries, "Predicción").apply {
                color = AmarilloMostaza.toArgb()
            }

            val barData = BarData(dataSetReal, dataSetPred)
            val groupSpace = 0.2f
            val barSpace = 0.05f
            val barWidth = 0.35f

            barData.barWidth = barWidth
            chart.data = barData
            chart.description.isEnabled = false
            chart.setFitBars(true)
            chart.axisRight.isEnabled = false
            chart.legend.isEnabled = true

            chart.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                granularity = 1f
                isGranularityEnabled = true
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM
                labelRotationAngle = -30f
                textColor = Color.Black.toArgb()
            }

            chart.axisLeft.textColor = Color.Black.toArgb()
            chart.groupBars(0f, groupSpace, barSpace)
            chart.invalidate()

            chart
        }
    )
}

