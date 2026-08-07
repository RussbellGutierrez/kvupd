package com.upd.kvupd.ui.fragment.cartera.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.upd.kvupd.databinding.DialogCarteraVendedorBinding
import com.upd.kvupd.databinding.DialogNegocioFiltroBinding
import com.upd.kvupd.ui.fragment.cartera.modelUI.VendedorItem
import com.upd.kvupd.ui.picker.DatePickerHelper

class NegocioFiltro(
    private val context: Context,
    private val negocios: List<String>,
    private val onConfirm: (negocio: String) -> Unit
) {

    fun show() {

        val binding = DialogNegocioFiltroBinding.inflate(
            LayoutInflater.from(context)
        )

        val dialog = MaterialDialog(context)
            .customView(
                view = binding.root,
                scrollable = false
            )

        var negocioSeleccionado: String? = null

        // Adapter AutoComplete
        val adapterNegocios = ArrayAdapter(
            context,
            android.R.layout.simple_list_item_1,
            negocios
        )

        binding.autoNegocios.setAdapter(
            adapterNegocios
        )

        binding.autoNegocios.setOnItemClickListener { _, _, position, _ ->

            negocioSeleccionado =
                adapterNegocios.getItem(position)
        }

        // Botón Filtrar
        binding.btnFiltro.setOnClickListener {

            val negocio =
                negocioSeleccionado
                    ?: return@setOnClickListener

            onConfirm(negocio)
            dialog.dismiss()
        }

        // Botón cerrar
        binding.btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}