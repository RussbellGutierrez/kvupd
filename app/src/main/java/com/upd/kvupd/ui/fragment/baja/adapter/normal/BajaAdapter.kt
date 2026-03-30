package com.upd.kvupd.ui.fragment.baja.adapter.normal

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.databinding.FlowRowBajaBinding
import com.upd.kvupd.ui.fragment.baja.enumFile.MotivoBaja
import com.upd.kvupd.utils.visibleIf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class BajaAdapter @AssistedInject constructor(
    @Assisted private val listener: Listener,
) : ListAdapter<TableBaja, BajaAdapter.ViewHolder>(Diff) {

    interface Listener {
        fun onBajaLongClick(baja: TableBaja)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FlowRowBajaBinding.inflate(
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
        private val bind: FlowRowBajaBinding
    ) : RecyclerView.ViewHolder(bind.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: TableBaja, listener: Listener) {
            val anulado = (item.anulado > 0)

            bind.txtFecha.text = item.fecha
            bind.txtCliente.text = "${item.cliente} - ${item.nombre}"
            bind.txtMotivo.text = MotivoBaja.labelFromId(item.motivo)
            bind.txtComentario.text = item.comentario

            bind.txtAnulado.visibleIf(anulado)

            bind.lnrBaja.setOnLongClickListener {
                listener.onBajaLongClick(item)
                true
            }
        }
    }

    companion object {
        val Diff = object : DiffUtil.ItemCallback<TableBaja>() {
            override fun areItemsTheSame(a: TableBaja, b: TableBaja): Boolean =
                a.cliente == b.cliente

            override fun areContentsTheSame(a: TableBaja, b: TableBaja): Boolean =
                a == b
        }
    }
}