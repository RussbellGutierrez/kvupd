package com.upd.kvupd.ui.fragment.reportes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.databinding.RowKpiBinding
import com.upd.kvupd.domain.enumFile.TipoUsuario
import com.upd.kvupd.ui.fragment.reportes.modelUI.KpiUI
import com.upd.kvupd.utils.gone
import com.upd.kvupd.utils.visible
import com.upd.kvupd.utils.visibleIf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class KpiAdapter @AssistedInject constructor(
    @Assisted private val listener: Listener,
    @Assisted private val tipoUsuario: TipoUsuario
) : ListAdapter<KpiUI, KpiAdapter.ViewHolder>(Diff) {

    interface Listener {
        fun onKpiClick(kpi: KpiUI)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowKpiBinding.inflate(
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
        private val bind: RowKpiBinding,
        private val tipoUsuario: TipoUsuario
    ) : RecyclerView.ViewHolder(bind.root) {

        fun bind(item: KpiUI, listener: Listener) {

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

            val canClick = item.tipo.canClick(tipoUsuario)

            bind.txtKpi.text = item.tipo.titulo
            bind.txtOjito.visibleIf(canClick)
            bind.txtCuota.text = item.cuota
            bind.txtAvance.text = item.avance
            bind.txtTotal.text = item.total

            bind.lnrContenido.setOnClickListener {
                if (canClick) listener.onKpiClick(item)
            }
        }
    }

    companion object {
        val Diff = object : DiffUtil.ItemCallback<KpiUI>() {
            override fun areItemsTheSame(a: KpiUI, b: KpiUI): Boolean =
                a.tipo == b.tipo

            override fun areContentsTheSame(a: KpiUI, b: KpiUI): Boolean =
                a == b
        }
    }
}