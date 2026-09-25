package com.carbajo.checking.activity

import android.app.DatePickerDialog
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
import androidx.core.widget.addTextChangedListener
import com.carbajo.checking.modelos.GastoMensualModelo
import com.carbajo.checking.activity.MainActivity
import com.carbajo.checking.data.BalanceResumenRepository
import com.carbajo.checking.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.database
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GastosMensualActivity : AppCompatActivity() {

    private lateinit var txt_fechaGastos: TextView
    private lateinit var txt_verduras: TextInputEditText
    private lateinit var txt_taxiCamal: TextInputEditText
    private lateinit var txt_corteCarne: TextInputEditText
    private lateinit var txt_afiladores: TextInputEditText
    private lateinit var txt_panes: TextInputEditText
    private lateinit var txt_menudencia: TextInputEditText
    private lateinit var txt_pescado: TextInputEditText
    private lateinit var txt_carneMolida: TextInputEditText
    private lateinit var txt_pollos: TextInputEditText
    private lateinit var txt_abarrotes: TextInputEditText
    private lateinit var txt_recojoViveres: TextInputEditText
    private lateinit var txt_huevos: TextInputEditText
    private lateinit var txt_pasajes: TextInputEditText
    private lateinit var txt_limpieza: TextInputEditText
    private lateinit var txt_mantenimiento: TextInputEditText
    private lateinit var txt_gastosExtras: TextInputEditText
    private lateinit var txt_totalGastosMensuales: TextView
    private lateinit var btn_registroGastosMensuales: Button
    private lateinit var btn_backMainGastos: Button
    private lateinit var btn_seleccionarFechaGastos: Button

    private val db = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gastos_mensual)

        crearGastosMensuales2()

        val root = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(root) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(
                sys.left,
                sys.top,
                sys.right,
                if (imeVisible) ime.bottom else sys.bottom
            )
            insets
        }

        val calendario = Calendar.getInstance()
        val fechaGastos = DatePickerDialog.OnDateSetListener{datepicker, year , month , day ->
            calendario.set(Calendar.YEAR, year)
            calendario.set(Calendar.MONTH, month)
            calendario.set(Calendar.DAY_OF_MONTH, day)

            actualizarFechaGasto(calendario)

        }

        btn_seleccionarFechaGastos.setOnClickListener {
            DatePickerDialog(
                this,
                fechaGastos,
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btn_registroGastosMensuales.setOnClickListener { registrarGastosMensuales() }
        btn_backMainGastos.setOnClickListener { regresarMain3() }

        actualizarFechaGasto(calendario)

        txt_verduras.addTextChangedListener {actualizarGastosMensuales()}
        txt_taxiCamal.addTextChangedListener {actualizarGastosMensuales()}
        txt_corteCarne.addTextChangedListener {actualizarGastosMensuales()}
        txt_afiladores.addTextChangedListener {actualizarGastosMensuales()}
        txt_panes.addTextChangedListener {actualizarGastosMensuales()}
        txt_menudencia.addTextChangedListener {actualizarGastosMensuales()}
        txt_pescado.addTextChangedListener {actualizarGastosMensuales()}
        txt_carneMolida.addTextChangedListener {actualizarGastosMensuales()}
        txt_pollos.addTextChangedListener {actualizarGastosMensuales()}
        txt_abarrotes.addTextChangedListener {actualizarGastosMensuales()}
        txt_recojoViveres.addTextChangedListener {actualizarGastosMensuales()}
        txt_huevos.addTextChangedListener {actualizarGastosMensuales()}
        txt_pasajes.addTextChangedListener {actualizarGastosMensuales()}
        txt_limpieza.addTextChangedListener {actualizarGastosMensuales()}
        txt_mantenimiento.addTextChangedListener {actualizarGastosMensuales()}
        txt_gastosExtras.addTextChangedListener {actualizarGastosMensuales()}


    }

    private fun crearGastosMensuales2 () {
        txt_fechaGastos = findViewById(R.id.txt_fechaGastos)
        txt_totalGastosMensuales = findViewById(R.id.txt_totalGastosMensuales)
        txt_verduras = findViewById(R.id.txt_verduras)
        txt_taxiCamal = findViewById(R.id.txt_taxiCamal)
        txt_corteCarne = findViewById(R.id.txt_corteCarne)
        txt_afiladores = findViewById(R.id.txt_afiladores)
        txt_panes = findViewById(R.id.txt_panes)
        txt_menudencia = findViewById(R.id.txt_menudencia)
        txt_pescado = findViewById(R.id.txt_pescado)
        txt_carneMolida = findViewById(R.id.txt_carneMolida)
        txt_pollos = findViewById(R.id.txt_pollos)
        txt_abarrotes = findViewById(R.id.txt_abarrotes)
        txt_recojoViveres = findViewById(R.id.txt_recojoViveres)
        txt_huevos = findViewById(R.id.txt_huevos)
        txt_pasajes = findViewById(R.id.txt_pasajes)
        txt_limpieza = findViewById(R.id.txt_limpieza)
        txt_mantenimiento = findViewById(R.id.txt_mantenimiento)
        txt_gastosExtras = findViewById(R.id.txt_gastosExtras)
        btn_registroGastosMensuales = findViewById(R.id.btn_registroGastosMensuales)
        btn_seleccionarFechaGastos = findViewById(R.id.btn_seleccionarFechaGastos)
        btn_backMainGastos = findViewById(R.id.btn_backMainGastos)


    }

    private fun registrarGastosMensuales() {

        val fechaGastos = txt_fechaGastos.text.toString()
        val verduras = txt_verduras.text.toString().toDoubleOrNull() ?:0.0
        val taxiCamal = txt_taxiCamal.text.toString().toDoubleOrNull() ?:0.0
        val carne = txt_corteCarne.text.toString().toDoubleOrNull() ?:0.0
        val afiladores = txt_afiladores.text.toString().toDoubleOrNull() ?:0.0
        val panes = txt_panes.text.toString().toDoubleOrNull() ?:0.0
        val menudencias = txt_menudencia.text.toString().toDoubleOrNull() ?:0.0
        val pescado = txt_pescado.text.toString().toDoubleOrNull() ?:0.0
        val carneMolida = txt_carneMolida.text.toString().toDoubleOrNull() ?:0.0
        val pollos = txt_pollos.text.toString().toDoubleOrNull() ?:0.0
        val abarrotes = txt_abarrotes.text.toString().toDoubleOrNull() ?:0.0
        val recojoViveres = txt_recojoViveres.text.toString().toDoubleOrNull() ?:0.0
        val huevos = txt_huevos.text.toString().toDoubleOrNull() ?:0.0
        val pasajes = txt_pasajes.text.toString().toDoubleOrNull() ?:0.0
        val articulosLimpieza = txt_limpieza.text.toString().toDoubleOrNull() ?:0.0
        val mantenimiento = txt_mantenimiento.text.toString().toDoubleOrNull() ?:0.0
        val gastosExtras = txt_gastosExtras.text.toString().toDoubleOrNull() ?:0.0
        val gastosMensual = GastoMensualModelo(
            fechaGastos, verduras, taxiCamal, carne, afiladores,
            panes, menudencias, pescado, carneMolida, pollos, abarrotes, recojoViveres, huevos,
            pasajes, articulosLimpieza, mantenimiento, gastosExtras
        )

        btn_registroGastosMensuales.isEnabled = false

        val ref = com.google.firebase.Firebase.database
            .getReference("Gasto Mensual")
            .child(fechaGastos)

        ref.setValue(gastosMensual)
            .addOnSuccessListener {

                BalanceResumenRepository()
                    .actualizarBalanceMes(fechaGastos)

                Toast.makeText(
                    this,
                    "Guardado",
                    Toast.LENGTH_SHORT
                ).show()

                limpiarCampos()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
            .addOnCompleteListener {
                btn_registroGastosMensuales.isEnabled = true
            }
    }

    private fun actualizarGastosMensuales() {
        val verduras = txt_verduras.text.toString().toDoubleOrNull() ?:0.0
        val taxiCamal = txt_taxiCamal.text.toString().toDoubleOrNull() ?:0.0
        val carne = txt_corteCarne.text.toString().toDoubleOrNull() ?:0.0
        val afiladores = txt_afiladores.text.toString().toDoubleOrNull() ?:0.0
        val panes = txt_panes.text.toString().toDoubleOrNull() ?:0.0
        val menudencias = txt_menudencia.text.toString().toDoubleOrNull() ?:0.0
        val pescado = txt_pescado.text.toString().toDoubleOrNull() ?:0.0
        val carneMolida = txt_carneMolida.text.toString().toDoubleOrNull() ?:0.0
        val pollos = txt_pollos.text.toString().toDoubleOrNull() ?:0.0
        val abarrotes = txt_abarrotes.text.toString().toDoubleOrNull() ?:0.0
        val recojoViveres = txt_recojoViveres.text.toString().toDoubleOrNull() ?:0.0
        val huevos = txt_huevos.text.toString().toDoubleOrNull() ?:0.0
        val pasajes = txt_pasajes.text.toString().toDoubleOrNull() ?:0.0
        val articulosLimpieza = txt_limpieza.text.toString().toDoubleOrNull() ?:0.0
        val mantenimiento = txt_mantenimiento.text.toString().toDoubleOrNull() ?:0.0
        val gastosExtras = txt_gastosExtras.text.toString().toDoubleOrNull() ?:0.0

        if(verduras != null && taxiCamal != null && carne != null && afiladores != null && panes != null && menudencias != null && pescado != null
            && carneMolida != null && pollos != null && abarrotes != null && recojoViveres != null && huevos != null && pasajes != null && articulosLimpieza != null
            && mantenimiento != null && gastosExtras != null){
            val totalGastosMensual = verduras + taxiCamal + carne + afiladores + panes + menudencias + pescado + carneMolida + pollos +
                    abarrotes + recojoViveres + huevos + pasajes + articulosLimpieza + mantenimiento + gastosExtras
            txt_totalGastosMensuales.text = String.Companion.format(Locale.getDefault(),"%.2f", totalGastosMensual)
        }else{
            txt_totalGastosMensuales.text = "0.00"
        }

    }

    private fun regresarMain3() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun limpiarCampos() {
        txt_verduras.text?.clear()
        txt_taxiCamal.text?.clear()
        txt_corteCarne.text?.clear()
        txt_afiladores.text?.clear()
        txt_panes.text?.clear()
        txt_menudencia.text?.clear()
        txt_pescado.text?.clear()
        txt_carneMolida.text?.clear()
        txt_pollos.text?.clear()
        txt_abarrotes.text?.clear()
        txt_recojoViveres.text?.clear()
        txt_huevos.text?.clear()
        txt_pasajes.text?.clear()
        txt_limpieza.text?.clear()
        txt_mantenimiento.text?.clear()
        txt_gastosExtras.text?.clear()
        txt_totalGastosMensuales.text = "0.00"
        txt_verduras.requestFocus()
    }


    private fun actualizarFechaGasto (calendar: Calendar){
        val formarto = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        txt_fechaGastos.text = formarto.format(calendar.time)
    }

}