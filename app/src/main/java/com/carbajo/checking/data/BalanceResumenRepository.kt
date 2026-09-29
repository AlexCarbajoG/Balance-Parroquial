package com.carbajo.checking.data

import com.carbajo.checking.modelos.BalanceResumenModelo
import com.carbajo.checking.modelos.EgresosModelo
import com.carbajo.checking.modelos.GastoMensualModelo
import com.carbajo.checking.modelos.IngresosModelo
import com.google.firebase.database.FirebaseDatabase
import com.carbajo.checking.domain.BalanceCalculator
import java.text.SimpleDateFormat
import java.util.Locale

class BalanceResumenRepository {

    private val db = FirebaseDatabase.getInstance().reference

    fun actualizarBalanceMes(fechaCompleta: String) {

        // Convierte por ejemplo:
        // 31-08-2026 -> 2026-08

        val formatoEntrada =
            SimpleDateFormat(
                "dd-MM-yyyy",
                Locale("es", "PE")
            )

        formatoEntrada.isLenient = false

        val formatoSalida =
            SimpleDateFormat(
                "yyyy-MM",
                Locale("es", "PE")
            )

        val fecha = try {
            formatoEntrada.parse(fechaCompleta)
        } catch (e: Exception) {
            null
        } ?: return

        val fechaMes =
            formatoSalida.format(fecha)


        // Buscar INGRESOS
        db.child("ingresos")
            .child(fechaCompleta)
            .get()
            .addOnSuccessListener { snapIngresos ->


                // Buscar GASTO MENSUAL
                db.child("Gasto Mensual")
                    .child(fechaCompleta)
                    .get()
                    .addOnSuccessListener { snapGastos ->


                        // Buscar EGRESOS
                        db.child("egresos")
                            .child(fechaCompleta)
                            .get()
                            .addOnSuccessListener { snapEgresos ->


                                // Solo continuar si existen LOS 3
                                if (
                                    snapIngresos.exists() &&
                                    snapGastos.exists() &&
                                    snapEgresos.exists()
                                ) {

                                    val ingresos =
                                        snapIngresos.getValue(
                                            IngresosModelo::class.java
                                        )

                                    val gastos =
                                        snapGastos.getValue(
                                            GastoMensualModelo::class.java
                                        )

                                    val egresos =
                                        snapEgresos.getValue(
                                            EgresosModelo::class.java
                                        )


                                    if (
                                        ingresos != null &&
                                        gastos != null &&
                                        egresos != null
                                    ) {

                                        val totalIngresos =
                                            ingresos.totalIngresos()

                                        val totalGastosMensuales =
                                            gastos.subtotal

                                        val totalEgresos =
                                            egresos.total

                                        val utilidad = BalanceCalculator.calcularUtilidad(
                                            totalIngresos = totalIngresos,
                                            totalGastosMensuales = totalGastosMensuales,
                                            totalEgresos = totalEgresos
                                        )


                                        val balanceResumen =
                                            BalanceResumenModelo(
                                                fechaId = fechaMes,
                                                totalIngresos = totalIngresos,
                                                totalGastosMensuales = totalGastosMensuales,
                                                totalEgresos = totalEgresos,
                                                utilidad = utilidad
                                            )


                                        db.child("balances")
                                            .child(fechaMes)
                                            .setValue(balanceResumen)
                                    }
                                }
                            }
                    }
            }
    }
}