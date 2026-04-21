package com.upd.kvupd.ui.fragment.altas.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.data.model.core.TableAlta
import com.upd.kvupd.databinding.FlowRowAltaBinding
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class AltaAdapter @AssistedInject constructor(
    @Assisted private val listener: Listener,
) : ListAdapter<TableAlta, AltaAdapter.ViewHolder>(Diff) {

    interface Listener {
        fun onLongClick(alta: TableAlta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FlowRowAltaBinding.inflate(
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
        private val bind: FlowRowAltaBinding
    ) : RecyclerView.ViewHolder(bind.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: TableAlta, listener: Listener) {
            var color = ""

            when (item.datos) {
                0 -> color = "#B6B6B6"
                1 -> color = "#3700B3"
            }

            bind.txtEmpleado.text = "Empleado - ${item.empleado}"
            bind.txtCodigo.text = item.idaux
            bind.txtFecha.text = item.fecha
            bind.txtDatos.setTextColor(Color.parseColor(color))

            bind.lnrAlta.setOnLongClickListener {
                listener.onLongClick(item)
                true
            }
        }
    }

    companion object {
        val Diff = object : DiffUtil.ItemCallback<TableAlta>() {
            override fun areItemsTheSame(a: TableAlta, b: TableAlta): Boolean =
                a.idaux == b.idaux

            override fun areContentsTheSame(a: TableAlta, b: TableAlta): Boolean =
                a == b
        }
    }
}