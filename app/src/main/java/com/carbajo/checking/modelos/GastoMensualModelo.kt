package com.carbajo.checking.modelos

data class GastoMensualModelo(
    var fechaId: String = "",
    var verduras: Double = 0.0,
    var taxiCamal: Double = 0.0,
    var carne: Double = 0.0,
    var afiladores: Double = 0.0,
    var panes: Double = 0.0,
    var menudencias: Double = 0.0,
    var pescado: Double = 0.0,
    var carneMolida: Double = 0.0,
    var pollos: Double = 0.0,
    var abarrotes: Double = 0.0,
    var recojoViveres: Double = 0.0,
    var huevos: Double = 0.0,
    var pasajes: Double = 0.0,
    var articulosLimpieza: Double = 0.0,
    var mantenimiento: Double = 0.0,
    var gastosExtras: Double = 0.0
) {
    val subtotal: Double
        get() = listOf(
            verduras, taxiCamal, carne, afiladores, panes,
            menudencias, pescado, carneMolida, pollos,
            abarrotes, recojoViveres, huevos, pasajes, articulosLimpieza,
            mantenimiento, gastosExtras
        ).sum()
}