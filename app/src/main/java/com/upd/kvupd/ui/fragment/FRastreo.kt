package com.upd.kvupd.ui.fragment

import MapHelper
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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.upd.kvupd.R
import com.upd.kvupd.data.model.JsonPedimap
import com.upd.kvupd.data.model.Pedimap
import com.upd.kvupd.databinding.FragmentFRastreoBinding
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.ui.sealed.ResultadoApi
import com.upd.kvupd.utils.GPSConstants.GPS_INTERVALO_NORMAL
import com.upd.kvupd.utils.GPSConstants.GPS_INTERVALO_RAPIDO
import com.upd.kvupd.utils.GPSConstants.IGNORAR_METROS
import com.upd.kvupd.utils.GPSConstants.TRACKER_RAPIDO
import com.upd.kvupd.utils.GpsTracker
import com.upd.kvupd.utils.InstanciaDialog
import com.upd.kvupd.utils.MaterialDialogTexto.T_ERROR
import com.upd.kvupd.utils.MaterialDialogTexto.T_SUCCESS
import com.upd.kvupd.utils.awaitMap
import com.upd.kvupd.utils.buildMaterialDialog
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.icono
import com.upd.kvupd.utils.snack
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.viewmodel.APIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class FRastreo : Fragment(), MenuProvider {

    private val apiViewmodel by activityViewModels<APIViewModel>()
    private val binding by viewBinding(FragmentFRastreoBinding::bind)

    private lateinit var mapHelper: MapHelper
    private var getLocation: Location? = null
    private var movedOnce = false
    private val _tag by lazy { FRastreo::class.java.simpleName }

    @Inject
    lateinit var gpsTracker: GpsTracker

    override fun onDestroyView() {
        super.onDestroyView()
        gpsTracker.stopTracking(TRACKER_RAPIDO)

        mapHelper.disableMyLocation()
        mapHelper.clearMarkers()
        mapHelper.clearPolygons()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFRastreoBinding.inflate(inflater, container, false).root

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        mapHelper = MapHelper(layoutInflater)

        val mapFragment =
            childFragmentManager.findFragmentById(binding.map.id) as SupportMapFragment

        collectFlow(apiViewmodel.pedimapEvent) collect@{ resultado ->
            when (resultado) {
                is ResultadoApi.Loading -> mostrarDialog(
                    AppDialogType.Progreso(
                        mensaje = "Obteniendo posiciones de pedimap"
                    )
                )

                is ResultadoApi.Exito -> {
                    stateSuccess(resultado.data)
                }

                is ResultadoApi.ErrorHttp -> mostrarDialog(
                    AppDialogType.Informativo(
                        titulo = T_ERROR,
                        mensaje = "Error HTTP ${resultado.code}: ${resultado.mensaje}"
                    )
                )

                is ResultadoApi.Fallo -> mostrarDialog(
                    AppDialogType.Informativo(
                        titulo = T_ERROR,
                        mensaje = "Fallo: ${resultado.mensaje}"
                    )
                )
            }
        }

        collectFlow(apiViewmodel.flowPolygon) { polygons ->
            Log.d(_tag, "Poligonos $polygons")
            mapHelper.clearPolygons()
            polygons.forEach { ruta ->
                val latLngs = ruta.corte.split(",")
                    .mapNotNull { punto ->
                        val parts = punto.trim().split(" ").filter { it.isNotBlank() }
                        if (parts.size < 2) return@mapNotNull null

                        val lng = parts[0].toDoubleOrNull()
                        val lat = parts[1].toDoubleOrNull()

                        if (lat != null && lng != null) LatLng(lat, lng) else null
                    }

                // Este dibujado será guardado si el mapa no está listo
                mapHelper.drawPolygon(latLngs)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val gMap = mapFragment.awaitMap()

            mapHelper.attachMap(gMap)
            mapHelper.enableMyLocation()

            launchGpsRastreo()
            apiViewmodel.downloadPedimap()
        }

        binding.fabUbicacion.setOnClickListener {
            getLocation?.let {
                mapHelper.moveCamera(it)
            }
        }

        binding.fabCentrar.setOnClickListener {
            mapHelper.centerMarkers(
                includeLocation = getLocation?.let { LatLng(it.latitude, it.longitude) }
            )
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.n_rastreo_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.actualizar -> consume { apiViewmodel.downloadPedimap() }
        R.id.voz -> consume { searchVoice() }
        else -> false
    }

    private fun launchGpsRastreo() {
        gpsTracker.startTracking(
            id = TRACKER_RAPIDO,
            interval = GPS_INTERVALO_NORMAL,
            fastest = GPS_INTERVALO_RAPIDO,
            minDistance = IGNORAR_METROS,
            onLocation = { location ->
                Log.d(_tag, "📍 Posición fragment: $location")

                getLocation = location
                if (!movedOnce) {
                    mapHelper.moveCamera(location)
                    movedOnce = true
                }
            },
            onError = { error ->
                Log.e(_tag, "Error de GPS rápido: $error")
            }
        )
    }

    private fun moveToMarkerByCode(codigo: String) {
        val pedimap = mapHelper
            .getMarkerData(Pedimap::class.java)
            .firstOrNull { it.codigo == codigo }

        if (pedimap != null) {
            mapHelper.focus(pedimap)
        } else {
            snack("No se encontró el vendedor con código $codigo")
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val spokenText = result.data
                    ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    ?.firstOrNull()
                    ?.replace("\\s".toRegex(), "")

                if (spokenText.isNullOrEmpty()) {
                    snack("No se reconocio ninguna palabra")
                    return@registerForActivityResult
                }

                moveToMarkerByCode(spokenText)
            } else {
                snack("Error procesando codigo de voz")
            }
        }

    private fun searchVoice() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Mencione código del vendedor")
            }
            resultLauncher.launch(intent)
        } catch (e: Exception) {
            snack("El reconocimiento de voz no está disponible en este dispositivo")
        }
    }

    private fun stateSuccess(pedimap: JsonPedimap?) {
        when {
            pedimap == null -> {
                mostrarDialog(
                    AppDialogType.Informativo(
                        titulo = T_ERROR,
                        mensaje = "No se obtuvo respuesta del servidor"
                    )
                )
            }

            pedimap.jobl.isEmpty() -> {
                mostrarDialog(
                    AppDialogType.Informativo(
                        titulo = T_ERROR,
                        mensaje = "No se encontraron posiciones en Pedimap"
                    )
                )
            }

            else -> {
                mostrarDialog(
                    AppDialogType.Informativo(
                        titulo = T_SUCCESS,
                        mensaje = "Se descargaron ${pedimap.jobl.size} posiciones correctamente"
                    )
                )

                drawMarkers(pedimap.jobl)
            }
        }
    }

    private fun mostrarDialog(dialogType: AppDialogType) {
        lifecycleScope.launch(Dispatchers.Main) {
            // Cerrar diálogo previo si existe
            InstanciaDialog.cerrarDialogActual()

            // Crear el dialog
            val dialog = buildMaterialDialog(requireContext(), dialogType)

            // Mostrarlo
            dialog.show()

            // Guardar referencia
            InstanciaDialog.REFERENCIA_DIALOG = WeakReference(dialog)
        }
    }

    private fun drawMarkers(vendedores: List<Pedimap>) {
        mapHelper.clearMarkers()

        vendedores.forEach { item ->
            mapHelper.addMarker(
                data = item,
                lat = item.posicion.latitud,
                lng = item.posicion.longitud,
                icon = item.icono(requireContext())
            )
        }
    }
}