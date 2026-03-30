package com.upd.kvupd.ui.dialog

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.upd.kvupd.databinding.CustomDialogProgressbarBinding
import com.upd.kvupd.utils.SharedPreferenceKeys.KEY_SYNC_INIT
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.utils.geo.GeoManager
import com.upd.kvupd.utils.gone
import com.upd.kvupd.utils.observeWorkersById
import com.upd.kvupd.utils.setResume
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.utils.visible
import com.upd.kvupd.viewmodel.ALLViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class DSincronizarDiario : DialogFragment() {

    private val localViewModel by activityViewModels<ALLViewModel>()
    private val binding by viewBinding(CustomDialogProgressbarBinding::bind)
    private val _tag by lazy { DSincronizarDiario::class.java.simpleName }

    @Inject
    lateinit var workManager: WorkManager

    @Inject
    lateinit var preferences: SharedPreferences

    override fun onStart() {
        super.onStart()
        setResume(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = CustomDialogProgressbarBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false

        binding.btnProcesar.setOnClickListener {
            iniciarSincronizacion()
        }

        binding.btnCancelar.setOnClickListener {
            dismiss()
        }

        collectFlow(localViewModel.remainingWorkersIds) { remainingIds ->
            if (remainingIds.isNotEmpty()) {
                observarWorkersRestantes(remainingIds)
            } else {
                mostrarErrorFinal("No se pudieron lanzar los workers restantes.")
            }
        }

        collectFlow(localViewModel.configMensaje) { mensaje ->
            binding.txtMensaje.visible()
            binding.txtMensaje.text = mensaje
        }
    }

    private fun iniciarSincronizacion() {
        Log.i(_tag, "🚀 Iniciando sincronización diaria...")

        // 🔹 Resetear UI y mostrar solo progressbar
        stateUI(inProgress = true)

        // 🔹 Lanza el worker de configuración y observa su ID
        val configId = localViewModel.ejecutandoWorkerInicial()
        observarWorkerConfiguracion(configId)
    }

    private fun observarWorkerConfiguracion(configId: UUID) {
        Log.d(_tag, "🟢 Observando ConfiguracionWorker ($configId)")

        observeWorkersById(
            workManager,
            viewLifecycleOwner,
            listOf(configId)
        ) { _, info, _, mensaje ->
            val estado = mensaje.ifBlank { info.progress.getString("estado") ?: "" }
            val error = info.outputData.getString("error")
            val state = info.state

            if (estado.isNotBlank()) {
                binding.txtProgreso.text = estado
                Log.i(_tag, "📡 Configuración: $estado")
            }

            // ❌ Error
            if (state.isFinished && error != null) {
                mostrarErrorFinal("Error en configuración: $error")
                Log.e(_tag, "❌ Configuración fallida: $error")
                return@observeWorkersById
            }

            // ✅ Configuración completada
            if (state == WorkInfo.State.SUCCEEDED) {
                Log.i(_tag, "✅ Configuración completada correctamente")
                localViewModel.ejecutandoWorkersRestantes()
            }
        }
    }

    private fun observarWorkersRestantes(workerIds: List<UUID>) {
        val completados = mutableSetOf<UUID>()
        var falloDetectado = false
        var mensajeMostrado = false

        Log.i(_tag, "🟢 Observando ${workerIds.size} workers restantes...")

        observeWorkersById(workManager, viewLifecycleOwner, workerIds) { id, workInfo, _, mensaje ->
            val error = workInfo.outputData.getString("error")
            val estado = mensaje.ifBlank { workInfo.progress.getString("estado") ?: "" }
            val state = workInfo.state

            with(binding) {
                // 🔹 Mostrar progreso en curso (solo si no hubo error)
                if (estado.isNotBlank() && !falloDetectado) {
                    txtProgreso.text = estado
                    linear1.visible()
                    txtMensaje.gone() // Oculto hasta el final
                }

                when {
                    // ❌ Error detectado
                    state.isFinished && error != null -> {
                        falloDetectado = true
                        mostrarErrorFinal("Error en worker: $error")
                        Log.e(_tag, "✖ FAILED ($id) -> $error")
                    }

                    // ✔ Completado correctamente
                    state == WorkInfo.State.SUCCEEDED && completados.add(id) -> {
                        val pct = ((completados.size.toFloat() / workerIds.size) * 100f).toInt()
                        pbLinear.progress = pct
                        txtPorcentaje.text = "$pct%"
                        Log.i(_tag, "✓ SUCCEEDED: $id ($pct%)")
                    }
                }

                // 🏁 Todos completados exitosamente
                if (!falloDetectado && completados.size == workerIds.size && !mensajeMostrado) {
                    mensajeMostrado = true
                    mostrarFinalExitoso()
                }
            }
        }
    }

    private fun stateUI(inProgress: Boolean) = with(binding) {
        if (inProgress) {
            // 🔹 Resetear todo antes de empezar
            txtPorcentaje.text = "0%"
            txtMensaje.text = ""
            txtProgreso.text = ""
            pbLinear.progress = 0

            // 🔹 Mostrar solo el contenedor del progress
            linear1.visible()

            // 🔹 Ocultar botones y mensaje final
            listOf(txtMensaje, btnProcesar, btnCancelar).forEach {
                it.gone()
            }
        } else {
            // 🔹 Mantener progress visible
            linear1.visible()

            // 🔹 Mostrar botones nuevamente
            listOf(btnProcesar, btnCancelar).forEach {
                it.visible()
            }
        }
    }

    private fun mostrarErrorFinal(mensaje: String) {
        with(binding) {
            pbLinear.progress = 0
            txtPorcentaje.text = "0%"
            txtProgreso.text = ""
            txtMensaje.text = "❌ $mensaje"
            txtMensaje.visible()
        }
        stateUI(inProgress = false)
    }

    private fun mostrarFinalExitoso() {
        with(binding) {
            pbLinear.progress = 100
            txtPorcentaje.text = "100%"
            txtProgreso.text = ""
            txtMensaje.text = "✅ Sincronización completada exitosamente"
            txtMensaje.visible()
        }
        stateUI(inProgress = false)

        // 🔥 Cargar Geo
        lifecycleScope.launch(Dispatchers.IO) {
            GeoManager.load(requireContext())
        }

        val inicializado = preferences.getBoolean(KEY_SYNC_INIT, false)

        if (!inicializado) {
            // 🔴 Primera vez: arranca todo el sistema
            localViewModel.ejecutarSyncInicial()
        } else {
            // 🟢 Días siguientes: solo reprograma alarmas
            localViewModel.reprogramarUsandoConfig()
        }
    }
}