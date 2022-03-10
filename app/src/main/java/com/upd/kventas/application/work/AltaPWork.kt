package com.upd.kventas.application.work

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.upd.kventas.data.model.TAlta
import com.upd.kventas.data.model.TVisita
import com.upd.kventas.domain.Functions
import com.upd.kventas.domain.Repository
import com.upd.kventas.utils.Constant
import com.upd.kventas.utils.Constant.CONF
import com.upd.kventas.utils.Network
import com.upd.kventas.utils.toReqBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import org.json.JSONObject

class AltaPWork @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { AltaPWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            val item = repository.getServerAlta("Pendiente")
            if (!item.isNullOrEmpty()) {
                item.forEach { i ->
                    val p = requestBody(i)
                    repository.setWebAlta(p).collect {
                        when(it) {
                            is Network.Success -> {
                                i.estado = "Enviado"
                                repository.saveAlta(i)
                                Log.d(_tag,"Alta enviado $i")
                            }
                            is Network.Error -> Log.e(_tag,"Alta Error ${it.message}")
                        }
                    }
                }
            }
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
}