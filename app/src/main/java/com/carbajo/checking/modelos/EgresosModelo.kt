package com.carbajo.checking.modelos

data class EgresosModelo(
    var fechaId: String = "",
    var sueldos: Double = 0.0,
    var impuestos: Double = 0.0,
    var gas: Double = 0.0,
    var agua: Double = 0.0,
    var luz: Double = 0.0
) {
    val total: Double
        get() = sueldos + impuestos + gas + agua + luz
}