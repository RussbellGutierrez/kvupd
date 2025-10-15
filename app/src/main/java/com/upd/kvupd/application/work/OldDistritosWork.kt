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
class OldDistritosWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: OldRepository
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { OldDistritosWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            /*lateinit var rst: Result
            val dist = repository.getDistritos()
            val req = requestBody()
            if (dist.isEmpty()) {
                try {
                    repository.getWebDistritos(req).collect { response ->
                        val rsp = response.data?.jobl
                        rst = if (rsp.isNullOrEmpty()) {
                            MSG_DISTRITO = "Respuesta: ${response.message}"
                            Result.success()
                        } else {
                            repository.saveDistritos(rsp)
                            MSG_DISTRITO = "* Distritos descargados"
                            Result.success()
                        }
                    }
                } catch (e: HttpException) {
                    println(e.message())
                    MSG_DISTRITO = e.message()
                    rst = Result.retry()
                }
            } else {
                MSG_DISTRITO = "* Full"
                rst = Result.success()
            }
            interListener?.onFinishWork(W_DISTRITO)*/
            return@withContext Result.success()//rst
        }

    /*private fun requestBody(): RequestBody {
        val json = JSONObject()
        json.put("empresa", CONF.empresa)
        return json.toReqBody()
    }*/

}