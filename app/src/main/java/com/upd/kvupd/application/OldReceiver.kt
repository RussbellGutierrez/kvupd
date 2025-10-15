package com.upd.kvupd.application

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.upd.kvupd.domain.OldFunctions
import com.upd.kvupd.domain.OldRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OldReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: OldRepository

    @Inject
    lateinit var functions: OldFunctions

    override fun onReceive(p0: Context, p1: Intent) {
        /*when (p1.action) {
            Intent.ACTION_BOOT_COMPLETED -> functions.executeService("setup", true)
            Intent.ACTION_REBOOT -> functions.executeService("setup", true)
            Intent.ACTION_PACKAGE_RESTARTED -> functions.executeService("setup", true)
            Intent.ACTION_MY_PACKAGE_REPLACED -> functions.executeService("setup", true)
            Intent.ACTION_MY_PACKAGE_SUSPENDED -> functions.executeService("setup", true)
            Intent.ACTION_TIME_CHANGED -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val item = functions.saveSystemActions("TIME", null)
                    if (item != null) {
                        //repository.saveIncidencia(item)
                    }
                }
            }

            Intent.ACTION_BATTERY_CHANGED -> {
                val level: Int = p1.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale: Int = p1.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                BATTERY_PCT = if (level != 0 && scale != 0) {
                    level * 100 / scale.toFloat()
                } else {
                    0f
                }
            }

            LocationManager.PROVIDERS_CHANGED_ACTION -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val item = functions.saveSystemActions("GPS", null)
                    if (item != null) {
                        //repository.saveIncidencia(item)
                    }
                    functions.checkGPSEnabled()
                }
            }

            ACTION_NOTIFICATION_DISMISSED -> {
                val notificationId = p1.getIntExtra(DISMISS_NAME, -1)
                if (notificationId == DISMISS_ID) {
                    interListener?.showNotificationSystem(0)
                }
            }

            ACTION_NOTIFICATION_SLEEPED -> {
                val notificationId = p1.getIntExtra(SLEEP_NAME, -1)
                if (notificationId == SLEEP_ID) {
                    interListener?.showNotificationSystem(1)
                }
            }

            ACTION_ALARM_SETUP -> {
                Log.d("Receiver", "Setup alarm launch")
                interListener?.launchAgainProcess()
            }

            ACTION_ALARM_FINISH -> {
                Log.d("Receiver", "Finish alarm launch")
                functions.executeService("finish", false)
            }
        }*/
    }
}