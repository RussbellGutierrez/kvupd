package com.upd.kvupd.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.upd.kvupd.databinding.BottomDialogEmergenciaBinding
import com.upd.kvupd.utils.OldConstant.IP_FILTER
import com.upd.kvupd.utils.OldRegexMaskTextWatcher
import com.upd.kvupd.viewmodel.OldAppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OldBDEmergencia : BottomSheetDialogFragment() {

    private val viewmodel by activityViewModels<OldAppViewModel>()
    private var _bind: BottomDialogEmergenciaBinding? = null
    private val bind get() = _bind!!
    private val _tag by lazy { OldBDEmergencia::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = BottomDialogEmergenciaBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.edtIp.addTextChangedListener(OldRegexMaskTextWatcher(IP_FILTER))

        bind.btnAuxiliar.setOnClickListener {
            val ip = bind.edtIp.text.toString()
            if (ip != "") {
                dismiss()
                //viewmodel.settingIPaux(ip)
            }
        }
    }
}