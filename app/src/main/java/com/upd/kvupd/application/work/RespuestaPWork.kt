package com.upd.kvupd.application.work

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.data.model.TRespuesta
import com.upd.kvupd.domain.Repository
import com.upd.kvupd.utils.Constant
import com.upd.kvupd.utils.Constant.CONF
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

class RespuestaPWork @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: Repository,
    private val host: HostSelectionInterceptor
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { RespuestaPWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            val item = repository.getServerRespuesta("Pendiente")
            if (!item.isNullOrEmpty()) {
                item.forEach { i ->
                    val p = requestBody(i)
                    repository.setWebRespuestas(p).collect {
                        when(it) {
                            is NetworkRetrofit.Success -> {
                                i.estado = "Enviado"
                                repository.saveRespuestaOneByOne(i)
                                Log.d(_tag,"Respuesta enviado $i")
                            }
                            is NetworkRetrofit.Error -> {
                                changeHostServer()
                                Log.e(_tag,"Respuesta Error ${it.message}")
                            }
                        }
                    }
                }
            }
            return@withContext Result.success()
        }

    private fun requestBody(j: TRespuesta): RequestBody {
        val p = JSONObject()
        p.put("empresa", CONF.empresa)
        p.put("empleado", CONF.codigo)
        p.put("cliente", j.cliente)
        p.put("encuesta", j.encuesta)
        p.put("pregunta", j.pregunta)
        p.put("respuesta", j.respuesta)
        p.put("fecha", j.fecha)
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