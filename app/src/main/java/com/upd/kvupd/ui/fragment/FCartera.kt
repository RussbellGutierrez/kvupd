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
import androidx.appcompat.widget.SearchView.OnQueryTextListener
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
import com.upd.kvupd.data.model.BajaAux
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.data.model.JsonCliente
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.data.model.TableVendedor
import com.upd.kvupd.databinding.FragmentFCarteraBinding
import com.upd.kvupd.ui.adapter.ClienteAdapter
import com.upd.kvupd.ui.adapter.ClienteAdapterFactory
import com.upd.kvupd.ui.dialog.CarteraVendedor
import com.upd.kvupd.ui.dialog.ListaClientesMapa
import com.upd.kvupd.ui.fragment.enumClass.Vista
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.ui.sealed.BajaEstados
import com.upd.kvupd.ui.sealed.ResultadoApi
import com.upd.kvupd.utils.BajaConstantes.KEY_BAJA
import com.upd.kvupd.utils.BajaConstantes.PAIR_BAJA
import com.upd.kvupd.utils.FechaHoraUtil
import com.upd.kvupd.utils.GPSConstants.GPS_INTERVALO_NORMAL
import com.upd.kvupd.utils.GPSConstants.GPS_INTERVALO_RAPIDO
import com.upd.kvupd.utils.GPSConstants.IGNORAR_METROS
import com.upd.kvupd.utils.GPSConstants.TRACKER_RAPIDO
import com.upd.kvupd.utils.GPSConstants.TRACKER_TEMPORAL
import com.upd.kvupd.utils.GpsTracker
import com.upd.kvupd.utils.InstanciaDialog
import com.upd.kvupd.utils.MaterialDialogTexto.T_ERROR
import com.upd.kvupd.utils.MaterialDialogTexto.T_SUCCESS
import com.upd.kvupd.utils.MaterialDialogTexto.T_WARNING
import com.upd.kvupd.utils.awaitMap
import com.upd.kvupd.utils.buildMaterialDialog
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.getParcelableCompat
import com.upd.kvupd.utils.icono
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.utils.snack
import com.upd.kvupd.utils.to2Decimals
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.viewmodel.ALLViewModel
import com.upd.kvupd.viewmodel.APIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class FCartera : Fragment(), OnQueryTextListener, ClienteAdapter.Listener,
    MenuProvider {

    private val apiViewModel by activityViewModels<APIViewModel>()
    private val localViewmodel by activityViewModels<ALLViewModel>()
    private val binding by viewBinding(FragmentFCarteraBinding::bind)

    private lateinit var adapter: ClienteAdapter
    private val mapHelper by lazy { MapHelper(layoutInflater) }
    private var bajaEstado: BajaEstados = BajaEstados.Reposo
    private var vistaActual: Vista = Vista.LISTA
    private var getLocation: Location? = null
    private var clientesCache: List<FlowCliente> = emptyList()
    private var vendedorList: List<TableVendedor> = emptyList()
    private var movedOnce = false
    private var mapaInicializado = false
    private val _tag by lazy { FCartera::class.java.simpleName }

    @Inject
    lateinit var adapterFactory: ClienteAdapterFactory

    @Inject
    lateinit var gpsTracker: GpsTracker

    override fun onDestroyView() {
        super.onDestroyView()
        stopGps()
        mapHelper.clearMarkers()
        mapHelper.clearPolygons()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFCarteraBinding.inflate(inflater, container, false).root

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        adapter = adapterFactory.create(listener = this, hoy = FechaHoraUtil.dia())
        binding.rcvCartera.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvCartera.adapter = adapter
        binding.searchview.setOnQueryTextListener(this)

        setupButtons()
        collectFlows()

        resultadoBajaDialogo()

        collectFlow(apiViewModel.bajaMessage) collect@{ mensaje ->
            mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = mensaje
                )
            )
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.n_cartera_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.descargar -> consume { carteraPorVendedor() }
        R.id.voz -> consume { searchVoice() }
        R.id.cambiar -> consume { toggleVista() }
        else -> false
    }

    override fun onQueryTextSubmit(p0: String) = false
    override fun onQueryTextChange(p0: String): Boolean {
        localViewmodel.setQuery(p0)
        return true
    }

    override fun onClick(cliente: FlowCliente) {
        mostrarDialog(AppDialogType.Informativo(
            titulo = "Ubicar en mapa",
            mensaje = "¿Desea ubicar el cliente en el mapa?",
            mostrarNegativo = true,
            onPositive = {
                toggleVista()
                movedOnce = true
                mapHelper.focus(cliente)
            }
        ))
    }

    override fun onLongClick(cliente: FlowCliente) {
        if (cliente.baja > 0) {
            mostrarClienteDadoDeBaja()
            return
        }

        navegarABaja(cliente)
    }

    private fun mostrarClienteDadoDeBaja() {
        mostrarDialog(
            AppDialogType.Informativo(
                titulo = T_WARNING,
                mensaje = "El cliente fue dado de baja, si fue por error, puede anularlo desde 'Administrar Bajas'"
            )
        )
    }

    private fun navegarABaja(cliente: FlowCliente) {
        val action = FCarteraDirections
            .actionFCarteraToBDBajaCliente(cliente)
        findNavController().navigate(action)
    }

    private fun setupButtons() {
        binding.fabLista.setOnClickListener {
            focusClienteEnMapa()
        }

        binding.fabCentrar.setOnClickListener {
            mapHelper.centerMarkers(
                includeLocation = getLocation?.let { LatLng(it.latitude, it.longitude) }
            )
        }

        binding.fabUbicacion.setOnClickListener {
            getLocation?.let {
                mapHelper.moveCamera(it)
            }
        }

        mapHelper.setOnInfoWindowClickListener(
            FlowCliente::class.java,
            object : MapHelper.OnInfoWindowClickListener<FlowCliente> {
                override fun onClick(data: FlowCliente) {
                    val action = FCarteraDirections.actionFCarteraToBDBajaCliente(data)
                    findNavController().navigate(action)
                }
            }
        )
    }

    private fun collectFlows() {
        collectFlow(apiViewModel.clienteEvent) { resultado ->
            when (resultado) {
                is ResultadoApi.Loading -> mostrarDialog(AppDialogType.Progreso("Obteniendo lista de clientes"))
                is ResultadoApi.Exito -> stateSuccess(resultado.data)
                is ResultadoApi.ErrorHttp -> mostrarDialog(
                    AppDialogType.Informativo(
                        T_ERROR,
                        "Error HTTP ${resultado.code}: ${resultado.mensaje}"
                    )
                )

                is ResultadoApi.Fallo -> mostrarDialog(
                    AppDialogType.Informativo(
                        T_ERROR,
                        "Fallo: ${resultado.mensaje}"
                    )
                )
            }
        }

        val flow = apiViewModel.flowClientesFiltrados(localViewmodel.query)
        collectFlow(flow) { lista ->
            clientesCache = lista
            renderCurrentView()
        }

        collectFlow(apiViewModel.flowVendedores) { lista ->
            vendedorList = lista
        }
    }

    private fun resultadoBajaDialogo() {
        parentFragmentManager.setFragmentResultListener(
            KEY_BAJA,
            viewLifecycleOwner
        ) { _, bundle ->

            if (bajaEstado !is BajaEstados.Reposo) return@setFragmentResultListener

            val baja = bundle.getParcelableCompat<BajaAux>(PAIR_BAJA)
                ?: return@setFragmentResultListener

            val location = getLocation
            if (location == null) {
                bajaEstado = BajaEstados.ObteniendoUbicacion
                obtenerUbicacionParaBaja(baja)
                return@setFragmentResultListener
            }

            bajaEstado = BajaEstados.Procesada
            procesarBaja(baja, location)
            bajaEstado = BajaEstados.Reposo
        }
    }

    private fun obtenerUbicacionParaBaja(baja: BajaAux) {
        snack("Obteniendo ubicación…")

        gpsTracker.startTracking(
            id = TRACKER_TEMPORAL,
            interval = 0,
            fastest = 0,
            minDistance = 0f,
            onLocation = { location ->
                if (bajaEstado !is BajaEstados.ObteniendoUbicacion) return@startTracking

                gpsTracker.stopTracking(TRACKER_TEMPORAL)
                getLocation = location

                bajaEstado = BajaEstados.Procesada
                procesarBaja(baja, location)
                bajaEstado = BajaEstados.Reposo
            },
            onError = {
                if (bajaEstado !is BajaEstados.ObteniendoUbicacion) return@startTracking

                bajaEstado = BajaEstados.Error
                snack("No se pudo obtener ubicación")
                bajaEstado = BajaEstados.Reposo
            }
        )
    }

    private fun procesarBaja(
        baja: BajaAux,
        location: Location
    ) {
        val cliente = baja.cliente

        val item = TableBaja(
            cliente = cliente.cliente,
            nombre = cliente.nomcli,
            motivo = baja.motivo,
            comentario = baja.comentario,
            longitud = location.longitude,
            latitud = location.latitude,
            precision = location.accuracy.toDouble().to2Decimals(),
            fecha = baja.fecha,
            anulado = 0
        )

        if (vistaActual == Vista.MAPA) {
            mapHelper.hideInfoWindow(cliente)
        }

        snack("Cliente ${cliente.nomcli} dado de baja")
        apiViewModel.saveAndSendBaja(item)
    }

    private fun renderCurrentView() {
        when (vistaActual) {
            Vista.LISTA -> renderLista()
            Vista.MAPA -> drawMarkers()
        }
    }

    private fun renderLista() {
        val hayDatos = clientesCache.isNotEmpty()
        binding.rcvCartera.setUI("v", hayDatos)
        binding.emptyContainer.root.setUI("v", !hayDatos)
        adapter.submitList(clientesCache)
    }

    private fun drawMarkers() {
        initMapaSiEsNecesario { _ ->
            mapHelper.clearMarkers()
            clientesCache.forEach { item ->
                mapHelper.addMarker(
                    data = item,
                    lat = item.latitud,
                    lng = item.longitud,
                    icon = item.icono(requireContext())
                )
            }
            startGps() // activa MyLocation y lanza rastreo
            mapHelper.resolvePendingFocus()
        }
    }

    private fun toggleVista() {
        val siguiente = if (vistaActual == Vista.LISTA) Vista.MAPA else Vista.LISTA
        cambiarVista(siguiente)
    }

    private fun cambiarVista(nuevaVista: Vista) {
        if (vistaActual == nuevaVista) return
        vistaActual = nuevaVista

        when (nuevaVista) {
            Vista.LISTA -> {
                binding.rcvCartera.setUI("v", true)
                binding.rltMapa.setUI("v", false)
                stopGps()
                renderLista()
            }

            Vista.MAPA -> {
                localViewmodel.clearQuery() // limpia filtro
                binding.searchview.setQuery("", true)
                binding.rltMapa.setUI("v", true)
                binding.rcvCartera.setUI("v", false)
                drawMarkers()
            }
        }
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
            childFragmentManager.findFragmentById(R.id.map_cartera) as? SupportMapFragment ?: return

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

    private fun carteraPorVendedor() {
        if (vendedorList.isEmpty()) {
            snack("No hay vendedores disponibles"); return
        }
        val lista = vendedorList.map { it.descripcion }
        CarteraVendedor(requireContext(), lista) { codigo, fecha ->
            apiViewModel.downloadClientes(codigo.toInt(), fecha)
        }.show()
    }

    private fun searchVoice() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )
            putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Mencione el codigo o nombre del cliente"
            )
        }

        try {
            resultLauncher.launch(intent)
        } catch (e: Exception) {
            snack("Reconocimiento de voz no disponible")
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

                buscarClienteVoz(spokenText)
            } else {
                snack("Error procesando código")
            }
        }

    private fun buscarClienteVoz(codigo: String) {
        when (vistaActual) {
            Vista.LISTA -> binding.searchview.setQuery(codigo, true)
            Vista.MAPA -> focusClienteEnMapa(
                filtro = codigo,
                forzadoDirecto = true
            )
        }
    }

    private fun mostrarDialog(dialogType: AppDialogType) {
        lifecycleScope.launch(Dispatchers.Main) {
            InstanciaDialog.cerrarDialogActual()
            val dialog = buildMaterialDialog(requireContext(), dialogType)
            dialog.show()
            InstanciaDialog.REFERENCIA_DIALOG = WeakReference(dialog)
        }
    }

    private fun stateSuccess(clientes: JsonCliente?) {
        when {
            clientes == null -> mostrarDialog(
                AppDialogType.Informativo(
                    T_ERROR,
                    "No se obtuvo respuesta del servidor"
                )
            )

            clientes.jobl.isEmpty() -> mostrarDialog(
                AppDialogType.Informativo(
                    T_ERROR,
                    "No se encontraron clientes"
                )
            )

            else -> mostrarDialog(
                AppDialogType.Informativo(
                    T_SUCCESS,
                    "Se descargaron ${clientes.jobl.size} clientes correctamente"
                )
            )
        }
    }

    private fun focusClienteEnMapa(
        filtro: String? = null,
        forzadoDirecto: Boolean = false
    ) {
        val clientesMapa = mapHelper
            .getMarkerData(FlowCliente::class.java)
            .sortedBy { it.nomcli }

        val lista = filtro?.let { q ->
            clientesMapa.filter {
                it.cliente.contains(q, true) ||
                        it.nomcli.contains(q, true)
            }
        } ?: clientesMapa

        when {
            lista.isEmpty() -> {
                snack("No se encontraron clientes en el mapa")
            }

            lista.size == 1 || forzadoDirecto -> {
                mapHelper.focus(lista.first())
            }

            else -> {
                ListaClientesMapa(
                    requireContext(),
                    lista
                ) { cliente ->
                    mapHelper.focus(cliente)
                }.show()
            }
        }
    }
}
