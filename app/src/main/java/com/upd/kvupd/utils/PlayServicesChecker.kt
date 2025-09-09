package com.upd.kvupd.utils

import android.app.Activity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import javax.inject.Inject

class PlayServicesChecker @Inject constructor() {
    fun hayServiciosGoogle(activity: Activity): Boolean {
        val estado = GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(activity)

        return estado == ConnectionResult.SUCCESS
    }
}