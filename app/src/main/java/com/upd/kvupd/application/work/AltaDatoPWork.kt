package com.upd.kvupd.application.work

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.data.model.TADatos
import com.upd.kvupd.domain.Repository
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Constant.IMEI
import com.upd.kvupd.utils.Constant.IPA
import com.upd.kvupd.utils.Constant.IP_AUX
import com.upd.kvupd.utils.Constant.IP_P
import com.upd.kvupd.utils.Constant.IP_S
import com.upd.kvupd.utils.Constant.OPTURL
import com.upd.kvupd.utils.HostSelectionInterceptor
import com.upd.kvupd.utils.NetworkRetrofit
import com.upd.kvupd.utils.toReqBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import org.json.JSONObject

class AltaDatoPWork @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: Repository,
    private val host: HostSelectionInterceptor
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { AltaDatoPWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            val item = repository.getServerAltadatos("Pendiente")
            if (item.isNotEmpty()) {
                item.forEach { i ->
                    val p = requestBody(i)
                    repository.setWebAltaDatos(p).collect {
                        when (it) {
                            is NetworkRetrofit.Success -> {
                                i.estado = "Enviado"
                                repository.saveAltaDatos(i)
                                Log.d(_tag, "Altadato enviado $i")
                            }
                            is NetworkRetrofit.Error -> {
                                changeHostServer()
                                Log.e(_tag, "Altadato Error ${it.message}")
                            }
                        }
                    }
                }
            }
            return@withContext Result.success()
        }

    private fun requestBody(j: TADatos): RequestBody {
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

        when (j.manzana) {
            "" -> p.put("calle", "${j.via} ${j.direccion} ${j.ubicacion}")
            else -> p.put(
                "calle",
                "${j.via} ${j.direccion} MZ ${j.manzana} ${j.ubicacion}"
            )
        }
        return p.toReqBody()
    }

    private suspend fun changeHostServer() {
        repository.getSesion().let { sesion ->
            when (OPTURL) {
                "aux" -> {
                    OPTURL = "ipp"
                    IP_P = "http://${sesion!!.ipp}/api/"
                }
                "ipp" -> {
                    OPTURL = "ips"
                    IP_S = "http://${sesion!!.ips}/api/"
                }
                "ips" -> {
                    OPTURL = "aux"
                    IP_AUX = "http://$IPA/api/"
                }
            }
            host.setHostBaseUrl()
        }
    }
}