package com.upd.kvupd.application.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.upd.kvupd.domain.OperationsFunctions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ActionsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var operationsFunctions: OperationsFunctions

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        when (intent.action) {

            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                operationsFunctions.syncInitial()
            }
        }
    }
}