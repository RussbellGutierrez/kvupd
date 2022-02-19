package com.upd.kv.utils

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission
import com.upd.kv.utils.Constant.REQ_BACK_CODE
import com.upd.kv.utils.Constant.REQ_CODE
import javax.inject.Inject

class Permission @Inject constructor(private val act: Activity) {

    @RequiresApi(Build.VERSION_CODES.Q)
    private val perBackgroundLocation = Manifest.permission.ACCESS_BACKGROUND_LOCATION
    private val perAccessFineLocation = Manifest.permission.ACCESS_FINE_LOCATION
    private val perAccessCoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION
    private val perPhone = Manifest.permission.READ_PHONE_STATE
    private val perCamera = Manifest.permission.CAMERA
    private val perRead = Manifest.permission.READ_EXTERNAL_STORAGE
    private val perWrite = Manifest.permission.WRITE_EXTERNAL_STORAGE

    fun reqPerm() {
        act.requestPermissions(listPermission(),REQ_CODE)
    }

    private fun listPermission(): Array<String> {
        val lp = mutableListOf<String>()
        lp.add(perCamera)
        lp.add(perPhone)
        lp.add(perRead)
        lp.add(perWrite)
        when {
            Build.VERSION.SDK_INT > Build.VERSION_CODES.Q -> {
                lp.add(perAccessFineLocation)
                lp.add(perAccessCoarseLocation)
            }
            Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> {
                lp.add(perAccessFineLocation)
                lp.add(perAccessCoarseLocation)
                lp.add(perBackgroundLocation)
            }
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                lp.add(perAccessFineLocation)
                lp.add(perAccessCoarseLocation)
            }
        }
        return lp.toTypedArray()
    }

    fun reqBackPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            if ( checkSinglePermission(perAccessFineLocation) &&
                    checkSinglePermission(perAccessCoarseLocation)) {
                val backPermList = arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                AlertDialog.Builder(act)
                    .setTitle("Ubicacion Segundo Plano")
                    .setMessage("Otorgar permiso de localizacion en segundo plano para tener coordenadas actualizadas")
                    .setPositiveButton("Permitir") { _, _ ->
                        act.requestPermissions(
                            backPermList,
                            REQ_BACK_CODE
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

    private fun checkSinglePermission(permission: String) : Boolean {
        return checkSelfPermission(act, permission) == PERMISSION_GRANTED
    }
}