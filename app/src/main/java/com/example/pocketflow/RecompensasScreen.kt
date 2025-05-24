package com.example.pocketflow

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pocketflow.ui.theme.CallingCode
import com.example.pocketflow.ui.theme.AnimatedWaveBackground
import com.example.pocketflow.ui.theme.BottomNavigationBar
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import com.example.pocketflow.ui.theme.TopBar

@Composable
fun RecompensasScreen(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val context = LocalContext.current
    var goals by remember { mutableStateOf(mutableListOf<String>()) }
    var progress by remember { mutableStateOf(0f) }
    var showCongrats by remember { mutableStateOf(false) }

    // Dialog para agregar meta
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var newGoalText by remember { mutableStateOf("") }

    // Efecto para mostrar felicitación y reiniciar
    if (showCongrats) {
        LaunchedEffect(Unit) {
            Toast.makeText(context, "¡Felicidades por lograr tus metas!", Toast.LENGTH_LONG).show()
            kotlinx.coroutines.delay(2000)
            goals = mutableListOf()
            progress = 0f
            showCongrats = false
        }
    }

    if (showAddGoalDialog) {
        AlertDialog(
            onDismissRequest = { showAddGoalDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newGoalText.isNotBlank()) {
                            goals = (goals + newGoalText).toMutableList()
                            newGoalText = ""
                            showAddGoalDialog = false
                        }
                    }
                ) { Text("Agregar") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddGoalDialog = false }
                ) { Text("Cancelar") }
            },
            title = { Text("Agregar nuevo objetivo") },
            text = {
                OutlinedTextField(
                    value = newGoalText,
                    onValueChange = { newGoalText = it },
                    label = { Text("Nuevo objetivo") }
                )
            }
        )
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, end = 24.dp, top = 10.dp)
            ) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Tus Metas",
                    fontSize = 28.sp,
                    fontFamily = CallingCode,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Circle with progress and trophy
                LiquidProgressCircle(
                    progress = progress,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(220.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "Últimos logros completados",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF555555),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Fila de botones - y +
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (goals.isNotEmpty()) goals = goals.dropLast(1).toMutableList()
                        },
                        enabled = goals.isNotEmpty()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.menos),
                            contentDescription = "Eliminar objetivo",
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { showAddGoalDialog = true }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.mas),
                            contentDescription = "Agregar objetivo",
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Lista de metas
                if (goals.isEmpty()) {
                    Text(
                        text = "Agrega tu primer objetivo con el botón +",
                        color = Color(0xFF888888),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        goals.forEachIndexed { idx, goal ->
                            GoalItem(
                                text = goal,
                                isCompleted = false,
                                onStarClick = {
                                    // Al presionar la estrella, aumentar el progreso
                                    if (progress < 1f) {
                                        val step = if (goals.isNotEmpty()) 1f / goals.size else 1f
                                        progress = (progress + step).coerceAtMost(1f)
                                        if (progress >= 1f) showCongrats = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiquidProgressCircle(progress: Float, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveTransition")
    val waveShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4000,
                easing = CubicBezierEasing(0.25f, 0.8f, 0.25f, 1f)
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaveShift"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val radius = size.minDimension / 2f

            drawCircle(
                color = Color(0xFFE0E0E0),
                radius = radius,
                center = center,
                style = Stroke(width = radius * 0.1f)
            )

            clipPath(Path().apply { addOval(Rect(center = center, radius = radius)) }) {
                val waveHeight = size.height * 0.1f
                val waterLevel = size.height * (1 - progress)

                val path = Path().apply {
                    moveTo(0f, waterLevel)

                    val waveLength = size.width
                    val numberOfWaves = 3

                    for (i in 0..numberOfWaves) {
                        val startX = i * waveLength / numberOfWaves
                        val endX = startX + waveLength / numberOfWaves
                        val middleX = (startX + endX) / 2

                        val offsetY = { x: Float ->
                            val sineWave = Math.sin(x / waveLength * 2 * Math.PI).toFloat()
                            val randomNoise = (Math.random().toFloat() - 0.1f) * 0.01f
                            sineWave + randomNoise
                        }

                        cubicTo(
                            middleX - waveLength / 8 + shiftOffset(waveShift, waveLength),
                            waterLevel - waveHeight + offsetY(startX) * waveHeight,
                            middleX + waveLength / 8 + shiftOffset(waveShift, waveLength),
                            waterLevel + waveHeight + offsetY(endX) * waveHeight,
                            endX, waterLevel
                        )
                    }

                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }

                drawPath(
                    path = path,
                    color = Color(0xFF8AB4CC)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.trophy),
                contentDescription = "Trofeo",
                tint = Color.Unspecified,
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${(progress * 100).toInt()}% completado",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333)
            )
        }
    }
}

fun shiftOffset(shift: Float, waveLength: Float): Float = shift * waveLength

@Composable
fun GoalItem(
    text: String,
    isCompleted: Boolean,
    onStarClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                color = Color(0xFF222222)
            )
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Completado",
                tint = Color(0xFFFFD700),
                modifier = Modifier
                    .size(22.dp)
                    .clickable { onStarClick() }
            )
        }
    }
}