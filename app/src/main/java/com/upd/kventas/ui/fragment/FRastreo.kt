package com.upd.kventas.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.upd.kventas.R
import com.upd.kventas.data.model.Pedimap
import com.upd.kventas.databinding.FragmentFRastreoBinding
import com.upd.kventas.utils.*
import com.upd.kventas.utils.Constant.FIRST_LOCATION
import com.upd.kventas.utils.Constant.IWP
import com.upd.kventas.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.*

@AndroidEntryPoint
class FRastreo : Fragment(), OnMapReadyCallback, OnMarkerClickListener {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFRastreoBinding? = null
    private val bind get() = _bind!!
    private lateinit var sup: SupportMapFragment
    private lateinit var map: GoogleMap
    private lateinit var location: Location
    private lateinit var markers: List<Marker>
    private lateinit var pdmp: List<Pedimap>
    private val _tag by lazy { FRastreo::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        location = FIRST_LOCATION
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

        if (!::sup.isInitialized) {
            sup = childFragmentManager.findFragmentById(bind.map.id) as SupportMapFragment
            sup.getMapAsync(this)
        }

        launchDownload()

        viewmodel.lastLocation().distinctUntilChanged().observe(viewLifecycleOwner) { result ->
            location.longitude = result[0].longitud
            location.latitude = result[0].latitud
        }

        viewmodel.pedimap.observe(viewLifecycleOwner) { rsl ->
            when(rsl) {
                is Network.Success -> {
                    if (rsl.data?.jobl.isNullOrEmpty()) {
                        showDialog("Error", "No se obtuvieron vendedores") {}
                    }else {
                        showDialog("Correcto", "Se descargo vendedores") {}
                        map.clear()
                        pdmp = rsl.data!!.jobl
                        markers = viewmodel.pedimapMarker(map, pdmp)
                    }
                }
                is Network.Error -> showDialog("Error", "Server ${rsl.message}") {}
            }
        }

        bind.fabUbicacion.setOnClickListener { moveCamera(location) }
        bind.fabCentrar.setOnClickListener { centerMarkers() }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.rastreo_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.actualizar -> consume { launchDownload() }
        R.id.voz -> consume { searchVoice() }
        else -> super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        p0.also {
            map = it
            map.apply {
                settingsMap()
                isMyLocationEnabled = true
                setOnMarkerClickListener(this@FRastreo)
                setInfoWindowAdapter(InfoWindow(LayoutInflater.from(requireContext())))
            }
            moveCamera(location)
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        pedimapMarker(p0)
        return true
    }

    private fun centerMarkers() {
        val builder = LatLngBounds.Builder()
        if (::markers.isInitialized) {
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

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Mencione codigo del vendedor")
                resultLauncher.launch(intent)
            }
        }
    }

    private fun launchDownload() {
        val p = JSONObject()
        p.put("empleado", Constant.CONF.codigo)
        p.put("empresa", Constant.CONF.empresa)
        progress("Descargando vendedores")
        viewmodel.fetchPedimap(p.toReqBody())
    }
}