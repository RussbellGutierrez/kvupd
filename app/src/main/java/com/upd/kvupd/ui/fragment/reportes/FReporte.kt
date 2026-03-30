package com.upd.kvupd.ui.fragment.reportes

import android.annotation.SuppressLint
import android.os.Bundle
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
import androidx.recyclerview.widget.PagerSnapHelper
import com.upd.kvupd.R
import com.upd.kvupd.data.model.TableConfiguracion
import com.upd.kvupd.data.remote.sealed.SocketEvent
import com.upd.kvupd.databinding.FragmentFReporteBinding
import com.upd.kvupd.domain.enumFile.TipoUsuario
import com.upd.kvupd.ui.fragment.reportes.adapter.KpiAdapter
import com.upd.kvupd.ui.fragment.reportes.adapter.KpiAdapterFactory
import com.upd.kvupd.ui.fragment.reportes.adapter.LineasAdapter
import com.upd.kvupd.ui.fragment.reportes.adapter.LineasAdapterFactory
import com.upd.kvupd.ui.fragment.reportes.enumFile.ReportAction
import com.upd.kvupd.ui.fragment.reportes.enumFile.TipoReporte
import com.upd.kvupd.ui.fragment.reportes.mapper.ReporteMapper.mapCambiosKpi
import com.upd.kvupd.ui.fragment.reportes.mapper.ReporteMapper.mapCarteraKpi
import com.upd.kvupd.ui.fragment.reportes.mapper.ReporteMapper.mapCoberturaKpi
import com.upd.kvupd.ui.fragment.reportes.mapper.ReporteMapper.mapPedidosKpi
import com.upd.kvupd.ui.fragment.reportes.mapper.ReporteMapper.mapPreventaKpi
import com.upd.kvupd.ui.fragment.reportes.modelUI.DetalleParams
import com.upd.kvupd.ui.fragment.reportes.modelUI.KpiUI
import com.upd.kvupd.ui.fragment.reportes.modelUI.LineaUI
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.ui.sealed.ResultadoApi
import com.upd.kvupd.utils.InstanciaDialog.REFERENCIA_DIALOG
import com.upd.kvupd.utils.InstanciaDialog.cerrarDialogActual
import com.upd.kvupd.utils.MaterialDialogTexto.T_ERROR
import com.upd.kvupd.utils.MaterialDialogTexto.T_SUCCESS
import com.upd.kvupd.utils.buildMaterialDialog
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.viewmodel.ALLViewModel
import com.upd.kvupd.viewmodel.APIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class FReporte : Fragment(), MenuProvider,
    KpiAdapter.Listener, LineasAdapter.Listener {

    private val apiViewModel by activityViewModels<APIViewModel>()
    private val localViewmodel by activityViewModels<ALLViewModel>()
    private val binding by viewBinding(FragmentFReporteBinding::bind)

    private lateinit var kpiAdapter: KpiAdapter
    private lateinit var lineasAdapter: LineasAdapter
    private lateinit var tipoUsuario: TipoUsuario
    private var isInitialized = false
    private var config: TableConfiguracion? = null
    private val kpiList = mutableListOf<KpiUI>()
    private val errores = mutableListOf<String>()
    private var errorJob: Job? = null
    private val _tag by lazy { FReporte::class.java.simpleName }

    @Inject
    lateinit var adapterKpiFactory: KpiAdapterFactory

    @Inject
    lateinit var adapterLineasFactory: LineasAdapterFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFReporteBinding.inflate(inflater, container, false).root

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        observerData()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.n_reporte_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.actualizar -> consume { apiViewModel.executeUpdater() }
        else -> false
    }

    override fun onKpiClick(kpi: KpiUI) {

        val conf = config ?: return

        if (!kpi.tipo.canClick(tipoUsuario)) return

        val params = DetalleParams(
            tipo = kpi.tipo,
            tipoUsuario = tipoUsuario,
            empleado = conf.codigo,
            empresa = conf.empresa.toString()
        )

        navigateToDetalle(params)
    }

    override fun onLineaClick(linea: LineaUI) {

        val action = linea.tipo.resolveAction(tipoUsuario)

        when (action) {
            ReportAction.SOLES -> {}
            else -> return
        }
    }

    private fun navigateToDetalle(params: DetalleParams) {
        val action = FReporteDirections
            .actionFReporteToFDetalle(params)
        findNavController().navigate(action)
    }

    private fun initAdapters() {
        kpiAdapter = adapterKpiFactory.create(
            listener = this,
            tipoUsuario = tipoUsuario
        )

        lineasAdapter = adapterLineasFactory.create(
            listener = this,
            tipoUsuario = tipoUsuario
        )

        initRecyclerViews()
        initShimmer()
    }

    private fun initRecyclerViews() {

        binding.rcvKpi.apply {
            adapter = kpiAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.rcvLineas.apply {
            adapter = lineasAdapter
            layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }

        PagerSnapHelper().attachToRecyclerView(binding.rcvLineas)
    }

    private fun initShimmer() {
        kpiList.clear()
        kpiList.addAll(
            listOf(
                KpiUI(tipo = TipoReporte.PREVENTA, isLoading = true),
                KpiUI(tipo = TipoReporte.COBERTURA, isLoading = true),
                KpiUI(tipo = TipoReporte.CARTERA, isLoading = true),
                KpiUI(tipo = TipoReporte.PEDIDOS, isLoading = true),
                KpiUI(tipo = TipoReporte.CAMBIOS, isLoading = true)
            )
        )

        kpiAdapter.submitList(kpiList.toList())

        lineasAdapter.submitList(
            List(5) { index ->
                LineaUI(
                    tipo = TipoReporte.SOLES,
                    codigo = -index - 1, // 👈 IDs negativos únicos
                    isLoading = true
                )
            }
        )
    }

    private fun updateKpi(kpi: KpiUI) {

        val index = kpiList.indexOfFirst { it.tipo == kpi.tipo }
        if (index == -1) return

        kpiList[index] = kpi

        kpiAdapter.submitList(
            kpiList
                .filter { !it.isEmpty }
                .toList()
        )
    }

    private fun observerData() {

        collectFlow(apiViewModel.flowConfiguracion) { list ->
            val cfg = list.firstOrNull() ?: return@collectFlow
            config = cfg
            tipoUsuario = TipoUsuario.fromCodigo(cfg.tipo)

            if (!isInitialized) {
                initAdapters()
                apiViewModel.downloadAllReports()
                isInitialized = true
            }
        }

        collectFlow(apiViewModel.preventaEvent) { result ->
            handleResultadoApi(result) {
                val kpi = mapOrEmpty(it, TipoReporte.PREVENTA, ::mapPreventaKpi)
                updateKpi(kpi)
            }
        }

        collectFlow(apiViewModel.coberturaEvent) { result ->
            handleResultadoApi(result) {
                val kpi = mapOrEmpty(it, TipoReporte.COBERTURA, ::mapCoberturaKpi)
                updateKpi(kpi)
            }
        }

        collectFlow(apiViewModel.carteraEvent) { result ->
            handleResultadoApi(result) {
                val kpi = mapOrEmpty(it, TipoReporte.CARTERA, ::mapCarteraKpi)
                updateKpi(kpi)
            }
        }

        collectFlow(apiViewModel.generalEvent) { result ->
            handleResultadoApi(result) {
                val kpi = mapOrEmpty(it, TipoReporte.PEDIDOS, ::mapPedidosKpi)
                updateKpi(kpi)
            }
        }

        collectFlow(apiViewModel.cambioEvent) { result ->
            handleResultadoApi(result) {
                val kpi = mapOrEmpty(it, TipoReporte.CAMBIOS, ::mapCambiosKpi)
                updateKpi(kpi)
            }
        }

        collectFlow(apiViewModel.solesEvent) { result ->
            handleResultadoApi(result) { lista ->
                lineasAdapter.submitList(lista ?: emptyList())
            }
        }

        collectFlow(apiViewModel.socketEvent) { result ->
            handleSocketEvent(result)
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

    private fun <T> handleResultadoApi(
        resultado: ResultadoApi<T>,
        onSuccess: (T?) -> Unit
    ) {
        when (resultado) {
            is ResultadoApi.Loading -> Unit

            is ResultadoApi.Exito -> onSuccess(resultado.data)

            is ResultadoApi.ErrorHttp -> {
                addError("Error HTTP ${resultado.code}: ${resultado.mensaje}")
                onSuccess(null)
            }

            is ResultadoApi.Fallo -> {
                addError("Fallo: ${resultado.mensaje}")
                onSuccess(null)
            }
        }
    }

    private fun handleSocketEvent(
        event: SocketEvent
    ) {
        when (event) {

            is SocketEvent.Loading -> mostrarDialog(
                AppDialogType.Progreso(
                    mensaje = "Actualizando reportes aprox 1 - 2 min, espere por favor"
                )
            )

            is SocketEvent.Success -> mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_SUCCESS,
                    mensaje = "Reporte actualizado"
                )
            )

            is SocketEvent.Error -> mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = "Error: ${event.msg}"
                )
            )
        }
    }

    private fun addError(mensaje: String) {

        errores.add(mensaje)

        errorJob?.cancel()
        errorJob = lifecycleScope.launch {
            delay(500) // espera a que lleguen más errores

            if (errores.isNotEmpty()) {

                val mensajeFinal = errores.joinToString("\n")

                mostrarDialog(
                    AppDialogType.Informativo(
                        titulo = T_ERROR,
                        mensaje = mensajeFinal
                    )
                )

                errores.clear()
            }
        }
    }

    private fun <T> mapOrEmpty(
        data: T?,
        tipo: TipoReporte,
        mapper: (T) -> KpiUI
    ): KpiUI {
        return data?.let(mapper)
            ?: KpiUI(
                tipo = tipo,
                isLoading = false,
                isEmpty = true
            )
    }
}
