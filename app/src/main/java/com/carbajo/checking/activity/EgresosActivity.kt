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
import com.carbajo.checking.modelos.EgresosModelo
import com.carbajo.checking.activity.MainActivity
import com.carbajo.checking.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EgresosActivity : AppCompatActivity() {


    private lateinit var txt_fechaEgresos: TextView
    private lateinit var txt_sueldoAyudantes: TextInputEditText
    private lateinit var txt_impuestos: TextInputEditText
    private lateinit var txt_servicioGas: TextInputEditText
    private lateinit var txt_servicioAgua: TextInputEditText
    private lateinit var txt_servicioLuz: TextInputEditText
    private lateinit var txt_totalEgresos: TextView
    private lateinit var btn_registroEgresos: Button
    private lateinit var btn_backMainEgresos: Button
    private lateinit var btn_seleccionarFechaEgresos: Button

    private var db = Firebase.database

//    private var fechaAutomatica = SimpleDateFormat("yyyy-MM", Locale.getDefault())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_egresos)

        crearEgresos()

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
        val fechaEgreso = DatePickerDialog.OnDateSetListener{datepicker, year , month , day ->
            calendario.set(Calendar.YEAR, year)
            calendario.set(Calendar.MONTH, month)
            calendario.set(Calendar.DAY_OF_MONTH, day)

            actualizarFechaEgreso(calendario)
        }

        btn_seleccionarFechaEgresos.setOnClickListener {
            DatePickerDialog(
                this,
                fechaEgreso,
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btn_registroEgresos.setOnClickListener { registrarEgresos() }
        btn_backMainEgresos.setOnClickListener { regresarMain2() }

        actualizarFechaEgreso(calendario)

        txt_sueldoAyudantes.addTextChangedListener {actualizarTotalEgreso()}
        txt_impuestos.addTextChangedListener {actualizarTotalEgreso()}
        txt_servicioGas.addTextChangedListener {actualizarTotalEgreso()}
        txt_servicioAgua.addTextChangedListener {actualizarTotalEgreso()}
        txt_servicioLuz.addTextChangedListener {actualizarTotalEgreso()}


    }

    private fun crearEgresos(){
        txt_fechaEgresos = findViewById(R.id.txt_fechaEgresos)
        txt_sueldoAyudantes = findViewById(R.id.txt_sueldoAyudantes)
        txt_impuestos = findViewById(R.id.txt_impuestos)
        txt_servicioGas = findViewById(R.id.txt_servicioGas)
        txt_servicioAgua = findViewById(R.id.txt_servicioAgua)
        txt_servicioLuz = findViewById(R.id.txt_servicioLuz)
        txt_totalEgresos = findViewById(R.id.txt_totalEgresos)
        btn_registroEgresos = findViewById(R.id.btn_registroEgresos)
        btn_backMainEgresos = findViewById(R.id.btn_backMainEgresos)
        btn_seleccionarFechaEgresos = findViewById(R.id.btn_seleccionarFechaEgresos)

    }

    private fun registrarEgresos() {
        val fechaEgresos = txt_fechaEgresos.text.toString()
        val sueldoAyudantes = txt_sueldoAyudantes.text.toString().toDoubleOrNull() ?:0.0
        val impuestos = txt_impuestos.text.toString().toDoubleOrNull() ?:0.0
        val serviciosGas = txt_servicioGas.text.toString().toDoubleOrNull() ?:0.0
        val serviciosAgua = txt_servicioAgua.text.toString().toDoubleOrNull() ?:0.0
        val servicioLuz = txt_servicioLuz.text.toString().toDoubleOrNull() ?:0.0
        val egresos = EgresosModelo(
            fechaEgresos, sueldoAyudantes, impuestos, serviciosGas,
            serviciosAgua, servicioLuz
        )

        btn_registroEgresos.isEnabled = false

        val ref = Firebase.database.getReference("egresos").child(fechaEgresos)
        ref.setValue(egresos)
            .addOnSuccessListener {
                Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show()
                limpiarCampos()           // ← limpia inputs y total
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
            .addOnCompleteListener {
                btn_registroEgresos.isEnabled = true
            }
    }

    private fun actualizarTotalEgreso() {
        val sueldoAyudantes = txt_sueldoAyudantes.text?.toString()?.toDoubleOrNull()
        val impuestos = txt_impuestos.text?.toString()?.toDoubleOrNull()
        val servicioGas = txt_servicioGas.text?.toString()?.toDoubleOrNull()
        val servicioAgua = txt_servicioAgua.text?.toString()?.toDoubleOrNull()
        val servicioLuz = txt_servicioLuz.text?.toString()?.toDoubleOrNull()

        if (sueldoAyudantes != null && impuestos != null && servicioGas != null &&
            servicioAgua != null && servicioLuz != null){
            val totalEgresos = sueldoAyudantes + impuestos + servicioGas + servicioAgua + servicioLuz
            txt_totalEgresos.text = String.Companion.format(Locale.getDefault(),"%.2f", totalEgresos)
        }else{
            txt_totalEgresos.text = "0.00"
        }

    }

    private fun regresarMain2(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun limpiarCampos() {

        txt_sueldoAyudantes.text?.clear()
        txt_impuestos.text?.clear()
        txt_servicioGas.text?.clear()
        txt_servicioAgua.text?.clear()
        txt_servicioLuz.text?.clear()
        txt_totalEgresos.text = "0.00"
        txt_sueldoAyudantes.clearFocus()
        txt_impuestos.clearFocus()
        txt_servicioGas.clearFocus()
        txt_servicioAgua.clearFocus()
        txt_servicioLuz.clearFocus()
        txt_sueldoAyudantes.requestFocus()
    }

    private fun actualizarFechaEgreso (calendar: Calendar){
        val formato = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        txt_fechaEgresos.text = formato.format(calendar.time)
    }

}