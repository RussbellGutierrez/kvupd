package com.upd.kvupd.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.upd.kvupd.R
import com.upd.kvupd.data.local.enumClass.InfoDispositivo
import com.upd.kvupd.data.model.BotonesConfig
import com.upd.kvupd.data.model.TableConfiguracion
import com.upd.kvupd.data.model.colorSeguimiento
import com.upd.kvupd.data.model.nombreEmpresa
import com.upd.kvupd.databinding.FragmentFBaseBinding
import com.upd.kvupd.ui.fragment.enumClass.TipoUsuario
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.ui.sealed.EstadoSesion
import com.upd.kvupd.utils.ExtraInfo
import com.upd.kvupd.utils.InstanciaDialog
import com.upd.kvupd.utils.MaterialDialogTexto.T_WARNING
import com.upd.kvupd.utils.buildMaterialDialog
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.viewmodel.ALLViewModel
import com.upd.kvupd.viewmodel.APIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.ref.WeakReference

@AndroidEntryPoint
class FBase : Fragment(), MenuProvider {

    private val localViewmodel by activityViewModels<ALLViewModel>()
    private val apiViewmodel by activityViewModels<APIViewModel>()
    private val binding by viewBinding(FragmentFBaseBinding::bind)
    private val _tag by lazy { FBase::class.java.simpleName }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFBaseBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding.apply {
            txtVersion.text = ExtraInfo.obtener(InfoDispositivo.VERSION_APP)

            btnVendedor.navegarSeguro(R.id.action_FBase_to_FRastreo)

            btnCartera.navegarSeguro(R.id.action_FBase_to_FCartera)

            btnCliente.navegarSeguro(R.id.action_FBase_to_FCartera)

            btnAlta.navegarSeguro(R.id.action_FBase_to_FAlta)

            btnBaja.navegarSeguro(R.id.action_FBase_to_FBaja)

            //btnCliente.setUI("v", true)
            //btnReporte.setUI("v", true)
            //btnEncuesta.setUI("v", true)
            //btnAlta.setUI("v", true)
            //btnServidor.setUI("v", true)
            //agregar un fragment para enviar solicitudes de promociones
        }

        collectFlows()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.n_base_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.registro -> consume { findNavController().navigate(R.id.action_FBase_to_BDConfiguracion) }
        R.id.sincronizar -> consume { findNavController().navigate(R.id.action_FBase_to_DSincronizarDiario) }
        R.id.encuesta -> consume { }
        R.id.apagar -> consume { requireActivity().finishAndRemoveTask() }
        else -> false
    }

    private fun parametrosConfig(config: TableConfiguracion) = with(binding) {
        val tipo = TipoUsuario.fromCodigo(config.tipo)
        val usuarioTipo = "$tipo - ${config.codigo}"
        txtUsuario.text = config.nombre
        txtEmpresa.text = config.nombreEmpresa()
        txtTipo.text = usuarioTipo
        imgEmit.setColorFilter(
            ContextCompat.getColor(requireContext(), config.colorSeguimiento())
        )
    }

    private fun collectFlows() {
        collectFlow(apiViewmodel.flowConfiguracion) { list ->
            val config = list.firstOrNull() ?: return@collectFlow
            parametrosConfig(config)
            localViewmodel.verificarFechaSesion()
        }

        collectFlow(apiViewmodel.flowRutas) { rutas ->
            binding.txtRuta.text = rutas
        }

        collectFlow(localViewmodel.tipoUsuario) { tipo ->
            if (tipo == null) return@collectFlow

            val cfg = configPorTipo(tipo)

            binding.apply {
                btnVendedor.setUI("v", cfg.vendedor)
                btnCartera.setUI("v", cfg.cartera)
                btnCliente.setUI("v", cfg.cliente)
                btnReporte.setUI("v", cfg.reporte)
                btnEncuesta.setUI("v", cfg.encuesta)
                btnAlta.setUI("v", cfg.alta)
                btnBaja.setUI("v", cfg.baja)
                btnServidor.setUI("v", cfg.servidor)
            }
        }
    }

    private fun configPorTipo(tipo: TipoUsuario): BotonesConfig =
        when (tipo) {
            TipoUsuario.VENDEDOR -> BotonesConfig(
                cliente = true,
                reporte = true,
                encuesta = true,
                alta = true,
                baja = true,
                servidor = true
            )

            TipoUsuario.SUPERVISOR -> BotonesConfig(
                vendedor = true,
                cartera = true,
                reporte = true,
                encuesta = true,
                alta = true,
                baja = true,
                servidor = true
            )

            TipoUsuario.JEFE_VENTAS -> BotonesConfig(
                reporte = true,
                encuesta = true,
                alta = true,
                servidor = true /// agregar rastreo para ver supervisor y vendedores
            )
        }

    /*override fun changeGPSstate(gps: Boolean) {
        val color = if (gps) Color.rgb(4, 106, 97) else Color.rgb(255, 51, 51)
        bind.fabGps.imageTintList = ColorStateList.valueOf(color)
    }*/

    private fun launchEncuesta() {
        val p = JSONObject()
        //p.put("empleado", CONF.codigo)
        //p.put("empresa", CONF.empresa)
        //progress("Descargando encuesta")
        //viewmodel.fetchEncuesta(p.toReqBody())
    }

    /*private fun sinchroData() {
        if (viewmodel.internetAvailable()) {
            progress("Sincronizando datos")
            viewmodel.fetchSinchro()
        } else {
            snack("No tenemos señal de internet")
        }
    }*/


    /*private fun checkEncuestaSelect() {
        lifecycleScope.launch {
            // Esperamos resultado de la función suspendida
            val sinEncuesta = viewmodel.isEncuestaEmpty()

            if (sinEncuesta) {
                snack("No hay encuestas disponibles para procesar")
                return@launch
            }

            if (isCONFinitialized()) {
                when (CONF.tipo) {
                    "V" -> findNavController().navigate(R.id.action_FBase_to_FAlterno)
                    "S" -> viewmodel.checkingEncuesta {
                        if (it) {
                            findNavController().navigate(R.id.action_FBase_to_FAlterno)
                        } else {
                            snack("Debe elegir una encuesta primero")
                        }
                    }
                }
            }
        }
    }*/

    private fun View.navegarSeguro(action: Int) {
        setOnClickListener {
            verificarSesionVigente {
                findNavController().navigate(action)
            }
        }
    }

    private fun verificarSesionVigente(f: () -> Unit) {
        when (localViewmodel.sesionEstado.value) {
            EstadoSesion.Valida -> f()

            EstadoSesion.Invalida -> {
                mostrarDialog(
                    AppDialogType.Informativo(
                        titulo = T_WARNING,
                        mensaje = "Debe sincronizar primero la aplicacion"
                    )
                )
            }

            EstadoSesion.Loading -> Unit
        }
    }

    private fun mostrarDialog(dialogType: AppDialogType) {
        lifecycleScope.launch(Dispatchers.Main) {
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