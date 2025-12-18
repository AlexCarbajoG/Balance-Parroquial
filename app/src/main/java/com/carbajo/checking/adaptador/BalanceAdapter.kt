package com.carbajo.checking.adaptador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carbajo.checking.R
import com.carbajo.checking.modelos.BalanceModelo
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class BalanceAdapter(
    private var items: List<BalanceModelo>,
    private val onDescargarClick: (BalanceModelo) -> Unit = {},
    private val onCompartirClick: (BalanceModelo) -> Unit = {}
) : RecyclerView.Adapter<BalanceAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        val txtNombreMes: TextView = v.findViewById(R.id.txt_nombreMes)
        val txtFechaMes: TextView  = v.findViewById(R.id.txt_fechaMes)
        val btnDescarga: ImageButton = v.findViewById(R.id.btn_descarga)
        val btn_compartir: ImageButton = v.findViewById(R.id.btn_compartir)
        // si luego usas el segundo ImageButton, agrégalo aquí
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_balance, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.txtNombreMes.text = nombreMes(item.fechaId) // "septiembre"
        holder.txtFechaMes.text  = item.fechaId            // "2025-09"

        holder.btnDescarga.setOnClickListener { onDescargarClick(item) }
        holder.btn_compartir.setOnClickListener { onCompartirClick(item) }
    }

    override fun getItemCount() = items.size

    fun submit(newItems: List<BalanceModelo>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun nombreMes(yyyyMM: String): String {
        // yyyy-MM -> "septiembre" en español
        // Seguro para API < 26
        val locale = Locale("es", "ES")
        val sdf = SimpleDateFormat("yyyy-MM", locale)
        val date = sdf.parse(yyyyMM) ?: return yyyyMM
        val cal = Calendar.getInstance(locale).apply { time = date }
        val monthIndex = cal.get(Calendar.MONTH) // 0..11
        val monthName = DateFormatSymbols(locale).months[monthIndex]
        // capitaliza primera letra si quieres:
        return monthName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    }



}