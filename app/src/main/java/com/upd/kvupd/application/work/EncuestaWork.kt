package com.upd.kvupd.application.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.data.model.TEncuestaSeleccionado
import com.upd.kvupd.domain.Repository
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Constant.MSG_ENCUESTA
import com.upd.kvupd.utils.Constant.W_ENCUESTA
import com.upd.kvupd.utils.Interface.servworkListener
import com.upd.kvupd.utils.toReqBody
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
                            MSG_ENCUESTA = "Encuestas descargadas"
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
                MSG_ENCUESTA = "Full"
                rst = Result.success()
            }
            servworkListener?.onFinishWork(W_ENCUESTA)
            return@withContext rst
        }

    private fun requestBody(): RequestBody {
        val json = JSONObject()
        json.put("empleado", CONF.codigo)
        json.put("empresa", CONF.empresa)
        return json.toReqBody()
    }

}