package com.upd.kventas.ui.adapter

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
import com.upd.kventas.R
import com.upd.kventas.data.model.RowCliente
import com.upd.kventas.data.model.TBaja
import com.upd.kventas.databinding.RowBajaBinding
import com.upd.kventas.databinding.RowClienteBinding
import com.upd.kventas.domain.Functions
import com.upd.kventas.utils.BaseViewHolder
import com.upd.kventas.utils.Constant.CONF
import com.upd.kventas.utils.Interface.bajaListener
import com.upd.kventas.utils.Interface.clienteListener
import com.upd.kventas.utils.setUI
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BajaAdapter @Inject constructor() : RecyclerView.Adapter<BaseViewHolder<*>>() {

    private val diffCallback = (object : DiffUtil.ItemCallback<TBaja>() {

        override fun areItemsTheSame(oldItem: TBaja, newItem: TBaja): Boolean {
            return oldItem.cliente == newItem.cliente
        }

        override fun areContentsTheSame(oldItem: TBaja, newItem: TBaja): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: TBaja, newItem: TBaja): Any? {
            val diff = Bundle()
            if (oldItem != newItem) {
                diff.putInt("cliente", newItem.cliente)
                diff.putString("nombre", newItem.nombre)
                diff.putInt("motivo", newItem.motivo)
                diff.putString("comentario", newItem.comentario)
                diff.putDouble("longitud", newItem.longitud)
                diff.putDouble("latitud", newItem.latitud)
                diff.putDouble("precision", newItem.precision)
                diff.putString("fecha", newItem.fecha)
                diff.putInt("anulado", newItem.anulado)
                diff.putString("estado", newItem.estado)
            }
            if (diff.size() == 0) {
                return null
            }
            return diff
        }
    })

    var mDiffer: AsyncListDiffer<TBaja> = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val bind = RowBajaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bind)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is ViewHolder -> holder.bind(mDiffer.currentList[position])
        }
    }

    override fun getItemCount() = mDiffer.currentList.size

    override fun getItemViewType(position: Int) = position

    private inner class ViewHolder(
        private val bind: RowBajaBinding
    ) : BaseViewHolder<TBaja>(bind.root) {

        override fun bind(item: TBaja) {

            val cliente = "${item.cliente} - ${item.nombre}"
            val motivo = when(item.motivo){
                1 -> "Código duplicado"
                2 -> "Cliente no existe"
                3 -> "Negocio cerrado"
                else -> "Cambio de giro"
            }

            bind.txtFecha.text = item.fecha
            bind.txtCliente.text = cliente
            bind.txtMotivo.text = motivo

            if (item.comentario != "") {
                bind.txtObservacion.setUI("v",true)
                bind.txtObservacion.text = item.comentario
            }

            if (item.anulado > 0) {
                ImageViewCompat.setImageTintList(bind.imgAnulado, ColorStateList.valueOf(Color.parseColor("#DF3E5F")))
            }

            bind.lnrBaja.setOnLongClickListener {
                if (item.anulado == 0) {
                    bajaListener.onPressBaja(item)
                }
                return@setOnLongClickListener true
            }
        }
    }

    interface OnBajaListener {
        fun onPressBaja(baja: TBaja)
    }
}