package com.upd.kvupd.ui.fragment.cartera.behavior

import android.content.Context
import com.upd.kvupd.ui.fragment.cartera.dialog.CarteraSupervisor
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

        val lista = vendedores
            .filter { it.cargo == "V" }
            .map {
                VendedorItem(
                    codigo = it.codigo,
                    nombre = it.descripcion,
                    cargo = it.cargo
                )
            }

        CarteraSupervisor(
            context,
            lista,
            onConfirm = { codigo, fecha ->
                api.downloadClientes(codigo.toInt(), fecha)
            },
            onError = { mensaje ->
                showMessage(mensaje)
            }
        ).show()
    }
}