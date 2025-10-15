package com.upd.kvupd.domain

import android.app.NotificationManager
import android.content.Context
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class OldFunImpl @Inject constructor(
    @ApplicationContext private val ctx: Context,
    private val workManager: WorkManager,
    private val notificationManager: NotificationManager
) : OldFunctions {

    /*override fun generateQR(value: String): Bitmap {
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

    override fun filterListCliente(list: List<DataCliente>): List<DataCliente> {
        val cm = mutableListOf<DataCliente>()
        val sorted = list.sortedBy { it.nombre }
        sorted.forEach { i ->
            if (FILTRO_OBS != 9) {
                if (FILTRO_OBS == i.observacion) {
                    cm.add(i)
                }
            } else {
                cm.add(i)
            }
        }
        return cm
    }

    @SuppressLint("MissingPermission")
    override fun mobileInternetState() {

        val debounceTimeout = 30000L
        val debounceHandler = Handler(Looper.getMainLooper())
        var lastNetworkCapabilities: NetworkCapabilities? = null

        val connectivityManager = ctx.getSystemService(ConnectivityManager::class.java)
        connectivityManager.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {

            override fun onLost(network: Network) {

                debounceHandler.removeCallbacksAndMessages(null)
                debounceHandler.postDelayed({
                    val item = saveSystemActions("INTERNET", "Servicio internet desconectado")
                    if (item != null) {
                        interListener?.savingSystemReport(item)
                    }
                }, debounceTimeout)
            }

            override fun onCapabilitiesChanged(network: Network, nc: NetworkCapabilities) {

                debounceHandler.removeCallbacksAndMessages(null)
                debounceHandler.postDelayed({
                    if (lastNetworkCapabilities != nc) {
                        lastNetworkCapabilities = nc

                        val st = when {
                            nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
                            nc.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ETHERNET"
                            nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                                val tm =
                                    ctx.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
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
                        if (item != null) {
                            interListener?.savingSystemReport(item)
                        }
                    }
                }, debounceTimeout)
            }
        })
    }

    override fun enableBroadcastGPS() {
        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        ctx.registerReceiver(OldReceiver(), filter)
    }

    override fun checkGPSEnabled() {
        gpsListener?.changeGPSstate(ctx.isGPSDisabled())
    }

    override fun enableBatteryChange() {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        ctx.registerReceiver(OldReceiver(), filter)
    }

    override fun saveSystemActions(tipo: String, msg: String?): TableIncidencia? {
        val obs = when (tipo) {
            "GPS" -> if (ctx.isGPSDisabled()) "Ubicacion GPS desactivada" else "Ubicacion GPS activada"
            "TIME" -> "Fecha y hora fueron modificados"
            "INTERNET" -> msg ?: "Nothing"
            "APP" -> msg ?: "App nothing"
            else -> ""
        }
        val fecha = Calendar.getInstance().time.dateToday(4)
        if (isCONFinitialized()) {
            return TableIncidencia(tipo, CONF.codigo, obs, fecha)
        }
        return null
    }

    override fun setupMarkers(map: GoogleMap, list: List<MarkerMap>): List<Marker> {
        val m = mutableListOf<Marker>()
        list.forEach { i ->
            if ((i.longitud < 0 && i.latitud < 0) ||
                (i.longitud > 0 && i.latitud > 0) ||
                (i.longitud < 0 && i.latitud > 0) ||
                (i.longitud > 0 && i.latitud < 0)
            ) {
                if (i.compra == 1) {
                    m.add(map.addingMarker(i, R.drawable.pin_peligro))
                } else {
                    if (i.venta == 0) {
                        m.add(map.addingMarker(i, R.drawable.pin_venta))
                    } else {
                        when (i.observacion) {
                            0 -> m.add(map.addingMarker(i, R.drawable.pin_pedido))
                            in 1..7 -> m.add(map.addingMarker(i, R.drawable.pin_otros))
                            9 -> if (i.atendido < 2) m.add(
                                map.addingMarker(
                                    i,
                                    R.drawable.pin_chess
                                )
                            )
                        }
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

    override fun consultaMarker(map: GoogleMap, list: List<TableConsulta>): List<Marker> {
        val m = mutableListOf<Marker>()
        list.forEach { i ->
            if ((i.longitud < 0 && i.latitud < 0) ||
                (i.longitud > 0 && i.latitud > 0) ||
                (i.longitud < 0 && i.latitud > 0) ||
                (i.longitud > 0 && i.latitud < 0)
            ) {
                m.add(map.markerConsulta(i, R.drawable.pin_chess))
            }
        }
        return m
    }

    override fun altaMarkers(map: GoogleMap, list: List<TableAlta>): List<Marker> {
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

    override fun bajaMarker(map: GoogleMap, baja: TableBajaSupervisor): List<Marker> {
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
        return m
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

    override fun closePeriodicWorker() {
        workManager.cancelAllWorkByTag(PERIODIC_WORK)
    }

    override fun alarmSetup(long: Long) {
        val time = if (long <= 0) 5000 else long
        val intent = Intent(ctx, OldReceiver::class.java)
        intent.action = ACTION_ALARM_SETUP

        val pendingIntent =
            PendingIntent.getBroadcast(
                ctx,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }

    override fun alarmFinish(long: Long) {
        val time = if (long <= 0) 5000 else long
        val intent = Intent(ctx, OldReceiver::class.java)
        intent.action = ACTION_ALARM_FINISH

        val pendingIntent =
            PendingIntent.getBroadcast(
                ctx,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }

    override fun workerConfiguracion() =
        OneTimeWorkRequestBuilder<OldConfigWork>()
            .addTag(W_CONFIG)
            .setConstraints(constrainsWork())
            .build()

    override fun workerUser() =
        OneTimeWorkRequestBuilder<OldUserWork>()
            .addTag(W_USER)
            .setConstraints(constrainsWork())
            .build()

    override fun workerDistritos() =
        OneTimeWorkRequestBuilder<OldDistritosWork>()
            .addTag(W_DISTRITO)
            .setConstraints(constrainsWork())
            .build()

    override fun workerNegocios() =
        OneTimeWorkRequestBuilder<OldNegociosWork>()
            .addTag(W_NEGOCIO)
            .setConstraints(constrainsWork())
            .build()

    override fun workerRutas() =
        OneTimeWorkRequestBuilder<OldRutasWork>()
            .addTag(W_RUTA)
            .setConstraints(constrainsWork())
            .build()

    override fun workerEncuestas() =
        OneTimeWorkRequestBuilder<OldEncuestaWork>()
            .addTag(W_ENCUESTA)
            .setConstraints(constrainsWork())
            .build()

    override fun workerperVisita() {
        val wp = PeriodicWorkRequestBuilder<OldVisitaPWork>(
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
        val wp = PeriodicWorkRequestBuilder<OldAltaPWork>(
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
        val wp = PeriodicWorkRequestBuilder<OldAltaDatoPWork>(
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
        val wp = PeriodicWorkRequestBuilder<OldBajaPWork>(
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
        val wp = PeriodicWorkRequestBuilder<OldBajaEstadoPWork>(
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
        val wp = PeriodicWorkRequestBuilder<OldRespuestaPWork>(
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
        val wp = PeriodicWorkRequestBuilder<OldFotoPWork>(
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
        val wp = PeriodicWorkRequestBuilder<OldAltaFotoPWork>(
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

    override fun closeAllNotifications() {
        notificationManager.cancelAll()
    }*/
}