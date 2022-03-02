package com.upd.kventas.service

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.BatteryManager
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.gms.location.*
import com.upd.kventas.application.work.HelperNotification
import com.upd.kventas.data.model.TSeguimiento
import com.upd.kventas.di.LocationRequestGps
import com.upd.kventas.di.LocationSettingsRequestGps
import com.upd.kventas.domain.Functions
import com.upd.kventas.domain.Repository
import com.upd.kventas.utils.Constant.CONF
import com.upd.kventas.utils.Constant.FIRST_LOCATION
import com.upd.kventas.utils.Constant.SETUP_NOTIF
import com.upd.kventas.utils.Constant.W_CONFIG
import com.upd.kventas.utils.Constant.W_DISTRITO
import com.upd.kventas.utils.Constant.W_NEGOCIO
import com.upd.kventas.utils.Constant.W_SETUP
import com.upd.kventas.utils.Constant.W_USER
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ServiceSetup : LifecycleService(), LocationListener {

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var functions: Functions

    @Inject
    lateinit var repository: Repository

    @Inject
    lateinit var helper: HelperNotification

    @LocationRequestGps
    @Inject
    lateinit var locationRequest: LocationRequest

    @LocationSettingsRequestGps
    @Inject
    lateinit var locationSettingsRequest: LocationSettingsRequest

    private val _tag by lazy { ServiceSetup::class.java.simpleName }
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val callback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            onLocationChanged(p0.lastLocation)
        }
    }
    private var batteryStatus: Intent? = null

    override fun onDestroy() {
        Log.d(_tag, "Service setup destroyed")
        if (::fusedLocationProviderClient.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(callback)
        }
        batteryStatus = null
        super.onDestroy()
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(_tag, "Service setup launch")

        batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            this.registerReceiver(null, ifilter)
        }

        notificationLaunch()

        workManager.getWorkInfosByTagLiveData(W_CONFIG).observe(this, workInfoObserver())
        workManager.getWorkInfosByTagLiveData(W_SETUP).observe(this, workInfoObserver())
        workManager.getWorkInfosByTagLiveData(W_USER).observe(this, workInfoObserver())
        workManager.getWorkInfosByTagLiveData(W_DISTRITO).observe(this, workInfoObserver())
        workManager.getWorkInfosByTagLiveData(W_NEGOCIO).observe(this, workInfoObserver())

        repository.getFlowConfig().asLiveData().observe(this) { result ->
            if (result.isNotEmpty()) {
                startLocation()
                helper.userNotifLaunch()
                helper.distritoNotifLaunch()
                helper.negocioNotifLaunch()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(_tag, "Service startcommand")
        functions.launchWorkers()
        return START_STICKY
    }

    private fun notificationLaunch() {
        val not = helper.setupNotif()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> startForeground(SETUP_NOTIF, not)
            Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> NotificationManagerCompat.from(this)
                .notify(SETUP_NOTIF, not)
        }
    }

    private fun workInfoObserver(): Observer<List<WorkInfo>> {
        return Observer { list ->
            if (list.isNullOrEmpty()) {
                return@Observer
            }

            list.forEach { wi ->
                if (wi.tags.contains(W_SETUP)) {
                    when (wi.state) {
                        WorkInfo.State.SUCCEEDED -> Log.d(_tag, "SetupW succees")
                        WorkInfo.State.FAILED -> Log.d(_tag, "SetupW fail")
                        else -> {}
                    }
                }
                when {
                    wi.tags.contains(W_CONFIG) -> {
                        helper.configNotif()
                        if (wi.state == WorkInfo.State.SUCCEEDED) {
                            setupWorker()
                        }
                    }
                    wi.tags.contains(W_USER) -> helper.userNotif()
                    wi.tags.contains(W_DISTRITO) -> helper.distritoNotif()
                    wi.tags.contains(W_NEGOCIO) -> helper.negocioNotif()
                }
            }
        }
    }

    private fun setupWorker() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getStarterTime()?.let {
                functions.workerSetup(it)
            }
        }
    }

    override fun onLocationChanged(p0: Location) {
        Log.d(_tag, "Location ${p0.longitude} / ${p0.latitude} / ${p0.accuracy}")
        FIRST_LOCATION = p0
        if (p0.accuracy <= 50f) {
            saveLocation(p0)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocation() {
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

    private fun saveLocation(location: Location) {
        CoroutineScope(Dispatchers.IO).launch {
            val porcentaje = batteryStatus?.let { intent ->
                val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                level * 100 / scale.toFloat()
            } ?: 0.0

            val fecha = functions.dateToday(4)
            val item = TSeguimiento(
                fecha,
                CONF.codigo,
                location.longitude,
                location.latitude,
                location.accuracy.toDouble(),
                porcentaje.toDouble(),
                "Pendiente"
            )
            repository.saveSeguimiento(item)
        }
    }
}