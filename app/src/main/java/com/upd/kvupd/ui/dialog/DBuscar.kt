package com.upd.kvupd.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.upd.kvupd.data.model.DataCliente
import com.upd.kvupd.data.model.DataSearch
import com.upd.kvupd.databinding.DialogBusquedaBinding
import com.upd.kvupd.ui.adapter.BuscarAdapter
import com.upd.kvupd.utils.Interface.buscarListener
import com.upd.kvupd.utils.setCreate
import com.upd.kvupd.utils.setResume
import com.upd.kvupd.utils.snack
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DBuscar : DialogFragment(), SearchView.OnQueryTextListener, BuscarAdapter.OnBuscarListener {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: DialogBusquedaBinding? = null
    private val bind get() = _bind!!
    private var listrow = mutableListOf<DataCliente>()
    private val args: DBuscarArgs by navArgs()
    private val _tag by lazy { DBuscar::class.java.simpleName }

    @Inject
    lateinit var adapter: BuscarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCreate()
        buscarListener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = DialogBusquedaBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onResume() {
        setResume()
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.rcvBuscar.layoutManager = LinearLayoutManager(requireContext())
        bind.rcvBuscar.adapter = adapter
        bind.searchView.setOnQueryTextListener(this)
        setupList(args.lista)
    }

    override fun onQueryTextSubmit(query: String) = false

    override fun onQueryTextChange(newText: String): Boolean {
        val search = mutableListOf<DataCliente>()
        listrow.forEach { i ->
            if ((i.id.toString().contains(newText.lowercase())) ||
                (i.nombre.lowercase().contains(newText.lowercase()))
            )
                search.add(i)
        }
        if (search.isEmpty()) {
            snack("No encontramos clientes")
        } else {
            adapter.mDiffer.submitList(search)
        }
        return false
    }

    override fun onClienteClick(cliente: DataCliente) {
        viewmodel.setClienteSelect(cliente.id.toString())
        dismiss()
    }

    private fun setupList(list: DataSearch) {
        listrow.clear()
        listrow.addAll(list.lista)
        adapter.mDiffer.submitList(listrow)
    }
}