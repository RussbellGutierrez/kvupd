package com.upd.kventas.application.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kventas.domain.Functions
import com.upd.kventas.domain.Repository
import com.upd.kventas.utils.Constant.CONF
import com.upd.kventas.utils.Constant.CONFIG_CHANNEL
import com.upd.kventas.utils.Constant.CONFIG_NOTIF
import com.upd.kventas.utils.Constant.IMEI
import com.upd.kventas.utils.Constant.MSG_CONFIG
import com.upd.kventas.utils.Constant.CONFIG_RENEW
import com.upd.kventas.utils.toReqBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException

class ConfigWork @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: Repository,
    private val functions: Functions
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { ConfigWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            lateinit var rst: Result
            val conf = repository.getConfig()
            if (CONFIG_RENEW) {
                try {
                    configNotif()
                    repository.getWebConfiguracion(getRequestBody()).collect { response ->
                        val config = response.data?.jobl
                        rst = if (config.isNullOrEmpty()) {
                            MSG_CONFIG = "Respuesta: ${response.message}"
                            Result.failure()
                        } else {
                            CONF = config[0]
                            repository.saveConfiguracion(config)
                            MSG_CONFIG = "Configuracion completa"
                            Result.success()
                        }
                    }
                    CONFIG_RENEW = false
                } catch (e: HttpException) {
                    println(e.message())
                    rst = Result.retry()
                }
            }else {
                if (conf.isEmpty()) {
                    try {
                        configNotif()
                        repository.getWebConfiguracion(getRequestBody()).collect { response ->
                            val config = response.data?.jobl
                            rst = if (config.isNullOrEmpty()) {
                                MSG_CONFIG = "Respuesta: ${response.message}"
                                Result.failure()
                            } else {
                                CONF = config[0]
                                repository.saveConfiguracion(config)
                                MSG_CONFIG = "Configuracion completa"
                                Result.success()
                            }
                        }
                    } catch (e: HttpException) {
                        println(e.message())
                        rst = Result.retry()
                    }
                } else {
                    MSG_CONFIG = "Full"
                    CONF = conf[0]
                    rst = Result.success()
                }
            }
            return@withContext rst
        }

    private fun getRequestBody(): RequestBody {
        val app = functions.appSO()
        val json = JSONObject()
        json.put("imei", IMEI)
        json.put("version", app)
        json.put("fecha", functions.dateToday(6))
        return json.toReqBody()
    }

    private fun configNotif() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CONFIG_CHANNEL,
                "Configuracion",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Notificacion para configuracion"
            val notificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, CONFIG_CHANNEL)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Download")
            .setContentText("Configuracion para el equipo")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setProgress(0, 0, true)
            .setOngoing(true)
        val manager = NotificationManagerCompat.from(applicationContext)
        manager.notify(CONFIG_NOTIF, builder.build())
    }
}