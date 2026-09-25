package com.carbajo.checking.pdf

import android.content.ContentValues
import android.content.Context

import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.text.DateFormatSymbols
import java.util.Locale

object PdfGenerator {

    private fun nombreMes(fechaId: String, locale: Locale = Locale("es", "PE")): String {
        // fechaId = "yyyy-MM"
        val mes = fechaId.split("-").getOrNull(1)?.toIntOrNull() ?: 1
        val nombre = DateFormatSymbols(locale).months[mes - 1]
        return nombre.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    }

    //ingresado recien para modificar los nombres de los meses

    private fun mesDe(yyyyMM: String, locale: Locale = Locale("es", "PE")): String {
        return try {
            val sdf = android.icu.text.SimpleDateFormat("yyyy-MM", locale)
            val cal = android.icu.util.Calendar.getInstance(locale).apply {
                time = sdf.parse(yyyyMM)!!
            }
            val nombre = java.text.DateFormatSymbols(locale).months[cal.get(android.icu.util.Calendar.MONTH)]
            nombre.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
        } catch (_: Exception) { yyyyMM }
    }

    private fun mesAnteriorDe(yyyyMM: String, locale: Locale = Locale("es", "PE")): String {
        return try {
            val sdf = android.icu.text.SimpleDateFormat("yyyy-MM", locale)
            val cal = android.icu.util.Calendar.getInstance(locale).apply {
                time = sdf.parse(yyyyMM)!!
                add(android.icu.util.Calendar.MONTH, -1)
            }
            val nombre = java.text.DateFormatSymbols(locale).months[cal.get(android.icu.util.Calendar.MONTH)]
            nombre.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
        } catch (_: Exception) { yyyyMM }
    }




    /**
     * Genera un PDF detallado con los totales del mes y lo guarda en
     * Downloads/Balances. Devuelve el Uri si se creó bien.
     */
    fun crearPdfBalanceDetallado(
        context: Context,
        fechaId: String,                      // "yyyy-MM"
        ingresos: com.carbajo.checking.modelos.IngresosModelo?,
        gastos: com.carbajo.checking.modelos.GastoMensualModelo?,
        egresos: com.carbajo.checking.modelos.EgresosModelo?
    ): Uri? {

        // ---- Config A4 (595x842 pt ~ 72dpi) ----
        val pdf = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdf.startPage(pageInfo)
        val canvas = page.canvas

        // ---- Estilos compactos (caben en 1 hoja) ----
        val MARGIN_H = 36f
        val START_Y  = 40f
        val ROW_H    = 16f            // altura de fila compacta

        val TITLE_SIZE   = 15f
        val SECTION_SIZE = 12f
        val BODY_SIZE    = 10f
        val SUMMARY_SIZE = 12.5f

        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.BLACK
            textAlign = Paint.Align.LEFT
            textSize = BODY_SIZE
        }
        val bold   = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        val normal = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        val mono   = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)

        fun line(y: Float) {
            val lp = Paint().apply { strokeWidth = 1.4f }
            canvas.drawLine(MARGIN_H - 2f, y, pageInfo.pageWidth - (MARGIN_H - 2f), y, lp)
        }
        fun money(d: Double?) = String.format(Locale.getDefault(), "%.2f", d ?: 0.0)
        fun nombreMes(yyyyMM: String): String {
            return try {
                val loc = Locale("es", "ES")
                val sdf = SimpleDateFormat("yyyy-MM", loc)
                val date = sdf.parse(yyyyMM) ?: return yyyyMM
                val cal = Calendar.getInstance(loc).apply { time = date }
                val month = DateFormatSymbols(loc).months[cal.get(Calendar.MONTH)]
                month.replaceFirstChar { if (it.isLowerCase()) it.titlecase(loc) else it.toString() }
            } catch (_: Exception) { yyyyMM }
        }

        val LABEL_X = MARGIN_H + 8f
        val VALUE_X = pageInfo.pageWidth - MARGIN_H - 8f

        fun row(label: String, value: String, y: Float): Float {
            p.typeface = mono
            p.textSize = BODY_SIZE

            // Label a la izquierda
            p.textAlign = Paint.Align.LEFT
            canvas.drawText(label, LABEL_X, y, p)

            // Valor a la derecha
            p.textAlign = Paint.Align.RIGHT
            canvas.drawText(value, VALUE_X, y, p)

            // ✅ IMPORTANTE: dejar el paint en LEFT para lo que sigue
            p.textAlign = Paint.Align.LEFT

            return y + ROW_H
        }



        // ----- Encabezado (pegado a la derecha) -----
        var y = 50f
        p.apply { typeface = bold; textSize = 18f; textAlign = Paint.Align.RIGHT }
        canvas.drawText("${nombreMes(fechaId)}  ($fechaId)", pageInfo.pageWidth - 40f, y, p)

        // restaurar alineación y tamaños por defecto para el resto
        p.textAlign = Paint.Align.LEFT
        p.textSize = 12f

        line(y + 10f)
        y += 40f

        // ---- INGRESOS ----
        p.typeface = bold; p.textSize = SECTION_SIZE
        canvas.drawText("INGRESOS", MARGIN_H, y, p)
        y += ROW_H * 0.75f
        line(y); y += ROW_H

        val saldoAnterior = ingresos?.saldoMesAnterior ?: 0.0
        val balanceMes    = ingresos?.balanceMesActual ?: 0.0
        val totalIngresos = saldoAnterior + balanceMes

