package com.upd.kventas.application.work

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.upd.kventas.data.model.TADatos
import com.upd.kventas.data.model.TBaja
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

class BajaPWork @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { BajaPWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            val item = repository.getServerBaja("Pendiente")
            if (!item.isNullOrEmpty()) {
                item.forEach { i ->
                    val p = requestBody(i)
                    repository.setWebBaja(p).collect {
                        when(it) {
                            is Network.Success -> {
                                i.estado = "Enviado"
                                repository.saveBaja(i)
                                Log.d(_tag,"Baja enviado $i")
                            }
                            is Network.Error -> Log.e(_tag,"Baja Error ${it.message}")
                        }
                    }
                }
            }
            return@withContext Result.success()
        }

    private fun requestBody(j: TBaja): RequestBody {
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
        return p.toReqBody()
    }
}