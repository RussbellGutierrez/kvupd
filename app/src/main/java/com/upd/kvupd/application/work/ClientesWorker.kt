package com.upd.kvupd.application.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.upd.kvupd.domain.IdentityImplementation
import com.upd.kvupd.domain.JsObFunctions
import com.upd.kvupd.domain.ServerFunctions
import com.upd.kvupd.utils.OldConstant.CONF
import com.upd.kvupd.utils.toReqBody
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.RequestBody
import org.json.JSONObject

@HiltWorker
class ClientesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val generalImplementation: IdentityImplementation,
    private val serverFunctions: ServerFunctions,
    private val jsobFunctions: JsObFunctions
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            val config = generalImplementation.
            val json = jsobFunctions.jsonObjectClientes()
            serverFunctions.apiDownloadCliente()
        } catch (e: Exception) {
            Result.failure(workDataOf("error" to (e.message ?: "Error desconocido")))
        }
    }

    /*override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            lateinit var rst: Result
            val cli = repository.getClientes()
            val emp = repository.getEmpleados()
            val req = requestBody()
            Log.d(_tag,"Value $CONF")
            when (CONF.tipo) {
                "V" -> if (cli.isEmpty()) {
                    try {
                        repository.getWebClientes(req).collect { response ->
                            val rsp = response.data?.jobl
                            rst = if (rsp.isNullOrEmpty()) {
                                MSG_USER = response.message!!
                                Result.success()
                            } else {
                                repository.saveClientes(rsp)
                                MSG_USER = "* Clientes descargados"
                                Result.success()
                            }
                        }
                    } catch (e: HttpException) {
                        println(e.message())
                        MSG_USER = e.message()
                        rst = Result.retry()
                    }
                } else {
                    MSG_USER = "* Full"
                    rst = Result.success()
                }
                else -> if (emp.isEmpty()) {
                    try {
                        repository.getWebEmpleados(req).collect { response ->
                            val rsp = response.data?.jobl
                            rst = if (rsp.isNullOrEmpty()) {
                                MSG_USER = "Respuesta: ${response.message}"
                                Result.success()
                            } else {
                                repository.saveEmpleados(rsp)
                                MSG_USER = "* Vendedores descargados"
                                Result.success()
                            }
                        }
                    } catch (e: HttpException) {
                        println(e.message())
                        MSG_USER = e.message()
                        rst = Result.retry()
                    }
                } else {
                    MSG_USER = "* Full"
                    rst = Result.success()
                }
            }
            interListener?.onFinishWork(W_USER)
            return@withContext Result.success()//rst
        }*/

    private fun requestBody(): RequestBody {
        val json = JSONObject()
        json.put("empleado", CONF.codigo)
        json.put("empresa", CONF.empresa)
        return json.toReqBody()
    }
}