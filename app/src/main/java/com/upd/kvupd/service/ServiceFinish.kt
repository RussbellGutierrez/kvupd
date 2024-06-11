package com.upd.kvupd.service

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LifecycleService
import com.upd.kvupd.data.model.TADatos
import com.upd.kvupd.data.model.TAlta
import com.upd.kvupd.data.model.TBEstado
import com.upd.kvupd.data.model.TBaja
import com.upd.kvupd.data.model.TRespuesta
import com.upd.kvupd.data.model.TSeguimiento
import com.upd.kvupd.data.model.TVisita
import com.upd.kvupd.domain.Functions
import com.upd.kvupd.domain.Repository
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Constant.IMEI
import com.upd.kvupd.utils.Constant.isCONFinitialized
import com.upd.kvupd.utils.Interface.closeListener
import com.upd.kvupd.utils.Interface.interListener
import com.upd.kvupd.utils.NetworkRetrofit
import com.upd.kvupd.utils.isServiceRunning
import com.upd.kvupd.utils.toReqBody
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.schedule

@AndroidEntryPoint
class ServiceFinish : LifecycleService() {

    @Inject
    lateinit var functions: Functions

    @Inject
    lateinit var repository: Repository

    private var pse: List<TSeguimiento>? = null
    private var pvi: List<TVisita>? = null
    private var pal: List<TAlta>? = null
    private var pad: List<TADatos>? = null
    private var pba: List<TBaja>? = null
    private var pbe: List<TBEstado>? = null
    private var pre: List<TRespuesta>? = null
    private var pfo: List<TRespuesta>? = null
    private val _tag by lazy { ServiceFinish::class.java.simpleName }

    override fun onDestroy() {
        Log.v(_tag, "Service finish destroyed")
        super.onDestroy()
    }

    override fun onCreate() {
        super.onCreate()
        functions.closeAllNotifications()
        procedureExit()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(_tag, "Processing")
        return START_NOT_STICKY
    }

    private fun procedureExit() {
        CoroutineScope(Dispatchers.IO).launch {
            //up to server
            repository.getServerSeguimiento("Todo").also {
                pse = it
                sendServer(0)
            }
            repository.getServerVisita("Todo").also {
                pvi = it
                sendServer(1)
            }
            repository.getServerAlta("Todo").also {
                pal = it
                sendServer(2)
            }
            repository.getServerAltadatos("Todo").also {
                pad = it
                sendServer(3)
            }
            repository.getServerBaja("Todo").also {
                pba = it
                sendServer(4)
            }
            repository.getServerBajaestado("Todo").also {
                pbe = it
                sendServer(5)
            }
            repository.getServerRespuesta("Todo").also {
                pre = it
                sendServer(6)
            }
            repository.getServerFoto("Todo").also {
                pfo = it
                sendServer(7)
            }

            functions.closePeriodicWorker()

            repository.getStarterTime().let {
                functions.alarmSetup(it)
                Log.i(_tag, "Starter time $it")
            }

            Timer().schedule(10000) {
                Log.w(_tag, "Cleaning and closing app")
                deleteTables()
                closeListener?.closingActivity() ?: run {
                    if (isServiceRunning(ServicePosicion::class.java))
                        stopService(Intent(this@ServiceFinish, ServicePosicion::class.java))

                    interListener?.changeBetweenIconNotification(1)
                    interListener?.closeGPS()
                    stopSelf()
                }
            }
        }
    }

