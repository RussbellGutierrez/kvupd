package com.upd.kvupd.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upd.kvupd.R
import com.upd.kvupd.data.model.BajaVendedor
import com.upd.kvupd.data.model.RowBaja
import com.upd.kvupd.databinding.FragmentFBajaDatosBinding
import com.upd.kvupd.ui.adapter.BajaSupervisorAdapter
import com.upd.kvupd.ui.adapter.BajaVendedorAdapter
import com.upd.kvupd.ui.dialog.DFiltro
import com.upd.kvupd.utils.*
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Interface.bajaSuperListener
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class FBajaDatos : Fragment(), SearchView.OnQueryTextListener,
    BajaSupervisorAdapter.OnBajaSuperListener {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFBajaDatosBinding? = null
    private val bind get() = _bind!!
    private var row = listOf<RowBaja>()
    private var filter = listOf<RowBaja>()
    private val _tag by lazy { FBajaDatos::class.java.simpleName }

    @Inject
    lateinit var vendedor: BajaVendedorAdapter

    @Inject
    lateinit var supervisor: BajaSupervisorAdapter

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bajaSuperListener = this
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentFBajaDatosBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.rcvBajas.layoutManager = LinearLayoutManager(requireContext())

        if (CONF.tipo == "S") {
            bind.cardSearch.setUI("v", true)
            bind.rcvBajas.adapter = supervisor
        } else {
            bind.rcvBajas.adapter = vendedor
        }

        bind.searchView.setOnQueryTextListener(this)

        viewmodel.filtro.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                changeFilter(y)
            }
        }

        viewmodel.rowBajaObs().distinctUntilChanged().observe(viewLifecycleOwner) { result ->
            row = result
            filter = row
            setupSupervisor(result)
        }

        viewmodel.bajasuper.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> showDialog(
                        "Correcto",
                        "Bajas descargadas correctamente"
                    ) {}
                    is NetworkRetrofit.Error -> showDialog("Error", "Server ${y.message}") {}
                }
            }
        }

        viewmodel.bajavend.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> {
                        showDialog("Correcto", "Bajas descargadas correctamente") {}
                        setupVendedor(y.data!!.jobl)
                    }
                    is NetworkRetrofit.Error -> showDialog("Error", "Server ${y.message}") {}
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.bajadatos_menu, menu)
        if (CONF.tipo == "V")
            menu.findItem(R.id.filtro).setUI("v", false)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.descargar -> consume { launchApi() }
        R.id.filtro -> consume { DFiltro().show(parentFragmentManager, "dialog") }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(p0: String) = false

    override fun onQueryTextChange(p0: String): Boolean {
        val search = mutableListOf<RowBaja>()
        filter.forEach { i ->
            if ((i.id.toString().contains(p0.lowercase())) ||
                (i.nombre.lowercase().contains(p0.lowercase()))
            )
                search.add(i)
        }
        if (search.isNullOrEmpty()) {
            snack("No se encontro baja")
        } else {
            supervisor.mDiffer.submitList(search)
        }
        return false
    }

    override fun onClickItem(baja: RowBaja) {
        val dato = "${baja.id}@${baja.fecha}"
        findNavController().navigate(
            FBajaDatosDirections.actionFBajaDatosToFValidar(dato)
        )
    }

    private fun changeFilter(dia: Int) {
        when(dia) {
            0 -> {
                filter = row
                snack("Filtrar todos los dias")
            }
            1 -> {
                filter = row.filter { it.dia == "LUNES" }
                snack("Filtrar solo lunes")
            }
            2 -> {
                filter = row.filter { it.dia == "MARTES" }
                snack("Filtrar solo martes")
            }
            3 -> {
                filter = row.filter { it.dia == "MIERCOLES" }
                snack("Filtrar solo miercoles")
            }
            4 -> {
                filter = row.filter { it.dia == "JUEVES" }
                snack("Filtrar solo jueves")
            }
            5 -> {
                filter = row.filter { it.dia == "VIERNES" }
                snack("Filtrar solo viernes")
            }
            6 -> {
                filter = row.filter { it.dia == "SABADO" }
                snack("Filtrar solo sabado")
            }
        }
        setupSupervisor(filter)
    }

    private fun setupSupervisor(list: List<RowBaja>) {
        if (list.isNullOrEmpty()) {
            bind.emptyContainer.root.setUI("v", true)
            bind.rcvBajas.setUI("v", false)
        } else {
            bind.emptyContainer.root.setUI("v", false)
            bind.rcvBajas.setUI("v", true)
            supervisor.mDiffer.submitList(list)
        }
    }

    private fun setupVendedor(list: List<BajaVendedor>) {
        if (list.isNullOrEmpty()) {
            bind.emptyContainer.root.setUI("v", true)
            bind.rcvBajas.setUI("v", false)
        } else {
            bind.emptyContainer.root.setUI("v", false)
            bind.rcvBajas.setUI("v", true)
            vendedor.mDiffer.submitList(list)
        }
    }

    private fun launchApi() {
        val p = JSONObject()
        p.put("empleado", CONF.codigo)
        p.put("empresa", CONF.empresa)

        progress("Descargando bajas")
        if (CONF.tipo == "S") {
            viewmodel.fetchBajaSupervisor(p.toReqBody())
        } else {
            viewmodel.fetchBajaVendedor(p.toReqBody())
        }
    }
}