package com.upd.kvupd.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.upd.kvupd.data.model.BajaAux
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.databinding.BottomBajaclienteBinding
import com.upd.kvupd.ui.fragment.enumClass.MotivoBaja
import com.upd.kvupd.utils.BajaConstantes.KEY_BAJA
import com.upd.kvupd.utils.BajaConstantes.PAIR_BAJA
import com.upd.kvupd.utils.FechaHoraUtil
import com.upd.kvupd.utils.toUpper
import com.upd.kvupd.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BDDetalleBaja : BottomSheetDialogFragment() {

    private val args: BDBajaClienteArgs by navArgs()
    private val binding by viewBinding(BottomBajaclienteBinding::bind)

    private lateinit var cliente: FlowCliente
    private val _tag by lazy { BDDetalleBaja::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cliente = args.cliente
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
            txtCliente.text = cliente.cliente
            txtNombre.text = cliente.nomcli
            txtDireccion.text = cliente.domicilio
            txtNegocio.text = cliente.negocio

            autoMotivo.setAdapter(adapter)

            btnAnular.setOnClickListener {
                devolverDatosFragment()
            }
        }
    }

    private fun recolectarDatos(): BajaAux {
        val comentario = binding.edtComentario.text.toString().trim()

        val motivo = MotivoBaja.entries
            .first { it.label == binding.autoMotivo.text.toString() }

        return BajaAux(
            cliente = cliente,
            motivo = motivo.id,
            comentario = comentario.toUpper(),
            fecha = FechaHoraUtil.ahora()
        )
    }

    private fun devolverDatosFragment() {
        val baja = recolectarDatos()

        parentFragmentManager.setFragmentResult(
            KEY_BAJA,
            bundleOf(PAIR_BAJA to baja)
        )

        dismiss()
    }
}