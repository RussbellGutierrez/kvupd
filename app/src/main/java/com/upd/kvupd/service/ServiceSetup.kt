package com.upd.kvupd.service

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.gms.location.LocationServices
import com.upd.kvupd.application.work.HelperNotification
import com.upd.kvupd.data.model.TIncidencia
import com.upd.kvupd.data.model.TSeguimiento
import com.upd.kvupd.domain.OldFunctions
import com.upd.kvupd.domain.OldLocationClient
import com.upd.kvupd.domain.OldOnInterSetup
import com.upd.kvupd.domain.OldRepository
import com.upd.kvupd.utils.OldCaptureLocation
import com.upd.kvupd.utils.OldConstant
import com.upd.kvupd.utils.OldConstant.CONF
import com.upd.kvupd.utils.OldConstant.GPS_FAST_INTERVAL
import com.upd.kvupd.utils.OldConstant.GPS_LOC
import com.upd.kvupd.utils.OldConstant.GPS_METERS
import com.upd.kvupd.utils.OldConstant.GPS_NORMAL_INTERVAL
import com.upd.kvupd.utils.OldConstant.IMEI
import com.upd.kvupd.utils.OldConstant.IPA
import com.upd.kvupd.utils.OldConstant.IP_AUX
import com.upd.kvupd.utils.OldConstant.IP_P
import com.upd.kvupd.utils.OldConstant.IP_S
import com.upd.kvupd.utils.OldConstant.LOOPING
import com.upd.kvupd.utils.OldConstant.OPTURL
import com.upd.kvupd.utils.OldConstant.SETUP_NOTIF
import com.upd.kvupd.utils.OldConstant.W_CONFIG
import com.upd.kvupd.utils.OldConstant.W_DISTRITO
import com.upd.kvupd.utils.OldConstant.W_ENCUESTA
import com.upd.kvupd.utils.OldConstant.W_NEGOCIO
import com.upd.kvupd.utils.OldConstant.W_RUTA
import com.upd.kvupd.utils.OldConstant.W_USER
import com.upd.kvupd.utils.OldConstant.isCONFinitialized
import com.upd.kvupd.utils.OldEvent
import com.upd.kvupd.utils.OldHostSelectionInterceptor
import com.upd.kvupd.utils.OldInterface.closeListener
import com.upd.kvupd.utils.OldInterface.interListener
import com.upd.kvupd.utils.dateToday
import com.upd.kvupd.utils.toReqBody
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class ServiceSetup : LifecycleService(), OldOnInterSetup {

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var functions: OldFunctions

    @Inject
    lateinit var repository: OldRepository

    @Inject
    lateinit var helperNotification: HelperNotification

    @Inject
    lateinit var host: OldHostSelectionInterceptor

    private var user = false
    private var distrito = false
    private var negocio = false
    private var ruta = false
    private var encuesta = false
    private var serviceScope: CoroutineScope? = null
    private lateinit var locationClient: OldLocationClient
    private lateinit var configLiveData: LiveData<OldEvent<List<WorkInfo>>>
    private lateinit var userLiveData: LiveData<OldEvent<List<WorkInfo>>>
    private lateinit var distritoLiveData: LiveData<OldEvent<List<WorkInfo>>>
    private lateinit var negocioLiveData: LiveData<OldEvent<List<WorkInfo>>>
    private lateinit var rutaLiveData: LiveData<OldEvent<List<WorkInfo>>>
    private lateinit var encuestaLiveData: LiveData<OldEvent<List<WorkInfo>>>
    private val _tag by lazy { ServiceSetup::class.java.simpleName }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(_tag, "Service setup destroyed")
        serviceScope?.cancel()
        serviceScope = null
        interListener = null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(_tag, "Service setup launch")
        createServiceScope()
        functions.enableBroadcastGPS()
        functions.enableBatteryChange()
        functions.mobileInternetState()

        interListener = this
        locationClient = OldCaptureLocation(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )

        IMEI = functions.parseQRtoIMEI(true)
        IPA = functions.parseQRtoIP()
        initObsWork()
        verifyHours()
        initIPS()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(_tag, "Service startcommand")
        changeBetweenIconNotification(0)
        launchLocation()
        return START_STICKY
    }

    private fun createServiceScope() {
        serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    private fun restartServiceFunctions() {
        initObsWork()
        verifyHours()
        changeBetweenIconNotification(0)
        if (serviceScope == null || serviceScope?.isActive == false) {
            createServiceScope()
            launchLocation()
        }
    }

    private fun initObsWork() {
        helperNotification.configNotifLaunch()
        configLiveData = workManager.getWorkInfosByTagLiveData(W_CONFIG).map { OldEvent(it) }
        userLiveData = workManager.getWorkInfosByTagLiveData(W_USER).map { OldEvent(it) }
        distritoLiveData = workManager.getWorkInfosByTagLiveData(W_DISTRITO).map { OldEvent(it) }
        negocioLiveData = workManager.getWorkInfosByTagLiveData(W_NEGOCIO).map { OldEvent(it) }
        rutaLiveData = workManager.getWorkInfosByTagLiveData(W_RUTA).map { OldEvent(it) }
        encuestaLiveData = workManager.getWorkInfosByTagLiveData(W_ENCUESTA).map { OldEvent(it) }
    }

    private fun launchLocation() {
        serviceScope?.let {
            locationClient
                .getLocationUpdates(GPS_NORMAL_INTERVAL, GPS_FAST_INTERVAL, GPS_METERS)
                .catch { e -> e.printStackTrace() }
                .onEach { location ->
                    Log.d(
                        _tag,
                        "GPS Location ${location.longitude} / ${location.latitude} / ${location.accuracy}"
                    )
                    GPS_LOC = location
                    if (location.accuracy <= 50f) {
                        Log.d(_tag, "Saving location")
                        saveLocation(location)
                    }
                }
                .launchIn(it)
        }
    }

    private fun priorityWorkers() {
        CoroutineScope(Dispatchers.IO).launch {
            helperNotification.userNotifLaunch()
            helperNotification.distritoNotifLaunch()
            helperNotification.negocioNotifLaunch()
            helperNotification.rutaNotifLaunch()
            helperNotification.encuestaNotifLaunch()

            repository.getFinishTime().let {
                functions.alarmFinish(it)
                Log.i(_tag, "Finish time $it")
            }
        }
    }

    private fun periodicWorkers() {
        if (user && distrito && negocio && ruta && encuesta) {
            Log.d(_tag, "Launch periodic workers")
            functions.workerperVisita()
            functions.workerperAlta()
            functions.workerperAltaEstado()
            functions.workerperBaja()
            functions.workerperBajaEstado()
            functions.workerperRespuesta()
            functions.workerperFoto()
            //functions.workerperAltaFoto()
            user = false
            distrito = false
            negocio = false
            ruta = false
            encuesta = false
        }
    }

    private fun verifyHours() {
        CoroutineScope(Dispatchers.IO).launch {
            val sesion = repository.getSesion()
            val config = repository.getConfig()

            if (sesion == null && config == null) {
                val item = functions.saveSystemActions("APP", "Sin registro configuracion previa")
                if (item != null) {
                    repository.saveIncidencia(item)
                }
                functions.launchWorkers()//No data, launch work config and more
            } else {
                Log.d(_tag, "Get some config or sesion")
                if (repository.getIntoHours()) {
                    checkingData()
                } else {
                    closeEntireApp()
                }
            }
        }
    }

    private fun initIPS() {
        lifecycleScope.launch {
            repository.getSesion().let { sesion ->
                if (sesion != null) {
                    IP_P = "http://${sesion.ipp}/api/"
                    IP_S = "http://${sesion.ips}/api/"
                }
                IP_AUX = "http://${IPA}/api/"
            }
        }
    }

    private fun checkingData() {
        CoroutineScope(Dispatchers.IO).launch {
            val today = repository.isDataToday()
            if (today != 0) {
                repository.deleteConfig()
                repository.deleteClientes()
                repository.deleteEmpleados()
                repository.deleteDistritos()
                repository.deleteNegocios()
                repository.deleteRutas()
                repository.deleteEncuesta()
                repository.deleteEncuestaSeleccionado()
                repository.deleteRespuesta()
                repository.deleteEstado()
                repository.deleteSeguimiento()
                repository.deleteVisita()
                repository.deleteAlta()
                repository.deleteAltaDatos()
                repository.deleteBaja()
                repository.deleteBajaSuper()
                repository.deleteBajaEstado()
                functions.deleteFotos()
                repository.deleteIncidencia()
                repository.deleteAAux()
            }
            functions.launchWorkers()
        }
    }

    private fun closeEntireApp() {
        functions.executeService("finish", false)
    }

    private fun saveLocation(location: Location) {
        CoroutineScope(Dispatchers.IO).launch {
            if (isCONFinitialized()) {
                val fecha = Calendar.getInstance().time.dateToday(4)
                val item = TSeguimiento(
                    fecha,
                    CONF.codigo,
                    location.longitude,
                    location.latitude,
                    location.accuracy.toDouble(),
                    OldConstant.BATTERY_PCT.toDouble(),
                    "Pendiente"
                )
                repository.saveSeguimiento(item)
                sendingLocation(item)
            }
        }
    }

    private fun sendingLocation(item: TSeguimiento) {
        CoroutineScope(Dispatchers.IO).launch {
            if (isCONFinitialized() && CONF.seguimiento == 1) {
                val p = requestBody(item)
                /*repository.setWebSeguimiento(p).collect {
                    when (it) {
                        is OldNetworkRetrofit.Success -> {
                            item.estado = "Enviado"
                            repository.updateSeguimiento(item)
                            Log.d(_tag, "Seguimiento enviado $item")
                        }

                        is OldNetworkRetrofit.Error -> {
                            changeHostServer()
                            Log.e(_tag, "Seguimiento Error ${it.message}")
                        }
                    }
                }*/
            }
        }
    }

    private fun requestBody(j: TSeguimiento): RequestBody {
        val p = JSONObject()
        p.put("fecha", j.fecha)
        p.put("empleado", j.usuario)
        p.put("longitud", j.longitud)
        p.put("latitud", j.latitud)
        p.put("precision", j.precision)
        p.put("imei", IMEI)
        p.put("bateria", j.bateria)
        p.put("sucursal", CONF.sucursal)
        p.put("esquema", CONF.esquema)
        p.put("empresa", CONF.empresa)
        return p.toReqBody()
    }

    private suspend fun changeHostServer() {
        repository.getSesion().let { sesion ->
            when (OPTURL) {
                "aux" -> {
                    OPTURL = "ipp"
                    IP_P = "http://${sesion!!.ipp}/api/"
                }

                "ipp" -> {
                    OPTURL = "ips"
                    IP_S = "http://${sesion!!.ips}/api/"
                }

                "ips" -> {
                    OPTURL = "aux"
                    IP_AUX = "http://$IPA/api/"
                }
            }
            host.setHostBaseUrl()
        }
    }

    /***EVITAR USAR POSICIONES FIJAS (FIXED POSITIONS) DENTRO DE lOS ARRAYS, PUEDE GENERAR PROBLEMAS EN EL CODIGO***/
    override fun onFinishWork(work: String) {
        CoroutineScope(Dispatchers.Main).launch {
            when (work) {
                W_CONFIG -> {
                    configLiveData.observe(this@ServiceSetup) {
                        it.getContentIfNotHandled()?.let { y ->
                            y.lastOrNull()?.let { j ->
                                if (j.state.isFinished) {
                                    helperNotification.configNotif()
                                    when (j.state) {
                                        WorkInfo.State.SUCCEEDED -> priorityWorkers()
                                        WorkInfo.State.FAILED -> {
                                            if (!LOOPING) {
                                                configFailed()
                                            }
                                        }

                                        else -> Unit
                                    }
                                }
                            }
                        }
                    }
                }

                W_USER -> {
                    userLiveData.observe(this@ServiceSetup) {
                        it.getContentIfNotHandled()?.let { y ->
                            y.lastOrNull()?.let { j ->
                                if (j.state.isFinished) {
                                    user = true
                                    helperNotification.userNotif()
                                    periodicWorkers()
                                }
                            }
                        }
                    }
                }

                W_DISTRITO -> {
                    distritoLiveData.observe(this@ServiceSetup) {
                        it.getContentIfNotHandled()?.let { y ->
                            y.lastOrNull()?.let { j ->
                                if (j.state.isFinished) {
                                    distrito = true
                                    helperNotification.distritoNotif()
                                    periodicWorkers()
                                }
                            }
                        }
                    }
                }

                W_NEGOCIO -> {
                    negocioLiveData.observe(this@ServiceSetup) {
                        it.getContentIfNotHandled()?.let { y ->
                            y.lastOrNull()?.let { j ->
                                if (j.state.isFinished) {
                                    negocio = true
                                    helperNotification.negocioNotif()
                                    periodicWorkers()
                                }
                            }
                        }
                    }
                }

                W_RUTA -> {
                    rutaLiveData.observe(this@ServiceSetup) {
                        it.getContentIfNotHandled()?.let { y ->
                            y.lastOrNull()?.let { j ->
                                if (j.state.isFinished) {
                                    ruta = true
                                    helperNotification.rutaNotif()
                                    periodicWorkers()
                                }
                            }
                        }
                    }
                }

                W_ENCUESTA -> {
                    encuestaLiveData.observe(this@ServiceSetup) {
                        it.getContentIfNotHandled()?.let { y ->
                            y.lastOrNull()?.let { j ->
                                if (j.state.isFinished) {
                                    encuesta = true
                                    helperNotification.encuestaNotif()
                                    periodicWorkers()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun savingSystemReport(item: TIncidencia) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.saveIncidencia(item)
        }
    }

    override fun showNotificationSystem(opt: Int) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            val notification = if (opt == 0) {
                helperNotification.setupNotif()
            } else {
                helperNotification.sleepNotif()
            }
            startForeground(SETUP_NOTIF, notification, FOREGROUND_SERVICE_TYPE_LOCATION)
        }
    }

    override fun closeGPS() {
        serviceScope?.cancel()
        serviceScope = null
    }

    @SuppressLint("MissingPermission")
    override fun changeBetweenIconNotification(opt: Int) {
        val notif = when (opt) {
            0 -> helperNotification.setupNotif()
            else -> helperNotification.sleepNotif()
        }
        NotificationManagerCompat.from(this).notify(SETUP_NOTIF, notif)
        when {
            Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU -> startForeground(
                SETUP_NOTIF,
                notif,
                FOREGROUND_SERVICE_TYPE_LOCATION
            )

            Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU &&
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> startForeground(
                SETUP_NOTIF,
                notif
            )
        }
    }

    override fun launchAgainProcess() {
        restartServiceFunctions()
    }

    private fun configFailed() {
        CoroutineScope(Dispatchers.Main).launch {
            val sesion = repository.getSesion()
            if (sesion != null) {
                Log.d(_tag, "Finishing app")
                functions.executeService("finish", false)
            } else {
                Log.e(_tag, "Never download data")
                closeListener?.closingActivity(true) ?: stopSelf()
            }
        }
    }
}