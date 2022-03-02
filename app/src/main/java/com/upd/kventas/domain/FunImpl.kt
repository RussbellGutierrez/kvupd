package com.upd.kventas.domain

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.work.BackoffPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.upd.kventas.BuildConfig
import com.upd.kventas.R
import com.upd.kventas.application.work.*
import com.upd.kventas.data.model.MarkerMap
import com.upd.kventas.service.ServicePosicion
import com.upd.kventas.service.ServiceSetup
import com.upd.kventas.utils.Constant.W_CONFIG
import com.upd.kventas.utils.Constant.W_DISTRITO
import com.upd.kventas.utils.Constant.W_NEGOCIO
import com.upd.kventas.utils.Constant.W_SETUP
import com.upd.kventas.utils.Constant.W_USER
import com.upd.kventas.utils.addingMarker
import com.upd.kventas.utils.isServiceRunning
import com.upd.kventas.utils.timeToText
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
            bm.compress(Bitmap.CompressFormat.PNG, 100, bytes)
            val f = File("${ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/qrcode.png")
            if (!f.exists()) {
                f.parentFile!!.mkdirs()
            }
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
        val qrRecognizer = BarcodeDetector.Builder(ctx).build()
        val frame = Frame.Builder().setBitmap(bitmap).build()
        val result = qrRecognizer.detect(frame)
        val campo = StringBuilder()
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
        val file = File(path)
        val uri = Uri.fromFile(file)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val source = ImageDecoder.createSource(ctx.contentResolver, uri)
            val listener = ImageDecoder.OnHeaderDecodedListener { imageDecoder, _, _ ->
                imageDecoder.isMutableRequired = true
            }
            ImageDecoder.decodeBitmap(source, listener)
        } else {
            MediaStore.Images.Media.getBitmap(ctx.contentResolver, uri)
        }
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
        return "App: ${BuildConfig.VERSION_NAME} $resultado"
    }

    override fun setupMarkers(map: GoogleMap, list: List<MarkerMap>): List<Marker> {
        val m = mutableListOf<Marker>()
        list.forEach { i ->
            if ((i.longitud < 0 && i.latitud < 0) ||
                (i.longitud > 0 && i.latitud > 0) ||
                (i.longitud < 0 && i.latitud > 0) ||
                (i.longitud > 0 && i.latitud < 0)
            ) {
                when(i.observacion) {
                    0 -> m.add(map.addingMarker(i, R.drawable.pin_pedido))
                    in 1..7 -> m.add(map.addingMarker(i, R.drawable.pin_otros))
                    9 -> if (i.atendido < 2) m.add(map.addingMarker(i, R.drawable.pin_chess))
                }
            }
        }
        return m
    }

    override fun executeService(service: String, foreground: Boolean) {
        val cs: Class<*> = when (service) {
            "setup" -> ServiceSetup::class.java
            else -> ServicePosicion::class.java
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
            .then(listOf(workerUser(), workerDistritos(), workerNegocios()))
            .enqueue()
    }

    override fun workerSetup(long: Long) {
        val work = OneTimeWorkRequestBuilder<SetupWork>()
            .setInitialDelay(long, TimeUnit.MILLISECONDS)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                5,
                TimeUnit.MINUTES
            )
            .addTag(W_SETUP)
            .build()
        workManager.enqueueUniqueWork(W_SETUP, ExistingWorkPolicy.REPLACE, work)
    }

    override fun workerConfiguracion() =
        OneTimeWorkRequestBuilder<ConfigWork>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                15,
                TimeUnit.MINUTES
            )
            .addTag(W_CONFIG)
            .build()

    override fun workerUser() =
        OneTimeWorkRequestBuilder<UserWork>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                5,
                TimeUnit.MINUTES
            )
            .addTag(W_USER)
            .build()

    override fun workerDistritos() =
        OneTimeWorkRequestBuilder<DistritosWork>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                5,
                TimeUnit.MINUTES
            )
            .addTag(W_DISTRITO)
            .build()

    override fun workerNegocios() =
        OneTimeWorkRequestBuilder<NegociosWork>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                5,
                TimeUnit.MINUTES
            )
            .addTag(W_NEGOCIO)
            .build()
}