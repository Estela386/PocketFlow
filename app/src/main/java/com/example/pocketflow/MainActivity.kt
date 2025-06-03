package com.example.pocketflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.example.pocketflow.data.remote.ApiService
import com.example.pocketflow.data.remote.RetrofitClient

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
        startDestination = "inicio" // La primera pantalla que se muestra
    ) {
        composable("perfil") {PerfilScreen(navController)}
        composable("categorias") {CategoriasScreen(navController)}
        composable("recompensas"){RecompensasScreen(navController)}
        composable("analisis") {
            val context = LocalContext.current
            val apiService = RetrofitClient.api
            AnalisisScreen(navController, apiService, context)
        }
        composable("egreso"){EgresoScreen(navController)}
        composable("escaner"){EscanerScreen(navController)}
        composable("huella"){HuellaScreen(navController)}
        composable("ingreso"){IngresoScreen(navController)}
        composable("inicio"){InicioScreen(navController)}
        composable("login"){LoginScreen(navController)}
        composable("predicciones"){PrediccionesScreen(navController)}
        composable("recibo"){ReciboScreen(navController)}
        composable("registro"){RegistroScreen(navController)}
    }
}

