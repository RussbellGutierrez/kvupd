package com.upd.kvupd.application.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.upd.kvupd.domain.OperationsFunctions
import com.upd.kvupd.domain.RoomFunctions
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class BootStartWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val roomFunctions: RoomFunctions,
    private val operationsFunctions: OperationsFunctions
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        val config = roomFunctions.queryConfiguracion()

        if (config != null) {
            operationsFunctions.syncInitial(config)
            operationsFunctions.reprogramBeforeConfig()
        }

        operationsFunctions.syncCoreCsv()

        return Result.success()
    }
}