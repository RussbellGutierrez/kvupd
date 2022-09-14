package com.upd.kvupd.utils

import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.Marker
import com.upd.kvupd.databinding.InfoWindowModelBinding
import com.upd.kvupd.utils.Constant.IWAM
import com.upd.kvupd.utils.Constant.IWDA
import com.upd.kvupd.utils.Constant.IWP
import com.upd.kvupd.utils.Constant.M_ALTA
import com.upd.kvupd.utils.Constant.M_CERRADO
import com.upd.kvupd.utils.Constant.M_DINERO
import com.upd.kvupd.utils.Constant.M_ENCARGADO
import com.upd.kvupd.utils.Constant.M_NOEXISTE
import com.upd.kvupd.utils.Constant.M_OCUPADO
import com.upd.kvupd.utils.Constant.M_PEDIDO
import com.upd.kvupd.utils.Constant.M_PRODUCTO
import com.upd.kvupd.utils.Constant.PROCEDE

class InfoWindow(private val inflater: LayoutInflater) :
    InfoWindowAdapter {

    private var _bind: InfoWindowModelBinding? = null
    private val bind get() = _bind!!

    override fun getInfoContents(p0: Marker): View? = null

    override fun getInfoWindow(p0: Marker): View = createIW(p0)

    private fun createIW(m: Marker): View {
        _bind = InfoWindowModelBinding.inflate(inflater)
        when (m.title?.toInt()) {
            in 0..10 -> setData(m)
            20 -> dataPedimap(m)
        }
        return bind.root
    }

    private fun setData(marker: Marker) {
        commonParams()
        bind.lnrCliente.setUI("v", true)
        bind.lnrVendedor.setUI("v", false)
        bind.txtLongitud.text = marker.position.longitude.toString()
        bind.txtLatitud.text = marker.position.latitude.toString()
        if (marker.title != "9") {
            bind.cardMotivo.setUI("v", true)
            val motivo = when (marker.title?.toInt()) {
                0 -> M_PEDIDO
                1 -> M_CERRADO
                2 -> M_PRODUCTO
                3 -> M_DINERO
                4 -> M_ENCARGADO
                6 -> M_OCUPADO
                7 -> M_NOEXISTE
                else -> M_ALTA
            }
            bind.txtMotivo.text = motivo
        }
    }

    private fun commonParams() {
        when (PROCEDE) {
            "Mapa" -> {
                val cliente = "${IWAM.id} - ${IWAM.nombre}"
                bind.txtCliente.text = cliente.trim()
                bind.txtDomicilio.text = IWAM.domicilio.trim()
                bind.txtNegocio.text = IWAM.negocio.trim()
                bind.txtTelefono.text = IWAM.telefono.trim()
            }
            "MapaAlta" -> {
                val cliente = "${IWDA.id} - ${IWDA.nombre}"
                bind.txtCliente.text = cliente.trim()
                bind.txtDomicilio.text = IWDA.domicilio.trim()
                bind.txtNegocio.text = IWDA.negocio.trim()
                bind.txtTelefono.text = IWDA.telefono.trim()
            }
        }
    }

    private fun dataPedimap(marker: Marker) {
        bind.lnrCliente.setUI("v", false)
        bind.lnrVendedor.setUI("v", true)
        val cliente = "${IWP.codigo} - ${IWP.nombre}"
        bind.txtVendedor.text = cliente
        bind.txtPrecision.text = IWP.precision.toString()
        bind.txtBateria.text = IWP.bateria
        bind.txtHora.text = IWP.hora
        bind.txtLongV.text = marker.position.longitude.toString()
        bind.txtLatiV.text = marker.position.latitude.toString()
    }

}