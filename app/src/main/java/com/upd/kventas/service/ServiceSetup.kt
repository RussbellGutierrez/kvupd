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
import com.upd.kventas.utils.Constant.IMEI
import com.upd.kventas.utils.Constant.CONFIG_RENEW
import com.upd.kventas.utils.Constant.IN_HOURS
import com.upd.kventas.utils.Constant.SETUP_NOTIF
import com.upd.kventas.utils.Constant.W_CONFIG
import com.upd.kventas.utils.Constant.W_DISTRITO
import com.upd.kventas.utils.Constant.W_ENCUESTA
import com.upd.kventas.utils.Constant.W_FINISH
import com.upd.kventas.utils.Constant.W_NEGOCIO
import com.upd.kventas.utils.Constant.W_RUTA
import com.upd.kventas.utils.Constant.W_SETUP
import com.upd.kventas.utils.Constant.W_USER
import com.upd.kventas.utils.Interface.serviceListener
import com.upd.kventas.utils.isServiceRunning
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

    private var user = false
    private var distrito = false
    private var negocio = false
    private var ruta = false
    private var encuesta = false
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

        IMEI = functions.parseQRtoIMEI(true)

        workManager.getWorkInfosByTagLiveData(W_CONFIG).observe(this, workInfoObserver())
        workManager.getWorkInfosByTagLiveData(W_SETUP).observe(this, workInfoObserver())
        workManager.getWorkInfosByTagLiveData(W_FINISH).observe(this, workInfoObserver())
        workManager.getWorkInfosByTagLiveData(W_USER).observe(this, workInfoObserver())
        workManager.getWorkInfosByTagLiveData(W_DISTRITO).observe(this, workInfoObserver())
        workManager.getWorkInfosByTagLiveData(W_NEGOCIO).observe(this, workInfoObserver())
        workManager.getWorkInfosByTagLiveData(W_RUTA).observe(this, workInfoObserver())
        workManager.getWorkInfosByTagLiveData(W_ENCUESTA).observe(this, workInfoObserver())

        repository.getFlowConfig().asLiveData().observe(this) { result ->
            if (!result.isNullOrEmpty() && IN_HOURS) {
                startLocation()
                helper.userNotifLaunch()
                helper.distritoNotifLaunch()
                helper.negocioNotifLaunch()
                helper.rutaNotifLaunch()
                helper.encuestaNotifLaunch()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(_tag, "Service startcommand")
        serviceNotification()
        verifyData {
            functions.launchWorkers()
        }
        return START_STICKY
    }

    private fun serviceNotification() {
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
                if (wi.tags.contains(W_FINISH)) {
                    when (wi.state) {
                        WorkInfo.State.SUCCEEDED -> Log.d(_tag, "FinishW succees")
                        WorkInfo.State.FAILED -> Log.d(_tag, "FinishW fail")
                        else -> {}
                    }
                }
                if (wi.tags.contains(W_CONFIG)) {
                    when (wi.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            helper.configNotif()
                            priorityWorkers()
                        }
                        WorkInfo.State.FAILED -> checkConfiguration()
                        else -> {}
                    }
                }

                if (wi.state.isFinished) {
                    when {
                        wi.tags.contains(W_USER) -> {
                            user = true
                            helper.userNotif()
                            periodicWorkers()
                        }
                        wi.tags.contains(W_DISTRITO) -> {
                            distrito = true
                            helper.distritoNotif()
                            periodicWorkers()
                        }
                        wi.tags.contains(W_NEGOCIO) -> {
                            negocio = true
                            helper.negocioNotif()
                            periodicWorkers()
                        }
                        wi.tags.contains(W_RUTA) -> {
                            ruta = true
                            helper.rutaNotif()
                            periodicWorkers()
                        }
                        wi.tags.contains(W_ENCUESTA) -> {
                            encuesta = true
                            helper.encuestaNotif()
                            periodicWorkers()
                        }
                    }
                }
            }
        }
    }

    private fun priorityWorkers() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.getStarterTime().let {
                functions.workerSetup(it)
            }
            repository.getFinishTime().let {
                functions.workerFinish(it)
            }
        }
    }

    private fun periodicWorkers() {
        if (user && distrito && negocio && ruta && encuesta) {
            functions.workerperSeguimiento()
            functions.workerperVisita()
            functions.workerperAlta()
            functions.workerperAltaEstado()
            functions.workerperBaja()
            functions.workerperBajaEstado()
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

    private fun checkConfiguration() {
        CoroutineScope(Dispatchers.IO).launch {
            val conf = repository.getConfig()
            if (conf.isNullOrEmpty()) {
                if (serviceListener != null) {
                    serviceListener?.onClosingActivity()
                } else {
                    if (isServiceRunning(ServicePosicion::class.java))
                        stopService(Intent(this@ServiceSetup, ServicePosicion::class.java))

                    if (isServiceRunning(ServiceFinish::class.java))
                        stopService(Intent(this@ServiceSetup, ServiceFinish::class.java))

                    stopSelf()
                }
            } else {
                repository.getStarterTime().let {
                    functions.workerSetup(it)
                }
            }
        }
    }

    private fun verifyData(T: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val fecha = functions.dateToday(5)
            val today = repository.isDataToday(fecha)
            if (today) {
                T()
            } else {
                CONFIG_RENEW = true
                repository.deleteClientes()
                repository.deleteEmpleados()
                repository.deleteDistritos()
                repository.deleteNegocios()
                repository.deleteRutas()
                repository.deleteEncuesta()
                repository.deleteEstado()
                repository.deleteSeguimiento()
                repository.deleteVisita()
                repository.deleteAlta()
                repository.deleteAltaDatos()
                repository.deleteBaja()
                repository.deleteBajaSuper()
                repository.deleteBajaEstado()
                T()
            }
        }
    }

    interface OnServiceListener {
        fun onClosingActivity()
    }
}