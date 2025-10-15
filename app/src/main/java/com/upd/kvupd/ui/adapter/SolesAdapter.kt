package com.upd.kvupd.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.R
import com.upd.kvupd.data.model.Soles
import com.upd.kvupd.databinding.RowReporteBinding
import com.upd.kvupd.utils.OldBaseViewHolder
import com.upd.kvupd.utils.OldInterface.solesListener
import com.upd.kvupd.utils.percent
import javax.inject.Inject

class SolesAdapter @Inject constructor() : RecyclerView.Adapter<OldBaseViewHolder<*>>() {

    private val diffCallback = (object : DiffUtil.ItemCallback<Soles>() {

        override fun areItemsTheSame(oldItem: Soles, newItem: Soles): Boolean {
            return oldItem.linea.codigo == newItem.linea.codigo
        }

        override fun areContentsTheSame(oldItem: Soles, newItem: Soles): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: Soles, newItem: Soles): Any? {
            val diff = Bundle()
            if (oldItem != newItem) {
                diff.putInt("linea_codigo", newItem.linea.codigo)
                diff.putString("linea_descripcion", newItem.linea.descripcion)
                diff.putDouble("cuota", newItem.cuota)
                diff.putDouble("avance", newItem.avance)
            }
            if (diff.size() == 0) {
                return null
            }
            return diff
        }
    })

    var mDiffer: AsyncListDiffer<Soles> = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OldBaseViewHolder<*> {
        val bind = RowReporteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bind)
    }

    override fun onBindViewHolder(holder: OldBaseViewHolder<*>, position: Int) {
        when (holder) {
            is ViewHolder -> holder.bind(mDiffer.currentList[position])
        }
    }

    override fun getItemCount() = mDiffer.currentList.size

    override fun getItemViewType(position: Int) = position

    private inner class ViewHolder(
        private val bind: RowReporteBinding
    ) : OldBaseViewHolder<Soles>(bind.root) {

        override fun bind(item: Soles) {
            val titulo = item.linea.descripcion
            val cuota = "Cuota: ${item.cuota}"
            val avance = "Avance: ${item.avance}"
            val percent = percent(item.avance,item.cuota)
            val porcentaje = "$percent%"

            /*if (CONF.tipo == "V") {
                bind.imgCerrar.setUI("v", false)
            }*/

            when{
                percent.toDouble() > 85 -> bind.imgFlecha.setImageResource(R.drawable.f_arriba)
                percent.toDouble() in 70.0..85.0 -> {
                    ImageViewCompat.setImageTintList(bind.imgFlecha, ColorStateList.valueOf(Color.parseColor("#FFAB00")))
                    bind.imgFlecha.setImageResource(R.drawable.f_arriba)
                }
                percent.toDouble() in 1.0..69.99 -> bind.imgFlecha.setImageResource(R.drawable.f_bajo)
                percent.toDouble() < 1 -> bind.imgFlecha.setImageResource(R.drawable.f_neutral)
            }

            bind.txtTitulo.text = titulo
            bind.txtCuota.text = cuota
            bind.txtAvance.text = avance
            bind.txtPorcentaje.text = porcentaje
            bind.imgCerrar.setOnClickListener { solesListener.onCloseItem(item) }
            bind.cardReporte.setOnClickListener { solesListener.onItemClick(item) }
            bind.cardReporte.setOnLongClickListener {
                solesListener.onItemPress(item)
                return@setOnLongClickListener true
            }
        }
    }

    interface OnSolesListener {
        fun onItemClick(soles: Soles)
        fun onItemPress(soles: Soles)
        fun onCloseItem(soles: Soles)
    }
}