package com.upd.kvupd.ui.fragment.reportes.adapter.detalle

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.databinding.RowSubprogresoBinding
import com.upd.kvupd.ui.fragment.reportes.modelUI.SubProgresoUI
import com.upd.kvupd.utils.gone
import com.upd.kvupd.utils.visible

class ProgresoAdapter :
    ListAdapter<SubProgresoUI, ProgresoAdapter.ViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowSubprogresoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val bind: RowSubprogresoBinding
    ) : RecyclerView.ViewHolder(bind.root) {

        fun bind(item: SubProgresoUI) {

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

            bind.txtVendedor.text = item.descripcion
            bind.txtCuota.text = item.objetivo
            bind.txtAvance.text = item.avance
            bind.txtTotal.text = item.porcentaje
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