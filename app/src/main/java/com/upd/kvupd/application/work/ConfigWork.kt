package com.upd.kvupd.application.work

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.data.model.asTConfig
import com.upd.kvupd.domain.Functions
import com.upd.kvupd.domain.Repository
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Constant.IMEI
import com.upd.kvupd.utils.Constant.IPA
import com.upd.kvupd.utils.Constant.IP_AUX
import com.upd.kvupd.utils.Constant.IP_P
import com.upd.kvupd.utils.Constant.IP_S
import com.upd.kvupd.utils.Constant.IS_CONFIG_FAILED
import com.upd.kvupd.utils.Constant.IS_SUNDAY
import com.upd.kvupd.utils.Constant.LOOPING
import com.upd.kvupd.utils.Constant.LOOP_CONFIG
import com.upd.kvupd.utils.Constant.MSG_CONFIG
import com.upd.kvupd.utils.Constant.OPTURL
import com.upd.kvupd.utils.Constant.W_CONFIG
import com.upd.kvupd.utils.HostSelectionInterceptor
import com.upd.kvupd.utils.Interface.servworkListener
import com.upd.kvupd.utils.dateToday
import com.upd.kvupd.utils.toReqBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.util.*

class ConfigWork @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: Repository,
    private val functions: Functions,
    private val host: HostSelectionInterceptor
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { ConfigWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            lateinit var rst: Result
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
                                    MSG_CONFIG = "Configuracion completa"
                                    rst = Result.success()
                                }
                            }
                        }
                    } catch (e: HttpException) {
                        println(e.message())
                        MSG_CONFIG = e.message()
                    }
                } else {
                    MSG_CONFIG = "Full"
                    CONF = conf
                    rst = Result.success()
                }
            }
            servworkListener?.onFinishWork(W_CONFIG)
            return@withContext rst
        }

    private fun getRequestBody(): RequestBody {
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
        repository.getSesion().let { sesion ->
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
        }
    }
}