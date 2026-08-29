package com.upd.kvupd.ui.fragment.solicitudes.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.upd.kvupd.R
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.databinding.BottomDetallebajaBinding
import com.upd.kvupd.databinding.BottomSolicitudesBinding
import com.upd.kvupd.ui.fragment.solicitudes.adapter.SolicitudAdapter
import com.upd.kvupd.ui.fragment.solicitudes.adapter.SolicitudAdapterFactory
import com.upd.kvupd.ui.fragment.solicitudes.modelUI.SolicitudUI
import com.upd.kvupd.utils.expandFullHeight
import com.upd.kvupd.utils.maps.vectorToBitmapDescriptor
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.viewmodel.APIViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BDSolicitudes : BottomSheetDialogFragment(),
    SolicitudAdapter.Listener{

    private val args: BDSolicitudesArgs by navArgs()
    private val apiViewModel by activityViewModels<APIViewModel>()
    private val binding by viewBinding(BottomSolicitudesBinding::bind)

    private lateinit var solicitudesAdapter: SolicitudAdapter
    private lateinit var flowCliente: FlowCliente

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
        initViews()
    }

    private fun initViews() {
        binding.txtCliente.text = "${flowCliente.cliente} - ${flowCliente.nomcli}"
    }

    private fun initAdapter() {
        solicitudesAdapter = adapterSolicitudesFactory.create(
            listener = this
        )
    }

    override fun onCheckedChanged(item: SolicitudUI, checked: Boolean) {
        //do something
    }







    //private fun iconoUbicacion(): BitmapDescriptor =
    //    requireContext().vectorToBitmapDescriptor(R.drawable.persona)

    /*private fun initViews() {

        detalle.let {
            val cliente = "${it.cliente} - ${it.nombre}"
            val vendedor = "${it.vendedor} - ${it.vendnom}"
            val canal = Canal.fromCodigo(it.canal)
            val motivo = MotivoBaja.fromId(it.motivo)
            val compra = "Ultima compra: ${it.compra}"

            binding.apply {
                txtCliente.text = cliente
                txtDireccion.text = it.direccion
                txtNegocio.text = it.negocio
                txtPago.text = it.pago
                txtVendedor.text = vendedor
                txtCompra.text = compra
                txtMotivo.text = motivo.label
                txtObservacion.text = it.observacion
            }

            binding.txtCanal.text = canal.codigo
            binding.txtCanal.setCompoundDrawablesWithIntrinsicBounds(
                canal.iconRes, 0, 0, 0
            )

            binding.btnDenegar.setOnClickListener { devolverDatosFragment(0) }
            binding.btnValidar.setOnClickListener { devolverDatosFragment(1) }
        }
    }*/

    /*private fun devolverDatosFragment(confirmacion: Int) {
        val comentario = binding.edtComentario.text.toString().trim()

        parentFragmentManager.setFragmentResult(
            KEY_DETALLE,
            bundleOf(
                "empleado" to detalle.vendedor,
                "cliente" to detalle.cliente,
                "procede" to confirmacion,
                "fecha" to detalle.creacion,
                "fechaconfirmacion" to FechaHoraUtil.ahora(),
                "observacion" to comentario
            )
        )

        dismiss()
    }*/
}