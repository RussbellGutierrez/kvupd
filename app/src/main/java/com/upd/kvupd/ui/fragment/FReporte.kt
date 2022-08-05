package com.upd.kvupd.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upd.kvupd.R
import com.upd.kvupd.data.model.Soles
import com.upd.kvupd.data.model.Umes
import com.upd.kvupd.databinding.FragmentFReporteBinding
import com.upd.kvupd.ui.adapter.SolesAdapter
import com.upd.kvupd.ui.adapter.UmesAdapter
import com.upd.kvupd.utils.*
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Constant.SOCKET_ORIUNDA
import com.upd.kvupd.utils.Constant.SOCKET_TERRANORTE
import com.upd.kvupd.utils.Interface.solesListener
import com.upd.kvupd.utils.Interface.umesListener
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class FReporte : Fragment(), UmesAdapter.OnUmesListener, SolesAdapter.OnSolesListener {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFReporteBinding? = null
    private val bind get() = _bind!!
    private lateinit var socket: Socket
    private val _tag by lazy { FReporte::class.java.simpleName }

    @Inject
    lateinit var umesAdapter: UmesAdapter

    @Inject
    lateinit var solesAdapter: SolesAdapter

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        umesListener = this
        solesListener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentFReporteBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (CONF.empresa) {
            1 -> {
                bind.rcvReporte.layoutManager = LinearLayoutManager(requireContext())
                bind.rcvReporte.adapter = umesAdapter
            }
            2 -> {
                bind.rcvReporte.layoutManager = LinearLayoutManager(requireContext())
                bind.rcvReporte.adapter = solesAdapter
                bind.lnrSoloOriunda.setUI("v", false)
            }
        }

        launchFetchs()

        viewmodel.preventa.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> {
                        var cuota = 0.0
                        var avance = 0.0
                        y.data?.jobl?.forEach { i ->
                            cuota += i.cuota
                            avance += i.avance
                        }
                        val cap1 = "Cuota: ${String.format("%.2f", cuota)}"
                        val cap2 = "Avance: ${String.format("%.2f", avance)}"
                        val cap3 = "${percent(avance, cuota)}%"
                        controlUI(0, true)
                        bind.txtRepo1Cap1.text = cap1
                        bind.txtRepo1Cap2.text = cap2
                        bind.txtRepo1Cap3.text = cap3
                        bind.cardRepo1.setOnClickListener {
                            if (CONF.tipo == "S") {
                                findNavController().navigate(
                                    FReporteDirections.actionFReporteToDMiniDetalle(
                                        y.data?.jobl?.toTypedArray(),
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        0
                                    )
                                )
                            }
                        }
                    }
                    is NetworkRetrofit.Error -> {
                        controlUI(0, false)
                        bind.txtMsg1.text = y.message
                    }
                }
            }
        }

        viewmodel.cobertura.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> {
                        var cartera = 0.0
                        var avance = 0.0
                        y.data?.jobl?.forEach { i ->
                            cartera += i.cartera
                            avance += i.avance
                        }
                        val cap1 = "Cuota: ${String.format("%.0f", cartera)}"
                        val cap2 = "Avance: ${String.format("%.0f", avance)}"
                        val cap3 = "${percent(avance, cartera)}%"
                        controlUI(1, true)
                        bind.txtRepo2Cap1.text = cap1
                        bind.txtRepo2Cap2.text = cap2
                        bind.txtRepo2Cap3.text = cap3
                        bind.cardRepo2.setOnClickListener {
                            if (CONF.tipo == "S") {
                                findNavController().navigate(
                                    FReporteDirections.actionFReporteToDMiniDetalle(
                                        null,
                                        y.data?.jobl?.toTypedArray(),
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        0
                                    )
                                )
                            }
                        }
                    }
                    is NetworkRetrofit.Error -> {
                        controlUI(1, false)
                        bind.txtMsg2.text = y.message
                    }
                }
            }
        }

        viewmodel.cartera.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> {
                        var cartera = 0.0
                        var avance = 0.0
                        y.data?.jobl?.forEach { i ->
                            cartera += i.cartera
                            avance += i.avance
                        }
                        val cap1 = "Cuota: ${String.format("%.0f", cartera)}"
                        val cap2 = "Avance: ${String.format("%.0f", avance)}"
                        val cap3 = "${percent(avance, cartera)}%"
                        controlUI(2, true)
                        bind.txtRepo3Cap1.text = cap1
                        bind.txtRepo3Cap2.text = cap2
                        bind.txtRepo3Cap3.text = cap3
                        bind.cardRepo3.setOnClickListener {
                            findNavController().navigate(
                                FReporteDirections.actionFReporteToDMiniDetalle(
                                    null,
                                    null,
                                    y.data?.jobl?.toTypedArray(),
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    0
                                )
                            )
                        }
                    }
                    is NetworkRetrofit.Error -> {
                        controlUI(2, false)
                        bind.txtMsg3.text = y.message
                    }
                }
            }
        }

        viewmodel.pedidos.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> {
                        var inicio = "S/N"
                        var ultimo = "S/N"
                        var pedido = 0
                        y.data?.jobl?.forEach { i ->
                            inicio = i.inicio
                            ultimo = i.ultimo
                            pedido = i.pedido
                        }
                        val cap1 = "Inicio- $inicio"
                        val cap2 = "Ultimo- $ultimo"
                        val cap3 = "Pedidos: $pedido"
                        controlUI(3, true)
                        bind.txtRepo4Cap1.text = cap1
                        bind.txtRepo4Cap2.text = cap2
                        bind.txtRepo4Cap3.text = cap3
                        bind.cardRepo4.setOnClickListener {
                            if (CONF.tipo == "S") {
                                findNavController().navigate(
                                    FReporteDirections.actionFReporteToDMiniDetalle(
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        null,
                                        1
                                    )
                                )
                            }
                        }
                    }
                    is NetworkRetrofit.Error -> {
                        controlUI(3, false)
                        bind.txtMsg4.text = y.message
                    }
                }
            }
        }

        viewmodel.visicooler.observe(viewLifecycleOwner) {
            Log.d(_tag, "launching visicooler")
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> {
                        var venta = 0
                        y.data?.jobl?.forEach { i ->
                            if (i.avance > 0.0)
                                venta++
                        }
                        val cap1 = "Total: ${y.data?.jobl?.size}"
                        val cap2 = "Venta: $venta"
                        val cap3 = "${percent(venta.toDouble(), y.data!!.jobl.size.toDouble())}%"
                        controlUI(4, true)
                        bind.txtRepo5Cap1.text = cap1
                        bind.txtRepo5Cap2.text = cap2
                        bind.txtRepo5Cap3.text = cap3
                        bind.cardRepo5.setOnClickListener {
                            findNavController().navigate(
                                FReporteDirections.actionFReporteToDMiniDetalle(
                                    null,
                                    null,
                                    null,
                                    null,
                                    y.data.jobl.toTypedArray(),
                                    null,
                                    null,
                                    null,
                                    0
                                )
                            )
                        }
                    }
                    is NetworkRetrofit.Error -> {
                        controlUI(4, false)
                        Log.d(_tag, "Er ${y.message}")
                        bind.txtMsg5.text = y.message
                    }
                }
            }
        }

        viewmodel.visisuper.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> {
                        var venta = 0.0
                        var total = 0.0
                        y.data?.jobl?.forEach { i ->
                            venta += i.avance
                            total += i.cliente
                        }
                        val cap1 = "Total: $total"
                        val cap2 = "Venta: $venta"
                        val cap3 = "${percent(venta, total)}%"
                        controlUI(5, true)
                        bind.txtRepo5Cap1.text = cap1
                        bind.txtRepo5Cap2.text = cap2
                        bind.txtRepo5Cap3.text = cap3
                        bind.cardRepo5.setOnClickListener {
                            findNavController().navigate(
                                FReporteDirections.actionFReporteToFDetalle(
                                    null,
                                    null,
                                    y.data?.jobl?.toTypedArray()
                                )
                            )
                        }
                    }
                    is NetworkRetrofit.Error -> {
                        controlUI(5, false)
                        bind.txtMsg5.text = y.message
                    }
                }
            }
        }

        viewmodel.cambiocli.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> {
                        var cambios = 0
                        var soles = 0.0
                        y.data?.jobl?.forEach { i ->
                            cambios += i.cambios
                            soles += i.monto
                        }
                        val cap1 = "Clientes: ${y.data!!.jobl.size}"
                        val cap2 = "Cambios: $cambios"
                        val cap3 = "Soles: ${String.format("%.2f", soles)}"
                        controlUI(6, true)
                        bind.txtRepo6Cap1.text = cap1
                        bind.txtRepo6Cap2.text = cap2
                        bind.txtRepo6Cap3.text = cap3
                        bind.cardRepo6.setOnClickListener {
                            findNavController().navigate(
                                FReporteDirections.actionFReporteToDMiniDetalle(
                                    null,
                                    null,
                                    null,
                                    y.data.jobl.toTypedArray(),
                                    null,
                                    null,
                                    null,
                                    null,
                                    0
                                )
                            )
                        }
                    }
                    is NetworkRetrofit.Error -> {
                        controlUI(6, false)
                        bind.txtMsg6.text = y.message
                    }
                }
            }
        }

        viewmodel.cambioemp.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> {
                        var cambios = 0
                        var soles = 0.0
                        y.data?.jobl?.forEach { i ->
                            cambios += i.cambios
                            soles += i.monto
                        }
                        val cap1 = "Clientes: ${y.data!!.jobl.size}"
                        val cap2 = "Cambios: $cambios"
                        val cap3 = "Soles: ${String.format("%.2f", soles)}"
                        controlUI(7, true)
                        bind.txtRepo6Cap1.text = cap1
                        bind.txtRepo6Cap2.text = cap2
                        bind.txtRepo6Cap3.text = cap3
                        bind.cardRepo6.setOnClickListener {
                            findNavController().navigate(
                                FReporteDirections.actionFReporteToDMiniDetalle(
                                    null,
                                    null,
                                    null,
                                    y.data.jobl.toTypedArray(),
                                    null,
                                    null,
                                    null,
                                    null,
                                    0
                                )
                            )
                        }
                    }
                    is NetworkRetrofit.Error -> {
                        controlUI(7, false)
                        bind.txtMsg6.text = y.message
                    }
                }
            }
        }

        viewmodel.umes.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> {
                        bind.txtMensaje.setUI("v", false)
                        bind.rcvReporte.setUI("v", true)
                        umesAdapter.mDiffer.submitList(y.data!!.jobl)
                    }
                    is NetworkRetrofit.Error -> {
                        bind.rcvReporte.setUI("v", false)
                        bind.txtMensaje.setUI("v", true)
                        bind.txtMensaje.text = y.message
                    }
                }
            }
        }

        viewmodel.soles.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> {
                        bind.txtMensaje.setUI("v", false)
                        bind.rcvReporte.setUI("v", true)
                        solesAdapter.mDiffer.submitList(y.data!!.jobl)
                    }
                    is NetworkRetrofit.Error -> {
                        bind.rcvReporte.setUI("v", false)
                        bind.txtMensaje.setUI("v", true)
                        bind.txtMensaje.text = y.message
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.reporte_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.actualizar -> consume { executeUpdater() }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onItemClick(soles: Soles) {
        findNavController().navigate(
            FReporteDirections.actionFReporteToFDetalle(null, soles, null)
        )
    }

    override fun onItemPress(soles: Soles) {
        if (CONF.tipo == "S") {
            findNavController().navigate(
                FReporteDirections.actionFReporteToDMiniDetalle(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    soles,
                    0
                )
            )
        }
    }

    override fun onCloseItem(soles: Soles) {
        val list = solesAdapter.mDiffer.currentList.toMutableList()
        list.remove(soles)
        solesAdapter.mDiffer.submitList(list)
    }

    override fun onItemClick(umes: Umes) {
        findNavController().navigate(
            FReporteDirections.actionFReporteToFDetalle(umes, null, null)
        )
    }

    override fun onItemPress(umes: Umes) {
        if (CONF.tipo == "S") {
            findNavController().navigate(
                FReporteDirections.actionFReporteToDMiniDetalle(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    umes,
                    null,
                    0
                )
            )
        }
    }

    override fun onCloseItem(umes: Umes) {
        val list = umesAdapter.mDiffer.currentList.toMutableList()
        list.remove(umes)
        umesAdapter.mDiffer.submitList(list)
    }

    private fun launchFetchs() {
        val p = JSONObject()
        p.put("empleado", CONF.codigo)
        p.put("empresa", CONF.empresa)

        viewmodel.fetchPreventa(p.toReqBody())
        viewmodel.fetchCobertura(p.toReqBody())
        viewmodel.fetchCartera(p.toReqBody())
        viewmodel.fetchPedidos(p.toReqBody())

        when (CONF.tipo) {
            "V" -> {
                viewmodel.fetchCambiosCliente(p.toReqBody())
                if (CONF.empresa == 1)
                    viewmodel.fetchVisicooler(p.toReqBody())
            }
            "S" -> {
                viewmodel.fetchCambiosEmpleado(p.toReqBody())
                if (CONF.empresa == 1)
                    viewmodel.fetchVisisuper(p.toReqBody())
            }
        }

        if (CONF.empresa == 1) {
            viewmodel.fetchUmes(p.toReqBody())
        } else {
            viewmodel.fetchSoles(p.toReqBody())
        }
    }

    private fun controlUI(ui: Int, status: Boolean) {
        when (ui) {
            0 -> {
                bind.progress1.setUI("v", false)
                if (status)
                    bind.lnrRepo1.setUI("v", true)
                else
                    bind.txtMsg1.setUI("v", true)
            }
            1 -> {
                bind.progress2.setUI("v", false)
                if (status)
                    bind.lnrRepo2.setUI("v", true)
                else
                    bind.txtMsg2.setUI("v", true)
            }
            2 -> {
                bind.progress3.setUI("v", false)
                if (status)
                    bind.lnrRepo3.setUI("v", true)
                else
                    bind.txtMsg3.setUI("v", true)
            }
            3 -> {
                bind.progress4.setUI("v", false)
                if (status)
                    bind.lnrRepo4.setUI("v", true)
                else
                    bind.txtMsg4.setUI("v", true)
            }
            4 -> {
                bind.progress5.setUI("v", false)
                if (status)
                    bind.lnrRepo5.setUI("v", true)
                else
                    bind.txtMsg5.setUI("v", true)
            }
            5 -> {
                bind.progress5.setUI("v", false)
                if (status)
                    bind.lnrRepo5.setUI("v", true)
                else
                    bind.txtMsg5.setUI("v", true)
            }
            6 -> {
                bind.progress6.setUI("v", false)
                if (status)
                    bind.lnrRepo6.setUI("v", true)
                else
                    bind.txtMsg6.setUI("v", true)
            }
            7 -> {
                bind.progress6.setUI("v", false)
                if (status)
                    bind.lnrRepo6.setUI("v", true)
                else
                    bind.txtMsg6.setUI("v", true)
            }
        }
    }

    private fun executeUpdater() {
        val fecha = viewmodel.fecha(3)
        val url = when (CONF.empresa) {
            1 -> SOCKET_ORIUNDA
            else -> SOCKET_TERRANORTE
        }
        val opcion = IO.Options()
        opcion.forceNew = false
        opcion.timeout = 30000
        progress("Comenzo actualizacion $fecha, culminara aprox. 2 min a 3 min")

        if (::socket.isInitialized && socket.connected()) {
            socket.disconnect()
            socket.off()
        } else {
            socket = IO.socket(url, opcion)
        }

        socket.emit("request")
        socket.on("response", respuesta)
        socket.connect()
    }

    private val respuesta = Emitter.Listener {
        socket.disconnect()
        socket.off()
        requireActivity().runOnUiThread {
            showDialog("correcto", "Reporte actualizado") {}
            launchFetchs()
        }
    }

}