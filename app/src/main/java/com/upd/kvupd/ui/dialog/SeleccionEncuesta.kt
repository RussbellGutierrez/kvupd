package com.upd.kvupd.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.upd.kvupd.databinding.DialogSeleccionEncuestaBinding
import com.upd.kvupd.ui.fragment.encuesta.modelUI.EncuestaUI

class SeleccionEncuesta(
    private val context: Context,
    private val encuestas: List<EncuestaUI>,
    private val onSelect: (id: Int) -> Unit
) {

    fun show() {

        // 🔥 caso especial: solo 1 encuesta → no mostrar dialog
        if (encuestas.size == 1) {
            onSelect(encuestas.first().id)
            return
        }

        val binding = DialogSeleccionEncuestaBinding.inflate(
            LayoutInflater.from(context)
        )

        val dialog = MaterialDialog(context)
            .customView(view = binding.root, scrollable = false)
            .cancelable(false)

        binding.rgEncuesta.removeAllViews()

        encuestas.forEach { encuesta ->

            val radio = RadioButton(context).apply {
                text = encuesta.toString()
                id = View.generateViewId()
                isChecked = encuesta.seleccionado

                // 🔥 SIEMPRE responde, incluso misma opción
                setOnClickListener {
                    onSelect(encuesta.id)
                    dialog.dismiss()
                }
            }

            binding.rgEncuesta.addView(radio)
        }

        dialog.show()
    }
}