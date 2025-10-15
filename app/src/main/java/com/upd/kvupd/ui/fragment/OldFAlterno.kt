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
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.upd.kvupd.databinding.FragmentFAlternoBinding
import com.upd.kvupd.service.ServicePosicion
import com.upd.kvupd.utils.OldConstant.POS_LOC
import com.upd.kvupd.utils.OldConstant.isPOSLOCinitialized
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.utils.snack
import com.upd.kvupd.viewmodel.OldAppViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class OldFAlterno : Fragment() {

    private val viewmodel by activityViewModels<OldAppViewModel>()
    private val args: OldFAlternoArgs by navArgs()
    private var _bind: FragmentFAlternoBinding? = null
    private val bind get() = _bind!!
    private var clienteDelMapa = 0
    private var countList = 0
    private var abspath = ""
    private var foto = false
    private var longitud = 0.0
    private var latitud = 0.0
    //private var listaClientesAux = mutableListOf<TClientes>()
    private var eliminarClientes = mutableListOf<Int>()
    private var listaClientesLimpia = mutableListOf<Int>()
    private var clienteSeleccionado = 0
    //private lateinit var listaPreguntas: List<TEncuesta>
    private val _tag by lazy { OldFAlterno::class.java.simpleName }

    override fun onDestroy() {
        super.onDestroy()
        _bind = null
        requireContext().stopService(Intent(requireContext(), ServicePosicion::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //viewmodel.launchPosition()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentFAlternoBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clienteDelMapa = args.cliente

        /*viewmodel.clienteRoom.observe(viewLifecycleOwner) { it ->
            it.getContentIfNotHandled()?.let { clientes ->
                processClientList(clientes)
            }
        }

        viewmodel.preguntas.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { preguntasEncuesta ->
                if (preguntasEncuesta.isNotEmpty()) {
                    val nombreEncuesta = preguntasEncuesta.firstOrNull()?.nombre.orEmpty()
                    bind.txtTitulo.text = nombreEncuesta
                    foto = preguntasEncuesta.any { y -> y.foto }
                    listaPreguntas = preguntasEncuesta
                }
                drawPregunta(preguntasEncuesta)
            }
        }

        viewmodel.cabecera.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                listaClientesAux.forEach { cliente ->
                    viewmodel.respuestaPreviaCliente(y, cliente.idcliente)
                }
            }
        }

        viewmodel.respuestaPrevia.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                if (y.respondio) {
                    eliminarClientes.add(y.cliente)
                }
                setClientesLimpios(eliminarClientes)
            }
        }

        bind.autoCliente.setOnClickListener {
            bind.autoCliente.showDropDown()
        }
        bind.autoCliente.setOnItemClickListener { _, _, position, _ ->
            setupUI(position)
        }
        bind.imgFoto.setOnClickListener { dispatchTakePictureIntent() }
        bind.btnGuardar.setOnClickListener {
            val respuestas = viewmodel.respuestas
            val contenedor = bind.lnrPreguntas

            val faltantes = listaPreguntas.filter { pregunta ->
                pregunta.necesaria &&
                        contenedor.children.any { it.tag == pregunta.pregunta && it.visibility == View.VISIBLE } &&
                        (respuestas[pregunta.pregunta].isNullOrBlank())
            }

            if (foto && abspath.isBlank()) {
                showDialog("Advertencia", "Debe tomar una foto del negocio") {}
                return@setOnClickListener
            }

            if (faltantes.isNotEmpty()) {
                showDialog("Advertencia", "Responda todas las preguntas obligatorias por favor") {}
                return@setOnClickListener
            }

            saveRespuestasVisibles()
        }

        viewmodel.getClientes()
        viewmodel.getPreguntas()*/
    }

    private fun setupUI(position: Int) {
        // Asignamos el elemento seleccionado a una variable global
        clienteSeleccionado = listaClientesLimpia[position]

        // Limpiar respuestas
        //viewmodel.limpiarRespuestas()

        // Redibujar preguntas desde la lista original
        //drawPregunta(listaPreguntas)

        // Iniciamos todos los views de la encuesta
        bind.lnrPreguntas.setUI("v", true)
        bind.cardFoto.setUI("v", foto)
        bind.btnGuardar.setUI("v", true)
    }

    /*private fun processClientList(clientes: List<TClientes>) {
        listaClientesAux.clear()
        listaClientesAux.addAll(clientes)
        viewmodel.gettingEncuestaLista()
    }

    private fun setClientesLimpios(eliminar: List<Int>) {
        countList += 1
        if (countList == listaClientesAux.size) {

            // Se completaron las iteraciones y limpiamos el contador
            countList = 0

            // Limpiamos de la lista los clientes que ya respondieron
            listaClientesLimpia.clear()
            listaClientesAux.removeIf { it.idcliente in eliminar }

            // Creamos las listas con los nombres y poblamos la nueva lista limpia
            val emoji = "\uD83D\uDE35"
            val codigosClientes = listaClientesAux.map { it.idcliente }
            val nombresClientes = listaClientesAux.map { cliente ->
                val prefijoEmoji = if (cliente.ventanio == 1) "$emoji " else ""
                "$prefijoEmoji${cliente.idcliente} - ${cliente.nomcli}"
            }.toMutableList()
            nombresClientes.add("Persona externa al padron")

            listaClientesLimpia.addAll(codigosClientes)
            listaClientesLimpia.add(0)

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                nombresClientes
            )
            bind.autoCliente.setAdapter(adapter)

            // Selección automática si el argumento no es 0
            if (clienteDelMapa != 0) {
                val index = listaClientesLimpia.indexOf(clienteDelMapa)
                if (index != -1) {
                    bind.autoCliente.setText(nombresClientes[index], false)
                    setupUI(index)
                }
            }
        }
    }*/

    /*private fun drawPregunta(preguntas: List<TEncuesta>) {
        val contenedor = bind.lnrPreguntas
        contenedor.removeAllViews()

        preguntas.forEach { item ->
            val insideBinding = RowAlternoBinding.inflate(layoutInflater, contenedor, false)
            val view = insideBinding.root

            // Título
            insideBinding.txtPregunta.text = item.descripcion

            // Mostrar si es obligatorio
            insideBinding.txtObligatorio.setUI("v", item.necesaria)

            when (item.tipo) {
                "U" -> {
                    insideBinding.rbUnico.setUI("v", true)
                    item.respuesta.split("|").forEach { opcion ->
                        val radio = RadioButton(requireContext()).apply {
                            text = opcion
                            setOnClickListener {
                                viewmodel.actualizarRespuesta(item.pregunta, opcion)
                                actualizarVisibilidadCondicional()
                            }
                        }
                        insideBinding.rbUnico.addView(radio)
                    }
                }

                "M" -> {
                    insideBinding.lnrMultiple.setUI("v", true)
                    item.respuesta.split("|").forEach { opcion ->
                        val check = CheckBox(requireContext()).apply {
                            text = opcion
                            setOnCheckedChangeListener { _, _ ->
                                val seleccionadas = insideBinding.lnrMultiple.children
                                    .filterIsInstance<CheckBox>()
                                    .filter { it.isChecked }
                                    .joinToString("|") { it.text.toString() }
                                viewmodel.actualizarRespuesta(item.pregunta, seleccionadas)
                                actualizarVisibilidadCondicional()
                            }
                        }
                        insideBinding.lnrMultiple.addView(check)
                    }
                }

                "L" -> {
                    insideBinding.txtLibre.setUI("v", true)
                    insideBinding.edtLibre.doAfterTextChanged {
                        viewmodel.actualizarRespuesta(item.pregunta, it.toString())
                        actualizarVisibilidadCondicional()
                    }
                }
            }

            // Guardar como tag para poder identificar la vista luego
            view.tag = item.pregunta

            // Si tiene previa y eleccion definidos se oculta(se evaluará luego)
            val debeOcultarse = item.previa != 0 && item.eleccion.isNotBlank()
            view.setUI("v", !debeOcultarse)

            // Asignamos el view creado al contenedor del parent
            contenedor.addView(view)
        }
    }*/

    /*private fun actualizarVisibilidadCondicional() {
        val respuestas = viewmodel.respuestas
        val contenedor = bind.lnrPreguntas

        listaPreguntas.forEach { pregunta ->
            // Verificamos si la pregunta depende de otra (tiene previa y elección definidas)
            if (pregunta.previa != 0 && pregunta.eleccion.isNotBlank()) {
                val respuestaPrevia = respuestas[pregunta.previa]
                val cumple = when {
                    // Si la previa es de tipo múltiple, puede venir separada por |
                    listaPreguntas.firstOrNull { it.pregunta == pregunta.previa }?.tipo == "M" ->
                        respuestaPrevia?.split("|")?.contains(pregunta.eleccion) == true

                    else -> respuestaPrevia.equals(pregunta.eleccion, ignoreCase = true)
                }

                val view = contenedor.children.firstOrNull { it.tag == pregunta.pregunta }
                view?.setUI("v", cumple)
            }
        }
    }*/

    private fun createPhoto(): File {
        val time = ""//viewmodel.fecha(4).multiReplace(listOf(" ", "-", ":"), "_")
        val directory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("Kvupd_${time}_", ".jpg", directory).apply {
            abspath = absolutePath
        }
    }

    private fun thumbnailPhoto() {
        val bitmap = BitmapFactory.decodeFile(abspath)
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
                        snack("Foto almacenada")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun onResourceCleared(placeholder: Drawable?) {}
            })
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
                thumbnailPhoto()
            } else {
                snack("Error procesando foto")
            }
        }

    private fun getCoordenadas() {
        if (isPOSLOCinitialized() && (POS_LOC.longitude != 0.0 && POS_LOC.latitude != 0.0)) {
            longitud = POS_LOC.longitude
            latitud = POS_LOC.latitude
        }
    }

    private fun convertClienteCodigo(): String {
        return if (clienteSeleccionado > 0) {
            clienteSeleccionado.toString()
        } else {
            val formato = SimpleDateFormat("HHmmss", Locale.getDefault())
            val hora24h = formato.format(Date())
            ""//"${CONF.codigo}$hora24h"
        }
    }

    private fun saveRespuestasVisibles() {
        val contenedor = bind.lnrPreguntas
        /*val respuestas = viewmodel.respuestas
        val fecha = viewmodel.fecha(4)

        getCoordenadas()
        val codigoCliente = convertClienteCodigo()

        val listaGuardar = listaPreguntas
            .filter { pregunta ->
                contenedor.children.any { it.tag == pregunta.pregunta && it.visibility == View.VISIBLE } &&
                        respuestas.containsKey(pregunta.pregunta)
            }
            .map { pregunta ->
                TRespuesta(
                    cliente = codigoCliente,
                    fecha = fecha,
                    encuesta = pregunta.id,
                    pregunta = pregunta.pregunta,
                    respuesta = respuestas[pregunta.pregunta].orEmpty(),
                    rutafoto = abspath,
                    foto = if (foto) 1 else 0,
                    longitud = longitud,
                    latitud = latitud,
                    estado = "Pendiente"
                )
            }

        viewmodel.savingRespuestas(listaGuardar)
        if (DESTINO_NAV == "mapa") {
            DESTINO_NAV = "base"
            findNavController().navigate(R.id.action_FAlterno_to_FMapa)
        } else {
            findNavController().navigate(R.id.action_FAlterno_to_FBase)
        }*/
        snack("Encuesta guardada correctamente")
    }
}