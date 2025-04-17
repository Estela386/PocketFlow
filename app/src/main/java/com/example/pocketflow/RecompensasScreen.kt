package com.example.pocketflow

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pocketflow.ui.theme.CallingCode
import com.example.pocketflow.ui.theme.AnimatedWaveBackground
import com.example.pocketflow.ui.theme.BottomNavigationBar


@Composable
fun RecompensasScreen(navController: NavHostController) {

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
                .padding(paddingValues) // importante para no tapar el contenido con el BottomNavigationBar
        ) {
            AnimatedWaveBackground()

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                val screenWidth = maxWidth
                val circleSize = screenWidth * 0.6f

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(30.dp, Alignment.CenterVertically)
                ) {
                    Text(
                        text = "Tus Metas",
                        fontSize = 28.sp,
                        fontFamily = CallingCode,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF222222)
                    )

                    LiquidProgressCircle(
                        progress = 0.4f,
                        modifier = Modifier.size(circleSize)
                    )

                    Text(
                        text = "Últimos logros completados",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF555555)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        GoalItem("No gastos innecesarios el fin de semana", true)
                        GoalItem("No comprar en línea por 15 días", true)
                        GoalItem("Usar transporte público en lugar de Uber", true)
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
fun GoalItem(text: String, isCompleted: Boolean) {
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
            if (isCompleted) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Completado",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
