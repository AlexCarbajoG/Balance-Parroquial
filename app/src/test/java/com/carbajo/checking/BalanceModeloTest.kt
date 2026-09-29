package com.carbajo.checking

import com.carbajo.checking.modelos.BalanceModelo
import org.junit.Assert.assertEquals
import org.junit.Test

class BalanceModeloTest {

    @Test
    fun utilidad_esPositiva_cuandoIngresosSuperaGastos(){
        val balance = BalanceModelo(
            fechaId = "2026_09",
            totalIngresos = 1000.0,
            totalGastosMensuales = 300.0,
            totalEgresos = 200.0
        )

        assertEquals(500.0, balance.utilidad, 0.001)
    }

    @Test
    fun utilidad_esCero_cuandoIngresosIgualanGastosTotales() {
        val balance = BalanceModelo(
            fechaId = "2026-09",
            totalIngresos = 500.0,
            totalGastosMensuales = 300.0,
            totalEgresos = 200.0
        )
        assertEquals(0.0, balance.utilidad, 0.001)
    }

    @Test
    fun utilidad_esNegativa_cuandoGastosSuperanIngresos(){
        val balance = BalanceModelo(
            fechaId = "2026-09",
            totalIngresos = 400.0,
            totalGastosMensuales = 300.0,
            totalEgresos = 200.0
        )
        //la tolerancia es para los numeros Double, por que los decilames pueden tener pequeñas diferencias.
        //assertEquals(lo esperado, lo real, la tolerancia )
        assertEquals(-100.0, balance.utilidad, 0.001)

    }


}