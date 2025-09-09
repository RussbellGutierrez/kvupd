package com.upd.kvupd.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.upd.kvupd.databinding.CustomDialogProgressbarBinding
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.viewmodel.APIViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DSincronizarDiario : DialogFragment() {

    private val apiViewModel by activityViewModels<APIViewModel>()
    private val binding by viewBinding(CustomDialogProgressbarBinding::bind)
    private val _tag by lazy { DSincronizarDiario::class.java.simpleName }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = CustomDialogProgressbarBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        binding.btnProcesar.setOnClickListener {
            launchWorker()
        }
    }

    private fun launchWorker() {
        stateUI(true)
    }

    private fun stateUI(hide: Boolean) {
        binding.apply {
            txtMensaje.setUI("v", !hide)
            linear1.setUI("v", hide)
            btnProcesar.setUI("e", !hide)
            btnCancelar.setUI("e", !hide)
        }
    }
}