package com.upd.kvupd.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.databinding.DialogClientesMapaBinding

class ListaClientesMapa(
    private val context: Context,
    private val clientes: List<FlowCliente>,
    private val onSelect: (cliente: FlowCliente) -> Unit
) {

    fun show() {
        val binding = DialogClientesMapaBinding.inflate(
            LayoutInflater.from(context)
        )

        val dialog = MaterialDialog(context)
            .customView(view = binding.root, scrollable = false)

        val adapter = ArrayAdapter(
            context,
            android.R.layout.simple_list_item_1,
            clientes
        )

        binding.listClientes.adapter = adapter

        // ListView Clientes
        binding.listClientes.setOnItemClickListener { _, _, position, _ ->
            val clienteSeleccionado = clientes[position]
            onSelect(clienteSeleccionado)
            dialog.dismiss()
        }

        // Boton Cerrar
        binding.btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}