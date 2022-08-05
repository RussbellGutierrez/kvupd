package com.upd.kvupd.ui.dialog

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.upd.kvupd.R
import com.upd.kvupd.data.model.*
import com.upd.kvupd.databinding.DialogMiniDetalleBinding
import com.upd.kvupd.databinding.RowMiniDetalleBinding
import com.upd.kvupd.databinding.RowReporteBinding
import com.upd.kvupd.databinding.RowTarjetaClienteBinding
import com.upd.kvupd.utils.*
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Constant.VISICOOLER_ID
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class DMiniDetalle : DialogFragment() {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: DialogMiniDetalleBinding? = null
    private val bind get() = _bind!!
    private val args: DMiniDetalleArgs by navArgs()
    private val _tag by lazy { DMiniDetalle::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCreate()
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

        checkMiniDetalle()

        viewmodel.detalle.observe(viewLifecycleOwner) { rsl ->
            when (rsl) {
                is NetworkRetrofit.Success -> {
                    val list = rsl.data?.jobl
                    if (!list.isNullOrEmpty()) {
                        bind.emptyContainer.root.setUI("v", false)
                        setUmeSoles(list)
                    }
                }
                is NetworkRetrofit.Error -> {
                    bind.emptyContainer.textView.text = rsl.message
                }
            }
        }

        viewmodel.cobpendiente.observe(viewLifecycleOwner) { rsl ->
            when (rsl) {
                is NetworkRetrofit.Success -> {
                    val list = rsl.data?.jobl
                    if (!list.isNullOrEmpty()) {
                        bind.emptyContainer.root.setUI("v", false)
                        setCoberturaPendiente(list)
                    }
                }
                is NetworkRetrofit.Error -> {
                    bind.emptyContainer.textView.text = rsl.message
                }
            }
        }

        viewmodel.pedigen.observe(viewLifecycleOwner) { rsl ->
            when (rsl) {
                is NetworkRetrofit.Success -> {
                    val list = rsl.data?.jobl
                    if (!list.isNullOrEmpty()) {
                        bind.emptyContainer.root.setUI("v", false)
                        setPedidosRealizados(list)
                    }
                }
                is NetworkRetrofit.Error -> {
                    bind.emptyContainer.textView.text = rsl.message
                }
            }
        }
    }

    private fun checkMiniDetalle() {
        args.preventa?.let {
            bind.emptyContainer.root.setUI("v", false)
            val titulo = "Informe Preventa"
            bind.txtTitulo.text = titulo
            preventa(it.toList())
        }
        args.cobertura?.let {
            bind.emptyContainer.root.setUI("v", false)
            val titulo = "Informe Cobertura"
            bind.txtTitulo.text = titulo
            cobertura(it.toList())
        }
        args.cartera?.let {
            if (CONF.tipo == "S") {
                bind.emptyContainer.root.setUI("v", false)
                val titulo = "Avance Clientes No Coberturados"
                bind.txtTitulo.text = titulo
                cobertura(it.toList())
            } else {
                val titulo = "Clientes No Coberturados"
                bind.txtTitulo.text = titulo
                val p = JSONObject()
                p.put("empleado", CONF.codigo)
                p.put("empresa", CONF.empresa)
                viewmodel.fetchCoberturaPendiente(p.toReqBody())
            }
        }
        args.cambios?.let {
            val titulo = "Informe Cambios"
            bind.txtTitulo.text = titulo
            if (it.isNotEmpty()) {
                bind.emptyContainer.root.setUI("v", false)
                cambios(it.toList())
            }
        }
        args.visicooler?.let {
            bind.emptyContainer.root.setUI("v", false)
            val titulo = if (VISICOOLER_ID > 0) {
                "Visicooler Vendedor $VISICOOLER_ID"
            } else {
                "Informe Visicooler"
            }
            VISICOOLER_ID = 0

            bind.txtTitulo.text = titulo
            visicooler(it.toList())
        }
        args.generico?.let {
            bind.txtTitulo.text = it.datos.descripcion
            launchDownload(it.datos.codigo,2)
        }
        args.umes?.let {
            bind.txtTitulo.text = it.linea.descripcion
            launchDownload(it.linea.codigo,1)
        }
        args.soles?.let {
            bind.txtTitulo.text = it.linea.descripcion
            launchDownload(it.linea.codigo,1)
        }
        if (args.pedidos > 0) {
            val titulo = "Informe Pedidos"
            bind.txtTitulo.text = titulo
            val p = JSONObject()
            p.put("empleado", CONF.codigo)
            p.put("empresa", CONF.empresa)
            viewmodel.fetchPediGen(p.toReqBody())
        }
    }

    private fun preventa(list: List<Volumen>) {
        bind.lnrMini.removeAllViews()
        list.forEach { i ->
            val minbind = RowMiniDetalleBinding.inflate(layoutInflater, view as ViewGroup, false)
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

    private fun cobertura(list: List<CobCart>) {
        bind.lnrMini.removeAllViews()
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

    private fun cambios(list: List<Cambio>) {
        bind.lnrMini.removeAllViews()
        list.forEach { i ->
            val minbind = RowMiniDetalleBinding.inflate(layoutInflater, view as ViewGroup, false)
            val cambio = "Cambios: ${i.cambios}"
            val monto = "Monto: ${i.monto}"

            minbind.lnrDos.setUI("v", true)
            minbind.txtCambio.text = cambio
            minbind.txtMonto.text = monto
            minbind.imgCerrar.setOnClickListener { minbind.cardReporte.setUI("v", false) }

            bind.lnrMini.addView(minbind.root)
        }
    }

    private fun visicooler(list: List<Visicooler>) {
        bind.lnrMini.removeAllViews()
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
    }

    private fun launchDownload(codigo: Int,nivel: Int) {
        val p = JSONObject()
        p.put("empleado", CONF.codigo)
        p.put("empresa", CONF.empresa)
        when(CONF.empresa) {
            1 -> { if (nivel < 2) p.put("linea",codigo) else p.put("generico", codigo) }
            2 -> { if (nivel < 2) p.put("marca",codigo) else p.put("linea", codigo) }
        }

        when (CONF.empresa) {
            1 -> viewmodel.fetchUmeDetalle(p.toReqBody())
            2 -> viewmodel.fetchSolesDetalle(p.toReqBody())
        }
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

    private fun setPedidosRealizados(list: List<PediGen>?) {
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