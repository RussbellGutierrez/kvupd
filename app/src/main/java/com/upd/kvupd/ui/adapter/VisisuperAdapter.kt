package com.upd.kvupd.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.utils.OldBaseViewHolder
import javax.inject.Inject

class VisisuperAdapter @Inject constructor() : RecyclerView.Adapter<OldBaseViewHolder<*>>() {

    /*private val diffCallback = (object : DiffUtil.ItemCallback<Visisuper>() {

        override fun areItemsTheSame(oldItem: Visisuper, newItem: Visisuper): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Visisuper, newItem: Visisuper): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: Visisuper, newItem: Visisuper): Any? {
            val diff = Bundle()
            if (oldItem != newItem) {
                diff.putInt("id", newItem.id)
                diff.putString("vendedor", newItem.vendedor)
                diff.putInt("cliente", newItem.cliente)
                diff.putInt("avance", newItem.avance)
            }
            if (diff.size() == 0) {
                return null
            }
            return diff
        }
    })

    var mDiffer: AsyncListDiffer<Visisuper> = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val bind = RowMiniDetalleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        private val bind: RowMiniDetalleBinding
    ) : BaseViewHolder<Visisuper>(bind.root) {

        override fun bind(item: Visisuper) {
            val titulo = "${item.id} - ${item.vendedor}"
            val cuota = "Cuota: ${item.cliente}"
            val avance = "Avance: ${item.avance}"
            val percent = percent(item.avance.toDouble(),item.cliente.toDouble())
            val porcentaje = "$percent%"

            bind.lnrTres.setUI("v", true)
            bind.txtDatos.text = titulo
            bind.txtCuota.text = cuota
            bind.txtAvance.text = avance
            bind.txtPorcentaje.text = porcentaje
            bind.imgCerrar.setOnClickListener { visisuListener.onCloseItem(item) }
            bind.cardReporte.setOnClickListener { visisuListener.onItemClick(item) }
        }
    }

    interface OnVisisuperListener {
        fun onItemClick(visisuper: Visisuper)
        fun onCloseItem(visisuper: Visisuper)
    }*/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OldBaseViewHolder<*> {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: OldBaseViewHolder<*>, position: Int) {
        TODO("Not yet implemented")
    }
}