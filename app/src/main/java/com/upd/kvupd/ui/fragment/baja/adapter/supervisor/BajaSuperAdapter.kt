package com.upd.kvupd.ui.fragment.baja.adapter.supervisor

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.data.model.FlowBajaSupervisor
import com.upd.kvupd.databinding.FlowRowBajasuperBinding
import com.upd.kvupd.ui.fragment.baja.enumFile.MotivoBaja
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class BajaSuperAdapter @AssistedInject constructor(
    @Assisted private val listener: Listener,
) : ListAdapter<FlowBajaSupervisor, BajaSuperAdapter.ViewHolder>(Diff) {

    interface Listener {
        fun onBajaSupervisorLongClick(bajaSupervisor: FlowBajaSupervisor)
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
            var colorAprobado = ""
            var colorDenegado = ""

            when (item.procede) {
                0 -> {
                    colorAprobado = "#B6B6B6"
                    colorDenegado = "#3700B3"
                }
                1 -> {
                    colorAprobado = "#3700B3"
                    colorDenegado = "#B6B6B6"
                }
                null -> {
                    colorAprobado = "#B6B6B6"
                    colorDenegado = "#B6B6B6"
                }
            }

            bind.txtFecha.text = item.creacion
            bind.txtCliente.text = "${item.cliente} - ${item.nombre}"
            bind.txtMotivo.text = MotivoBaja.fromId(item.motivo).label
            bind.txtVendedor.text = "V-${item.vendedor}"
            bind.txtDenegado.setTextColor(Color.parseColor(colorDenegado))
            bind.txtAprobado.setTextColor(Color.parseColor(colorAprobado))

            bind.lnrBaja.setOnLongClickListener {
                listener.onBajaSupervisorLongClick(item)
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