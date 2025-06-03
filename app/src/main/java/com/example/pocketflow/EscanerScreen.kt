package com.example.pocketflow

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.example.pocketflow.ui.theme.AnimatedWaveBackground
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File

// Modelo de datos para una compra individual
data class Compra(val descripcion: String, val precio: String)

// Modelo de datos para el ticket escaneado
data class TicketInfo(
    val total: String,
    val fecha: String,
    val compras: List<Compra>
)

object TicketProcessor {
    fun extraerInformacion(ticketTexto: String): TicketInfo {
        val totalRegex = Regex("""(?i)total\s*[:\$]?\s*(\d+(?:\.\d{2})?)""")
        val fechaRegex = Regex("""\b\d{2}/\d{2}/\d{4}\b""")
        val lineaCompraRegex = Regex("""(.+?)\s+\$?(\d+(?:\.\d{2})?)""")

        val total = totalRegex.find(ticketTexto)?.groupValues?.get(1) ?: "No detectado"
        val fecha = fechaRegex.find(ticketTexto)?.value ?: "No detectada"

        // --- Nuevo preprocesamiento de líneas para unir descripción y precio separados ---
        val lineas = ticketTexto.lines().map { it.trim() }.filter { it.isNotEmpty() }
        val lineasUnidas = mutableListOf<String>()

        var buffer = ""
        for (line in lineas) {
            if (line.matches(Regex("""^\$?\d+(?:\.\d{2})?$"""))) {
                // La línea actual parece ser solo un precio
                buffer += " $line"
                lineasUnidas.add(buffer.trim())
                buffer = ""
            } else {
                // Línea nueva, posiblemente descripción
                if (buffer.isNotEmpty()) {
                    lineasUnidas.add(buffer.trim())
                }
                buffer = line
            }
        }
        if (buffer.isNotEmpty()) {
            lineasUnidas.add(buffer.trim())
        }

        // Extraer compras usando el regex
        val compras = lineasUnidas.mapNotNull { linea ->
            val match = lineaCompraRegex.find(linea)
            if (match != null) {
                val desc = match.groupValues[1].trim()
                val precio = match.groupValues[2]
                Compra(desc, precio)
            } else null
        }

        return TicketInfo(total = total, fecha = fecha, compras = compras)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EscanerScreen(navController: NavHostController) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val previewView = remember { PreviewView(context) }
    var resultadoTexto by remember { mutableStateOf("") }
    var infoExtraida by remember { mutableStateOf<TicketInfo?>(null) }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                cameraProviderFuture.addListener({
                    cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    cameraProvider?.unbindAll()
                    cameraProvider?.bindToLifecycle(
                        context as LifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                }, ContextCompat.getMainExecutor(context))
            }
        }
    )

    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                escanearTextoDesdeImagen(context, it) { texto ->
                    resultadoTexto = texto
                    infoExtraida = TicketProcessor.extraerInformacion(texto)
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Escáner de Tickets") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                AnimatedWaveBackground()

                AndroidView(
                    factory = { previewView },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(bottom = 16.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    ElevatedButton(onClick = {
                        val photoFile = File.createTempFile("ticket", ".jpg", context.cacheDir)
                        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                        imageCapture.takePicture(
                            outputOptions,
                            ContextCompat.getMainExecutor(context),
                            object : ImageCapture.OnImageSavedCallback {
                                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                    val uri = Uri.fromFile(photoFile)
                                    escanearTextoDesdeImagen(context, uri) { texto ->
                                        resultadoTexto = texto
                                        infoExtraida = TicketProcessor.extraerInformacion(texto)
                                    }
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    Log.e("Camera", "Error al capturar imagen", exception)
                                }
                            }
                        )
                    }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Tomar Foto")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Desde Cámara")
                    }

                    OutlinedButton(onClick = {
                        galeriaLauncher.launch("image/*")
                    }) {
                        Icon(Icons.Default.Folder, contentDescription = "Seleccionar Imagen")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Desde Galería")
                    }
                }

                infoExtraida?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    if (it.compras.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("→ Compras:", fontWeight = FontWeight.Bold)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEFEF)),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Descripción", fontWeight = FontWeight.Bold)
                                    Text("Precio", fontWeight = FontWeight.Bold)
                                }
                                Divider(modifier = Modifier.padding(vertical = 4.dp))

                                it.compras.forEach { compra ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(compra.descripcion, modifier = Modifier.weight(1f))
                                        Text("$${compra.precio}", modifier = Modifier.padding(start = 8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

fun escanearTextoDesdeImagen(context: Context, uri: Uri, onResult: (String) -> Unit) {
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val inputImage = InputImage.fromFilePath(context, uri)

    recognizer.process(inputImage)
        .addOnSuccessListener { visionText ->
            Log.d("OCR", "Texto detectado:\n${visionText.text}")
            onResult(visionText.text)
        }
        .addOnFailureListener { e ->
            Log.e("OCR", "Error al procesar la imagen", e)
            onResult("Error al procesar la imagen.")
        }
}

