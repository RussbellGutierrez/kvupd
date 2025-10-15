package com.upd.kvupd.application.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.domain.OldFunctions
import com.upd.kvupd.domain.OldRepository
import com.upd.kvupd.utils.OldHostSelectionInterceptor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class OldConfigWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: OldRepository,
    private val functions: OldFunctions,
    private val host: OldHostSelectionInterceptor
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { OldConfigWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            /*lateinit var rst: Result
            repository.getConfig().let { conf ->
                iterateLoop()
                if (conf == null) {
                    try {
                        repository.getWebConfiguracion(getRequestBody()).collect { response ->
                            val config = response.data?.jobl
                            Log.e(_tag, "Message: ${response.message}")
                            Log.w(_tag, "Config $config")
                            if (!response.message.isNullOrEmpty()) {
                                if (response.message.contains("Error", true)) {
                                    when (LOOP_CONFIG) {
                                        0 -> {
                                            LOOP_CONFIG = 1
                                            rst = Result.retry()
                                        }
                                        1 -> {
                                            LOOP_CONFIG = 2
                                            rst = Result.retry()
                                        }
                                        2 -> {
                                            LOOPING = false
                                            IS_CONFIG_FAILED = true
                                            MSG_CONFIG = "Respuesta-> ${response.message}"
                                            rst = Result.failure()
                                        }
                                    }
                                }
                            } else {
                                if (config.isNullOrEmpty()) {
                                    LOOPING = false
                                    IS_SUNDAY = functions.isSunday()
                                    IS_CONFIG_FAILED = true
                                    MSG_CONFIG = "Respuesta-> ${response.message}"
                                    rst = Result.failure()
                                } else {
                                    IS_SUNDAY = false
                                    IS_CONFIG_FAILED = false
                                    CONF = config[0].asTConfig()
                                    repository.saveConfiguracion(config)
                                    repository.saveSesion(config[0])
                                    repository.processAAux(CONF.codigo)
                                    MSG_CONFIG = "* Configuracion completa"
                                    rst = Result.success()
                                }
                            }
                        }
                    } catch (e: HttpException) {
                        println(e.message())
                        MSG_CONFIG = e.message()
                    }
                } else {
                    MSG_CONFIG = "* Full"
                    CONF = conf
                    rst = Result.success()
                }
            }
            interListener?.onFinishWork(W_CONFIG)*/
            return@withContext Result.success()//rst
        }

    /*private fun getRequestBody(): RequestBody {
        val app = functions.appSO()
        val modelo = "${Build.MANUFACTURER} ${Build.MODEL}"
        val json = JSONObject()
        json.put("imei", IMEI)
        json.put("modelo", modelo.uppercase())
        json.put("version", app)
        json.put("fecha", Calendar.getInstance().time.dateToday(6))
        return json.toReqBody()
    }

    private suspend fun iterateLoop() {
        /*repository.getSesion().let { sesion ->
            when (LOOP_CONFIG) {
                0 -> {
                    OPTURL = "aux"
                    IP_AUX = "http://$IPA/api/"
                }
                1 -> {
                    if (sesion != null) {
                        OPTURL = "ipp"
                        IP_P = "http://${sesion.ipp}/api/"
                    }
                }
                2 -> {
                    if (sesion != null) {
                        OPTURL = "ips"
                        IP_S = "http://${sesion.ips}/api/"
                    }
                }
            }
            host.setHostBaseUrl()
        }*/
    }*/
}