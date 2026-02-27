package com.upd.kvupd.ui.dialog

import MapHelper
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.upd.kvupd.R
import com.upd.kvupd.data.model.BajaAux
import com.upd.kvupd.data.model.FlowBajaSupervisor
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.databinding.BottomBajaclienteBinding
import com.upd.kvupd.databinding.BottomDetallebajaBinding
import com.upd.kvupd.ui.fragment.enumClass.MotivoBaja
import com.upd.kvupd.utils.BajaConstantes.KEY_BAJA
import com.upd.kvupd.utils.BajaConstantes.PAIR_BAJA
import com.upd.kvupd.utils.FechaHoraUtil
import com.upd.kvupd.utils.awaitMap
import com.upd.kvupd.utils.icono
import com.upd.kvupd.utils.toUpper
import com.upd.kvupd.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BDDetalleBaja : BottomSheetDialogFragment() {

    private val args: BDDetalleBajaArgs by navArgs()
    private val binding by viewBinding(BottomDetallebajaBinding::bind)

    private lateinit var detalle: FlowBajaSupervisor
    private val mapHelper by lazy { MapHelper(layoutInflater) }
    private val _tag by lazy { BDDetalleBaja::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detalle = args.detalle
    }

    override fun onDestroy() {
        mapHelper.clearMarkers()
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = BottomDetallebajaBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMiniMapa {
            drawBajaMapa()
        }

        binding.apply {
            txtCliente
            txtDireccion
            txtCanal
            txtNegocio
            txtPago
            txtVendedor
            txtObservacion
            edtComentario
            btnDenegar
            btnValidar
        }

        /*val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            MotivoBaja.entries.map { it.label }
        )

        binding.apply {
            txtCliente.text = cliente.cliente
            txtNombre.text = cliente.nomcli
            txtDireccion.text = cliente.domicilio
            txtNegocio.text = cliente.negocio

            autoMotivo.setAdapter(adapter)

            btnAnular.setOnClickListener {
                devolverDatosFragment()
            }
        }*/
    }

    private fun drawBajaMapa() {
        mapHelper.clearMarkers()

        mapHelper.addMarker(
            data = detalle,
            lat = detalle.latitud,
            lng = detalle.longitud,
            icon = detalle.icono(requireContext())
        )

        mapHelper.centerMarkers(
            includeLocation = getLocation?.let { LatLng(it.latitude, it.longitude) }
        )
    }

    private fun initMiniMapa(onReady: (GoogleMap) -> Unit = {}) {
        val fragment = childFragmentManager
            .findFragmentById(R.id.minimap) as? SupportMapFragment
            ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val gMap = fragment.awaitMap()
            mapHelper.attachMap(gMap)
            configurarMiniMapa(gMap)
            onReady(gMap)
        }
    }

    private fun configurarMiniMapa(gMap: GoogleMap) {
        gMap.uiSettings.apply {
            isZoomControlsEnabled = false
            isScrollGesturesEnabled = false
            isZoomGesturesEnabled = false
            isRotateGesturesEnabled = false
            isTiltGesturesEnabled = false
            isMapToolbarEnabled = false
        }
    }

    /*private fun recolectarDatos(): BajaAux {
        val comentario = binding.edtComentario.text.toString().trim()

        val motivo = MotivoBaja.entries
            .first { it.label == binding.autoMotivo.text.toString() }

        return BajaAux(
            cliente = cliente,
            motivo = motivo.id,
            comentario = comentario.toUpper(),
            fecha = FechaHoraUtil.ahora()
        )
    }

    private fun devolverDatosFragment() {
        val baja = recolectarDatos()

        parentFragmentManager.setFragmentResult(
            KEY_BAJA,
            bundleOf(PAIR_BAJA to baja)
        )

        dismiss()
    }*/
}