package com.upd.kvupd.ui.fragment.solicitudes.dialog

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.databinding.BottomSolicitudesBinding
import com.upd.kvupd.domain.enumFile.TipoUsuario
import com.upd.kvupd.ui.fragment.solicitudes.adapter.SolicitudAdapter
import com.upd.kvupd.ui.fragment.solicitudes.adapter.SolicitudAdapterFactory
import com.upd.kvupd.ui.fragment.solicitudes.enumFile.TipoSolicitud
import com.upd.kvupd.ui.fragment.solicitudes.modelUI.SolicitudUI
import com.upd.kvupd.utils.GridSpacingItemDecoration
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.utils.expandFullHeight
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.viewmodel.APIViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BDSolicitudes : BottomSheetDialogFragment(),
    SolicitudAdapter.Listener {

    private val args: BDSolicitudesArgs by navArgs()
    private val apiViewModel by activityViewModels<APIViewModel>()
    private val binding by viewBinding(BottomSolicitudesBinding::bind)

    private lateinit var solicitudesAdapter: SolicitudAdapter
    private lateinit var flowCliente: FlowCliente

    private var solicitudes: List<SolicitudUI> = emptyList()
    private var longitud = 0.0f
    private var latitud = 0.0f
    private val _tag by lazy { BDSolicitudes::class.java.simpleName }

    @Inject
    lateinit var adapterSolicitudesFactory: SolicitudAdapterFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        flowCliente = args.cliente
        latitud = args.latitud
        longitud = args.longitud
    }

    override fun onStart() {
        super.onStart()
        expandFullHeight()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = BottomSolicitudesBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initViews()
        observeConfiguracion()
    }

    private fun initViews() {
        binding.txtCliente.text = "${flowCliente.cliente} - ${flowCliente.nomcli}"
    }

    private fun initAdapter() {
        solicitudesAdapter = adapterSolicitudesFactory.create(this)

        val spacing = (10 * resources.displayMetrics.density).toInt()

        binding.rcvOpciones.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(
                GridSpacingItemDecoration(2, spacing, true)
            )
            adapter = solicitudesAdapter
        }
    }

    private fun observeConfiguracion() {
        collectFlow(apiViewModel.flowConfiguracion) { configuraciones ->
            val configuracion = configuraciones.firstOrNull()
                ?: return@collectFlow

            val tipoUsuario = TipoUsuario.fromCodigo(
                configuracion.tipo
            )

            cargarSolicitudes(tipoUsuario)
        }
    }

    private fun cargarSolicitudes(tipoUsuario: TipoUsuario) {
        solicitudes = TipoSolicitud
            .visiblesPara(tipoUsuario)
            .map { tipo ->
                SolicitudUI(
                    tipo = tipo,
                    seleccionado = false
                )
            }

        solicitudesAdapter.submitList(solicitudes)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Ubicacion voucher")
        resultGallery.launch(chooser)
    }

    override fun onCheckedChanged(item: SolicitudUI, checked: Boolean) {
        solicitudes = solicitudes.map { solicitud ->
            if (solicitud.tipo == item.tipo) {
                solicitud.copy(seleccionado = checked)
            } else {
                solicitud
            }
        }

        solicitudesAdapter.submitList(solicitudes)
    }

    private val resultGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri: Uri? = result.data?.data
                imageUri?.let {
                    movePicture(it)
                }
            } else {
                showDialog("Advertencia", "No se eligio foto") {}
            }
        }
}