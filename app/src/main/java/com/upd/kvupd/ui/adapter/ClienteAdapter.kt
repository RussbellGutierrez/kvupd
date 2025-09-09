package com.upd.kvupd.ui.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.R
import com.upd.kvupd.data.model.RowCliente
import com.upd.kvupd.databinding.RowClienteBinding
import com.upd.kvupd.domain.OldRepository
import com.upd.kvupd.utils.OldBaseViewHolder
import com.upd.kvupd.utils.OldConstant.CONF
import com.upd.kvupd.utils.OldInterface.clienteListener
import com.upd.kvupd.utils.dateToday
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.utils.textToTime
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class ClienteAdapter @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val repository: OldRepository
) : RecyclerView.Adapter<OldBaseViewHolder<*>>() {

    private val diffCallback = (object : DiffUtil.ItemCallback<RowCliente>() {

        override fun areItemsTheSame(oldItem: RowCliente, newItem: RowCliente): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RowCliente, newItem: RowCliente): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: RowCliente, newItem: RowCliente): Any? {
            val diff = Bundle()
            if (oldItem != newItem) {
                diff.putInt("idcliente", newItem.id)
                diff.putString("nomcli", newItem.nombre)
                diff.putInt("empleado", newItem.vendedor)
                diff.putString("descripcion", newItem.nomven)
                diff.putInt("secuencia", newItem.secuencia)
                diff.putInt("ruta", newItem.ruta)
                diff.putInt("atendido", newItem.atendido)
                diff.putString("fecha", newItem.fecha)
                diff.putString("encuestas", newItem.encuestas)
                diff.putInt("resuelto", newItem.resuelto)
            }
            if (diff.size() == 0) {
                return null
            }
            return diff
        }
    })

    var mDiffer: AsyncListDiffer<RowCliente> = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OldBaseViewHolder<*> {
        val bind = RowClienteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bind)
    }

    override fun onBindViewHolder(holder: OldBaseViewHolder<*>, position: Int) {
        when (holder) {
            is ViewHolder -> holder.bind(mDiffer.currentList[position])
        }
    }

    override fun getItemCount() = mDiffer.currentList.size

    override fun getItemViewType(position: Int) = position

    private inner class ViewHolder(
        private val bind: RowClienteBinding
    ) : OldBaseViewHolder<RowCliente>(bind.root) {

        override fun bind(item: RowCliente) {
            CoroutineScope(Dispatchers.Main).launch {
                val fecha = Calendar.getInstance().time.dateToday(5).textToTime(5)!!
                val cliente = "${item.id} - ${item.nombre}"
                val secuencia = "Sec ${item.secuencia}"
                val fc = item.fecha.textToTime(5)

                if (CONF.tipo == "V") {
                    bind.txtVendedor.setUI("v", false)
                    bind.dvdVendedor.setUI("v", false)
                    if (fecha.compareTo(fc) == 0) {
                        bind.txtFecha.setTextColor(ctx.getColor(R.color.dodgerblue))
                    } else {
                        bind.txtFecha.setTextColor(ctx.getColor(R.color.gold))
                    }
                }

                val selec = repository.getSeleccionado()
                if (selec != null) {
                    if (item.encuestas == "") {
                        if (item.resuelto > 0) {
                            ImageViewCompat.setImageTintList(
                                bind.imgEncuesta,
                                ColorStateList.valueOf(Color.parseColor("#1E90FF"))
                            )
                        }
                    } else {
                        item.encuestas.split(",").forEach {
                            val encuesta = it.trim().toInt()
                            when {
                                encuesta == item.resuelto -> ImageViewCompat.setImageTintList(
                                    bind.imgEncuesta,
                                    ColorStateList.valueOf(Color.parseColor("#1E90FF"))
                                )
                                encuesta == selec.encuesta -> ImageViewCompat.setImageTintList(
                                    bind.imgEncuesta,
                                    ColorStateList.valueOf(Color.parseColor("#F564EE"))
                                )
                                item.resuelto > 0 -> ImageViewCompat.setImageTintList(
                                    bind.imgEncuesta,
                                    ColorStateList.valueOf(Color.parseColor("#1E90FF"))
                                )
                            }
                        }
                    }
                }

                bind.txtVendedor.text = item.nomven
                bind.txtCliente.text = cliente
                bind.txtFecha.text = item.fecha
                bind.txtSecuencia.text = secuencia

                when (item.atendido) {
                    1 -> ImageViewCompat.setImageTintList(
                        bind.imgVisita,
                        ColorStateList.valueOf(Color.parseColor("#07A395"))
                    )
                    2 -> ImageViewCompat.setImageTintList(
                        bind.imgAnulado,
                        ColorStateList.valueOf(Color.parseColor("#DF3E5F"))
                    )
                }
                bind.lnrCliente.setOnClickListener {
                    clienteListener.onClienteClick(item)
                }
                bind.lnrCliente.setOnLongClickListener {
                    clienteListener.onPressCliente(item)
                    return@setOnLongClickListener true
                }
            }
        }
    }

    interface OnClienteListener {
        fun onClienteClick(cliente: RowCliente)
        fun onPressCliente(cliente: RowCliente)
    }
}