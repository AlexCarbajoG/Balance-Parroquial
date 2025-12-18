package com.carbajo.checking.modelos

data class BalanceModelo(
    val fechaId: String,              // "yyyy-MM"
    val totalIngresos: Double,        // ingresos.totalIngresos()
    val totalGastosMensuales: Double, // gasto.subtotal
    val totalEgresos: Double          // egresos.total
) {
    val utilidad: Double
        get() = totalIngresos - totalGastosMensuales - totalEgresos
}
