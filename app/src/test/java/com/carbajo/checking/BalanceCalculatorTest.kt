package com.carbajo.checking

import com.carbajo.checking.domain.BalanceCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class BalanceCalculatorTest {

    @Test
    fun utilidad_esPositiva_cuandoIngresosSuperanGastos() {

        val resultado = BalanceCalculator.calcularUtilidad(
            totalIngresos = 1000.0,
            totalGastosMensuales = 300.0,
            totalEgresos = 200.0
        )

        assertEquals(500.0, resultado, 0.001)
    }

    @Test
    fun utilidad_esCero_cuandoIngresosIgualanGastosTotales() {

        val resultado = BalanceCalculator.calcularUtilidad(
            totalIngresos = 500.0,
            totalGastosMensuales = 300.0,
            totalEgresos = 200.0
        )

        assertEquals(0.0, resultado, 0.001)
    }

    @Test
    fun utilidad_esNegativa_cuandoGastosSuperanIngresos() {

        val resultado = BalanceCalculator.calcularUtilidad(
            totalIngresos = 400.0,
            totalGastosMensuales = 300.0,
            totalEgresos = 200.0
        )

        assertEquals(-100.0, resultado, 0.001)
    }
}