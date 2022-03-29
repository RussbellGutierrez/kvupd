package com.upd.kvupd.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PolygonOptions
import com.upd.kvupd.R
import com.upd.kvupd.data.model.DataCliente
import com.upd.kvupd.data.model.TRutas
import com.upd.kvupd.databinding.FragmentFMapaBinding
import com.upd.kvupd.utils.*
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Constant.GPS_LOC
import com.upd.kvupd.utils.Constant.IWAM
import com.upd.kvupd.utils.Constant.PROCEDE
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class FMapa : Fragment(), OnMapReadyCallback, OnMarkerClickListener,
    OnInfoWindowClickListener, OnInfoWindowLongClickListener {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFMapaBinding? = null
    private val bind get() = _bind!!
    private lateinit var sup: SupportMapFragment
    private lateinit var map: GoogleMap
    private lateinit var location: Location
    private lateinit var markers: List<Marker>
    private lateinit var rutas: List<TRutas>
    private lateinit var mclk: Marker
    private val _tag by lazy { FMapa::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        location = GPS_LOC
    }

    override fun onResume() {
        super.onResume()
        PROCEDE = "Mapa"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentFMapaBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        viewmodel.detail.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                if (y.size > 1) {
                    searchList(y)
                } else {
                    IWAM = y[0]
                    moveCamera(mclk.position.toLocation())
                    mclk.showInfoWindow()
                }
            }
        }
        viewmodel.climap.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                showMarker(y)
            }
        }
        viewmodel.marker.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                if (::map.isInitialized) {
                    map.clear()
                    markers = viewmodel.setMarker(map, y)
                }
            }
        }

        bind.fabUbicacion.setOnClickListener { moveCamera(location) }
        bind.fabCentrar.setOnClickListener { centerMarkers() }
        backButton()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.mapa_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.lista -> consume { viewmodel.getClientDet("0") }
        R.id.voz -> consume { searchVoice() }
        android.R.id.home -> consume {
            when(CONF.tipo) {
                "V" -> findNavController().navigate(R.id.action_FMapa_to_FCliente)
                "S" -> findNavController().navigate(R.id.action_FMapa_to_FVendedor)
            }
        }
        else -> super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        p0.also {
            map = it
            map.apply {
                settingsMap()
                isMyLocationEnabled = true
                setOnMarkerClickListener(this@FMapa)
                setOnInfoWindowClickListener(this@FMapa)
                setOnInfoWindowLongClickListener(this@FMapa)
                setInfoWindowAdapter(InfoWindow(LayoutInflater.from(requireContext())))
            }
            drawRoutes()
            viewmodel.markerMap()
            moveCamera(location)
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        mclk = p0
        viewmodel.getClientDet(mclk.snippet!!)
        return true
    }

    override fun onInfoWindowClick(p0: Marker) {
        navigateToDialog(0, IWAM)
    }

    override fun onInfoWindowLongClick(p0: Marker) {
        navigateToDialog(1, IWAM)
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

    private fun moveCamera(location: Location) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.longitude), 15f
            )
        )
    }

    private fun showMarker(search: String) {
        val cliente = markers.find { it.snippet == search }
        if (cliente != null) {
            cliente.let {
                moveCamera(it.position.toLocation())
                viewmodel.getClientDet(it.snippet!!)
                mclk = it
            }
        } else {
            snack("No se encontro cliente, revisar en bajas")
        }
    }

    private val resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            var codigo = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!![0]
            codigo = codigo.replace("\\s".toRegex(), "")
            showMarker(codigo)
        } else {
            snack("Error procesando codigo")
        }
    }

    private fun searchVoice() {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).also { intent ->
            intent.resolveActivity(requireActivity().packageManager).also {
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Mencione codigo del cliente")
                resultLauncher.launch(intent)
            }
        }
    }

    private fun searchList(list: List<DataCliente>) {
        val dt = arrayListOf<String>()
        list.forEach { i ->
            val cliente = "${i.id} - ${i.nombre}"
            dt.add(cliente)
        }
        search(dt)
    }

    private fun navigateToDialog(dialog: Int, cliente: DataCliente) {
        val cli = "${cliente.id} - ${cliente.nombre} - ${cliente.ruta}"
        when (dialog) {
            0 -> when(CONF.tipo) {
                "V" -> findNavController().navigate(
                    FMapaDirections.actionFMapaToBDObservacion(cli)
                )
                "S" -> viewmodel.checkingEncuesta {
                    if (it) {
                        findNavController().navigate(
                            FMapaDirections.actionFMapaToBDObservacion(cli)
                        )
                    } else {
                        snack("Regrese a la lista y elija una encuesta")
                    }
                }
            }
            1 -> findNavController().navigate(
                FMapaDirections.actionFMapaToDBaja(cli)
            )
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

    private fun backButton() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when(CONF.tipo) {
                        "V" -> findNavController().navigate(R.id.action_FMapa_to_FCliente)
                        "S" -> findNavController().navigate(R.id.action_FMapa_to_FVendedor)
                    }
                }
            }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}