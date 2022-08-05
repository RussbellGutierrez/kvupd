package com.upd.kvupd.application

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.util.Log
import com.upd.kvupd.domain.Functions
import com.upd.kvupd.domain.Repository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class Receiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: Repository
    @Inject
    lateinit var functions: Functions

    override fun onReceive(p0: Context, p1: Intent) {
        when (p1.action) {
            Intent.ACTION_BOOT_COMPLETED -> functions.executeService("setup",true)
            Intent.ACTION_REBOOT -> functions.executeService("setup",true)
            Intent.ACTION_PACKAGE_RESTARTED -> functions.executeService("setup",true)
            Intent.ACTION_MY_PACKAGE_REPLACED -> functions.executeService("setup",true)
            Intent.ACTION_MY_PACKAGE_SUSPENDED -> functions.executeService("setup",true)
            Intent.ACTION_TIME_CHANGED -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val item = functions.saveSystemActions("TIME",null)
                    if (item != null) {
                        repository.saveIncidencia(item)
                    }
                }
            }
            LocationManager.PROVIDERS_CHANGED_ACTION -> {
                CoroutineScope(Dispatchers.IO).launch {
                    val item = functions.saveSystemActions("GPS",null)
                    if (item != null) {
                        repository.saveIncidencia(item)
                    }
                }
            }
        }
    }

}