//        y = row("Saldo del mes anterior", money(saldoAnterior), y)
//        y = row("Balance del mes",        money(balanceMes),    y)

        y = row("Saldo del mes de ${mesAnteriorDe(fechaId)}", money(saldoAnterior), y)
        y = row("Balance del mes de ${mesDe(fechaId)}",       money(balanceMes),    y)

        line(y + 4f); y += ROW_H
        p.typeface = bold; p.textSize = BODY_SIZE
        y = row("TOTAL INGRESOS", money(totalIngresos), y)
        y += ROW_H * 0.5f

        // ---- EGRESOS (Sueldos y Servicios) ----
        p.typeface = bold; p.textSize = SECTION_SIZE
        canvas.drawText("EGRESOS", MARGIN_H, y, p)
        y += ROW_H * 0.75f
        line(y); y += ROW_H

        val sueldos   = egresos?.sueldos ?: 0.0
        val impuestos = egresos?.impuestos ?: 0.0
        val gas       = egresos?.gas ?: 0.0
        val agua      = egresos?.agua ?: 0.0
        val luz       = egresos?.luz ?: 0.0

        y = row("Sueldos (Hilda + 2 ayudantes)", money(sueldos), y)
        y = row("Impuestos",                      money(impuestos), y)
        y = row("Gas",                            money(gas), y)
        y = row("Agua",                           money(agua), y)
        y = row("Luz",                            money(luz), y)

        val totalSueldosServicios = sueldos + impuestos + gas + agua + luz

        line(y + 4f); y += ROW_H
        p.typeface = bold; p.textSize = BODY_SIZE
        y = row("TOTAL SUELDOS Y SUMINISTROS", money(totalSueldosServicios), y)
        y += ROW_H * 0.5f

        // ---- GASTOS MENSUALES ----
        p.typeface = bold; p.textSize = SECTION_SIZE
        canvas.drawText("GASTOS MENSUALES", MARGIN_H, y, p)
        y += ROW_H * 0.75f
        line(y); y += ROW_H

        fun g(v: Double?) = money(v)
        y = row("Verduras",               g(gastos?.verduras), y)
        y = row("Taxis camal",            g(gastos?.taxiCamal), y)
        y = row("Corte de carne",         g(gastos?.carne), y)
        y = row("Afiladores / Gasfitero", g(gastos?.afiladores), y)
        y = row("Panes",                  g(gastos?.panes), y)
        y = row("Menudencias",            g(gastos?.menudencias), y)
        y = row("Pescado",                g(gastos?.pescado), y)
        y = row("Carne molida",           g(gastos?.carneMolida), y)
        y = row("Pollos",                 g(gastos?.pollos), y)
        y = row("Abarrotes",              g(gastos?.abarrotes), y)
        y = row("Recojo de víveres",      g(gastos?.recojoViveres), y)
        y = row("Huevos",                 g(gastos?.huevos), y)
        y = row("Pasajes",                g(gastos?.pasajes), y)
        y = row("Artículos de limpieza",  g(gastos?.articulosLimpieza), y)
        y = row("Mantenimiento",          g(gastos?.mantenimiento), y)
        y = row("Gastos extras",          g(gastos?.gastosExtras), y)

        val subtotalGastos = gastos?.subtotal ?: 0.0
        line(y + 4f); y += ROW_H
        p.typeface = bold; p.textSize = BODY_SIZE
        y = row("TOTAL GASTOS", money(subtotalGastos), y)
        y += ROW_H * 0.75f

        // ---- TOTAL EGRESOS ----
        val totalEgresos = totalSueldosServicios + subtotalGastos
