package com.upd.kventas.application.work

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.*
import com.upd.kventas.data.model.Config
import com.upd.kventas.domain.Functions
import com.upd.kventas.domain.Repository
import com.upd.kventas.utils.Constant
import com.upd.kventas.utils.Constant.CONF
import com.upd.kventas.utils.Constant.MSG_USER
import com.upd.kventas.utils.Constant.W_USER
import com.upd.kventas.utils.Interface
import com.upd.kventas.utils.Interface.workListener
import com.upd.kventas.utils.toReqBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.HttpException

class UserWork @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val repository: Repository,
    private val functions: Functions
) : CoroutineWorker(appContext, workerParameters) {
    private val _tag by lazy { UserWork::class.java.simpleName }

    override suspend fun doWork(): Result =
        withContext(Dispatchers.IO) {
            lateinit var rst: Result
            val cli = repository.getClientes()
            val emp = repository.getEmpleados()
            val req = requestBody()
            when (CONF.tipo) {
                "V" -> if (cli.isNullOrEmpty()) {
                    try {
                        repository.getWebClientes(req).collect { response ->
                            val rsp = response.data?.jobl
                            rst = if (rsp.isNullOrEmpty()) {
                                MSG_USER = "Respuesta: ${response.message}"
                                Result.success()
                            } else {
                                repository.saveClientes(rsp)
                                MSG_USER = "Clientes descargados"
                                Result.success()
                            }
                        }
                    } catch (e: HttpException) {
                        println(e.message())
                        MSG_USER = e.message()
                        rst = Result.retry()
                    }
                } else {
                    MSG_USER = "Full"
                    rst = Result.success()
                }
                "S" -> if (emp.isNullOrEmpty()) {
                    try {
                        repository.getWebEmpleados(req).collect { response ->
                            val rsp = response.data?.jobl
                            rst = if (rsp.isNullOrEmpty()) {
                                MSG_USER = "Respuesta: ${response.message}"
                                Result.success()
                            } else {
                                repository.saveEmpleados(rsp)
                                MSG_USER = "Vendedores descargados"
                                Result.success()
                            }
                        }
                    } catch (e: HttpException) {
                        println(e.message())
                        MSG_USER = e.message()
                        rst = Result.retry()
                    }
                } else {
                    MSG_USER = "Full"
                    rst = Result.success()
                }
            }
            workListener?.onFinishWork(W_USER)
            return@withContext rst
        }

    private fun requestBody(): RequestBody {
        val json = JSONObject()
        json.put("empleado", CONF.codigo)
        json.put("empresa", CONF.empresa)
        if (CONF.tipo == "V")
            json.put("fecha", functions.dateToday(6))
        return json.toReqBody()
    }
}