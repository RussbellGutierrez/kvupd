package com.upd.kvupd.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upd.kvupd.R
import com.upd.kvupd.data.model.MiniUpdBaja
import com.upd.kvupd.data.model.TBaja
import com.upd.kvupd.databinding.FragmentFBajaBinding
import com.upd.kvupd.ui.adapter.BajaAdapter
import com.upd.kvupd.utils.Interface.bajaListener
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.utils.showDialog
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FBaja : Fragment(), BajaAdapter.OnBajaListener, MenuProvider {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFBajaBinding? = null
    private val bind get() = _bind!!

    @Inject
    lateinit var adapter: BajaAdapter

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bajaListener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentFBajaBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        bind.rcvBajas.layoutManager = LinearLayoutManager(requireContext())
        bind.rcvBajas.adapter = adapter

        viewmodel.bajasObs().observe(viewLifecycleOwner) {
            setupList(it)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.baja_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when(menuItem.itemId) {
        R.id.lista -> consume { findNavController().navigate(R.id.action_FBaja_to_FBajaDatos) }
        else -> false
    }

    override fun onPressBaja(baja: TBaja) {
        val cliente = "${baja.cliente} - ${baja.nombre}"
        showDialog(
            "Advertencia",
            "Baja del cliente:\n $cliente \n Si anula la baja, tendra que esperar hasta mañana para generarla nuevamente"
        ) {
            val item = MiniUpdBaja(baja.cliente, 1, "Pendiente")
            viewmodel.updateBaja(item)
        }
    }

    private fun setupList(list: List<TBaja>) {
        if (list.isNullOrEmpty()) {
            bind.emptyContainer.root.setUI("v", true)
            bind.rcvBajas.setUI("v", false)
        } else {
            bind.emptyContainer.root.setUI("v", false)
            bind.rcvBajas.setUI("v", true)
            adapter.mDiffer.submitList(list)
        }
    }
}