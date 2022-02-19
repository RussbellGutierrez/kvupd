package com.upd.kv.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upd.kv.R
import com.upd.kv.data.model.RowCliente
import com.upd.kv.databinding.FragmentFClienteBinding
import com.upd.kv.ui.adapter.ClienteAdapter
import com.upd.kv.ui.dialog.DCliente
import com.upd.kv.utils.*
import com.upd.kv.utils.Constant.CONF
import com.upd.kv.utils.Interface.clienteListener
import com.upd.kv.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class FCliente @Inject constructor() : Fragment(), SearchView.OnQueryTextListener, ClienteAdapter.OnClienteListener {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFClienteBinding? = null
    private val bind get() = _bind!!
    private var row = listOf<RowCliente>()
    private val _tag by lazy { FCliente::class.java.simpleName }

    @Inject
    lateinit var clienteAdapter: ClienteAdapter

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        clienteListener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentFClienteBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.rcvClientes.layoutManager = LinearLayoutManager(requireContext())
        bind.rcvClientes.adapter = clienteAdapter

        bind.searchView.setOnQueryTextListener(this)

        viewmodel.rowClienteObs().distinctUntilChanged().observe(viewLifecycleOwner) { result ->
            row = result
            setupList(result)
        }

        viewmodel.fecha.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                launchDownload(y)
            }
        }

        viewmodel.cliente.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is Network.Success -> showDialog(
                        "Correcto",
                        "Clientes descargados correctamente"
                    ) {}
                    is Network.Error -> showDialog("Error", "Server ${y.message}") {}
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.cliente_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.descargar -> consume { DCliente().show(parentFragmentManager, "dialog") }
        R.id.mapa -> consume { findNavController().navigate(R.id.action_FCliente_to_FMapa) }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(p0: String) = false

    override fun onQueryTextChange(p0: String): Boolean {
        val search = mutableListOf<RowCliente>()
        row.forEach { i ->
            if ((i.id.toString().contains(p0.lowercase())) ||
                (i.nombre.lowercase().contains(p0.lowercase()))
            )
                search.add(i)
        }
        if (search.isNullOrEmpty()) {
            snack("No encontramos clientes")
        } else {
            clienteAdapter.mDiffer.submitList(search)
        }
        return false
    }

    private fun launchDownload(fecha: String) {
        val json = JSONObject()
        json.put("empleado", CONF.codigo)
        json.put("fecha", fecha)
        json.put("empresa", CONF.empresa)
        progress("Descargando clientes")
        viewmodel.fetchClientes(json.toReqBody())
    }

    private fun setupList(list: List<RowCliente>) {
        if (list.isNullOrEmpty()) {
            bind.emptyContainer.root.setUI("v", true)
            bind.rcvClientes.setUI("v", false)
        } else {
            bind.emptyContainer.root.setUI("v", false)
            bind.rcvClientes.setUI("v", true)
            clienteAdapter.mDiffer.submitList(list)
        }
    }

    override fun onClienteClick(cliente: RowCliente) {
        snack("Click cliente ${cliente.id}")
    }
}