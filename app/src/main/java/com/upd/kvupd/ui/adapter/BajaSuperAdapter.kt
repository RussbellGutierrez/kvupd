package com.upd.kvupd.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.data.model.FlowBajaSupervisor
import com.upd.kvupd.databinding.FlowRowBajasuperBinding
import com.upd.kvupd.ui.fragment.enumClass.MotivoBaja
import com.upd.kvupd.utils.setUI
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class BajaSuperAdapter @AssistedInject constructor(
    @Assisted private val listener: Listener,
) : ListAdapter<FlowBajaSupervisor, BajaSuperAdapter.ViewHolder>(Diff) {

    interface Listener {
        fun onLongClick(bajaSupervisor: FlowBajaSupervisor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FlowRowBajasuperBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class ViewHolder(
        private val bind: FlowRowBajasuperBinding
    ) : RecyclerView.ViewHolder(bind.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: FlowBajaSupervisor, listener: Listener) {
            bind.txtFecha.text = item.fecha
            bind.txtCliente.text = "${item.cliente} - ${item.nombre}"
            bind.txtMotivo.text = item.motivo
            bind.txtVendedor.text = "V-${item.vendedor}"

            when (item.procede) {
                0 -> {
                    bind.txtDenegado.setTextColor(Color.parseColor("#3700B3"))
                    bind.txtAprobado.setTextColor(Color.parseColor("#B6B6B6"))
                }
                1 -> {
                    bind.txtAprobado.setTextColor(Color.parseColor("#3700B3"))
                    bind.txtDenegado.setTextColor(Color.parseColor("#B6B6B6"))
                }
                null -> {
                    bind.txtDenegado.setTextColor(Color.parseColor("#B6B6B6"))
                    bind.txtAprobado.setTextColor(Color.parseColor("#B6B6B6"))
                }
            }

            bind.lnrBaja.setOnLongClickListener {
                listener.onLongClick(item)
                true
            }
        }
    }

    companion object {
        val Diff = object : DiffUtil.ItemCallback<FlowBajaSupervisor>() {
            override fun areItemsTheSame(a: FlowBajaSupervisor, b: FlowBajaSupervisor): Boolean =
                (a.cliente == b.cliente) && (a.vendedor == b.vendedor)

            override fun areContentsTheSame(a: FlowBajaSupervisor, b: FlowBajaSupervisor): Boolean =
                a == b
        }
    }
}