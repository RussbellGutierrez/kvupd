package com.upd.kvupd.ui.fragment.reportes.adapter.lineas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.databinding.RowLineasResumenBinding
import com.upd.kvupd.ui.fragment.reportes.modelUI.LineaUI
import com.upd.kvupd.utils.gone
import com.upd.kvupd.utils.visible
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class LineasResumenAdapter @AssistedInject constructor(
    @Assisted private val listener: Listener
) : ListAdapter<LineaUI, LineasResumenAdapter.ViewHolder>(Diff) {

    interface Listener {
        fun onResumenLongClick(linea: LineaUI, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowLineasResumenBinding.inflate(
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
        private val bind: RowLineasResumenBinding
    ) : RecyclerView.ViewHolder(bind.root) {

        fun bind(item: LineaUI, listener: Listener) {

            val position = adapterPosition

            // 🔹 1. Loading
            if (item.isLoading) {
                bind.shimmer.startShimmer()
                bind.shimmer.visible()
                bind.lnrContenido.gone()
                return
            }

            // 🔹 2. Content
            bind.shimmer.stopShimmer()
            bind.shimmer.gone()
            bind.lnrContenido.visible()

            bind.txtLinea.text = item.titulo
            bind.txtCuota.text = item.cuota
            bind.txtAvance.text = item.avance
            bind.txtTotal.text = item.total
            bind.imgIndicador.setImageResource(item.indicador)

            bind.lnrContenido.setOnLongClickListener {
                if (position != RecyclerView.NO_POSITION) {
                    listener.onResumenLongClick(
                        item,
                        position
                    )
                }
                true
            }
        }
    }

    companion object {
        val Diff = object : DiffUtil.ItemCallback<LineaUI>() {
            override fun areItemsTheSame(a: LineaUI, b: LineaUI): Boolean =
                a.codigo == b.codigo

            override fun areContentsTheSame(a: LineaUI, b: LineaUI): Boolean =
                a == b
        }
    }
}