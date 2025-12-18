package com.carbajo.checking.data

import com.google.firebase.database.FirebaseDatabase
import com.carbajo.checking.modelos.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class BalanceRepository(
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    // Suspender para facilitar la orquestación (usa coroutines en Activity/Adapter)
    suspend fun cargarBalanceDelMes(fechaId: String): BalanceModelo = suspendCoroutine { cont ->
        val refIngresos = db.getReference("ingresos").child(fechaId)
        val refGastos   = db.getReference("Gasto Mensual").child(fechaId)
        val refEgresos  = db.getReference("egresos").child(fechaId)

        var ingresos: IngresosModelo? = null
        var gastos: GastoMensualModelo? = null
        var egresos: EgresosModelo? = null

        fun intentarResolver() {
            if (ingresos != null && gastos != null && egresos != null) {
                val item = BalanceModelo(
                    fechaId = fechaId,
                    totalIngresos = ingresos!!.totalIngresos(),
                    totalGastosMensuales = gastos!!.subtotal,
                    totalEgresos = egresos!!.total
                )
                cont.resume(item)
            }
        }

        refIngresos.get().addOnSuccessListener {
            ingresos = it.getValue(IngresosModelo::class.java) ?: IngresosModelo(fechaId)
            intentarResolver()
        }.addOnFailureListener { cont.resume(BalanceModelo(fechaId,0.0,0.0,0.0)) }

        refGastos.get().addOnSuccessListener {
            gastos = it.getValue(GastoMensualModelo::class.java) ?: GastoMensualModelo(fechaId)
            intentarResolver()
        }.addOnFailureListener { cont.resume(BalanceModelo(fechaId,0.0,0.0,0.0)) }

        refEgresos.get().addOnSuccessListener {
            egresos = it.getValue(EgresosModelo::class.java) ?: EgresosModelo(fechaId)
            intentarResolver()
        }.addOnFailureListener { cont.resume(BalanceModelo(fechaId,0.0,0.0,0.0)) }
    }
}