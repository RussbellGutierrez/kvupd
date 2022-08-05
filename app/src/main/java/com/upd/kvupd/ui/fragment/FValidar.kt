package com.upd.kvupd.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.upd.kvupd.R
import com.upd.kvupd.data.model.TBEstado
import com.upd.kvupd.data.model.TBajaSuper
import com.upd.kvupd.databinding.FragmentFValidarBinding
import com.upd.kvupd.service.ServicePosicion
import com.upd.kvupd.utils.*
import com.upd.kvupd.utils.Constant.GPS_LOC
import com.upd.kvupd.utils.Constant.POS_LOC
import com.upd.kvupd.utils.Constant.isPOSLOCinitialized
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FValidar : Fragment(), OnMapReadyCallback, OnMarkerClickListener {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFValidarBinding? = null
    private val bind get() = _bind!!
    private val arg: FValidarArgs by navArgs()
    private lateinit var sup: SupportMapFragment
    private lateinit var map: GoogleMap
    private lateinit var bmk: Marker
    private lateinit var bs: TBajaSuper
    private var codVend = 0
    private val _tag by lazy { FValidar::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
        POS_LOC.longitude = 0.0
        POS_LOC.latitude = 0.0
        requireContext().stopService(Intent(requireContext(), ServicePosicion::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel.launchPosition()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentFValidarBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!::sup.isInitialized) {
            sup = childFragmentManager.findFragmentById(bind.map.id) as SupportMapFragment
            sup.getMapAsync(this)
        }

        gettingData()

        viewmodel.bajasuperspecif.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                bs = y
                setupUI(bs)
            }
        }

        bind.fabCentrar.setOnClickListener { distanceBetween() }
        bind.btnDenegar.setOnClickListener { saveBajaValidar(0) }
        bind.btnValidar.setOnClickListener { saveBajaValidar(1) }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        p0.also {
            map = it
            map.apply {
                settingsMap()
                isMyLocationEnabled = true
                setOnMarkerClickListener(this@FValidar)
            }
            moveCamera(GPS_LOC)
        }
        bmk = viewmodel.bajaMarker(map, bs)
        distanceBetween()
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        p0.showInfoWindow()
        moveCamera(p0.position.toLocation())
        return true
    }

    private fun moveCamera(location: Location) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.longitude), 16f
            )
        )
    }

    private fun gettingData() {
        arg.datos?.let {
            val codigo = it.split("@")[0].trim()
            val fecha = it.split("@")[1].trim()
            viewmodel.getBajaSuperSpecific(codigo, fecha)
        }
    }

    private fun setupUI(item: TBajaSuper) {
        val cliente = "${item.clicodigo} - ${item.clinombre}"
        val ruta = "Ruta: ${item.ruta}"
        val pago = "Pago: ${item.pago}"
        val compra = "Compró: ${item.compra}"
        val vendedor = "${item.empleado} - ${item.nombre}"

        bind.txtCliente.text = cliente
        bind.txtDireccion.text = item.direccion
        bind.txtRuta.text = ruta
        bind.txtNegocio.text = item.negocio
        bind.txtPago.text = pago
        bind.txtCompra.text = compra
        bind.txtCanal.text = item.canal

        when (item.canal) {
            "BRONCE" -> bind.imgCanal.setImageResource(R.drawable.bronce)
            "PLATA" -> bind.imgCanal.setImageResource(R.drawable.plata)
            "ORO" -> bind.imgCanal.setImageResource(R.drawable.oro)
            "DIAMANTE" -> bind.imgCanal.setImageResource(R.drawable.diamante)
            "MINORISTA" -> bind.imgCanal.setImageResource(R.drawable.minorista)
            "MAYORISTA" -> bind.imgCanal.setImageResource(R.drawable.mayorista)
            "HORIZONTAL" -> bind.imgCanal.setImageResource(R.drawable.horizontal)
            "MERCADOS" -> bind.imgCanal.setImageResource(R.drawable.tienda)
            "BOTICAS" -> bind.imgCanal.setImageResource(R.drawable.pildora)
        }

        codVend = item.empleado
        bind.txtVendedor.text = vendedor
        bind.txtMotivo.text = item.descripcion
        val obs = item.observacion.trim()
        if (obs != "") {
            bind.txtObservacion.setUI("v", true)
            bind.txtObservacion.text = obs
        }
    }

    private fun distanceBetween() {
        val builder = LatLngBounds.Builder()
        val empleado = LatLng(GPS_LOC.latitude, GPS_LOC.longitude)
        builder.include(bmk.position)
        builder.include(empleado)
        val bounds = builder.build()
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun saveBajaValidar(procede: Int) {
        if (isPOSLOCinitialized() &&
            POS_LOC.longitude != 0.0 && POS_LOC.latitude != 0.0) {
            val observacion = bind.edtComentario.text.toString().trim()
            val fechaconf = viewmodel.fecha(4)
            val item = TBEstado(
                codVend,
                bs.clicodigo,
                procede,
                bs.creado,
                POS_LOC.accuracy.toDouble(),
                POS_LOC.longitude,
                POS_LOC.latitude,
                fechaconf,
                observacion,
                "Pendiente"
            )

            viewmodel.saveEstadoBaja(item)
            findNavController().navigate(
                FValidarDirections.actionFValidarToFBajaDatos()
            )
        } else {
            snack("Ocurrio un problema con las coordenadas, intentelo nuevamente")
        }
    }

}