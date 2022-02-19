package com.upd.kv.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.upd.kv.databinding.DialogClienteDiaBinding
import com.upd.kv.utils.setCreate
import com.upd.kv.utils.setResume
import com.upd.kv.utils.setUI
import com.upd.kv.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class DCliente : DialogFragment() {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: DialogClienteDiaBinding? = null
    private val bind get() = _bind!!
    private val _tag by lazy { DCliente::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = DialogClienteDiaBinding.inflate(inflater, container, false)
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
                viewmodel.setFecha(dia)
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
        val mm = if ((c.get(Calendar.MONTH) + 1).toString().length < 2) {
            "0${c.get(Calendar.MONTH) + 1}"
        } else {
            (c.get(Calendar.MONTH) + 1).toString()
        }

        val dd = if (c.get(Calendar.DAY_OF_MONTH).toString().length < 2) {
            "0${c.get(Calendar.DAY_OF_MONTH)}"
        } else {
            c.get(Calendar.DAY_OF_MONTH).toString()
        }

        val date = "${c.get(Calendar.YEAR)}/$mm/$dd"

        bind.txtDia.text = date
    }
}