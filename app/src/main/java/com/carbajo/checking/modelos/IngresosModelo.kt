package com.carbajo.checking.modelos

data class IngresosModelo(
    var fechaId: String = "",
    var saldoMesAnterior: Double = 0.0,
    var balanceMesActual: Double = 0.0
) {
    fun totalIngresos() = saldoMesAnterior + balanceMesActual
}