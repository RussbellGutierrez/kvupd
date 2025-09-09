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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.distinctUntilChanged
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PolygonOptions
import com.upd.kvupd.R
import com.upd.kvupd.data.model.Pedimap
import com.upd.kvupd.data.model.TRutas
import com.upd.kvupd.databinding.FragmentFRastreoBinding
import com.upd.kvupd.utils.*
import com.upd.kvupd.utils.OldConstant.CONF
import com.upd.kvupd.utils.OldConstant.GPS_LOC
import com.upd.kvupd.utils.OldConstant.IWP
import com.upd.kvupd.viewmodel.OldAppViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.*

@AndroidEntryPoint
class OldFRastreo : Fragment(), OnMapReadyCallback, OnMarkerClickListener, MenuProvider {

    private val viewmodel by activityViewModels<OldAppViewModel>()
    private var _bind: FragmentFRastreoBinding? = null
    private val bind get() = _bind!!
    private lateinit var sup: SupportMapFragment
    private lateinit var map: GoogleMap
    private lateinit var location: Location
    private lateinit var markers: List<Marker>
    private lateinit var pdmp: List<Pedimap>
    private lateinit var rutas: List<TRutas>
    private val _tag by lazy { OldFRastreo::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        location = GPS_LOC
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentFRastreoBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        if (!::sup.isInitialized) {
            sup = childFragmentManager.findFragmentById(bind.map.id) as SupportMapFragment
            sup.getMapAsync(this)
        }

        viewmodel.rutasObs().distinctUntilChanged().observe(viewLifecycleOwner) {
            rutas = it
        }

        viewmodel.lastLocation().distinctUntilChanged().observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                location.longitude = it[0].longitud
                location.latitude = it[0].latitud
            }
        }

        viewmodel.pedimap.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is OldNetworkRetrofit.Success -> {
                        if (y.data?.jobl.isNullOrEmpty()) {
                            showDialog("Error", "No se obtuvieron vendedores") {}
                        } else {
                            showDialog("Correcto", "Se descargo vendedores") {}
                            map.clear()
                            pdmp = y.data!!.jobl
                            markers = viewmodel.pedimapMarker(map, pdmp)
                            drawRoutes()
                        }
                    }

                    is OldNetworkRetrofit.Error -> showDialog("Error", "Server ${y.message}") {}
                }
            }
        }

        bind.fabUbicacion.setOnClickListener { moveCamera(location) }
        bind.fabCentrar.setOnClickListener { centerMarkers() }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.rastreo_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.actualizar -> consume { launchDownload() }
        R.id.voz -> consume { searchVoice() }
        else -> false
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        Log.d(_tag, "Iniciando mapa")
        p0.also {
            map = it
            map.apply {
                settingsMap()
                isMyLocationEnabled = true
                setOnMarkerClickListener(this@OldFRastreo)
                setInfoWindowAdapter(OldInfoWindow(LayoutInflater.from(requireContext())))
            }
            moveCamera(location)
            launchDownload()
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        pedimapMarker(p0)
        return true
    }

    private fun centerMarkers() {
        val builder = LatLngBounds.Builder()
        if (::markers.isInitialized && markers.isNotEmpty()) {

            markers.forEach { i -> builder.include(i.position) }
            builder.include(LatLng(location.latitude, location.longitude))
            val bounds = builder.build()
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200))
        } else {
            snack("No se detectan marcadores")
        }
    }

    private fun pedimapMarker(marker: Marker) {
        IWP = pdmp.find { it.codigo.toString() == marker.snippet }!!
        marker.showInfoWindow()
        moveCamera(marker.position.toLocation())
    }

    private fun moveCamera(location: Location) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.longitude), 15f
            )
        )
    }

    private fun showMarker(search: String) {
        if (::markers.isInitialized) {
            val vendedor = markers.find { it.snippet == search }
            if (vendedor != null) {
                pedimapMarker(vendedor)
            } else {
                snack("No se encontro vendedor")
            }
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                var codigo =
                    result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!![0]
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
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Mencione codigo del vendedor")
                resultLauncher.launch(intent)
            }
        }
    }

    private fun launchDownload() {
        val p = JSONObject()
        p.put("empleado", CONF.codigo)
        p.put("empresa", CONF.empresa)
        p.put("esquema", CONF.esquema)
        println(p)
        progress("Descargando vendedores")
        viewmodel.fetchPedimap(p.toReqBody())
    }

    private fun drawRoutes() {
        val polygon = mutableListOf<LatLng>()
        if (::rutas.isInitialized && rutas.isNotEmpty()) {
            rutas.forEach { i ->
                if (i.corte != "") {
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
            }
        } else {
            snack("No se encontraron rutas")
        }
    }

}