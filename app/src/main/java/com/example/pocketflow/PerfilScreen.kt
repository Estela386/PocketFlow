package com.example.pocketflow

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pocketflow.data.local.UserPreferences
import com.example.pocketflow.ui.theme.AnimatedWaveBackground
import com.example.pocketflow.ui.theme.BottomNavigationBar

val SkyBlue = Color(0xFF90B7C9)
val Gold = Color(0xFFFFB703)
val Mint = Color(0xFFAFD9D3)
val SoftBlack = Color(0xFF1A1A1A)
val PureWhite = Color(0xFFFFFFFF)

@Composable
fun PerfilScreen(navController: NavHostController) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }

    val nombre = userPreferences.getNombre() ?: "Usuario desconocido"
    val email = "correo@desconocido.com" // Puedes agregar más campos en UserPreferences para el email si lo guardaste.

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = currentRoute)
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AnimatedWaveBackground()
            ContentColumn(navController = navController, nombre = nombre, email = email)
        }
    }
}

@Composable
fun ContentColumn(navController: NavHostController, nombre: String, email: String) {
    var showPasswordDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate("inicio") }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = SoftBlack)
            }
            IconButton(onClick = { navController.navigate("recompensas") }) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Recompensas",
                    tint = Gold,
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        Box(modifier = Modifier.clickable { navController.navigate("perfil") }) {
            ProfileImageWithInitial(nombre, navController)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Aquí mostrará el nombre obtenido de SharedPreferences
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = SkyBlue, fontWeight = FontWeight.Bold)) {
                    append(nombre)
                }
            },
            fontSize = 26.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        ProfileFieldButton(
            icon = Icons.Default.Person,
            text = email
        )

        ProfileFieldButton(
            icon = Icons.Default.Lock,
            text = "Contraseña",
            onClick = { showPasswordDialog = true }
        )

        Spacer(modifier = Modifier.height(80.dp))

        ProfileFieldButton(
            icon = Icons.Default.List,
            text = "Mis Categorías",
            onClick = { navController.navigate("categorias") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        val context = LocalContext.current
        OutlinedButton(
            onClick = {
                // Puedes limpiar la sesión aquí.

                UserPreferences(context).clearSession()
                navController.navigate("login") { popUpTo("perfil") { inclusive = true } }
            },
            border = BorderStroke(1.dp, SkyBlue),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SkyBlue),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión", tint = SkyBlue)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión", color = SoftBlack, fontSize = 18.sp)
        }
    }

    @Composable
    fun ProfileImageWithInitial(nombre: String) {
        val primerNombre = nombre.split(" ").firstOrNull() ?: nombre
        val inicial = primerNombre.firstOrNull()?.uppercase() ?: ""

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFF8AB4CC))
        ) {
            Text(
                text = inicial,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }


    if (showPasswordDialog) {
        PasswordChangeDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { oldPassword, newPassword, confirmPassword ->
                if (newPassword == confirmPassword) {
                    println("Contraseña actual: $oldPassword, Nueva: $newPassword")
                } else {
                    println("Las contraseñas no coinciden.")
                }
                showPasswordDialog = false
            }
        )
    }
}

@Composable
fun PasswordChangeDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { onConfirm(currentPassword, newPassword, confirmPassword); onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Mint)) {
                Text("Guardar", color = SoftBlack)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, border = BorderStroke(1.dp, SkyBlue)) {
                Text("Cancelar", color = SoftBlack)
            }
        },
        title = { Text("Cambiar Contraseña", color = SoftBlack) },
        text = {
            Column {
                OutlinedTextField(value = currentPassword, onValueChange = { currentPassword = it }, label = { Text("Contraseña actual") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = newPassword, onValueChange = { newPassword = it }, label = { Text("Nueva contraseña") }, singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirmar nueva contraseña") }, singleLine = true)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = PureWhite
    )
}

@Composable
fun ProfileFieldButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit = {}
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Mint),
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp), tint = SoftBlack)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, color = SoftBlack, fontSize = 18.sp)
    }
}
