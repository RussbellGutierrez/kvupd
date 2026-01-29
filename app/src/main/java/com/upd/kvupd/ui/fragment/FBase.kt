package com.upd.kvupd.ui.fragment

import android.os.Bundle
import android.util.Log
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
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.ui.sealed.TipoUsuario
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

        collectFlow(apiViewmodel.flowConfiguracion) { list ->
            list.firstOrNull()?.let { config ->
                parametrosConfig(config)
                showBotones(config)
            }
        }

        collectFlow(apiViewmodel.flowRutas) { rutas ->
            binding.txtRuta.text = rutas
        }

        binding.apply {
            txtVersion.text = ExtraInfo.obtener(InfoDispositivo.VERSION_APP)

            btnVendedor.setOnClickListener {
                verificarSesionVigente {
                    findNavController().navigate(R.id.action_FBase_to_FRastreo)
                }
            }
            btnCartera.setOnClickListener {
                verificarSesionVigente {
                    findNavController().navigate(R.id.action_FBase_to_FCartera)
                }
            }
            //btnCliente.setUI("v", true)
            //btnReporte.setUI("v", true)
            //btnEncuesta.setUI("v", true)
            //btnAlta.setUI("v", true)
            //btnBaja.setUI("v", true)
            //btnServidor.setUI("v", true)
            //agregar un fragment para enviar solicitudes de promociones
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.n_base_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.registro -> consume { findNavController().navigate(R.id.action_FBase_to_BDConfiguracion) }
        R.id.sincronizar -> consume { findNavController().navigate(R.id.action_FBase_to_DSincronizarDiario) }
        R.id.encuesta -> consume { }
        R.id.incidencia -> consume { }
        R.id.apagar -> consume { requireActivity().finishAndRemoveTask() }
        else -> false
    }

    private fun parametrosConfig(config: TableConfiguracion) = with(binding) {
        val tipo = TipoUsuario.nombreDesdeCodigo(config.tipo)
        val usuarioTipo = "$tipo - ${config.codigo}"
        txtUsuario.text = config.nombre
        txtEmpresa.text = config.nombreEmpresa()
        txtTipo.text = usuarioTipo
        imgEmit.setColorFilter(
            ContextCompat.getColor(requireContext(), config.colorSeguimiento())
        )
    }

    private fun showBotones(config: TableConfiguracion) {
        val tipo = TipoUsuario.inicialTipo(config.tipo)
        Log.d(_tag, "User type ${tipo.nombre()}")

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

    private fun configPorTipo(tipo: TipoUsuario): BotonesConfig =
        when (tipo) {
            TipoUsuario.Vendedor -> BotonesConfig(
                cliente = true,
                reporte = true,
                encuesta = true,
                alta = true,
                baja = true,
                servidor = true
            )

            TipoUsuario.Supervisor -> BotonesConfig(
                vendedor = true,
                cartera = true,
                reporte = true,
                encuesta = true,
                alta = true,
                baja = true,
                servidor = true
            )

            TipoUsuario.JefeVentas -> BotonesConfig(
                reporte = true,
                encuesta = true,
                alta = true,
                servidor = true
            )
        }

    /*override fun changeGPSstate(gps: Boolean) {
        val color = if (gps) Color.rgb(4, 106, 97) else Color.rgb(255, 51, 51)
        bind.fabGps.imageTintList = ColorStateList.valueOf(color)
    }*/

    /*private fun setParams(config: TConfiguracion) {
        if (config.tipo != "S") {
            bind.lnrVendedor.setUI("v", false)
            bind.txtRuta.setUI("v", true)
        }
        if (config.esquema == 8) {
            bind.lnrConsulta.setUI("v", true)
        } else {
            bind.lnrConsulta.setUI("v", false)
        }
        val img = if (config.empresa == 1) R.drawable.oriunda_logo else R.drawable.terranorte_logo
        val version = "ver. ${BuildConfig.VERSION_NAME}"
        val usuario = getUsuario(config)
        val gps =
            if (requireContext().isGPSDisabled()) Color.rgb(255, 51, 51) else Color.rgb(4, 106, 97)
        val seguimiento =
            if (config.seguimiento == 1) Color.rgb(4, 106, 97) else Color.rgb(221, 150, 6)
        bind.imgEmpresa.setImageResource(img)
        bind.txtUsuario.text = usuario
        bind.txtVersion.text = version
        bind.fabGps.imageTintList = ColorStateList.valueOf(gps)
        bind.fabEmit.imageTintList = ColorStateList.valueOf(seguimiento)
    }*/

    /*private fun setRuta(l: List<RowCliente>) {
        var mensaje = ""
        val rutas = arrayListOf<String>()
        l.forEach { i ->
            rutas.add(i.ruta.toString())
        }
        val rd = rutas.distinct()
        rd.forEach { i ->
            if (mensaje == "") {
                mensaje = "Ruta $i"
            } else {
                mensaje += " - Ruta $i"
            }
        }
        bind.txtRuta.text = mensaje
    }*/

    /*private fun getUsuario(item: TConfiguracion): String {
        return if (item.nombre == "") {
            when (item.tipo) {
                "S" -> "Supervisor de ventas - ${item.codigo}"
                else -> "Usuario de ventas - ${item.codigo}"
            }
        } else {
            "${item.nombre} - ${item.codigo}"
        }
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

    private fun verificarSesionVigente(f: () -> Unit) {
        if (localViewmodel.sesionActual.value) {
            f()
        }else {
            mostrarDialog(AppDialogType.Informativo(
                titulo = T_WARNING,
                mensaje = "Debe sincronizar primero la aplicacion"
            ))
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