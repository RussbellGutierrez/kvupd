package com.upd.kvupd.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.upd.kvupd.R
import com.upd.kvupd.databinding.ActivityMainBinding
import com.upd.kvupd.service.ServiceFinish
import com.upd.kvupd.service.ServicePosicion
import com.upd.kvupd.service.ServiceSetup
import com.upd.kvupd.utils.*
import com.upd.kvupd.utils.Constant.IS_CONFIG_FAILED
import com.upd.kvupd.utils.Constant.IS_SUNDAY
import com.upd.kvupd.utils.Constant.REQ_BACK_CODE
import com.upd.kvupd.utils.Constant.REQ_CODE
import com.upd.kvupd.utils.Interface.serviceListener
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ServiceSetup.OnServiceListener {

    private val viewModel by viewModels<AppViewModel>()
    private lateinit var bind: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var permission: Permission

    override fun onDestroy() {
        super.onDestroy()
        serviceListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setSupportActionBar(bind.toolbar)
        setupApp()
        serviceListener = this
    }

    override fun onClosingActivity(notRegister: Boolean) {
        if (isServiceRunning(ServiceSetup::class.java))
            stopService(Intent(this, ServiceSetup::class.java))

        if (isServiceRunning(ServicePosicion::class.java))
            stopService(Intent(this, ServicePosicion::class.java))

        if (isServiceRunning(ServiceFinish::class.java))
            stopService(Intent(this, ServiceFinish::class.java))

        runOnUiThread {
            if (notRegister) {
                showDialog("error","Revise en la lista de dispositivos si está registrado su equipo celular") {
                    finishAndRemoveTask()
                }
            } else {
                if (!IS_SUNDAY && IS_CONFIG_FAILED) {
                    showDialog("error","Al parecer se elimino su registro, consulte con sistemas") {
                        finishAndRemoveTask()
                    }
                }else {
                    toast("Cerrando KVentas")
                    finishAndRemoveTask()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            REQ_CODE -> permission.reqBackPermission()
            REQ_BACK_CODE -> snack("GPS segundo plano")
        }
    }

    private fun setupApp() {
        if (isPlayServicesEnabled()) {
            permission.reqPerm()
            setUpNavController()
        }
    }

    private fun setUpNavController() {
        navController = bind.navHostFragment.getFragment<NavHostFragment>().navController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    private fun isPlayServicesEnabled(): Boolean {
        val api = GoogleApiAvailability()
        val rc = api.isGooglePlayServicesAvailable(this)
        if (rc != ConnectionResult.SUCCESS) {
            if (api.isUserResolvableError(rc)) {
                toast("KVentas necesita los servicios de GooglePlay para ejecutarse")
            }
            finishAndRemoveTask()
            return false
        }
        return true
    }

    interface OnMainListener {
        fun changeGPSstate(gps: Boolean)
    }

}