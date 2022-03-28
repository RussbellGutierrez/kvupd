package com.upd.kvupd.service

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
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.gms.location.*
import com.upd.kvupd.application.work.HelperNotification
import com.upd.kvupd.data.model.TSeguimiento
import com.upd.kvupd.di.LocationRequestGps
import com.upd.kvupd.di.LocationSettingsRequestGps
import com.upd.kvupd.domain.Functions
import com.upd.kvupd.domain.Repository
import com.upd.kvupd.domain.ServiceWork
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Constant.GPS_LOC
import com.upd.kvupd.utils.Constant.IMEI
import com.upd.kvupd.utils.Constant.SETUP_NOTIF
import com.upd.kvupd.utils.Constant.W_CONFIG
import com.upd.kvupd.utils.Constant.W_DISTRITO
import com.upd.kvupd.utils.Constant.W_ENCUESTA
import com.upd.kvupd.utils.Constant.W_NEGOCIO
import com.upd.kvupd.utils.Constant.W_RUTA
import com.upd.kvupd.utils.Constant.W_USER
import com.upd.kvupd.utils.Event
import com.upd.kvupd.utils.Interface.serviceListener
import com.upd.kvupd.utils.Interface.workListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ServiceSetup : LifecycleService(), LocationListener, ServiceWork {

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
    private lateinit var configLiveData: LiveData<Event<List<WorkInfo>>>
    private lateinit var userLiveData: LiveData<Event<List<WorkInfo>>>
    private lateinit var distritoLiveData: LiveData<Event<List<WorkInfo>>>
    private lateinit var negocioLiveData: LiveData<Event<List<WorkInfo>>>
    private lateinit var rutaLiveData: LiveData<Event<List<WorkInfo>>>
    private lateinit var encuestaLiveData: LiveData<Event<List<WorkInfo>>>
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
        workListener = null
        super.onDestroy()
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(_tag, "Service setup launch")
        functions.chooseCloseWorker("setup")

        workListener = this
        serviceNotification()

        batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            this.registerReceiver(null, ifilter)
        }

        IMEI = functions.parseQRtoIMEI(true)
        initObsWork()
        verifyHours()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(_tag, "Service startcommand")
        return START_STICKY
    }

    override fun onSinchronizeData() {
        helper.userNotifLaunch()
        helper.distritoNotifLaunch()
        helper.negocioNotifLaunch()
        helper.rutaNotifLaunch()
        CoroutineScope(Dispatchers.Main).launch {
            repository.deleteClientes()
            repository.deleteEmpleados()
            repository.deleteDistritos()
            repository.deleteNegocios()
            repository.deleteRutas()
            functions.sinchroWorkers()
        }
    }

    override fun onFinishWork(work: String) {
        CoroutineScope(Dispatchers.Main).launch {
            when (work) {
                W_CONFIG -> {
                    configLiveData.observe(this@ServiceSetup) {
                        it.getContentIfNotHandled()?.let { y ->
                            y[0].let { j ->
                                if (j.state.isFinished) {
                                    helper.configNotif()
                                    when (j.state) {
                                        WorkInfo.State.SUCCEEDED -> priorityWorkers()
                                        WorkInfo.State.FAILED -> configFailed()
                                        else -> {}
                                    }
                                }
                            }
                        }
                    }
                }
                W_USER -> {
                    userLiveData.observe(this@ServiceSetup) {
                        it.getContentIfNotHandled()?.let { y ->
                            y[0].let { j ->
                                if (j.state.isFinished) {
                                    user = true
                                    helper.userNotif()
                                    periodicWorkers()
                                }
                            }
                        }
                    }
                }
                W_DISTRITO -> {
                    distritoLiveData.observe(this@ServiceSetup) {
                        it.getContentIfNotHandled()?.let { y ->
                            y[0].let { j ->
                                if (j.state.isFinished) {
                                    distrito = true
                                    helper.distritoNotif()
                                    periodicWorkers()
                                }
                            }
                        }
                    }
                }
                W_NEGOCIO -> {
                    negocioLiveData.observe(this@ServiceSetup) {
                        it.getContentIfNotHandled()?.let { y ->
                            y[0].let { j ->
                                if (j.state.isFinished) {
                                    negocio = true
                                    helper.negocioNotif()
                                    periodicWorkers()
                                }
                            }
                        }
                    }
                }
                W_RUTA -> {
                    rutaLiveData.observe(this@ServiceSetup) {
                        it.getContentIfNotHandled()?.let { y ->
                            y[0].let { j ->
                                if (j.state.isFinished) {
                                    ruta = true
                                    helper.rutaNotif()
                                    periodicWorkers()
                                }
                            }
                        }
                    }
                }
                W_ENCUESTA -> {
                    encuestaLiveData.observe(this@ServiceSetup) {
                        it.getContentIfNotHandled()?.let { y ->
                            y[0].let { j ->
                                if (j.state.isFinished) {
                                    encuesta = true
                                    helper.encuestaNotif()
                                    periodicWorkers()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initObsWork() {
        configLiveData = workManager.getWorkInfosByTagLiveData(W_CONFIG).map { Event(it) }
        userLiveData = workManager.getWorkInfosByTagLiveData(W_USER).map { Event(it) }
        distritoLiveData = workManager.getWorkInfosByTagLiveData(W_DISTRITO).map { Event(it) }
        negocioLiveData = workManager.getWorkInfosByTagLiveData(W_NEGOCIO).map { Event(it) }
        rutaLiveData = workManager.getWorkInfosByTagLiveData(W_RUTA).map { Event(it) }
        encuestaLiveData = workManager.getWorkInfosByTagLiveData(W_ENCUESTA).map { Event(it) }
    }

    private fun serviceNotification() {
        val not = helper.setupNotif()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> startForeground(SETUP_NOTIF, not)
            Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> NotificationManagerCompat.from(this)
                .notify(SETUP_NOTIF, not)
        }
    }

    private fun priorityWorkers() {
        CoroutineScope(Dispatchers.IO).launch {
            startLocation()
            helper.userNotifLaunch()
            helper.distritoNotifLaunch()
            helper.negocioNotifLaunch()
            helper.rutaNotifLaunch()
            helper.encuestaNotifLaunch()

            repository.getFinishTime().let {
                Log.d(_tag, "Finish time $it")
                functions.workerFinish(it)
            }
        }
    }

    private fun periodicWorkers() {
        if (user && distrito && negocio && ruta && encuesta) {
            Log.d(_tag, "Launch periodic workers")
            /*functions.workerperSeguimiento()
            functions.workerperVisita()
            functions.workerperAlta()
            functions.workerperAltaEstado()
            functions.workerperBaja()
            functions.workerperBajaEstado()*/
        }
    }

    override fun onLocationChanged(p0: Location) {
        Log.d(_tag, "Location ${p0.longitude} / ${p0.latitude} / ${p0.accuracy}")
        GPS_LOC = p0
        if (p0.accuracy <= 50f) {
            saveLocation(p0)
        }
    }

    private fun verifyHours() {
        CoroutineScope(Dispatchers.IO).launch {
            val sesion = repository.getSesion() != null
            val config = repository.getConfig() != null

            if (sesion && config) {
                Log.d(_tag, "Sesion and config")
                repository.getConfig()!!.let {
                    val hora = functions.dateToday(3).replace(":", "").toInt()
                    val inicio = it.hini.replace(":", "").toInt()
                    val fin = it.hfin.replace(":", "").toInt()
                    val inhours = hora in inicio..fin

                    if (inhours) {
                        checkingData()
                    } else {
                        closeEntireApp()
                    }
                }
            } else {
                Log.e(_tag, "No sesion and config")
                functions.launchWorkers()
            }
        }
    }

    private fun checkingData() {
        CoroutineScope(Dispatchers.IO).launch {
            val fecha = functions.dateToday(5)
            val today = repository.isDataToday(fecha)
            if (!today) {
                repository.deleteConfig()
                repository.deleteClientes()
                repository.deleteEmpleados()
                repository.deleteDistritos()
                repository.deleteNegocios()
                repository.deleteRutas()
                repository.deleteEncuesta()
                repository.deleteSeleccionado()
                repository.deleteEstado()
                repository.deleteSeguimiento()
                repository.deleteVisita()
                repository.deleteAlta()
                repository.deleteAltaDatos()
                repository.deleteBaja()
                repository.deleteBajaSuper()
                repository.deleteBajaEstado()
            }
            functions.launchWorkers()
        }
    }

    private fun closeEntireApp() {
        functions.executeService("finish", false)
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

    private fun configFailed() {
        CoroutineScope(Dispatchers.IO).launch {
            val sesion = repository.getSesion()
            if (sesion != null) {
                Log.d(_tag, "Finishing app")
                functions.executeService("finish", false)
            } else {
                Log.e(_tag, "Never download data")
                if (serviceListener != null) {
                    serviceListener?.onClosingActivity()
                } else {
                    stopSelf()
                }
            }
        }
    }

    interface OnServiceListener {
        fun onClosingActivity()
    }
}