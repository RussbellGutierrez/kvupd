package com.upd.kv.ui.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upd.kv.R
import com.upd.kv.data.model.RowCliente
import com.upd.kv.databinding.RowClienteBinding
import com.upd.kv.domain.Functions
import com.upd.kv.ui.fragment.FCliente
import com.upd.kv.utils.BaseViewHolder
import com.upd.kv.utils.Constant.CONF
import com.upd.kv.utils.Interface.clienteListener
import com.upd.kv.utils.setUI
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ClienteAdapter @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val functions: Functions
) : RecyclerView.Adapter<BaseViewHolder<*>>() {

    private val diffCallback = (object : DiffUtil.ItemCallback<RowCliente>() {

        override fun areItemsTheSame(oldItem: RowCliente, newItem: RowCliente): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RowCliente, newItem: RowCliente): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: RowCliente, newItem: RowCliente): Any? {
            val diff = Bundle()
            if (oldItem != newItem) {
                diff.putInt("idcliente", newItem.id)
                diff.putString("nomcli", newItem.nombre)
                diff.putInt("empleado", newItem.vendedor)
                diff.putString("descripcion", newItem.nomven)
                diff.putInt("secuencia", newItem.secuencia)
                diff.putInt("ruta", newItem.ruta)
                diff.putInt("atendido", newItem.atendido)
                diff.putString("fecha", newItem.fecha)
                diff.putString("encuestas", newItem.encuestas)
            }
            if (diff.size() == 0) {
                return null
            }
            return diff
        }
    })

    var mDiffer: AsyncListDiffer<RowCliente> = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val bind = RowClienteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        private val bind: RowClienteBinding
    ) : BaseViewHolder<RowCliente>(bind.root) {

        override fun bind(item: RowCliente) {
            val fecha = functions.dateToday(5)
            val cliente = "${item.id} - ${item.nombre}"
            val secuencia = "Sec ${item.secuencia}"
            val ruta = "Ruta ${item.ruta}"

            if (CONF.tipo == "V") {
                bind.txtVendedor.setUI("v",false)
                bind.dvdVendedor.setUI("v",false)
                if (fecha != item.fecha) {
                    bind.txtFecha.setTextColor(ctx.getColor(R.color.gold))
                }else {
                    bind.txtFecha.setTextColor(ctx.getColor(R.color.dodgerblue))
                }
            }
            bind.txtVendedor.text = item.nomven
            bind.txtCliente.text = cliente
            bind.txtFecha.text = item.fecha
            bind.txtSecuencia.text = secuencia
            bind.txtRuta.text = ruta

            when(item.atendido) {
                1 -> bind.imgVisita.setColorFilter(R.color.lightgreen)
                2 -> bind.imgAnulado.setColorFilter(R.color.lightcrimson)
            }
            bind.lnrCliente.setOnClickListener {
                clienteListener.onClienteClick(item)
            }
        }
    }

    interface OnClienteListener {
        fun onClienteClick(cliente: RowCliente)
    }
}