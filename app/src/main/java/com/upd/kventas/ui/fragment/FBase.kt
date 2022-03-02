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
import com.upd.kventas.utils.consume
import com.upd.kventas.utils.isGPSDisabled
import com.upd.kventas.utils.setUI
import com.upd.kventas.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FBase : Fragment() {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFBaseBinding? = null
    private val bind get() = _bind!!
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

        bind.fabVendedor.setOnClickListener {  }
        bind.fabCliente.setOnClickListener { findNavController().navigate(R.id.action_FBase_to_FCliente) }
        bind.fabReporte.setOnClickListener { findNavController().navigate(R.id.action_FBase_to_FReporte) }
        bind.fabAltas.setOnClickListener {  }
        bind.fabBajas.setOnClickListener {  }
        bind.fabOtros.setOnClickListener {  }

        viewLifecycleOwner.lifecycleScope.launch {
            viewmodel.setupApp { findNavController().navigate(R.id.action_FBase_to_FAjuste) }
        }
        viewmodel.configObserver().observe(viewLifecycleOwner) { result ->
            if (result.isNotEmpty()) {
                setParams(result[0])
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.ajustes -> consume { findNavController().navigate(R.id.action_FBase_to_DLogin) }
        R.id.apagar -> consume { requireActivity().finishAndRemoveTask() }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setParams(config: Config) {
        if (config.tipo != "S") {
            bind.lnrVendedor.setUI("v",false)
        }
        val img = if (config.empresa == 1) R.drawable.oriunda_logo else R.drawable.terranorte_logo
        val version = "ver. ${BuildConfig.VERSION_NAME}"
        val usuario = "${config.nombre} - ${config.codigo}"
        val gps = if (isGPSDisabled()) Color.rgb(221, 150, 6) else Color.rgb(4, 106, 97)
        val seguimiento = if (config.seguimiento == 1) Color.rgb(4, 106, 97) else Color.rgb(221, 150, 6)
        bind.imgEmpresa.setImageResource(img)
        bind.txtUsuario.text = usuario
        bind.txtVersion.text = version
        bind.fabGps.imageTintList = ColorStateList.valueOf(gps)
        bind.fabEmit.imageTintList = ColorStateList.valueOf(seguimiento)
    }
}