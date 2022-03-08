package com.upd.kventas.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upd.kventas.data.model.BajaVendedor
import com.upd.kventas.databinding.RowBajaVendedorBinding
import com.upd.kventas.domain.Functions
import com.upd.kventas.utils.BaseViewHolder
import com.upd.kventas.utils.daysBetween
import javax.inject.Inject

class BajaVendedorAdapter @Inject constructor(
    private val functions: Functions
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    private val diffCallback = (object : DiffUtil.ItemCallback<BajaVendedor>() {

        override fun areItemsTheSame(oldItem: BajaVendedor, newItem: BajaVendedor): Boolean {
            return oldItem.cliente == newItem.cliente
        }

        override fun areContentsTheSame(oldItem: BajaVendedor, newItem: BajaVendedor): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: BajaVendedor, newItem: BajaVendedor): Any? {
            val diff = Bundle()
            if (oldItem != newItem) {
                diff.putInt("sucursal", newItem.sucursal)
                diff.putInt("empleado", newItem.empleado)
                diff.putString("fecha", newItem.fecha)
                diff.putInt("motivo", newItem.motivo)
                diff.putString("descripcion", newItem.descripcion)
                diff.putString("estado", newItem.estado)
                diff.putString("confirmado", newItem.confirmado)
                diff.putInt("cliente", newItem.cliente)
                diff.putString("nombre", newItem.nombre)
            }
            if (diff.size() == 0) {
                return null
            }
            return diff
        }
    })

    var mDiffer: AsyncListDiffer<BajaVendedor> = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val bind = RowBajaVendedorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        private val bind: RowBajaVendedorBinding
    ) : BaseViewHolder<BajaVendedor>(bind.root) {

        override fun bind(item: BajaVendedor) {

            val cliente = "${item.cliente} - ${item.nombre}"
            val hoy = functions.dateToday(5)
            val dias = "Dias ${item.fecha.daysBetween(hoy)}"

            bind.txtFecha.text = item.fecha
            bind.txtCliente.text = cliente
            bind.txtMotivo.text = item.descripcion
            bind.txtDias.text = dias
        }
    }
}