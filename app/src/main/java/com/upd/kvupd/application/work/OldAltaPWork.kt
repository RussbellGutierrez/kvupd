package com.upd.kvupd.application.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.data.model.TAlta
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
class OldAltaPWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: OldRepository,
    private val host: OldHostSelectionInterceptor
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { OldAltaPWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            /*val item = repository.getServerAlta("Pendiente")
            if (item.isNotEmpty()) {
                item.forEach { i ->
                    val p = requestBody(i)
                    repository.setWebAlta(p).collect {
                        when(it) {
                            is OldNetworkRetrofit.Success -> {
                                i.estado = "Enviado"
                                repository.updateAlta(i)
                                Log.d(_tag,"Alta enviado $i")
                            }
                            is OldNetworkRetrofit.Error -> {
                                changeHostServer()
                                Log.e(_tag,"Alta Error ${it.message}")
                            }
                        }
                    }
                }
            }*/
            return@withContext Result.success()
        }

    private fun requestBody(j: TAlta): RequestBody {
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