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
class OldNegociosWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: OldRepository
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { OldNegociosWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            /*lateinit var rst: Result
            val neg = repository.getNegocios()
            val req = requestBody()
            if (neg.isEmpty()) {
                try {
                    repository.getWebNegocios(req).collect { response ->
                        val rsp = response.data?.jobl
                        rst = if (rsp.isNullOrEmpty()) {
                            MSG_NEGOCIO = "Respuesta: ${response.message}"
                            Result.success()
                        } else {
                            repository.saveNegocios(rsp)
                            MSG_NEGOCIO = "* Negocios descargados"
                            Result.success()
                        }
                    }
                } catch (e: HttpException) {
                    println(e.message())
                    MSG_NEGOCIO = e.message()
                    rst = Result.retry()
                }
            } else {
                MSG_NEGOCIO = "* Full"
                rst = Result.success()
            }
            interListener?.onFinishWork(W_NEGOCIO)*/
            return@withContext Result.success()//rst
        }

    /*private fun requestBody(): RequestBody {
        val json = JSONObject()
        json.put("empresa", CONF.empresa)
        return json.toReqBody()
    }*/

}