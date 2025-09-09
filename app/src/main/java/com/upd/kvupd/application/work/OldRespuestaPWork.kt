package com.upd.kvupd.application.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.data.model.TRespuesta
import com.upd.kvupd.domain.OldRepository
import com.upd.kvupd.utils.OldConstant.CONF
import com.upd.kvupd.utils.OldConstant.IPA
import com.upd.kvupd.utils.OldConstant.IP_AUX
import com.upd.kvupd.utils.OldConstant.IP_P
import com.upd.kvupd.utils.OldConstant.IP_S
import com.upd.kvupd.utils.OldConstant.OPTURL
import com.upd.kvupd.utils.OldHostSelectionInterceptor
import com.upd.kvupd.utils.toReqBody
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import org.json.JSONObject

@HiltWorker
class OldRespuestaPWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: OldRepository,
    private val host: OldHostSelectionInterceptor
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { OldRespuestaPWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            /*val item = repository.getServerRespuesta("Pendiente")
            if (item.isNotEmpty()) {
                item.forEach { i ->
                    val p = requestBody(i)
                    repository.setWebRespuestas(p).collect {
                        when (it) {
                            is OldNetworkRetrofit.Success -> {
                                i.estado = "Enviado"
                                repository.updateRespuesta(i)
                                Log.d(_tag, "Respuesta enviado $i")
                            }

                            is OldNetworkRetrofit.Error -> {
                                changeHostServer()
                                Log.e(_tag, "Respuesta Error ${it.message}")
                            }
                        }
                    }
                }
            }*/
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
        p.put("xcoord", j.longitud)
        p.put("ycoord", j.latitud)
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