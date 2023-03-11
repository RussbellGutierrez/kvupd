package com.upd.kvupd.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upd.kvupd.R
import com.upd.kvupd.data.model.Generico
import com.upd.kvupd.data.model.Soles
import com.upd.kvupd.data.model.Visisuper
import com.upd.kvupd.databinding.FragmentFDetalleBinding
import com.upd.kvupd.ui.adapter.GenericoAdapter
import com.upd.kvupd.ui.adapter.VisisuperAdapter
import com.upd.kvupd.utils.*
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Constant.VISICOOLER_ID
import com.upd.kvupd.utils.Interface.generListener
import com.upd.kvupd.utils.Interface.visisuListener
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class FDetalle : Fragment(), GenericoAdapter.OnGenericoListener,
    VisisuperAdapter.OnVisisuperListener {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFDetalleBinding? = null
    private val bind get() = _bind!!
    private lateinit var data: Bundle
    private val _tag by lazy { FDetalle::class.java.simpleName }

    @Inject
    lateinit var generAdapter: GenericoAdapter

    @Inject
    lateinit var visiAdapter: VisisuperAdapter

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        generListener = this
        visisuListener = this
        data = arguments!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentFDetalleBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        bind.rcvDetalle.layoutManager = LinearLayoutManager(requireContext())

        checkDetalle()

        viewmodel.generico.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> {
                        bind.emptyContainer.root.setUI("v", false)
                        bind.rcvDetalle.setUI("v", true)
                        generAdapter.mDiffer.submitList(y.data!!.jobl)
                        showDialog("Correcto", "Datos descargados") {}
                    }
                    is NetworkRetrofit.Error -> {
                        bind.emptyContainer.root.setUI("v", true)
                        bind.rcvDetalle.setUI("v", false)
                        showDialog("Error", "Server ${y.message}") {}
                    }
                }
            }
        }

        viewmodel.visicooler.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> {
                        snack("Mostrando resultado")
                        val bundle = bundleOf(
                            "informe" to 4,
                            "array" to y.data?.jobl?.toTypedArray(),
                            "item" to null
                        )
                        findNavController().navigate(
                            R.id.action_FDetalle_to_DMiniDetalle,
                            bundle
                        )
                    }
                    is NetworkRetrofit.Error -> snack("Error ${y.message}")
                }
            }
        }
    }

    override fun onItemClick(generico: Generico) {
        if (CONF.tipo == "S") {
            val bundle = bundleOf(
                "informe" to 7,
                "array" to null,
                "item" to generico
            )
            findNavController().navigate(
                R.id.action_FDetalle_to_DMiniDetalle,
                bundle
            )
        }
    }

    override fun onCloseItem(generico: Generico) {
        val list = generAdapter.mDiffer.currentList.toMutableList()
        list.remove(generico)
        if (list.size > 0) {
            generAdapter.mDiffer.submitList(list)
        } else {
            bind.emptyContainer.root.setUI("v", true)
            bind.rcvDetalle.setUI("v", false)
        }
    }

    override fun onItemClick(visisuper: Visisuper) {
        VISICOOLER_ID = visisuper.id

        val p = JSONObject()
        p.put("empleado", visisuper.id)
        p.put("empresa", CONF.empresa)
        snack("Cargando datos")
        viewmodel.fetchVisicooler(p.toReqBody())
    }

    override fun onCloseItem(visisuper: Visisuper) {
        val list = visiAdapter.mDiffer.currentList.toMutableList()
        list.remove(visisuper)
        if (list.size > 0) {
            visiAdapter.mDiffer.submitList(list)
        } else {
            bind.emptyContainer.root.setUI("v", true)
            bind.rcvDetalle.setUI("v", false)
        }
    }

    private fun checkDetalle() {
        val array: List<Visisuper>? =
            data.getParcelableArray("visisuper")?.filterIsInstance<Visisuper>()
        if (array.isNullOrEmpty()) {
            bind.rcvDetalle.adapter = generAdapter
            launchDownload()
        } else {
            bind.rcvDetalle.adapter = visiAdapter
            bind.txtTitulo.text = "Visicooler Vendedor"
            visiAdapter.mDiffer.submitList(array.toList())
        }
    }

    private fun launchDownload() {
        /*var linea = 0
        var titulo = ""*/

        val soles = data.getParcelable<Soles>("soles")!!

        /*args.ume?.let {
            titulo = "UMES"
            linea = it.linea.codigo
        }
        args.soles?.let {
            titulo = "SOLES"
            linea = it.linea.codigo
        }*/
        bind.txtTitulo.text = "SOLES"

        val p = JSONObject()
        p.put("empleado", CONF.codigo)
        p.put("empresa", CONF.empresa)
        p.put("linea", soles.linea.codigo)
        progress("Descargando informacion")

        viewmodel.fetchSolesGenerico(p.toReqBody())
        /*when (CONF.empresa) {
            1 -> detalleUME()//viewmodel.fetchUmesGenerico(p.toReqBody())
            2 -> viewmodel.fetchSolesGenerico(p.toReqBody())
        }*/
    }

    /*private fun detalleUME() {
        var codigo = 0
        val lista = arrayListOf<Generico>()
        args.ume?.let {
            codigo = it.marca.codigo
        }
        UMELISTA.forEach { i ->
            if (i.marca.codigo == codigo) {
                val item =
                    Generico(ValueName(i.linea.codigo, i.linea.descripcion), i.cuota, i.avance)
                lista.add(item)
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            bind.emptyContainer.root.setUI("v", false)
            bind.rcvDetalle.setUI("v", true)
            generAdapter.mDiffer.submitList(lista)
            showDialog("Correcto", "Datos descargados") {}
        }, 3000)
    }*/
}