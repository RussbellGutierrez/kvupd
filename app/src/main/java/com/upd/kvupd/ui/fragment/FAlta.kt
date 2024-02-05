package com.upd.kvupd.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.distinctUntilChanged
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.upd.kvupd.R
import com.upd.kvupd.data.model.TAlta
import com.upd.kvupd.databinding.FragmentFAltaBinding
import com.upd.kvupd.service.ServicePosicion
import com.upd.kvupd.ui.adapter.AltaAdapter
import com.upd.kvupd.utils.Constant.ALTADATOS
import com.upd.kvupd.utils.Constant.POS_LOC
import com.upd.kvupd.utils.Constant.isPOSLOCinitialized
import com.upd.kvupd.utils.Interface.altaListener
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.progress
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.utils.showDialog
import com.upd.kvupd.utils.snack
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FAlta : Fragment(), AltaAdapter.OnAltaListener, MenuProvider {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFAltaBinding? = null
    private val bind get() = _bind!!
    private val _tag by lazy { FAlta::class.java.simpleName }

    @Inject
    lateinit var adapter: AltaAdapter

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
        requireContext().stopService(Intent(requireContext(), ServicePosicion::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        altaListener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentFAltaBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ALTADATOS = "lista"

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        bind.rcvAltas.layoutManager = LinearLayoutManager(requireContext())
        bind.rcvAltas.adapter = adapter

        viewmodel.launchPosition()
        viewmodel.altasObs().distinctUntilChanged().observe(viewLifecycleOwner) {
            setupList(it)
        }

        bind.fabAlta.setOnClickListener {
            showDialog("Advertencia", "¿Desea agregar un alta?", true) {
                if (isPOSLOCinitialized() &&
                    POS_LOC.longitude != 0.0 && POS_LOC.latitude != 0.0
                ) {
                    viewmodel.addingAlta(POS_LOC)
                } else {
                    snack("Procesando coordenadas, intente nuevamente")
                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.alta_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.manual -> consume { findNavController().navigate(R.id.action_FAlta_to_FAltaMapa) }
        else -> false
    }

    override fun onItemClick(alta: TAlta) {
        progress("Cargando configuracion")
        findNavController().navigate(
            FAltaDirections.actionFAltaToFAltaDatos(alta.idaux)
        )
    }

    private fun setupList(list: List<TAlta>) {
        if (list.isEmpty()) {
            bind.emptyContainer.root.setUI("v", true)
            bind.rcvAltas.setUI("v", false)
        } else {
            bind.emptyContainer.root.setUI("v", false)
            bind.rcvAltas.setUI("v", true)
            adapter.mDiffer.submitList(list)
        }
    }
}