package com.upd.kvupd.ui.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.annotation.RequiresApi
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.upd.kvupd.R
import com.upd.kvupd.databinding.DialogCarteraVendedorBinding
import com.upd.kvupd.ui.picker.DatePickerHelper

class CarteraVendedor(
    private val context: Context,
    private val vendedores: List<String>, // "codigo - nombre"
    private val onConfirm: (codigoVendedor: String, fecha: String?) -> Unit
) {

    fun show() {
        val binding = DialogCarteraVendedorBinding.inflate(
            LayoutInflater.from(context)
        )

        val dialog = MaterialDialog(context)
            .customView(view = binding.root, scrollable = false)

        var vendedorSeleccionado: String? = null
        var fechaSeleccionada: String? = null

        // Adapter para AutoCompleteTextView
        val adapterVendedores = ArrayAdapter(
            context,
            android.R.layout.simple_list_item_1,
            vendedores
        )

        binding.autoVendedor.setAdapter(adapterVendedores)

        binding.autoVendedor.setOnItemClickListener { parent, _, position, _ ->
            vendedorSeleccionado = parent.getItemAtPosition(position) as String
        }

        // DatePicker
        binding.btnFecha.setOnClickListener {
            DatePickerHelper.show(context) { fecha ->
                fechaSeleccionada = fecha
                binding.txtFecha.setText(fecha)
            }
        }

        // Boton Descargar
        binding.btnDescargar.setOnClickListener {
            val vendedor = vendedorSeleccionado ?: return@setOnClickListener
            val codigo = vendedor.substringBefore(" -")
            onConfirm(codigo, fechaSeleccionada)
            dialog.dismiss()
        }

        // Boton Cerrar
        binding.btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}