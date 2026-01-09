package com.upd.kvupd.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.databinding.FlowRowClienteBinding
import com.upd.kvupd.utils.setUI
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class ClienteAdapter @AssistedInject constructor(
    @Assisted private val listener: Listener,
    @Assisted private val hoy: String
) : ListAdapter<FlowCliente, ClienteAdapter.ViewHolder>(Diff) {

    interface Listener {
        fun onClick(cliente: FlowCliente)
        fun onLongClick(cliente: FlowCliente)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FlowRowClienteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), hoy, listener)
    }

    class ViewHolder(
        private val bind: FlowRowClienteBinding
    ) : RecyclerView.ViewHolder(bind.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: FlowCliente, hoy: String, listener: Listener) {
            bind.txtVendedor.text = item.nomemp
            bind.txtCliente.text = "${item.cliente} - ${item.nomcli}"
            bind.txtFecha.text = item.fecha

            val color = if (item.fecha == hoy)
                Color.parseColor("#000000")
            else
                Color.parseColor("#082EB3")

            val show = item.baja > 0

            bind.txtFecha.setTextColor(color)
            bind.txtBaja.setUI("v", show)

            bind.lnrCliente.setOnClickListener {
                listener.onClick(item)
            }
            bind.lnrCliente.setOnLongClickListener {
                listener.onClick(item)
                true
            }
        }
    }

    companion object {
        val Diff = object : DiffUtil.ItemCallback<FlowCliente>() {
            override fun areItemsTheSame(a: FlowCliente, b: FlowCliente): Boolean =
                a.cliente == b.cliente

            override fun areContentsTheSame(a: FlowCliente, b: FlowCliente): Boolean =
                a == b
        }
    }
}