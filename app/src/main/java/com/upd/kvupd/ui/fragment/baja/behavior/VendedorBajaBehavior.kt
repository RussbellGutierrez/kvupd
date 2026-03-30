package com.upd.kvupd.ui.fragment.baja.behavior

import com.upd.kvupd.ui.fragment.baja.enumFile.VistaBaja
import com.upd.kvupd.viewmodel.APIViewModel

class VendedorBajaBehavior(
    private val api: APIViewModel
) : BajaBehavior {

    override fun onDescargar() {
        api.downloadAndShowBajas()
    }

    override fun onToggleVista(current: VistaBaja): VistaBaja {
        return VistaBaja.GENERADO // siempre lista
    }
}