package com.upd.kvupd.application.work

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.data.model.TSeguimiento
import com.upd.kvupd.domain.Repository
import com.upd.kvupd.utils.Constant
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Constant.IMEI
import com.upd.kvupd.utils.Constant.IPA
import com.upd.kvupd.utils.Constant.IP_AUX
import com.upd.kvupd.utils.Constant.IP_P
import com.upd.kvupd.utils.Constant.IP_S
import com.upd.kvupd.utils.Constant.OPTURL
import com.upd.kvupd.utils.Constant.isCONFinitialized
import com.upd.kvupd.utils.HostSelectionInterceptor
import com.upd.kvupd.utils.NetworkRetrofit
import com.upd.kvupd.utils.toReqBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import org.json.JSONObject

class SeguimientoPWork @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: Repository,
    private val host: HostSelectionInterceptor
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { SeguimientoPWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            if (isCONFinitialized() && CONF.seguimiento == 1) {
                val item = repository.getServerSeguimiento("Pendiente")
                if (!item.isNullOrEmpty()) {
                    item.forEach { i ->
                        val p = requestBody(i)
                        repository.setWebSeguimiento(p).collect {
                            when(it) {
                                is NetworkRetrofit.Success -> {
                                    i.estado = "Enviado"
                                    repository.saveSeguimiento(i)
                                    Log.d(_tag,"Seguimiento enviado $i")
                                }
                                is NetworkRetrofit.Error -> {
                                    changeHostServer()
                                    Log.e(_tag,"Seguimiento Error ${it.message}")
                                }
                            }
                        }
                    }
                }
            }
            return@withContext Result.success()
        }

    private fun requestBody(j: TSeguimiento): RequestBody {
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