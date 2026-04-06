package com.upd.kvupd.ui.fragment.reportes.adapter.detalle

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.databinding.RowSubPedidoCambiosBinding
import com.upd.kvupd.ui.fragment.reportes.modelUI.SubCambioUI
import com.upd.kvupd.utils.gone
import com.upd.kvupd.utils.visible

class CambiosAdapter :
    ListAdapter<SubCambioUI, CambiosAdapter.ViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowSubPedidoCambiosBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    @SuppressLint("SetTextI18n")
    class ViewHolder(
        private val bind: RowSubPedidoCambiosBinding
    ) : RecyclerView.ViewHolder(bind.root) {

        fun bind(item: SubCambioUI) {

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

            bind.txtUsuario.text = "${item.codigo} - ${item.nombre}"
            bind.txtSuperior.text = item.cambios
            bind.txtInferior.text = item.monto
        }
    }

    companion object {
        val Diff = object : DiffUtil.ItemCallback<SubCambioUI>() {
            override fun areItemsTheSame(a: SubCambioUI, b: SubCambioUI): Boolean =
                a.codigo == b.codigo

            override fun areContentsTheSame(a: SubCambioUI, b: SubCambioUI): Boolean =
                a == b
        }
    }
}