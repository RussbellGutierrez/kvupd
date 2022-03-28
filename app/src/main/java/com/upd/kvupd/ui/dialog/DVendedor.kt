package com.upd.kvupd.ui.dialog

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.upd.kvupd.databinding.ChipsBinding
import com.upd.kvupd.databinding.DialogClienteVendedorBinding
import com.upd.kvupd.utils.castDate
import com.upd.kvupd.utils.setCreate
import com.upd.kvupd.utils.setResume
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class DVendedor : DialogFragment() {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: DialogClienteVendedorBinding? = null
    private val bind get() = _bind!!
    private val _tag by lazy { DVendedor::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCreate()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = DialogClienteVendedorBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onResume() {
        setResume()
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setup()

        bind.imgCalendario.setOnClickListener { showCalendar() }
        bind.btnDescargar.setOnClickListener {
            processData()
        }
    }

    private fun setup() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewmodel.gettingVendedores().forEach { i ->
                val minbind = ChipsBinding.inflate(layoutInflater, view as ViewGroup, false)
                minbind.txtChip.text = i.codigo.toString()
                minbind.cardChip.setOnClickListener {
                    bind.txtVendedor.text = i.descripcion
                }
                bind.flxVendedor.addView(minbind.root)
            }
        }
    }

    private fun showCalendar() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val calendar = DatePickerDialog(requireContext(), { _, y, m, d ->
            val dt = castDate(d, m, y)
            bind.txtDia.text = dt
        }, year, month, day)
        calendar.show()
    }

    private fun processData() {
        val codigo = bind.txtVendedor.text.toString().split("-")[0].trim()
        val fecha = bind.txtDia.text.toString()
        if (codigo == "" || fecha == "") {
            bind.txtMensaje.setUI("v", true)
            bind.txtMensaje.text = "Elija un vendedor y fecha por favor"
        } else {
            dismiss()
            viewmodel.setVendedorSelect(listOf(codigo, fecha))
        }
    }
}