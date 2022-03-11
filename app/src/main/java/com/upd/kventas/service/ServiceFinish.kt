package com.upd.kventas.service

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import com.upd.kventas.data.model.*
import com.upd.kventas.domain.Functions
import com.upd.kventas.domain.Repository
import com.upd.kventas.utils.Constant.CONF
import com.upd.kventas.utils.Constant.IMEI
import com.upd.kventas.utils.Interface.serviceListener
import com.upd.kventas.utils.Network
import com.upd.kventas.utils.isServiceRunning
import com.upd.kventas.utils.toReqBody
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
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
    private val _tag by lazy { ServiceFinish::class.java.simpleName }

    override fun onDestroy() {
        Log.d(_tag, "Service finish destroyed")
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(_tag, "Processing")
        procedureExit()
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

            functions.closePeriodicWorkers()

            Timer().schedule(10000) {
                Log.d(_tag, "Final part")
                deleteTables()
                if (serviceListener != null) {
                    serviceListener?.onClosingActivity()
                } else {
                    if (isServiceRunning(ServiceSetup::class.java))
                        stopService(Intent(this@ServiceFinish, ServiceSetup::class.java))

                    if (isServiceRunning(ServicePosicion::class.java))
                        stopService(Intent(this@ServiceFinish, ServicePosicion::class.java))

                    stopSelf()
                }
            }
        }
    }

    private fun sendServer(opt: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            when (opt) {
                0 -> {
                    if (CONF.seguimiento == 1) {
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
                                    is Network.Success -> {
                                        i.estado = "Enviado"
                                        repository.saveSeguimiento(i)
                                        Log.d(_tag, "Seguimiento enviado $i")
                                    }
                                    is Network.Error -> Log.e(
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
                                is Network.Success -> {
                                    i.estado = "Enviado"
                                    repository.saveVisita(i)
                                    Log.d(_tag, "Visita enviado $i")
                                }
                                is Network.Error -> Log.e(_tag, "Visita Error ${it.message}")
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
                                is Network.Success -> {
                                    i.estado = "Enviado"
                                    repository.saveAlta(i)
                                    Log.d(_tag, "Alta enviado $i")
                                }
                                is Network.Error -> Log.e(_tag, "Alta Error ${it.message}")
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
                        p.put("tipodoc", i.documento)
                        p.put("giro", i.giro.split("-")[0].trim())
                        p.put("movil1", i.movil1)
                        p.put("movil2", i.movil2)
                        p.put("email", i.correo)
                        p.put("urbanizacion", "${i.zona} ${i.zonanombre}")
                        p.put("altura", i.numero)
                        p.put("distrito", i.distrito.split("-")[0].trim())
                        p.put("ruta", i.ruta)
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
                                is Network.Success -> {
                                    i.estado = "Enviado"
                                    repository.saveAltaDatos(i)
                                    Log.d(_tag, "Altadato enviado $i")
                                }
                                is Network.Error -> Log.e(_tag, "Altadato Error ${it.message}")
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
                                is Network.Success -> {
                                    i.estado = "Enviado"
                                    repository.saveBaja(i)
                                    Log.d(_tag, "Baja enviado $i")
                                }
                                is Network.Error -> Log.e(_tag, "Baja Error ${it.message}")
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
                                is Network.Success -> {
                                    i.estado = "Enviado"
                                    repository.saveBajaEstado(i)
                                    Log.d(_tag, "Bajaestado enviado $i")
                                }
                                is Network.Error -> Log.e(_tag, "Bajaestado Error ${it.message}")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun deleteTables() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.deleteClientes()
            repository.deleteEmpleados()
            repository.deleteDistritos()
            repository.deleteNegocios()
            repository.deleteRutas()
            repository.deleteEncuesta()
            repository.deleteEstado()
            repository.deleteSeguimiento()
            repository.deleteVisita()
            repository.deleteAlta()
            repository.deleteAltaDatos()
            repository.deleteBaja()
            repository.deleteBajaSuper()
            repository.deleteBajaEstado()
        }
    }
}