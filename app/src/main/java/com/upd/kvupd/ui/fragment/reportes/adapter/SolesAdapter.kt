package com.upd.kvupd.ui.fragment.reportes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.databinding.RowSolesBinding
import com.upd.kvupd.ui.fragment.reportes.modelUI.SolesUI

class SolesAdapter :
    ListAdapter<SolesUI, SolesAdapter.ViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowSolesBinding.inflate(
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
        private val bind: RowSolesBinding
    ) : RecyclerView.ViewHolder(bind.root) {

        fun bind(item: SolesUI) {

            // 🔹 1. Content
            bind.txtNombre.text = item.nombre
            bind.txtMinicuota.text = item.cuota
            bind.txtMiniavance.text = item.avance
            bind.txtMinitotal.text = item.total
            bind.imgMiniindicador.setImageResource(item.indicador)
        }
    }

    companion object {
        val Diff = object : DiffUtil.ItemCallback<SolesUI>() {
            override fun areItemsTheSame(a: SolesUI, b: SolesUI): Boolean =
                a.id == b.id

            override fun areContentsTheSame(a: SolesUI, b: SolesUI): Boolean =
                a == b
        }
    }
}