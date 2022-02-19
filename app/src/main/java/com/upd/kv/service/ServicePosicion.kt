package com.upd.kv.service

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ServicePosicion: LifecycleService() {

    private val _tag by lazy { ServicePosicion::class.java.simpleName }

    override fun onCreate() {
        super.onCreate()
        Log.d(_tag,"Service posicion launch")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }
}