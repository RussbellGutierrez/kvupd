package com.upd.kvupd.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.data.model.RowBaja
import com.upd.kvupd.databinding.RowBajaSupervisorBinding
import com.upd.kvupd.utils.OldBaseViewHolder
import com.upd.kvupd.utils.OldInterface.bajaSuperListener
import javax.inject.Inject

class BajaSupervisorAdapter @Inject constructor() : RecyclerView.Adapter<OldBaseViewHolder<*>>() {

    private val diffCallback = (object : DiffUtil.ItemCallback<RowBaja>() {

        override fun areItemsTheSame(oldItem: RowBaja, newItem: RowBaja): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RowBaja, newItem: RowBaja): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: RowBaja, newItem: RowBaja): Any? {
            val diff = Bundle()
            if (oldItem != newItem) {
                diff.putInt("id", newItem.id)
                diff.putString("nombre", newItem.nombre)
                diff.putString("direccion", newItem.direccion)
                diff.putString("fecha", newItem.fecha)
                diff.putString("dia", newItem.dia)
                diff.putString("motivo", newItem.motivo)
                diff.putString("negocio", newItem.negocio)
                diff.putInt("procede", newItem.procede)
            }
            if (diff.size() == 0) {
                return null
            }
            return diff
        }
    })

    var mDiffer: AsyncListDiffer<RowBaja> = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OldBaseViewHolder<*> {
        val bind = RowBajaSupervisorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        private val bind: RowBajaSupervisorBinding
    ) : OldBaseViewHolder<RowBaja>(bind.root) {

        override fun bind(item: RowBaja) {

            val cliente = "${item.id} - ${item.nombre}"

            bind.txtCliente.text = cliente
            bind.txtFecha.text = item.fecha
            bind.txtDia.text = item.dia
            bind.txtDireccion.text = item.direccion
            bind.txtMotivo.text = item.motivo
            bind.txtNegocio.text = item.negocio
            bind.lnrBaja.setOnClickListener { bajaSuperListener.onClickItem(item) }
        }
    }

    interface OnBajaSuperListener {
        fun onClickItem(baja: RowBaja)
    }
}