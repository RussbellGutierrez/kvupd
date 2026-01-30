package com.upd.kvupd.ui.sealed

sealed class BajaEstados {
    object Reposo : BajaEstados()
    object ObteniendoUbicacion : BajaEstados()
    object Procesada : BajaEstados()
    object Error : BajaEstados()
}