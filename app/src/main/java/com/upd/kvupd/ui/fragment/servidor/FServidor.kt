package com.upd.kvupd.ui.fragment.servidor

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.upd.kvupd.databinding.FragmentFServidorBinding
import com.upd.kvupd.ui.fragment.servidor.adapter.GridSpacingItemDecoration
import com.upd.kvupd.ui.fragment.servidor.adapter.ServidorAdapter
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.utils.InstanciaDialog.REFERENCIA_DIALOG
import com.upd.kvupd.utils.InstanciaDialog.cerrarDialogActual
import com.upd.kvupd.utils.buildMaterialDialog
import com.upd.kvupd.utils.collectFlow
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

    private lateinit var adapter: ServidorAdapter
    private val _tag by lazy { FServidor::class.java.simpleName }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFServidorBinding.inflate(inflater, container, false).root

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        observeData()

        apiViewmodel.loadServerData()
        apiViewmodel.verifyStatusServidor()

        /*binding.txtUuid.text =
            localViewModel.obtenerUUID().takeUnless { it.isNullOrBlank() }
                ?: NO_FIND_UUID*/
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
        }
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

    private fun mostrarDialog(dialogType: AppDialogType) {
        lifecycleScope.launch(Dispatchers.Main) {
            cerrarDialogActual()

            val dialog = buildMaterialDialog(requireContext(), dialogType)
            dialog.show()

            REFERENCIA_DIALOG = WeakReference(dialog)
        }
    }
}