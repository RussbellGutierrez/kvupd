package com.upd.kvupd.application.work

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.upd.kvupd.domain.Functions

class SetupWork @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val functions: Functions
) : Worker(appContext, workerParameters) {
    private val _tag by lazy { SetupWork::class.java.simpleName }

    override fun doWork(): Result {
        Log.w(_tag,"Setup Work Launch")
        functions.executeService("setup",true)
        return Result.success()
    }
}