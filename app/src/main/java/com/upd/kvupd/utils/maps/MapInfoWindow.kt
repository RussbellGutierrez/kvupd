package com.upd.kvupd.utils.maps

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
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

    private fun bindPedimap(data: Pedimap): View {
        val binding = InfowindowPedimapBinding.inflate(inflater)
        binding.apply {
            txtCodigo.text = data.codigo
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
            txtCodigo.text = data.cliente
            txtCliente.text = data.nomcli
            txtDireccion.text = data.domicilio
            txtNegocio.text = data.negocio
            txtRuta.text = data.ruta.toString()
            txtVendedor.text = "V - ${data.vendedor}"

            val show = (data.baja > 0)
            txtBaja.visibleIf(show)

            when {
                data.compras == 1 -> {
                    txtVentas.setTextColor(Color.parseColor("#B6B6B6"))
                    txtCompras.setTextColor(Color.parseColor("#3700B3"))
                }

                data.ventas == 0 -> {
                    txtCompras.setTextColor(Color.parseColor("#B6B6B6"))
                    txtVentas.setTextColor(Color.parseColor("#3700B3"))
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