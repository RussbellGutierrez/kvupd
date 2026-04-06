package com.upd.kvupd.ui.fragment.reportes.adapter.detalle

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.databinding.RowSolesDetalleBinding
import com.upd.kvupd.ui.fragment.reportes.modelUI.SubProgresoUI
import com.upd.kvupd.utils.gone
import com.upd.kvupd.utils.visible
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class SolesDetalleAdapter @AssistedInject constructor(
    @Assisted private val listener: Listener
) : ListAdapter<SubProgresoUI, SolesDetalleAdapter.ViewHolder>(Diff) {

    interface Listener {
        fun onSolesClick(soles: SubProgresoUI)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowSolesDetalleBinding.inflate(
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
        private val bind: RowSolesDetalleBinding
    ) : RecyclerView.ViewHolder(bind.root) {

        fun bind(item: SubProgresoUI, listener: Listener) {

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

            bind.txtDetalle.text = item.descripcion
            bind.txtCuota.text = item.objetivo
            bind.txtAvance.text = item.avance
            bind.txtTotal.text = item.porcentaje
            bind.imgIndicador.setImageResource(item.indicador)

            bind.lnrContenido.setOnClickListener {
                listener.onSolesClick(item)
            }
        }
    }

    companion object {
        val Diff = object : DiffUtil.ItemCallback<SubProgresoUI>() {
            override fun areItemsTheSame(a: SubProgresoUI, b: SubProgresoUI): Boolean =
                a.codigo == b.codigo

            override fun areContentsTheSame(a: SubProgresoUI, b: SubProgresoUI): Boolean =
                a == b
        }
    }
}