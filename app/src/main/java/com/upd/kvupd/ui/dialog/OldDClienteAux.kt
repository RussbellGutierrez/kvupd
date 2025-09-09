package com.upd.kvupd.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.upd.kvupd.databinding.DialogClienteAuxBinding
import com.upd.kvupd.utils.OldConstant.PROCEDE
import com.upd.kvupd.utils.setCreate
import com.upd.kvupd.utils.setResume
import com.upd.kvupd.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OldDClienteAux : DialogFragment() {

    private var _bind: DialogClienteAuxBinding? = null
    private val arg: OldDClienteAuxArgs by navArgs()
    private val bind get() = _bind!!
    private val _tag by lazy { OldDClienteAux::class.java.simpleName }

    override fun onDestroy() {
        super.onDestroy()
        _bind = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = DialogClienteAuxBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onResume() {
        setResume()
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arg.cliente?.let {
            val cliente = "${arg.cliente?.id}-${arg.cliente?.nombre}"
            bind.txtCliente.text = cliente
        }

        bind.btnMapa.setOnClickListener {
            if (PROCEDE == "Mapa") {
                toast("Ya se encuentra en el mapa")
            } else {
                findNavController().navigate(
                    OldDClienteAuxDirections.actionDClienteAuxToFMapa(arg.cliente)
                )
                dismiss()
            }
        }
        bind.btnBaja.setOnClickListener {
            findNavController().navigate(
                OldDClienteAuxDirections.actionDClienteAuxToDBaja(arg.cliente)
            )
        }
    }
}