package com.upd.kvupd.ui.fragment.reportes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.databinding.RowLineasBinding
import com.upd.kvupd.domain.enumFile.TipoUsuario
import com.upd.kvupd.ui.fragment.reportes.enumFile.TipoReporte
import com.upd.kvupd.ui.fragment.reportes.modelUI.LineaUI
import com.upd.kvupd.utils.gone
import com.upd.kvupd.utils.visible
import com.upd.kvupd.utils.visibleIf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class LineasAdapter @AssistedInject constructor(
    @Assisted private val listener: Listener,
    @Assisted private val tipoUsuario: TipoUsuario
) : ListAdapter<LineaUI, LineasAdapter.ViewHolder>(Diff) {

    interface Listener {
        fun onLineaClick(linea: LineaUI)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowLineasBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, tipoUsuario)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    class ViewHolder(
        private val bind: RowLineasBinding,
        private val tipoUsuario: TipoUsuario
    ) : RecyclerView.ViewHolder(bind.root) {

        private val solesAdapter = SolesAdapter()

        init {
            bind.rcvSoles.adapter = solesAdapter
            bind.rcvSoles.layoutManager = LinearLayoutManager(bind.root.context)
            bind.rcvSoles.itemAnimator = null
        }

        fun bind(item: LineaUI, listener: Listener) {

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

            val canClick = TipoReporte.SOLES.canClick(tipoUsuario)

            bind.txtLinea.text = item.titulo
            bind.txtOjito.visibleIf(canClick)
            bind.txtCuota.text = item.cuota
            bind.txtAvance.text = item.avance
            bind.txtTotal.text = item.total
            bind.imgIndicador.setImageResource(item.indicador)

            solesAdapter.submitList(item.soles.toList())

            bind.lnrContenido.setOnClickListener {
                if (canClick) listener.onLineaClick(item)
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