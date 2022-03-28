package com.upd.kvupd.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.upd.kvupd.data.model.*
import com.upd.kvupd.databinding.FragmentFServidorBinding
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Constant.IMEI
import com.upd.kvupd.utils.Network
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.utils.toReqBody
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.util.*
import kotlin.concurrent.schedule

@AndroidEntryPoint
class FServidor : Fragment() {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFServidorBinding? = null
    private val bind get() = _bind!!
    private var list1 = 0
    private var list2 = 0
    private var list3 = 0
    private var list4 = 0
    private var list5 = 0
    private var list6 = 0
    private lateinit var seguimiento: TSeguimiento
    private lateinit var visita: TVisita
    private lateinit var alta: TAlta
    private lateinit var altadatos: TADatos
    private lateinit var baja: TBaja
    private lateinit var bajaestado: TBEstado

    private val _tag by lazy { FServidor::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentFServidorBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (CONF.tipo == "V") {
            bind.cardBajaestado.setUI("v", false)
            bind.minicardBajaestado.setUI("v", false)
        }

        if (CONF.seguimiento == 0) {
            bind.cardSeguimiento.setUI("v",false)
            bind.minicardSeguimiento.setUI("v",false)
        }

        viewmodel.fetchServerAll("Todo")
        getDataRoom()
        updateDataRoom()
    }

    private fun getDataRoom() {
        if (CONF.seguimiento == 1) {
            viewmodel.servseguimiento.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let { y ->
                    setTextUI(y.size,0)
                    Timer().schedule(3000) {
                        y.forEach { j ->
                            seguimiento = j
                            val p = JSONObject()
                            p.put("fecha", j.fecha)
                            p.put("empleado", j.usuario)
                            p.put("longitud", j.longitud)
                            p.put("latitud", j.latitud)
                            p.put("precision", j.precision)
                            p.put("imei", IMEI)
                            p.put("bateria", j.bateria)
                            p.put("sucursal", CONF.sucursal)
                            p.put("esquema", CONF.esquema)
                            p.put("empresa", CONF.empresa)
                            viewmodel.webSeguimiento(p.toReqBody())
                        }
                    }
                }
            }
        }

