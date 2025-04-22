package com.example.pocketflow

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.navigation.NavHostController
import com.example.pocketflow.ui.theme.AnimatedWaveBackground
import com.example.pocketflow.ui.theme.TopBar

@Composable
fun AnalisisScreen(navController: NavHostController) {

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedWaveBackground()
        TopBar(navController)

    }
}
