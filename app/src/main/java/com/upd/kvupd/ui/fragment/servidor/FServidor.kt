package com.upd.kvupd.ui.fragment.servidor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.upd.kvupd.R
import com.upd.kvupd.databinding.FragmentFServidorBinding
import com.upd.kvupd.ui.fragment.servidor.adapter.GridSpacingItemDecoration
import com.upd.kvupd.ui.fragment.servidor.adapter.ServidorAdapter
import com.upd.kvupd.ui.fragment.servidor.enumFile.ApiServerStatus
import com.upd.kvupd.ui.fragment.servidor.enumFile.DrawablePosition
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.utils.ConstantsExtras.NO_FIND_UUID
import com.upd.kvupd.utils.InstanciaDialog.REFERENCIA_DIALOG
import com.upd.kvupd.utils.InstanciaDialog.cerrarDialogActual
import com.upd.kvupd.utils.MaterialDialogTexto.T_ERROR
import com.upd.kvupd.utils.buildMaterialDialog
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.utils.setDrawableTint
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.viewmodel.ALLViewModel
import com.upd.kvupd.viewmodel.APIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@AndroidEntryPoint
class FServidor : Fragment() {

    private val apiViewmodel by activityViewModels<APIViewModel>()
    private val localViewmodel by activityViewModels<ALLViewModel>()
    private val binding by viewBinding(FragmentFServidorBinding::bind)

    private var isUploading = false
    private var shouldUpload = false
    private lateinit var adapter: ServidorAdapter
    private val _tag by lazy { FServidor::class.java.simpleName }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFServidorBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUUIDparam()
        uiButtons()
        initAdapter()
        observeData()
        updateErrorUI()

        apiViewmodel.resetItemsState()
        apiViewmodel.loadServerData()
        apiViewmodel.verifyStatusAndUpload()
    }

    private fun uiButtons() {
        binding.cardLanzar.setOnClickListener {

            if (isUploading) return@setOnClickListener

            isUploading = true

            apiViewmodel.clearErrors()       // 🔥 LIMPIA ERRORES
            apiViewmodel.resetItemsState()   // 🔥 RESETEA ITEMS

            updateErrorUI()                  // 🔥 ACTUALIZA UI (gris + disabled)

            apiViewmodel.verifyStatusAndUpload()
        }

        binding.cardErrores.setOnClickListener {

            val errores = apiViewmodel.errorMap.values.flatten()
            if (errores.isEmpty()) return@setOnClickListener

            val mensaje = errores.joinToString("\n")

            mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = mensaje
                )
            )
        }
    }

    private fun observeData() {

        collectFlow(apiViewmodel.items) { list ->
            adapter.submitList(list)
        }

        collectFlow(apiViewmodel.status) { status ->

            binding.txtEstado.text = status.message

            val color = ContextCompat.getColor(
                requireContext(),
                status.status.colorRes
            )
            binding.imgEstado.setColorFilter(color)

            if (status.status == ApiServerStatus.ERROR) {
                isUploading = false
                updateErrorUI()
            }
        }

        collectFlow(apiViewmodel.uploadFinished) {
            isUploading = false
            updateErrorUI()
        }
    }

    private fun setUUIDparam() {
        val uuid = localViewmodel.obtenerUUID()
            .takeUnless { it.isNullOrBlank() }
            ?: NO_FIND_UUID

        apiViewmodel.setExtraParam(uuid)
    }

    private fun initAdapter() {
        adapter = ServidorAdapter()

        val spacing = (10 * resources.displayMetrics.density).toInt()

        binding.rcvServidor.layoutManager =
            GridLayoutManager(requireContext(), 2)

        binding.rcvServidor.addItemDecoration(
            GridSpacingItemDecoration(2, spacing, true)
        )

        binding.rcvServidor.adapter = adapter
    }

    private fun updateErrorUI() {

        val hasErrors = apiViewmodel.errorMap.isNotEmpty()

        val color = ContextCompat.getColor(
            requireContext(),
            if (hasErrors) R.color.lightcrimson else R.color.lightgray
        )

        binding.txtErrores.setTextColor(color)
        binding.txtErrores.setDrawableTint(DrawablePosition.END, color)

        // 🔥 aquí entra isUploading
        binding.cardLanzar.isEnabled = !isUploading
        binding.cardErrores.isEnabled = hasErrors && !isUploading
        binding.cardErrores.isClickable = hasErrors && !isUploading
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