package com.example.pocketflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

    NavHost(
        navController = navController,
        startDestination = "perfil" // La primera pantalla que se muestra
    ) {
        composable("perfil") { PerfilScreen(navController) }
        composable("categorias") { CategoriasScreen() }
        composable("recompensas"){ RecompensasScreen()}
        composable("analisis"){}
        composable("egreso"){}
        composable("escaner"){}
        composable("huella"){}
        composable("ingreso"){}
        composable("inicio"){}
        composable("login"){}
        composable("predicciones"){}
        composable("recibo"){}
        composable("registro"){}
    }
}