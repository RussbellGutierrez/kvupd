package com.upd.kvupd.domain

import com.upd.kvupd.data.model.TIncidencia

interface OldOnInterSetup {
    fun onFinishWork(work: String)
    fun savingSystemReport(item: TIncidencia)
    fun showNotificationSystem(opt: Int)
    fun closeGPS()
    fun changeBetweenIconNotification(opt: Int)
    fun launchAgainProcess()
}