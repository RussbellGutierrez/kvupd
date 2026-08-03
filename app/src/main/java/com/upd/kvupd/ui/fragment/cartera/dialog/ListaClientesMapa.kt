package com.upd.kvupd.ui.fragment.cartera.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
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
            clientes.toMutableList()
        )

        binding.listClientes.adapter = adapter

        // Buscar cliente
        binding.edtBusqueda.doAfterTextChanged { editable ->

            val texto = editable.toString().trim()

            val filtrados = if (texto.isEmpty()) {
                clientes
            } else {
                clientes.filter {
                    it.nomcli.contains(texto, ignoreCase = true) ||
                            it.cliente.contains(texto)
                }
            }

            adapter.clear()
            adapter.addAll(filtrados)
            adapter.notifyDataSetChanged()
        }

        // Selección
        binding.listClientes.setOnItemClickListener { _, _, position, _ ->
            val clienteSeleccionado = adapter.getItem(position)
            clienteSeleccionado?.let {
                onSelect(it)
            }
            dialog.dismiss()
        }

        // Cerrar
        binding.btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}