package com.example.pocketflow

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ScanViewModel : ViewModel() {
    private val _lineasTexto = MutableStateFlow<List<String>>(emptyList())
    val lineasTexto: StateFlow<List<String>> = _lineasTexto

    fun actualizarLineas(nuevasLineas: List<String>) {
        _lineasTexto.value = nuevasLineas
    }
}
