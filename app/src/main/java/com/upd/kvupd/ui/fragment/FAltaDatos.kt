package com.upd.kvupd.ui.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.upd.kvupd.R
import com.upd.kvupd.data.model.TADatos
import com.upd.kvupd.data.model.TAFoto
import com.upd.kvupd.data.model.TNegocio
import com.upd.kvupd.data.model.asSpinner
import com.upd.kvupd.data.model.toSpinner
import com.upd.kvupd.databinding.FragmentFAltaDatosBinding
import com.upd.kvupd.utils.Constant.ALTADATOS
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.checkDocumento
import com.upd.kvupd.utils.checkEmail
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.hideprogress
import com.upd.kvupd.utils.multiReplace
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.utils.showDialog
import com.upd.kvupd.utils.snack
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class FAltaDatos : Fragment(), MenuProvider, OnItemSelectedListener {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFAltaDatosBinding? = null
    private val bind get() = _bind!!
    private var tipo = ""
    private var abspath = ""
    private var negocios = listOf<TNegocio>()
    private var distrito = listOf<String>()
    private var giro = listOf<String>()
    private var subgiro = listOf<String>()
    private var ruta = listOf<String>()
    private val args: FAltaDatosArgs by navArgs()
    private var adStored: TADatos? = null
    private val _tag by lazy { FAltaDatos::class.java.simpleName }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null
        adStored = null
    }

    /**Agregar campos dnice,ruc,tdoc,imei, todos string**/
    /**Cuando es persona juridica, dni y ruc son obligatorios**/
    /**Cuando es persona natural, dni es obligatorio y ruc opcional**/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentFAltaDatosBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        bind.spnDocumento.onItemSelectedListener = this
        bind.spnGiro.onItemSelectedListener = this
        bind.rbGrupo.setOnCheckedChangeListener { _, id ->
            when (id) {
                bind.rbJuridica.id -> {
                    tipo = "PJ"
                    setDataSpinner(tipo)
                    showFields(0)
                }

                bind.rbNatural.id -> {
                    tipo = "PN"
                    setDataSpinner(tipo)
                    showFields(1)
                }
            }
        }

        bind.imgFoto.setOnClickListener { dispatchTakePictureIntent() }

        viewmodel.distritosObs().observe(viewLifecycleOwner) {
            distrito = it.asSpinner()
            bind.spnDistrito.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                distrito
            )
            setupFields()
        }
        viewmodel.negociosObs().observe(viewLifecycleOwner) { y ->
            negocios = y
            giro = y.distinctBy { it.giro }.asSpinner(0)
            getAndSetSubGiros("2")
            bind.spnGiro.adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, giro)
            setupFields()
        }
        viewmodel.rutasObs().observe(viewLifecycleOwner) {
            ruta = it.toSpinner()
            bind.spnRuta.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                ruta
            )
            setupFields()
        }
        viewmodel.altadatos.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled().let { y ->
                if (y != null) {
                    adStored = y
                }
            }
        }

        checkAlta()
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.spn_documento -> processDocumento(position)
            R.id.spn_giro -> {
                val giro = parent.getItemAtPosition(position).toString().split(" - ")[0]
                getAndSetSubGiros(giro)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) = Unit

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.altadatos_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.guardar -> consume { saveAltaDatos() }
        else -> false
    }

    private fun setDataSpinner(tipo: String) {
        var array = 0
        when (tipo) {
            "PJ" -> array = R.array.docsjuridico
            "PN" -> array = R.array.docsnatural
        }
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bind.spnDocumento.adapter = adapter
    }

    private fun cleanFields() {
        bind.edtRazon.setText("")
        bind.edtPaterno.setText("")
        bind.edtMaterno.setText("")
        bind.edtNombre.setText("")
        bind.edtDnice.setText("")
        bind.edtRuc.setText("")
        bind.spnDocumento.setSelection(0)
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
        bind.spnGiro.setSelection(0)
        bind.spnRuta.setSelection(0)
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

    private fun processDocumento(id: Int) {
        when (id) {
            0 -> {
                bind.inlRuc.setUI("v", false)
                bind.inlDnice.setUI("v", false)
                bind.txtMensaje.setUI("v", false)
                bind.edtRuc.setText("")
                bind.edtDnice.setText("")
            }

            1 -> {
                bind.inlRuc.setUI("v", true)
                bind.inlDnice.setUI("v", true)
                bind.txtMensaje.setUI("v", true)
                bind.txtMensaje.text = "* EL RUC ES OBLIGATORIO"
            }

            else -> {
                bind.inlDnice.setUI("v", true)
                bind.txtMensaje.setUI("v", true)
                bind.inlRuc.setUI("v", false)
                bind.txtMensaje.text = "* EL DNI/CARNET ES OBLIGATORIO"
                bind.edtRuc.setText("")
            }
        }
    }

    private fun setupFields() {
        hideprogress()
        if (adStored != null) {
            if (distrito.isNotEmpty() && giro.isNotEmpty() && subgiro.isNotEmpty() && ruta.isNotEmpty()) {

                Log.w(_tag, "Distrito: ${distrito.size}")
                Log.w(_tag, "Giro: ${giro.size}")
                Log.w(_tag, "Ruta: ${ruta.size}")

                if (adStored?.tipo == "PJ") {
                    bind.rbJuridica.isChecked = true
                    setDataSpinner("PJ")
                } else {
                    bind.rbNatural.isChecked = true
                    setDataSpinner("PN")
                }

                val ald = distrito.indexOf(adStored?.distrito)
                val alr = ruta.indexOf(adStored?.ruta)

                /*** @param adStored.giro pertenece a la tabla TADatos, es el subgiro que se envia al servidor ***/
                val codigo = adStored!!.giro.split(" - ")[0]
                val auxItem = negocios.find { it.codigo == codigo }!!
                val auxGiro = "${auxItem.giro} - ${auxItem.descripcion}"
                val alg = giro.indexOf(auxGiro)
                getAndSetSubGiros(auxItem.giro)
                val alsg = subgiro.indexOf(adStored?.giro)

                Log.w(_tag, "Distrito: ${distrito.size}")
                Log.w(_tag, "Giro: ${giro.size}")
                Log.w(_tag, "Ruta: ${ruta.size}")

                Log.w(_tag, "Distrito pos: $ald")
                Log.w(_tag, "Giro pos: $alg")
                Log.w(_tag, "Ruta pos: $alr")

                bind.edtRazon.setText(adStored?.razon)
                bind.edtPaterno.setText(adStored?.appaterno)
                bind.edtMaterno.setText(adStored?.apmaterno)
                bind.edtNombre.setText(adStored?.nombre)
                bind.edtDnice.setText(adStored?.dnice)
                bind.edtRuc.setText(adStored?.ruc)
                bind.spnDocumento.setSelection(setDocumento(adStored!!.tipodocu))
                processDocumento(setDocumento(adStored!!.tipodocu))
                //bind.edtDocumento.setText(adStored.documento)
                bind.edtMovil1.setText(adStored?.movil1)
                bind.edtMovil2.setText(adStored?.movil2)
                bind.edtCorreo.setText(adStored?.correo)
                bind.spnVia.setSelection(setVia(adStored!!.via))
                bind.edtManzana.setText(adStored?.manzana)
                bind.edtDireccion.setText(adStored?.direccion)
                bind.spnNumero.setSelection(setUbicacion(adStored!!.ubicacion))
                bind.edtNumero.setText(adStored?.numero)
                bind.spnZona.setSelection(setZona(adStored!!.zona))
                bind.edtZona.setText(adStored?.zonanombre)
                //bind.edtRuta.setText(adStored?.ruta)
                bind.edtSecuencia.setText(adStored?.secuencia)
                bind.spnRuta.setSelection(alr)
                bind.spnDistrito.setSelection(ald)
                bind.spnGiro.setSelection(alg)
                bind.spnSubgiro.setSelection(alsg)
                if (adStored?.dniruta != "") {
                    thumbnailPhoto(adStored!!.dniruta, true)
                }
            }
        }
    }

    private fun getAndSetSubGiros(giro: String) {
        subgiro = negocios.filter { it.giro == giro }.asSpinner(1)
        bind.spnSubgiro.adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                subgiro
            )
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager).also {
                val photo: File? = try {
                    createPhoto()
                } catch (ex: IOException) {
                    Log.e(_tag, "Error ->${ex.stackTrace} ->${ex.message}")
                    null
                }
                photo?.also {
                    val uriPhoto = FileProvider.getUriForFile(requireContext(), "com.upd.kvupd", it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriPhoto)
                    resultLauncher.launch(takePictureIntent)
                }
            }
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                thumbnailPhoto("", false)
            } else {
                snack("Error procesando foto")
            }
        }

    private fun createPhoto(): File {
        val time = viewmodel.fecha(4).multiReplace(listOf(" ", "-", ":"), "_")
        val directory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("Kvupd_DNI_${time}_", ".jpg", directory).apply {
            abspath = absolutePath
        }
    }

    private fun thumbnailPhoto(path: String, show: Boolean) {
        val ruta: String = if (show) {
            path
        } else {
            abspath
        }
        val bitmap = BitmapFactory.decodeFile(ruta)
        bind.txtRuta.setUI("v", true)
        bind.txtRuta.text = abspath
        Glide
            .with(requireContext())
            .load(bitmap)
            .override(500, 600)
            .fitCenter()
            .into(object : CustomViewTarget<ImageView, Drawable>(bind.imgFoto) {
                override fun onLoadFailed(errorDrawable: Drawable?) {}

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    try {
                        bind.imgFoto.setImageDrawable(resource)
                        val out = FileOutputStream(abspath)
                        resource.toBitmap().compress(Bitmap.CompressFormat.JPEG, 80, out)
                        out.flush()
                        out.close()
                        if (!show) {
                            snack("Foto almacenada")
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun onResourceCleared(placeholder: Drawable?) {}
            })
    }

    private fun saveAltaDatos() {
        val razon = bind.edtRazon.text.toString().trim().uppercase()
        val paterno = bind.edtPaterno.text.toString().trim().uppercase()
        val materno = bind.edtMaterno.text.toString().trim().uppercase()
        val nombre = bind.edtNombre.text.toString().trim().uppercase()
        val dnice = bind.edtDnice.text.toString().trim().uppercase()
        val ruc = bind.edtRuc.text.toString().trim().uppercase()
        val tipodoc = getDocumento()
        //val documento = bind.edtDocumento.text.toString().trim().uppercase()
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
        val subgiro = bind.spnSubgiro.selectedItem.toString()
        val ruta = bind.spnRuta.selectedItem.toString()
        val secuencia = bind.edtSecuencia.text.toString().trim()

        when {
            tipo == "PJ" && razon == "" -> showDialog("Advertencia", "Ingrese una razon social") {}
            tipo == "PN" && (paterno == "" || materno == "" || nombre == "") -> showDialog(
                "Advertencia",
                "Ingrese nombre y apellidos del cliente"
            ) {}

            tipo == "PJ" && ruc == "" -> showDialog(
                "Advertencia",
                "Debe ingresar el RUC de la empresa"
            ) {}
            /*tipo == "PJ" && ruc == "" && dnice == "" -> showDialog(
                "Advertencia",
                "Debe registrar RUC y DNI"
            ) {}
            tipo == "PN" && dnice == "" -> showDialog(
                "Advertencia",
                "Debe registrar un DNI o CARNET EXTRANJERIA"
            ) {}*/
            //documento == "" -> showDialog("Error", "Ingrese documento de identificacion") {}
            movil1 == "" && movil2 == "" -> showDialog(
                "Advertencia",
                "Ingrese un numero de celular"
            ) {}

            correo != "" && !correo.checkEmail() -> showDialog(
                "Advertencia",
                "Ingrese un correo valido"
            ) {}

            tipodoc == "Ninguno" -> showDialog(
                "Advertencia",
                "Seleccione el documento a registrar"
            ) {}

            tipodoc == "RUC" && ruc == "" -> showDialog(
                "Advertencia",
                "Si eligio RUC, debe completar el campo"
            ) {}

            tipodoc == "DNI" && dnice == "" -> showDialog(
                "Advertencia",
                "Si eligio DNI, debe completar el campo"
            ) {}

            tipodoc == "CE" && dnice == "" -> showDialog(
                "Advertencia",
                "Si eligio CARNET EXTRANJERIA, debe completar el campo"
            ) {}

            numero == "" -> showDialog("Advertencia", "Ingrese el numero de calle") {}
            secuencia == "" -> showDialog("Advertencia", "Ingrese la secuencia") {}
            numero.toInt() == 0 -> showDialog(
                "Advertencia",
                "Ingrese un numero valido para calle"
            ) {}

            secuencia.toInt() == 0 -> showDialog("Advertencia", "Ingrese una secuencia valida") {}
            /*bind.txtRuta.text == "" -> showDialog(
                "Advertencia",
                "Debe tomar una foto del documento del cliente"
            ) {}*/
            subgiro == "" -> showDialog("Advertencia", "Debe seleccionar un subgiro") {}
            ruc != "" && !ruc.checkDocumento(tipo) -> showDialog(
                "Advertencia",
                "-RUC -> 11 dígitos\n-RUC jurídico inicia con 20 (solo empresas)\n-RUC natural inicia con 10 o 15 (solo personas naturales)"
            ) {}

            dnice != "" && !dnice.checkDocumento(tipo) -> showDialog(
                "Advertencia",
                "-DNI -> 8 dígitos\n-EXTRANJERIA -> 9 dígitos"
            ) {}

            else -> {
                val dnipath = bind.txtRuta.text.toString()
                val item = TADatos(
                    args.idaux,
                    CONF.codigo,
                    tipo,
                    razon,
                    nombre,
                    paterno,
                    materno,
                    ruc,
                    dnice,
                    tipodoc,
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
                    subgiro,
                    ruta,
                    secuencia,
                    dnipath,
                    "Pendiente"
                )
                viewmodel.saveAltaDatos(item)
                if (dnipath != "") {
                    val dni =
                        TAFoto(
                            args.idaux,
                            CONF.codigo,
                            dnipath,
                            viewmodel.fecha(6),
                            "Pendiente"
                        )
                    viewmodel.savingDNI(dni)
                }
                when (ALTADATOS) {
                    "lista" -> findNavController().navigate(R.id.action_FAltaDatos_to_FAlta)
                    "mapa" -> findNavController().navigate(R.id.action_FAltaDatos_to_FAltaMapa)
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

    private fun getDocumento() = when (bind.spnDocumento.selectedItem.toString()) {
        "RUC" -> "RUC"
        "DNI" -> "DNI"
        "CARNET" -> "CE"
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

    private fun setDocumento(texto: String) = when (texto) {
        "RUC" -> 1
        "DNI" -> 2
        "CE" -> 3
        else -> 0
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