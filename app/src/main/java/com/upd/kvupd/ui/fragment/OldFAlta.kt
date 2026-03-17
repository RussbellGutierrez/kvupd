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
import androidx.recyclerview.widget.LinearLayoutManager
import com.upd.kvupd.R
import com.upd.kvupd.databinding.FragmentFAltaBinding
import com.upd.kvupd.service.OldServicePosicion
import com.upd.kvupd.ui.adapter.OldAltaAdapter
import com.upd.kvupd.utils.OldConstant.ALTADATOS
import com.upd.kvupd.utils.consume
import com.upd.kvupd.viewmodel.OldAppViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OldFAlta : Fragment(), MenuProvider {//, AltaAdapter.OnAltaListener, MenuProvider {

    private val viewmodel by activityViewModels<OldAppViewModel>()
    private var _bind: FragmentFAltaBinding? = null
    private val bind get() = _bind!!
    private val _tag by lazy { OldFAlta::class.java.simpleName }

    @Inject
    lateinit var adapter: OldAltaAdapter

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
        requireContext().stopService(Intent(requireContext(), OldServicePosicion::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //altaListener = this
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

        //bind.rcvAltas.layoutManager = LinearLayoutManager(requireContext())
        //bind.rcvAltas.adapter = adapter

        /*viewmodel.launchPosition()
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
        }*/
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.n_alta_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.manual -> consume { }//findNavController().navigate(R.id.action_FAlta_to_FAltaMapa) }
        else -> false
    }

    /*override fun onItemClick(alta: TAlta) {
        progress("Cargando configuracion")
        findNavController().navigate(
            OldFAltaDirections.actionFAltaToFAltaDatos(alta.idaux)
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
    }*/
}