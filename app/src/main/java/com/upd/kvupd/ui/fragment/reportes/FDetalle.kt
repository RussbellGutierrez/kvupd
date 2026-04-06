package com.upd.kvupd.ui.fragment.reportes

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.upd.kvupd.databinding.FragmentFDetalleBinding
import com.upd.kvupd.ui.dialog.BDSolesDetalle
import com.upd.kvupd.ui.fragment.reportes.adapter.detalle.CambiosAdapter
import com.upd.kvupd.ui.fragment.reportes.adapter.detalle.CoberturadosAdapter
import com.upd.kvupd.ui.fragment.reportes.adapter.detalle.DetalleCoberturaAdapter
import com.upd.kvupd.ui.fragment.reportes.adapter.detalle.PedidoGeneralAdapter
import com.upd.kvupd.ui.fragment.reportes.adapter.detalle.ProgresoAdapter
import com.upd.kvupd.ui.fragment.reportes.adapter.detalle.SolesDetalleAdapter
import com.upd.kvupd.ui.fragment.reportes.adapter.detalle.SolesDetalleAdapterFactory
import com.upd.kvupd.ui.fragment.reportes.enumFile.ReportAction
import com.upd.kvupd.ui.fragment.reportes.mapper.ReporteMapper.toSubUI
import com.upd.kvupd.ui.fragment.reportes.modelUI.DetalleParams
import com.upd.kvupd.ui.fragment.reportes.modelUI.SubCambioUI
import com.upd.kvupd.ui.fragment.reportes.modelUI.SubCoberturadosUI
import com.upd.kvupd.ui.fragment.reportes.modelUI.SubDetalleCoberturaUI
import com.upd.kvupd.ui.fragment.reportes.modelUI.SubPedidoGeneralUI
import com.upd.kvupd.ui.fragment.reportes.modelUI.SubProgresoUI
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.ui.sealed.ResultadoApi
import com.upd.kvupd.utils.InstanciaDialog.REFERENCIA_DIALOG
import com.upd.kvupd.utils.InstanciaDialog.cerrarDialogActual
import com.upd.kvupd.utils.MaterialDialogTexto.T_ERROR
import com.upd.kvupd.utils.buildMaterialDialog
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.viewmodel.APIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class FDetalle : Fragment(), SolesDetalleAdapter.Listener {

    private val apiViewModel by activityViewModels<APIViewModel>()
    private val args: FDetalleArgs by navArgs()
    private val binding by viewBinding(FragmentFDetalleBinding::bind)

    private lateinit var action: ReportAction
    private val detParams: DetalleParams by lazy { args.params }

    private lateinit var progresoAdapter: ProgresoAdapter
    private lateinit var detalleCoberturaAdapter: DetalleCoberturaAdapter
    private lateinit var coberturadosAdapter: CoberturadosAdapter
    private lateinit var pedidoGeneralAdapter: PedidoGeneralAdapter
    private lateinit var cambiosAdapter: CambiosAdapter
    private lateinit var solesDetalleAdapter: SolesDetalleAdapter
    private val _tag by lazy { FDetalle::class.java.simpleName }

    @Inject
    lateinit var adapterSolesDetalleFactory: SolesDetalleAdapterFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFDetalleBinding.inflate(inflater, container, false).root

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        action = detParams.tipo.resolveAction(detParams.tipoUsuario)

        initAdapters()
        observerData()

        apiViewModel.downloadDetailReport(detParams)
    }

    override fun onSolesClick(soles: SubProgresoUI) {
        val dialog = BDSolesDetalle.newInstance(
            linea = soles.codigo,
            titulo = soles.descripcion
        )

        dialog.show(parentFragmentManager, "BDSolesDetalle")
    }

    private fun observerData() {

        when (action) {

            ReportAction.PREVENTA ->
                observe(
                    flow = apiViewModel.preventaEvent,
                    mapper = { it.toSubUI() },
                    submit = progresoAdapter::submitList
                )

            ReportAction.COBERTURA_SUP ->
                observe(
                    flow = apiViewModel.coberturaEvent,
                    mapper = { it.toSubUI() },
                    submit = progresoAdapter::submitList
                )

            ReportAction.COBERTURA_VEN ->
                observe(
                    flow = apiViewModel.coberturaDetalleEvent,
                    mapper = { it.toSubUI() },
                    submit = detalleCoberturaAdapter::submitList
                )

            ReportAction.CARTERA_SUP ->
                observe(
                    flow = apiViewModel.carteraEvent,
                    mapper = { it.toSubUI() },
                    submit = progresoAdapter::submitList
                )

            ReportAction.CARTERA_VEN ->
                observe(
                    flow = apiViewModel.coberturaPendienteEvent,
                    mapper = { it.toSubUI() },
                    submit = coberturadosAdapter::submitList
                )

            ReportAction.PEDIDOS ->
                observe(
                    flow = apiViewModel.pedidoEmpleadoEvent,
                    mapper = { it.toSubUI() },
                    submit = pedidoGeneralAdapter::submitList
                )

            ReportAction.CAMBIOS ->
                observe(
                    flow = apiViewModel.cambioEvent,
                    mapper = { it.toSubUI() },
                    submit = cambiosAdapter::submitList
                )

            ReportAction.SOLES ->
                observe(
                    flow = apiViewModel.solesDetalleEvent,
                    mapper = { it.toSubUI() },
                    submit = solesDetalleAdapter::submitList
                )
        }
    }

    private fun initAdapters() {
        progresoAdapter = ProgresoAdapter()
        detalleCoberturaAdapter = DetalleCoberturaAdapter()
        coberturadosAdapter = CoberturadosAdapter()
        pedidoGeneralAdapter = PedidoGeneralAdapter()
        cambiosAdapter = CambiosAdapter()

        solesDetalleAdapter = adapterSolesDetalleFactory.create(
            listener = this
        )

        binding.txtTitulo.text = detParams.tipo.titulo

        binding.rcvDetalle.apply {
            adapter = getAdapterForAction()
            layoutManager = LinearLayoutManager(requireContext())
        }

        initShimmer()
    }

    private fun getAdapterForAction(): RecyclerView.Adapter<*> {

        return when (action) {

            ReportAction.PREVENTA,
            ReportAction.COBERTURA_SUP,
            ReportAction.CARTERA_SUP -> progresoAdapter

            ReportAction.COBERTURA_VEN -> detalleCoberturaAdapter

            ReportAction.CARTERA_VEN -> coberturadosAdapter

            ReportAction.PEDIDOS -> pedidoGeneralAdapter

            ReportAction.CAMBIOS -> cambiosAdapter

            ReportAction.SOLES -> solesDetalleAdapter
        }
    }

    private fun initShimmer() {

        when (action) {

            ReportAction.PREVENTA,
            ReportAction.COBERTURA_SUP,
            ReportAction.CARTERA_SUP -> progresoAdapter.submitList(
                createFakeList { id ->
                    SubProgresoUI(codigo = id, isLoading = true)
                }
            )

            ReportAction.COBERTURA_VEN -> detalleCoberturaAdapter.submitList(
                createFakeList { id ->
                    SubDetalleCoberturaUI(codigo = id, isLoading = true)
                }
            )

            ReportAction.CARTERA_VEN -> coberturadosAdapter.submitList(
                createFakeList { id ->
                    SubCoberturadosUI(codigo = id, isLoading = true)
                }
            )

            ReportAction.PEDIDOS -> pedidoGeneralAdapter.submitList(
                createFakeList { id ->
                    SubPedidoGeneralUI(id = id, isLoading = true)
                }
            )

            ReportAction.CAMBIOS -> cambiosAdapter.submitList(
                createFakeList { id ->
                    SubCambioUI(codigo = id, isLoading = true)
                }
            )

            ReportAction.SOLES -> solesDetalleAdapter.submitList(
                createFakeList { id ->
                    SubProgresoUI(codigo = id, isLoading = true)
                }
            )
        }
    }

    private fun <T> createFakeList(
        size: Int = 7,
        factory: (Int) -> T
    ): List<T> {
        return List(size) { index ->
            factory(-index - 1)
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

    private fun <T, U> observe(
        flow: Flow<ResultadoApi<T>>,
        mapper: (T) -> List<U>,
        submit: (List<U>) -> Unit
    ) {
        collectFlow(flow) { result ->
            handleResultadoApi(result) { data ->
                val lista = data?.let(mapper) ?: emptyList()
                submit(lista)
            }
        }
    }

    private fun <T> handleResultadoApi(
        resultado: ResultadoApi<T>,
        onSuccess: (T?) -> Unit
    ) {
        when (resultado) {
            is ResultadoApi.Loading -> Unit

            is ResultadoApi.Exito -> onSuccess(resultado.data)

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
}
