package com.carbajo.checking.adaptador

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.carbajo.checking.R
import com.carbajo.checking.activity.VistaPreviaActivity
import com.carbajo.checking.activity.HorariosActivity
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
        val btnDescarga: Button = v.findViewById(R.id.btn_descarga)
        val btn_compartir: Button = v.findViewById(R.id.btn_compartir)
        val btn_vistaPrevia: Button = v.findViewById(R.id.btn_vistaPrevia)
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
        holder.btn_vistaPrevia.setOnClickListener {

            val context = holder.itemView.context

            val intent = Intent(
                context,
                VistaPreviaActivity::class.java
            )

            intent.putExtra(
                "fechaId",
                item.fechaId
            )

            context.startActivity(intent)
        }
    }

    override fun getItemCount() = items.size

    fun submit(newItems: List<BalanceModelo>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun nombreMes(fechaId: String): String {

        val locale =
            Locale("es", "PE")

        return try {

            val formato =
                SimpleDateFormat(
                    "dd-MM-yyyy",
                    locale
                )

            formato.isLenient = false

            val fecha =
                formato.parse(fechaId)
                    ?: return fechaId

            val calendario =
                Calendar.getInstance(locale).apply {
                    time = fecha
                }

            val indiceMes =
                calendario.get(
                    Calendar.MONTH
                )

            val nombre =
                DateFormatSymbols(locale)
                    .months[indiceMes]

            nombre.replaceFirstChar {

                if (it.isLowerCase()) {
                    it.titlecase(locale)
                } else {
                    it.toString()
                }
            }

        } catch (e: Exception) {

            fechaId
        }
    }




}