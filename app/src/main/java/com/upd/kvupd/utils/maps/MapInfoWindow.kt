package com.upd.kvupd.utils.maps

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.upd.kvupd.R
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.data.model.Pedimap
import com.upd.kvupd.databinding.InfowindowClientesBinding
import com.upd.kvupd.databinding.InfowindowDefaultBinding
import com.upd.kvupd.databinding.InfowindowPedimapBinding
import com.upd.kvupd.utils.gone
import com.upd.kvupd.utils.visible
import com.upd.kvupd.utils.visibleIf

class MapInfoWindow(
    private val inflater: LayoutInflater
) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? = null

    override fun getInfoContents(marker: Marker): View? {
        return when (val tag = marker.tag) {
            is Pedimap -> bindPedimap(tag)
            is FlowCliente -> bindFlowClientes(tag)
            else -> {
                if (marker.title.isNullOrEmpty() && marker.snippet.isNullOrEmpty()) {
                    return null
                }

                bindDefault(marker.title!!, marker.snippet!!)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindPedimap(data: Pedimap): View {
        val binding = InfowindowPedimapBinding.inflate(inflater)
        binding.apply {
            txtCodigo.text = "${data.cargo} - ${data.codigo}"
            if (data.emitiendo > 0) {
                txtEmite.visible()
                txtNoemite.gone()
                txtEmite.text = data.nombre
            } else {
                txtEmite.gone()
                txtNoemite.visible()
                txtNoemite.text = data.nombre
            }
            txtPrecision.text = data.precision.toString()
            txtBateria.text = data.bateria
            txtHora.text = data.hora
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun bindFlowClientes(data: FlowCliente): View {
        val binding = InfowindowClientesBinding.inflate(inflater)
        binding.apply {
            val context = root.context

            txtCodigo.text = data.cliente
            txtCliente.text = data.nomcli
            txtDireccion.text = data.domicilio
            txtNegocio.text = data.negocio
            txtFecha.text = data.fecha
            txtRuta.text = data.ruta.toString()
            txtVendedor.text = "V - ${data.vendedor}"

            val show = (data.baja > 0)
            txtBaja.visibleIf(show)

            when {
                data.compras == 1 -> {
                    txtVentas.setTextColor(
                        ContextCompat.getColor(context, R.color.sin_datos)
                    )
                    txtCompras.setTextColor(
                        ContextCompat.getColor(context, R.color.con_datos)
                    )
                }

                data.ventas == 1 -> {
                    txtVentas.setTextColor(
                        ContextCompat.getColor(context, R.color.con_datos)
                    )
                    txtCompras.setTextColor(
                        ContextCompat.getColor(context, R.color.sin_datos)
                    )
                }
            }
        }
        return binding.root
    }

    private fun bindDefault(titulo: String, snippet: String): View {
        val binding = InfowindowDefaultBinding.inflate(inflater)
        binding.apply {
            txtTitulo.text = titulo
            txtContenido.text = snippet
        }
        return binding.root
    }
}