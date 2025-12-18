package com.carbajo.checking.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.carbajo.checking.R

class MainActivity : AppCompatActivity() {

    private lateinit var btn_crearBalance: Button
    private lateinit var btn_crearIngresos: Button
    private lateinit var btn_crearEgresos: Button
    private lateinit var btn_crearGastosMensuales: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

}