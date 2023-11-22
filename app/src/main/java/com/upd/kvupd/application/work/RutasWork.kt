package com.upd.kvupd.application.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.domain.Repository
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Constant.MSG_RUTA
import com.upd.kvupd.utils.Constant.W_RUTA
import com.upd.kvupd.utils.Interface.servworkListener
import com.upd.kvupd.utils.toReqBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException

class RutasWork @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { RutasWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            lateinit var rst: Result
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
                            MSG_RUTA = "Rutas descargadas"
                            Result.success()
                        }
                    }
                } catch (e: HttpException) {
                    println(e.message())
                    MSG_RUTA = e.message()
                    rst = Result.failure()
                }
            } else {
                MSG_RUTA = "Full"
                rst = Result.success()
            }
            servworkListener?.onFinishWork(W_RUTA)
            return@withContext rst
        }

    private fun requestBody(): RequestBody {
        val emp = if (CONF.tipo == "S") {
            0
        } else {
            CONF.codigo
        }
        val json = JSONObject()
        json.put("empleado", emp)
        json.put("empresa", CONF.empresa)
        return json.toReqBody()
    }

}