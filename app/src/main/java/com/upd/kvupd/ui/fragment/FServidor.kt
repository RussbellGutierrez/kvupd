package com.upd.kvupd.ui.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.upd.kvupd.R
import com.upd.kvupd.data.model.TADatos
import com.upd.kvupd.data.model.TAFoto
import com.upd.kvupd.data.model.TAlta
import com.upd.kvupd.data.model.TBEstado
import com.upd.kvupd.data.model.TBaja
import com.upd.kvupd.data.model.TRespuesta
import com.upd.kvupd.data.model.TSeguimiento
import com.upd.kvupd.data.model.TVisita
import com.upd.kvupd.databinding.FragmentFServidorBinding
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Constant.IMEI
import com.upd.kvupd.utils.Constant.IP_AUX
import com.upd.kvupd.utils.Constant.OPTURL
import com.upd.kvupd.utils.HostSelectionInterceptor
import com.upd.kvupd.utils.NetworkRetrofit
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.utils.showDialog
import com.upd.kvupd.utils.toReqBody
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.schedule

@AndroidEntryPoint
class FServidor : Fragment(), MenuProvider {

    @Inject
    lateinit var host: HostSelectionInterceptor

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFServidorBinding? = null
    private val bind get() = _bind!!
    private var prevIP = false
    private var listS = 0
    private var listV = 0
    private var listA = 0
    private var listAD = 0
    private var listB = 0
    private var listBE = 0
    private var listR = 0
    private var listF = 0
    private var listDNI = 0
    private var mS = ""
    private var mV = ""
    private var mA = ""
    private var mAD = ""
    private var mB = ""
    private var mBE = ""
    private var mR = ""
    private var mF = ""
    private var mDNI = ""
    private var errorResponse = 0
    private lateinit var seguimiento: TSeguimiento
    private lateinit var visita: TVisita
    private lateinit var alta: TAlta
    private lateinit var altadatos: TADatos
    private lateinit var baja: TBaja
    private lateinit var bajaestado: TBEstado
    private lateinit var respuesta: TRespuesta
    private lateinit var foto: TRespuesta
    private lateinit var dni: TAFoto

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

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        if (CONF.tipo == "V") {
            bind.cardBajaestado.setUI("v", false)
            bind.minicardBajaestado.setUI("v", false)
        }

        if (CONF.seguimiento == 0) {
            bind.cardSeguimiento.setUI("v", false)
            bind.minicardSeguimiento.setUI("v", false)
        }

        restoreUI()
        viewmodel.fetchServerAll()
        getDataRoom()
        updateDataRoom()

