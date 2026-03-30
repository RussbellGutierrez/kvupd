package com.upd.kvupd.ui.fragment.baja.behavior

import com.upd.kvupd.ui.fragment.baja.enumFile.VistaBaja

interface BajaBehavior {
    fun onDescargar()
    fun onToggleVista(current: VistaBaja): VistaBaja
}