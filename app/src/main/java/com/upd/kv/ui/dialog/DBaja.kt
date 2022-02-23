package com.upd.kv.ui.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.upd.kv.data.model.TBaja
import com.upd.kv.data.model.TVisita
import com.upd.kv.databinding.DialogBajaBinding
import com.upd.kv.service.ServicePosicion
import com.upd.kv.utils.*
import com.upd.kv.utils.Constant.POS_LOC
import com.upd.kv.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DBaja : DialogFragment(), AdapterView.OnItemSelectedListener {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: DialogBajaBinding? = null
    private val bind get() = _bind!!
    private val args: DBajaArgs by navArgs()
    private val _tag by lazy { DBaja::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
        POS_LOC = null
        requireContext().stopService(Intent(requireContext(), ServicePosicion::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCreate()
        viewmodel.launchPosition()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = DialogBajaBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parg = args.cliente.split("-")
        bind.txtCliente.text = nameCliente(parg)
        bind.spnMotivo.setSelection(0, false)
        bind.spnMotivo.onItemSelectedListener = this
        bind.btnAnular.setOnClickListener { checkFields() }
    }

    override fun onItemSelected(p0: AdapterView<*>, p1: View?, p2: Int, p3: Long) {
        val motivo = p0.getItemAtPosition(p2)
        if (motivo.toString() == "DUPLICADO") {
            bind.txtAviso.setUI("v", true)
        } else {
            bind.txtAviso.setUI("v", false)
        }
        bind.txtMensaje.setUI("v", false)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) = Unit

    private fun nameCliente(list: List<String>): String {
        val p0 = list[0].trim()
        val p1 = list[1].trim()
        return "$p0 - $p1"
    }

    private fun checkFields() {
        if (POS_LOC != null) {
            val motivo = when (bind.spnMotivo.selectedItem.toString()) {
                "DUPLICADO" -> 0
                "NO EXISTE" -> 1
                "CAMBIO GIRO" -> 2
                "CERRADO" -> 3
                else -> 4
            }
            if (motivo < 4) {
                val cliente = args.cliente.split("-")[0].trim().toInt()
                val ruta = args.cliente.split("-")[2].trim().toInt()
                val comentario = bind.edtComentario.text.toString()
                val fecha = viewmodel.fecha(4)
                val item = TBaja(
                    cliente,
                    motivo,
                    comentario,
                    POS_LOC!!.longitude,
                    POS_LOC!!.latitude,
                    POS_LOC!!.accuracy.toDouble(),
                    fecha,
                    0,
                    "Pendiente"
                )
                if (motivo < 1 && comentario.trim() == "") {
                    bind.txtMensaje.setUI("v", true)
                    bind.txtMensaje.text = "Ingrese el codigo duplicado en comentario"
                }else {
                    viewmodel.saveBaja(item, ruta)
                    dismiss()
                }
            } else {
                bind.txtMensaje.setUI("v", true)
                bind.txtMensaje.text = "Debe seleccionar un motivo de baja"
            }
        } else {
            toast("No se encontro coordenadas")
        }
    }
}