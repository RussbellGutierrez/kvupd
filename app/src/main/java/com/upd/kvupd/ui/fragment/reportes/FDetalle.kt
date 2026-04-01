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
import com.upd.kvupd.databinding.FragmentFDetalleBinding
import com.upd.kvupd.ui.fragment.reportes.modelUI.DetalleParams
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
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@AndroidEntryPoint
class FDetalle : Fragment() {

    private val apiViewModel by activityViewModels<APIViewModel>()
    private val args: FDetalleArgs by navArgs()
    private val binding by viewBinding(FragmentFDetalleBinding::bind)

    private var detParams: DetalleParams? = null
    private val _tag by lazy { FDetalle::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detParams = args.params
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFDetalleBinding.inflate(inflater, container, false).root

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observerData()
        launchApiDownload()
    }

    private fun observerData() {
        collectFlow(apiViewModel.preventaEvent) { result ->
            handleResultadoApi(result) {
                //
            }
        }

        collectFlow(apiViewModel.coberturaEvent) { result ->
            handleResultadoApi(result) {
                //
            }
        }

        collectFlow(apiViewModel.coberturaDetalleEvent) { result ->
            handleResultadoApi(result) {
                //
            }
        }

        collectFlow(apiViewModel.carteraEvent) { result ->
            handleResultadoApi(result) {
                //
            }
        }

        collectFlow(apiViewModel.coberturaPendienteEvent) { result ->
            handleResultadoApi(result) {
                //
            }
        }

        collectFlow(apiViewModel.pedidoEmpleadoEvent) { result ->
            handleResultadoApi(result) {
                //
            }
        }

        collectFlow(apiViewModel.cambioEvent) { resutl ->
            handleResultadoApi(resutl) {
                //
            }
        }
    }

    private fun launchApiDownload() {
        detParams?.let {
            apiViewModel.downloadDetailReport(it)
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
