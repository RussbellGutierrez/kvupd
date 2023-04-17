package com.upd.kvupd.ui.dialog

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.upd.kvupd.data.model.TBaja
import com.upd.kvupd.databinding.DialogBajaBinding
import com.upd.kvupd.service.ServicePosicion
import com.upd.kvupd.utils.*
import com.upd.kvupd.utils.Constant.POS_LOC
import com.upd.kvupd.utils.Constant.isPOSLOCinitialized
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DBaja : DialogFragment(), AdapterView.OnItemSelectedListener {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: DialogBajaBinding? = null
    private val bind get() = _bind!!
    private val args: DBajaArgs by navArgs()
    private var cliente = 0
    private var nombre = ""
    private var ruta = 0
    private val _tag by lazy { DBaja::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
        POS_LOC.longitude = 0.0
        POS_LOC.latitude = 0.0
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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val parg = args.cliente.split("-")
        //bind.txtCliente.text = nameCliente(parg)
        args.cliente?.let {
            cliente = it.id
            nombre = it.nombre
            ruta = it.ruta
            bind.txtCliente.text = "$cliente - $nombre"
        }
        bind.spnMotivo.setSelection(0, false)
        bind.spnMotivo.onItemSelectedListener = this
        bind.btnAnular.setOnClickListener { checkFields() }
    }

    override fun onResume() {
        setResume()
        super.onResume()
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

    private fun checkFields() {
        if (isPOSLOCinitialized() &&
            POS_LOC.longitude != 0.0 && POS_LOC.latitude != 0.0) {
            val motivo = when (bind.spnMotivo.selectedItem.toString()) {
                "DUPLICADO" -> 1
                "NO EXISTE" -> 2
                "CERRADO" -> 3
                else -> 4
            }
            val comentario = bind.edtComentario.text.toString()
            val fecha = viewmodel.fecha(4)
            val item = TBaja(
                cliente,
                nombre,
                motivo,
                comentario,
                POS_LOC.longitude,
                POS_LOC.latitude,
                POS_LOC.accuracy.toDouble(),
                fecha,
                0,
                "Pendiente"
            )
            if (motivo == 1 && comentario.trim() == "") {
                bind.txtMensaje.setUI("v", true)
                bind.txtMensaje.text = "Ingrese el codigo duplicado en comentario"
            }else {
                viewmodel.saveBaja(item, ruta)
                dismiss()
            }
        } else {
            toast("No se encontro coordenadas")
        }
    }

}