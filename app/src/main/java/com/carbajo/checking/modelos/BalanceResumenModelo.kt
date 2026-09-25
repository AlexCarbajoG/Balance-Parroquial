package com.carbajo.checking.modelos

data class BalanceResumenModelo(
    var fechaId: String = "",
    var totalIngresos: Double = 0.0,
    var totalGastosMensuales: Double = 0.0,
    var totalEgresos: Double = 0.0,
    var utilidad: Double = 0.0
)
