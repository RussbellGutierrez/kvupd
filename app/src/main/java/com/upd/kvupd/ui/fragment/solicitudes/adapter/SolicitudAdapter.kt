package com.upd.kvupd.ui.fragment.solicitudes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.databinding.RowServidorBinding
import com.upd.kvupd.databinding.RowSolicitudBinding
import com.upd.kvupd.ui.fragment.servidor.enumFile.ApiServerStatus
import com.upd.kvupd.ui.fragment.servidor.enumFile.DrawablePosition
import com.upd.kvupd.ui.fragment.servidor.modelUI.UploadItem
import com.upd.kvupd.ui.fragment.solicitudes.modelUI.SolicitudUI
import com.upd.kvupd.utils.setDrawableTint
import com.upd.kvupd.utils.visibleIf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class SolicitudAdapter @AssistedInject constructor(
    @Assisted private val listener: Listener
) : ListAdapter<SolicitudUI, SolicitudAdapter.ViewHolder>(Diff) {

    interface Listener {
        fun onCheckedChanged(item: SolicitudUI, checked: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowSolicitudBinding.inflate(
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
        private val bind: RowSolicitudBinding
    ) : RecyclerView.ViewHolder(bind.root) {

        fun bind(item: SolicitudUI, listener: Listener) {

            bind.chkSolicitud.setOnCheckedChangeListener(null)

            bind.chkSolicitud.text = item.tipo.descripcion
            bind.chkSolicitud.isChecked = item.seleccionado

            bind.chkSolicitud.setOnCheckedChangeListener { _, checked ->
                listener.onCheckedChanged(item, checked)
            }

            bind.root.setOnClickListener {
                bind.chkSolicitud.performClick()
            }
        }
    }

    companion object {
        val Diff = object : DiffUtil.ItemCallback<SolicitudUI>() {
            override fun areItemsTheSame(a: SolicitudUI, b: SolicitudUI): Boolean =
                a.tipo.id == b.tipo.id

            override fun areContentsTheSame(a: SolicitudUI, b: SolicitudUI): Boolean =
                a == b
        }
    }
}