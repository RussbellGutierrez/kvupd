package com.upd.kvupd.application.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.domain.Repository
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Constant.MSG_DISTRITO
import com.upd.kvupd.utils.Constant.W_DISTRITO
import com.upd.kvupd.utils.Interface.servworkListener
import com.upd.kvupd.utils.toReqBody
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
            val dist = repository.getDistritos()
            val req = requestBody()
            if (dist.isNullOrEmpty()) {
                try {
                    repository.getWebDistritos(req).collect { response ->
                        val rsp = response.data?.jobl
                        rst = if (rsp.isNullOrEmpty()) {
                            MSG_DISTRITO = "Respuesta: ${response.message}"
                            Result.success()
                        } else {
                            repository.saveDistritos(rsp)
                            MSG_DISTRITO = "Distritos descargados"
                            Result.success()
                        }
                    }
                } catch (e: HttpException) {
                    println(e.message())
                    MSG_DISTRITO = e.message()
                    rst = Result.retry()
                }
            } else {
                MSG_DISTRITO = "Full"
                rst = Result.success()
            }
            servworkListener?.onFinishWork(W_DISTRITO)
            return@withContext rst
        }

    private fun requestBody(): RequestBody {
        val json = JSONObject()
        json.put("empresa", CONF.empresa)
        return json.toReqBody()
    }

}