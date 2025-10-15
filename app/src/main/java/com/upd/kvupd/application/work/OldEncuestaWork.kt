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
class OldEncuestaWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: OldRepository
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { OldEncuestaWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            /*lateinit var rst: Result
            val enc = repository.getListEncuestas()
            val req = requestBody()
            if (enc.isEmpty()) {
                try {
                    repository.getWebEncuesta(req).collect { response ->
                        val rsp = response.data?.jobl
                        rst = if (rsp.isNullOrEmpty()) {
                            MSG_ENCUESTA = "Respuesta: ${response.message}"
                            Result.success()
                        } else {
                            repository.saveEncuesta(rsp)
                            MSG_ENCUESTA = "* Encuestas descargadas"
                            if (CONF.tipo == "V") {
                                val item = TEncuestaSeleccionado(1,rsp[0].id,rsp[0].foto)
                                repository.saveSeleccionado(item)
                            }
                            Result.success()
                        }
                    }
                } catch (e: HttpException) {
                    println(e.message())
                    MSG_ENCUESTA = e.message()
                    rst = Result.retry()
                }
            } else {
                MSG_ENCUESTA = "* Full"
                rst = Result.success()
            }
            interListener?.onFinishWork(W_ENCUESTA)*/
            return@withContext Result.success()//rst
        }

    /*private fun requestBody(): RequestBody {
        val json = JSONObject()
        json.put("empleado", CONF.codigo)
        json.put("empresa", CONF.empresa)
        return json.toReqBody()
    }*/

}