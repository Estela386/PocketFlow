package com.example.pocketflow

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

data class Producto(
    val nombre: String,
    val precio: String
)

fun procesarImagenConVision(
    bitmap: Bitmap,
    onResultado: (List<Producto>, String?) -> Unit,
    onError: (Exception) -> Unit
) {
    val image = InputImage.fromBitmap(bitmap, 0)
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    recognizer.process(image)
        .addOnSuccessListener { visionText ->
            val productos = mutableListOf<Producto>()
            var total: String? = null

            for (block in visionText.textBlocks) {
                for (line in block.lines) {
                    val texto = line.text.trim()

                    when {
                        // Detectamos "TOTAL"
                        texto.contains("TOTAL", ignoreCase = true) -> {
                            total = texto
                        }
                        // Detectamos líneas tipo "NombreProducto    $precio"
                        texto.contains(Regex("\\$\\s*\\d+(\\.\\d{2})?")) -> {
                            val partes = texto.split(Regex("\\$"))
                            if (partes.size >= 2) {
                                val nombreProducto = partes[0].trim()
                                val precioProducto = "$" + partes[1].trim()
                                productos.add(Producto(nombreProducto, precioProducto))
                            }
                        }
                    }
                }
            }

            onResultado(productos, total)
        }
        .addOnFailureListener { e ->
            onError(e)
        }
}
