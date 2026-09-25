package com.carbajo.checking.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carbajo.checking.R
import com.carbajo.checking.adaptador.BalanceAdapter
import com.carbajo.checking.modelos.BalanceModelo
import com.carbajo.checking.modelos.EgresosModelo
import com.carbajo.checking.modelos.GastoMensualModelo
import com.carbajo.checking.modelos.IngresosModelo
import com.carbajo.checking.pdf.PdfGenerator
import com.google.firebase.database.FirebaseDatabase
import android.content.ClipData
import android.provider.OpenableColumns
import java.text.SimpleDateFormat
import java.util.Locale


class BalanceActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var adapter: BalanceAdapter
    private lateinit var btn_regresarListaBalances: Button
    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_balance)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        rv = findViewById(R.id.rcv_balances)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = BalanceAdapter(
            emptyList(),
            onDescargarClick = { item ->
                // Genera el PDF y muestra toast de guardado
                crearPdfDelMes(item.fechaId) { uri ->
                    if (uri != null) {
                        Toast.makeText(this, "PDF guardado en Descargas/Balances", Toast.LENGTH_SHORT).show()
                        // Si quieres abrirlo automáticamente:
                        // PdfGenerator.abrirPdf(this, uri)   (si tienes esa función)
                    } else {
                        Toast.makeText(this, "No se pudo crear el PDF", Toast.LENGTH_LONG).show()
                    }
                }
            },
            onCompartirClick = { item ->
                // Genera el PDF y abre WhatsApp (o share sheet)
                crearPdfDelMes(item.fechaId) { uri ->
                    if (uri != null) {
                        compartirPdfPorWhatsApp(uri)
                    } else {
                        Toast.makeText(this, "No se pudo crear el PDF", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
        rv.adapter = adapter


        cargarBalances()

        btn_regresarListaBalances = findViewById(R.id.btn_regresarListaBalances)
        btn_regresarListaBalances.setOnClickListener {
            regresarMain()
            finish()
        }



    }


    private fun cargarBalances() {
        // 1) Trae todos los nodos
        db.child("ingresos").get().addOnSuccessListener { snapIngresos ->
            val mapIngresos = mutableMapOf<String, IngresosModelo>()
            snapIngresos.children.forEach { c ->
                c.getValue(IngresosModelo::class.java)?.let { m ->
                    mapIngresos[c.key.orEmpty()] = m
                }
            }

            db.child("Gasto Mensual").get().addOnSuccessListener { snapGastos ->
                val mapGastos = mutableMapOf<String, GastoMensualModelo>()
                snapGastos.children.forEach { c ->
                    c.getValue(GastoMensualModelo::class.java)?.let { m ->
                        mapGastos[c.key.orEmpty()] = m
                    }
                }

                db.child("egresos").get().addOnSuccessListener { snapEgresos ->
                    val mapEgresos = mutableMapOf<String, EgresosModelo>()
                    snapEgresos.children.forEach { c ->
                        c.getValue(EgresosModelo::class.java)?.let { m ->
                            mapEgresos[c.key.orEmpty()] = m
                        }
                    }

                    // 2) Unir por clave yyyy-MM (la unión de todas las llaves)
                    val todasLasFechas = (mapIngresos.keys + mapGastos.keys + mapEgresos.keys).toSortedSet()

                    val formatoFecha = SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale("es", "PE")
                    )

                    formatoFecha.isLenient = false

                    val lista = todasLasFechas.map { key ->

                        val ing = mapIngresos[key]
                        val gas = mapGastos[key]
                        val egr = mapEgresos[key]

                        BalanceModelo(
                            fechaId = key,
                            totalIngresos = ing?.totalIngresos() ?: 0.0,
                            totalGastosMensuales = gas?.subtotal ?: 0.0,
                            totalEgresos = egr?.total ?: 0.0
                        )

                    }.sortedByDescending { balance ->

                        try {
                            formatoFecha.parse(balance.fechaId)?.time ?: 0L
                        } catch (e: Exception) {
                            0L
                        }
                    }

                    adapter.submit(lista)

                }.addOnFailureListener { e -> errorToast(e) }
            }.addOnFailureListener { e -> errorToast(e) }
        }.addOnFailureListener { e -> errorToast(e) }
    }

    private fun errorToast(e: Exception) {
        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
    }


    private fun regresarMain(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun crearPdfDelMes(fechaId: String, onResult: (Uri?) -> Unit) {
        var mIng: IngresosModelo? = null
        var mGas: GastoMensualModelo? = null
        var mEgr: EgresosModelo? = null

        db.child("ingresos").child(fechaId).get().addOnSuccessListener { s1 ->
            mIng = s1.getValue(IngresosModelo::class.java)
            db.child("Gasto Mensual").child(fechaId).get().addOnSuccessListener { s2 ->
                mGas = s2.getValue(GastoMensualModelo::class.java)
                db.child("egresos").child(fechaId).get().addOnSuccessListener { s3 ->
                    mEgr = s3.getValue(EgresosModelo::class.java)

                    val uri = PdfGenerator.crearPdfBalanceDetallado(this, fechaId, mIng, mGas, mEgr)
                    onResult(uri)

                }.addOnFailureListener { e -> errorToast(e); onResult(null) }
            }.addOnFailureListener { e -> errorToast(e); onResult(null) }
        }.addOnFailureListener { e -> errorToast(e); onResult(null) }
    }

    private fun nombreDe(uri: Uri): String {
        contentResolver.query(uri, null, null, null, null)?.use { c ->
            val idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (idx != -1 && c.moveToFirst()) return c.getString(idx) ?: "balance.pdf"
        }
        return "balance.pdf"
    }


    private fun compartirPdfPorWhatsApp(pdfUri: Uri, textoOpcional: String = "Te adjunto el balance en PDF") {
        val displayName = nombreDe(pdfUri)

        val base = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            putExtra(Intent.EXTRA_TEXT, textoOpcional)
            // sugerir nombre/título en algunos choosers
            putExtra(Intent.EXTRA_TITLE, displayName)
            putExtra(Intent.EXTRA_SUBJECT, displayName)

            // ClipData con etiqueta = nombre de archivo (mejora lo que muestra el chooser)
            clipData = ClipData.newUri(contentResolver, displayName, pdfUri)

            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Targets específicos (WhatsApp y WhatsApp Business)
        val wa = Intent(base).apply { setPackage("com.whatsapp") }
        val wab = Intent(base).apply { setPackage("com.whatsapp.w4b") }

        try { grantUriPermission("com.whatsapp", pdfUri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {}
        try { grantUriPermission("com.whatsapp.w4b", pdfUri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) {}

        val pm = packageManager
        val targets = buildList {
            if (wa.resolveActivity(pm) != null) add(wa)
            if (wab.resolveActivity(pm) != null) add(wab)
        }

        if (targets.isNotEmpty()) {
            val chooser = Intent.createChooser(targets.first(), "Compartir PDF por WhatsApp").apply {
                if (targets.size > 1) putExtra(Intent.EXTRA_INITIAL_INTENTS, targets.drop(1).toTypedArray())
            }
            startActivity(chooser)
        } else {
            // sin WhatsApp: usa el share sheet general
            startActivity(Intent.createChooser(base, "Compartir PDF"))
        }
    }







}