package com.upd.kvupd.ui.fragment

import MapHelper
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.SupportMapFragment
import com.upd.kvupd.R
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.data.model.JsonCliente
import com.upd.kvupd.data.model.TableVendedor
import com.upd.kvupd.databinding.FragmentFCarteraBinding
import com.upd.kvupd.ui.adapter.ClienteAdapter
import com.upd.kvupd.ui.adapter.ClienteAdapterFactory
import com.upd.kvupd.ui.dialog.CarteraVendedor
import com.upd.kvupd.ui.fragment.enumClass.Vista
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.ui.sealed.ResultadoApi
import com.upd.kvupd.utils.FechaHoraUtil
import com.upd.kvupd.utils.GPSConstants
import com.upd.kvupd.utils.GPSConstants.MODO_RAPIDO
import com.upd.kvupd.utils.GpsTracker
import com.upd.kvupd.utils.InstanciaDialog
import com.upd.kvupd.utils.MaterialDialogTexto.T_ERROR
import com.upd.kvupd.utils.MaterialDialogTexto.T_SUCCESS
import com.upd.kvupd.utils.awaitMap
import com.upd.kvupd.utils.buildMaterialDialog
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.setUI
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
class FCartera : Fragment(), OnQueryTextListener, ClienteAdapter.Listener,
    MenuProvider {

    private val apiViewModel by activityViewModels<APIViewModel>()
    private val binding by viewBinding(FragmentFCarteraBinding::bind)

    private lateinit var adapter: ClienteAdapter
    private lateinit var mapHelper: MapHelper
    private var vistaActual: Vista = Vista.LISTA
    private var getLocation: Location? = null
    private val vendedorList = mutableListOf<TableVendedor>()
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

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        mapHelper = MapHelper(layoutInflater)

        adapter = adapterFactory.create(
            listener = this,
            hoy = FechaHoraUtil.dia()
        )

        binding.rcvCartera.layoutManager = LinearLayoutManager(requireContext())
        binding.rcvCartera.adapter = adapter
        binding.searchview.setOnQueryTextListener(this)

        binding.fabLista.setOnClickListener {
            //
        }
        binding.fabCentrar.setOnClickListener {
            //
        }
        binding.fabUbicacion.setOnClickListener {
            //
        }

        collectFlow(apiViewModel.clienteEvent) collect@{ resultado ->
            when (resultado) {
                is ResultadoApi.Loading -> mostrarDialog(
                    AppDialogType.Progreso(
                        mensaje = "Obteniendo lista de clientes"
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

        collectFlow(apiViewModel.flowClientesFiltrados) collect@{ lista ->
            renderLista(lista)
        }

        collectFlow(apiViewModel.flowVendedores) collect@{ lista ->
            vendedorList.addAll(lista)
        }

        /*val mapFragment =
            childFragmentManager.findFragmentById(binding.rltMapa.id) as SupportMapFragment

        viewLifecycleOwner.lifecycleScope.launch {
            val gMap = mapFragment.awaitMap()

            mapHelper.attachMap(gMap)
            gMap.isMyLocationEnabled = true

            launchGpsRastreo()
        }*/

        //bind.rcvClientes.layoutManager = LinearLayoutManager(requireContext())
        //bind.rcvClientes.adapter = adapter

        //bind.searchView.setOnQueryTextListener(this)

        /*viewmodel.rowClienteObs().distinctUntilChanged().observe(viewLifecycleOwner) {
            row = it
            setupList(it)
        }

        viewmodel.vendedor.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                launchDownload(y[0], y[1])
            }
        }

        viewmodel.cliente.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is OldNetworkRetrofit.Success -> showDialog(
                        "Correcto",
                        "Clientes descargados correctamente"
                    ) {}

                    is OldNetworkRetrofit.Error -> showDialog("Error", "Clientes server ${y.message}") {}
                }
            }
        }

        viewmodel.rutas.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is OldNetworkRetrofit.Success -> showDialog(
                        "Correcto",
                        "Rutas descargadas correctamente"
                    ) {}

                    is OldNetworkRetrofit.Error -> showDialog("Error", "Rutas server ${y.message}") {}
                }
            }
        }*/
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.n_cartera_menu, menu)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.descargar -> consume { carteraPorVendedor() }
        R.id.voz -> consume { searchVoice() }
        R.id.cambiar -> consume { toggleVista() }
        else -> false
    }

    override fun onQueryTextSubmit(p0: String) = false

    override fun onQueryTextChange(p0: String): Boolean {
        apiViewModel.setQuery(p0)
        return false
    }

    /*override fun onClienteClick(cliente: RowCliente) {
        /*viewLifecycleOwner.lifecycleScope.launch {
            clienteBaja = viewmodel.isClienteBaja(cliente.id.toString())
            navigateToDialog(0, cliente)
        }*/
    }

    override fun onPressCliente(cliente: RowCliente) {
        /*viewLifecycleOwner.lifecycleScope.launch {
            clienteBaja = viewmodel.isClienteBaja(cliente.id.toString())
            navigateToDialog(1, cliente)
        }*/
    }*/

    override fun onClick(cliente: FlowCliente) {
        TODO("Not yet implemented")
    }

    override fun onLongClick(cliente: FlowCliente) {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun carteraPorVendedor() {
        if (vendedorList.isEmpty()) {
            snack("No hay vendedores disponibles")
            return
        }

        val lista = vendedorList.map { it.descripcion }

        CarteraVendedor(
            context = requireContext(),
            vendedores = lista
        ) { codigo, fecha ->
            apiViewModel.downloadClientes(
                vendedor = codigo.toInt(),
                fecha = fecha
            )
        }.show()
    }

    private fun launchGpsRastreo() {
        gpsTracker.startTracking(
            id = MODO_RAPIDO,
            interval = GPSConstants.GPS_INTERVALO_NORMAL,
            fastest = GPSConstants.GPS_INTERVALO_RAPIDO,
            minDistance = GPSConstants.IGNORAR_METROS,
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

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val codigo =
                    result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!![0]
                buscarClienteVoz(codigo)
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
                intent.putExtra(
                    RecognizerIntent.EXTRA_PROMPT,
                    "Mencione el codigo o nombre del cliente"
                )
                resultLauncher.launch(intent)
            }
        }
    }

    /*private fun launchDownload(codigo: String, fecha: String) {
        progress("Descargando clientes y rutas")

        //viewmodel.cleanDataVendedor()

        val clientes = JSONObject()
        clientes.put("empleado", codigo)
        clientes.put("fecha", fecha)
        clientes.put("empresa", CONF.empresa)
        //viewmodel.fetchClientes(clientes.toReqBody())

        val rutas = JSONObject()
        rutas.put("empleado", codigo)
        rutas.put("empresa", CONF.empresa)
        //viewmodel.fetchRutas(rutas.toReqBody())
    }*/

    override fun onResume() {
        super.onResume()
        if (vistaActual == Vista.MAPA) {
            startGps()
        }
    }

    override fun onPause() {
        if (vistaActual == Vista.MAPA) {
            stopGps()
        }
        super.onPause()
    }

    private fun renderLista(lista: List<FlowCliente>) {
        val hayDatos = lista.isNotEmpty()

        binding.rcvCartera.setUI("v", hayDatos)
        binding.emptyContainer.root.setUI("v", !hayDatos)

        adapter.submitList(lista)
    }

    private fun buscarClienteVoz(codigo: String) {
        when (vistaActual) {
            Vista.LISTA -> binding.searchview.setQuery(codigo, true)
            Vista.MAPA -> {}
        }
    }

    private fun toggleVista() {
        val siguiente = when (vistaActual) {
            Vista.LISTA -> Vista.MAPA
            Vista.MAPA -> Vista.LISTA
        }
        cambiarVista(siguiente)
    }

    private fun cambiarVista(nuevaVista: Vista) {
        if (vistaActual == nuevaVista) return

        vistaActual = nuevaVista

        when (nuevaVista) {
            Vista.LISTA -> mostrarLista()
            Vista.MAPA -> mostrarMapa()
        }
    }

    private fun mostrarLista() {
        binding.rcvCartera.setUI("v", true)
        binding.rltMapa.setUI("v", false)

        stopGps()
    }

    private fun mostrarMapa() {
        binding.rltMapa.setUI("v", true)
        binding.rcvCartera.setUI("v", false)

        initMapaSiEsNecesario()
        startGps()
    }

    private fun startGps() {
        mapHelper.enableMyLocation()
        launchGpsRastreo()
    }

    private fun stopGps() {
        gpsTracker.stopTracking(MODO_RAPIDO)
        mapHelper.disableMyLocation()
    }

    private fun initMapaSiEsNecesario() {
        if (mapaInicializado) return

        val mapFragment =
            childFragmentManager.findFragmentById(binding.rltMapa.id) as SupportMapFragment

        viewLifecycleOwner.lifecycleScope.launch {
            val gMap = mapFragment.awaitMap()
            mapHelper.attachMap(gMap)
            mapaInicializado = true
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

    private fun stateSuccess(clientes: JsonCliente?) {
        when {
            clientes == null -> {
                mostrarDialog(
                    AppDialogType.Informativo(
                        titulo = T_ERROR,
                        mensaje = "No se obtuvo respuesta del servidor"
                    )
                )
            }

            clientes.jobl.isEmpty() -> {
                mostrarDialog(
                    AppDialogType.Informativo(
                        titulo = T_ERROR,
                        mensaje = "No se encontraron clientes"
                    )
                )
            }

            else -> {
                mostrarDialog(
                    AppDialogType.Informativo(
                        titulo = T_SUCCESS,
                        mensaje = "Se descargaron ${clientes.jobl.size} clientes correctamente"
                    )
                )
            }
        }
    }
}