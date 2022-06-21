package com.upd.kvupd.application.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.data.model.asTConfig
import com.upd.kvupd.domain.Functions
import com.upd.kvupd.domain.Repository
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Constant.CONFIG_CHANNEL
import com.upd.kvupd.utils.Constant.CONFIG_NOTIF
import com.upd.kvupd.utils.Constant.IMEI
import com.upd.kvupd.utils.Constant.IS_CONFIG_FAILED
import com.upd.kvupd.utils.Constant.IS_SUNDAY
import com.upd.kvupd.utils.Constant.MSG_CONFIG
import com.upd.kvupd.utils.Constant.W_CONFIG
import com.upd.kvupd.utils.Interface.workListener
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
    private val functions: Functions
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { ConfigWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            lateinit var rst: Result
            configNotif()
            repository.getConfig().let {
                if (it == null) {
                    try {
                        repository.getWebConfiguracion(getRequestBody()).collect { response ->
                            val config = response.data?.jobl
                            Log.e(_tag,"Message: ${response.message}")
                            Log.w(_tag,"Config $config")
                            rst = if (config.isNullOrEmpty()) {
                                IS_SUNDAY = functions.isSunday()
                                IS_CONFIG_FAILED = true
                                MSG_CONFIG = "Respuesta-> ${response.message}"
                                Result.failure()
                            } else {
                                IS_SUNDAY = false
                                IS_CONFIG_FAILED = false
                                CONF = config[0].asTConfig()
                                repository.saveConfiguracion(config)
                                repository.saveSesion(config[0])
                                MSG_CONFIG = "Configuracion completa"
                                Result.success()
                            }
                        }
                    } catch (e: HttpException) {
                        println(e.message())
                        MSG_CONFIG = e.message()
                        rst = Result.retry()
                    }
                }else {
                    MSG_CONFIG = "Full"
                    CONF = it
                    rst = Result.success()
                }
            }
            workListener?.onFinishWork(W_CONFIG)
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
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setProgress(0, 0, true)
            .setOngoing(true)
        val manager = NotificationManagerCompat.from(applicationContext)
        manager.notify(CONFIG_NOTIF, builder.build())
    }
}