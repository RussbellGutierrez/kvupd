package com.upd.kvupd.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.upd.kvupd.BuildConfig
import com.upd.kvupd.R
import com.upd.kvupd.data.model.RowCliente
import com.upd.kvupd.data.model.TConfiguracion
import com.upd.kvupd.databinding.FragmentFBaseBinding
import com.upd.kvupd.ui.activity.MainActivity
import com.upd.kvupd.utils.*
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Interface.mainListener
import com.upd.kvupd.utils.Interface.serviceListener
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*

@AndroidEntryPoint
class FBase : Fragment(), MainActivity.OnMainListener, MenuProvider {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFBaseBinding? = null
    private val bind get() = _bind!!
    private var opt = 0
    private var sincro = 0
    private val _tag by lazy { FBase::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
        mainListener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainListener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentFBaseBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewLifecycleOwner.lifecycleScope.launch {
            viewmodel.setupApp { findNavController().navigate(R.id.action_FBase_to_FAjuste) }
        }

        bind.fabVendedor.setOnClickListener {
            checkingGPS {
                opt = 1
                viewmodel.dataDownloaded()
            }
        }
        bind.fabCliente.setOnClickListener {
            checkingGPS {
                opt = 2
                viewmodel.dataDownloaded()
            }
        }
        bind.fabReporte.setOnClickListener {
            checkingGPS {
                opt = 3
                viewmodel.dataDownloaded()
            }
        }
        bind.fabAltas.setOnClickListener {
            checkingGPS {
                opt = 4
                viewmodel.dataDownloaded()
            }
        }
        bind.fabBajas.setOnClickListener {
            checkingGPS {
                opt = 5
                viewmodel.dataDownloaded()
            }
        }
        bind.fabServidor.setOnClickListener {
            checkingGPS {
                opt = 6
                viewmodel.dataDownloaded()
            }
        }

        viewmodel.inicio.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                if (y) {
                    when (opt) {
                        1 -> findNavController().navigate(R.id.action_FBase_to_FRastreo)
                        2 -> {
                            if (CONF.tipo == "V") {
                                findNavController().navigate(R.id.action_FBase_to_FCliente)
                            } else {
                                findNavController().navigate(R.id.action_FBase_to_FVendedor)
                            }
                        }
                        3 -> findNavController().navigate(R.id.action_FBase_to_FReporte)
                        4 -> findNavController().navigate(R.id.action_FBase_to_FAlta)
                        5 -> findNavController().navigate(R.id.action_FBase_to_FBaja)
                        6 -> findNavController().navigate(R.id.action_FBase_to_FServidor)
                    }
                } else {
                    snack("Los datos no se han descargado")
                }
            }
        }
        viewmodel.configObserver().observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                setParams(it[0])
            }
        }
        viewmodel.encuesta.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                val d = y.data?.jobl
                when (y) {
                    is NetworkRetrofit.Success -> if (d.isNullOrEmpty()) {
                        showDialog(
                            "Advertencia",
                            "No tiene encuesta programada"
                        ) {}
                    } else {
                        showDialog(
                            "Correcto",
                            "Encuesta descargada correctamente"
                        ) {}
                    }
                    is NetworkRetrofit.Error -> showDialog("Error", "Server ${y.message}") {}
                }
            }
        }
        viewmodel.sincro.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                sincro += y
                when (sincro) {
                    4 -> {
                        sincro = 0
                        snack("Sincronizacion completa")
                    }
                    90 -> {
                        sincro = 0
                        snack("Archivo de configuracion no encontrado")
                    }
                }
            }
        }
        viewmodel.rowClienteObs().distinctUntilChanged().observe(viewLifecycleOwner) { result ->
            setRuta(result)
        }
    }

    override fun onResume() {
        super.onResume()
        viewmodel.checking.observe(viewLifecycleOwner) {
            if (it) {
                //permBackground()
                viewmodel.launchSetup()
            } else {
                serviceListener?.onClosingActivity()
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.main_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.sincronizar -> consume { sinchroData() }
        R.id.ajustes -> consume { findNavController().navigate(R.id.action_FBase_to_DLogin) }
        R.id.encuesta -> consume { launchEncuesta() }
        R.id.incidencia -> consume { findNavController().navigate(R.id.action_FBase_to_FIncidencia) }
        R.id.apagar -> consume { requireActivity().finishAndRemoveTask() }
        else -> false
    }

    override fun changeGPSstate(gps: Boolean) {
        val color = if (gps) Color.rgb(4, 106, 97) else Color.rgb(255, 51, 51)
        bind.fabGps.imageTintList = ColorStateList.valueOf(color)
    }

    private fun setParams(config: TConfiguracion) {
        if (config.tipo != "S") {
            bind.lnrVendedor.setUI("v", false)
            bind.txtRuta.setUI("v", true)
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
    }

    private fun setRuta(l: List<RowCliente>) {
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
    }

    @SuppressLint("BatteryLife")
    private fun permBackground() {
        val packageName = requireContext().packageName
        val pm = requireContext().getSystemService(AppCompatActivity.POWER_SERVICE) as PowerManager
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent()
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

    private fun getUsuario(item: TConfiguracion): String {
        return if (item.nombre == "") {
            when (item.tipo) {
                "S" -> "Supervisor de ventas - ${item.codigo}"
                else -> "Usuario de ventas - ${item.codigo}"
            }
        } else {
            "${item.nombre} - ${item.codigo}"
        }
    }

    private fun launchEncuesta() {
        val p = JSONObject()
        p.put("empleado", CONF.codigo)
        p.put("empresa", CONF.empresa)
        progress("Descargando encuesta")
        viewmodel.fetchEncuesta(p.toReqBody())
    }

    private fun sinchroData() {
        if (viewmodel.internetAvailable()) {
            viewmodel.fetchSinchro()
        } else {
            snack("No tenemos señal de internet")
        }
    }

    private fun checkingGPS(T: () -> Unit) {
        if (requireContext().isGPSDisabled()) {
            snack("Habilite el GPS primero")
        } else {
            T()
        }
    }

}