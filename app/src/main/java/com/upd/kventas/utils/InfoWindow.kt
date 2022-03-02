package com.upd.kventas.utils

import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.Marker
import com.upd.kventas.databinding.InfoWindowModelBinding
import com.upd.kventas.utils.Constant.IWAM
import com.upd.kventas.utils.Constant.M_ALTA
import com.upd.kventas.utils.Constant.M_BAJA
import com.upd.kventas.utils.Constant.M_CERRADO
import com.upd.kventas.utils.Constant.M_DINERO
import com.upd.kventas.utils.Constant.M_ENCARGADO
import com.upd.kventas.utils.Constant.M_NOEXISTE
import com.upd.kventas.utils.Constant.M_OCUPADO
import com.upd.kventas.utils.Constant.M_PEDIDO
import com.upd.kventas.utils.Constant.M_PRODUCTO

class InfoWindow (private val inflater: LayoutInflater) :
    InfoWindowAdapter {

    private var _bind: InfoWindowModelBinding? = null
    private val bind get() = _bind!!

    override fun getInfoContents(p0: Marker): View? = null

    override fun getInfoWindow(p0: Marker): View = createIW(p0)

    private fun createIW(m: Marker): View {
        _bind = InfoWindowModelBinding.inflate(inflater)
        when (m.title?.toInt()) {
            in 0..9 -> setData(m)
            10 -> { TODO("option for markers altas") }
            11 -> { TODO("option for markers bajas") }
            20 -> { TODO("option for markers from pedimap") }
        }
        return bind.root
    }

    private fun setData(marker: Marker) {
        bind.lnrCliente.setUI("v",true)
        bind.lnrVendedor.setUI("v",false)
        val cliente = "${IWAM.id} - ${IWAM.nombre}"
        bind.txtCliente.text = cliente
        bind.txtDomicilio.text = IWAM.domicilio
        bind.txtNegocio.text = IWAM.negocio
        bind.txtTelefono.text = IWAM.telefono
        bind.txtLongitud.text = marker.position.longitude.toString()
        bind.txtLatitud.text = marker.position.latitude.toString()
        if (marker.title != "9") {
            bind.cardMotivo.setUI("v",true)
            val motivo = when(marker.title?.toInt()) {
                0 -> M_PEDIDO
                1 -> M_CERRADO
                2 -> M_PRODUCTO
                3 -> M_DINERO
                4 -> M_ENCARGADO
                6 -> M_OCUPADO
                7 -> M_NOEXISTE
                10 -> M_ALTA
                else -> M_BAJA
            }
            bind.txtMotivo.text = motivo
        }
    }
}