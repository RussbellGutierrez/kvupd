package com.upd.kvupd.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.utils.OldBaseViewHolder
import javax.inject.Inject

class OldUmesAdapter @Inject constructor() : RecyclerView.Adapter<OldBaseViewHolder<*>>() {

    /*private val diffCallback = (object : DiffUtil.ItemCallback<Umes>() {

        override fun areItemsTheSame(oldItem: Umes, newItem: Umes): Boolean {
            return oldItem.linea.codigo == newItem.linea.codigo
        }

        override fun areContentsTheSame(oldItem: Umes, newItem: Umes): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: Umes, newItem: Umes): Any? {
            val diff = Bundle()
            if (oldItem != newItem) {
                diff.putInt("marca_codigo", newItem.marca.codigo)
                diff.putString("marca_descripcion", newItem.marca.descripcion)
                diff.putInt("linea_codigo", newItem.linea.codigo)
                diff.putString("linea_descripcion", newItem.linea.descripcion)
                diff.putDouble("cuota", newItem.cuota)
                diff.putDouble("avance", newItem.avance)
            }
            if (diff.size() == 0) {
                return null
            }
            return diff
        }
    })

    var mDiffer: AsyncListDiffer<Umes> = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val bind = RowReporteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        private val bind: RowReporteBinding
    ) : BaseViewHolder<Umes>(bind.root) {

        override fun bind(item: Umes) {
            val titulo = item.marca.descripcion
            val cuota = "Cuota: ${item.cuota}"
            val avance = "Avance: ${item.avance}"
            val percent = percent(item.avance,item.cuota)
            val porcentaje = "$percent%"

            if (CONF.tipo == "V") {
                bind.imgCerrar.setUI("v", false)
            }

            when{
                percent.toDouble() > 85 -> bind.imgFlecha.setImageResource(R.drawable.f_arriba)
                percent.toDouble() in 70.0..85.0 -> {
                    ImageViewCompat.setImageTintList(bind.imgFlecha, ColorStateList.valueOf(Color.parseColor("#FFAB00")))
                    bind.imgFlecha.setImageResource(R.drawable.f_arriba)
                }
                percent.toDouble() in 1.0..69.99 -> bind.imgFlecha.setImageResource(R.drawable.f_bajo)
                percent.toDouble() < 1 -> bind.imgFlecha.setImageResource(R.drawable.f_neutral)
            }

            bind.txtTitulo.text = titulo
            bind.txtCuota.text = cuota
            bind.txtAvance.text = avance
            bind.txtPorcentaje.text = porcentaje
            //bind.imgCerrar.setOnClickListener { umesListener.onCloseItem(item) }
            //bind.cardReporte.setOnClickListener { umesListener.onItemClick(item) }
            /*bind.cardReporte.setOnLongClickListener {
                umesListener.onItemPress(item)
                return@setOnLongClickListener true
            }*/
        }
    }

    interface OnUmesListener {
        //fun onItemClick(umes: Umes)
        //fun onItemPress(umes: Umes)
        //fun onCloseItem(umes: Umes)
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