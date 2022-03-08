package com.upd.kventas.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upd.kventas.R
import com.upd.kventas.data.model.Generico
import com.upd.kventas.data.model.TAlta
import com.upd.kventas.databinding.RowAltaBinding
import com.upd.kventas.databinding.RowReporteBinding
import com.upd.kventas.utils.BaseViewHolder
import com.upd.kventas.utils.Constant.CONF
import com.upd.kventas.utils.Interface.altaListener
import com.upd.kventas.utils.Interface.generListener
import com.upd.kventas.utils.percent
import com.upd.kventas.utils.setUI
import javax.inject.Inject

class AltaAdapter @Inject constructor() : RecyclerView.Adapter<BaseViewHolder<*>>() {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val bind = RowAltaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bind)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is ViewHolder -> holder.bind(mDiffer.currentList[position])
        }
    }

    override fun getItemCount() = mDiffer.currentList.size

    override fun getItemViewType(position: Int) = position

    private inner class ViewHolder(
        private val bind: RowAltaBinding
    ) : BaseViewHolder<TAlta>(bind.root) {

        override fun bind(item: TAlta) {
            val texto = "Alta cliente - ${item.idaux}"
            bind.txtAlta.text = texto
            bind.txtFecha.text = item.fecha
            if (item.datos > 0) {
                ImageViewCompat.setImageTintList(bind.imgDatos, ColorStateList.valueOf(Color.parseColor("#1E90FF")))
            }
            bind.lnrAlta.setOnClickListener { altaListener.onItemClick(item) }
        }
    }

    interface OnAltaListener {
        fun onItemClick(alta: TAlta)
    }
}