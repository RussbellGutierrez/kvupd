package com.upd.kv.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.upd.kv.R
import com.upd.kv.databinding.DialogLoginBinding
import com.upd.kv.databinding.DialogObservacionBinding
import com.upd.kv.utils.*
import com.upd.kv.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class DObservacion : BottomSheetDialogFragment() {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: DialogObservacionBinding? = null
    private val bind get() = _bind!!
    private val _tag by lazy { DObservacion::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = DialogObservacionBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}