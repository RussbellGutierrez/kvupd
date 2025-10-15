package com.upd.kvupd.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.upd.kvupd.databinding.DialogSemanaClienteBinding
import com.upd.kvupd.utils.castDate
import com.upd.kvupd.utils.setCreate
import com.upd.kvupd.utils.setResume
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.viewmodel.OldAppViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
class OldDSemana : DialogFragment() {

    private val viewmodel by activityViewModels<OldAppViewModel>()
    private var _bind: DialogSemanaClienteBinding? = null
    private val bind get() = _bind!!
    private val _tag by lazy { OldDSemana::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = DialogSemanaClienteBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onResume() {
        setResume()
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setup()
        bind.fabLunes.setOnClickListener { dayWeek(2) }
        bind.fabMartes.setOnClickListener { dayWeek(3) }
        bind.fabMiercoles.setOnClickListener { dayWeek(4) }
        bind.fabJueves.setOnClickListener { dayWeek(5) }
        bind.fabViernes.setOnClickListener { dayWeek(6) }
        bind.fabSabado.setOnClickListener { dayWeek(7) }
        bind.btnDescargar.setOnClickListener {
            val dia = bind.txtDia.text.toString().trim()
            if (dia != "") {
                dismiss()
                //viewmodel.setFecha(dia)
            } else {
                bind.txtMensaje.setUI("v", true)
            }
        }
    }

    private fun setup() {
        val c = Calendar.getInstance()
        when (c.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> bind.fabLunes.setUI("e", false)
            Calendar.TUESDAY -> bind.fabMartes.setUI("e", false)
            Calendar.WEDNESDAY -> bind.fabMiercoles.setUI("e", false)
            Calendar.THURSDAY -> bind.fabJueves.setUI("e", false)
            Calendar.FRIDAY -> bind.fabViernes.setUI("e", false)
            Calendar.SATURDAY -> bind.fabSabado.setUI("e", false)
        }
    }

    private fun dayWeek(day: Int) {
        val c = Calendar.getInstance()
        c.set(Calendar.DAY_OF_WEEK, day)
        val y = c.get(Calendar.YEAR)
        val m = c.get(Calendar.MONTH)
        val d = c.get(Calendar.DAY_OF_MONTH)
        val date = castDate(d, m, y)
        bind.txtDia.text = date
    }
}