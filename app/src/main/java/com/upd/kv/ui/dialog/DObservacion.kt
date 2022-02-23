package com.upd.kv.ui.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.upd.kv.data.model.TVisita
import com.upd.kv.databinding.DialogObservacionBinding
import com.upd.kv.service.ServicePosicion
import com.upd.kv.utils.Constant.CONF
import com.upd.kv.utils.Constant.POS_LOC
import com.upd.kv.utils.snack
import com.upd.kv.utils.toast
import com.upd.kv.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DObservacion : BottomSheetDialogFragment() {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: DialogObservacionBinding? = null
    private val bind get() = _bind!!
    private val args: DObservacionArgs by navArgs()
    private val _tag by lazy { DObservacion::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
        POS_LOC = null
        requireContext().stopService(Intent(requireContext(), ServicePosicion::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel.launchPosition()
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

        val parg = args.cliente.split("-")
        bind.txtCliente.text = nameCliente(parg)
        bind.fabPedido.setOnClickListener { saveVisita(0) }
        bind.fabPuesto.setOnClickListener { saveVisita(1) }
        bind.fabProducto.setOnClickListener { saveVisita(2) }
        bind.fabDinero.setOnClickListener { saveVisita(3) }
        bind.fabEncargado.setOnClickListener { saveVisita(4) }
        bind.fabOcupado.setOnClickListener { saveVisita(6) }
        bind.fabExiste.setOnClickListener { saveVisita(7) }
    }

    private fun nameCliente(list: List<String>): String {
        val p0 = list[0].trim()
        val p1 = list[1].trim()
        return "$p0 - $p1"
    }

    private fun saveVisita(obs: Int) {
        if (POS_LOC != null) {

            val cliente = args.cliente.split("-")[0].trim().toInt()
            val ruta = args.cliente.split("-")[2].trim().toInt()
            val fecha = viewmodel.fecha(4)

            val item = TVisita(
                cliente,
                fecha,
                CONF.codigo,
                POS_LOC!!.longitude,
                POS_LOC!!.latitude,
                obs,
                POS_LOC!!.accuracy.toDouble(),
                "Pendiente"
            )
            viewmodel.saveVisita(item, ruta)
            dismiss()
        } else {
            toast("No se encontro coordenadas")
        }
    }
}