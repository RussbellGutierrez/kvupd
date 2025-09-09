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
import com.upd.kvupd.data.model.TAlta
import com.upd.kvupd.databinding.RowAltaBinding
import com.upd.kvupd.utils.OldBaseViewHolder
import com.upd.kvupd.utils.OldInterface.altaListener
import javax.inject.Inject

class AltaAdapter @Inject constructor() : RecyclerView.Adapter<OldBaseViewHolder<*>>() {

    private val diffCallback = (object : DiffUtil.ItemCallback<TAlta>() {

        override fun areItemsTheSame(oldItem: TAlta, newItem: TAlta): Boolean {
            return oldItem.idaux == newItem.idaux
        }

        override fun areContentsTheSame(oldItem: TAlta, newItem: TAlta): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: TAlta, newItem: TAlta): Any? {
            val diff = Bundle()
            if (oldItem != newItem) {
                diff.putInt("idaux", newItem.idaux)
                diff.putString("fecha", newItem.fecha)
                diff.putInt("empleado", newItem.empleado)
                diff.putDouble("longitud", newItem.longitud)
                diff.putDouble("latitud", newItem.latitud)
                diff.putDouble("precision", newItem.precision)
                diff.putString("estado", newItem.estado)
                diff.putInt("datos", newItem.datos)
            }
            if (diff.size() == 0) {
                return null
            }
            return diff
        }
    })

    var mDiffer: AsyncListDiffer<TAlta> = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OldBaseViewHolder<*> {
        val bind = RowAltaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        private val bind: RowAltaBinding
    ) : OldBaseViewHolder<TAlta>(bind.root) {

        override fun bind(item: TAlta) {
            val texto = "Alta cliente - ${item.idaux}"
            bind.txtAlta.text = texto
            bind.txtFecha.text = item.fecha
            if (item.datos > 0) {
                ImageViewCompat.setImageTintList(
                    bind.imgDatos,
                    ColorStateList.valueOf(Color.parseColor("#1E90FF"))
                )
            } else {
                ImageViewCompat.setImageTintList(
                    bind.imgDatos,
                    ColorStateList.valueOf(Color.parseColor("#7A7A7A"))
                )
            }
            bind.lnrAlta.setOnClickListener { altaListener.onItemClick(item) }
        }
    }

    interface OnAltaListener {
        fun onItemClick(alta: TAlta)
    }
}