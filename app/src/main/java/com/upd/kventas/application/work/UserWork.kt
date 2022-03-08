package com.upd.kventas.application.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.*
import com.upd.kventas.data.model.Config
import com.upd.kventas.domain.Functions
import com.upd.kventas.domain.Repository
import com.upd.kventas.utils.Constant.MSG_USER
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
            val conf = repository.getConfig()[0]
            val cli = repository.getClientes()
            val emp = repository.getEmpleados()
            val req = requestBody(conf)
            when (conf.tipo) {
                "V" -> if (cli.isNullOrEmpty()) {
                    try {
                        repository.getWebClientes(req).collect { response ->
                            val rsp = response.data?.jobl
                            rst = if (rsp.isNullOrEmpty()) {
                                MSG_USER = "Respuesta vacia"
                                Result.failure()
                            } else {
                                repository.saveClientes(rsp)
                                MSG_USER = "Clientes descargados"
                                Result.success()
                            }
                        }
                    } catch (e: HttpException) {
                        println(e.message())
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
                                MSG_USER = "Respuesta vacia"
                                Result.failure()
                            } else {
                                repository.saveEmpleados(rsp)
                                MSG_USER = "Vendedores descargados"
                                Result.success()
                            }
                        }
                    } catch (e: HttpException) {
                        println(e.message())
                        rst = Result.retry()
                    }
                } else {
                    MSG_USER = "Full"
                    rst = Result.success()
                }
            }
            return@withContext rst
        }

    private fun requestBody(conf: Config): RequestBody {
        val json = JSONObject()
        json.put("empleado", conf.codigo)
        json.put("empresa", conf.empresa)
        json.put("fecha", functions.dateToday(6))
        return json.toReqBody()
    }
}