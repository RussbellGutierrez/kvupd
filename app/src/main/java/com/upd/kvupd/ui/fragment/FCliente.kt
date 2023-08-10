package com.upd.kvupd.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.*
import android.widget.SearchView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upd.kvupd.R
import com.upd.kvupd.data.model.HeadCliente
import com.upd.kvupd.data.model.RowCliente
import com.upd.kvupd.databinding.FragmentFClienteBinding
import com.upd.kvupd.ui.adapter.ClienteAdapter
import com.upd.kvupd.ui.dialog.DCliente
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
class FCliente : Fragment(), SearchView.OnQueryTextListener, ClienteAdapter.OnClienteListener,
    MenuProvider {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFClienteBinding? = null
    private val bind get() = _bind!!
    private var row = listOf<RowCliente>()
    private var clienteBaja = false
    private val _tag by lazy { FCliente::class.java.simpleName }

    @Inject
    lateinit var adapter: ClienteAdapter

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        clienteListener = this
    }

    override fun onResume() {
        super.onResume()
        PROCEDE = "Cliente"
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

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        bind.rcvClientes.layoutManager = LinearLayoutManager(requireContext())
        bind.rcvClientes.adapter = adapter

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
                    is NetworkRetrofit.Success -> showDialog(
                        "Correcto",
                        "Clientes descargados correctamente"
                    ) {}
                    is NetworkRetrofit.Error -> showDialog("Error", "Server ${y.message}") {}
                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.cliente_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.voz -> consume { searchVoice() }
        R.id.descargar -> consume { DCliente().show(parentFragmentManager, "dialog") }
        R.id.mapa -> consume {
            findNavController().navigate(
                FClienteDirections.actionFClienteToFMapa(null)
            )
        }
        else -> false
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
        if (search.isEmpty()) {
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
                snack("Error procesando busqueda")
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

    private fun launchDownload(fecha: String) {
        val json = JSONObject()
        json.put("empleado", CONF.codigo)
        json.put("fecha", fecha)
        json.put("empresa", CONF.empresa)
        progress("Descargando clientes")
        viewmodel.fetchClientes(json.toReqBody())
    }

    private fun setupList(list: List<RowCliente>) {
        if (list.isEmpty()) {
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
            val item = HeadCliente(cliente.id, cliente.nombre, cliente.ruta)
            when (dialog) {
                0 -> findNavController().navigate(
                    FClienteDirections.actionFClienteToBDObservacion(item)
                )
                1 -> findNavController().navigate(
                    FClienteDirections.actionFClienteToDClienteAux(item)
                )
            }
        }
    }

}