package com.upd.kvupd.ui.dialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.upd.kvupd.databinding.BottomConfiguracionBinding
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.ui.sealed.ResultadoApi
import com.upd.kvupd.utils.ConstantsExtras.NO_FIND_UUID
import com.upd.kvupd.utils.InstanciaDialog
import com.upd.kvupd.utils.MaterialDialogTexto.T_ERROR
import com.upd.kvupd.utils.MaterialDialogTexto.T_SUCCESS
import com.upd.kvupd.utils.MaterialDialogTexto.T_WARNING
import com.upd.kvupd.utils.buildMaterialDialog
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.utils.obtenerMensajeListaJson
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.viewmodel.ALLViewModel
import com.upd.kvupd.viewmodel.APIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@AndroidEntryPoint
class BDConfiguracion : BottomSheetDialogFragment() {

    private val apiViewModel by activityViewModels<APIViewModel>()
    private val localViewModel by activityViewModels<ALLViewModel>()
    private val binding by viewBinding(BottomConfiguracionBinding::bind)
    private val _tag by lazy { BDConfiguracion::class.java.simpleName }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = BottomConfiguracionBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectFlow(apiViewModel.registerEvent) collect@{ resultado ->
            when (resultado) {
                is ResultadoApi.Loading -> mostrarDialog(
                    AppDialogType.Progreso(
                        mensaje = "Registrando equipo en Pedimap"
                    )
                )

                is ResultadoApi.Exito -> {
                    val mensaje = resultado.data?.obtenerMensajeListaJson("data", "message")
                    if (mensaje.isNullOrEmpty()) {
                        mostrarDialog(
                            AppDialogType.Informativo(
                                titulo = T_SUCCESS,
                                mensaje = "Usuario registrado en el servidor",
                                onPositive = { dismiss() }
                            )
                        )
                        return@collect
                    }

                    mostrarDialog(
                        AppDialogType.Informativo(
                            titulo = T_WARNING,
                            mensaje = mensaje,
                            onPositive = { dismiss() })
                    )
                }

                is ResultadoApi.ErrorHttp -> mostrarDialog(
                    AppDialogType.Informativo(
                        titulo = T_ERROR,
                        mensaje = "Error HTTP ${resultado.code}: ${resultado.mensaje}"
                    )
                )

                is ResultadoApi.Fallo -> mostrarDialog(
                    AppDialogType.Informativo(
                        titulo = T_ERROR,
                        mensaje = "Fallo: ${resultado.mensaje}"
                    )
                )
            }
        }

        collectFlow(localViewModel.pedimapMensaje) { mensaje ->
            enviarMensajeWhatsapp(mensaje)
        }

        binding.apply {
            autoEmpresa.setOnClickListener { autoEmpresa.showDropDown() }
            btnRegistrar.setOnClickListener { registerAndroidMovil() }
            btnEnviar.setOnClickListener { localViewModel.entregarRegistroPedimap() }
        }

        initUI()
    }

    private fun initUI() {
        binding.autoEmpresa.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                listOf("Terranorte", "Oriunda")
            )
        )

        binding.txtUuid.text =
            localViewModel.obtenerUUID().takeUnless { it.isNullOrBlank() }
                ?: NO_FIND_UUID
    }

    private fun registerAndroidMovil() {
        val empresa = binding.autoEmpresa.text.toString()
        val uuid = binding.txtUuid.text.toString()

        if (empresa.isEmpty()) {
            mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = "Debe seleccionar una empresa"
                )
            )
            return
        }

        if (uuid == NO_FIND_UUID) {
            mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = "No existe identificador generado previamente"
                )
            )
            return
        }

        apiViewModel.registrarEquipoServidor(uuid, empresa)
    }

    private fun enviarMensajeWhatsapp(mensaje: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, mensaje)
        }

        requireContext().startActivity(
            Intent.createChooser(intent, "Enviar mensaje por")
        )
    }

    private fun mostrarDialog(dialogType: AppDialogType) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            // Cerrar diálogo previo si existe
            InstanciaDialog.cerrarDialogActual()

            // Crear el dialog
            val dialog = buildMaterialDialog(requireContext(), dialogType)

            // Mostrarlo
            dialog.show()

            // Guardar referencia
            InstanciaDialog.REFERENCIA_DIALOG = WeakReference(dialog)
        }
    }
}