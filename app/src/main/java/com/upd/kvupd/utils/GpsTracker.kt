package com.upd.kvupd.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.upd.kvupd.ui.sealed.GpsError
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class GpsTracker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cliente: FusedLocationProviderClient
) {
    private val trackers = mutableMapOf<String, CoroutineScope>()
    private val onLocationMap = mutableMapOf<String, (Location) -> Unit>()
    private val onErrorMap = mutableMapOf<String, (GpsError) -> Unit>()

    // Iniciamos con valores por defecto
    fun startTracking(
        id: String,
        interval: Long = ConfiguracionFrecuenciaCoordenadas.TRACKER_INTERVALO_NORMAL,
        fastest: Long = ConfiguracionFrecuenciaCoordenadas.TRACKER_INTERVALO_RAPIDO,
        minDistance: Float = ConfiguracionFrecuenciaCoordenadas.FRECUENCIA_METROS,
        onLocation: (Location) -> Unit,
        onError: (GpsError) -> Unit = {}
    ) {
        stopTracking(id)

        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        trackers[id] = scope
        onLocationMap[id] = onLocation
        onErrorMap[id] = onError

        scope.launch {
            try {
                getLocationFlow(interval, fastest, minDistance).collect {
                    onLocation(it)
                }
            } catch (e: Throwable) {
                val error = if (e is GpsError) e else GpsError.ErrorDesconocido(e)
                onError(error)
            }
        }
    }

    fun stopTracking(id: String) {
        trackers[id]?.cancel()
        trackers.remove(id)
        onLocationMap.remove(id)
        onErrorMap.remove(id)
    }

    fun stopAllTracking() {
        trackers.values.forEach { it.cancel() }
        trackers.clear()
        onLocationMap.clear()
        onErrorMap.clear()
    }

    // Al momento de actualizar, llamamos esta funcion
    fun updateTrackingConfig(id: String, modoExtenso: Boolean) {
        val onLocation = onLocationMap[id] ?: return
        val onError = onErrorMap[id] ?: {}

        if (modoExtenso) {
            startTracking(
                id = id,
                interval = ConfiguracionFrecuenciaCoordenadas.TRACKER_LAPSO_EXTENSO,
                fastest = ConfiguracionFrecuenciaCoordenadas.TRACKER_LAPSO_EXTENSO,
                minDistance = ConfiguracionFrecuenciaCoordenadas.FRECUENCIA_METROS,
                onLocation = onLocation,
                onError = onError
            )
        } else {
            startTracking(
                id = id,
                onLocation = onLocation,
                onError = onError
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocationFlow(
        interval: Long,
        fastest: Long,
        minDistance: Float
    ): Flow<Location> = callbackFlow {
        if (!context.hasLocationPermission()) {
            throw GpsError.PermisosDenegados
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGPSEnabled && !isNetworkEnabled) {
            throw GpsError.GpsDesactivado
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, interval).apply {
            setMinUpdateIntervalMillis(fastest)
            setMinUpdateDistanceMeters(minDistance)
        }.build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.lastOrNull()?.let { trySend(it) }
            }
        }

        cliente.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )

        awaitClose {
            cliente.removeLocationUpdates(locationCallback)
        }
    }
}