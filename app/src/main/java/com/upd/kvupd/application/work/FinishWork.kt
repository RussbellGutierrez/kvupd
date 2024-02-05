package com.upd.kvupd.application.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.upd.kvupd.domain.Functions
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class FinishWork @AssistedInject constructor(
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