package com.upd.kvupd.ui.fragment.altas

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.upd.kvupd.R
import com.upd.kvupd.data.model.TableAlta
import com.upd.kvupd.data.model.TableWrapper
import com.upd.kvupd.databinding.FragmentFAltaBinding
import com.upd.kvupd.ui.fragment.altas.adapter.AltaAdapter
import com.upd.kvupd.ui.fragment.altas.adapter.AltaAdapterFactory
import com.upd.kvupd.ui.fragment.altas.enumAlta.VistaAlta
import com.upd.kvupd.ui.fragment.altas.sealed.AltaResult
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.utils.GPSConstants.GPS_INTERVALO_NORMAL
import com.upd.kvupd.utils.GPSConstants.GPS_INTERVALO_RAPIDO
import com.upd.kvupd.utils.GPSConstants.IGNORAR_METROS
import com.upd.kvupd.utils.GPSConstants.TRACKER_RAPIDO
import com.upd.kvupd.utils.InstanciaDialog.REFERENCIA_DIALOG
import com.upd.kvupd.utils.InstanciaDialog.cerrarDialogActual
import com.upd.kvupd.utils.MaterialDialogTexto.T_ERROR
import com.upd.kvupd.utils.MaterialDialogTexto.T_WARNING
import com.upd.kvupd.utils.buildMaterialDialog
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.gps.GpsTracker
import com.upd.kvupd.utils.maps.MapHelper
import com.upd.kvupd.utils.maps.awaitMap
import com.upd.kvupd.utils.maps.icono
import com.upd.kvupd.utils.observeResult
import com.upd.kvupd.utils.snack
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.utils.visibleIf
import com.upd.kvupd.viewmodel.APIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class FAlta : Fragment(), AltaAdapter.Listener, MenuProvider {

    private val apiViewModel by activityViewModels<APIViewModel>()
    private val binding by viewBinding(FragmentFAltaBinding::bind)

    private lateinit var adapter: AltaAdapter
    private val mapHelper by lazy { MapHelper(layoutInflater) }

    private var altaCache: List<TableAlta> = emptyList()
    private var vistaActual: VistaAlta = VistaAlta.LISTA
    private var getLocation: Location? = null
    private var movedOnce = false
    private var mapaInicializado = false

    private val _tag by lazy { FAlta::class.java.simpleName }

    @Inject
    lateinit var adapterFactory: AltaAdapterFactory

    @Inject
    lateinit var gpsTracker: GpsTracker

    override fun onDestroyView() {
        super.onDestroyView()
        stopGps()
        mapHelper.clearMarkers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFAltaBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        adapter = adapterFactory.create(listener = this)
        binding.rcvAlta.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvAlta.adapter = adapter

        startGps()
        setupButtons()
        setupMapActions()
        observerData()
        messageFromAltaDatos()
    }

    override fun onStart() {
        super.onStart()
        renderContentUI(vistaActual)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.n_alta_menu, menu)

        toggleMenu(menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.mapa -> consume { toggleVista() }
        R.id.lista -> consume { toggleVista() }
        else -> false
    }

    override fun onLongClick(alta: TableAlta) {
        navigateTo(alta)
    }

    private fun setupButtons() {
        binding.apply {

            fabCentrar.setOnClickListener {
                mapHelper.centerMarkers(
                    includeLocation = getLocation?.let { LatLng(it.latitude, it.longitude) }
                )
            }

            fabUbicacion.setOnClickListener {
                getLocation?.let {
                    mapHelper.moveCamera(it)
                }
            }

            btnAlta.setOnClickListener {
                passParameters()
            }
        }
    }

    private fun setupMapActions() {
        mapHelper.setOnMapClickListener { latLng ->
            val location = Location("").apply {
                latitude = latLng.latitude
                longitude = latLng.longitude
                accuracy = getLocation?.accuracy ?: 0f
            }

            preventAltaRandom(location)
        }

        mapHelper.setOnInfoWindowClickListener(
            TableWrapper::class.java,
            object : MapHelper.OnInfoWindowClickListener<TableWrapper<*>> {
                override fun onClick(data: TableWrapper<*>) {
                    data.onInfoClick?.invoke()
                }
            }
        )

        mapHelper.setOnMarkerMovedListener(
            TableWrapper::class.java,
            object : MapHelper.OnMarkerMovedListener<TableWrapper<*>> {

                override fun onMoved(data: TableWrapper<*>, position: LatLng) {

                    val alta = data.data as? TableAlta ?: return

                    val actualizado = alta.copy(
                        latitud = position.latitude,
                        longitud = position.longitude,
                        precision = getLocation?.accuracy?.toDouble() ?: 0.0
                    )

                    snack("Ubicación actualizada")
                    apiViewModel.retrySendAlta(actualizado)
                }
            }
        )
    }

    private fun observerData() {
        collectFlow(apiViewModel.flowAlta) { lista ->
            altaCache = lista
            renderContentUI(vistaActual)
        }

        collectFlow(apiViewModel.altaMessage) { mensaje ->
            mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = mensaje
                )
            )
        }
    }

    private fun navigateTo(item: TableAlta) {
        val action = FAltaDirections
            .actionFAltaToFAltaDatos(
                empleado = item.empleado,
                idaux = item.idaux,
                fecha = item.fecha,
                longitud = item.longitud.toFloat(),
                latitud = item.latitud.toFloat()
            )
        findNavController().navigate(action)
    }

    private fun messageFromAltaDatos() {
        observeResult<AltaResult>("alta_result") { result ->

            when (result) {
                is AltaResult.Success -> Unit

                is AltaResult.Error -> {
                    mostrarDialog(
                        AppDialogType.Informativo(
                            titulo = T_ERROR,
                            mensaje = result.mensaje
                        )
                    )
                }
            }
        }
    }

    private fun passParameters() {
        val location = getLocation ?: run {
            snack("No se obtuvieron coordenadas")
            return
        }

        preventAltaRandom(location)
    }

    private fun preventAltaRandom(location: Location) {
        mostrarDialog(
            AppDialogType.Informativo(
                titulo = T_WARNING,
                mensaje = "Desea tomar un alta?",
                mostrarNegativo = true,
                onPositive = {
                    apiViewModel.createAlta(location)
                }
            )
        )
    }

    private fun renderContentUI(vista: VistaAlta) {
        binding.consAlta.visibleIf(vista == VistaAlta.LISTA)
        binding.rltMapa.visibleIf(vista == VistaAlta.MAPA)

        when (vista) {
            VistaAlta.LISTA -> renderLista()
            VistaAlta.MAPA -> drawMarkers()
        }
    }

    private fun renderLista() {
        val hayDatos = altaCache.isNotEmpty()
        binding.rcvAlta.visibleIf(hayDatos)
        binding.emptyContainer.root.visibleIf(!hayDatos)
        adapter.submitList(altaCache)
    }

    private fun drawMarkers() {
        initMapaSiEsNecesario { _ ->
            mapHelper.clearMarkers()
            altaCache.forEach { item ->
                val wrapper = TableWrapper(
                    data = item,
                    mapId = item.idaux,
                    onInfoClick = {
                        navigateTo(item)
                    }
                )

                val snip = when (item.datos) {
                    0 -> "Alta no tiene datos"
                    else -> "Alta tiene datos"
                }

                mapHelper.addMarker(
                    data = wrapper,
                    lat = item.latitud,
                    lng = item.longitud,
                    icon = wrapper.icono(requireContext(), R.drawable.pin_altas),
                    movable = true,
                    title = item.idaux,
                    snippet = snip
                )
            }
            startGps() // activa MyLocation y lanza rastreo
            mapHelper.resolvePendingFocus()
        }
    }

    private fun toggleVista() {
        val siguiente =
            if (vistaActual == VistaAlta.LISTA) VistaAlta.MAPA else VistaAlta.LISTA
        cambiarVista(siguiente)
    }

    private fun toggleMenu(menu: Menu) {
        val esLista = vistaActual == VistaAlta.LISTA

        menu.findItem(R.id.mapa).isVisible = esLista
        menu.findItem(R.id.lista).isVisible = !esLista
    }

    private fun cambiarVista(nuevaVista: VistaAlta) {
        vistaActual = nuevaVista

        if (nuevaVista == VistaAlta.LISTA) {
            stopGps()
        }

        requireActivity().invalidateOptionsMenu()
        renderContentUI(nuevaVista)
    }

    private fun startGps() {
        mapHelper.enableMyLocation()
        launchGpsRastreo()
    }

    private fun stopGps() {
        gpsTracker.stopTracking(TRACKER_RAPIDO)
        mapHelper.disableMyLocation()
    }

    private fun launchGpsRastreo() {
        gpsTracker.startTracking(
            id = TRACKER_RAPIDO,
            interval = GPS_INTERVALO_NORMAL,
            fastest = GPS_INTERVALO_RAPIDO,
            minDistance = IGNORAR_METROS,
            onLocation = { location ->
                getLocation = location
                if (!movedOnce) mapHelper.moveCamera(location).also { movedOnce = true }
            },
            onError = { error -> Log.e(_tag, "Error GPS: $error") }
        )
    }

    private fun initMapaSiEsNecesario(onReady: (GoogleMap) -> Unit = {}) {
        val fragment =
            childFragmentManager.findFragmentById(R.id.map_alta) as? SupportMapFragment ?: return

        if (mapaInicializado) {
            mapHelper.getMap()?.let { onReady(it) }
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val gMap = fragment.awaitMap()
            mapHelper.attachMap(gMap)
            mapaInicializado = true
            onReady(gMap)
        }
    }

    private fun mostrarDialog(dialogType: AppDialogType) {
        lifecycleScope.launch(Dispatchers.Main) {
            cerrarDialogActual()
            val dialog = buildMaterialDialog(requireContext(), dialogType)
            dialog.show()
            REFERENCIA_DIALOG = WeakReference(dialog)
        }
    }
}
