package com.carbajo.checking.pdf

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale

object PdfGeneratorHorarios {

    private val locale = Locale("es", "PE")


    // =====================================================
    // NOMBRE DEL MES
    // =====================================================

    private fun nombreMes(mes: Int): String {

        return DateFormatSymbols(locale)
            .months[mes - 1]
            .uppercase(locale)
    }


    // =====================================================
    // NOMBRE DEL ARCHIVO
    // =====================================================

    private fun nombreArchivo(
        mes: Int,
        responsable: String
    ): String {

        val mesTexto = nombreMes(mes)

        val nombreLimpio = responsable
            .trim()
            .replace(" ", "_")
            .replace(Regex("[^A-Za-zÁÉÍÓÚáéíóúÑñ0-9_]"), "")

        return "${mesTexto}_${nombreLimpio}.pdf"
    }


    // =====================================================
    // CREAR PDF PARA DESCARGAR
    // =====================================================

    fun crearPdfHorarios(
        context: Context,
        anio: Int,
        mes: Int,
        responsable: String
    ): Uri? {

        val pdf = construirPdf(
            anio = anio,
            mes = mes,
            responsable = responsable
        )

        val fileName =
            nombreArchivo(mes, responsable)

        val resolver =
            context.contentResolver


        val values = ContentValues().apply {

            put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                fileName
            )

            put(
                MediaStore.MediaColumns.TITLE,
                fileName.removeSuffix(".pdf")
            )

            put(
                MediaStore.MediaColumns.MIME_TYPE,
                "application/pdf"
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                put(
                    MediaStore.Downloads.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS +
                            "/Horarios"
                )
            }
        }


