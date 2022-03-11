package com.upd.kventas.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.upd.kventas.databinding.ActivityMainBinding
import com.upd.kventas.service.ServiceFinish
import com.upd.kventas.service.ServicePosicion
import com.upd.kventas.service.ServiceSetup
import com.upd.kventas.utils.Constant.REQ_BACK_CODE
import com.upd.kventas.utils.Constant.REQ_CODE
import com.upd.kventas.utils.Interface.serviceListener
import com.upd.kventas.utils.Permission
import com.upd.kventas.utils.isServiceRunning
import com.upd.kventas.utils.snack
import com.upd.kventas.utils.toast
import com.upd.kventas.viewmodel.AppViewModel
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

    override fun onClosingActivity() {
        if (isServiceRunning(ServiceSetup::class.java))
            stopService(Intent(this, ServiceSetup::class.java))

        if (isServiceRunning(ServicePosicion::class.java))
            stopService(Intent(this, ServicePosicion::class.java))

        if (isServiceRunning(ServiceFinish::class.java))
            stopService(Intent(this, ServiceFinish::class.java))

        runOnUiThread {
            toast("Cerrando KVentas")
            finishAndRemoveTask()
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
}