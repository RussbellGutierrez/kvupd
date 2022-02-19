package com.upd.kv.application.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kv.data.model.Config
import com.upd.kv.domain.Repository
import com.upd.kv.utils.Constant.MSG_DISTRITO
import com.upd.kv.utils.toReqBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException

class DistritosWork @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { DistritosWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            lateinit var rst: Result
            val conf = repository.getConfig()[0]
            val dist = repository.getDistritos()
            val req = requestBody(conf)
            if (dist.isNullOrEmpty()) {
                try {
                    repository.getWebDistritos(req).collect { response ->
                        val rsp = response.data?.data
                        rst = if (rsp.isNullOrEmpty()) {
                            MSG_DISTRITO = "Respuesta vacia"
                            Result.failure()
                        } else {
                            repository.saveDistrito(rsp)
                            MSG_DISTRITO = "Distritos descargados"
                            Result.success()
                        }
                    }
                } catch (e: HttpException) {
                    println(e.message())
                    rst = Result.retry()
                }
            } else {
                MSG_DISTRITO = "Full"
                rst = Result.success()
            }
            return@withContext rst
        }

    private fun requestBody(conf: Config): RequestBody {
        val json = JSONObject()
        json.put("empresa", conf.empresa)
        return json.toReqBody()
    }
}