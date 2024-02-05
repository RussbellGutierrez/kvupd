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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowLongClickListener
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PolygonOptions
import com.upd.kvupd.R
import com.upd.kvupd.data.model.TRutas
import com.upd.kvupd.databinding.FragmentFAltaMapaBinding
import com.upd.kvupd.utils.Constant
import com.upd.kvupd.utils.Constant.ALTADATOS
import com.upd.kvupd.utils.Constant.GPS_LOC
import com.upd.kvupd.utils.Constant.IWDA
import com.upd.kvupd.utils.Constant.PROCEDE
import com.upd.kvupd.utils.InfoWindow
import com.upd.kvupd.utils.settingsMap
import com.upd.kvupd.utils.showDialog
import com.upd.kvupd.utils.snack
import com.upd.kvupd.utils.toLocation
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FAltaMapa : Fragment(), OnMapReadyCallback, OnMapLongClickListener, OnMarkerClickListener,
    OnInfoWindowLongClickListener, OnMarkerDragListener {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFAltaMapaBinding? = null
    private val bind get() = _bind!!
    private var snippet = ""
    private lateinit var sup: SupportMapFragment
    private lateinit var map: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var markerList: List<Marker>
    private lateinit var rutas: List<TRutas>
    private val _tag by lazy { FAltaMapa::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            if (!it.isNullOrEmpty()) {
                lastLocation = LatLng(it[0].latitud, it[0].longitud).toLocation()
            }
        }

        viewmodel.altasObs().distinctUntilChanged().observe(viewLifecycleOwner) {
            if (::map.isInitialized) {
                map.clear()
                markerList = viewmodel.altaMarker(map, it)
                drawRoutes()
            }
        }

        viewmodel.altamark.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                IWDA = y
                movingAndShowing()
            }
        }

        bind.fabUbicacion.setOnClickListener { moveCamera() }
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
            map.setOnMapLoadedCallback { moveCamera() }
        }
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
        showDialog("Advertencia", "¿Poner un alta en la ubicacion marcada?") {
            viewmodel.addingAlta(
                p0.toLocation()
            )
        }
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
        val m = markerList.find { it.snippet == snippet }
        val location = m!!.position.toLocation()
        if (::map.isInitialized) {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude), 17f
                )
            )
        }
        m.showInfoWindow()
    }

    private fun moveCamera() {
        val location = if (Constant.isGPSLOCinitialized()) {
            GPS_LOC
        } else {
            lastLocation
        }
        if (::map.isInitialized) {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(location.latitude, location.longitude), 15f
                )
            )
        }
    }

    private fun centerMarkers() {
        val builder = LatLngBounds.Builder()
        if (::markerList.isInitialized) {
            if (markerList.isNotEmpty()) {
                markerList.forEach { i -> builder.include(i.position) }
                builder.include(LatLng(GPS_LOC.latitude, GPS_LOC.longitude))
                val bounds = builder.build()
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200))
            } else {
                snack("Sin marcadores para ubicar")
            }
        } else {
            snack("No se inicializaron los marcadores")
        }
    }

    private fun drawRoutes() {
        val polygon = mutableListOf<LatLng>()
        if (::rutas.isInitialized && rutas.isNotEmpty()) {
            rutas.forEach { i ->
                val coordenadas = i.corte.split(",")
                if (coordenadas.isNotEmpty()) {
                    coordenadas.forEach { j ->
                        val item = j.trim().split(" ")
                        if (item.size >= 2) {
                            polygon.add(LatLng(item[1].toDouble(), item[0].toDouble()))
                        }
                    }
                    if (polygon.isNotEmpty()) {
                        map.addPolygon(
                            PolygonOptions()
                                .addAll(polygon)
                                .strokeWidth(2f)
                                .strokeColor(Color.parseColor("#D01215"))
                                .fillColor(Color.argb(102, 118, 131, 219))
                        )
                        polygon.clear()
                    }
                }
            }
        } else {
            snack("No se encontraron rutas")
        }
    }
}