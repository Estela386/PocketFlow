package com.example.pocketflow

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.pocketflow.data.local.PerfilViewModel
import com.example.pocketflow.data.local.UserPreferences
import com.example.pocketflow.ui.theme.AmarilloMostaza
import com.example.pocketflow.ui.theme.AnimatedWaveBackground
import com.example.pocketflow.ui.theme.Blanco
import com.example.pocketflow.ui.theme.BottomNavigationBar

val SkyBlue = Color(0xFF90B7C9)
val Gold = Color(0xFFFFB703)
val Mint = Color(0xFFAFD9D3)
val SoftBlack = Color(0xFF1A1A1A)
val PureWhite = Color(0xFFFFFFFF)

@Composable
fun PerfilScreen(navController: NavHostController, viewModel: PerfilViewModel = viewModel()) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }

    val nombre = userPreferences.getNombre() ?: "Usuario desconocido"
    val correo by viewModel.correo.collectAsState()
    var showEmailDialog by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(Unit) {
        viewModel.obtenerCorreo()
    }

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
            ContentColumn(
                navController = navController,
                nombre = nombre,
                correo = correo,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun ContentColumn(
    navController: NavHostController,
    nombre: String,
    correo: String,
    viewModel: PerfilViewModel
) {
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showEmailDialog by remember { mutableStateOf(false) }
    var nuevoCorreo by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botones superiores
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

        ProfileImageWithInitial(nombre)

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Blanco, fontWeight = FontWeight.Bold)) {
                    append(nombre)
                }
            },
            fontSize = 26.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Botón para editar correo
        ProfileFieldButton(
            icon = Icons.Default.Email,
            text = correo,
            onClick = {
                nuevoCorreo = correo // precargar correo actual
                showEmailDialog = true
            }
        )

        // Botón para editar contraseña
        ProfileFieldButton(
            icon = Icons.Default.Lock,
            text = "Contraseña",
            onClick = { showPasswordDialog = true }
        )

        Spacer(modifier = Modifier.height(80.dp))

        // Botón categorías
        ProfileFieldButton(
            icon = Icons.Default.List,
            text = "Mis Categorías",
            onClick = { navController.navigate("categorias") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Botón cerrar sesión
        OutlinedButton(
            onClick = {
                UserPreferences(context).clearSession()
                navController.navigate("login") {
                    popUpTo("perfil") { inclusive = true }
                }
            },
            border = BorderStroke(1.dp, SkyBlue),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Blanco),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión", tint = SkyBlue)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión", color = AmarilloMostaza, fontSize = 18.sp)
        }
    }

    // Diálogo para actualizar contraseña
    if (showPasswordDialog) {
        PasswordChangeDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { oldPassword, newPassword, confirmPassword ->
                if (newPassword == confirmPassword) {
                    viewModel.actualizarContrasena(oldPassword, newPassword)
                } else {
                    Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                }
                showPasswordDialog = false
            }
        )
    }

    // Diálogo para actualizar correo
    if (showEmailDialog) {
        AlertDialog(
            onDismissRequest = { showEmailDialog = false },
            title = { Text("Actualizar correo") },
            text = {
                Column {
                    Text("Ingresa tu nuevo correo:")
                    TextField(
                        value = nuevoCorreo,
                        onValueChange = { nuevoCorreo = it },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.actualizarCorreo(nuevoCorreo) { success ->
                        if (success) {
                            Toast.makeText(context, "Correo actualizado", Toast.LENGTH_SHORT).show()
                            showEmailDialog = false
                        } else {
                            Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text("Actualizar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEmailDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}


@Composable
fun ProfileImageWithInitial(nombre: String) {
    val primerNombre = nombre.split(" ").firstOrNull() ?: nombre
    val inicial = primerNombre.firstOrNull()?.uppercase() ?: ""

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(170.dp)
            .clip(CircleShape)
            .background(Color(0xFF8AB4CC))
    ) {
        Text(
            text = inicial,
            fontSize = 120.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
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
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (newPassword == confirmPassword) {
                        errorMessage = null
                        onConfirm(currentPassword, newPassword, confirmPassword)
                    } else {
                        errorMessage = "Las contraseñas no coinciden"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Mint)
            ) {
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
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Contraseña actual") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva contraseña") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar nueva contraseña") },
                    singleLine = true
                )
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
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
            .padding(vertical = 6.dp)
    ) {
        Icon(icon, contentDescription = text, tint = SoftBlack)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, color = SoftBlack, fontSize = 18.sp)
    }
}

