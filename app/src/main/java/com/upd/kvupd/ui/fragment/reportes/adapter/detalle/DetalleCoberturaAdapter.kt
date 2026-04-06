package com.upd.kvupd.ui.fragment.reportes.adapter.detalle

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.databinding.RowPedidoBinding
import com.upd.kvupd.databinding.RowSubdetalleCoberturaBinding
import com.upd.kvupd.ui.fragment.reportes.modelUI.SubDetalleCoberturaUI
import com.upd.kvupd.utils.gone
import com.upd.kvupd.utils.visible

class DetalleCoberturaAdapter :
    ListAdapter<SubDetalleCoberturaUI, DetalleCoberturaAdapter.ViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowSubdetalleCoberturaBinding.inflate(
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
        private val bind: RowSubdetalleCoberturaBinding
    ) : RecyclerView.ViewHolder(bind.root) {

        fun bind(item: SubDetalleCoberturaUI) {

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

            bind.txtCliente.text = item.nombre
            bind.flxPedidos.removeAllViews()

            item.pedidos.forEach { pedido ->

                val pedidoBinding = RowPedidoBinding.inflate(
                    LayoutInflater.from(bind.root.context),
                    bind.flxPedidos,
                    false
                )

                pedidoBinding.txtPedido.text = pedido.numero
                pedidoBinding.txtTotal.text = pedido.importe

                bind.flxPedidos.addView(pedidoBinding.root)
            }
        }
    }

    companion object {
        val Diff = object : DiffUtil.ItemCallback<SubDetalleCoberturaUI>() {
            override fun areItemsTheSame(a: SubDetalleCoberturaUI, b: SubDetalleCoberturaUI): Boolean =
                a.codigo == b.codigo

            override fun areContentsTheSame(a: SubDetalleCoberturaUI, b: SubDetalleCoberturaUI): Boolean =
                a == b
        }
    }
}