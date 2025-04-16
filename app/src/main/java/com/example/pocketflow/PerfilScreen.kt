package com.example.pocketflow

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.pocketflow.ui.theme.AnimatedWaveBackground

@Composable
fun PerfilScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedWaveBackground() // Fondo con animación
        // Pasa el navController a ContentColumn para poder usarlo en la navegación.
        ContentColumn(navController = navController)
    }
}

@Composable
fun ContentColumn(navController: NavHostController) {
    // Estado para mostrar el modal de cambio de contraseña
    var showPasswordDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Fila superior
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Acción de volver */ }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.Black)
            }
            IconButton(onClick = { navController.navigate("recompensas") }) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Idea",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(50.dp))

        // Foto de perfil
        Image(
            painter = painterResource(id = R.drawable.perfil),
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(250.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Nombre
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Color(0xFF1A237E), fontWeight = FontWeight.Bold)) {
                    append("Ana ")
                }
                withStyle(SpanStyle(color = Color(0xFF1A237E), fontWeight = FontWeight.Bold)) {
                    append("Marmolejo")
                }
            },
            fontSize = 26.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Botones de perfil (Sin acción de navegación)
        ProfileFieldButton(
            icon = Icons.Default.Person,
            text = "anamarmolejo@hotmail.com"
        )
        ProfileFieldButton(
            icon = Icons.Default.Lock,
            text = "Contraseña",
            onClick = { showPasswordDialog = true }
        )

        Spacer(modifier = Modifier.height(80.dp))

        // Botón de "Mis Categorías" con acción de navegación
        ProfileFieldButton(
            icon = Icons.Default.List,
            text = "Mis Categorías",
            onClick = { navController.navigate("categorias") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Botón Cerrar Sesión
        OutlinedButton(
            onClick = { /* Cerrar sesión */ },
            border = BorderStroke(1.dp, Color(0xFF8AB4CC)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF8AB4CC)),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            Icon(
                Icons.Default.ExitToApp,
                contentDescription = "Cerrar sesión",
                tint = Color(0xFF8AB4CC)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión", color = Color.Black, fontSize = 18.sp)
        }
    }

    // Modal de cambio de contraseña
    if (showPasswordDialog) {
        PasswordChangeDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { oldPassword, newPassword, confirmPassword ->
                // Aquí puedes validar o guardar la contraseña
                if (newPassword == confirmPassword) {
                    println("Contraseña actual: $oldPassword, Nueva: $newPassword")
                    // Implementar lógica para guardar la nueva contraseña
                } else {
                    println("Las contraseñas no coinciden.")
                    // Mostrar un mensaje de error si las contraseñas no coinciden
                }
                showPasswordDialog = false // Cerrar el modal
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
            Button(
                onClick = {
                    onConfirm(currentPassword, newPassword, confirmPassword)
                    onDismiss()
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        title = { Text("Cambiar Contraseña") },
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
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}

@Composable
fun ProfileFieldButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit = {}  // Valor por defecto vacío para mantener la compatibilidad
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8AB4CC)),
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, color = Color.Black, fontSize = 18.sp)
    }
}
