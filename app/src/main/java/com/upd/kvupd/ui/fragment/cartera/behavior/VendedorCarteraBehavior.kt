package com.upd.kvupd.ui.fragment.cartera.behavior

import android.content.Context
import com.upd.kvupd.ui.picker.DatePickerHelper
import com.upd.kvupd.viewmodel.APIViewModel

class VendedorCarteraBehavior(
    private val api: APIViewModel
) : CarteraBehavior {

    override fun onDescargar(context: Context, showMessage: (String) -> Unit) {
        DatePickerHelper.show(context) { fecha ->
            api.downloadClientes(fecha = fecha)
        }
    }
}