        viewmodel.ipaux.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { ip ->
                prevIP = true
                OPTURL = "aux"
                IP_AUX = "http://$ip/api/"
                host.setHostBaseUrl()

                restoreUI()
                viewmodel.fetchServerAll()
                getDataRoom()
                updateDataRoom()
            }
        }

        viewmodel.urlServer.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { server ->
                if (server) {
                    host.setHostBaseUrl()
                    restoreUI()
                    viewmodel.fetchServerAll()
                    getDataRoom()
                    updateDataRoom()
                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.servidor_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.emergencia -> consume { findNavController().navigate(R.id.action_FServidor_to_BDEmergencia) }
        R.id.situacional -> consume { retryUploadData() }
        else -> false
    }

    private fun getDataRoom() {
        if (CONF.seguimiento == 1) {
            viewmodel.servseguimiento.observe(viewLifecycleOwner) {
                it.getContentIfNotHandled()?.let { y ->
                    setTextUI(y.size, 0)
                    mS = ""
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
                            Log.d(_tag, "Seg: $p")
                            viewmodel.webSeguimiento(p.toReqBody())
                        }
                    }
                }
            }
        }

        viewmodel.servvisita.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                setTextUI(y.size, 1)
                mV = ""
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
                        Log.d(_tag, "Vis: $p")
                        viewmodel.webVisita(p.toReqBody())
                    }
                }
            }
        }

        viewmodel.servalta.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                setTextUI(y.size, 2)
                mA = ""
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
                        Log.d(_tag, "Alt: $p")
                        viewmodel.webAlta(p.toReqBody())
                    }
                }
            }
        }

        viewmodel.servaltadatos.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                setTextUI(y.size, 3)
                mAD = ""
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
                        p.put("dnice", j.dnice)
                        p.put("ruc", j.ruc)
                        p.put("tdoc", j.tipodocu)
                        //p.put("tipodoc", j.documento)
                        p.put("giro", j.giro.split("-")[0].trim())
                        p.put("movil1", j.movil1)
                        p.put("movil2", j.movil2)
                        p.put("email", j.correo)
                        p.put("urbanizacion", "${j.zona} ${j.zonanombre}")
                        p.put("altura", j.numero)
                        p.put("distrito", j.distrito.split("-")[0].trim())
                        p.put("ruta", j.ruta.split(" ")[2].trim())
                        p.put("imei", IMEI)
                        p.put("secuencia", j.secuencia)
                        p.put("sucursal", CONF.sucursal)
                        p.put("esquema", CONF.esquema)
                        p.put("empresa", CONF.empresa)
                        p.put("observacion", j.observacion)

                        when (j.manzana) {
                            "" -> p.put("calle", "${j.via} ${j.direccion} ${j.ubicacion}")
                            else -> p.put(
                                "calle",
                                "${j.via} ${j.direccion} MZ ${j.manzana} ${j.ubicacion}"
                            )
                        }
                        Log.d(_tag, "AltD: $p")
                        viewmodel.webAltaDatos(p.toReqBody())
                    }
                }
            }
        }

        viewmodel.servbaja.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                setTextUI(y.size, 4)
                mB = ""
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
                        if (CONF.tipo == "S") {
                            p.put("estado", 2)
                        }
                        Log.d(_tag, "Baj: $p")
                        viewmodel.webBaja(p.toReqBody())
                    }
                }
            }
        }

        viewmodel.servbajaestado.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                setTextUI(y.size, 5)
                mBE = ""
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
                        Log.d(_tag, "BajE: $p")
                        viewmodel.webBajaEstado(p.toReqBody())
                    }
                }
            }
        }

        viewmodel.servrespuesta.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                calcEncuestasTotal(y)
                mR = ""
                Timer().schedule(3000) {
                    y.forEach { j ->
                        respuesta = j
                        val p = JSONObject()
                        p.put("empresa", CONF.empresa)
                        p.put("empleado", CONF.codigo)
                        p.put("cliente", j.cliente)
                        p.put("encuesta", j.encuesta)
                        p.put("pregunta", j.pregunta)
                        p.put("respuesta", j.respuesta)
                        p.put("xcoord", j.longitud)
                        p.put("ycoord", j.latitud)
                        p.put("fecha", j.fecha)
                        Log.d(_tag, "Resp: $p")
                        viewmodel.webRespuesta(p.toReqBody())
                    }
                }
            }
        }

        viewmodel.servfoto.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                setTextUI(y.size, 7)
                mF = ""
                Timer().schedule(3000) {
                    y.forEach { j ->
                        foto = j
                        val baos = ByteArrayOutputStream()
                        Log.w(_tag, "Ruta foto ${j.rutafoto}")
                        val bm = BitmapFactory.decodeFile(j.rutafoto)
                        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos)
                        val byteArray = baos.toByteArray()
                        val pic = Base64.encodeToString(byteArray, Base64.DEFAULT)

                        val p = JSONObject()
                        p.put("empresa", CONF.empresa)
                        p.put("empleado", CONF.codigo)
                        p.put("cliente", j.cliente)
                        p.put("encuesta", j.encuesta)
                        p.put("sucursal", CONF.sucursal)
                        p.put("foto", pic)
                        Log.d(_tag, "Fot: $p")
                        viewmodel.webFoto(p.toReqBody())
                    }
                }
            }
        }

        viewmodel.servdni.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                setTextUI(y.size, 8)
                mDNI = ""
                Timer().schedule(3000) {
                    y.forEach { j ->
                        dni = j
                        val baos = ByteArrayOutputStream()
                        Log.w(_tag, "Ruta dni ${j.ruta}")
                        val bm = BitmapFactory.decodeFile(j.ruta)
                        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos)
                        val byteArray = baos.toByteArray()
                        val pic = Base64.encodeToString(byteArray, Base64.DEFAULT)

                        val p = JSONObject()
                        p.put("fecha", j.fecha)
                        p.put("id", j.idaux)
                        p.put("empleado", j.empleado)
                        p.put("empresa", CONF.empresa)
                        p.put("foto", pic)
                        Log.d(_tag, "DNI: $p")
                        viewmodel.webDNI(p.toReqBody())
                    }
                }
            }
        }
    }

    private fun updateDataRoom() {
        viewmodel.respseguimiento.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> if (::seguimiento.isInitialized) {
                        seguimiento.estado = "Enviado"
                        viewmodel.updSeguimiento(seguimiento)
                    }

                    is NetworkRetrofit.Error -> {
                        errorResponse++
                        mS = y.message!!
                        Log.w(_tag, "Seguimiento-> ${y.message} $seguimiento")
                    }
                }
                outputUI(0, mS)
            }
        }

        viewmodel.respvisita.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> if (::visita.isInitialized) {
                        visita.estado = "Enviado"
                        viewmodel.updVisita(visita)
                    }

                    is NetworkRetrofit.Error -> {
                        errorResponse++
                        mV = y.message!!
                        Log.w(_tag, "Visita-> ${y.message} $visita")
                    }
                }
                outputUI(1, mV)
            }
        }

        viewmodel.respalta.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> if (::alta.isInitialized) {
                        alta.estado = "Enviado"
                        viewmodel.updAlta(alta)
                    }

                    is NetworkRetrofit.Error -> {
                        errorResponse++
                        mA = y.message!!
                        Log.w(_tag, "Alta-> ${y.message} $alta")
                    }
                }
                outputUI(2, mA)
            }
        }

        viewmodel.respaltadatos.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> if (::altadatos.isInitialized) {
                        altadatos.estado = "Enviado"
                        viewmodel.updAltaDatos(altadatos)
                    }

                    is NetworkRetrofit.Error -> {
                        errorResponse++
                        mAD = y.message!!
                        Log.w(_tag, "AltaDatos-> ${y.message} $altadatos")
                    }
                }
                outputUI(3, mAD)
            }
        }

        viewmodel.respbaja.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> if (::baja.isInitialized) {
                        baja.estado = "Enviado"
                        viewmodel.updBaja(baja)
                    }

                    is NetworkRetrofit.Error -> {
                        errorResponse++
                        mB = y.message!!
                        Log.w(_tag, "Baja-> ${y.message} $baja")
                    }
                }
                outputUI(4, mB)
            }
        }

        viewmodel.respbajaestado.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> if (::bajaestado.isInitialized) {
                        bajaestado.estado = "Enviado"
                        viewmodel.updBajaEstado(bajaestado)
                    }

                    is NetworkRetrofit.Error -> {
                        errorResponse++
                        mBE = y.message!!
                        Log.w(_tag, "BajaEstado-> ${y.message} $bajaestado")
                    }
                }
                outputUI(5, mBE)
            }
        }

        viewmodel.resprespuesta.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> if (::respuesta.isInitialized) {
                        respuesta.estado = "Enviado"
                        viewmodel.updRespuesta(respuesta)
                        Log.d(_tag, "Respuesta enviada $respuesta")
                    }

                    is NetworkRetrofit.Error -> {
                        errorResponse++
                        mR = y.message!!
                        Log.e(_tag, "Respuesta-> ${y.message} $respuesta")
                    }
                }
                outputUI(6, mR)
            }
        }

        viewmodel.respfoto.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> if (::foto.isInitialized) {
                        foto.estado = "Enviado"
                        viewmodel.updRespuesta(foto)
                        Log.d(_tag, "Foto enviada $foto")
                    }

                    is NetworkRetrofit.Error -> {
                        errorResponse++
                        mF = y.message!!
                        Log.e(_tag, "Foto-> ${y.message} $foto")
                    }
                }
                outputUI(7, mF)
            }
        }

        viewmodel.respdni.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is NetworkRetrofit.Success -> if (::dni.isInitialized) {
                        dni.estado = "Enviado"
                        viewmodel.updAltaFoto(dni)
                        Log.d(_tag, "DNI enviado $dni")
                    }

                    is NetworkRetrofit.Error -> {
                        errorResponse++
                        mDNI = y.message!!
                        Log.e(_tag, "DNI-> ${y.message} $dni")
                    }
                }
                outputUI(8, mDNI)
            }
        }
    }

    private fun restoreUI() {
        bind.progress1.setUI("v", true)
        bind.txtComp1.setUI("v", false)
        bind.progress2.setUI("v", true)
        bind.txtComp2.setUI("v", false)
        bind.progress3.setUI("v", true)
        bind.txtComp3.setUI("v", false)
        bind.progress4.setUI("v", true)
        bind.txtComp4.setUI("v", false)
        bind.progress5.setUI("v", true)
        bind.txtComp5.setUI("v", false)
        bind.progress6.setUI("v", true)
        bind.txtComp6.setUI("v", false)
        bind.progress7.setUI("v", true)
        bind.txtComp7.setUI("v", false)
        bind.progress8.setUI("v", true)
        bind.txtComp8.setUI("v", false)
        bind.progress9.setUI("v", true)
        bind.txtComp9.setUI("v", false)
    }

    private fun setTextUI(size: Int, opt: Int) {
        val texto: String
        val mensaje = "SIN DATOS PARA ENVIAR"
        when (opt) {
            0 -> {
                listS = size
                texto = "Ubicaciones : $size"
                bind.txtSeguimiento.text = texto
                if (size == 0) {
                    bind.progress1.setUI("v", false)
                    bind.txtComp1.setUI("v", true)
                    bind.txtComp1.text = mensaje
                }
            }

            1 -> {
                listV = size
                texto = "Visitas : $size"
                bind.txtVisita.text = texto
                if (size == 0) {
                    bind.progress2.setUI("v", false)
                    bind.txtComp2.setUI("v", true)
                    bind.txtComp2.text = mensaje
                }
            }

            2 -> {
                listA = size
                texto = "Altas : $size"
                bind.txtAlta.text = texto
                if (size == 0) {
                    bind.progress3.setUI("v", false)
                    bind.txtComp3.setUI("v", true)
                    bind.txtComp3.text = mensaje
                }
            }

            3 -> {
                listAD = size
                texto = "Detalle altas : $size"
                bind.txtAltadato.text = texto
                if (size == 0) {
                    bind.progress4.setUI("v", false)
                    bind.txtComp4.setUI("v", true)
                    bind.txtComp4.text = mensaje
                }
            }

            4 -> {
                listB = size
                texto = "Bajas : $size"
                bind.txtBaja.text = texto
                if (size == 0) {
                    bind.progress5.setUI("v", false)
                    bind.txtComp5.setUI("v", true)
                    bind.txtComp5.text = mensaje
                }
            }

            5 -> {
                listBE = size
                texto = "Bajas confirmadas : $size"
                bind.txtBajaestado.text = texto
                if (size == 0) {
                    bind.progress6.setUI("v", false)
                    bind.txtComp6.setUI("v", true)
                    bind.txtComp6.text = mensaje
                }
            }

            6 -> {
                listR = size
                texto = "Encuestas resueltas : $size"
                bind.txtRespuesta.text = texto
                if (size == 0) {
                    bind.progress7.setUI("v", false)
                    bind.txtComp7.setUI("v", true)
                    bind.txtComp7.text = mensaje
                }
            }

            7 -> {
                listF = size
                texto = "Fotos encuesta : $size"
                bind.txtFoto.text = texto
                if (size == 0) {
                    bind.progress8.setUI("v", false)
                    bind.txtComp8.setUI("v", true)
                    bind.txtComp8.text = mensaje
                }
            }

            8 -> {
                listDNI = size
                texto = "DNI tomados : $size"
                bind.txtDni.text = texto
                if (size == 0) {
                    bind.progress9.setUI("v", false)
                    bind.txtComp9.setUI("v", true)
                    bind.txtComp9.text = mensaje
                }
            }
        }
    }

    private fun outputUI(opt: Int, msg: String) {
        when (opt) {
            0 -> {
                if (listS == 1) {
                    bind.progress1.setUI("v", false)
                    bind.txtComp1.setUI("v", true)
                    if (msg != "") {
                        bind.txtComp1.text = msg
                    } else {
                        bind.txtComp1.text = "COMPLETO"
                    }
                    listS--
                } else {
                    listS--
                }
            }

            1 -> {
                if (listV == 1) {
                    bind.progress2.setUI("v", false)
                    bind.txtComp2.setUI("v", true)
                    if (msg != "") {
                        bind.txtComp2.text = msg
                    } else {
                        bind.txtComp2.text = "COMPLETO"
                    }
                    listV--
                } else {
                    listV--
                }
            }

            2 -> {
                if (listA == 1) {
                    bind.progress3.setUI("v", false)
                    bind.txtComp3.setUI("v", true)
                    if (msg != "") {
                        bind.txtComp3.text = msg
                    } else {
                        bind.txtComp3.text = "COMPLETO"
                    }
                    listA--
                } else {
                    listA--
                }
            }

            3 -> {
                if (listAD == 1) {
                    bind.progress4.setUI("v", false)
                    bind.txtComp4.setUI("v", true)
                    if (msg != "") {
                        bind.txtComp4.text = msg
                    } else {
                        bind.txtComp4.text = "COMPLETO"
                    }
                    listAD--
                } else {
                    listAD--
                }
            }

            4 -> {
                if (listB == 1) {
                    bind.progress5.setUI("v", false)
                    bind.txtComp5.setUI("v", true)
                    if (msg != "") {
                        bind.txtComp5.text = msg
                    } else {
                        bind.txtComp5.text = "COMPLETO"
                    }
                    listB--
                } else {
                    listB--
                }
            }

            5 -> {
                if (listBE == 1) {
                    bind.progress6.setUI("v", false)
                    bind.txtComp6.setUI("v", true)
                    if (msg != "") {
                        bind.txtComp6.text = msg
                    } else {
                        bind.txtComp6.text = "COMPLETO"
                    }
                    listBE--
                } else {
                    listBE--
                }
            }

            6 -> {
                if (listR == 1) {
                    bind.progress7.setUI("v", false)
                    bind.txtComp7.setUI("v", true)
                    if (msg != "") {
                        bind.txtComp7.text = msg
                    } else {
                        bind.txtComp7.text = "COMPLETO"
                    }
                    listR--
                } else {
                    listR--
                }
            }

            7 -> {
                if (listF == 1) {
                    bind.progress8.setUI("v", false)
                    bind.txtComp8.setUI("v", true)
                    if (msg != "") {
                        bind.txtComp8.text = msg
                    } else {
                        bind.txtComp8.text = "COMPLETO"
                    }
                    listF--
                } else {
                    listF--
                }
            }

            8 -> {
                if (listDNI == 1) {
                    bind.progress9.setUI("v", false)
                    bind.txtComp9.setUI("v", true)
                    if (msg != "") {
                        bind.txtComp9.text = msg
                    } else {
                        bind.txtComp9.text = "COMPLETO"
                    }
                    listDNI--
                } else {
                    listDNI--
                }
            }
        }
        if (listS == 0 && listV == 0 && listA == 0 && listAD == 0 && listB == 0 && listBE == 0 && listR == 0 && listF == 0 && listDNI == 0) {
            if (prevIP) {
                prevIP = false
            }
        }
    }

    private fun calcEncuestasTotal(list: List<TRespuesta>) {
        val tmn = arrayListOf<TRespuesta>()
        var cli = 0
        var enc = 0
        list.forEach { g ->
            if (cli != g.cliente.toInt() || enc != g.encuesta) {
                cli = g.cliente.toInt()
                enc = g.encuesta
                tmn.add(g)
            }
        }
        setTextUI(tmn.size, 6)
    }

    private fun retryUploadData() {
        if (errorResponse > 0) {
            showDialog(
                "Advertencia", "Use esta opcion en caso presente problemas para subir los datos, " +
                        "en cada intento se van modificando las direcciones IP, si no es posible enviar datos utilice la opcion " +
                        "alternativa, se encuentra a la derecha en la parte superior.", true
            ) {
                errorResponse = 0
                viewmodel.changeURLserver()
            }
        } else {
            showDialog("Correcto", "No es necesario, no hubo errores al subir los datos") {}
        }
    }
}