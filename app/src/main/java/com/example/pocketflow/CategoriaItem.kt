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
        colors = CardDefaults.cardColors(containerColor = Color(0xFF8AB4CC))
    ) {
        Text(
            text = nombre,
            modifier = Modifier
                .padding(16.dp),
            color = Color.Black,
            fontSize = 18.sp
        )
    }
}

