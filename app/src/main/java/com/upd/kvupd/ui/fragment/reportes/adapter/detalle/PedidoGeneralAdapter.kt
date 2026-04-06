package com.upd.kvupd.ui.fragment.reportes.adapter.detalle

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.databinding.RowSubPedidoCambiosBinding
import com.upd.kvupd.ui.fragment.reportes.modelUI.SubPedidoGeneralUI
import com.upd.kvupd.utils.gone
import com.upd.kvupd.utils.visible

class PedidoGeneralAdapter :
    ListAdapter<SubPedidoGeneralUI, PedidoGeneralAdapter.ViewHolder>(Diff) {

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

        fun bind(item: SubPedidoGeneralUI) {

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

            bind.txtUsuario.text = "${item.id} - ${item.nombre}"
            bind.txtSuperior.text = item.clientes
            bind.txtInferior.text = item.pedidos
        }
    }

    companion object {
        val Diff = object : DiffUtil.ItemCallback<SubPedidoGeneralUI>() {
            override fun areItemsTheSame(a: SubPedidoGeneralUI, b: SubPedidoGeneralUI): Boolean =
                a.id == b.id

            override fun areContentsTheSame(a: SubPedidoGeneralUI, b: SubPedidoGeneralUI): Boolean =
                a == b
        }
    }
}