        viewmodel.servvisita.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                setTextUI(y.size,1)
                Timer().schedule(3000) {
                    y.forEach { j ->
                        visita = j
                        val p = JSONObject()
                        p.put("cliente", j.cliente)
                        p.put("fecha", j.fecha)
                        p.put("empleado", j.usuario)
                        p.put("longitud", j.longitud)
                        p.put("latitud", j.latitud)
                        p.put("motivo", j.observacion)
                        p.put("precision", j.precision)
                        p.put("sucursal", CONF.sucursal)
                        p.put("esquema", CONF.esquema)
                        p.put("empresa", CONF.empresa)
                        viewmodel.webVisita(p.toReqBody())
                    }
                }
            }
        }

        viewmodel.servalta.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                setTextUI(y.size,2)
                Timer().schedule(3000) {
                    y.forEach { j ->
                        alta = j
                        val p = JSONObject()
                        p.put("empleado", j.empleado)
                        p.put("fecha", j.fecha)
                        p.put("id", j.idaux)
                        p.put("longitud", j.longitud)
                        p.put("latitud", j.latitud)
                        p.put("precision", j.precision)
                        p.put("sucursal", CONF.sucursal)
                        p.put("esquema", CONF.esquema)
                        p.put("empresa", CONF.empresa)
                        viewmodel.webAlta(p.toReqBody())
                    }
                }
            }
        }

        viewmodel.servaltadatos.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                setTextUI(y.size,3)
                Timer().schedule(3000) {
                    y.forEach { j ->
                        altadatos = j
                        val p = JSONObject()
                        p.put("empleado", j.empleado)
                        p.put("id", j.idaux)
                        p.put("appaterno", j.appaterno)
                        p.put("apmaterno", j.apmaterno)
                        p.put("nombre", j.nombre)
                        p.put("razon", j.razon)
                        p.put("tipo", j.tipo)
                        p.put("tipodoc", j.documento)
                        p.put("giro", j.giro.split("-")[0].trim())
                        p.put("movil1", j.movil1)
                        p.put("movil2", j.movil2)
                        p.put("email", j.correo)
                        p.put("urbanizacion", "${j.zona} ${j.zonanombre}")
                        p.put("altura", j.numero)
                        p.put("distrito", j.distrito.split("-")[0].trim())
                        p.put("ruta", j.ruta)
                        p.put("secuencia", j.secuencia)
                        p.put("sucursal", CONF.sucursal)
                        p.put("esquema", CONF.esquema)
                        p.put("empresa", CONF.empresa)

                        when (j.manzana) {
                            "" -> p.put("calle", "${j.via} ${j.direccion} ${j.ubicacion}")
                            else -> p.put(
                                "calle",
                                "${j.via} ${j.direccion} MZ ${j.manzana} ${j.ubicacion}"
                            )
                        }
                        viewmodel.webAltaDatos(p.toReqBody())
                    }
                }
            }
        }

        viewmodel.servbaja.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                setTextUI(y.size,4)
                Timer().schedule(3000) {
                    y.forEach { j ->
                        baja = j
                        val p = JSONObject()
                        p.put("empleado", CONF.codigo)
                        p.put("fecha", j.fecha)
                        p.put("cliente", j.cliente)
                        p.put("motivo", j.motivo)
                        p.put("observacion", j.comentario)
                        p.put("xcoord", j.longitud)
                        p.put("ycoord", j.latitud)
                        p.put("precision", j.precision)
                        p.put("anulado", j.anulado)
                        p.put("empresa", CONF.empresa)
                        viewmodel.webBaja(p.toReqBody())
                    }
                }
            }
        }

        viewmodel.servbajaestado.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                setTextUI(y.size,5)
                Timer().schedule(3000) {
                    y.forEach { j ->
                        bajaestado = j
                        val p = JSONObject()
                        p.put("empleado", j.empleado)
                        p.put("fecha", j.fecha)
                        p.put("cliente", j.cliente)
                        p.put("cfecha", j.fechaconf)
                        p.put("observacion", j.observacion)
                        p.put("precision", j.precision)
                        p.put("xcoord", j.longitud)
                        p.put("ycoord", j.latitud)
                        p.put("confirmar", j.procede)
                        p.put("empresa", CONF.empresa)
                        viewmodel.webBajaEstado(p.toReqBody())
                    }
                }
            }
        }
    }

    private fun updateDataRoom() {
        viewmodel.respseguimiento.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is Network.Success -> {
                        seguimiento.estado = "Enviado"
                        viewmodel.updSeguimiento(seguimiento)
                    }
                    is Network.Error -> Log.w(_tag, "S Error ${y.message} $seguimiento")
                }
                outputUI(0)
            }
        }

        viewmodel.respvisita.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is Network.Success -> {
                        visita.estado = "Enviado"
                        viewmodel.updVisita(visita)
                    }
                    is Network.Error -> Log.w(_tag, "V Error ${y.message} $visita")
                }
                outputUI(1)
            }
        }

        viewmodel.respalta.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is Network.Success -> {
                        alta.estado = "Enviado"
                        viewmodel.updAlta(alta)
                    }
                    is Network.Error -> Log.w(_tag, "A Error ${y.message} $alta")
                }
                outputUI(2)
            }
        }

        viewmodel.respaltadatos.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is Network.Success -> {
                        altadatos.estado = "Enviado"
                        viewmodel.updAltaDatos(altadatos)
                    }
                    is Network.Error -> Log.w(_tag, "AD Error ${y.message} $altadatos")
                }
                outputUI(3)
            }
        }

        viewmodel.respbaja.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is Network.Success -> {
                        baja.estado = "Enviado"
                        viewmodel.updBaja(baja)
                    }
                    is Network.Error -> Log.w(_tag, "B Error ${y.message} $baja")
                }
                outputUI(4)
            }
        }

        viewmodel.respbajaestado.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is Network.Success -> {
                        bajaestado.estado = "Enviado"
                        viewmodel.updBajaEstado(bajaestado)
                    }
                    is Network.Error -> Log.w(_tag, "BE Error ${y.message} $bajaestado")
                }
                outputUI(5)
            }
        }
    }

    private fun setTextUI(size: Int, opt: Int) {
        var texto = ""
        val mensaje = "SIN DATOS PARA ENVIAR"
        when(opt) {
            0 -> {
                list1 = size
                texto = "Ubicaciones : $size"
                bind.txtSeguimiento.text = texto
                if (size == 0) {
                    bind.progress1.setUI("v", false)
                    bind.txtComp1.setUI("v", true)
                    bind.txtComp1.text = mensaje
                }
            }
            1 -> {
                list2 = size
                texto = "Visitas : $size"
                bind.txtVisita.text = texto
                if (size == 0) {
                    bind.progress2.setUI("v", false)
                    bind.txtComp2.setUI("v", true)
                    bind.txtComp2.text = mensaje
                }
            }
            2 -> {
                list3 = size
                texto = "Altas : $size"
                bind.txtAlta.text = texto
                if (size == 0) {
                    bind.progress3.setUI("v", false)
                    bind.txtComp3.setUI("v", true)
                    bind.txtComp3.text = mensaje
                }
            }
            3 -> {
                list4 = size
                texto = "Detalle altas : $size"
                bind.txtAltadato.text = texto
                if (size == 0) {
                    bind.progress4.setUI("v", false)
                    bind.txtComp4.setUI("v", true)
                    bind.txtComp4.text = mensaje
                }
            }
            4 -> {
                list5 = size
                texto = "Bajas : $size"
                bind.txtBaja.text = texto
                if (size == 0) {
                    bind.progress5.setUI("v", false)
                    bind.txtComp5.setUI("v", true)
                    bind.txtComp5.text = mensaje
                }
            }
            5 -> {
                list6 = size
                texto = "Bajas confirmadas : $size"
                bind.txtBajaestado.text = texto
                if (size == 0) {
                    bind.progress6.setUI("v", false)
                    bind.txtComp6.setUI("v", true)
                    bind.txtComp6.text = mensaje
                }
            }
        }
    }

    private fun outputUI(opt: Int) {
        when(opt) {
            0 -> {
                if (list1 == 1) {
                    bind.progress1.setUI("v", false)
                    bind.txtComp1.setUI("v", true)
                } else {
                    list1--
                }
            }
            1 -> {
                if (list2 == 1) {
                    bind.progress2.setUI("v", false)
                    bind.txtComp2.setUI("v", true)
                } else {
                    list2--
                }
            }
            2 -> {
                if (list3 == 1) {
                    bind.progress3.setUI("v", false)
                    bind.txtComp3.setUI("v", true)
                } else {
                    list3--
                }
            }
            3 -> {
                if (list4 == 1) {
                    bind.progress4.setUI("v", false)
                    bind.txtComp4.setUI("v", true)
                } else {
                    list4--
                }
            }
            4 -> {
                if (list5 == 1) {
                    bind.progress5.setUI("v", false)
                    bind.txtComp5.setUI("v", true)
                } else {
                    list5--
                }
            }
            5 -> {
                if (list6 == 1) {
                    bind.progress6.setUI("v", false)
                    bind.txtComp6.setUI("v", true)
                } else {
                    list6--
                }
            }
        }
    }
}