//        p.typeface = bold; p.textSize = SECTION_SIZE
//        canvas.drawText("TOTAL DE EGRESOS", MARGIN_H, y, p)
//        y += ROW_H * 0.75f
//        line(y); y += ROW_H
//        p.typeface = bold; p.textSize = BODY_SIZE
//        y = row("TOTAL EGRESOS (Sueldos+Servicios + Gastos)", money(totalEgresos), y)
//        y += ROW_H * 0.75f

        // ---- RESUMEN GENERAL (cuadro final) ----
        val boxTop = y + 12f
        val boxLeft = MARGIN_H
        val boxRight = pageInfo.pageWidth - MARGIN_H
        val boxPadding = 8f

        // borde del cuadro
        val border = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 1.6f
            color = android.graphics.Color.BLACK
        }

        p.typeface = bold; p.textSize = SUMMARY_SIZE
        canvas.drawText("RESUMEN GENERAL", boxLeft + boxPadding, y, p)
        y += ROW_H

        p.typeface = normal; p.textSize = BODY_SIZE
        y = row("TOTAL INGRESOS",            money(totalIngresos), y)
//        y = row("Total Sueldos y Suministros", money(totalSueldosServicios), y)
//        y = row("Total Gastos",           money(subtotalGastos), y)
        p.typeface = bold; p.textSize = BODY_SIZE
        y = row("TOTAL EGRESOS (Sueldos y Suministros + Gastos)",  money(totalEgresos), y)


        //PARA PODER VER LA FECHA ACTUAL EN EL CUADO DE RESUMEN
        val utilidad = totalIngresos - totalEgresos
        p.typeface = bold; p.textSize = SUMMARY_SIZE
        y = row("Total saldo del mes de  ${mesDe(fechaId)}", money(utilidad), y)



        // dibujar el rectángulo alrededor del resumen
        val boxBottom = y + boxPadding
        canvas.drawRect(boxLeft, boxTop - boxPadding, boxRight, boxBottom, border)

        pdf.finishPage(page)

        // ---- Guardar en Descargas/Balances ----
        val fileName = "BalanceDetallado_$fechaId.pdf"

        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.TITLE, fileName.removeSuffix(".pdf"))
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Downloads.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + "/Balances"
                )
            }
        }

        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        } else {
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            values.put(
                MediaStore.MediaColumns.DATA,
                java.io.File(dir, fileName).absolutePath
            )
            resolver.insert(MediaStore.Files.getContentUri("external"), values)
        }

        uri?.let { out -> resolver.openOutputStream(out)?.use { os -> pdf.writeTo(os) } }
        pdf.close()
        return uri
    }

}