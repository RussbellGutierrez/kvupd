package com.upd.kv.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.upd.kv.databinding.DialogProgressBinding
import com.upd.kv.utils.setCreate
import com.upd.kv.utils.setResume
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DProgress: DialogFragment() {

    private var mensaje = ""
    private var _bind: DialogProgressBinding? = null
    private val bind get() = _bind!!
    private val _tag by lazy { DProgress::class.java.simpleName }

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
        _bind = DialogProgressBinding.inflate(inflater, container, false)
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