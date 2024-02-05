package com.upd.kvupd.service

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import com.google.android.gms.location.LocationServices
import com.upd.kvupd.domain.LocationClient
import com.upd.kvupd.utils.CaptureLocation
import com.upd.kvupd.utils.Constant.POSITION_F_INTERVAL
import com.upd.kvupd.utils.Constant.POSITION_METERS
import com.upd.kvupd.utils.Constant.POSITION_N_INTERVAL
import com.upd.kvupd.utils.Constant.POS_LOC
import com.upd.kvupd.utils.Constant.isPOSLOCinitialized
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ServicePosicion : LifecycleService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private val _tag by lazy { ServicePosicion::class.java.simpleName }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(_tag, "Service posicion destroyed")
        if (isPOSLOCinitialized()) {
            POS_LOC.longitude = 0.0
            POS_LOC.latitude = 0.0
        }
        serviceScope.cancel()
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = CaptureLocation(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        launchLocation()
        return START_NOT_STICKY
    }

    private fun launchLocation() {
        locationClient
            .getLocationUpdates(POSITION_N_INTERVAL, POSITION_F_INTERVAL, POSITION_METERS)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                Log.d(
                    _tag,
                    "Position ${location.longitude} / ${location.latitude} / ${location.accuracy}"
                )
                POS_LOC = location
            }
            .launchIn(serviceScope)
    }
}