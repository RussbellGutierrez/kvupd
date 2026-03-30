package com.upd.kvupd.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.upd.kvupd.databinding.DialogCarteraVendedorBinding
import com.upd.kvupd.ui.fragment.cartera.modelUI.VendedorItem
import com.upd.kvupd.ui.picker.DatePickerHelper

class CarteraSupervisor(
    private val context: Context,
    private val vendedores: List<VendedorItem>,
    private val onConfirm: (codigoVendedor: String, fecha: String?) -> Unit
) {

    fun show() {
        val binding = DialogCarteraVendedorBinding.inflate(
            LayoutInflater.from(context)
        )

        val dialog = MaterialDialog(context)
            .customView(view = binding.root, scrollable = false)

        var vendedorSeleccionado: VendedorItem? = null
        var fechaSeleccionada: String? = null

        // Adapter para AutoCompleteTextView
        val adapterVendedores = ArrayAdapter(
            context,
            android.R.layout.simple_list_item_1,
            vendedores
        )

        binding.autoVendedor.setAdapter(adapterVendedores)

        binding.autoVendedor.setOnItemClickListener { _, _, position, _ ->
            vendedorSeleccionado = adapterVendedores.getItem(position)
        }

        // DatePicker
        binding.btnFecha.setOnClickListener {
            DatePickerHelper.show(context) { fecha ->
                fechaSeleccionada = fecha
                binding.txtFecha.text = fecha
            }
        }

        // Boton Descargar
        binding.btnDescargar.setOnClickListener {
            val vendedor = vendedorSeleccionado ?: return@setOnClickListener
            onConfirm(vendedor.codigo, fechaSeleccionada)
            dialog.dismiss()
        }

        // Boton Cerrar
        binding.btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}