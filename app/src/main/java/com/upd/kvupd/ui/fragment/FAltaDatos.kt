package com.upd.kvupd.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.upd.kvupd.R
import com.upd.kvupd.data.model.TADatos
import com.upd.kvupd.data.model.asSpinner
import com.upd.kvupd.databinding.FragmentFAltaDatosBinding
import com.upd.kvupd.utils.*
import com.upd.kvupd.utils.Constant.ALTADATOS
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FAltaDatos : Fragment() {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFAltaDatosBinding? = null
    private val bind get() = _bind!!
    private var tipo = ""
    private var distrito = listOf<String>()
    private var giro = listOf<String>()
    private val args: FAltaDatosArgs by navArgs()
    private val _tag by lazy { FAltaDatos::class.java.simpleName }

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
        _bind = FragmentFAltaDatosBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.rbGrupo.setOnCheckedChangeListener { _, id ->
            when (id) {
                bind.rbJuridica.id -> {
                    tipo = "PJ"
                    showFields(0)
                }
                bind.rbNatural.id -> {
                    tipo = "PN"
                    showFields(1)
                }
            }
        }

        viewmodel.distritosObs().observe(viewLifecycleOwner) {
            distrito = it.asSpinner()
            bind.spnDistrito.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, distrito)
        }

        viewmodel.negociosObs().observe(viewLifecycleOwner) {
            giro = it.asSpinner()
            bind.spnGiro.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, giro)
        }

        viewmodel.altadatos.observe(viewLifecycleOwner) {
            if (it != null) {
                setupFields(it)
            }
        }

        checkAlta()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.altadatos_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.guardar -> consume { saveAltaDatos() }
        else -> super.onOptionsItemSelected(item)
    }

    private fun cleanFields() {
        bind.edtRazon.setText("")
        bind.edtPaterno.setText("")
        bind.edtMaterno.setText("")
        bind.edtNombre.setText("")
        bind.edtDocumento.setText("")
        bind.edtMovil1.setText("")
        bind.edtMovil2.setText("")
        bind.edtCorreo.setText("")
        bind.spnVia.setSelection(0)
        bind.edtManzana.setText("")
        bind.edtDireccion.setText("")
        bind.spnNumero.setSelection(0)
        bind.edtNumero.setText("")
        bind.spnZona.setSelection(0)
        bind.edtZona.setText("")
        bind.edtRuta.setText("")
        bind.edtSecuencia.setText("")
    }

    private fun showFields(opt: Int) {
        cleanFields()
        bind.lnrDetalle.setUI("v", true)
        when (opt) {
            0 -> {
                bind.txtRazon.setUI("v", true)
                bind.lnrCliente.setUI("v", false)
            }
            1 -> {
                bind.txtRazon.setUI("v", false)
                bind.lnrCliente.setUI("v", true)
            }
        }
    }

    private fun checkAlta() {
        val cliente = "Alta cliente :: ${args.idaux}"
        bind.txtTitulo.text = cliente
        viewmodel.fetchAltaDatos(args.idaux.toString())
    }

    private fun setupFields(alta: TADatos) {
        if (alta.tipo == "PJ")
            bind.rbJuridica.isChecked = true
        else
            bind.rbNatural.isChecked = true

        val ald = distrito.find { it == alta.distrito }
        val alg = giro.find { it == alta.giro }

        bind.edtRazon.setText(alta.razon)
        bind.edtPaterno.setText(alta.appaterno)
        bind.edtMaterno.setText(alta.apmaterno)
        bind.edtNombre.setText(alta.nombre)
        bind.edtDocumento.setText(alta.documento)
        bind.edtMovil1.setText(alta.movil1)
        bind.edtMovil2.setText(alta.movil2)
        bind.edtCorreo.setText(alta.correo)
        bind.spnVia.setSelection(setVia(alta.via))
        bind.edtManzana.setText(alta.manzana)
        bind.edtDireccion.setText(alta.direccion)
        bind.spnNumero.setSelection(setUbicacion(alta.ubicacion))
        bind.edtNumero.setText(alta.numero)
        bind.spnZona.setSelection(setZona(alta.zona))
        bind.edtZona.setText(alta.zonanombre)
        bind.edtRuta.setText(alta.ruta)
        bind.edtSecuencia.setText(alta.secuencia)
        bind.spnDistrito.setSelection(distrito.indexOf(ald))
        bind.spnGiro.setSelection(giro.indexOf(alg))
    }

    private fun saveAltaDatos() {
        val razon = bind.edtRazon.text.toString().trim().uppercase()
        val paterno = bind.edtPaterno.text.toString().trim().uppercase()
        val materno = bind.edtMaterno.text.toString().trim().uppercase()
        val nombre = bind.edtNombre.text.toString().trim().uppercase()
        val documento = bind.edtDocumento.text.toString().trim().uppercase()
        val movil1 = bind.edtMovil1.text.toString().trim()
        val movil2 = bind.edtMovil2.text.toString().trim()
        val correo = bind.edtCorreo.text.toString().trim()
        val via = getVia()
        val manzana = bind.edtManzana.text.toString().trim().uppercase()
        val direccion = bind.edtDireccion.text.toString().trim().uppercase()
        val ubicacion = getUbicacion()
        val numero = bind.edtNumero.text.toString().trim().uppercase()
        val zona = getZona()
        val zonanombre = bind.edtZona.text.toString().trim().uppercase()
        val distrito = bind.spnDistrito.selectedItem.toString()
        val giro = bind.spnGiro.selectedItem.toString()
        val ruta = bind.edtRuta.text.toString().trim()
        val secuencia = bind.edtSecuencia.text.toString().trim()

        when {
            tipo == "PJ" && razon == "" -> showDialog("Error", "Ingrese una razon social") {}
            tipo == "PN" && (paterno == "" || materno == "" || nombre == "") -> showDialog(
                "Error",
                "Ingrese nombre y apellidos del cliente"
            ) {}
            documento == "" -> showDialog("Error", "Ingrese documento de identificacion") {}
            movil1 == "" && movil2 == "" -> showDialog("Error", "Ingrese un numero de celular") {}
            correo != "" && !correo.checkEmail() -> showDialog(
                "Error",
                "Ingrese un correo valido"
            ) {}
            numero == "" -> showDialog("Error", "Ingrese el numero de calle") {}
            ruta == "" -> showDialog("Error", "Ingrese la ruta") {}
            secuencia == "" -> showDialog("Error", "Ingrese la secuencia") {}
            else -> {
                if (documento.checkDocumento(tipo)) {
                    val item = TADatos(
                        args.idaux,
                        CONF.codigo,
                        tipo,
                        razon,
                        nombre,
                        paterno,
                        materno,
                        documento,
                        movil1,
                        movil2,
                        correo,
                        via,
                        direccion,
                        manzana,
                        zona,
                        zonanombre,
                        ubicacion,
                        numero,
                        distrito,
                        giro,
                        ruta,
                        secuencia,
                        "Pendiente"
                    )
                    viewmodel.saveAltaDatos(item)
                    when(ALTADATOS) {
                        "lista" -> findNavController().navigate(R.id.action_FAltaDatos_to_FAlta)
                        "mapa" -> findNavController().navigate(
                            FAltaDatosDirections.actionFAltaDatosToFAltaMapa(args.idaux)
                        )
                    }
                } else {
                    showDialog(
                        "Error",
                        "-DNI -> 8 dígitos\n-EXTRANJERIA -> 9 dígitos\n-RUC -> 11 dígitos\n-RUC jurídico inicia con 20 (solo empresas)\n-RUC natural inicia con 10 o 15 (solo personas naturales)"
                    ) {}
                }
            }
        }
    }

    private fun getUbicacion() = when (bind.spnNumero.selectedItem.toString()) {
        "NUMERO" -> ""
        "INTERIOR" -> "INT"
        "BLOCK" -> "BLOCK"
        "DPTO" -> "DPTO"
        "PUESTO" -> "PTO"
        "LOTE" -> "LT"
        else -> "Ninguno"
    }

    private fun getVia() = when (bind.spnVia.selectedItem.toString()) {
        "AVENIDA" -> "AV"
        "BOULEVARD" -> "BLVR"
        "CALLE" -> "CL"
        "CARRETERA" -> "CARR"
        "INTERIOR" -> "INT"
        "JIRON" -> "JR"
        "MERCADO" -> "MCDO"
        "PASAJE" -> "PJE"
        "PASEO" -> "P"
        "PLAZA" -> "PLZA"
        else -> "Ninguno"
    }

    private fun getZona() = when (bind.spnZona.selectedItem.toString()) {
        "AMPLIACION" -> "AMPL"
        "ANEXO" -> "ANEXO"
        "ASENTAMIENTO HUMANO" -> "AAHH"
        "ASOCIACION" -> "ASOC"
        "ASOCIACION VIVIENDA" -> "ASOC VIV"
        "BARRIO" -> "BAR"
        "CENTRO COMERCIAL" -> "CC"
        "CENTRO POBLADO" -> "CP"
        "COMITE" -> "COMI"
        "CONDOMINIO" -> "COND"
        "CONJUNTO HABITACIONAL" -> "CONJ HAB"
        "CONJUNTO RESIDENCIAL" -> "CR"
        "COOPERATIVA" -> "COOP"
        "COOPERATIVA VIVIENDA" -> "COOP VIV"
        "HABILITACION URBANA" -> "HAB URB"
        "LOTIZACION" -> "LOT"
        "PARCELA" -> "PARC"
        "POBLADO" -> "POB"
        "PUEBLO JOVEN" -> "PJ"
        "QUINTA" -> "QUINTA"
        "RESIDENCIAL" -> "RES"
        "SECTOR" -> "SECT"
        "UNIDAD VECINAL" -> "UV"
        "URBANIZACION" -> "URB"
        "VILLA" -> "VILLA"
        "ZONA INDUSTRIAL" -> "ZI"
        else -> "Ninguno"
    }

    private fun setUbicacion(texto: String) = when (texto) {
        "" -> 0
        "INT" -> 1
        "BLOCK" -> 2
        "DPTO" -> 3
        "PTO" -> 4
        "LT" -> 5
        else -> 999
    }

    private fun setVia(texto: String) = when (texto) {
        "AV" -> 0
        "BLVR" -> 1
        "CL" -> 2
        "CARR" -> 3
        "INT" -> 4
        "JR" -> 5
        "MCDO" -> 6
        "PJE" -> 7
        "P" -> 8
        "PLZA" -> 9
        else -> 999
    }

    private fun setZona(texto: String) = when (texto) {
        "AMPL" -> 0
        "ANEXO" -> 1
        "AAHH" -> 2
        "ASOC" -> 3
        "ASOC VIV" -> 4
        "BAR" -> 5
        "CC" -> 6
        "CP" -> 7
        "COMI" -> 8
        "COND" -> 9
        "CONJ HAB" -> 10
        "CR" -> 11
        "COOP" -> 12
        "COOP VIV" -> 13
        "HAB URB" -> 14
        "LOT" -> 15
        "PARC" -> 16
        "POB" -> 17
        "PJ" -> 18
        "QUINTA" -> 19
        "RES" -> 20
        "SECT" -> 21
        "UV" -> 22
        "URB" -> 23
        "VILLA" -> 24
        "ZI" -> 25
        else -> 999
    }
}