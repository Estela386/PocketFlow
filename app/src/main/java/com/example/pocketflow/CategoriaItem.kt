package com.example.pocketflow

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoriaItem(nombre: String, descripcion: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE7EEF1)
        )
    ) {
        // Ajuste para permitir scroll pero limitar el tamaño
        Column(
            modifier = Modifier
                .padding(16.dp)
                .heightIn(max = 300.dp) // Limitar la altura máxima
                .verticalScroll(rememberScrollState()) // Agregado para habilitar el scroll
        ) {
            Text(
                text = nombre,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = descripcion,
                color = Color.DarkGray,
                fontSize = 14.sp
            )
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}
