package com.upd.kvupd.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val basePermissions: List<String> by lazy {
        buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                add(Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            add(Manifest.permission.CAMERA)
        }
    }

    private val backgroundLocationPermission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        else emptyList()

    // Verifica si TODOS los permisos base están concedidos
    fun checkBasePermissions(): Boolean {
        return basePermissions.all { perm ->
            ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Verifica permiso de ubicación en segundo plano
    fun checkBackgroundLocationPermission(): Boolean {
        if (backgroundLocationPermission.isEmpty()) return true
        return backgroundLocationPermission.all { perm ->
            ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun getBasePermissions(): Array<String> = basePermissions.toTypedArray()

    fun getBackgroundLocationPermission(): Array<String> =
        backgroundLocationPermission.toTypedArray()
}