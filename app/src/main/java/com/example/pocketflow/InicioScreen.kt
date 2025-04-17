package com.example.pocketflow

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun InicioScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF697EB9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HeaderSection(navController)
            IncomeExpenseSection(navController)
            OptionsGrid(navController)
            FooterSection()
        }
    }
}

@Composable
fun HeaderSection(navController: NavHostController) {
    Surface(
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFBCE6F0), Color(0xFFE4F7FA))
                    )
                )
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navController.navigate("recompensas") }) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Idea",
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(60.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Hola, Ana",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C2633),
                    modifier = Modifier.weight(1f)
                )
                Image(
                    painter = painterResource(id = R.drawable.perfil),
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .clickable {
                            navController.navigate("perfil")
                        }
                )

            }
        }
    }
}

@Composable
fun IncomeExpenseSection(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IncomeExpenseButton("GASTOS", R.drawable.gastos) {
            navController.navigate("egreso")
        }
        IncomeExpenseButton("INGRESOS", R.drawable.ingresos) {
            navController.navigate("ingreso")
        }
    }
}

@Composable
fun IncomeExpenseButton(text: String, icon: Int, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "buttonScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .background(Color(0xFFFFE082), RoundedCornerShape(16.dp))
            .clickable(interactionSource = interactionSource, indication = null) {
                onClick()
            }
            .padding(12.dp)
            .width(130.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = text,
            modifier = Modifier.size(60.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = text,
            color = Color(0xFF1C2633),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun OptionsGrid(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OptionCard("Análisis", R.drawable.analisis, Modifier.weight(1f)) {
                navController.navigate("analisis")
            }
            OptionCard("Predicciones", R.drawable.predicciones, Modifier.weight(1f)) {
                navController.navigate("predicciones")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OptionCard("Mi huella CO2", R.drawable.huella, Modifier.weight(1f)) {
                navController.navigate("huella")
            }
            OptionCard("Escanear", R.drawable.camara, Modifier.weight(1f)) {
                navController.navigate("escaner")
            }
        }
    }
}

@Composable
fun OptionCard(title: String, icon: Int, modifier: Modifier, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 8.dp,
        label = "cardElevation"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .aspectRatio(1f)
            .shadow(elevation, RoundedCornerShape(20.dp))
            .clickable(interactionSource = interactionSource, indication = null) {
                onClick()
            },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = title,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                color = Color(0xFF1C2633),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun FooterSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFFBCE6F0), Color(0xFFE4F7FA))
                ),
                shape = RoundedCornerShape(60.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "PocketFlow®",
            color = Color(0xFF1C2633),
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )
    }
}
