package com.upd.kventas.ui.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.upd.kventas.BuildConfig
import com.upd.kventas.R
import com.upd.kventas.data.model.Config
import com.upd.kventas.databinding.FragmentFBaseBinding
import com.upd.kventas.utils.*
import com.upd.kventas.utils.Constant.CONF
import com.upd.kventas.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject

@AndroidEntryPoint
class FBase : Fragment() {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFBaseBinding? = null
    private val bind get() = _bind!!
    private var opt = 0
    private val _tag by lazy { FBase::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

        bind.fabVendedor.setOnClickListener {
            opt = 1
            viewmodel.dataDowloaded()
        }
        bind.fabCliente.setOnClickListener {
            opt = 2
            viewmodel.dataDowloaded()
        }
        bind.fabReporte.setOnClickListener {
            opt = 3
            viewmodel.dataDowloaded()
        }
        bind.fabAltas.setOnClickListener {
            opt = 4
            viewmodel.dataDowloaded()
        }
        bind.fabBajas.setOnClickListener {
            opt = 5
            viewmodel.dataDowloaded()
        }
        bind.fabServidor.setOnClickListener {
            opt = 6
            viewmodel.dataDowloaded()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewmodel.setupApp { findNavController().navigate(R.id.action_FBase_to_FAjuste) }
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
                when (y) {
                    is Network.Success -> showDialog(
                        "Correcto",
                        "Encuesta descargada correctamente"
                    ) {}
                    is Network.Error -> showDialog("Error", "Server ${y.message}") {}
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.ajustes -> consume { findNavController().navigate(R.id.action_FBase_to_DLogin) }
        R.id.encuesta -> consume { launchEncuesta() }
        R.id.apagar -> consume { requireActivity().finishAndRemoveTask() }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setParams(config: Config) {
        if (config.tipo != "S") {
            bind.lnrVendedor.setUI("v", false)
        }
        val img = if (config.empresa == 1) R.drawable.oriunda_logo else R.drawable.terranorte_logo
        val version = "ver. ${BuildConfig.VERSION_NAME}"
        val usuario = getUsuario(config)
        val gps = if (isGPSDisabled()) Color.rgb(221, 150, 6) else Color.rgb(4, 106, 97)
        val seguimiento =
            if (config.seguimiento == 1) Color.rgb(4, 106, 97) else Color.rgb(221, 150, 6)
        bind.imgEmpresa.setImageResource(img)
        bind.txtUsuario.text = usuario
        bind.txtVersion.text = version
        bind.fabGps.imageTintList = ColorStateList.valueOf(gps)
        bind.fabEmit.imageTintList = ColorStateList.valueOf(seguimiento)
    }

    private fun getUsuario(item: Config): String {
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
}