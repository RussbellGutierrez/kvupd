package com.upd.kvupd.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import com.upd.kvupd.utils.Constant.REQ_CODE
import javax.inject.Inject

class Permission @Inject constructor(private val act: Activity) {

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private val perDataSync = Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val perPostNotification = Manifest.permission.POST_NOTIFICATIONS

    @RequiresApi(Build.VERSION_CODES.Q)
    private val perBackgroundLocation = Manifest.permission.ACCESS_BACKGROUND_LOCATION
    private val perAccessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION
    private val perAccessCoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION
    private val perPhone = Manifest.permission.READ_PHONE_STATE
    private val perCamera = Manifest.permission.CAMERA
    private val perRead = Manifest.permission.READ_EXTERNAL_STORAGE
    private val perWrite = Manifest.permission.WRITE_EXTERNAL_STORAGE

    fun reqPerm() {
        act.requestPermissions(listPermission(), REQ_CODE)
    }

    private fun listPermission(): Array<String> {
        val lp = mutableListOf<String>()
        lp.add(perCamera)
        lp.add(perPhone)

        //NOTA: A partir de la version Q, el access_background_location debe llamarse luego
        //de llamar los demas permisos, sino se anularan y no mostrara nada
        when {
            Build.VERSION.SDK_INT == Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                lp.add(perAccessFineLocation)
                lp.add(perAccessCoarseLocation)
                lp.add(perPostNotification)
                lp.add(perDataSync)
            }

            Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU -> {
                lp.add(perAccessFineLocation)
                lp.add(perAccessCoarseLocation)
                lp.add(perPostNotification)
            }

            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU &&
                    Build.VERSION.SDK_INT > Build.VERSION_CODES.Q -> {
                lp.add(perAccessFineLocation)
                lp.add(perAccessCoarseLocation)
            }

            Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q -> {
                lp.add(perRead)
                lp.add(perWrite)
                lp.add(perAccessFineLocation)
                lp.add(perAccessCoarseLocation)
            }
        }
        return lp.toTypedArray()
    }

    fun checkAccessBackground(): Boolean {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            return checkSinglePermission(perBackgroundLocation)
        }
        return true
    }

    fun reqBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if (checkSinglePermission(perAccessFineLocation) &&
                checkSinglePermission(perAccessCoarseLocation)
            ) {
                AlertDialog.Builder(act)
                    .setTitle("Ubicacion Segundo Plano")
                    .setMessage("Otorgar permiso de localizacion en segundo plano para tener coordenadas actualizadas")
                    .setPositiveButton("Permitir") { _, _ ->
                        act.requestPermissions(
                            listOf(perBackgroundLocation).toTypedArray(),
                            REQ_CODE
                        )
                    }
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
        }
    }

    fun deniedPermissions() {
        AlertDialog.Builder(act)
            .setTitle("Permisos Denegados")
            .setMessage("No se otorgaron todos los permisos necesarios. Por favor, otórgalos para el correcto funcionamiento.")
            .setPositiveButton("Aceptar permisos") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", act.packageName, null)
                intent.data = uri
                act.startActivity(intent)
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    fun checkAllPermissions(): Boolean {
        val permisos = listPermission()
        permisos.forEach { y ->
            if (!checkSinglePermission(y)) {
                return false
            }
        }
        return true
    }

    private fun checkSinglePermission(permission: String): Boolean {
        return checkSelfPermission(act, permission) == PERMISSION_GRANTED
    }
}