package com.upd.kvupd.ui.fragment.reportes.adapter.detalle

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.databinding.RowSubcoberturadosBinding
import com.upd.kvupd.ui.fragment.reportes.modelUI.SubCoberturadosUI
import com.upd.kvupd.utils.gone
import com.upd.kvupd.utils.visible

class CoberturadosAdapter :
    ListAdapter<SubCoberturadosUI, CoberturadosAdapter.ViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowSubcoberturadosBinding.inflate(
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
        private val bind: RowSubcoberturadosBinding
    ) : RecyclerView.ViewHolder(bind.root) {

        fun bind(item: SubCoberturadosUI) {

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

            bind.txtDocumento.text = item.documento
            bind.txtCliente.text = "${item.codigo} - ${item.nombre}"
            bind.txtDireccion.text = item.direccion
        }
    }

    companion object {
        val Diff = object : DiffUtil.ItemCallback<SubCoberturadosUI>() {
            override fun areItemsTheSame(a: SubCoberturadosUI, b: SubCoberturadosUI): Boolean =
                a.codigo == b.codigo

            override fun areContentsTheSame(a: SubCoberturadosUI, b: SubCoberturadosUI): Boolean =
                a == b
        }
    }
}