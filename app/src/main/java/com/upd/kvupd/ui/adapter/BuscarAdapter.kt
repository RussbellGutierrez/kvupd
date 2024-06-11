package com.upd.kvupd.ui.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.R
import com.upd.kvupd.data.model.DataCliente
import com.upd.kvupd.databinding.RowBuscarBinding
import com.upd.kvupd.utils.BaseViewHolder
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Interface.buscarListener
import javax.inject.Inject

class BuscarAdapter @Inject constructor() : RecyclerView.Adapter<BaseViewHolder<*>>() {

    private val diffCallback = (object : DiffUtil.ItemCallback<DataCliente>() {

        override fun areItemsTheSame(oldItem: DataCliente, newItem: DataCliente): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataCliente, newItem: DataCliente): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: DataCliente, newItem: DataCliente): Any? {
            val diff = Bundle()
            if (oldItem != newItem) {
                diff.putInt("idcliente", newItem.id)
                diff.putString("nomcli", newItem.nombre)
                diff.putString("domicli", newItem.domicilio)
                diff.putInt("ruta", newItem.ruta)
                diff.putString("negocio", newItem.negocio)
                diff.putString("telefono", newItem.telefono)
                diff.putInt("observacion", newItem.observacion)
            }
            if (diff.size() == 0) {
                return null
            }
            return diff
        }
    })

    var mDiffer: AsyncListDiffer<DataCliente> = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val bind = RowBuscarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        private val bind: RowBuscarBinding
    ) : BaseViewHolder<DataCliente>(bind.root) {

        @SuppressLint("UseCompatLoadingForDrawables")
        override fun bind(item: DataCliente) {

            val cliente = "${item.id} - ${item.nombre}"

            when (CONF.esquema) {
                1 -> bind.imgEmp.setImageResource(R.drawable.terranorte)
                7 -> bind.imgEmp.setImageResource(R.drawable.oriunda)
            }

            bind.txtCliente.text = cliente

            bind.lnrBuscar.setOnClickListener {
                buscarListener.onClienteClick(item)
            }
        }
    }

    interface OnBuscarListener {
        fun onClienteClick(cliente: DataCliente)
    }
}