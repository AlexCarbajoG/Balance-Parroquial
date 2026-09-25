package com.carbajo.checking.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.carbajo.checking.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.carbajo.checking.modelos.IngresosModelo
import com.carbajo.checking.modelos.BalanceResumenModelo
import com.carbajo.checking.modelos.GastoMensualModelo
import com.carbajo.checking.modelos.EgresosModelo
import com.carbajo.checking.modelos.BalanceModelo
import java.util.Calendar


class MainActivity : AppCompatActivity() {

    private lateinit var btn_crearBalance: Button
    private lateinit var btn_crearIngresos: Button
    private lateinit var btn_crearEgresos: Button
    private lateinit var btn_crearGastosMensuales: Button
    private lateinit var btn_salir: Button
    private lateinit var btn_crearRegistro: Button
    private lateinit var txt_fechaActual: TextView
    private lateinit var txt_mostrarBalanceMesActual: TextView
    private lateinit var txt_balanceDelMes: TextView
    private lateinit var txtIngresosMes: TextView
    private lateinit var txtEgresosMes: TextView
    private val db = FirebaseDatabase.getInstance().reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.txt_ingresos)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        crearBalance()
        crearIngresos()
        crearEgresos()
        crearGastosMensuales()
        crearHorarios()
        mostrarFechaActual()
        asignarBalanceMain()
        cargarUltimoBalanceResumen()
        salir()
    }

    private fun crearHorarios(){
        btn_crearRegistro = findViewById(R.id.btn_crearRegistro)
        btn_crearRegistro.setOnClickListener {
            val intent = Intent(this, HorariosActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun crearGastosMensuales() {
        btn_crearGastosMensuales = findViewById(R.id.btn_gastosMensuales)
        btn_crearGastosMensuales.setOnClickListener {
            val intent = Intent(this, GastosMensualActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun crearEgresos() {
        btn_crearEgresos = findViewById(R.id.btn_egresos)
        btn_crearEgresos.setOnClickListener {
            val intent = Intent(this, EgresosActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun crearBalance() {
        btn_crearBalance = findViewById(R.id.btn_crearBalance)
        btn_crearBalance.setOnClickListener {
            val intent = Intent(this, BalanceActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    private fun crearIngresos(){
        btn_crearIngresos = findViewById(R.id.btn_crearIngresos)
        btn_crearIngresos.setOnClickListener {
            val intent = Intent(this, IngresosActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun mostrarFechaActual() {
        txt_fechaActual = findViewById(R.id.txt_mostrarFechaActual)

        val  formatoFecha = SimpleDateFormat(
            "EEEE, d 'de' MMMM yyyy",
            Locale("es","PE")
        )
        val fechaActual = formatoFecha.format(Date())
        val fechaFormateada =
            fechaActual.replaceFirstChar { it.uppercase() }

        txt_fechaActual.text = fechaFormateada
    }

    private fun asignarBalanceMain() {

        txt_mostrarBalanceMesActual =
            findViewById(R.id.txt_mostrarBalanceMesActual)

        txtIngresosMes =
            findViewById(R.id.txtIngresosMes)

        txtEgresosMes =
            findViewById(R.id.txtEgresosMes)

        txt_balanceDelMes =
            findViewById(R.id.txt_balanceDelMes)
    }

    private fun cargarUltimoBalanceResumen() {

        val ref = FirebaseDatabase
            .getInstance()
            .reference
            .child("balances")

        ref.orderByKey()
            .limitToLast(1)
            .get()
            .addOnSuccessListener { snapshot ->

                val ultimoSnapshot =
                    snapshot.children.firstOrNull()

                if (ultimoSnapshot != null) {

                    val balance =
                        ultimoSnapshot.getValue(
                            BalanceResumenModelo::class.java
                        )

                    if (balance != null) {

                        txt_mostrarBalanceMesActual.text =
                            "S/. %.2f".format(balance.utilidad)

                        txtIngresosMes.text =
                            "S/. %.2f".format(balance.totalIngresos)

                        val totalSalidas =
                            balance.totalGastosMensuales +
                                    balance.totalEgresos

                        txtEgresosMes.text =
                            "S/. %.2f".format(totalSalidas)

                        txt_balanceDelMes.text =
                            "BALANCE DE ${obtenerNombreMes(balance.fechaId)}"
                    }
                }
            }
            .addOnFailureListener {

                txt_mostrarBalanceMesActual.text = "S/. 0.00"
                txtIngresosMes.text = "S/. 0.00"
                txtEgresosMes.text = "S/. 0.00"
                txt_balanceDelMes.text = "BALANCE SIN REGISTRO"
            }
    }

    private fun obtenerNombreMes(fechaId: String): String {

        val locale = Locale("es", "PE")

        return try {

            val formatoEntrada =
                SimpleDateFormat(
                    "yyyy-MM",
                    locale
                )

            val fecha =
                formatoEntrada.parse(fechaId)
                    ?: return ""

            val formatoMes =
                SimpleDateFormat(
                    "MMMM",
                    locale
                )

            formatoMes
                .format(fecha)
                .uppercase(locale)

        } catch (e: Exception) {

            ""
        }
    }


    private fun salir(){
        btn_salir = findViewById(R.id.btn_salir)

        btn_salir.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
        }
    }

}
