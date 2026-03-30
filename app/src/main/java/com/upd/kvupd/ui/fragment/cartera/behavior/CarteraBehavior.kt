package com.upd.kvupd.ui.fragment.cartera.behavior

import android.content.Context

interface CarteraBehavior {
    fun onDescargar(context: Context, showMessage: (String) -> Unit)
}