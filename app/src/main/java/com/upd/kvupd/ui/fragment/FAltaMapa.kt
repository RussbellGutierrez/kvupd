package com.upd.kvupd.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.upd.kvupd.R
import com.upd.kvupd.data.model.TRutas
import com.upd.kvupd.databinding.FragmentFAltaMapaBinding
import com.upd.kvupd.utils.*
import com.upd.kvupd.utils.Constant.ALTADATOS
import com.upd.kvupd.utils.Constant.GPS_LOC
import com.upd.kvupd.utils.Constant.IWDA
import com.upd.kvupd.utils.Constant.PROCEDE
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FAltaMapa : Fragment(), OnMapReadyCallback, OnMapLongClickListener, OnMarkerClickListener,
    OnInfoWindowLongClickListener, OnMarkerDragListener {

    private val viewmodel by activityViewModels<AppViewModel>()
    private val args: FAltaMapaArgs by navArgs()
    private var _bind: FragmentFAltaMapaBinding? = null
    private val bind get() = _bind!!
    private var snippet = ""
    private lateinit var sup: SupportMapFragment
    private lateinit var map: GoogleMap
    private lateinit var location: Location
    private lateinit var markers: List<Marker>
    private lateinit var rutas: List<TRutas>
    private val _tag by lazy { FAltaMapa::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        location = GPS_LOC
    }

    override fun onResume() {
        super.onResume()
        PROCEDE = "MapaAlta"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentFAltaMapaBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ALTADATOS = "mapa"

        if (!::sup.isInitialized) {
            sup = childFragmentManager.findFragmentById(bind.map.id) as SupportMapFragment
            sup.getMapAsync(this)
        }

        viewmodel.rutasObs().distinctUntilChanged().observe(viewLifecycleOwner) {
            rutas = it
        }

        viewmodel.lastLocation().distinctUntilChanged().observe(viewLifecycleOwner) {
            location.longitude = it[0].longitud
            location.latitude = it[0].latitud
        }

        viewmodel.altasObs().distinctUntilChanged().observe(viewLifecycleOwner) {
            map.clear()
            markers = viewmodel.altaMarker(map, it)
            drawRoutes()
        }

        viewmodel.altamark.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                IWDA = y
                movingAndShowing()
            }
        }

        bind.fabUbicacion.setOnClickListener { moveCamera(location) }
        bind.fabCentrar.setOnClickListener { centerMarkers() }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        p0.also {
            map = it
            map.apply {
                settingsMap()
                isMyLocationEnabled = true
                setOnMapLongClickListener(this@FAltaMapa)
                setOnMarkerClickListener(this@FAltaMapa)
                setOnMarkerDragListener(this@FAltaMapa)
                setOnInfoWindowLongClickListener(this@FAltaMapa)
                setInfoWindowAdapter(InfoWindow(LayoutInflater.from(requireContext())))
            }
            moveCamera(location)
        }
        comingDatos()
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        snippet = p0.snippet!!
        viewmodel.getAltaData(snippet)
        return true
    }

    override fun onInfoWindowLongClick(p0: Marker) {
        findNavController().navigate(
            FAltaMapaDirections.actionFAltaMapaToFAltaDatos(p0.snippet!!.toInt())
        )
    }

    override fun onMapLongClick(p0: LatLng) {
        showDialog("Advertencia", "¿Poner un alta en la ubicacion marcada?") { viewmodel.addingAlta(p0.toLocation()) }
    }

    override fun onMarkerDragStart(p0: Marker) {
        p0.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_mover))
        snack("Elija una nueva ubicacion")
    }

    override fun onMarkerDrag(p0: Marker) = Unit

    override fun onMarkerDragEnd(p0: Marker) {
        p0.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pin_altas))
        viewmodel.updateLocationAlta(p0)
    }

    private fun movingAndShowing() {
        val m = markers.find { it.snippet == snippet }
        moveCamera(m!!.position.toLocation())
        m.showInfoWindow()
    }

    private fun comingDatos() {
        args.idaux.let {
            if (it > 0) {
                snippet = it.toString()
                viewmodel.getAltaData(snippet)
            }
        }
    }

    private fun moveCamera(location: Location) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.longitude), 15f
            )
        )
    }

    private fun centerMarkers() {
        val builder = LatLngBounds.Builder()
        when {
            markers.isNotEmpty() -> {
                markers.forEach { i -> builder.include(i.position) }
                builder.include(LatLng(location.latitude, location.longitude))
                val bounds = builder.build()
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200))
            }
            markers.isEmpty() -> snack("Sin marcadores para ubicar")
        }
    }

    private fun drawRoutes() {
        val polygon = mutableListOf<LatLng>()
        if (::rutas.isInitialized && !rutas.isNullOrEmpty()) {
            rutas.forEach { i ->
                val coordenadas = i.corte.split(",")
                coordenadas.forEach { j ->
                    val item = j.trim().split(" ")
                    polygon.add(LatLng(item[1].toDouble(), item[0].toDouble()))
                }
                map.addPolygon(
                    PolygonOptions()
                        .addAll(polygon)
                        .strokeWidth(2f)
                        .strokeColor(Color.parseColor("#D01215"))
                        .fillColor(Color.argb(102, 118, 131, 219))
                )
                polygon.clear()
            }
        } else {
            snack("No se encontraron rutas")
        }
    }
}