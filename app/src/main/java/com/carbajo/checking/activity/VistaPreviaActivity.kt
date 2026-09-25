package com.carbajo.checking.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.carbajo.checking.R
import com.carbajo.checking.data.BalanceResumenRepository
import com.carbajo.checking.modelos.EgresosModelo
import com.carbajo.checking.modelos.GastoMensualModelo
import com.carbajo.checking.modelos.IngresosModelo
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class VistaPreviaActivity : AppCompatActivity() {

    private var fechaId: String = ""

    private val db =
        FirebaseDatabase.getInstance().reference


    // FECHA
    private lateinit var txtFechaVistaPrevia: TextView


    // INGRESOS
    private lateinit var txtSaldoAnterior: TextInputEditText
    private lateinit var txtBalanceActual: TextInputEditText


    // EGRESOS
    private lateinit var txtSueldos: TextInputEditText
    private lateinit var txtImpuestos: TextInputEditText
    private lateinit var txtGas: TextInputEditText
    private lateinit var txtAgua: TextInputEditText
    private lateinit var txtLuz: TextInputEditText


    // GASTOS MENSUALES
    private lateinit var txtVerduras: TextInputEditText
    private lateinit var txtTaxiCamal: TextInputEditText
    private lateinit var txtCarne: TextInputEditText
    private lateinit var txtAfiladores: TextInputEditText
    private lateinit var txtPanes: TextInputEditText
    private lateinit var txtMenudencias: TextInputEditText
    private lateinit var txtPescado: TextInputEditText
    private lateinit var txtCarneMolida: TextInputEditText
    private lateinit var txtPollos: TextInputEditText
    private lateinit var txtAbarrotes: TextInputEditText
    private lateinit var txtRecojoViveres: TextInputEditText
    private lateinit var txtHuevos: TextInputEditText
    private lateinit var txtPasajes: TextInputEditText
    private lateinit var txtLimpieza: TextInputEditText
    private lateinit var txtMantenimiento: TextInputEditText
    private lateinit var txtGastosExtras: TextInputEditText


    // BOTONES
    private lateinit var btnEditar: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnRegresar: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(
            R.layout.activity_vista_previa
        )




        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v, insets ->

            val systemBars =
                insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                )

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }


        // RECIBIR FECHA
        fechaId =
            intent.getStringExtra("fechaId")
                ?: ""


        configurarNombres()

        asignarControles()

        configurarBotones()


        // MOSTRAR FECHA SELECCIONADA
        txtFechaVistaPrevia.text =
            fechaId


        // CARGAR FIREBASE
        cargarDatos()
    }


    // =====================================================
    // ASIGNAR CONTROLES
    // =====================================================

    private fun asignarControles() {

        txtFechaVistaPrevia =
            findViewById(
                R.id.txtFechaVistaPrevia
            )


        // INGRESOS

        txtSaldoAnterior =
            obtenerCampo(
                R.id.itemVistaSaldoAnterior
            )

        txtBalanceActual =
            obtenerCampo(
                R.id.itemVistaBalanceActual
            )


        // EGRESOS

        txtSueldos =
            obtenerCampo(
                R.id.itemVistaSueldos
            )

        txtImpuestos =
            obtenerCampo(
                R.id.itemVistaImpuestos
            )

        txtGas =
            obtenerCampo(
                R.id.itemVistaGas
            )

        txtAgua =
            obtenerCampo(
                R.id.itemVistaAgua
            )

        txtLuz =
            obtenerCampo(
                R.id.itemVistaLuz
            )


        // GASTOS

        txtVerduras =
            obtenerCampo(
                R.id.itemVistaVerduras
            )

        txtTaxiCamal =
            obtenerCampo(
                R.id.itemVistaTaxiCamal
            )

        txtCarne =
            obtenerCampo(
                R.id.itemVistaCarne
            )

        txtAfiladores =
            obtenerCampo(
                R.id.itemVistaAfiladores
            )

        txtPanes =
            obtenerCampo(
                R.id.itemVistaPanes
            )

        txtMenudencias =
            obtenerCampo(
                R.id.itemVistaMenudencias
            )

        txtPescado =
            obtenerCampo(
                R.id.itemVistaPescado
            )

        txtCarneMolida =
            obtenerCampo(
                R.id.itemVistaCarneMolida
            )

        txtPollos =
            obtenerCampo(
                R.id.itemVistaPollos
            )

        txtAbarrotes =
            obtenerCampo(
                R.id.itemVistaAbarrotes
            )

        txtRecojoViveres =
            obtenerCampo(
                R.id.itemVistaRecojoViveres
            )

        txtHuevos =
            obtenerCampo(
                R.id.itemVistaHuevos
            )

        txtPasajes =
            obtenerCampo(
                R.id.itemVistaPasajes
            )

        txtLimpieza =
            obtenerCampo(
                R.id.itemVistaLimpieza
            )

        txtMantenimiento =
            obtenerCampo(
                R.id.itemVistaMantenimiento
            )

        txtGastosExtras =
            obtenerCampo(
                R.id.itemVistaGastosExtras
            )


        btnEditar =
            findViewById(
                R.id.btnEditarVistaPrevia
            )

        btnGuardar =
            findViewById(
                R.id.btnGuardarVistaPrevia
            )

        btnRegresar =
            findViewById(
                R.id.btnRegresarVistaPrevia
            )
    }


    private fun obtenerCampo(
        idItem: Int
    ): TextInputEditText {

        val item =
            findViewById<View>(
                idItem
            )

        return item.findViewById(
            R.id.txtMontoConcepto
        )
    }


    // =====================================================
    // CARGAR FIREBASE
    // =====================================================

    private fun cargarDatos() {

        if (fechaId.isBlank()) {

            Toast.makeText(
                this,
                "No se recibió la fecha del balance",
                Toast.LENGTH_LONG
            ).show()

            return
        }


        cargarIngresos()

        cargarEgresos()

        cargarGastosMensuales()
    }


    private fun cargarIngresos() {

        db.child("ingresos")
            .child(fechaId)
            .get()
            .addOnSuccessListener { snapshot ->

                val ingresos =
                    snapshot.getValue(
                        IngresosModelo::class.java
                    )

                if (ingresos != null) {

                    txtSaldoAnterior.setText(
                        formatoMonto(
                            ingresos.saldoMesAnterior
                        )
                    )

                    txtBalanceActual.setText(
                        formatoMonto(
                            ingresos.balanceMesActual
                        )
                    )
                }
            }
    }


    private fun cargarEgresos() {

        db.child("egresos")
            .child(fechaId)
            .get()
            .addOnSuccessListener { snapshot ->

                val egresos =
                    snapshot.getValue(
                        EgresosModelo::class.java
                    )

                if (egresos != null) {

                    txtSueldos.setText(
                        formatoMonto(
                            egresos.sueldos
                        )
                    )

                    txtImpuestos.setText(
                        formatoMonto(
                            egresos.impuestos
                        )
                    )

                    txtGas.setText(
                        formatoMonto(
                            egresos.gas
                        )
                    )

                    txtAgua.setText(
                        formatoMonto(
                            egresos.agua
                        )
                    )

                    txtLuz.setText(
                        formatoMonto(
                            egresos.luz
                        )
                    )
                }
            }
    }


    private fun cargarGastosMensuales() {

        db.child("Gasto Mensual")
            .child(fechaId)
            .get()
            .addOnSuccessListener { snapshot ->

                val gastos =
                    snapshot.getValue(
                        GastoMensualModelo::class.java
                    )

                if (gastos != null) {

                    txtVerduras.setText(
                        formatoMonto(gastos.verduras)
                    )

                    txtTaxiCamal.setText(
                        formatoMonto(gastos.taxiCamal)
                    )

                    txtCarne.setText(
                        formatoMonto(gastos.carne)
                    )

                    txtAfiladores.setText(
                        formatoMonto(gastos.afiladores)
                    )

                    txtPanes.setText(
                        formatoMonto(gastos.panes)
                    )

                    txtMenudencias.setText(
                        formatoMonto(gastos.menudencias)
                    )

                    txtPescado.setText(
                        formatoMonto(gastos.pescado)
                    )

                    txtCarneMolida.setText(
                        formatoMonto(gastos.carneMolida)
                    )

                    txtPollos.setText(
                        formatoMonto(gastos.pollos)
                    )

                    txtAbarrotes.setText(
                        formatoMonto(gastos.abarrotes)
                    )

                    txtRecojoViveres.setText(
                        formatoMonto(gastos.recojoViveres)
                    )

                    txtHuevos.setText(
                        formatoMonto(gastos.huevos)
                    )

                    txtPasajes.setText(
                        formatoMonto(gastos.pasajes)
                    )

                    txtLimpieza.setText(
                        formatoMonto(
                            gastos.articulosLimpieza
                        )
                    )

                    txtMantenimiento.setText(
                        formatoMonto(
                            gastos.mantenimiento
                        )
                    )

                    txtGastosExtras.setText(
                        formatoMonto(
                            gastos.gastosExtras
                        )
                    )
                }
            }
    }


    // =====================================================
    // BOTONES
    // =====================================================

    private fun configurarBotones() {

        btnEditar =
            findViewById(
                R.id.btnEditarVistaPrevia
            )

        btnGuardar =
            findViewById(
                R.id.btnGuardarVistaPrevia
            )

        btnRegresar =
            findViewById(
                R.id.btnRegresarVistaPrevia
            )


        btnEditar.setOnClickListener {

            habilitarEdicion(true)

            btnGuardar.isEnabled = true

            btnEditar.isEnabled = false
        }


        btnGuardar.setOnClickListener {

            guardarCambios()
        }


        btnRegresar.setOnClickListener {

            val intent =
                Intent(
                    this,
                    BalanceActivity::class.java
                )

            startActivity(intent)

            finish()
        }
    }


    // =====================================================
    // HABILITAR / BLOQUEAR CAMPOS
    // =====================================================

    private fun habilitarEdicion(
        habilitar: Boolean
    ) {

        val campos = listOf(

            txtSaldoAnterior,
            txtBalanceActual,

            txtSueldos,
            txtImpuestos,
            txtGas,
            txtAgua,
            txtLuz,

            txtVerduras,
            txtTaxiCamal,
            txtCarne,
            txtAfiladores,
            txtPanes,
            txtMenudencias,
            txtPescado,
            txtCarneMolida,
            txtPollos,
            txtAbarrotes,
            txtRecojoViveres,
            txtHuevos,
            txtPasajes,
            txtLimpieza,
            txtMantenimiento,
            txtGastosExtras
        )


        campos.forEach {

            it.isEnabled =
                habilitar
        }
    }


    // =====================================================
    // GUARDAR CAMBIOS
    // =====================================================

    private fun guardarCambios() {

        btnGuardar.isEnabled = false


        val ingresos =
            mapOf(
                "fechaId" to fechaId,

                "saldoMesAnterior" to
                        valor(txtSaldoAnterior),

                "balanceMesActual" to
                        valor(txtBalanceActual)
            )


        val egresos =
            mapOf(
                "fechaId" to fechaId,

                "sueldos" to
                        valor(txtSueldos),

                "impuestos" to
                        valor(txtImpuestos),

                "gas" to
                        valor(txtGas),

                "agua" to
                        valor(txtAgua),

                "luz" to
                        valor(txtLuz)
            )


        val gastos =
            mapOf(
                "fechaId" to fechaId,

                "verduras" to
                        valor(txtVerduras),

                "taxiCamal" to
                        valor(txtTaxiCamal),

                "carne" to
                        valor(txtCarne),

                "afiladores" to
                        valor(txtAfiladores),

                "panes" to
                        valor(txtPanes),

                "menudencias" to
                        valor(txtMenudencias),

                "pescado" to
                        valor(txtPescado),

                "carneMolida" to
                        valor(txtCarneMolida),

                "pollos" to
                        valor(txtPollos),

                "abarrotes" to
                        valor(txtAbarrotes),

                "recojoViveres" to
                        valor(txtRecojoViveres),

                "huevos" to
                        valor(txtHuevos),

                "pasajes" to
                        valor(txtPasajes),

                "articulosLimpieza" to
                        valor(txtLimpieza),

                "mantenimiento" to
                        valor(txtMantenimiento),

                "gastosExtras" to
                        valor(txtGastosExtras)
            )


        val actualizaciones =
            hashMapOf<String, Any>(

                "/ingresos/$fechaId" to
                        ingresos,

                "/egresos/$fechaId" to
                        egresos,

                "/Gasto Mensual/$fechaId" to
                        gastos
            )


        db.updateChildren(
            actualizaciones
        )
            .addOnSuccessListener {

                // RECALCULAR BALANCE
                BalanceResumenRepository()
                    .actualizarBalanceMes(
                        fechaId
                    )


                habilitarEdicion(false)

                btnEditar.isEnabled = true

                btnGuardar.isEnabled = false


                Toast.makeText(
                    this,
                    "Datos actualizados correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            }

            .addOnFailureListener { e ->

                btnGuardar.isEnabled = true

                Toast.makeText(
                    this,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }


    // =====================================================
    // UTILIDADES
    // =====================================================

    private fun valor(
        campo: TextInputEditText
    ): Double {

        return campo.text
            ?.toString()
            ?.toDoubleOrNull()
            ?: 0.0
    }


    private fun formatoMonto(
        monto: Double
    ): String {

        return String.format(
            Locale("es", "PE"),
            "%.2f",
            monto
        )
    }


    // =====================================================
    // NOMBRES
    // =====================================================

    private fun configurarNombre(
        idItem: Int,
        nombre: String
    ) {

        val item =
            findViewById<View>(
                idItem
            )

        val txtNombre =
            item.findViewById<TextView>(
                R.id.txtNombreConcepto
            )

        txtNombre.text =
            nombre
    }


    private fun configurarNombres() {

        // INGRESOS

        configurarNombre(
            R.id.itemVistaSaldoAnterior,
            "Saldo mes anterior"
        )

        configurarNombre(
            R.id.itemVistaBalanceActual,
            "Balance mes actual"
        )


        // EGRESOS

        configurarNombre(
            R.id.itemVistaSueldos,
            "Sueldos ayudantes"
        )

        configurarNombre(
            R.id.itemVistaImpuestos,
            "Impuestos"
        )

        configurarNombre(
            R.id.itemVistaGas,
            "Servicio de gas"
        )

        configurarNombre(
            R.id.itemVistaAgua,
            "Servicio de agua"
        )

        configurarNombre(
            R.id.itemVistaLuz,
            "Servicio de luz"
        )


        // GASTOS

        configurarNombre(
            R.id.itemVistaVerduras,
            "Verduras"
        )

        configurarNombre(
            R.id.itemVistaTaxiCamal,
            "Taxi Camal"
        )

        configurarNombre(
            R.id.itemVistaCarne,
            "Corte de carne"
        )

        configurarNombre(
            R.id.itemVistaAfiladores,
            "Afiladores"
        )

        configurarNombre(
            R.id.itemVistaPanes,
            "Panes"
        )

        configurarNombre(
            R.id.itemVistaMenudencias,
            "Menudencia"
        )

        configurarNombre(
            R.id.itemVistaPescado,
            "Pescado"
        )

        configurarNombre(
            R.id.itemVistaCarneMolida,
            "Carne molida"
        )

        configurarNombre(
            R.id.itemVistaPollos,
            "Pollos"
        )

        configurarNombre(
            R.id.itemVistaAbarrotes,
            "Abarrotes"
        )

        configurarNombre(
            R.id.itemVistaRecojoViveres,
            "Recojo de víveres"
        )

        configurarNombre(
            R.id.itemVistaHuevos,
            "Huevos"
        )

        configurarNombre(
            R.id.itemVistaPasajes,
            "Pasajes"
        )

        configurarNombre(
            R.id.itemVistaLimpieza,
            "Artículos de limpieza"
        )

        configurarNombre(
            R.id.itemVistaMantenimiento,
            "Mantenimiento"
        )

        configurarNombre(
            R.id.itemVistaGastosExtras,
            "Gastos extras"
        )
    }
}