package com.upd.kvupd.utils

import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.upd.kvupd.data.model.Cliente
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
            is Cliente -> bindClientes(tag)
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

    private fun bindClientes(data: Cliente): View {
        val binding = InfowindowClientesBinding.inflate(inflater)
        binding.apply {
            //
        }
        return binding.root
    }
}