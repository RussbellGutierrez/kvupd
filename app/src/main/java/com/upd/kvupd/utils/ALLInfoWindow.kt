package com.upd.kvupd.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.upd.kvupd.R
import com.upd.kvupd.data.model.Cliente
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.data.model.Pedimap
import com.upd.kvupd.databinding.InfowindowClientesBinding
import com.upd.kvupd.databinding.InfowindowPedimapBinding

class ALLInfoWindow(
    private val inflater: LayoutInflater
) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? = null

    override fun getInfoContents(marker: Marker): View? {
        val tag = marker.tag ?: return null

        return when (tag) {
            is Pedimap -> bindPedimap(tag)
            is FlowCliente -> bindFlowClientes(tag)
            else -> null
        }
    }

    private fun bindPedimap(data: Pedimap): View {
        val binding = InfowindowPedimapBinding.inflate(inflater)
        binding.apply {
            txtCodigo.text = data.codigo.toString()
            if (data.emitiendo > 0) {
                txtEmite.setUI("v", true)
                txtNoemite.setUI("v", false)
                txtEmite.text = data.nombre
            } else {
                txtEmite.setUI("v", false)
                txtNoemite.setUI("v", true)
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
            when {
                data.baja == 1 -> txtEstado.apply {
                    text = "CLIENTE CON BAJA"
                    setBackgroundColor(
                        Color.parseColor("#DF3E5F")
                    )
                    setUI("v", true)
                }

                data.compras == 1 -> txtEstado.apply {
                    text = "NO COMPRA HACE 1 AÑO"
                    setBackgroundColor(
                        Color.parseColor("#3700B3")
                    )
                    setUI("v", true)
                }

                data.ventas == 0 -> txtEstado.apply {
                    text = "NO COMPRA HACE 3 MESES"
                    setBackgroundColor(
                        Color.parseColor("#DF3E5F")
                    )
                    setUI("v", true)
                }

                else -> txtEstado.setUI("v", false)
            }
        }
        return binding.root
    }
}