package com.upd.kventas.ui.fragment

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
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.upd.kventas.R
import com.upd.kventas.data.model.TBEstado
import com.upd.kventas.data.model.TBajaSuper
import com.upd.kventas.databinding.FragmentFValidarBinding
import com.upd.kventas.service.ServicePosicion
import com.upd.kventas.utils.Constant.CONF
import com.upd.kventas.utils.Constant.FIRST_LOCATION
import com.upd.kventas.utils.Constant.POS_LOC
import com.upd.kventas.utils.settingsMap
import com.upd.kventas.utils.toLocation
import com.upd.kventas.viewmodel.AppViewModel
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
    private val _tag by lazy { FValidar::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
        POS_LOC = null
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

        viewmodel.bajasuperspecif.observe(viewLifecycleOwner) {
            bs = it
            setupUI(bs)
        }

        gettingData()

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
            moveCamera(FIRST_LOCATION)
        }
        bmk = viewmodel.bajaMarker(map, bs)
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

        bind.txtVendedor.text = vendedor
        bind.txtMotivo.text = item.descripcion
        bind.txtObservacion.text = item.observacion
    }

    private fun distanceBetween() {
        val builder = LatLngBounds.Builder()
        val empleado = LatLng(POS_LOC!!.latitude, POS_LOC!!.longitude)
        builder.include(bmk.position)
        builder.include(empleado)
        val bounds = builder.build()
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }

    private fun saveBajaValidar(procede: Int) {

        val observacion = bind.edtComentario.text.toString().trim()
        val fechaconf = viewmodel.fecha(4)
        val item = TBEstado(
            CONF.codigo,
            bs.clicodigo,
            procede,
            bs.creado,
            bs.precision,
            bs.longitud,
            bs.latitud,
            fechaconf,
            observacion,
            "Pendiente"
        )

        viewmodel.saveEstadoBaja(item)
        findNavController().navigate(
            FValidarDirections.actionFValidarToFBajaDatos()
        )
    }
}