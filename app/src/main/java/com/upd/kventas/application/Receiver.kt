package com.upd.kventas.application

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.upd.kventas.domain.Functions
import com.upd.kventas.domain.Repository
import dagger.hilt.android.AndroidEntryPoint
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
            Intent.ACTION_PACKAGE_REPLACED -> functions.executeService("setup",true)
            Intent.ACTION_PACKAGE_RESTARTED -> functions.executeService("setup",true)
            Intent.ACTION_MY_PACKAGE_SUSPENDED -> functions.executeService("setup",true)
        }
    }
}