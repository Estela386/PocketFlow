package com.example.pocketflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PocketFlowApp()
        }
    }
}

@Composable
fun PocketFlowApp() {
    val navController = rememberNavController()
    val scanViewModel = remember { ScanViewModel() }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("perfil") { PerfilScreen(navController) }
        composable("categorias") { CategoriasScreen(navController) }
        composable("recompensas") { RecompensasScreen(navController) }
        composable("analisis") { AnalisisScreen(navController) }
        composable("egreso") { EgresoScreen(navController) }
        composable("huella") { HuellaScreen(navController) }
        composable("ingreso") { IngresoScreen(navController) }
        composable("inicio") { InicioScreen(navController) }
        composable("login") { LoginScreen() }
        composable("predicciones") { PrediccionesScreen(navController) }
        composable("registro") { RegistroScreen() }

        // Aquí van las pantallas que usan el ViewModel
        composable("escaner") { EscanerScreen(navController, scanViewModel) }
        composable("recibo") { ReciboScreen(navController, scanViewModel) }
    }
}

