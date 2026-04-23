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

            // Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }

            // Ubicación principal
            add(Manifest.permission.ACCESS_FINE_LOCATION)

            // Cámara
            add(Manifest.permission.CAMERA)
        }
    }

    private val backgroundLocationPermission: List<String> by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            emptyList()
        }
    }

    // Verifica permisos base
    fun checkBasePermissions(): Boolean {
        return basePermissions.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Verifica ubicación en segundo plano
    fun checkBackgroundLocationPermission(): Boolean {
        return backgroundLocationPermission.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    // Para solicitar permisos base
    fun getBasePermissions(): Array<String> =
        basePermissions.toTypedArray()

    // Para solicitar background location después
    fun getBackgroundLocationPermission(): Array<String> =
        backgroundLocationPermission.toTypedArray()
}