package com.upd.kvupd.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.upd.kvupd.databinding.DialogFiltroObsBinding
import com.upd.kvupd.utils.Constant.FILTRO_OBS
import com.upd.kvupd.utils.setCreate
import com.upd.kvupd.utils.setResume
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DFiltroObs : DialogFragment() {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: DialogFiltroObsBinding? = null
    private val bind get() = _bind!!
    private val _tag by lazy { DFiltroObs::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = DialogFiltroObsBinding.inflate(inflater, container,false)
        return bind.root
    }

    override fun onResume() {
        setResume()
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkingRadio()

        bind.rbGrupo.setOnCheckedChangeListener { _, id ->
            when (id) {
                bind.rbTodos.id -> {
                    FILTRO_OBS = 9
                    viewmodel.filterMarkerObs(9)
                    dismiss()
                }
                bind.rbCerrado.id -> {
                    FILTRO_OBS = 1
                    viewmodel.filterMarkerObs(1)
                    dismiss()
                }
                bind.rbProducto.id -> {
                    FILTRO_OBS = 2
                    viewmodel.filterMarkerObs(2)
                    dismiss()
                }
                bind.rbDinero.id -> {
                    FILTRO_OBS = 3
                    viewmodel.filterMarkerObs(3)
                    dismiss()
                }
                bind.rbEncargado.id -> {
                    FILTRO_OBS = 4
                    viewmodel.filterMarkerObs(4)
                    dismiss()
                }
                bind.rbOcupado.id -> {
                    FILTRO_OBS = 6
                    viewmodel.filterMarkerObs(6)
                    dismiss()
                }
            }
        }
    }

    private fun checkingRadio() {
        when(FILTRO_OBS) {
            1 -> bind.rbCerrado.isChecked = true
            2 -> bind.rbProducto.isChecked = true
            3 -> bind.rbDinero.isChecked = true
            4 -> bind.rbEncargado.isChecked = true
            6 -> bind.rbOcupado.isChecked = true
            9 -> bind.rbTodos.isChecked = true
        }
    }
}