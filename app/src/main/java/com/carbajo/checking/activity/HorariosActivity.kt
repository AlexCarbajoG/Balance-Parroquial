package com.carbajo.checking.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.carbajo.checking.R
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.carbajo.checking.pdf.PdfGeneratorHorarios
import android.content.Context
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.print.PageRange
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class HorariosActivity : AppCompatActivity() {

    private lateinit var btn_regresoHorarios: Button
    private lateinit var btnDescargarHorario: Button
    private lateinit var btnImprimirHorario: Button

    private lateinit var txtAnioPeriodo: TextView
    private lateinit var txtMesAnterior: TextView
    private lateinit var txtMesActual: TextView
    private lateinit var txtMesSiguiente: TextView

    private lateinit var cardMesAnterior: MaterialCardView
    private lateinit var cardMesActual: MaterialCardView
    private lateinit var cardMesSiguiente: MaterialCardView

    private val calendarioSeleccionado = Calendar.getInstance()
    private val calendarioActual = Calendar.getInstance()
    private val limiteMinimo = Calendar.getInstance()

    private lateinit var radioResponsable1: RadioButton
    private lateinit var radioResponsable2: RadioButton
    private lateinit var radioResponsable3: RadioButton

    private lateinit var cardResponsableMaria: MaterialCardView
    private lateinit var cardResponsableJose: MaterialCardView
    private lateinit var cardResponsableCarmen: MaterialCardView

    private lateinit var txtNombreResponsable1: TextView
    private lateinit var txtNombreResponsable2: TextView
    private lateinit var txtNombreResponsable3: TextView

    private var responsableSeleccionado: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_horarios)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        configurarPeriodo()
        obtenerPeriodoSeleccionado()
        configurarResponsables()
        configurarPdfHorarios()
        regresarMain7()
    }

    private fun configurarPeriodo() {

        txtAnioPeriodo = findViewById(R.id.txtAnioPeriodo)
        txtMesAnterior = findViewById(R.id.txtMesAnterior)
        txtMesActual = findViewById(R.id.txtMesActual)
        txtMesSiguiente = findViewById(R.id.txtMesSiguiente)

        cardMesAnterior = findViewById(R.id.cardMesAnterior)
        cardMesActual = findViewById(R.id.cardMesActual)
        cardMesSiguiente = findViewById(R.id.cardMesSiguiente)

        // El mes seleccionado inicia en el mes actual
        calendarioSeleccionado.time = calendarioActual.time

        // Solo permitir regresar hasta 1 año atrás
        limiteMinimo.time = calendarioActual.time
        limiteMinimo.add(Calendar.YEAR, -1)

        actualizarPeriodo()

        cardMesAnterior.setOnClickListener {
            retrocederMes()
        }

        cardMesSiguiente.setOnClickListener {
            avanzarMes()
        }
    }

    private fun actualizarPeriodo() {

        val locale = Locale("es", "PE")
        val formatoMes = SimpleDateFormat("MMM", locale)

        // MES SELECCIONADO
        txtMesActual.text =
            formatoMes.format(calendarioSeleccionado.time)
                .replace(".", "")
                .replaceFirstChar { it.uppercase() }

        txtAnioPeriodo.text =
            calendarioSeleccionado.get(Calendar.YEAR).toString()


        // MES ANTERIOR
        val anterior =
            calendarioSeleccionado.clone() as Calendar

        anterior.add(Calendar.MONTH, -1)

        txtMesAnterior.text =
            formatoMes.format(anterior.time)
                .replace(".", "")
                .replaceFirstChar { it.uppercase() }


        // MES SIGUIENTE
        val siguiente =
            calendarioSeleccionado.clone() as Calendar

        siguiente.add(Calendar.MONTH, 1)

        txtMesSiguiente.text =
            formatoMes.format(siguiente.time)
                .replace(".", "")
                .replaceFirstChar { it.uppercase() }
    }

    private fun retrocederMes() {

        val posibleMes =
            calendarioSeleccionado.clone() as Calendar

        posibleMes.add(Calendar.MONTH, -1)

        if (posibleMes.before(limiteMinimo)) {

            Toast.makeText(
                this,
                "Solo puedes consultar hasta un año atrás",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        calendarioSeleccionado.add(Calendar.MONTH, -1)

        actualizarPeriodo()
    }

    private fun avanzarMes() {

        calendarioSeleccionado.add(Calendar.MONTH, 1)

        actualizarPeriodo()
    }

    private fun obtenerPeriodoSeleccionado(): String {

        return SimpleDateFormat(
            "yyyy-MM",
            Locale("es", "PE")
        ).format(calendarioSeleccionado.time)
    }

    private fun configurarResponsables() {

        radioResponsable1 = findViewById(R.id.radioResponsable1)
        radioResponsable2 = findViewById(R.id.radioResponsable2)
        radioResponsable3 = findViewById(R.id.radioResponsable3)

        cardResponsableMaria = findViewById(R.id.cardResponsableMaria)
        cardResponsableJose = findViewById(R.id.cardResponsableJose)
        cardResponsableCarmen = findViewById(R.id.cardResponsableCarmen)

        txtNombreResponsable1 = findViewById(R.id.txtNombreResponsable1)
        txtNombreResponsable2 = findViewById(R.id.txtNombreResponsable2)
        txtNombreResponsable3 = findViewById(R.id.txtNombreResponsable3)


        // Responsable inicial
        seleccionarResponsable(1)


        cardResponsableMaria.setOnClickListener {
            seleccionarResponsable(1)
        }

        cardResponsableJose.setOnClickListener {
            seleccionarResponsable(2)
        }

        cardResponsableCarmen.setOnClickListener {
            seleccionarResponsable(3)
        }


        radioResponsable1.setOnClickListener {
            seleccionarResponsable(1)
        }

        radioResponsable2.setOnClickListener {
            seleccionarResponsable(2)
        }

        radioResponsable3.setOnClickListener {
            seleccionarResponsable(3)
        }
    }

    private fun seleccionarResponsable(numero: Int) {

        radioResponsable1.isChecked = numero == 1
        radioResponsable2.isChecked = numero == 2
        radioResponsable3.isChecked = numero == 3


        responsableSeleccionado = when (numero) {

            1 -> txtNombreResponsable1.text.toString()

            2 -> txtNombreResponsable2.text.toString()

            3 -> txtNombreResponsable3.text.toString()

            else -> ""
        }


        // Cambiar color visual de la tarjeta seleccionada

        cardResponsableMaria.setCardBackgroundColor(
            if (numero == 1)
                android.graphics.Color.parseColor("#EDF5EE")
            else
                android.graphics.Color.parseColor("#F2F6F2")
        )

        cardResponsableJose.setCardBackgroundColor(
            if (numero == 2)
                android.graphics.Color.parseColor("#EDF5EE")
            else
                android.graphics.Color.parseColor("#F2F6F2")
        )

        cardResponsableCarmen.setCardBackgroundColor(
            if (numero == 3)
                android.graphics.Color.parseColor("#EDF5EE")
            else
                android.graphics.Color.parseColor("#F2F6F2")
        )
    }

    private fun configurarPdfHorarios() {

        btnDescargarHorario =
            findViewById(R.id.button3)

        btnImprimirHorario =
            findViewById(R.id.button2)


        btnDescargarHorario.setOnClickListener {

            descargarHorario()
        }


        btnImprimirHorario.setOnClickListener {

            imprimirHorario()
        }
    }

    private fun descargarHorario() {

        if (responsableSeleccionado.isBlank()) {

            Toast.makeText(
                this,
                "Selecciona un responsable",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        val mesSeleccionado =
            calendarioSeleccionado.get(
                Calendar.MONTH
            ) + 1


        val anioSeleccionado =
            calendarioSeleccionado.get(
                Calendar.YEAR
            )


        val uri =
            PdfGeneratorHorarios.crearPdfHorarios(
                context = this,
                anio = anioSeleccionado,
                mes = mesSeleccionado,
                responsable = responsableSeleccionado
            )


        if (uri != null) {

            Toast.makeText(
                this,
                "PDF guardado en Descargas/Horarios",
                Toast.LENGTH_LONG
            ).show()

        } else {

            Toast.makeText(
                this,
                "No se pudo crear el PDF",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun imprimirHorario() {

        if (responsableSeleccionado.isBlank()) {

            Toast.makeText(
                this,
                "Selecciona un responsable",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        val mesSeleccionado =
            calendarioSeleccionado.get(
                Calendar.MONTH
            ) + 1


        val anioSeleccionado =
            calendarioSeleccionado.get(
                Calendar.YEAR
            )


        val archivo =
            PdfGeneratorHorarios.crearPdfTemporal(
                context = this,
                anio = anioSeleccionado,
                mes = mesSeleccionado,
                responsable = responsableSeleccionado
            )


        if (archivo == null) {

            Toast.makeText(
                this,
                "No se pudo preparar el documento",
                Toast.LENGTH_LONG
            ).show()

            return
        }


        val printManager =
            getSystemService(
                Context.PRINT_SERVICE
            ) as PrintManager


        val adapter =
            PdfFilePrintAdapter(
                archivo
            )


        val atributos =
            PrintAttributes.Builder()

                .setMediaSize(
                    PrintAttributes.MediaSize.ISO_A4
                        .asLandscape()
                )

                .build()


        printManager.print(
            archivo.nameWithoutExtension,
            adapter,
            atributos
        )
    }

    private class PdfFilePrintAdapter(
        private val archivo: File
    ) : PrintDocumentAdapter() {


        override fun onLayout(
            oldAttributes: PrintAttributes?,
            newAttributes: PrintAttributes?,
            cancellationSignal: CancellationSignal?,
            callback: LayoutResultCallback,
            extras: Bundle?
        ) {

            if (
                cancellationSignal?.isCanceled == true
            ) {

                callback.onLayoutCancelled()
                return
            }


            val info =
                PrintDocumentInfo.Builder(
                    archivo.name
                )
                    .setContentType(
                        PrintDocumentInfo.CONTENT_TYPE_DOCUMENT
                    )
                    .setPageCount(1)
                    .build()


            callback.onLayoutFinished(
                info,
                true
            )
        }


        override fun onWrite(
            pages: Array<out PageRange>,
            destination: ParcelFileDescriptor,
            cancellationSignal: CancellationSignal?,
            callback: WriteResultCallback
        ) {

            try {

                FileInputStream(
                    archivo
                ).use { input ->

                    FileOutputStream(
                        destination.fileDescriptor
                    ).use { output ->

                        input.copyTo(output)
                    }
                }


                callback.onWriteFinished(
                    arrayOf(
                        PageRange.ALL_PAGES
                    )
                )

            } catch (e: Exception) {

                callback.onWriteFailed(
                    e.message
                )
            }
        }
    }

    private fun regresarMain7(){
        btn_regresoHorarios = findViewById(R.id.btn_regresoHorarios)
        btn_regresoHorarios.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}