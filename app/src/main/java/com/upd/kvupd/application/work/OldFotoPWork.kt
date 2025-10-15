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
class OldFotoPWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: OldRepository,
    private val host: OldHostSelectionInterceptor
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { OldFotoPWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            /*val item = repository.getServerFoto("Pendiente")
            if (item.isNotEmpty()) {
                item.forEach { i ->
                    val p = requestBody(i)
                    repository.setWebFotos(p).collect {
                        when (it) {
                            is OldNetworkRetrofit.Success -> {
                                i.estado = "Enviado"
                                repository.updateRespuesta(i)
                                Log.d(_tag, "Foto enviado $i")
                            }
                            is OldNetworkRetrofit.Error -> {
                                changeHostServer()
                                Log.e(_tag, "Foto Error -> ${it.message}")
                            }
                        }
                    }
                }
            }*/
            return@withContext Result.success()
        }

    /*private fun requestBody(j: TRespuesta): RequestBody {
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
    }*/
}