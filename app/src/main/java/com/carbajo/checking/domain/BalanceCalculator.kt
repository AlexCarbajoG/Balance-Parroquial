package com.carbajo.checking.domain

object BalanceCalculator {

    fun calcularUtilidad(
        totalIngresos: Double,
        totalGastosMensuales: Double,
        totalEgresos: Double
    ): Double {
        return totalIngresos - totalGastosMensuales - totalEgresos
    }
}