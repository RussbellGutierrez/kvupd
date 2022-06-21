package com.upd.kvupd.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.upd.kvupd.databinding.DialogFiltroBinding
import com.upd.kvupd.utils.setCreate
import com.upd.kvupd.utils.setResume
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DFiltro : DialogFragment() {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: DialogFiltroBinding? = null
    private val bind get() = _bind!!
    private val _tag by lazy { DFiltro::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = DialogFiltroBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onResume() {
        setResume()
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.fabLunes.setOnClickListener { setDayFilter(1) }
        bind.fabMartes.setOnClickListener { setDayFilter(2) }
        bind.fabMiercoles.setOnClickListener { setDayFilter(3) }
        bind.fabJueves.setOnClickListener { setDayFilter(4) }
        bind.fabViernes.setOnClickListener { setDayFilter(5) }
        bind.fabSabado.setOnClickListener { setDayFilter(6) }
        bind.fabTodo.setOnClickListener { setDayFilter(0) }
    }

    private fun setDayFilter(day: Int) {
        dismiss()
        viewmodel.setFiltro(day)
    }
}