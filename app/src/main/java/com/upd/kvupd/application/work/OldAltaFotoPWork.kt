package com.upd.kvupd.application.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.domain.OldRepository
import com.upd.kvupd.utils.OldHostSelectionInterceptor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class OldAltaFotoPWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: OldRepository,
    private val host: OldHostSelectionInterceptor
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { OldAltaFotoPWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            /*val item = repository.getServerAltaFoto("Pendiente")
            if (!item.isNullOrEmpty()) {
                item.forEach { i ->
                    val p = requestBody(i)
                    repository.setWebAltaFotos(p).collect {
                        when (it) {
                            is NetworkRetrofit.Success -> {
                                i.estado = "Enviado"
                                repository.updateAltaFoto(i)
                                Log.d(_tag, "Foto enviado $i")
                            }
                            is NetworkRetrofit.Error -> {
                                changeHostServer()
                                Log.e(_tag, "AltaFoto Error -> ${it.message}")
                            }
                        }
                    }
                }
            }*/
            return@withContext Result.success()
        }

    /*private fun requestBody(j: TAFoto): RequestBody {
        val baos = ByteArrayOutputStream()
        val bm = BitmapFactory.decodeFile(j.ruta)
        bm.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val byteArray = baos.toByteArray()
        val foto = Base64.encodeToString(byteArray, Base64.DEFAULT)

        val p = JSONObject()
        p.put("fecha",j.fecha)
        p.put("id",j.idaux)
        p.put("empleado", j.empleado)
        p.put("empresa", CONF.empresa)
        p.put("foto", foto)
        return p.toReqBody()
    }*/

    /*private suspend fun changeHostServer() {
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
    }*/
}