package com.upd.kvupd.domain

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Environment
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
import com.upd.kvupd.R
import com.upd.kvupd.BuildConfig
import com.upd.kvupd.application.work.*
import com.upd.kvupd.data.model.MarkerMap
import com.upd.kvupd.data.model.Pedimap
import com.upd.kvupd.data.model.TAlta
import com.upd.kvupd.data.model.TBajaSuper
import com.upd.kvupd.service.ServiceFinish
import com.upd.kvupd.service.ServicePosicion
import com.upd.kvupd.service.ServiceSetup
import com.upd.kvupd.utils.*
import com.upd.kvupd.utils.Constant.PERIODIC_WORK
import com.upd.kvupd.utils.Constant.WP_ALTA
import com.upd.kvupd.utils.Constant.WP_ALTADATO
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

    override fun parseQRtoIMEI(add: Boolean): String {
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

    override fun dateToday(formato: Int): String {
        val fecha = Calendar.getInstance().time
        return fecha.timeToText(formato)
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

    override fun setupMarkers(map: GoogleMap, list: List<MarkerMap>): List<Marker> {
        val m = mutableListOf<Marker>()
        list.forEach { i ->
            if ((i.longitud < 0 && i.latitud < 0) ||
                (i.longitud > 0 && i.latitud > 0) ||
                (i.longitud < 0 && i.latitud > 0) ||
                (i.longitud > 0 && i.latitud < 0)
            ) {
                when (i.observacion) {
                    0 -> m.add(map.addingMarker(i, R.drawable.pin_pedido))
                    in 1..7 -> m.add(map.addingMarker(i, R.drawable.pin_otros))
                    9 -> if (i.atendido < 2) m.add(map.addingMarker(i, R.drawable.pin_chess))
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

    override fun launchWorkers() {
        workManager
            .beginWith(workerConfiguracion())
            .then(listOf(workerUser(), workerDistritos(), workerNegocios(), workerRutas()))
            .then(workerEncuestas())
            .enqueue()
    }

    override fun sinchroWorkers() {
        workManager
            .beginWith(listOf(workerUser(), workerDistritos(), workerNegocios(), workerRutas()))
            .enqueue()
    }

    override fun chooseCloseWorker(work: String) {
        when(work) {
            "setup" -> workManager.cancelUniqueWork(W_SETUP)
            "finish" -> workManager.cancelUniqueWork(W_FINISH)
            "periodic" -> workManager.cancelAllWorkByTag(PERIODIC_WORK)
        }
    }

    override fun workerSetup(long: Long) {
        val work = OneTimeWorkRequestBuilder<SetupWork>()
            .setInitialDelay(long, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                3,
                TimeUnit.MINUTES
            )
            .addTag(W_SETUP)
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
            .build()
        workManager.enqueueUniqueWork(W_FINISH, ExistingWorkPolicy.REPLACE, work)
    }

    override fun workerConfiguracion() =
        OneTimeWorkRequestBuilder<ConfigWork>()
            .addTag(W_CONFIG)
            .build()

    override fun workerUser() =
        OneTimeWorkRequestBuilder<UserWork>()
            .addTag(W_USER)
            .build()

    override fun workerDistritos() =
        OneTimeWorkRequestBuilder<DistritosWork>()
            .addTag(W_DISTRITO)
            .build()

    override fun workerNegocios() =
        OneTimeWorkRequestBuilder<NegociosWork>()
            .addTag(W_NEGOCIO)
            .build()

    override fun workerRutas() =
        OneTimeWorkRequestBuilder<RutasWork>()
            .addTag(W_RUTA)
            .build()

    override fun workerEncuestas() =
        OneTimeWorkRequestBuilder<EncuestaWork>()
            .addTag(W_ENCUESTA)
            .build()

    override fun workerperSeguimiento() {
        val wp = PeriodicWorkRequestBuilder<SeguimientoPWork>(
            PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS,
            TimeUnit.MILLISECONDS,
            PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
            TimeUnit.MILLISECONDS
        )
            .addTag(PERIODIC_WORK)
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
            .build()
        workManager.enqueueUniquePeriodicWork(
            WP_FOTO,
            ExistingPeriodicWorkPolicy.REPLACE,
            wp
        )
    }
}