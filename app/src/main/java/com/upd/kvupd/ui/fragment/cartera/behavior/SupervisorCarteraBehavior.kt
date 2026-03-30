package com.upd.kvupd.ui.fragment.cartera.behavior

import android.content.Context
import com.upd.kvupd.ui.dialog.CarteraSupervisor
import com.upd.kvupd.ui.fragment.cartera.modelUI.VendedorItem
import com.upd.kvupd.viewmodel.APIViewModel

class SupervisorCarteraBehavior(
    private val api: APIViewModel,
) : CarteraBehavior {

    override fun onDescargar(context: Context, showMessage: (String) -> Unit) {
        val vendedores = api.flowVendedores.value

        if (vendedores.isEmpty()) {
            showMessage("No hay vendedores disponibles")
            return
        }

        val lista = vendedores.map {
            VendedorItem(
                codigo = it.codigo,
                nombre = it.descripcion
            )
        }

        CarteraSupervisor(context, lista) { codigo, fecha ->
            api.downloadClientes(codigo.toInt(), fecha)
        }.show()
    }
}