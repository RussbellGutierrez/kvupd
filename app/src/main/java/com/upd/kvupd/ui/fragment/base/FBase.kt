package com.upd.kvupd.ui.fragment.base

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
import com.google.android.material.button.MaterialButton
import com.upd.kvupd.R
import com.upd.kvupd.data.local.enumClass.InfoDispositivo
import com.upd.kvupd.data.model.BotonesConfig
import com.upd.kvupd.data.model.core.TableConfiguracion
import com.upd.kvupd.data.model.colorSeguimiento
import com.upd.kvupd.data.model.nombreEmpresa
import com.upd.kvupd.databinding.FragmentFBaseBinding
import com.upd.kvupd.domain.enumFile.TipoUsuario
import com.upd.kvupd.ui.fragment.base.modelUI.BotonInicioUI
import com.upd.kvupd.ui.fragment.base.sealed.EstadoSesion
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.utils.ExtraInfo
import com.upd.kvupd.utils.InstanciaDialog.REFERENCIA_DIALOG
import com.upd.kvupd.utils.InstanciaDialog.cerrarDialogActual
import com.upd.kvupd.utils.MaterialDialogTexto.T_WARNING
import com.upd.kvupd.utils.buildMaterialDialog
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.utils.visibleIf
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

            btnRastreo.navegarSeguro(R.id.action_FBase_to_FRastreo)

            btnCartera.navegarSeguro(R.id.action_FBase_to_FCartera)

            btnAlta.navegarSeguro(R.id.action_FBase_to_FAlta)

            btnBaja.navegarSeguro(R.id.action_FBase_to_FBaja)

            btnEncuesta.navegarSeguro(R.id.action_FBase_to_FEncuesta)

            btnReporte.navegarSeguro(R.id.action_FBase_to_FReporte)

            btnServidor.navegarSeguro(R.id.action_FBase_to_FServidor)
        }

        observeData()
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

    private fun observeData() {
        collectFlow(apiViewmodel.flowConfiguracion) { list ->
            val config = list.firstOrNull() ?: return@collectFlow
            parametrosConfig(config)
            localViewmodel.verificarFechaSesion()

            val tipo = TipoUsuario.fromCodigo(config.tipo)
            val cfg = configPorTipo(tipo)

            configurarBotonesVariables(tipo)

            binding.apply {
                btnRastreo.visibleIf(cfg.rastreo)
                btnCartera.visibleIf(cfg.cartera)
                btnReporte.visibleIf(cfg.reporte)
                btnEncuesta.visibleIf(cfg.encuesta)
                btnAlta.visibleIf(cfg.alta)
                btnBaja.visibleIf(cfg.baja)
                btnServidor.visibleIf(cfg.servidor)
            }
        }

        collectFlow(apiViewmodel.flowUIRutas) { rutas ->
            binding.txtRuta.text = rutas
        }
    }

    private fun configPorTipo(tipo: TipoUsuario): BotonesConfig =
        when (tipo) {
            TipoUsuario.VENDEDOR -> BotonesConfig(
                cartera = true,
                reporte = true,
                encuesta = true,
                alta = true,
                baja = true,
                servidor = true
            )

            TipoUsuario.SUPERVISOR -> BotonesConfig(
                rastreo = true,
                cartera = true,
                reporte = true,
                encuesta = true,
                alta = true,
                baja = true,
                servidor = true
            )

            TipoUsuario.JEFE_VENTAS -> BotonesConfig(
                rastreo = true,
                cartera = true,
                reporte = true,
                encuesta = true,
                alta = true,
                servidor = true
            )
        }

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
            cerrarDialogActual()

            val dialog = buildMaterialDialog(requireContext(), dialogType)
            dialog.show()

            REFERENCIA_DIALOG = WeakReference(dialog)
        }
    }

    private fun MaterialButton.aplicar(config: BotonInicioUI?) {
        visibleIf(config != null && config.visible)

        config ?: return

        text = config.texto
        setIconResource(config.icono)
    }

    private fun configurarBotonesVariables(tipo: TipoUsuario) = with(binding) {
        val botonRastreo: BotonInicioUI?
        val botonCartera: BotonInicioUI?

        when (tipo) {
            TipoUsuario.VENDEDOR -> {
                botonRastreo = null

                botonCartera = BotonInicioUI(
                    texto = "Administrar clientes",
                    icono = R.drawable.clientes
                )
            }

            TipoUsuario.SUPERVISOR -> {
                botonRastreo = BotonInicioUI(
                    texto = "Control de vendedores",
                    icono = R.drawable.rastreo
                )

                botonCartera = BotonInicioUI(
                    texto = "Visualizar clientes por vendedor",
                    icono = R.drawable.alzamano
                )
            }

            TipoUsuario.JEFE_VENTAS -> {
                botonRastreo = BotonInicioUI(
                    texto = "Control de personal",
                    icono = R.drawable.radar
                )

                botonCartera = BotonInicioUI(
                    texto = "Visualizar clientes por vendedor",
                    icono = R.drawable.alzamano
                )
            }
        }

        btnRastreo.aplicar(botonRastreo)
        btnCartera.aplicar(botonCartera)
    }
}