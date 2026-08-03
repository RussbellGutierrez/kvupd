package com.upd.kvupd.ui.fragment.baja.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.data.model.core.TableBaja
import com.upd.kvupd.databinding.BottomBajaclienteBinding
import com.upd.kvupd.ui.fragment.baja.enumFile.MotivoBaja
import com.upd.kvupd.utils.FechaHoraUtil
import com.upd.kvupd.utils.snack
import com.upd.kvupd.utils.toUpper
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.viewmodel.APIViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BDBajaCliente : BottomSheetDialogFragment() {

    private val apiViewModel by activityViewModels<APIViewModel>()
    private val args: BDBajaClienteArgs by navArgs()
    private val binding by viewBinding(BottomBajaclienteBinding::bind)

    private lateinit var flowCliente: FlowCliente
    private var longitud = 0.0f
    private var latitud = 0.0f
    private var precision = 0.0f
    private val _tag by lazy { BDBajaCliente::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        flowCliente = args.cliente
        longitud = args.longitud
        latitud = args.latitud
        precision = args.precision
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = BottomBajaclienteBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            MotivoBaja.entries.map { it.label }
        )

        binding.apply {
            txtCliente.text = flowCliente.cliente
            txtNombre.text = flowCliente.nomcli
            txtDireccion.text = flowCliente.domicilio
            txtNegocio.text = flowCliente.negocio

            autoMotivo.setAdapter(adapter)

            btnAnular.setOnClickListener {
                procesarBaja()
            }
        }
    }

    private fun procesarBaja() {
        val comentario = binding.edtComentario.text.toString().trim()

        val motivo = MotivoBaja.entries
            .first { it.label == binding.autoMotivo.text.toString() }

        val item = TableBaja(
            cliente = flowCliente.cliente,
            nombre = flowCliente.nomcli,
            motivo = motivo.id,
            comentario = comentario.toUpper(),
            longitud = longitud.toDouble(),
            latitud = latitud.toDouble(),
            precision = precision.toDouble(),
            fecha = FechaHoraUtil.ahora(),
            anulado = 0
        )

        snack("Cliente ${item.nombre} dado de baja")
        apiViewModel.saveAndSendBaja(item)

        dismiss()
    }
}