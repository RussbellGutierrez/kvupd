package com.upd.kvupd.application.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.domain.OldRepository
import com.upd.kvupd.utils.OldHostSelectionInterceptor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class OldBajaPWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: OldRepository,
    private val host: OldHostSelectionInterceptor
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { OldBajaPWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            /*val item = repository.getServerBaja("Pendiente")
            if (item.isNotEmpty()) {
                item.forEach { i ->
                    val p = requestBody(i)
                    repository.setWebBaja(p).collect {
                        when(it) {
                            is OldNetworkRetrofit.Success -> {
                                i.estado = "Enviado"
                                repository.updateBaja(i)
                                Log.d(_tag,"Baja enviado $i")
                            }
                            is OldNetworkRetrofit.Error -> {
                                changeHostServer()
                                Log.e(_tag,"Baja Error ${it.message}")
                            }
                            else -> {}
                        }
                    }
                }
            }*/
            return@withContext Result.success()
        }

    /*private fun requestBody(j: TBaja): RequestBody {
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
            p.put("estado",2)
        }
        return p.toReqBody()
    }*/

    /*private suspend fun changeHostServer() {
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
    }*/
}