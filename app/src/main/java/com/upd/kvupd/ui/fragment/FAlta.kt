package com.upd.kvupd.ui.fragment

import MapHelper
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.upd.kvupd.R
import com.upd.kvupd.data.model.JsonCliente
import com.upd.kvupd.data.model.TableAlta
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.databinding.FragmentFAltaBinding
import com.upd.kvupd.ui.adapter.AltaAdapter
import com.upd.kvupd.ui.adapter.AltaAdapterFactory
import com.upd.kvupd.ui.fragment.enumClass.VistaAlta
import com.upd.kvupd.ui.sealed.AppDialogType
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
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.viewmodel.ALLViewModel
import com.upd.kvupd.viewmodel.APIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class FAlta : Fragment(), AltaAdapter.Listener, MenuProvider {

    private val apiViewModel by activityViewModels<APIViewModel>()
    private val localViewmodel by activityViewModels<ALLViewModel>()
    private val binding by viewBinding(FragmentFAltaBinding::bind)

    private lateinit var adapter: AltaAdapter
    private val mapHelper by lazy { MapHelper(layoutInflater) }

    /*private var bajaEstado: EstadoBaja = EstadoBaja.Reposo

    private var clientesCache: List<FlowCliente> = emptyList()
    private var vendedorList: List<TableVendedor> = emptyList()
    */

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

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        adapter = adapterFactory.create(listener = this)
        binding.rcvAlta.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvAlta.adapter = adapter

        setupButtons()
        //collectFlows()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.n_alta_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.manual -> consume { toggleVista() }
        else -> false
    }

    override fun onLongClick(alta: TableAlta) {
        //Falta
    }

    /*override fun onClick(cliente: FlowCliente) {
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
    }*/

    /*override fun onLongClick(cliente: FlowCliente) {
        if (cliente.baja > 0) {
            mostrarClienteDadoDeBaja()
            return
        }

        navegarABaja(cliente)
    }*/

    /*private fun mostrarClienteDadoDeBaja() {
        mostrarDialog(
            AppDialogType.Informativo(
                titulo = T_WARNING,
                mensaje = "El cliente fue dado de baja, si fue por error, puede anularlo desde 'Administrar Bajas'"
            )
        )
    }*/

    /*private fun navegarABaja(cliente: FlowCliente) {
        val action = FCarteraDirections
            .actionFCarteraToBDBajaCliente(cliente)
        findNavController().navigate(action)
    }*/

    private fun setupButtons() {
        binding.apply {

            fabCentrar.setOnClickListener {
                //
            }

            fabUbicacion.setOnClickListener {
                //
            }
        }
        /*binding.fabLista.setOnClickListener {
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
        )*/
    }

    private fun collectFlows() {
        /*collectFlow(apiViewModel.clienteEvent) { resultado ->
            when (resultado) {
                is ResultadoApi.Loading -> mostrarDialog(
                    AppDialogType.Progreso(
                        mensaje = "Obteniendo lista de clientes"
                    )
                )

                is ResultadoApi.Exito -> stateSuccess(resultado.data)
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

        val flow = apiViewModel.flowClientesFiltrados(localViewmodel.query)
        collectFlow(flow) { lista ->
            clientesCache = lista
            renderCurrentView()
        }

        collectFlow(apiViewModel.flowVendedores) { lista ->
            vendedorList = lista
        }

        collectFlow(apiViewModel.bajaMessage) collect@{ mensaje ->
            mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = mensaje
                )
            )
        }*/
    }

    /*private fun resultadoBajaDialogo() {
        parentFragmentManager.setFragmentResultListener(
            KEY_BAJA,
            viewLifecycleOwner
        ) { _, bundle ->

            if (bajaEstado != EstadoBaja.Reposo) return@setFragmentResultListener

            val cliente = bundle.getString("cliente") ?: return@setFragmentResultListener
            val nombre = bundle.getString("nombre") ?: return@setFragmentResultListener
            val motivo = bundle.getInt("motivo")
            val comentario = bundle.getString("comentario") ?: ""
            val fecha = bundle.getString("fecha") ?: return@setFragmentResultListener

            val baja = TableBaja(
                cliente = cliente,
                nombre = nombre,
                motivo = motivo,
                comentario = comentario,
                longitud = 0.0,
                latitud = 0.0,
                precision = 0.0,
                fecha = fecha,
                anulado = 0
            )

            val location = getLocation
            if (location == null) {
                bajaEstado = EstadoBaja.ObteniendoUbicacion
                obtenerUbicacionParaBaja(baja)
                return@setFragmentResultListener
            }

            bajaEstado = EstadoBaja.Procesada
            procesarBaja(baja, location)
            bajaEstado = EstadoBaja.Reposo
        }
    }*/

    private fun obtenerUbicacionParaBaja(baja: TableBaja) {
        /*snack("Obteniendo ubicación…")

        gpsTracker.startTracking(
            id = TRACKER_TEMPORAL,
            interval = GT_SIN_INTERVALO,
            fastest = GT_SIN_INTERVALO,
            minDistance = IGNORAR_METROS,
            onLocation = { location ->
                if (bajaEstado != EstadoBaja.ObteniendoUbicacion) return@startTracking

                gpsTracker.stopTracking(TRACKER_TEMPORAL)
                getLocation = location

                bajaEstado = EstadoBaja.Procesada
                procesarBaja(baja, location)
                bajaEstado = EstadoBaja.Reposo
            },
            onError = {
                if (bajaEstado != EstadoBaja.ObteniendoUbicacion) return@startTracking

                bajaEstado = EstadoBaja.Error
                snack("No se pudo obtener ubicación")
                bajaEstado = EstadoBaja.Reposo
            }
        )*/
    }

    /*private fun procesarBaja(
        baja: TableBaja,
        location: Location
    ) {
        val item = baja.copy(
            longitud = location.longitude,
            latitud = location.latitude,
            precision = location.accuracy.toDouble().to2Decimals()
        )

        if (vistaActual == VistaInterfaz.MAPA) {
            val clienteMapa = clientesCache
                .firstOrNull { it.cliente == baja.cliente }

            clienteMapa?.let {
                mapHelper.hideInfoWindow(it)
            }
        }

        snack("Cliente ${item.nombre} dado de baja")
        apiViewModel.saveAndSendBaja(item)
    }*/

    private fun renderCurrentView() {
        when (vistaActual) {
            VistaAlta.LISTA -> renderLista()
            VistaAlta.MAPA -> drawMarkers()
        }
    }

    private fun renderLista() {
        /*val hayDatos = clientesCache.isNotEmpty()
        binding.rcvCartera.setUI("v", hayDatos)
        binding.emptyContainer.root.setUI("v", !hayDatos)
        adapter.submitList(clientesCache)*/
    }

    private fun drawMarkers() {
        /*initMapaSiEsNecesario { _ ->
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
        }*/
    }

    private fun toggleVista() {
        val siguiente =
            if (vistaActual == VistaAlta.LISTA) VistaAlta.MAPA else VistaAlta.LISTA
        cambiarVista(siguiente)
    }

    private fun cambiarVista(nuevaVista: VistaAlta) {
        if (vistaActual == nuevaVista) return
        vistaActual = nuevaVista

        binding.lnrAlta.setUI("v", nuevaVista == VistaAlta.LISTA)
        binding.rltMapa.setUI("v", nuevaVista == VistaAlta.MAPA)

        when (nuevaVista) {
            VistaAlta.LISTA -> {
                stopGps()
                renderLista()
            }

            VistaAlta.MAPA -> {
                localViewmodel.clearQuery()
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
                    titulo = T_ERROR,
                    mensaje = "No se obtuvo respuesta del servidor"
                )
            )

            clientes.jobl.isEmpty() -> mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = "No se encontraron clientes"
                )
            )

            else -> mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_SUCCESS,
                    mensaje = "Se descargaron ${clientes.jobl.size} clientes correctamente"
                )
            )
        }
    }
}
