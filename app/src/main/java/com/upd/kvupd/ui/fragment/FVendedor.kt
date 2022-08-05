package com.upd.kvupd.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upd.kvupd.R
import com.upd.kvupd.data.model.RowCliente
import com.upd.kvupd.databinding.FragmentFVendedorBinding
import com.upd.kvupd.ui.adapter.ClienteAdapter
import com.upd.kvupd.ui.dialog.DListaEncuesta
import com.upd.kvupd.ui.dialog.DVendedor
import com.upd.kvupd.utils.*
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Constant.PROCEDE
import com.upd.kvupd.utils.Interface.clienteListener
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class FVendedor : Fragment(), SearchView.OnQueryTextListener, ClienteAdapter.OnClienteListener {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFVendedorBinding? = null
    private val bind get() = _bind!!
    private var row = listOf<RowCliente>()
    private var clienteBaja = false
    private val _tag by lazy { FVendedor::class.java.simpleName }

    @Inject
    lateinit var adapter: ClienteAdapter

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        clienteListener = this
    }

    override fun onResume() {
        super.onResume()
        PROCEDE = "Vendedor"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentFVendedorBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.rcvClientes.layoutManager = LinearLayoutManager(requireContext())
        bind.rcvClientes.adapter = adapter

        bind.searchView.setOnQueryTextListener(this)

        viewmodel.rowClienteObs().distinctUntilChanged().observe(viewLifecycleOwner) {
            Log.d(_tag,"Row cliente observer")
            row = it
            setupList(it)
        }

        viewmodel.vendedor.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                launchDownload(y[0], y[1])
            }
        }

        viewmodel.cliente.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> showDialog(
                        "Correcto",
                        "Clientes descargados correctamente"
                    ) {}
                    is NetworkRetrofit.Error -> showDialog("Error", "Server ${y.message}") {}
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.vendedor_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.voz -> consume { searchVoice() }
        R.id.descargar -> consume { DVendedor().show(parentFragmentManager, "dialog") }
        R.id.encuesta -> consume { DListaEncuesta().show(parentFragmentManager, "dialog") }
        R.id.mapa -> consume { findNavController().navigate(R.id.action_FVendedor_to_FMapa) }
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
            adapter.mDiffer.submitList(search)
        }
        return false
    }

    override fun onClienteClick(cliente: RowCliente) {
        viewLifecycleOwner.lifecycleScope.launch {
            clienteBaja = viewmodel.isClienteBaja(cliente.id.toString())
            navigateToDialog(0, cliente)
        }
    }

    override fun onPressCliente(cliente: RowCliente) {
        viewLifecycleOwner.lifecycleScope.launch {
            clienteBaja = viewmodel.isClienteBaja(cliente.id.toString())
            navigateToDialog(1, cliente)
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val codigo =
                    result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!![0]
                bind.searchView.setQuery(codigo, true)
            } else {
                snack("Error procesando codigo")
            }
        }

    private fun searchVoice() {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).also { intent ->
            intent.resolveActivity(requireActivity().packageManager).also {
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                intent.putExtra(
                    RecognizerIntent.EXTRA_PROMPT,
                    "Mencione el codigo o nombre del cliente"
                )
                resultLauncher.launch(intent)
            }
        }
    }

    private fun launchDownload(codigo: String, fecha: String) {
        val json = JSONObject()
        json.put("empleado", codigo)
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
            adapter.mDiffer.submitList(list)
        }
    }

    private fun navigateToDialog(dialog: Int, cliente: RowCliente) {
        if (clienteBaja) {
            snack("Cliente con baja, revise lista de bajas")
        } else {
            val cli = "${cliente.id} - ${cliente.nombre} - ${cliente.ruta}"
            when (dialog) {
                0 -> viewmodel.checkingEncuesta {
                    if (it) {
                        findNavController().navigate(
                            FVendedorDirections.actionFVendedorToBDObservacion(cli)
                        )
                    } else {
                        snack("Debe elegir una encuesta primero")
                    }
                }
                1 -> findNavController().navigate(
                    FVendedorDirections.actionFVendedorToDBaja(cli)
                )
            }
        }
    }
}