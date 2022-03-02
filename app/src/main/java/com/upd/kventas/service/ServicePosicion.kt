package com.upd.kventas.service

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LifecycleService
import com.google.android.gms.location.*
import com.upd.kventas.di.LocationRequestPosition
import com.upd.kventas.di.LocationSettingsRequestPosition
import com.upd.kventas.utils.Constant.POS_LOC
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ServicePosicion: LifecycleService(), LocationListener {

    @LocationRequestPosition
    @Inject
    lateinit var locationRequest: LocationRequest

    @LocationSettingsRequestPosition
    @Inject
    lateinit var locationSettingsRequest: LocationSettingsRequest

    private val _tag by lazy { ServicePosicion::class.java.simpleName }
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val callback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            onLocationChanged(p0.lastLocation)
        }
    }

    override fun onDestroy() {
        Log.d(_tag, "Service posicion destroyed")
        if (::fusedLocationProviderClient.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(callback)
        }
        super.onDestroy()
    }

    override fun onCreate() {
        super.onCreate()
        startPosition()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }

    override fun onLocationChanged(p0: Location) {
        Log.d(_tag, "Position ${p0.longitude} / ${p0.latitude} / ${p0.accuracy}")
        POS_LOC = p0
    }

    @SuppressLint("MissingPermission")
    private fun startPosition() {
        val settingClient = LocationServices.getSettingsClient(this)
        settingClient.checkLocationSettings(locationSettingsRequest)
        if (!::fusedLocationProviderClient.isInitialized) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        }

        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                callback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}