package com.upd.kvupd.domain

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import androidx.work.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.upd.kvupd.BuildConfig
import com.upd.kvupd.R
import com.upd.kvupd.application.Receiver
import com.upd.kvupd.application.work.*
import com.upd.kvupd.data.model.*
import com.upd.kvupd.service.ServiceFinish
import com.upd.kvupd.service.ServicePosicion
import com.upd.kvupd.service.ServiceSetup
import com.upd.kvupd.utils.*
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Constant.FILTRO_OBS
import com.upd.kvupd.utils.Constant.LOOPING
import com.upd.kvupd.utils.Constant.LOOP_CONFIG
import com.upd.kvupd.utils.Constant.PERIODIC_WORK
import com.upd.kvupd.utils.Constant.WP_ALTA
import com.upd.kvupd.utils.Constant.WP_ALTADATO
import com.upd.kvupd.utils.Constant.WP_ALTAFOTO
import com.upd.kvupd.utils.Constant.WP_BAJA
import com.upd.kvupd.utils.Constant.WP_BAJAESTADO
import com.upd.kvupd.utils.Constant.WP_FOTO
import com.upd.kvupd.utils.Constant.WP_RESPUESTA
import com.upd.kvupd.utils.Constant.WP_SEGUIMIENTO
import com.upd.kvupd.utils.Constant.WP_VISITA
import com.upd.kvupd.utils.Constant.W_CONFIG
import com.upd.kvupd.utils.Constant.W_DISTRITO
import com.upd.kvupd.utils.Constant.W_ENCUESTA
import com.upd.kvupd.utils.Constant.W_FINISH
import com.upd.kvupd.utils.Constant.W_NEGOCIO
import com.upd.kvupd.utils.Constant.W_RUTA
import com.upd.kvupd.utils.Constant.W_SETUP
import com.upd.kvupd.utils.Constant.W_USER
import com.upd.kvupd.utils.Constant.isCONFinitialized
import com.upd.kvupd.utils.Interface.servworkListener
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FunImpl @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val workManager: WorkManager
) : Functions {

    override fun generateQR(value: String): Bitmap {
        lateinit var bitmap: Bitmap
        val mfw = MultiFormatWriter()
        try {
            val be = BarcodeEncoder()
            val bm = mfw.encode(value, BarcodeFormat.QR_CODE, 500, 500)
            bitmap = be.createBitmap(bm)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    override fun saveQR(bm: Bitmap) {
        try {
            val bytes = ByteArrayOutputStream()
            bm.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
            val rootPath = "${ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/"
            val root = File(rootPath)
            if (!root.exists()) {
                root.mkdirs()
            }
            val f = File("${rootPath}qrcode.png")
            if (f.exists()) {
                f.delete()
            }
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun existQR(): Boolean {
        val path = "${ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/qrcode.png"
        return File(path).exists()
    }

    override fun addIPtoQRIMEI() {
        val bitmap = getQR()
        val campo = StringBuilder()
        val qrRecognizer = BarcodeDetector.Builder(ctx)
            .setBarcodeFormats(Barcode.QR_CODE)
            .build()
        val frame = Frame.Builder().setBitmap(bitmap).build()
        val result = qrRecognizer.detect(frame)
        for (i in 0 until result.size()) {
            campo.append(result.valueAt(i).rawValue)
        }
        if (!campo.toString().contains("-")) {
            val newQr = "191.98.177.57-$campo"
            val bm = generateQR(newQr)
            saveQR(bm)
        }
    }

    override fun parseQRtoIP(): String {
        val bitmap = getQR()
        val campo = StringBuilder()
        val qrRecognizer = BarcodeDetector.Builder(ctx)
            .setBarcodeFormats(Barcode.QR_CODE)
            .build()
        val frame = Frame.Builder().setBitmap(bitmap).build()
        val result = qrRecognizer.detect(frame)
        for (i in 0 until result.size()) {
            val ip = result.valueAt(i).rawValue.split("-")[0]
            campo.append(ip)
        }
        return campo.toString()
    }

    override fun parseQRtoIMEI(add: Boolean): String {
        val bitmap = getQR()
        val campo = StringBuilder()
        val qrRecognizer = BarcodeDetector.Builder(ctx)
            .setBarcodeFormats(Barcode.QR_CODE)
            .build()
        val frame = Frame.Builder().setBitmap(bitmap).build()
        val result = qrRecognizer.detect(frame)
        for (i in 0 until result.size()) {
            val imei = result.valueAt(i).rawValue.split("-")[1]
            campo.append(imei)
        }
        return if (add) {
            "$campo-V"
        } else {
            campo.toString()
        }
    }

    override fun getQR(): Bitmap {
        val path = "${ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/qrcode.png"
        return BitmapFactory.decodeFile(path)
    }

    override fun appSO(): String {
        var resultado = ""
        val data = Build.VERSION_CODES::class.java.fields
        data.forEach { i ->
            var api = -1
            try {
                api = i.getInt(object {})
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
            if (api == Build.VERSION.SDK_INT) {
                resultado += "API: $api "
                resultado += "SO: ${i.name}"
            }
        }
        return "App: KVU Ver: ${BuildConfig.VERSION_NAME} $resultado"
    }

    override fun formatLongToHour(l: Long): String {
        var segundos = l / 1000
        var minutos = segundos / 60
        val horas = minutos / 60

        segundos %= 60
        minutos %= 60

        return "${horas}h - ${minutos}m - ${segundos}s"
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    override fun isConnected(): Boolean {
        val resultado: Boolean
        val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val n = cm.activeNetwork
        resultado = if (n != null) {
            val nc = cm.getNetworkCapabilities(n)!!
            when {
                nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            false
        }
        return resultado
    }

    override fun deleteFotos() {
        val file = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        if (file.exists()) {
            val fotos = file.listFiles()
            if (fotos != null) {
                for (i in fotos.indices) {
                    fotos[i].delete()
                }
            }
        }
    }

    override fun isSunday(): Boolean {
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DAY_OF_WEEK]
        return day == Calendar.SUNDAY
    }

    override fun filterListCliente(list: List<DataCliente>): MutableList<String> {
        val dt = mutableListOf<String>()
        list.forEach { i ->
            val cliente = "${i.id} - ${i.nombre}"
            if (FILTRO_OBS != 9) {

                if (FILTRO_OBS == i.observacion) {
                    dt.add(cliente)
                }
            } else {
                dt.add(cliente)
            }
        }
        return dt
    }

    @SuppressLint("MissingPermission")
    override fun mobileInternetState() {
        val connectivityManager = ctx.getSystemService(ConnectivityManager::class.java)
        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {

            override fun onLost(network: Network) {
                val item = saveSystemActions("INTERNET", "Servicio internet desconectado")
                servworkListener?.savingSystemReport(item)
            }

            override fun onCapabilitiesChanged(network: Network, nc: NetworkCapabilities) {
                val st = when {
                    nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
                    nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ETHERNET"
                    nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        val tm = ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                        when (tm.dataNetworkType) {
                            TelephonyManager.NETWORK_TYPE_IDEN -> "2G MOBILE 25 kbps"
                            TelephonyManager.NETWORK_TYPE_CDMA -> "2G MOBILE 14-64 kbps"
                            TelephonyManager.NETWORK_TYPE_1xRTT -> "2G MOBILE 50-100 kbps"
                            TelephonyManager.NETWORK_TYPE_EDGE -> "2G MOBILE 50-100 kbps"
                            TelephonyManager.NETWORK_TYPE_GPRS -> "2G MOBILE 100 kbps"
                            TelephonyManager.NETWORK_TYPE_EVDO_0 -> "3G MOBILE 400-1000 kbps"
                            TelephonyManager.NETWORK_TYPE_EVDO_A -> "3G MOBILE 600-1400 kbps"
                            TelephonyManager.NETWORK_TYPE_HSPA -> "3G MOBILE 700-1700 kbps"
                            TelephonyManager.NETWORK_TYPE_UMTS -> "3G MOBILE 400-7000 kbps"
                            TelephonyManager.NETWORK_TYPE_EHRPD -> "3G MOBILE 1-2 Mbps"
                            TelephonyManager.NETWORK_TYPE_HSUPA -> "3G MOBILE 1-23 Mbps"
                            TelephonyManager.NETWORK_TYPE_HSDPA -> "3G MOBILE 2-14 Mbps"
                            TelephonyManager.NETWORK_TYPE_EVDO_B -> "3G MOBILE 5 Mbps"
                            TelephonyManager.NETWORK_TYPE_HSPAP -> "3G MOBILE 10-20 Mbps"
                            TelephonyManager.NETWORK_TYPE_LTE -> "4G MOBILE 10+ Mbps"
                            else -> "MOBILE UNKNOW"
                        }
                    }

                    else -> "UNKNOW"
                }
                val item = saveSystemActions("INTERNET", "Internet conectado $st")
                servworkListener?.savingSystemReport(item)
            }
        })
    }

    override fun enableBroadcastGPS() {
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        ctx.registerReceiver(Receiver(), filter)
    }

    override fun saveSystemActions(tipo: String, msg: String?): TIncidencia {
        val obs = when (tipo) {
            "GPS" -> if (ctx.isGPSDisabled()) "Ubicacion GPS desactivada" else "Ubicacion GPS activada"
            "TIME" -> "Fecha y hora fueron modificados"
            "INTERNET" -> msg ?: "Nothing"
            "APP" -> msg ?: "App nothing"
            else -> "Do something"
        }
        val fecha = Calendar.getInstance().time.dateToday(4)
        val codigo = if (isCONFinitialized()) {
            CONF.codigo
        } else {
            0
        }
        return TIncidencia(tipo, codigo, obs, fecha)
    }

    override fun setupMarkers(map: GoogleMap, list: List<MarkerMap>): List<Marker> {
        val m = mutableListOf<Marker>()
        list.forEach { i ->
            if ((i.longitud < 0 && i.latitud < 0) ||
                (i.longitud > 0 && i.latitud > 0) ||
                (i.longitud < 0 && i.latitud > 0) ||
                (i.longitud > 0 && i.latitud < 0)
            ) {
                if (i.venta == 0) {
                    m.add(map.addingMarker(i, R.drawable.pin_venta))
                } else {
                    when (i.observacion) {
                        0 -> m.add(map.addingMarker(i, R.drawable.pin_pedido))
                        in 1..7 -> m.add(map.addingMarker(i, R.drawable.pin_otros))
                        9 -> if (i.atendido < 2) m.add(map.addingMarker(i, R.drawable.pin_chess))
                    }
                }
            }
        }
        return m
    }

    override fun pedimapMarkers(map: GoogleMap, list: List<Pedimap>): List<Marker> {
        val m = mutableListOf<Marker>()
        list.forEach { i ->
            val p = i.posicion
            if ((p.longitud < 0 && p.latitud < 0) ||
                (p.longitud > 0 && p.latitud > 0) ||
                (p.longitud < 0 && p.latitud > 0) ||
                (p.longitud > 0 && p.latitud < 0)
            ) {
                when (i.emitiendo) {
                    0 -> m.add(map.markerPedimap(i, R.drawable.pin_noemite))
                    1 -> m.add(map.markerPedimap(i, R.drawable.pin_emite))
                }
            }
        }
        return m
    }

    override fun altaMarkers(map: GoogleMap, list: List<TAlta>): List<Marker> {
        val m = mutableListOf<Marker>()
        list.forEach { i ->
            if ((i.longitud < 0 && i.latitud < 0) ||
                (i.longitud > 0 && i.latitud > 0) ||
                (i.longitud < 0 && i.latitud > 0) ||
                (i.longitud > 0 && i.latitud < 0)
            ) {
                m.add(map.markerAlta(i, R.drawable.pin_altas))
            }
        }
        return m
    }

    override fun bajaMarker(map: GoogleMap, baja: TBajaSuper): Marker {
        val m = mutableListOf<Marker>()
        val longitud = baja.longitud
        val latitud = baja.latitud
        if ((longitud < 0 && latitud < 0) ||
            (longitud > 0 && latitud > 0) ||
            (longitud < 0 && latitud > 0) ||
            (longitud > 0 && latitud < 0)
        ) {
            m.add(map.markerBaja(baja, R.drawable.pin_bajas))
        }
        return m[0]
    }

    override fun executeService(service: String, foreground: Boolean) {
        val cs: Class<*> = when (service) {
            "setup" -> ServiceSetup::class.java
            "position" -> ServicePosicion::class.java
            else -> ServiceFinish::class.java
        }
        val intent = Intent(ctx, cs)
        if (!ctx.isServiceRunning(cs)) {
            if (foreground) {
                when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> ctx.startForegroundService(
                        intent
                    )

                    Build.VERSION.SDK_INT < Build.VERSION_CODES.O -> ctx.startService(intent)
                }
            } else {
                ctx.startService(intent)
            }
        }
    }

    override fun constrainsWork(): Constraints {
        return Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    }

    override fun launchWorkers() {
        LOOPING = true
        LOOP_CONFIG = 0
        workManager
            .beginWith(workerConfiguracion())
            .then(listOf(workerUser(), workerDistritos(), workerNegocios(), workerRutas()))
            .then(workerEncuestas())
            .enqueue()
    }

    /*override fun sinchroWorkers() {
        workManager
            .beginWith(listOf(workerUser(), workerDistritos(), workerNegocios(), workerRutas()))
            .enqueue()
    }*/

    override fun chooseCloseWorker(work: String) {
        when (work) {
            "setup" -> workManager.cancelUniqueWork(W_SETUP)
            "finish" -> workManager.cancelUniqueWork(W_FINISH)
            "periodic" -> workManager.cancelAllWorkByTag(PERIODIC_WORK)
        }
    }

    override fun workerSetup(long: Long) {
        val delay = if (long < 0) 5000 else long
        val work = OneTimeWorkRequestBuilder<SetupWork>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                3,
                TimeUnit.MINUTES
            )
            .addTag(W_SETUP)
            .setConstraints(constrainsWork())
            .build()
        workManager.enqueueUniqueWork(W_SETUP, ExistingWorkPolicy.REPLACE, work)
    }

    override fun workerFinish(long: Long) {
        val delay = if (long < 0) 5000 else long
        val work = OneTimeWorkRequestBuilder<FinishWork>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                3,
                TimeUnit.MINUTES
            )
            .addTag(W_FINISH)
            .setConstraints(constrainsWork())
            .build()
        workManager.enqueueUniqueWork(W_FINISH, ExistingWorkPolicy.REPLACE, work)
    }

    override fun workerConfiguracion() =
        OneTimeWorkRequestBuilder<ConfigWork>()
            .addTag(W_CONFIG)
            .setConstraints(constrainsWork())
            .build()

    override fun workerUser() =
        OneTimeWorkRequestBuilder<UserWork>()
            .addTag(W_USER)
            .setConstraints(constrainsWork())
            .build()

    override fun workerDistritos() =
        OneTimeWorkRequestBuilder<DistritosWork>()
            .addTag(W_DISTRITO)
            .setConstraints(constrainsWork())
            .build()

    override fun workerNegocios() =
        OneTimeWorkRequestBuilder<NegociosWork>()
            .addTag(W_NEGOCIO)
            .setConstraints(constrainsWork())
            .build()

    override fun workerRutas() =
        OneTimeWorkRequestBuilder<RutasWork>()
            .addTag(W_RUTA)
            .setConstraints(constrainsWork())
            .build()

    override fun workerEncuestas() =
        OneTimeWorkRequestBuilder<EncuestaWork>()
            .addTag(W_ENCUESTA)
            .setConstraints(constrainsWork())
            .build()

    override fun workerperSeguimiento() {
        val wp = PeriodicWorkRequestBuilder<SeguimientoPWork>(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            TimeUnit.MILLISECONDS
        )
            .addTag(PERIODIC_WORK)
            .setConstraints(constrainsWork())
            .build()
        workManager.enqueueUniquePeriodicWork(
            WP_SEGUIMIENTO,
            ExistingPeriodicWorkPolicy.REPLACE,
            wp
        )
    }

    override fun workerperVisita() {
        val wp = PeriodicWorkRequestBuilder<VisitaPWork>(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            TimeUnit.MILLISECONDS
        )
            .addTag(PERIODIC_WORK)
            .setConstraints(constrainsWork())
            .build()
        workManager.enqueueUniquePeriodicWork(
            WP_VISITA,
            ExistingPeriodicWorkPolicy.REPLACE,
            wp
        )
    }

    override fun workerperAlta() {
        val wp = PeriodicWorkRequestBuilder<AltaPWork>(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            TimeUnit.MILLISECONDS
        )
            .addTag(PERIODIC_WORK)
            .setConstraints(constrainsWork())
            .build()
        workManager.enqueueUniquePeriodicWork(
            WP_ALTA,
            ExistingPeriodicWorkPolicy.REPLACE,
            wp
        )
    }

    override fun workerperAltaEstado() {
        val wp = PeriodicWorkRequestBuilder<AltaDatoPWork>(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            TimeUnit.MILLISECONDS
        )
            .addTag(PERIODIC_WORK)
            .setConstraints(constrainsWork())
            .build()
        workManager.enqueueUniquePeriodicWork(
            WP_ALTADATO,
            ExistingPeriodicWorkPolicy.REPLACE,
            wp
        )
    }

    override fun workerperBaja() {
        val wp = PeriodicWorkRequestBuilder<BajaPWork>(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            TimeUnit.MILLISECONDS
        )
            .addTag(PERIODIC_WORK)
            .setConstraints(constrainsWork())
            .build()
        workManager.enqueueUniquePeriodicWork(
            WP_BAJA,
            ExistingPeriodicWorkPolicy.REPLACE,
            wp
        )
    }

    override fun workerperBajaEstado() {
        val wp = PeriodicWorkRequestBuilder<BajaEstadoPWork>(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            TimeUnit.MILLISECONDS
        )
            .addTag(PERIODIC_WORK)
            .setConstraints(constrainsWork())
            .build()
        workManager.enqueueUniquePeriodicWork(
            WP_BAJAESTADO,
            ExistingPeriodicWorkPolicy.REPLACE,
            wp
        )
    }

    override fun workerperRespuesta() {
        val wp = PeriodicWorkRequestBuilder<RespuestaPWork>(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            TimeUnit.MILLISECONDS
        )
            .addTag(PERIODIC_WORK)
            .setConstraints(constrainsWork())
            .build()
        workManager.enqueueUniquePeriodicWork(
            WP_RESPUESTA,
            ExistingPeriodicWorkPolicy.REPLACE,
            wp
        )
    }

    override fun workerperFoto() {
        val wp = PeriodicWorkRequestBuilder<FotoPWork>(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            TimeUnit.MILLISECONDS
        )
            .addTag(PERIODIC_WORK)
            .setConstraints(constrainsWork())
            .build()
        workManager.enqueueUniquePeriodicWork(
            WP_FOTO,
            ExistingPeriodicWorkPolicy.REPLACE,
            wp
        )
    }

    override fun workerperAltaFoto() {
        val wp = PeriodicWorkRequestBuilder<AltaFotoPWork>(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            TimeUnit.MILLISECONDS
        )
            .addTag(PERIODIC_WORK)
            .setConstraints(constrainsWork())
            .build()
        workManager.enqueueUniquePeriodicWork(
            WP_ALTAFOTO,
            ExistingPeriodicWorkPolicy.REPLACE,
            wp
        )
    }
}