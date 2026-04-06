package com.upd.kvupd.ui.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.upd.kvupd.databinding.BottomSolesdetalleBinding
import com.upd.kvupd.ui.fragment.reportes.adapter.detalle.ProgresoAdapter
import com.upd.kvupd.ui.fragment.reportes.mapper.ReporteMapper.toSubUI
import com.upd.kvupd.ui.fragment.reportes.modelUI.SubProgresoUI
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.ui.sealed.ResultadoApi
import com.upd.kvupd.utils.InstanciaDialog.REFERENCIA_DIALOG
import com.upd.kvupd.utils.InstanciaDialog.cerrarDialogActual
import com.upd.kvupd.utils.MaterialDialogTexto.T_ERROR
import com.upd.kvupd.utils.buildMaterialDialog
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.viewmodel.APIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@AndroidEntryPoint
class BDSolesDetalle : BottomSheetDialogFragment() {

    private val apiViewModel by activityViewModels<APIViewModel>()

    // 🔥 Binding manual (solo en el caso de usar recyclerview dentro de un dialog)
    private var _binding: BottomSolesdetalleBinding? = null
    private val binding get() = _binding!!

    private var linea: Int = 0
    private var titulo: String = ""
    private lateinit var adapter: ProgresoAdapter
    private val _tag by lazy { BDSolesDetalle::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            linea = it.getInt(ARG_LINEA)
            titulo = it.getString(ARG_TITULO).orEmpty()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSolesdetalleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupRecycler()
        observerData()

        // 🔥 Lanza descarga
        apiViewModel.apiSubSolesDetalle(linea)
    }

    override fun onStart() {
        super.onStart()

        val bottomSheet = dialog?.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        ) ?: return

        val behavior = BottomSheetBehavior.from(bottomSheet)

        // 🔥 altura máxima (por ejemplo 85% pantalla)
        val height = (resources.displayMetrics.heightPixels * 0.85).toInt()
        bottomSheet.layoutParams.height = height

        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun setupUI() {
        binding.txtDetalle.text = titulo
        binding.imgCerrar.setOnClickListener { dismiss() }
    }

    private fun setupRecycler() {

        adapter = ProgresoAdapter()

        binding.rcvSolesdetalle.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@BDSolesDetalle.adapter
        }

        // 🔥 Shimmer inicial (IMPORTANTE)
        adapter.submitList(
            List(7) { index ->
                SubProgresoUI(
                    codigo = -index - 1,
                    isLoading = true
                )
            }
        )
    }

    private fun observerData() {

        collectFlow(apiViewModel.subSolesEvent) { result ->

            handleResultadoApi(result) { data ->
                val lista = data?.toSubUI() ?: emptyList()
                Log.d(_tag, "Soles detalle: ${lista.size}")
                adapter.submitList(lista)
            }
        }
    }

    private fun mostrarDialog(dialogType: AppDialogType) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
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

    companion object {
        private const val ARG_LINEA = "linea"
        private const val ARG_TITULO = "titulo"

        fun newInstance(linea: Int, titulo: String): BDSolesDetalle {
            return BDSolesDetalle().apply {
                arguments = Bundle().apply {
                    putInt(ARG_LINEA, linea)
                    putString(ARG_TITULO, titulo)
                }
            }
        }
    }
}