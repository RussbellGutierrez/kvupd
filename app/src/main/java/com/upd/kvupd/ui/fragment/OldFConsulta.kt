package com.upd.kvupd.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.upd.kvupd.R
import com.upd.kvupd.data.model.TConsulta
import com.upd.kvupd.databinding.FragmentFConsultaBinding
import com.upd.kvupd.service.ServicePosicion
import com.upd.kvupd.utils.OldConstant.CONF
import com.upd.kvupd.utils.OldConstant.GPS_LOC
import com.upd.kvupd.utils.OldNetworkRetrofit
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.progress
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.utils.settingsMap
import com.upd.kvupd.utils.showDialog
import com.upd.kvupd.utils.snack
import com.upd.kvupd.utils.toLocation
import com.upd.kvupd.utils.toReqBody
import com.upd.kvupd.viewmodel.OldAppViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject

@AndroidEntryPoint
class OldFConsulta : Fragment(), OnMapReadyCallback, OnMarkerClickListener, MenuProvider {

    private val viewmodel by activityViewModels<OldAppViewModel>()
    private var _bind: FragmentFConsultaBinding? = null
    private val bind get() = _bind!!
    private lateinit var sup: SupportMapFragment
    private lateinit var map: GoogleMap
    private lateinit var markers: List<Marker>
    private var lc = mutableListOf<TConsulta>()
    private val _tag by lazy { OldFConsulta::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
        /*POS_LOC.longitude = 0.0
        POS_LOC.latitude = 0.0*/
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
        _bind = FragmentFConsultaBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!::sup.isInitialized) {
            sup = childFragmentManager.findFragmentById(bind.map.id) as SupportMapFragment
            sup.getMapAsync(this)
        }

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        bind.fabCentrar.setOnClickListener { centerMarkers() }
        bind.btnConsulta.setOnClickListener { searchCliente() }

        viewmodel.consulta.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                when (y) {
                    is OldNetworkRetrofit.Success -> showDialog(
                        "Correcto",
                        "Clientes del dia descargados correctamente"
                    ) {}

                    is OldNetworkRetrofit.Error -> showDialog("Error", "Server ${y.message}") {}
                }
            }
        }
        viewmodel.consultado.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                if (y.isEmpty()) {
                    showDialog("Advertencia", "No se encontraron coincidencias") {}
                } else {
                    if (::map.isInitialized) {
                        map.clear()
                        lc.clear()
                        markers = viewmodel.consultaMarker(map, y)
                        lc.addAll(y)
                        centerMarkers()
                    }
                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.consulta_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.descargar -> consume { launchClientesDia() }
        else -> false
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        p0.also {
            map = it
            map.apply {
                settingsMap()
                isMyLocationEnabled = true
                setOnMarkerClickListener(this@OldFConsulta)
            }
            moveCamera(GPS_LOC)
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        p0.showInfoWindow()
        moveCamera(p0.position.toLocation())
        setupUI(p0.title)
        return true
    }

    private fun moveCamera(location: Location) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.longitude), 16f
            )
        )
    }

    private fun centerMarkers() {
        val builder = LatLngBounds.Builder()
        if (::markers.isInitialized) {
            if (markers.isNotEmpty()) {
                markers.forEach { i -> builder.include(i.position) }
                builder.include(LatLng(GPS_LOC.latitude, GPS_LOC.longitude))
                val bounds = builder.build()
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200))
            }
        } else {
            snack("Sin marcadores para ubicar")
        }
    }

    private fun launchClientesDia() {
        val p = JSONObject()
        p.put("fecha", CONF.fecha)
        p.put("empresa", CONF.empresa)
        progress("Descargando clientes del día")
        viewmodel.fetchConsulta(p.toReqBody())
    }

    private fun searchCliente() {
        var numero = bind.edtDocumento.text.toString().trim()
        var nombre = bind.edtNombres.text.toString().uppercase().trim()

        bind.cardConsulta.setUI("v", false)
        map.clear()

        if (numero.isEmpty()) {
            numero = "0"
        }
        nombre = if (nombre.isEmpty()) {
            "NOT"
        } else {
            "*$nombre*"
        }
        if (numero == "0" && nombre == "NOT") {
            showDialog("Advertencia", "Debe completar uno de los 2 campos para la busqueda") {}
        } else {
            if (numero != "0") {
                nombre = "NOT"
            } else if (nombre != "NOT") {
                numero = "0"
            }
        }
        viewmodel.getClienteConsultado(numero, nombre)
    }

    private fun setupUI(codigo: String?) {
        if (codigo.isNullOrEmpty()) {
            showDialog("Error", "No se encuentra el codigo de cliente") {}
        } else {
            lc.forEach { i ->

                if (i.cliente == codigo.toInt()) {

                    bind.cardConsulta.setUI("v", true)
                    bind.edtNombres.setText("")
                    bind.edtDocumento.setText("")

                    var compra = ""
                    val documento = "DOC. ${i.documento}"
                    val cliente = "${i.cliente} - ${i.nombre}"
                    if (i.ventas > 0) {
                        compra = "CLIENTE REALIZA COMPRAS"
                        bind.cardCompra.setCardBackgroundColor(
                            resources.getColor(
                                R.color.green,
                                null
                            )
                        )
                    } else {
                        compra = "CLIENTE NO REALIZO COMPRAS"
                        bind.cardCompra.setCardBackgroundColor(
                            resources.getColor(
                                R.color.lightcrimson,
                                null
                            )
                        )
                    }
                    val direccion = i.domicilio

                    when (i.canal) {
                        "BRONCE" -> bind.imgCanal.setImageResource(R.drawable.bronce)
                        "PLATA" -> bind.imgCanal.setImageResource(R.drawable.plata)
                        "ORO" -> bind.imgCanal.setImageResource(R.drawable.oro)
                        "DIAMANTE" -> bind.imgCanal.setImageResource(R.drawable.diamante)
                        "MINORISTA" -> bind.imgCanal.setImageResource(R.drawable.minorista)
                        "MAYORISTA" -> bind.imgCanal.setImageResource(R.drawable.mayorista)
                        "HORIZONTAL" -> bind.imgCanal.setImageResource(R.drawable.horizontal)
                        "MERCADOS" -> bind.imgCanal.setImageResource(R.drawable.tienda)
                        "BOTICAS" -> bind.imgCanal.setImageResource(R.drawable.pildora)
                        "OTROS" -> bind.imgCanal.setImageResource(R.drawable.casa)
                        else -> bind.imgCanal.setImageResource(R.drawable.restringido)
                    }

                    when (i.anulado) {
                        0 -> bind.imgEstado.setImageResource(R.drawable.cliente_activo)
                        1 -> bind.imgEstado.setImageResource(R.drawable.cliente_anulado)
                    }

                    bind.txtDocumento.text = documento
                    bind.txtCliente.text = cliente
                    bind.txtDireccion.text = direccion
                    bind.txtCompra.text = compra
                    bind.txtCanal.text = i.canal
                    bind.txtNegocio.text = i.negocio
                }
            }
        }
    }
}