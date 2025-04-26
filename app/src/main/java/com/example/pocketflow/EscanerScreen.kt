package com.example.pocketflow

import android.Manifest
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary


@Composable
fun EscanerScreen(navController: NavHostController, scanViewModel: ScanViewModel) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val previewView = remember { PreviewView(context) }
    var cameraDisponible by remember { mutableStateOf(true) }

    // Launcher para elegir imagen desde galería
    val galeriaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let {
                procesarImagen(it, context, scanViewModel) {
                    navController.navigate("recibo")
                }
            }
        }
    )

    // Permisos de cámara
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = androidx.camera.core.Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            context as androidx.lifecycle.LifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                        cameraDisponible = true
                    } catch (e: Exception) {
                        Log.e("Camera", "No se pudo acceder a la cámara", e)
                        cameraDisponible = false
                    }
                }, ContextCompat.getMainExecutor(context))
            } else {
                cameraDisponible = false
            }
        }
    )

    // Pedir permisos
    LaunchedEffect(Unit) {
        permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (cameraDisponible) {
                Column(modifier = Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = { previewView },
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Botón Cancelar
                        IconButton(onClick = {
                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancelar"
                            )
                        }

                        // Botón Tomar Foto (ícono)
                        IconButton(onClick = {
                            val photoFile = File(
                                context.cacheDir,
                                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) + ".jpg"
                            )
                            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                            imageCapture.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                        val uri = Uri.fromFile(photoFile)
                                        procesarImagen(uri, context, scanViewModel) {
                                            navController.navigate("recibo")
                                        }
                                    }

                                    override fun onError(exception: ImageCaptureException) {
                                        Log.e("Foto", "Error al tomar foto", exception)
                                    }
                                }
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Tomar Foto"
                            )
                        }

                        // Botón Galería
                        IconButton(onClick = {
                            galeriaLauncher.launch("image/*")
                        }) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = "Seleccionar desde Galería"
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "No hay cámara disponible. Puedes seleccionar una imagen de la galería:",
                    modifier = Modifier.padding(16.dp)
                )

                Button(
                    onClick = { galeriaLauncher.launch("image/*") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Seleccionar imagen")
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

// Procesamiento de imagen con ML Kit OCR
fun procesarImagen(uri: Uri, context: android.content.Context, scanViewModel: ScanViewModel, onFinish: () -> Unit) {
    try {
        val bitmap = if (Build.VERSION.SDK_INT < 28) {
            android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } else {
            val source = android.graphics.ImageDecoder.createSource(context.contentResolver, uri)
            android.graphics.ImageDecoder.decodeBitmap(source)
        }

        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val lineasFiltradas = visionText.textBlocks
                    .flatMap { it.lines }
                    .map { it.text }
                    .filter { linea ->
                        linea.contains(Regex(".*\\$\\d+.*", RegexOption.IGNORE_CASE))
                    }

                scanViewModel.actualizarLineas(lineasFiltradas)
                onFinish()
            }
            .addOnFailureListener { e ->
                Log.e("OCR", "Error al procesar imagen", e)
            }
    } catch (e: Exception) {
        Log.e("OCR", "Error al cargar imagen", e)
    }
}
