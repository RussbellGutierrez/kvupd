package com.upd.kvupd.ui.fragment.baja

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.upd.kvupd.R
import com.upd.kvupd.data.model.BajaVendedor
import com.upd.kvupd.data.model.FlowBajaSupervisor
import com.upd.kvupd.data.model.JsonBajaSupervisor
import com.upd.kvupd.data.model.JsonBajaVendedor
import com.upd.kvupd.data.model.core.TableBaja
import com.upd.kvupd.databinding.DialogBajasVendedorBinding
import com.upd.kvupd.databinding.FragmentFBajaBinding
import com.upd.kvupd.databinding.RowBajavendedorBinding
import com.upd.kvupd.domain.enumFile.TipoUsuario
import com.upd.kvupd.ui.fragment.baja.adapter.normal.BajaAdapter
import com.upd.kvupd.ui.fragment.baja.adapter.normal.BajaAdapterFactory
import com.upd.kvupd.ui.fragment.baja.adapter.supervisor.BajaSuperAdapter
import com.upd.kvupd.ui.fragment.baja.adapter.supervisor.BajaSuperAdapterFactory
import com.upd.kvupd.ui.fragment.baja.behavior.BajaBehavior
import com.upd.kvupd.ui.fragment.baja.behavior.SupervisorBajaBehavior
import com.upd.kvupd.ui.fragment.baja.behavior.VendedorBajaBehavior
import com.upd.kvupd.ui.fragment.baja.enumFile.VistaBaja
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.ui.sealed.ResultadoApi
import com.upd.kvupd.utils.FechaHoraUtil
import com.upd.kvupd.utils.GPSConstants
import com.upd.kvupd.utils.GPSConstants.GT_SIN_INTERVALO
import com.upd.kvupd.utils.GPSConstants.IGNORAR_METROS
import com.upd.kvupd.utils.GPSConstants.TRACKER_TEMPORAL
import com.upd.kvupd.utils.InstanciaDialog.REFERENCIA_DIALOG
import com.upd.kvupd.utils.InstanciaDialog.cerrarDialogActual
import com.upd.kvupd.utils.MaterialDialogTexto.T_ERROR
import com.upd.kvupd.utils.MaterialDialogTexto.T_WARNING
import com.upd.kvupd.utils.buildMaterialDialog
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.gone
import com.upd.kvupd.utils.gps.GpsTracker
import com.upd.kvupd.utils.snack
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.utils.visible
import com.upd.kvupd.viewmodel.ALLViewModel
import com.upd.kvupd.viewmodel.APIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class FBaja : Fragment(), MenuProvider, OnQueryTextListener,
    BajaAdapter.Listener, BajaSuperAdapter.Listener {

    private val apiViewModel by activityViewModels<APIViewModel>()
    private val localViewmodel by activityViewModels<ALLViewModel>()
    private val binding by viewBinding(FragmentFBajaBinding::bind)

    private lateinit var bajaAdapter: BajaAdapter
    private lateinit var bajasuperAdapter: BajaSuperAdapter
    private lateinit var bajaBehavior: BajaBehavior

    private lateinit var tipoUsuario: TipoUsuario
    private val esVendedor get() = tipoUsuario == TipoUsuario.VENDEDOR
    private val esSupervisor get() = tipoUsuario == TipoUsuario.SUPERVISOR
    private var vistaActual: VistaBaja = VistaBaja.GENERADO
    private var bajaCache: List<TableBaja> = emptyList()
    private var bajaSuperCache: List<FlowBajaSupervisor> = emptyList()
    private val _tag by lazy { FBaja::class.java.simpleName }

    @Inject
    lateinit var adapterBFactory: BajaAdapterFactory

    @Inject
    lateinit var adapterBSFactory: BajaSuperAdapterFactory

    @Inject
    lateinit var gpsTracker: GpsTracker

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFBajaBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        bajaAdapter = adapterBFactory.create(listener = this)
        bajasuperAdapter = adapterBSFactory.create(listener = this)
        binding.rcvBaja.layoutManager = LinearLayoutManager(requireContext())
        binding.searchview.setOnQueryTextListener(this)

        observerData()
        functionPerUserType()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.n_baja_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.lista -> consume { toggleVista() }
        else -> false
    }

    override fun onBajaLongClick(baja: TableBaja) {
        mostrarDialog(
            AppDialogType.Informativo(
                titulo = T_WARNING,
                mensaje = "Desea anular la baja del cliente ${baja.nombre}?",
                onPositive = {
                    val item = baja.copy(anulado = 1)
                    apiViewModel.retrySendBaja(item)
                }
            )
        )
    }

    override fun onBajaSupervisorLongClick(
        bajaSupervisor: FlowBajaSupervisor
    ) {
        if (bajaSupervisor.procede == null) {
            obtenerUbicacionParaAbrirDetalle(bajaSupervisor)
        }
    }

    private fun obtenerUbicacionParaAbrirDetalle(
        bajaSupervisor: FlowBajaSupervisor
    ) {

        gpsTracker.startTracking(
            id = TRACKER_TEMPORAL,
            interval = GT_SIN_INTERVALO,
            fastest = GT_SIN_INTERVALO,
            minDistance = IGNORAR_METROS,

            onLocation = { location ->

                gpsTracker.stopTracking(
                    TRACKER_TEMPORAL
                )

                val action = FBajaDirections
                    .actionFBajaToBDDetalleBaja(
                        detalle = bajaSupervisor,
                        latitud = location.latitude.toFloat(),
                        longitud = location.longitude.toFloat(),
                        precision = location.accuracy
                    )

                findNavController().navigate(action)
            },

            onError = {
                gpsTracker.stopTracking(
                    TRACKER_TEMPORAL
                )

                snack("No se pudo obtener ubicación")
            }
        )
    }

    override fun onQueryTextSubmit(p0: String) = false
    override fun onQueryTextChange(p0: String): Boolean {
        localViewmodel.setQuery(p0)
        return true
    }

    override fun onStop() {
        localViewmodel.clearQuery()
        super.onStop()
    }

    private fun observerData() {
        val flowBajas = apiViewModel.flowBajasFiltrados(localViewmodel.query)
        val flowBajaSupervisor = apiViewModel.flowBajasSupervisorFiltrados(localViewmodel.query)

        collectFlow(flowBajas) { bajas ->
            bajaCache = bajas
            renderVistaActual()
        }

        collectFlow(flowBajaSupervisor) { bajasSupervisor ->
            bajaSuperCache = bajasSupervisor
            renderVistaActual()
        }

        collectFlow(apiViewModel.bajasuperEvent) { resultado ->
            handleResultadoApi(
                resultado
            ) { data ->
                stateSuccessBajaSuper(data)
            }
        }

        collectFlow(apiViewModel.bajaestadoEvent) { resultado ->
            handleResultadoApi(
                resultado
            ) { data ->
                stateSuccessBajaEstado(data)
            }
        }

        collectFlow(apiViewModel.bajaProcesadaMessage) { mensaje ->
            mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = mensaje
                )
            )
        }
    }

    private fun functionPerUserType() {
        lifecycleScope.launch {
            tipoUsuario = localViewmodel.obtenerTipoUsuario()
                ?: return@launch

            requireActivity().invalidateOptionsMenu()

            bajaBehavior = when (tipoUsuario) {
                TipoUsuario.JEFE_VENTAS, TipoUsuario.VENDEDOR -> VendedorBajaBehavior(apiViewModel)
                TipoUsuario.SUPERVISOR -> SupervisorBajaBehavior(apiViewModel)
            }
        }
    }

    private fun toggleVista() {

        // 👤 VENDEDOR
        if (esVendedor) {
            showLoading()
            bajaBehavior.onDescargar()
            return
        }

        val nueva = bajaBehavior.onToggleVista(vistaActual)

        // 👤 SUPERVISOR
        if (esSupervisor && nueva == VistaBaja.PROCESAR) {
            handleSupervisorVista()
            return
        }

        cambiarVista(nueva)
    }

    private fun cambiarVista(nuevaVista: VistaBaja) {
        if (vistaActual == nuevaVista) return
        vistaActual = nuevaVista

        binding.searchview.setQuery("", false)
        localViewmodel.clearQuery()

        renderVistaActual()
    }

    private fun renderVistaActual() {
        when (vistaActual) {
            VistaBaja.GENERADO -> renderGeneradas()
            VistaBaja.PROCESAR -> renderSupervisor()
        }
    }

    private fun <T> renderLista(
        titulo: String,
        adapter: RecyclerView.Adapter<*>,
        data: List<T>,
        submit: (List<T>) -> Unit
    ) {
        binding.txtVista.text = titulo

        if (binding.rcvBaja.adapter != adapter) {
            binding.rcvBaja.adapter = adapter
        }

        if (data.isEmpty()) {
            showEmpty()
        } else {
            showData()
            submit(data)
        }
    }

    private fun renderGeneradas() {
        renderLista("Bajas generadas", bajaAdapter, bajaCache) {
            bajaAdapter.submitList(it)
        }
    }

    private fun renderSupervisor() {
        renderLista("Bajas pendientes", bajasuperAdapter, bajaSuperCache) {
            bajasuperAdapter.submitList(it)
        }
    }

    private fun stateSuccessBajaSuper(bajaSupervisores: JsonBajaSupervisor?) {
        when {
            bajaSupervisores == null -> mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = "No se obtuvo respuesta del servidor"
                )
            )

            bajaSupervisores.jobl.isEmpty() -> mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = "No se encontraron bajas pendientes"
                )
            )

            else -> renderVistaActual()
        }
    }

    private fun stateSuccessBajaEstado(bajaVendedores: JsonBajaVendedor?) {
        when {
            bajaVendedores == null -> mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = "No se obtuvo respuesta del servidor"
                )
            )

            bajaVendedores.jobl.isEmpty() -> mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = "Todas las bajas revisadas"
                )
            )

            else -> renderBajasPendientes(bajaVendedores.jobl)
        }
    }

    private fun renderBajasPendientes(lista: List<BajaVendedor>) {

        val bindingDialog = DialogBajasVendedorBinding.inflate(layoutInflater)

        val dialog = MaterialDialog(requireContext())
            .customView(view = bindingDialog.root, scrollable = false)

        lista.forEach { item ->
            val row = createRow(item, bindingDialog.lnrBajavendedor)
            bindingDialog.lnrBajavendedor.addView(row)
        }

        dialog.show()
    }

    private fun createRow(
        item: BajaVendedor,
        parent: ViewGroup
    ): View {

        val cliente = "${item.cliente} - ${item.nombre}"
        val lapso = FechaHoraUtil.diasDesde(item.fecha)
        val dias = "$lapso dias"

        val binding = RowBajavendedorBinding.inflate(
            layoutInflater,
            parent,
            false
        )

        binding.txtFecha.text = item.fecha
        binding.txtCliente.text = cliente
        binding.txtMotivo.text = item.descripcion
        binding.txtDias.text = dias

        return binding.root
    }

    private fun <T> handleResultadoApi(
        resultado: ResultadoApi<T>,
        onSuccess: (T?) -> Unit
    ) {
        when (resultado) {
            is ResultadoApi.Loading -> showLoading()

            is ResultadoApi.Exito -> {
                hideLoading()
                onSuccess(resultado.data)
            }

            is ResultadoApi.ErrorHttp -> {
                hideLoading()
                showEmpty()
                mostrarDialog(
                    AppDialogType.Informativo(
                        titulo = T_ERROR,
                        mensaje = "Error HTTP ${resultado.code}: ${resultado.mensaje}"
                    )
                )
            }

            is ResultadoApi.Fallo -> {
                hideLoading()
                showEmpty()
                mostrarDialog(
                    AppDialogType.Informativo(
                        titulo = T_ERROR,
                        mensaje = "Fallo: ${resultado.mensaje}"
                    )
                )
            }
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

    private fun showLoading() {
        binding.shimmerBaja.visible()
        binding.shimmerBaja.startShimmer()

        binding.rcvBaja.gone()
        binding.emptyContainer.root.gone()
    }

    private fun hideLoading() {
        binding.shimmerBaja.stopShimmer()
        binding.shimmerBaja.gone()
    }

    private fun showData() {
        binding.rcvBaja.visible()
        binding.emptyContainer.root.gone()
    }

    private fun showEmpty() {
        binding.rcvBaja.gone()
        binding.emptyContainer.root.visible()
    }

    private fun handleSupervisorVista() {

        if (bajaSuperCache.isEmpty()) {
            showLoading()
            cambiarVista(VistaBaja.PROCESAR)
            bajaBehavior.onDescargar()
            return
        }

        mostrarDialog(
            AppDialogType.Informativo(
                titulo = T_WARNING,
                mensaje = "¿Desea actualizar las bajas pendientes?",
                mostrarNegativo = true,
                onPositive = {
                    showLoading()
                    cambiarVista(VistaBaja.PROCESAR)
                    bajaBehavior.onDescargar()
                },
                onNegative = {
                    cambiarVista(VistaBaja.PROCESAR)
                }
            )
        )
    }
}