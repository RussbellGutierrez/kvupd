package com.upd.kvupd.ui.fragment.servidor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.databinding.RowServidorBinding
import com.upd.kvupd.ui.fragment.servidor.enumFile.ApiServerStatus
import com.upd.kvupd.ui.fragment.servidor.enumFile.DrawablePosition
import com.upd.kvupd.ui.fragment.servidor.modelUI.UploadItem
import com.upd.kvupd.utils.setDrawableTint
import com.upd.kvupd.utils.visibleIf

class ServidorAdapter :
    ListAdapter<UploadItem, ServidorAdapter.ViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowServidorBinding.inflate(
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
        private val bind: RowServidorBinding
    ) : RecyclerView.ViewHolder(bind.root) {

        fun bind(item: UploadItem) {
            val ctx = bind.root.context
            val hasData = item.total > 0
            val isLoading = item.status == ApiServerStatus.LOADING

            val colorRes = item.status.colorRes

            val color = ContextCompat.getColor(ctx, colorRes)

            // 🔹 1. Content
            bind.txtServidor.text = item.type.titulo
            bind.txtServidor.setDrawableTint(DrawablePosition.TOP, color)

            bind.txtCantidad.text = when {
                item.total == 0 -> "Sin registros"
                else -> "${item.processed} / ${item.pending} pendientes (${item.total} total)"
            }

            bind.pbLinear.visibleIf(isLoading && hasData)
        }
    }

    companion object {
        val Diff = object : DiffUtil.ItemCallback<UploadItem>() {
            override fun areItemsTheSame(a: UploadItem, b: UploadItem): Boolean =
                a.type == b.type

            override fun areContentsTheSame(a: UploadItem, b: UploadItem): Boolean =
                a == b
        }
    }
}