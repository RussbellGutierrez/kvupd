package com.upd.kvupd.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.upd.kvupd.databinding.ActivityMainBinding
import com.upd.kvupd.domain.OnClosingApp
import com.upd.kvupd.service.ServiceFinish
import com.upd.kvupd.service.ServicePosicion
import com.upd.kvupd.service.ServiceSetup
import com.upd.kvupd.utils.Constant.IS_CONFIG_FAILED
import com.upd.kvupd.utils.Constant.IS_SUNDAY
import com.upd.kvupd.utils.Constant.REQ_BACK_CODE
import com.upd.kvupd.utils.Constant.REQ_CODE
import com.upd.kvupd.utils.Interface.closeListener
import com.upd.kvupd.utils.Interface.interListener
import com.upd.kvupd.utils.Permission
import com.upd.kvupd.utils.isServiceRunning
import com.upd.kvupd.utils.showDialog
import com.upd.kvupd.utils.snack
import com.upd.kvupd.utils.toast
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnClosingApp {

    private val viewModel by viewModels<AppViewModel>()
    private lateinit var bind: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var permission: Permission

    override fun onDestroy() {
        super.onDestroy()
        closeListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
        setSupportActionBar(bind.toolbar)
        setupApp()
        closeListener = this
    }

    override fun closingActivity(notRegister: Boolean) {
        if (isServiceRunning(ServicePosicion::class.java))
            stopService(Intent(this, ServicePosicion::class.java))

        if (isServiceRunning(ServiceFinish::class.java))
            stopService(Intent(this, ServiceFinish::class.java))

        interListener?.closeGPS()

        runOnUiThread {
            if (notRegister) {
                showDialog(
                    "error",
                    "Revise en la lista de dispositivos si está registrado su equipo celular"
                ) {
                    finishAndRemoveTask()
                }
            } else {
                if (!IS_SUNDAY && IS_CONFIG_FAILED) {
                    showDialog(
                        "error",
                        "Al parecer se elimino su registro, consulte con sistemas"
                    ) {
                        finishAndRemoveTask()
                    }
                } else {
                    toast("Cerrando KVentas")
                    finishAndRemoveTask()
                }
            }
        }
    }

    override fun closeServiceSetup() {
        if (isServiceRunning(ServiceSetup::class.java))
            stopService(Intent(this, ServiceSetup::class.java))
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
        when (requestCode) {
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
}