        val uri: Uri? = if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        ) {

            resolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                values
            )

        } else {

            val directorio =
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                )

            val carpeta =
                File(directorio, "Horarios")

            if (!carpeta.exists()) {
                carpeta.mkdirs()
            }

            val archivo =
                File(carpeta, fileName)

            values.put(
                MediaStore.MediaColumns.DATA,
                archivo.absolutePath
            )

            resolver.insert(
                MediaStore.Files.getContentUri("external"),
                values
            )
        }


        try {

            uri?.let {

                resolver
                    .openOutputStream(it)
                    ?.use { output ->

                        pdf.writeTo(output)
                    }
            }

        } catch (e: Exception) {

            pdf.close()
            return null
        }


        pdf.close()

        return uri
    }


    // =====================================================
    // PDF TEMPORAL PARA IMPRIMIR
    // =====================================================

    fun crearPdfTemporal(
        context: Context,
        anio: Int,
        mes: Int,
        responsable: String
    ): File? {

        return try {

            val pdf = construirPdf(
                anio,
                mes,
                responsable
            )

            val archivo = File(
                context.cacheDir,
                nombreArchivo(mes, responsable)
            )

            FileOutputStream(archivo).use {
                pdf.writeTo(it)
            }

            pdf.close()

            archivo

        } catch (e: Exception) {

            null
        }
    }


    // =====================================================
    // CONSTRUIR PDF
    // =====================================================

    private fun construirPdf(
        anio: Int,
        mes: Int,
        responsable: String
    ): PdfDocument {

        val pdf = PdfDocument()

        /*
         * A4 HORIZONTAL
         * 842 x 595
         */
        val pageInfo =
            PdfDocument.PageInfo.Builder(
                842,
                595,
                1
            ).create()

        val page =
            pdf.startPage(pageInfo)

        val canvas =
            page.canvas


        // =================================================
        // ESTILOS
        // =================================================

        val paint = Paint(
            Paint.ANTI_ALIAS_FLAG
        ).apply {

            color = Color.BLACK
            textSize = 10f
            typeface =
                Typeface.create(
                    Typeface.SANS_SERIF,
                    Typeface.NORMAL
                )
        }


        val bold =
            Typeface.create(
                Typeface.SANS_SERIF,
                Typeface.BOLD
            )


        // =================================================
        // ENCABEZADO
        // =================================================

        paint.textSize = 10f
        paint.typeface = bold

        canvas.drawText(
            "EMPRESA",
            15f,
            22f,
            paint
        )

        canvas.drawText(
            "DIRECCION",
            15f,
            37f,
            paint
        )

        canvas.drawText(
            "RUC",
            15f,
            52f,
            paint
        )


        paint.typeface =
            Typeface.DEFAULT

        canvas.drawText(
            ":   COMEDOR \"BUEN PASTOR\"",
            88f,
            22f,
            paint
        )

        canvas.drawText(
            ":   JUSTO PASOR DAVILA N° 285",
            88f,
            37f,
            paint
        )

        canvas.drawText(
            ":",
            88f,
            52f,
            paint
        )


        // =================================================
        // TÍTULO
        // =================================================

        val mesTexto =
            nombreMes(mes)

        paint.typeface = bold
        paint.textSize = 18f
        paint.textAlign =
            Paint.Align.CENTER

        canvas.drawText(
            "CONTROL MENSUAL DE ASISTENCIA : $mesTexto",
            421f,
            82f,
            paint
        )

        paint.textAlign =
            Paint.Align.LEFT


        // =================================================
        // RESPONSABLE
        // =================================================

        paint.textSize = 10f
        paint.typeface = bold

        canvas.drawText(
            "Nombre y Apellido:",
            15f,
            105f,
            paint
        )

        paint.typeface =
            Typeface.DEFAULT

        canvas.drawText(
            responsable,
            125f,
            105f,
            paint
        )


        paint.typeface = bold

        canvas.drawText(
            "DNI:",
            690f,
            105f,
            paint
        )


        // =================================================
        // TABLA
        // =================================================

        val tableLeft = 12f
        val tableTop = 115f

        val rowHeight = 12.2f


        /*
         * Anchos similares al documento original.
         */
        val columnas = floatArrayOf(
            80f,   // FECHA
            82f,   // INGRESO
            105f,  // INICIO ALMUERZO
            120f,  // TERMINO ALMUERZO
            65f,   // SALIDA
            72f,   // FIRMA
            100f,  // INICIO EXTRA
            110f,  // TERMINO EXTRA
            82f    // FIRMA
        )


        val encabezados = arrayOf(
            "FECHA",
            "H . INGRESO",
            "INICIO ALMUERZO",
            "TERMINO ALMUERZO",
            "SALIDA",
            "FIRMA",
            "INICIO H. EXTRA",
            "TERMINO H. EXTRA",
            "FIRMA"
        )


        // 1 encabezado + 31 filas
        val numeroFilas = 32

        val tableBottom =
            tableTop +
                    (numeroFilas * rowHeight)


        // =================================================
        // LÍNEAS VERTICALES
        // =================================================

        val linePaint =
            Paint().apply {

                color = Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = 0.8f
            }


        var x = tableLeft

        canvas.drawLine(
            x,
            tableTop,
            x,
            tableBottom,
            linePaint
        )


        columnas.forEach { ancho ->

            x += ancho

            canvas.drawLine(
                x,
                tableTop,
                x,
                tableBottom,
                linePaint
            )
        }


        // =================================================
        // LÍNEAS HORIZONTALES
        // =================================================

        for (fila in 0..numeroFilas) {

            val y =
                tableTop +
                        (fila * rowHeight)

            canvas.drawLine(
                tableLeft,
                y,
                x,
                y,
                linePaint
            )
        }


        // =================================================
        // ENCABEZADOS TABLA
        // =================================================

        paint.typeface = bold
        paint.textSize = 7.8f
        paint.textAlign =
            Paint.Align.CENTER


        var columnaX = tableLeft

        encabezados.forEachIndexed { index, texto ->

            val ancho =
                columnas[index]

            canvas.drawText(
                texto,
                columnaX + ancho / 2,
                tableTop + 8.5f,
                paint
            )

            columnaX += ancho
        }


        paint.textAlign =
            Paint.Align.LEFT


        // =================================================
        // DÍAS DEL MES
        // =================================================

        val calendario =
            Calendar.getInstance().apply {

                set(
                    anio,
                    mes - 1,
                    1
                )
            }


        val diasDelMes =
            calendario.getActualMaximum(
                Calendar.DAY_OF_MONTH
            )


        paint.typeface =
            Typeface.DEFAULT

        paint.textSize = 8.5f


        /*
         * Conservamos las 31 filas del documento original.
         *
         * Si febrero tiene 28 días, las filas restantes
         * quedan vacías.
         */
        for (dia in 1..31) {

            val textoFecha =

                if (dia <= diasDelMes) {

                    String.format(
                        locale,
                        "%02d/%02d/%04d",
                        dia,
                        mes,
                        anio
                    )

                } else {

                    ""
                }


            val y =
                tableTop +
                        rowHeight * dia +
                        8.8f


            canvas.drawText(
                textoFecha,
                tableLeft + 7f,
                y,
                paint
            )
        }


        // =================================================
        // NOTAS
        // =================================================

        val notasY =
            tableBottom + 13f

        paint.typeface = bold
        paint.textSize = 8f


        canvas.drawText(
            "NOTA 1 : EL CONTROLDE ASISTENCIA NO DEBE TENER BORRONES NI ENMENDADURAS",
            95f,
            notasY,
            paint
        )

        canvas.drawText(
            "NOTA 2 : SE DEBE ANOTAR LA FECHA DEL MES, EL DIA, LAS HORAS Y MINUTOS",
            95f,
            notasY + 12f,
            paint
        )

        canvas.drawText(
            "NOTA 3 : SE DEBE ANOTAR EL INICIO Y EL TERMINO DEL HORA DE ALMUERZO",
            95f,
            notasY + 24f,
            paint
        )

        canvas.drawText(
            "NOTA 4 : LAS HORAS DE INGRESO Y SALIDA NO PUEDEN SER IGUALES TODOS LOS DIAS",
            95f,
            notasY + 36f,
            paint
        )

        canvas.drawText(
            "NOTA 5 : REGISTRAR LAS HORAS EXTRAS",
            95f,
            notasY + 48f,
            paint
        )


        pdf.finishPage(page)

        return pdf
    }
}