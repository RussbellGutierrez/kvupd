package com.upd.kvupd.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.upd.kvupd.databinding.CustomDialogLoadingBinding
import com.upd.kvupd.utils.setCreate
import com.upd.kvupd.utils.setResume
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OldDProgress: DialogFragment() {

    private var mensaje = ""
    private var _bind: CustomDialogLoadingBinding? = null
    private val bind get() = _bind!!
    private val _tag by lazy { OldDProgress::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCreate()
        if (arguments != null)
            mensaje = arguments?.getString("mensaje")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = CustomDialogLoadingBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onResume() {
        setResume()
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.txtDialog.text = mensaje
    }

}