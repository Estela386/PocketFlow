package com.example.pocketflow.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@Composable
fun AnimatedWaveBackground() {
    var startAnimation by remember { mutableStateOf(false) }

    val offsetY by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 1000f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "waveAnimation"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    WaveShapeBackground(offsetY = offsetY)
}

@Composable
fun WaveShapeBackground(offsetY: Float) {
    Canvas(
        modifier = Modifier
            .offset { IntOffset(x = 0, y = offsetY.roundToInt()) }
            .fillMaxSize()
    ) {
        val width = size.width
        val height = size.height

        val waveHeight = 350f
        val curveHeight = 80f

        val path = Path().apply {
            moveTo(0f, waveHeight - curveHeight)
            quadraticBezierTo(
                width / 2, waveHeight + curveHeight,
                width, waveHeight - curveHeight
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1976D2), Color.White)
            )
        )
    }
}
