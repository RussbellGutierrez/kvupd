package com.upd.kvupd.ui.dialog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.upd.kvupd.databinding.DialogBusquedaBinding
import com.upd.kvupd.utils.setCreate
import com.upd.kvupd.utils.setResume
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DBuscar : DialogFragment(), TextWatcher {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: DialogBusquedaBinding? = null
    private val bind get() = _bind!!
    private var list = arrayListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private val _tag by lazy { DBuscar::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCreate()
        if (arguments != null)
            list = arguments?.getStringArrayList("lista")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = DialogBusquedaBinding.inflate(inflater, container,false)
        return bind.root
    }

    override fun onResume() {
        setResume()
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1,list)
        bind.lstCliente.adapter = adapter
        bind.edtCliente.addTextChangedListener(this)
        bind.lstCliente.setOnItemClickListener { _, _, position, _ ->
            val rst = adapter.getItem(position)!!.split("-")[0].trim()
            viewmodel.setClienteSelect(rst)
            dismiss()
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

    override fun afterTextChanged(p0: Editable?) = Unit

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        adapter.filter.filter(p0)
    }
}