package com.upd.kvupd.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.data.model.TIncidencia
import com.upd.kvupd.databinding.RowIncidenciaBinding
import com.upd.kvupd.utils.OldBaseViewHolder
import javax.inject.Inject

class IncidenciaAdapter @Inject constructor() : RecyclerView.Adapter<OldBaseViewHolder<*>>() {

    private val diffCallback = (object : DiffUtil.ItemCallback<TIncidencia>() {

        override fun areItemsTheSame(oldItem: TIncidencia, newItem: TIncidencia): Boolean {
            return (oldItem.tipo == newItem.tipo && oldItem.fecha == newItem.fecha)
        }

        override fun areContentsTheSame(oldItem: TIncidencia, newItem: TIncidencia): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: TIncidencia, newItem: TIncidencia): Any? {
            val diff = Bundle()
            if (oldItem != newItem) {
                diff.putString("tipo", newItem.tipo)
                diff.putInt("usuario", newItem.usuario)
                diff.putString("observacion", newItem.observacion)
                diff.putString("fecha", newItem.fecha)
            }
            if (diff.size() == 0) {
                return null
            }
            return diff
        }
    })

    var mDiffer: AsyncListDiffer<TIncidencia> = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OldBaseViewHolder<*> {
        val bind = RowIncidenciaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        private val bind: RowIncidenciaBinding
    ) : OldBaseViewHolder<TIncidencia>(bind.root) {

        override fun bind(item: TIncidencia) {
            val tipo = when(item.tipo) {
                "GPS" -> "Ubicacion"
                "TIME" -> "Fecha y hora"
                "INTERNET" -> "Conexion"
                "APP" -> "Aplicacion"
                else -> ""
            }
            bind.txtTipo.text = tipo
            bind.txtFecha.text = item.fecha
            bind.txtObs.text = item.observacion
        }
    }

}