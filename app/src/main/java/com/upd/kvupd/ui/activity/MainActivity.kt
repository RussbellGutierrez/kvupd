package com.upd.kvupd.ui.activity

import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.upd.kvupd.databinding.ActivityMainBinding
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.ui.sealed.InitialState
import com.upd.kvupd.utils.InstanciaDialog
import com.upd.kvupd.utils.MaterialDialogTexto.T_ERROR
import com.upd.kvupd.utils.MaterialDialogTexto.T_SUCCESS
import com.upd.kvupd.utils.MaterialDialogTexto.T_WARNING
import com.upd.kvupd.utils.PermissionManager
import com.upd.kvupd.utils.buildMaterialDialog
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.utils.toast
import com.upd.kvupd.viewmodel.ALLViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val localViewModel by viewModels<ALLViewModel>()
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var navController: NavController

    @Inject
    lateinit var permissionManager: PermissionManager

    // Launcher centralizado
    private val permisosLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        val baseOk = permissionManager.checkBasePermissions()
        val backgroundOk = permissionManager.checkBackgroundLocationPermission()
        localViewModel.iniciarFlujo(this, baseOk, backgroundOk)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        observarEstadosEventosUUID()
        configurarNavegacion()

        localViewModel.iniciarFlujo(
            this,
            permissionManager.checkBasePermissions(),
            permissionManager.checkBackgroundLocationPermission()
        )
    }

    private fun observarEstadosEventosUUID() {
        collectFlow(localViewModel.uuidEstados) { state ->
            when (state) {
                is InitialState.Loading -> {
                    if (state.mensaje.isNotEmpty()) {
                        mostrarDialog(
                            AppDialogType.Progreso(
                                mensaje = state.mensaje
                            )
                        )
                    }
                }

                InitialState.NoGooglePlay ->
                    mostrarDialog(
                        AppDialogType.Informativo(
                            titulo = T_ERROR,
                            mensaje = "KVentas necesita los servicios de Google Play para ejecutarse.",
                            onPositive = { finishAndRemoveTask() }
                        )
                    )

                InitialState.NoBasePermissions -> lifecycleScope.launch {
                    permisosLauncher.launch(permissionManager.getBasePermissions())
                }

                InitialState.NoBackgroundLocationPermission -> lifecycleScope.launch {
                    permisosLauncher.launch(permissionManager.getBackgroundLocationPermission())
                }

                InitialState.NoUUID ->
                    mostrarDialog(
                        AppDialogType.Informativo(
                            titulo = T_WARNING,
                            mensaje = "El equipo no cuenta con un identificador, necesitamos crearlo.",
                            onPositive = { localViewModel.procesandoHashFirebase() }
                        )
                    )

                InitialState.CreatedUUID ->
                    mostrarDialog(
                        AppDialogType.Informativo(
                            titulo = T_SUCCESS,
                            mensaje = "Identificador creado correctamente, asigne y luego sincronice por favor."
                        )
                    )

                InitialState.FailCreateUUID ->
                    mostrarDialog(
                        AppDialogType.Informativo(
                            titulo = T_ERROR,
                            mensaje = "Ocurrio un error procesando el identificador. Desea intentarlo nuevamente?",
                            mostrarNegativo = true,
                            onPositive = { localViewModel.procesandoHashFirebase() },
                            onNegative = null
                        )
                    )

                InitialState.HasUUID -> toast("Bienvenido")
            }
        }
    }

    private fun configurarNavegacion() {
        navController = binding.navHostFragment
            .getFragment<NavHostFragment>().navController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    private fun mostrarDialog(dialogType: AppDialogType) {
        lifecycleScope.launch(Dispatchers.Main) {
            // Cerrar diálogo previo si existe
            InstanciaDialog.cerrarDialogActual()

            // Crear el dialog
            val dialog = buildMaterialDialog(this@MainActivity, dialogType)

            // Mostrarlo
            dialog.show()

            // Guardar referencia
            InstanciaDialog.REFERENCIA_DIALOG = WeakReference(dialog)
        }
    }
}