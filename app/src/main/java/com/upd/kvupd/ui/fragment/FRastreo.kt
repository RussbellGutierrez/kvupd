package com.upd.kvupd.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.upd.kvupd.R
import com.upd.kvupd.databinding.FragmentFRastreoBinding
import com.upd.kvupd.utils.OldConstant.CONF
import com.upd.kvupd.utils.awaitMap
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.progress
import com.upd.kvupd.utils.settingsMap
import com.upd.kvupd.utils.snack
import com.upd.kvupd.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Locale

@AndroidEntryPoint
class FRastreo : Fragment(), MenuProvider {

    private val binding by viewBinding(FragmentFRastreoBinding::bind)
    private var _map: GoogleMap? = null
    private val map get() = _map ?: error("Mapa aun no inicializado o ya destruido")
    private val _tag by lazy { FRastreo::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _map = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFRastreoBinding.inflate(inflater, container, false).root

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewLifecycleOwner.lifecycleScope.launch {
            val mapFragment = childFragmentManager.findFragmentById(binding.map.id)
                    as SupportMapFragment

            _map = mapFragment.awaitMap().apply {
                settingsMap()
                isMyLocationEnabled = true
                //setOnMarkerClickListener(this@FRastreo)
                //setInfoWindowAdapter(OldInfoWindow(LayoutInflater.from(requireContext())))
            }

            Log.d(_tag, "🗺️ Mapa inicializado correctamente (${_map.hashCode()})")

            //moveCamera(location)
            //launchDownload()
        }

        //val mapFragment = childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment
        //mapFragment.getMapAsync(this)

        /*if (!::sup.isInitialized) {
            sup = childFragmentManager.findFragmentById(bind.map.id) as SupportMapFragment
            sup.getMapAsync(this)
        }*/

        /*viewmodel.rutasObs().distinctUntilChanged().observe(viewLifecycleOwner) {
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
        }*/

        //bind.fabUbicacion.setOnClickListener { moveCamera(location) }
        //bind.fabCentrar.setOnClickListener { centerMarkers() }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.n_rastreo_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.actualizar -> consume { launchDownload() }
        R.id.voz -> consume { searchVoice() }
        else -> false
    }

    /*@SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        Log.d(_tag, "Iniciando mapa")
        p0.also {
            map = it
            map.apply {
                settingsMap()
                isMyLocationEnabled = true
                setOnMarkerClickListener(this@FRastreo)
                setInfoWindowAdapter(OldInfoWindow(LayoutInflater.from(requireContext())))
            }
            moveCamera(location)
            launchDownload()
        }
    }*/

    /*override fun onMarkerClick(p0: Marker): Boolean {
        pedimapMarker(p0)
        return true
    }*/

    /*private fun centerMarkers() {
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
    }*/

    private fun moveCamera(location: Location) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.longitude), 15f
            )
        )
    }

    /*private fun showMarker(search: String) {
        if (::markers.isInitialized) {
            val vendedor = markers.find { it.snippet == search }
            if (vendedor != null) {
                pedimapMarker(vendedor)
            } else {
                snack("No se encontro vendedor")
            }
        }
    }*/

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                /*var codigo =
                    result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!![0]
                codigo = codigo.replace("\\s".toRegex(), "")
                showMarker(codigo)*/
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
        //viewmodel.fetchPedimap(p.toReqBody())
    }

    private fun drawRoutes() {
        /*val polygon = mutableListOf<LatLng>()
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
        }*/
    }

}