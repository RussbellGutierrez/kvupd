package com.upd.kventas.application.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kventas.domain.Repository
import com.upd.kventas.utils.Constant.CONF
import com.upd.kventas.utils.Constant.MSG_NEGOCIO
import com.upd.kventas.utils.Constant.W_NEGOCIO
import com.upd.kventas.utils.Interface.workListener
import com.upd.kventas.utils.toReqBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException

class NegociosWork @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { NegociosWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            lateinit var rst: Result
            val neg = repository.getNegocios()
            val req = requestBody()
            if (neg.isNullOrEmpty()) {
                try {
                    repository.getWebNegocios(req).collect { response ->
                        val rsp = response.data?.jobl
                        rst = if (rsp.isNullOrEmpty()) {
                            MSG_NEGOCIO = "Respuesta: ${response.message}"
                            Result.success()
                        } else {
                            repository.saveNegocios(rsp)
                            MSG_NEGOCIO = "Negocios descargados"
                            Result.success()
                        }
                    }
                } catch (e: HttpException) {
                    println(e.message())
                    MSG_NEGOCIO = e.message()
                    rst = Result.retry()
                }
            } else {
                MSG_NEGOCIO = "Full"
                rst = Result.success()
            }
            workListener?.onFinishWork(W_NEGOCIO)
            return@withContext rst
        }

    private fun requestBody(): RequestBody {
        val json = JSONObject()
        json.put("empresa", CONF.empresa)
        return json.toReqBody()
    }
}