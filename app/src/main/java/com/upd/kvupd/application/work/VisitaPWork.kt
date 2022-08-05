package com.upd.kvupd.application.work

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.data.model.TVisita
import com.upd.kvupd.domain.Repository
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.NetworkRetrofit
import com.upd.kvupd.utils.toReqBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import org.json.JSONObject

class VisitaPWork @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { VisitaPWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            val item = repository.getServerVisita("Pendiente")
            if (!item.isNullOrEmpty()) {
                item.forEach { i ->
                    val p = requestBody(i)
                    repository.setWebVisita(p).collect {
                        when(it) {
                            is NetworkRetrofit.Success -> {
                                i.estado = "Enviado"
                                repository.saveVisita(i)
                                Log.d(_tag,"Visita enviado $i")
                            }
                            is NetworkRetrofit.Error -> Log.e(_tag,"Visita Error ${it.message}")
                        }
                    }
                }
            }
            return@withContext Result.success()
        }

    private fun requestBody(j: TVisita): RequestBody {
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
        return p.toReqBody()
    }
}