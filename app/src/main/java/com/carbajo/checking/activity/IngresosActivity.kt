package com.carbajo.checking.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.carbajo.checking.modelos.IngresosModelo
import com.carbajo.checking.activity.MainActivity
import com.carbajo.checking.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class IngresosActivity : AppCompatActivity() {

    private lateinit var txt_saldito: TextInputEditText
    private lateinit var txt_balance: TextInputEditText
    private lateinit var txt_fechaIngresos: TextView
    private lateinit var txt_totalIngresos: TextView
    private lateinit var btn_registroIngreso: Button
    private lateinit var btn_backIngreso: Button
    private lateinit var btn_seleccionarFechaIngresos: Button

    private var db = Firebase.database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ingresos)

        asignarIngresos()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val calendario = Calendar.getInstance()
        val fecha = DatePickerDialog.OnDateSetListener{datepicker, year , month , day ->
            calendario.set(Calendar.YEAR, year)
            calendario.set(Calendar.MONTH , month)
            calendario.set(Calendar.DAY_OF_MONTH, day)

            actualizarFecha(calendario)
        }


        btn_seleccionarFechaIngresos.setOnClickListener {
            DatePickerDialog(
                this,
                fecha,
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btn_registroIngreso.setOnClickListener { registrarIngresos() }
        btn_backIngreso.setOnClickListener { regresarMain() }

        actualizarFecha(calendario)

        txt_saldito.addTextChangedListener {actualizarTotal()}
        txt_balance.addTextChangedListener {actualizarTotal()}


    }

    private fun asignarIngresos(){

        txt_saldito = findViewById(R.id.txt_saldito)
        txt_balance = findViewById(R.id.txt_balance)
        txt_fechaIngresos = findViewById(R.id.txt_fechaIngresos)
        txt_totalIngresos = findViewById(R.id.txt_totalIngresos)
        btn_seleccionarFechaIngresos = findViewById(R.id.btn_seleccionarFechaIngresos)
        btn_registroIngreso = findViewById(R.id.btn_registroIngreso)
        btn_backIngreso = findViewById(R.id.btn_backIngreso)

    }

    private fun registrarIngresos(){
        val saldo = txt_saldito.text.toString().toDoubleOrNull() ?:0.0
        val balance = txt_balance.text.toString().toDoubleOrNull() ?: 0.0
        val fecha = txt_fechaIngresos.text.toString()
        val ingresos = IngresosModelo(fecha, saldo, balance)


        btn_registroIngreso.isEnabled = false

        val ref = Firebase.database.getReference("ingresos").child(fecha)
        ref.setValue(ingresos)
            .addOnSuccessListener {
                Toast.makeText(this, "Guardado", Toast.LENGTH_SHORT).show()
                limpiarCampos()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
            .addOnCompleteListener {

                btn_registroIngreso.isEnabled = true
            }
    }

    private fun actualizarTotal(){
        val saldo = txt_saldito.text?.toString()?.toDoubleOrNull()
        val balance = txt_balance.text?.toString()?.toDoubleOrNull()

        if (saldo !=null && balance != null){
            val total = saldo + balance
            txt_totalIngresos.text = String.Companion.format(Locale.getDefault(), "%.2f", total)
        }else{
            txt_totalIngresos.text = "0.00"
        }
    }

    private fun regresarMain(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun limpiarCampos() {

        txt_saldito.text?.clear()
        txt_balance.text?.clear()
        txt_totalIngresos.text = "0.00"
        txt_saldito.clearFocus()
        txt_balance.clearFocus()

    }

    private fun actualizarFecha (calendar: Calendar){
        val formato = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        txt_fechaIngresos.text = formato.format(calendar.time)

    }

}