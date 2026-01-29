package com.upd.kvupd.application.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.upd.kvupd.domain.OperationsFunctions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WakeUpReceiver : BroadcastReceiver() {

    @Inject
    lateinit var operationsFunctions: OperationsFunctions

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> operationsFunctions.initBootWorker()
        }
    }
}