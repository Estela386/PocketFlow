package com.example.pocketflow.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun TopBar(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fila superior
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón de atrás
            IconButton(onClick = { navController.navigate("inicio") }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.Black)
            }
            // Botón de recompensas
            IconButton(onClick = { navController.navigate("recompensas") }) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Idea",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(60.dp)
                )
            }
        }
    }
}
