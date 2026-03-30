package com.upd.kvupd.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.upd.kvupd.R
import com.upd.kvupd.data.model.FlowBajaSupervisor
import com.upd.kvupd.data.model.TableSeguimiento
import com.upd.kvupd.databinding.BottomDetallebajaBinding
import com.upd.kvupd.ui.fragment.baja.enumFile.Canal
import com.upd.kvupd.ui.fragment.baja.enumFile.MotivoBaja
import com.upd.kvupd.utils.BundleConstantes.KEY_DETALLE
import com.upd.kvupd.utils.FechaHoraUtil
import com.upd.kvupd.utils.UbicacionActual
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.utils.expandFullHeight
import com.upd.kvupd.utils.maps.MapHelper
import com.upd.kvupd.utils.maps.awaitMap
import com.upd.kvupd.utils.maps.icono
import com.upd.kvupd.utils.maps.vectorToBitmapDescriptor
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.viewmodel.APIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BDDetalleBaja : BottomSheetDialogFragment() {

    private val apiViewModel by activityViewModels<APIViewModel>()
    private val args: BDDetalleBajaArgs by navArgs()
    private val binding by viewBinding(BottomDetallebajaBinding::bind)

    private var mapaListo = false
    private var lastGps: TableSeguimiento? = null
    private var markerCliente: Marker? = null
    private var markerUbicacion: Marker? = null
    private lateinit var detalle: FlowBajaSupervisor
    private val mapHelper by lazy { MapHelper(layoutInflater) }
    private val _tag by lazy { BDDetalleBaja::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detalle = args.detalle
    }

    override fun onDestroyView() {
        mapHelper.clearMarkers()
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        expandFullHeight()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = BottomDetallebajaBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMiniMapa()

        collectFlow(apiViewModel.flowLastGPS) { seguimiento ->
            lastGps = seguimiento
            tryDrawGps()
        }

        initViews()
    }

    private fun initMiniMapa() {
        val fragment = childFragmentManager
            .findFragmentById(R.id.minimap) as? SupportMapFragment
            ?: return

        viewLifecycleOwner.lifecycleScope.launch {
            val gMap = fragment.awaitMap()
            mapHelper.attachMap(gMap)
            configurarMiniMapa(gMap)

            mapaListo = true

            // 🔴 Crear marker del cliente una sola vez
            markerCliente = mapHelper.addMarker(
                data = detalle,
                lat = detalle.latitud,
                lng = detalle.longitud,
                icon = detalle.icono(requireContext())
            )

            tryDrawGps()
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

    private fun tryDrawGps() {
        if (!mapaListo) return

        lastGps?.let { gps ->

            val latLng = LatLng(gps.latitud, gps.longitud)

            if (markerUbicacion == null) {
                markerUbicacion = mapHelper.addMarker(
                    data = UbicacionActual,
                    lat = gps.latitud,
                    lng = gps.longitud,
                    icon = iconoUbicacion()
                )
            } else {
                markerUbicacion?.position = latLng
            }

            mapHelper.centerMarkersWithMaxZoom(
                includeLocation = latLng,
                maxZoom = 15f
            )
        }
    }

    private fun iconoUbicacion(): BitmapDescriptor =
        requireContext().vectorToBitmapDescriptor(R.drawable.cliente_anulado)

    private fun initViews() {

        detalle.let {
            val cliente = "${it.cliente} - ${it.nombre}"
            val vendedor = "${it.vendedor} - ${it.vendnom}"
            val canal = Canal.fromCodigo(it.canal)
            val motivo = MotivoBaja.fromId(it.motivo)
            val compra = "Ultima compra: ${it.compra}"

            binding.apply {
                txtCliente.text = cliente
                txtDireccion.text = it.direccion
                txtNegocio.text = it.negocio
                txtPago.text = it.pago
                txtVendedor.text = vendedor
                txtCompra.text = compra
                txtMotivo.text = motivo.label
                txtObservacion.text = it.observacion
            }

            binding.txtCanal.text = canal.codigo
            binding.txtCanal.setCompoundDrawablesWithIntrinsicBounds(
                canal.iconRes, 0, 0, 0
            )

            binding.btnDenegar.setOnClickListener { devolverDatosFragment(0) }
            binding.btnValidar.setOnClickListener { devolverDatosFragment(1) }
        }
    }

    private fun devolverDatosFragment(confirmacion: Int) {
        val comentario = binding.edtComentario.text.toString().trim()

        parentFragmentManager.setFragmentResult(
            KEY_DETALLE,
            bundleOf(
                "empleado" to detalle.vendedor,
                "cliente" to detalle.cliente,
                "procede" to confirmacion,
                "fecha" to detalle.creacion,
                "fechaconfirmacion" to FechaHoraUtil.ahora(),
                "observacion" to comentario
            )
        )

        dismiss()
    }
}