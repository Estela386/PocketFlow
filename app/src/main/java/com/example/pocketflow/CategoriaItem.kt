package com.example.pocketflow

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun CategoriaItem(nombre: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp), // sombra suave
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF90B7C9) // Azul suave
        )
    ) {
        Text(
            text = nombre,
            modifier = Modifier
                .padding(16.dp),
            color = Color(0xFF000000), // Negro: texto principal
            fontSize = 18.sp
        )
    }
}



