package com.upd.kvupd.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import com.upd.kvupd.data.model.Soles
import com.upd.kvupd.databinding.FragmentFReporteBinding
import com.upd.kvupd.ui.adapter.SolesAdapter
import com.upd.kvupd.utils.OldConstant.CONF
import com.upd.kvupd.utils.OldInterface.solesListener
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.utils.showDialog
import com.upd.kvupd.viewmodel.OldAppViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class OldFReporte : Fragment(), SolesAdapter.OnSolesListener,
    MenuProvider {

    private val viewmodel by activityViewModels<OldAppViewModel>()
    private var _bind: FragmentFReporteBinding? = null
    private val bind get() = _bind!!
    private lateinit var socket: Socket
    private val _tag by lazy { OldFReporte::class.java.simpleName }

    @Inject
    lateinit var solesAdapter: SolesAdapter

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        bind.rcvReporte.layoutManager = LinearLayoutManager(requireContext())
        bind.rcvReporte.adapter = solesAdapter

        if (CONF.empresa == 2) {
            bind.lnrSoloOriunda.setUI("v", false)
        }

        launchFetchs()

        /*viewmodel.preventa.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is OldNetworkRetrofit.Success -> {
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
                                val bundle = bundleOf(
                                    "informe" to 0,
                                    "array" to y.data?.jobl?.toTypedArray(),
                                    "item" to null
                                )
                                findNavController().navigate(
                                    R.id.action_FReporte_to_DMiniDetalle,
                                    bundle
                                )
                            }
                        }
                    }

                    is OldNetworkRetrofit.Error -> {
                        controlUI(0, false)
                        bind.txtMsg1.text = y.message
                    }
                }
            }
        }

        viewmodel.cobertura.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is OldNetworkRetrofit.Success -> {
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
                            val bundle = bundleOf(
                                "informe" to 1,
                                "array" to y.data?.jobl?.toTypedArray(),
                                "item" to null
                            )
                            findNavController().navigate(
                                R.id.action_FReporte_to_DMiniDetalle,
                                bundle
                            )
                        }
                    }

                    is OldNetworkRetrofit.Error -> {
                        controlUI(1, false)
                        bind.txtMsg2.text = y.message
                    }
                }
            }
        }

        viewmodel.cartera.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is OldNetworkRetrofit.Success -> {
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
                            val bundle = bundleOf(
                                "informe" to 2,
                                "array" to y.data?.jobl?.toTypedArray(),
                                "item" to null
                            )
                            findNavController().navigate(
                                R.id.action_FReporte_to_DMiniDetalle,
                                bundle
                            )
                        }
                    }

                    is OldNetworkRetrofit.Error -> {
                        controlUI(2, false)
                        bind.txtMsg3.text = y.message
                    }
                }
            }
        }

        viewmodel.pedidos.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is OldNetworkRetrofit.Success -> {
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
                                val bundle = bundleOf(
                                    "informe" to 3,
                                    "array" to null,
                                    "item" to null
                                )
                                findNavController().navigate(
                                    R.id.action_FReporte_to_DMiniDetalle,
                                    bundle
                                )
                            }
                        }
                    }

                    is OldNetworkRetrofit.Error -> {
                        controlUI(3, false)
                        bind.txtMsg4.text = y.message
                    }
                }
            }
        }*/

        if (CONF.tipo == "S") {
            /*viewmodel.visisuper.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let { y ->
                    when (y) {
                        is OldNetworkRetrofit.Success -> {
                            var venta = 0.0
                            var total = 0.0
                            y.data?.jobl?.forEach { i ->
                                venta += i.avance
                                total += i.cliente
                            }
                            val cap1 = "Total: $total"
                            val cap2 = "Venta: $venta"
                            val cap3 = "${percent(venta, total)}%"
                            controlUI(4, true)
                            bind.txtRepo5Cap1.text = cap1
                            bind.txtRepo5Cap2.text = cap2
                            bind.txtRepo5Cap3.text = cap3
                            bind.cardRepo5.setOnClickListener {
                                val bundle = bundleOf(
                                    "visisuper" to y.data?.jobl?.toTypedArray(),
                                    "soles" to null
                                )
                                findNavController().navigate(
                                    R.id.action_FReporte_to_FDetalle,
                                    bundle
                                )
                            }
                        }

                        is OldNetworkRetrofit.Error -> {
                            controlUI(4, false)
                            bind.txtMsg5.text = y.message
                        }
                    }
                }
            }*/
        } else {
            /*viewmodel.visicooler.observe(viewLifecycleOwner) {
                Log.d(_tag, "launching visicooler")
                it.getContentIfNotHandled()?.let { y ->
                    when (y) {
                        is OldNetworkRetrofit.Success -> {
                            var venta = 0
                            var size = 0
                            y.data?.jobl?.forEach { i ->
                                size++
                                if (i.avance > 0.0)
                                    venta++
                            }
                            val cap1 = "Total: ${y.data?.jobl?.size}"
                            val cap2 = "Venta: $venta"
                            val cap3 = "${percent(venta.toDouble(), size.toDouble())}%"
                            controlUI(4, true)
                            bind.txtRepo5Cap1.text = cap1
                            bind.txtRepo5Cap2.text = cap2
                            bind.txtRepo5Cap3.text = cap3
                            bind.cardRepo5.setOnClickListener {
                                val bundle = bundleOf(
                                    "informe" to 4,
                                    "array" to y.data?.jobl?.toTypedArray(),
                                    "item" to null
                                )
                                findNavController().navigate(
                                    R.id.action_FReporte_to_DMiniDetalle,
                                    bundle
                                )
                            }
                        }

                        is OldNetworkRetrofit.Error -> {
                            controlUI(4, false)
                            Log.e(_tag, "Er ${y.message}")
                            bind.txtMsg5.text = y.message
                        }
                    }
                }
            }*/
        }

        /*viewmodel.cambios.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is OldNetworkRetrofit.Success -> {
                        var size = 0
                        var cambios = 0
                        var soles = 0.0
                        y.data?.jobl?.forEach { i ->
                            size++
                            cambios += i.cambios
                            soles += i.monto
                        }
                        val cap1 = "Clientes: $size"
                        val cap2 = "Cambios: $cambios"
                        val cap3 = "Soles: ${String.format("%.2f", soles)}"
                        controlUI(5, true)
                        bind.txtRepo6Cap1.text = cap1
                        bind.txtRepo6Cap2.text = cap2
                        bind.txtRepo6Cap3.text = cap3
                        bind.cardRepo6.setOnClickListener {
                            val bundle = bundleOf(
                                "informe" to 5,
                                "array" to y.data?.jobl?.toTypedArray(),
                                "item" to null
                            )
                            findNavController().navigate(
                                R.id.action_FReporte_to_DMiniDetalle,
                                bundle
                            )
                        }
                    }

                    is OldNetworkRetrofit.Error -> {
                        controlUI(5, false)
                        bind.txtMsg6.text = y.message
                    }
                }
            }
        }*/

        /*viewmodel.umes.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is OldNetworkRetrofit.Success -> {
                        /*bind.txtMensaje.setUI("v", false)
                        bind.rcvReporte.setUI("v", true)
                        proccessUME(y.data!!.jobl)*/
                    }

                    is OldNetworkRetrofit.Error -> {
                        /*bind.rcvReporte.setUI("v", false)
                        bind.txtMensaje.setUI("v", true)
                        bind.txtMensaje.text = y.message*/
                    }
                }
            }
        }*/

        /*viewmodel.soles.observe(viewLifecycleOwner) { j ->
            j.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is OldNetworkRetrofit.Success -> {
                        bind.txtMensaje.setUI("v", false)
                        bind.rcvReporte.setUI("v", true)
                        val sorted = y.data!!.jobl.sortedBy { it.linea.codigo }
                        solesAdapter.mDiffer.submitList(sorted)
                    }

                    is OldNetworkRetrofit.Error -> {
                        bind.rcvReporte.setUI("v", false)
                        bind.txtMensaje.setUI("v", true)
                        bind.txtMensaje.text = y.message
                    }
                }
            }
        }*/
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.reporte_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.actualizar -> consume { executeUpdater() }
        else -> false
    }

    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.reporte_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.actualizar -> consume { executeUpdater() }
        else -> super.onOptionsItemSelected(item)
    }*/

    override fun onItemClick(soles: Soles) {
        val bundle = bundleOf(
            "visisuper" to null,
            "soles" to soles
        )

        findNavController().navigate(
            R.id.action_FReporte_to_FDetalle,
            bundle
        )
        /*findNavController().navigate(
            FReporteDirections.actionFReporteToFDetalle(null, soles, null)
        )*/
    }

    override fun onItemPress(soles: Soles) {
        if (CONF.tipo == "S") {
            val bundle = bundleOf(
                "informe" to 6,
                "array" to null,
                "item" to soles
            )
            findNavController().navigate(
                R.id.action_FReporte_to_DMiniDetalle,
                bundle
            )
            /*findNavController().navigate(
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
            )*/
        }
    }

    override fun onCloseItem(soles: Soles) {
        val list = solesAdapter.mDiffer.currentList.toMutableList()
        list.remove(soles)
        solesAdapter.mDiffer.submitList(list)
    }

    /*override fun onItemClick(umes: Umes) {
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
    }*/

    private fun launchFetchs() {
        val p = JSONObject()
        p.put("empleado", CONF.codigo)
        p.put("empresa", CONF.empresa)

        /*viewmodel.fetchPreventa(p.toReqBody())
        viewmodel.fetchCobertura(p.toReqBody())
        viewmodel.fetchCartera(p.toReqBody())
        viewmodel.fetchPedidos(p.toReqBody())
        viewmodel.fetchCambios(p.toReqBody())

        if (CONF.empresa == 1) {
            if (CONF.tipo == "S") {
                viewmodel.fetchVisisuper(p.toReqBody())
            } else {
                viewmodel.fetchVisicooler(p.toReqBody())
            }
        }*/

        //viewmodel.fetchSoles(p.toReqBody())
        /*if (CONF.empresa == 1) {
            viewmodel.fetchUmes(p.toReqBody())
        } else {
            viewmodel.fetchSoles(p.toReqBody())
        }*/
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
                bind.progress6.setUI("v", false)
                if (status)
                    bind.lnrRepo6.setUI("v", true)
                else
                    bind.txtMsg6.setUI("v", true)
            }
        }
    }

    /*private fun proccessUME(list: List<Umes>) {

        val nlist = arrayListOf<Umes>()
        val size = list.size

        list.forEach { i ->
            if (UMEMARCA == "") {
                UME = i
                UMEMARCA = i.marca.descripcion
                UMECUOTA = i.cuota
                UMEAVANCE = i.avance
            } else {
                if (UMEMARCA != i.marca.descripcion) {
                    UME.cuota = (UMECUOTA * 100.0).roundToInt() / 100.0
                    UME.avance = (UMEAVANCE * 100.0).roundToInt() / 100.0
                    nlist.add(UME)
                    UME = i
                    UMEMARCA = i.marca.descripcion
                    UMECUOTA = i.cuota
                    UMEAVANCE = i.avance
                } else {
                    UME = i
                    UMECUOTA += i.cuota
                    UMEAVANCE += i.avance
                }
            }
            if (UMECOUNT == size) {
                UME.cuota = (UMECUOTA * 100.0).roundToInt() / 100.0
                UME.avance = (UMEAVANCE * 100.0).roundToInt() / 100.0
                nlist.add(UME)
                UMECOUNT = 1
                UMEMARCA = ""
                UMECUOTA = 0.0
                UMEAVANCE = 0.0
            } else {
                UMECOUNT++
            }
        }

        UMELISTA = list
        umesAdapter.mDiffer.submitList(nlist)
    }*/

    private fun executeUpdater() {
        /*val fecha = viewmodel.fecha(3)

        val http = when (OPTURL) {
            "ipp" -> if (isCONFinitialized()) {
                "http://${CONF.ipp}"
            } else {
                "http://191.98.177.57"
            }

            "ips" -> if (isCONFinitialized()) {
                "http://${CONF.ips}"
            } else {
                "http://191.98.177.57"
            }

            else -> "http://$IPA"
        }

        val url = when (CONF.empresa) {
            1 -> "$http:8080/oriunda/update"
            else -> "$http:80/terranorte/update"
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
        socket.connect()*/
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