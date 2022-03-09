package com.upd.kventas.application.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kventas.data.model.Config
import com.upd.kventas.domain.Repository
import com.upd.kventas.utils.Constant.MSG_ENCUESTA
import com.upd.kventas.utils.Constant.MSG_NEGOCIO
import com.upd.kventas.utils.toReqBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException

class EncuestaWork @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { EncuestaWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            lateinit var rst: Result
            val conf = repository.getConfig()[0]
            val enc = repository.getEncuestas()
            val req = requestBody(conf)
            if (enc.isNullOrEmpty()) {
                try {
                    repository.getWebEncuesta(req).collect { response ->
                        val rsp = response.data?.jobl
                        rst = if (rsp.isNullOrEmpty()) {
                            MSG_ENCUESTA = "Respuesta: ${response.message}"
                            Result.failure()
                        } else {
                            repository.saveEncuesta(rsp)
                            MSG_ENCUESTA = "Encuestas descargadas"
                            Result.success()
                        }
                    }
                } catch (e: HttpException) {
                    println(e.message())
                    rst = Result.retry()
                }
            } else {
                MSG_ENCUESTA = "Full"
                rst = Result.success()
            }
            return@withContext rst
        }

    private fun requestBody(conf: Config): RequestBody {
        val json = JSONObject()
        json.put("empleado", conf.codigo)
        json.put("empresa", conf.empresa)
        return json.toReqBody()
    }
}