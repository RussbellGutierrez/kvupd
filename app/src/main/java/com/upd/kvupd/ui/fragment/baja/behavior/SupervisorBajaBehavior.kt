package com.upd.kvupd.ui.fragment.baja.behavior

import com.upd.kvupd.ui.fragment.baja.enumFile.VistaBaja
import com.upd.kvupd.viewmodel.APIViewModel

class SupervisorBajaBehavior(
    private val api: APIViewModel
) : BajaBehavior {

    override fun onDescargar() {
        api.downloadBajasSupervisor()
    }

    override fun onToggleVista(current: VistaBaja): VistaBaja {
        return if (current == VistaBaja.GENERADO)
            VistaBaja.PROCESAR
        else
            VistaBaja.GENERADO
    }
}