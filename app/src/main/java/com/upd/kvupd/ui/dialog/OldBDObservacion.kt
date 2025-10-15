package com.upd.kvupd.ui.dialog

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.upd.kvupd.databinding.BottomDialogObservacionBinding
import com.upd.kvupd.service.ServicePosicion
import com.upd.kvupd.viewmodel.OldAppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OldBDObservacion : BottomSheetDialogFragment() {

    private val viewmodel by activityViewModels<OldAppViewModel>()
    private var _bind: BottomDialogObservacionBinding? = null
    private val bind get() = _bind!!
    private var cliente = 0
    private var nombre = ""
    private var ruta = 0
    private var obs = 0
    private val args: OldBDObservacionArgs by navArgs()
    private val _tag by lazy { OldBDObservacion::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
        requireContext().stopService(Intent(requireContext(), ServicePosicion::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewmodel.launchPosition()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _bind = BottomDialogObservacionBinding.inflate(inflater, container, false)
        return bind.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.cliente?.let {
            cliente = it.id
            nombre = it.nombre
            ruta = it.ruta
            bind.txtCliente.text = "$cliente - $nombre"
        }
        bind.fabPedido.setOnClickListener { saveVisita(0) }
        bind.fabPuesto.setOnClickListener { saveVisita(1) }
        bind.fabProducto.setOnClickListener { saveVisita(2) }
        bind.fabDinero.setOnClickListener { saveVisita(3) }
        bind.fabEncargado.setOnClickListener { saveVisita(4) }
        bind.fabTiempo.setOnClickListener { saveVisita(5) }
        bind.fabOcupado.setOnClickListener { saveVisita(6) }
        bind.fabExiste.setOnClickListener { saveVisita(7) }

        /*viewmodel.cabecera.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                if (y.isNotEmpty() && (obs == 0 || obs == 2 || obs == 3)) {
                    viewmodel.clienteRespondio(y, cliente.toString())
                } else {
                    dismiss()
                }
            }
        }
        viewmodel.clienteRespondio.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                dismiss()
                if (!y) {
                    findNavController().navigate(
                        OldBDObservacionDirections.actionBDObservacionToFEncuesta(args.cliente)
                    )
                }
            }
        }*/
    }

    private fun saveVisita(seleccion: Int) {

        /*if (isPOSLOCinitialized() && (POS_LOC.longitude != 0.0 && POS_LOC.latitude != 0.0)) {
            obs = seleccion
            val fecha = viewmodel.fecha(4)

            val item = TVisita(
                cliente,
                fecha,
                CONF.codigo,
                POS_LOC.longitude,
                POS_LOC.latitude,
                obs,
                POS_LOC.accuracy.toDouble(),
                "Pendiente"
            )
            viewmodel.saveVisita(item, ruta)
            viewmodel.gettingEncuestaLista()
            toast("Cliente ${item.cliente} - ${checkObs(obs)}")
        } else {
            toast("No se encontro coordenadas")
        }*/
    }

    private fun checkObs(obs: Int): String {
        var result = ""
        when (obs) {
            0 -> result = "Hizo pedido"
            1 -> result = "Puesto cerrado"
            2 -> result = "Tiene producto"
            3 -> result = "Sin dinero"
            4 -> result = "Sin encargado"
            5 -> result = "Falta de tiempo"
            6 -> result = "Cliente ocupado"
            7 -> result = "No existe"
        }
        return result
    }
}