    private fun sendServer(opt: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            when (opt) {
                0 -> {
                    if (isCONFinitialized() && CONF.seguimiento == 1) {
                        pse?.forEach { i ->
                            val p = JSONObject()
                            p.put("fecha", i.fecha)
                            p.put("empleado", i.usuario)
                            p.put("longitud", i.longitud)
                            p.put("latitud", i.latitud)
                            p.put("precision", i.precision)
                            p.put("imei", IMEI)
                            p.put("bateria", i.bateria)
                            p.put("sucursal", CONF.sucursal)
                            p.put("esquema", CONF.esquema)
                            p.put("empresa", CONF.empresa)
                            repository.setWebSeguimiento(p.toReqBody()).collect {
                                when (it) {
                                    is NetworkRetrofit.Success -> {
                                        i.estado = "Enviado"
                                        repository.saveSeguimiento(i)
                                        Log.d(_tag, "Seguimiento enviado $i")
                                    }

                                    is NetworkRetrofit.Error -> Log.e(
                                        _tag, "Seguimiento Error ${it.message}"
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    pvi?.forEach { i ->
                        val p = JSONObject()
                        p.put("cliente", i.cliente)
                        p.put("fecha", i.fecha)
                        p.put("empleado", i.usuario)
                        p.put("longitud", i.longitud)
                        p.put("latitud", i.latitud)
                        p.put("motivo", i.observacion)
                        p.put("precision", i.precision)
                        p.put("sucursal", CONF.sucursal)
                        p.put("esquema", CONF.esquema)
                        p.put("empresa", CONF.empresa)
                        repository.setWebVisita(p.toReqBody()).collect {
                            when (it) {
                                is NetworkRetrofit.Success -> {
                                    i.estado = "Enviado"
                                    repository.saveVisita(i)
                                    Log.d(_tag, "Visita enviado $i")
                                }

                                is NetworkRetrofit.Error -> Log.e(
                                    _tag,
                                    "Visita Error ${it.message}"
                                )
                            }
                        }
                    }
                }

                2 -> {
                    pal?.forEach { i ->
                        val p = JSONObject()
                        p.put("empleado", i.empleado)
                        p.put("fecha", i.fecha)
                        p.put("id", i.idaux)
                        p.put("longitud", i.longitud)
                        p.put("latitud", i.latitud)
                        p.put("precision", i.precision)
                        p.put("sucursal", CONF.sucursal)
                        p.put("esquema", CONF.esquema)
                        p.put("empresa", CONF.empresa)
                        repository.setWebAlta(p.toReqBody()).collect {
                            when (it) {
                                is NetworkRetrofit.Success -> {
                                    i.estado = "Enviado"
                                    repository.saveAlta(i)
                                    Log.d(_tag, "Alta enviado $i")
                                }

                                is NetworkRetrofit.Error -> Log.e(_tag, "Alta Error ${it.message}")
                            }
                        }
                    }
                }

                3 -> {
                    pad?.forEach { i ->
                        val p = JSONObject()
                        p.put("empleado", i.empleado)
                        p.put("id", i.idaux)
                        p.put("appaterno", i.appaterno)
                        p.put("apmaterno", i.apmaterno)
                        p.put("nombre", i.nombre)
                        p.put("razon", i.razon)
                        p.put("tipo", i.tipo)
                        p.put("dnice", i.dnice)
                        p.put("ruc", i.ruc)
                        p.put("tdoc", i.tipodocu)
                        //p.put("tipodoc", i.documento)
                        p.put("giro", i.giro.split("-")[0].trim())
                        p.put("movil1", i.movil1)
                        p.put("movil2", i.movil2)
                        p.put("email", i.correo)
                        p.put("urbanizacion", "${i.zona} ${i.zonanombre}")
                        p.put("altura", i.numero)
                        p.put("distrito", i.distrito.split("-")[0].trim())
                        p.put("ruta", i.ruta.split(" ")[2].trim())
                        p.put("imei", IMEI)
                        p.put("secuencia", i.secuencia)
                        p.put("sucursal", CONF.sucursal)
                        p.put("esquema", CONF.esquema)
                        p.put("empresa", CONF.empresa)

                        when (i.manzana) {
                            "" -> p.put("calle", "${i.via} ${i.direccion} ${i.ubicacion}")
                            else -> p.put(
                                "calle",
                                "${i.via} ${i.direccion} MZ ${i.manzana} ${i.ubicacion}"
                            )
                        }
                        repository.setWebAltaDatos(p.toReqBody()).collect {
                            when (it) {
                                is NetworkRetrofit.Success -> {
                                    i.estado = "Enviado"
                                    repository.saveAltaDatos(i)
                                    Log.d(_tag, "Altadato enviado $i")
                                }

                                is NetworkRetrofit.Error -> Log.e(
                                    _tag,
                                    "Altadato Error ${it.message}"
                                )
                            }
                        }
                    }
                }

                4 -> {
                    pba?.forEach { i ->
                        val p = JSONObject()
                        p.put("empleado", CONF.codigo)
                        p.put("fecha", i.fecha)
                        p.put("cliente", i.cliente)
                        p.put("motivo", i.motivo)
                        p.put("observacion", i.comentario)
                        p.put("xcoord", i.longitud)
                        p.put("ycoord", i.latitud)
                        p.put("precision", i.precision)
                        p.put("anulado", i.anulado)
                        p.put("empresa", CONF.empresa)
                        repository.setWebBaja(p.toReqBody()).collect {
                            when (it) {
                                is NetworkRetrofit.Success -> {
                                    i.estado = "Enviado"
                                    repository.saveBaja(i)
                                    Log.d(_tag, "Baja enviado $i")
                                }

                                is NetworkRetrofit.Error -> Log.e(_tag, "Baja Error ${it.message}")
                            }
                        }
                    }
                }

                5 -> {
                    pbe?.forEach { i ->
                        val p = JSONObject()
                        p.put("empleado", i.empleado)
                        p.put("fecha", i.fecha)
                        p.put("cliente", i.cliente)
                        p.put("cfecha", i.fechaconf)
                        p.put("observacion", i.observacion)
                        p.put("precision", i.precision)
                        p.put("xcoord", i.longitud)
                        p.put("ycoord", i.latitud)
                        p.put("confirmar", i.procede)
                        p.put("empresa", CONF.empresa)
                        repository.setWebBajaEstados(p.toReqBody()).collect {
                            when (it) {
                                is NetworkRetrofit.Success -> {
                                    i.estado = "Enviado"
                                    repository.saveBajaEstado(i)
                                    Log.d(_tag, "Bajaestado enviado $i")
                                }

                                is NetworkRetrofit.Error -> Log.e(
                                    _tag,
                                    "Bajaestado Error ${it.message}"
                                )
                            }
                        }
                    }
                }

                6 -> {
                    pre?.forEach { i ->
                        val p = JSONObject()
                        p.put("empresa", CONF.empresa)
                        p.put("empleado", CONF.codigo)
                        p.put("cliente", i.cliente)
                        p.put("encuesta", i.encuesta)
                        p.put("pregunta", i.pregunta)
                        p.put("respuesta", i.respuesta)
                        p.put("fecha", i.fecha)
                        repository.setWebRespuestas(p.toReqBody()).collect {
                            when (it) {
                                is NetworkRetrofit.Success -> {
                                    i.estado = "Enviado"
                                    repository.saveRespuestaOneByOne(i)
                                    Log.d(_tag, "Respuesta enviado $i")
                                }

                                is NetworkRetrofit.Error -> Log.e(
                                    _tag,
                                    "Respuesta Error ${it.message}"
                                )
                            }
                        }
                    }
                }

                7 -> {
                    pfo?.forEach { i ->
                        val baos = ByteArrayOutputStream()
                        val bm = BitmapFactory.decodeFile(i.rutafoto)
                        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos)
                        val byteArray = baos.toByteArray()
                        val foto = Base64.encodeToString(byteArray, Base64.DEFAULT)

                        val p = JSONObject()
                        p.put("empresa", CONF.empresa)
                        p.put("empleado", CONF.codigo)
                        p.put("cliente", i.cliente)
                        p.put("encuesta", i.encuesta)
                        p.put("sucursal", CONF.sucursal)
                        p.put("foto", foto)
                        repository.setWebFotos(p.toReqBody()).collect {
                            when (it) {
                                is NetworkRetrofit.Success -> {
                                    i.estado = "Enviado"
                                    repository.saveFoto(i)
                                    Log.d(_tag, "Foto enviado $i")
                                }

                                is NetworkRetrofit.Error -> Log.e(_tag, "Foto Error ${it.message}")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun deleteTables() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteConfig()
            repository.deleteClientes()
            repository.deleteEmpleados()
            repository.deleteDistritos()
            repository.deleteNegocios()
            repository.deleteRutas()
            repository.deleteEncuesta()
            repository.deleteEncuestaSeleccionado()
            repository.deleteRespuesta()
            repository.deleteEstado()
            repository.deleteSeguimiento()
            repository.deleteVisita()
            repository.deleteAlta()
            repository.deleteAltaDatos()
            repository.deleteBaja()
            repository.deleteBajaSuper()
            repository.deleteBajaEstado()
            functions.deleteFotos()
            repository.deleteIncidencia()
            repository.deleteAAux()
        }
    }
}