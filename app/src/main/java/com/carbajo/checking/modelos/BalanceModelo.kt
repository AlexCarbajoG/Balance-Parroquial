package com.carbajo.checking.modelos

import com.carbajo.checking.domain.BalanceCalculator

data class BalanceModelo(
    val fechaId: String,              // "yyyy-MM"
    val totalIngresos: Double,        // ingresos.totalIngresos()
    val totalGastosMensuales: Double, // gasto.subtotal
    val totalEgresos: Double          // egresos.total
) {
    val utilidad: Double
        get() = BalanceCalculator.calcularUtilidad(
            totalIngresos = totalIngresos,
            totalGastosMensuales = totalGastosMensuales,
            totalEgresos = totalEgresos
        )
}
