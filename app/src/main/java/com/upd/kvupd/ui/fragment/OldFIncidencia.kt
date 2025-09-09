package com.upd.kvupd.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.upd.kvupd.data.model.TIncidencia
import com.upd.kvupd.databinding.FragmentFIncidenciaBinding
import com.upd.kvupd.ui.adapter.IncidenciaAdapter
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.viewmodel.OldAppViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OldFIncidencia : Fragment() {

    private val viewmodel by activityViewModels<OldAppViewModel>()
    private var _bind: FragmentFIncidenciaBinding? = null
    private val bind get() = _bind!!
    private val _tag by lazy { OldFIncidencia::class.java.simpleName }

    @Inject
    lateinit var adapter: IncidenciaAdapter

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentFIncidenciaBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.rcvIncidencia.layoutManager = LinearLayoutManager(requireContext())
        bind.rcvIncidencia.adapter = adapter

        viewmodel.incidenciaObs().distinctUntilChanged().observe(viewLifecycleOwner) {
            setupList(it)
        }
    }

    private fun setupList(list: List<TIncidencia>) {
        if (list.isNullOrEmpty()) {
            bind.emptyContainer.root.setUI("v", true)
            bind.rcvIncidencia.setUI("v", false)
        } else {
            bind.emptyContainer.root.setUI("v", false)
            bind.rcvIncidencia.setUI("v", true)
            adapter.mDiffer.submitList(list)
        }
    }

}