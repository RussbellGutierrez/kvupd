package com.upd.kvupd.ui.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.upd.kvupd.data.model.Cabecera
import com.upd.kvupd.data.model.TEncuestaSeleccionado
import com.upd.kvupd.databinding.DialogListaEncuestaBinding
import com.upd.kvupd.utils.*
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DListaEncuesta : DialogFragment() {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: DialogListaEncuestaBinding? = null
    private val bind get() = _bind!!
    private var seleccion = ""
    private val lista = mutableListOf<Cabecera>()
    private val _tag by lazy { DListaEncuesta::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = DialogListaEncuestaBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onResume() {
        setResume()
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setup()

        bind.rbGrupo.setOnCheckedChangeListener { _, id ->
            lista.forEach {
                if (it.id == id) {
                    seleccion = "${it.id}@${it.foto}"
                }
            }
        }
        bind.btnSeleccion.setOnClickListener { saveSeleccion() }

        viewmodel.cabecera.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                y.forEach { j ->
                    Log.d(_tag,"Encuesta $j")
                    val rb = RadioButton(requireActivity())
                    rb.text = j.nombre
                    rb.id = j.id
                    if (j.seleccion > 0) {
                        rb.isChecked = true
                        seleccion = "${j.id}@${j.foto}"
                    }
                    bind.rbGrupo.addView(rb)
                    lista.add(j)
                }
            }
        }
    }

    private fun setup() {
        viewmodel.gettingEncuestaLista()
    }

    private fun saveSeleccion() {
        val datos = seleccion.split("@")
        val item = TEncuestaSeleccionado(1,datos[0].toInt(),datos[1].toBoolean())
        viewmodel.saveSeleccion(item)
        dismiss()
    }
}