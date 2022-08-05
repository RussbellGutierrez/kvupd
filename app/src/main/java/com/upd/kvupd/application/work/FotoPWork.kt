package com.upd.kvupd.application.work

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.data.model.TRespuesta
import com.upd.kvupd.domain.Repository
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.NetworkRetrofit
import com.upd.kvupd.utils.toReqBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class FotoPWork @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: Repository
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { FotoPWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            val item = repository.getServerFoto("Pendiente")
            if (!item.isNullOrEmpty()) {
                item.forEach { i ->
                    val p = requestBody(i)
                    repository.setWebFotos(p).collect {
                        when(it) {
                            is NetworkRetrofit.Success -> {
                                i.estado = "Enviado"
                                repository.saveFoto(i)
                                Log.d(_tag,"Foto enviado $i")
                            }
                            is NetworkRetrofit.Error -> Log.e(_tag,"Foto Error -> ${it.message}")
                        }
                    }
                }
            }
            return@withContext Result.success()
        }

    private fun requestBody(j: TRespuesta): RequestBody {
        val baos = ByteArrayOutputStream()
        val bm = BitmapFactory.decodeFile(j.rutafoto)
        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val byteArray = baos.toByteArray()
        val foto = Base64.encodeToString(byteArray, Base64.DEFAULT)

        val p = JSONObject()
        p.put("empresa", CONF.empresa)
        p.put("empleado", CONF.codigo)
        p.put("cliente", j.cliente)
        p.put("encuesta", j.encuesta)
        p.put("sucursal", CONF.sucursal)
        p.put("foto", foto)
        return p.toReqBody()
    }

}