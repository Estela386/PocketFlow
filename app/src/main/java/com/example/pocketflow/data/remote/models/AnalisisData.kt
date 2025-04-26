package com.example.pocketflow.data.remote.models

data class AnalisisData(
    val totalIngresos: Double,
    val totalEgresos: Double,
    val resumenMensual: List<ResumenMensual>
)

data class ResumenMensual(
    val mes: String,
    val ingreso: Double,
    val egreso: Double
)




