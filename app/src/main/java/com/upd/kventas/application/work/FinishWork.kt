package com.upd.kventas.application.work

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.upd.kventas.domain.Functions
import com.upd.kventas.domain.Repository
import com.upd.kventas.utils.Network
import com.upd.kventas.utils.toReqBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class FinishWork @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val functions: Functions
) : Worker(appContext, workerParameters) {
    private val _tag by lazy { FinishWork::class.java.simpleName }

    override fun doWork(): Result {
        Log.e(_tag,"Finish Work Launch")
        functions.executeService("finish",false)
        return Result.success()
    }
}