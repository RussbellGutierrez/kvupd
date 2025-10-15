package com.upd.kvupd.application.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.domain.OldRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class OldRutasWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: OldRepository
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { OldRutasWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            /*lateinit var rst: Result
            val rut = repository.getRutas()
            val req = requestBody()
            if (rut.isEmpty()) {
                try {
                    repository.getWebRutas(req).collect { response ->
                        val rsp = response.data?.jobl
                        rst = if (rsp.isNullOrEmpty()) {
                            MSG_RUTA = "Respuesta: ${response.message}"
                            Result.success()
                        } else {
                            repository.saveRutas(rsp)
                            MSG_RUTA = "* Rutas descargadas"
                            Result.success()
                        }
                    }
                } catch (e: HttpException) {
                    println(e.message())
                    MSG_RUTA = e.message()
                    rst = Result.failure()
                }
            } else {
                MSG_RUTA = "* Full"
                rst = Result.success()
            }
            interListener?.onFinishWork(W_RUTA)*/
            return@withContext Result.success()//rst
        }

    /*private fun requestBody(): RequestBody {
        val emp = if (CONF.tipo == "S") {
            0
        } else {
            CONF.codigo
        }
        val json = JSONObject()
        json.put("empleado", emp)
        json.put("empresa", CONF.empresa)
        return json.toReqBody()
    }*/

}