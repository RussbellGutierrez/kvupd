package com.upd.kvupd.application.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.upd.kvupd.domain.OperationsFunctions
import com.upd.kvupd.domain.RoomFunctions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ActionsReceiver : BroadcastReceiver() {

    @Inject
    lateinit var operationsFunctions: OperationsFunctions

    @Inject
    lateinit var roomFunctions: RoomFunctions

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (intent.action) {

                    Intent.ACTION_TIME_CHANGED,
                    Intent.ACTION_TIMEZONE_CHANGED -> {

                        val config = roomFunctions.queryConfiguracion()
                            ?: return@launch

                        // 🔹 recalcular modo
                        operationsFunctions.syncInitial(config)

                        // 🔥 reprogramar alarmas (CLAVE)
                        operationsFunctions.reprogramBeforeConfig()
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}