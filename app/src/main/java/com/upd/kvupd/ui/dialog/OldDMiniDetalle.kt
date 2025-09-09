package com.upd.kvupd.ui.dialog

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.upd.kvupd.R
import com.upd.kvupd.data.model.Cambio
import com.upd.kvupd.data.model.CoberturaCartera
import com.upd.kvupd.data.model.Coberturados
import com.upd.kvupd.data.model.DetalleCobertura
import com.upd.kvupd.data.model.Generico
import com.upd.kvupd.data.model.OrderVenta
import com.upd.kvupd.data.model.PedidoGeneral
import com.upd.kvupd.data.model.Soles
import com.upd.kvupd.data.model.Venta
import com.upd.kvupd.data.model.Volumen
import com.upd.kvupd.databinding.DialogMiniDetalleBinding
import com.upd.kvupd.databinding.RowCoberturaDetalleBinding
import com.upd.kvupd.databinding.RowMiniCobdetBinding
import com.upd.kvupd.databinding.RowMiniDetalleBinding
import com.upd.kvupd.databinding.RowReporteBinding
import com.upd.kvupd.databinding.RowTarjetaClienteBinding
import com.upd.kvupd.utils.OldConstant.CONF
import com.upd.kvupd.utils.OldNetworkRetrofit
import com.upd.kvupd.utils.percent
import com.upd.kvupd.utils.setCreate
import com.upd.kvupd.utils.setResume
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.utils.toReqBody
import com.upd.kvupd.viewmodel.OldAppViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class OldDMiniDetalle : DialogFragment() {

    private val viewmodel by activityViewModels<OldAppViewModel>()
    private var _bind: DialogMiniDetalleBinding? = null
    private val bind get() = _bind!!
    private lateinit var data: Bundle
    private val _tag by lazy { OldDMiniDetalle::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCreate()
        data = requireArguments()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = DialogMiniDetalleBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onResume() {
        setResume(false)
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(_tag, "Args $data")

        checkMiniDetalle()

        viewmodel.detalle.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is OldNetworkRetrofit.Success -> {
                        val list = y.data?.jobl
                        bind.emptyContainer.root.setUI("v", list.isNullOrEmpty())
                        if (!list.isNullOrEmpty()) {
                            setUmeSoles(list)
                        }
                    }

                    is OldNetworkRetrofit.Error -> {
                        bind.emptyContainer.textView.text = y.message
                    }
                }
            }
        }

        viewmodel.cobpendiente.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is OldNetworkRetrofit.Success -> {
                        val list = y.data?.jobl
                        bind.emptyContainer.root.setUI("v", list.isNullOrEmpty())
                        if (!list.isNullOrEmpty()) {
                            setCoberturaPendiente(list)
                        }
                    }

                    is OldNetworkRetrofit.Error -> {
                        bind.emptyContainer.textView.text = y.message
                    }
                }
            }
        }

        viewmodel.pedigen.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is OldNetworkRetrofit.Success -> {
                        val list = y.data?.jobl
                        bind.emptyContainer.root.setUI("v", list.isNullOrEmpty())
                        if (!list.isNullOrEmpty()) {
                            setPedidosRealizados(list)
                        }
                    }

                    is OldNetworkRetrofit.Error -> {
                        bind.emptyContainer.textView.text = y.message
                    }
                }
            }
        }

        viewmodel.detcob.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is OldNetworkRetrofit.Success -> {
                        val list = y.data?.jobl
                        bind.emptyContainer.root.setUI("v", list.isNullOrEmpty())
                        if (!list.isNullOrEmpty()) {
                            coberturaDetalle(list)
                        }
                    }

                    is OldNetworkRetrofit.Error -> {
                        bind.emptyContainer.textView.text = y.message
                    }
                }
            }
        }
    }

    private fun checkMiniDetalle() {
        when (data.getInt("informe")) {
            0 -> {
                val array: List<Volumen>? =
                    data.getParcelableArray("array")?.filterIsInstance<Volumen>()
                bind.lnrMini.removeAllViews()
                bind.emptyContainer.root.setUI("v", array.isNullOrEmpty())
                val titulo = "Informe Preventa"
                bind.txtTitulo.text = titulo
                if (!array.isNullOrEmpty()) {
                    preventa(array.toList())
                }
            }

            1 -> {
                if (CONF.tipo == "S") {
                    val array: List<CoberturaCartera>? =
                        data.getParcelableArray("array")?.filterIsInstance<CoberturaCartera>()
                    bind.lnrMini.removeAllViews()
                    bind.emptyContainer.root.setUI("v", array.isNullOrEmpty())
                    val titulo = "Informe Cobertura"
                    bind.txtTitulo.text = titulo
                    if (!array.isNullOrEmpty()) {
                        cobertura(array.toList())
                    }
                } else {
                    bind.txtTitulo.text = "Detalle cobertura"
                    getCoberturaDetalle()
                }
            }

            2 -> {
                if (CONF.tipo == "S") {
                    val array: List<CoberturaCartera>? =
                        data.getParcelableArray("array")?.filterIsInstance<CoberturaCartera>()
                    bind.lnrMini.removeAllViews()
                    bind.emptyContainer.root.setUI("v", array.isNullOrEmpty())
                    val titulo = "Avance Clientes No Coberturados"
                    bind.txtTitulo.text = titulo
                    if (!array.isNullOrEmpty()) {
                        cobertura(array.toList())
                    }
                } else {
                    val titulo = "Clientes No Coberturados"
                    bind.txtTitulo.text = titulo
                    val p = JSONObject()
                    p.put("empleado", CONF.codigo)
                    p.put("empresa", CONF.empresa)
                    viewmodel.fetchCoberturaPendiente(p.toReqBody())
                }
            }

            3 -> {
                val titulo = "Informe Pedidos"
                bind.txtTitulo.text = titulo
                val p = JSONObject()
                p.put("empleado", CONF.codigo)
                p.put("empresa", CONF.empresa)
                viewmodel.fetchPediGen(p.toReqBody())
            }

            4 -> {
                /*val array: List<Visicooler>? =
                    data.getParcelableArray("array")?.filterIsInstance<Visicooler>()
                bind.lnrMini.removeAllViews()
                bind.emptyContainer.root.setUI("v", array.isNullOrEmpty())
                val titulo = if (VISICOOLER_ID > 0) {
                    "Visicooler Vendedor $VISICOOLER_ID"
                } else {
                    "Informe Visicooler"
                }
                VISICOOLER_ID = 0
                bind.txtTitulo.text = titulo
                if (!array.isNullOrEmpty()) {
                    visicooler(array.toList())
                }*/
            }

            5 -> {
                val array: List<Cambio>? =
                    data.getParcelableArray("array")?.filterIsInstance<Cambio>()
                Log.d(_tag, "List $array")
                bind.lnrMini.removeAllViews()
                bind.emptyContainer.root.setUI("v", array.isNullOrEmpty())
                val titulo = "Informe Cambios"
                bind.txtTitulo.text = titulo
                if (!array.isNullOrEmpty()) {
                    cambios(array.toList())
                }
            }

            6 -> {
                val soles = data.getParcelable<Soles>("item")!!
                bind.txtTitulo.text = soles.linea.descripcion
                launchDownload(soles.linea.codigo, 1)
            }

            7 -> {
                val generico = data.getParcelable<Generico>("item")!!
                bind.txtTitulo.text = generico.datos.descripcion
                launchDownload(generico.datos.codigo, 2)
            }
        }
    }

    private fun preventa(list: List<Volumen>) {
        list.forEach { i ->
            val minbind =
                RowMiniDetalleBinding.inflate(layoutInflater, view as ViewGroup, false)
            val cuota = "Cuota: ${i.cuota}"
            val avance = "Avance: ${i.avance}"
            val porcentaje = "${percent(i.avance, i.cuota)}%"

            minbind.lnrTres.setUI("v", true)
            minbind.txtDatos.text = i.datos.descripcion
            minbind.txtCuota.text = cuota
            minbind.txtAvance.text = avance
            minbind.txtPorcentaje.text = porcentaje
            minbind.imgCerrar.setOnClickListener { minbind.cardReporte.setUI("v", false) }

            bind.lnrMini.addView(minbind.root)
        }
    }

    private fun cobertura(list: List<CoberturaCartera>) {
        list.forEach { i ->
            val minbind = RowMiniDetalleBinding.inflate(layoutInflater, view as ViewGroup, false)
            val cuota = "Cuota: ${i.cartera}"
            val avance = "Avance: ${i.avance}"
            val porcentaje = "${percent(i.avance.toDouble(), i.cartera.toDouble())}%"

            minbind.lnrTres.setUI("v", true)
            minbind.txtDatos.text = i.datos.descripcion
            minbind.txtCuota.text = cuota
            minbind.txtAvance.text = avance
            minbind.txtPorcentaje.text = porcentaje
            minbind.imgCerrar.setOnClickListener { minbind.cardReporte.setUI("v", false) }

            if (CONF.tipo == "V") {
                minbind.imgCerrar.setUI("v", false)
            }
            bind.lnrMini.addView(minbind.root)
        }
    }

    private fun coberturaDetalle(list: List<DetalleCobertura>) {
        var codigo = 0
        var nombre = ""
        val lv = mutableListOf<Venta>()
        val lov = mutableListOf<OrderVenta>()
        list.forEach { i ->
            if (codigo == 0) {
                codigo = i.codigo
                nombre = i.nombre
                lv.add(Venta(i.pedido,i.importe))
            } else {
                if (codigo == i.codigo) {
                    lv.add(Venta(i.pedido,i.importe))
                } else {
                    val cliente = "$codigo - $nombre"
                    lov.add(OrderVenta(cliente,lv.toList()))
                    lv.clear()
                    codigo = i.codigo
                    nombre = i.nombre
                    lv.add(Venta(i.pedido,i.importe))
                }
            }
        }

        val cliente = "$codigo - $nombre"
        lov.add(OrderVenta(cliente,lv.toList()))
        lv.clear()

        lov.forEach { j ->
            val padre = RowCoberturaDetalleBinding.inflate(layoutInflater,view as ViewGroup,false)
            padre.txtCliente.text = j.cliente
            j.pedidos.forEach { k ->
                val hijo = RowMiniCobdetBinding.inflate(layoutInflater,view as ViewGroup, false)
                hijo.txtPedido.text = "P. ${k.numero}"
                hijo.txtTotal.text = "s/ ${k.total}"
                padre.flxPedidos.addView(hijo.root)
            }
            bind.lnrMini.addView(padre.root)
        }
        lov.clear()
    }

    private fun cambios(list: List<Cambio>) {
        list.forEach { i ->
            val minbind = RowMiniDetalleBinding.inflate(layoutInflater, view as ViewGroup, false)
            val cambio = "Cambios: ${i.cambios}"
            val monto = "Monto: ${i.monto}"

            minbind.txtDatos.text = "${i.codigo} - ${i.nombre}"
            minbind.lnrDos.setUI("v", true)
            minbind.txtCambio.text = cambio
            minbind.txtMonto.text = monto
            minbind.imgCerrar.setOnClickListener { minbind.cardReporte.setUI("v", false) }

            bind.lnrMini.addView(minbind.root)
        }
    }

    /*private fun visicooler(list: List<Visicooler>) {
        list.forEach { i ->
            val minbind = RowMiniDetalleBinding.inflate(layoutInflater, view as ViewGroup, false)
            val cliente = "${i.cliente.codigo} - ${i.cliente.descripcion}"
            val modelo = "Modelo: ${i.equipo.descripcion}"
            val cuota = "Cuota: ${i.cuota}"
            val avance = "Avance: ${i.avance}"
            val porcentaje = "${percent(i.avance, i.avance)}%"

            minbind.lnrTres.setUI("v", true)
            minbind.txtVisicooler.setUI("v", true)
            minbind.txtDatos.text = cliente
            minbind.txtVisicooler.text = modelo
            minbind.txtCuota.text = cuota
            minbind.txtAvance.text = avance
            minbind.txtPorcentaje.text = porcentaje
            minbind.imgCerrar.setOnClickListener { minbind.cardReporte.setUI("v", false) }

            if (CONF.tipo == "V") {
                minbind.imgCerrar.setUI("v", false)
            }

            bind.lnrMini.addView(minbind.root)
        }
    }*/

    private fun getCoberturaDetalle() {
        val p = JSONObject()
        p.put("empleado", CONF.codigo)
        p.put("empresa", CONF.empresa)
        viewmodel.fetchDetCobertura(p.toReqBody())
    }

    private fun launchDownload(codigo: Int, nivel: Int) {
        val p = JSONObject()
        p.put("empleado", CONF.codigo)
        p.put("empresa", CONF.empresa)
        if (nivel < 2) {
            p.put("marca", codigo)
        } else {
            p.put("linea", codigo)
        }
        viewmodel.fetchSolesDetalle(p.toReqBody())
    }

    private fun setUmeSoles(list: List<Generico>?) {
        bind.lnrMini.removeAllViews()
        list?.forEach { i ->
            val minbind = RowReporteBinding.inflate(layoutInflater, view as ViewGroup, false)

            val titulo = i.datos.descripcion
            val cuota = "Cuota: ${i.cuota}"
            val avance = "Avance: ${i.avance}"
            val percent = percent(i.avance, i.cuota)
            val porcentaje = "$percent%"

            if (CONF.tipo == "V") {
                minbind.imgCerrar.setUI("v", false)
            }

            when {
                percent.toDouble() > 85 -> minbind.imgFlecha.setImageResource(R.drawable.f_arriba)
                percent.toDouble() in 70.0..85.0 -> {
                    ImageViewCompat.setImageTintList(
                        minbind.imgFlecha,
                        ColorStateList.valueOf(Color.parseColor("#FFAB00"))
                    )
                    minbind.imgFlecha.setImageResource(R.drawable.f_arriba)
                }

                percent.toDouble() in 1.0..69.99 -> minbind.imgFlecha.setImageResource(R.drawable.f_bajo)
                percent.toDouble() < 1 -> minbind.imgFlecha.setImageResource(R.drawable.f_neutral)
            }

            minbind.txtTitulo.text = titulo
            minbind.txtCuota.text = cuota
            minbind.txtAvance.text = avance
            minbind.txtPorcentaje.text = porcentaje
            minbind.imgCerrar.setOnClickListener { minbind.cardReporte.setUI("v", false) }

            bind.lnrMini.addView(minbind.root)
        }
    }

    private fun setCoberturaPendiente(list: List<Coberturados>?) {
        bind.lnrMini.removeAllViews()
        list?.forEach { i ->
            val minbind = RowTarjetaClienteBinding.inflate(layoutInflater, view as ViewGroup, false)

            val nombre = "${i.codigo} - ${i.nombre}"

            minbind.txtCliente.text = nombre
            minbind.txtDocumento.text = i.documento
            minbind.txtDomicilio.text = i.direccion

            bind.lnrMini.addView(minbind.root)
        }
    }

    private fun setPedidosRealizados(list: List<PedidoGeneral>?) {
        bind.lnrMini.removeAllViews()
        list?.forEach { i ->
            val minbind = RowMiniDetalleBinding.inflate(layoutInflater, view as ViewGroup, false)

            val nombre = "${i.id} - ${i.nombre}"
            val clientes = "Clientes: ${i.clientes}"
            val pedidos = "Pedidos: ${i.pedidos}"

            minbind.lnrUno.setUI("v", true)
            minbind.txtDatos.text = nombre
            minbind.txtCliente.text = clientes
            minbind.txtPedido.text = pedidos
            minbind.imgCerrar.setOnClickListener { minbind.cardReporte.setUI("v", false) }

            bind.lnrMini.addView(minbind.root)
        }
    }
}