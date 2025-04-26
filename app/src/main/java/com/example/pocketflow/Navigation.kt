package com.example.pocketflow

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavigation(navController: NavHostController) {
    val scanViewModel: ScanViewModel = viewModel()

    NavHost(navController = navController, startDestination = "escaner") {
        composable("escaner") {
            EscanerScreen(navController = navController, scanViewModel = scanViewModel)
        }
        composable("recibo") {
            ReciboScreen(navController = navController, scanViewModel = scanViewModel)
        }
    }
}

