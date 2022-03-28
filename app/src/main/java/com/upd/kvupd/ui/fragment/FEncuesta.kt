package com.upd.kvupd.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.upd.kvupd.data.model.Respuesta
import com.upd.kvupd.data.model.TEncuesta
import com.upd.kvupd.databinding.FragmentFEncuestaBinding
import com.upd.kvupd.utils.setUI
import com.upd.kvupd.utils.snack
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FEncuesta : Fragment() {

    private val viewmodel by activityViewModels<AppViewModel>()
    private var _bind: FragmentFEncuestaBinding? = null
    private val args: FEncuestaArgs by navArgs()
    private val bind get() = _bind!!
    private var preguntas = listOf<TEncuesta>()
    private var respuesta = mutableListOf<Respuesta>()
    private var encuesta = 0
    private var posicion = 0
    private var total = 0
    private var previo = ""
    private var radiocheck = ""
    private var necesario = false
    private var foto = false
    private val _tag by lazy { FEncuesta::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(_tag,"Datos iniciales: encuesta $encuesta, posicion $posicion, total $total, previo $previo")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentFEncuestaBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setup()

        bind.edtLibre.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (necesario && p0 == "") {
                    bind.btnSiguiente.setUI("v", false)
                } else {
                    bind.btnSiguiente.setUI("v", true)
                }
            }

            override fun afterTextChanged(p0: Editable?) = Unit
        })
        bind.btnCancelar.setOnClickListener { }
        bind.btnSiguiente.setOnClickListener { storeAnswer() }
        bind.btnGuardar.setOnClickListener { Log.d(_tag,"Respuestas $respuesta") }

        viewmodel.preguntas.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { y ->
                if (!y.isNullOrEmpty()) {
                    preguntas = y
                    encuesta = y[0].id
                    foto = preguntas.find { j -> j.foto } != null
                    total = preguntas.size
                    drawQuestion()
                }
            }
        }
    }

    private fun setup() {
        val mensaje = "Completo la encuesta del cliente ${args.cliente}"
        bind.txtCliente.text = args.cliente
        bind.txtFoto.text = args.cliente
        bind.txtMensaje.text = mensaje
        bind.txtEncuesta
        bind.lnrPregunta
        bind.txtPregunta
        bind.rbUnico
        bind.lnrMultiple
        bind.txtLibre
        bind.edtLibre
        bind.cardPregunta
        bind.cardFoto
        bind.cardMensaje
        viewmodel.getPreguntas()
    }

    private fun drawQuestion() {
        if (posicion < total) {
            Log.w(_tag,"Compare $posicion < $total")
            preguntas[posicion].let {
                Log.w(_tag, "Pregunta $it")
                hasPrevio(it) {
                    cleanFields(it)
                    when (it.tipo) {
                        "L" -> {
                            when (it.formato) {
                                "N" -> bind.edtLibre.inputType = InputType.TYPE_CLASS_NUMBER
                                "A" -> bind.edtLibre.inputType = InputType.TYPE_CLASS_TEXT
                            }
                            posicion++
                        }
                        "U" -> {
                            it.respuesta.split("|").forEach { y ->
                                val rb = RadioButton(requireActivity())
                                rb.text = y
                                bind.rbUnico.addView(rb)
                            }
                            val child = bind.rbUnico.childCount
                            bind.rbUnico.setOnCheckedChangeListener { _, checked ->
                                for (i in 0 until child) {
                                    val rb = bind.rbUnico.getChildAt(i) as RadioButton
                                    if (checked == rb.id) {
                                        radiocheck = rb.text.toString()
                                        bind.btnSiguiente.setUI("v", true)
                                    }
                                }
                            }
                            posicion++
                        }
                        "M" -> {
                            val checkbox = arrayListOf<CheckBox>()
                            it.respuesta.split("|").forEach { y ->
                                val cb = CheckBox(requireActivity())
                                cb.text = y
                                checkbox.add(cb)
                                bind.lnrMultiple.addView(cb)
                            }
                            checkbox.forEach { y ->
                                y.setOnCheckedChangeListener { _, checked ->
                                    if (checked) {
                                        if (radiocheck == "") {
                                            radiocheck = y.text.toString()
                                        } else {
                                            radiocheck += "|${y.text}"
                                        }
                                        bind.btnSiguiente.setUI("v", true)
                                    } else {
                                        when {
                                            radiocheck.contains("|${y.text}") -> radiocheck =
                                                radiocheck.replace("|${y.text}", "")
                                            radiocheck.contains("${y.text}|") -> radiocheck =
                                                radiocheck.replace("${y.text}|", "")
                                            radiocheck.contains(y.text) -> radiocheck =
                                                radiocheck.replace(y.text.toString(), "")
                                        }
                                        if (radiocheck == "") {
                                            if (necesario) {
                                                bind.btnSiguiente.setUI("v", false)
                                            }
                                        }
                                    }
                                }
                            }
                            posicion++
                        }
                    }
                }
            }
            Log.w(_tag, "Posicion after draw $posicion")
        } else {
            if (foto) {
                bind.cardPregunta.setUI("v", false)
                bind.cardFoto.setUI("v", true)
                bind.imgFoto.setOnClickListener {  }
                bind.txtRuta
            } else {
                bind.lnrPregunta.setUI("v", false)
                bind.cardMensaje.setUI("v", true)
            }
        }
    }

    private fun cleanFields(pregunta: TEncuesta) {
        bind.txtEncuesta.text = pregunta.nombre
        bind.txtPregunta.text = pregunta.descripcion

        when (pregunta.tipo) {
            "L" -> {
                bind.rbUnico.setUI("v", false)
                bind.lnrMultiple.setUI("v", false)
                bind.txtLibre.setUI("v", true)
            }
            "U" -> {
                bind.rbUnico.setUI("v", true)
                bind.lnrMultiple.setUI("v", false)
                bind.txtLibre.setUI("v", false)
            }
            "M" -> {
                bind.rbUnico.setUI("v", false)
                bind.lnrMultiple.setUI("v", true)
                bind.txtLibre.setUI("v", false)
            }
        }
        bind.edtLibre.setText("")
        bind.rbUnico.removeAllViews()
        bind.lnrMultiple.removeAllViews()

        necesario = pregunta.necesaria

        if (necesario) {
            bind.btnSiguiente.setUI("v", false)
        } else {
            bind.btnSiguiente.setUI("v", true)
        }
    }

    private fun hasPrevio(pregunta: TEncuesta, T: () -> Unit) {
        Log.e(_tag,"Posicion $posicion")
        if (pregunta.previa == 0) {
            T()
        } else {
            if (previo == "") {
                T()
            } else {
                val p = previo.split("|")
                if (p[0].toInt() == pregunta.previa && p[1] == pregunta.eleccion) {
                    T()
                } else {
                    posicion++
                    drawQuestion()
                }
            }
        }
    }

    private fun storeAnswer() {
        if (posicion < total) {
            preguntas[posicion - 1].let {
                when (it.tipo) {
                    "L" -> {
                        if (it.condicional) {
                            previo = "${it.pregunta}|${bind.edtLibre.text}"
                        }
                        val rsp = bind.edtLibre.text.toString()
                        val item = Respuesta(it.id, it.pregunta, rsp, "")
                        respuesta.add(item)
                        drawQuestion()
                    }
                    "U" -> {
                        if (it.condicional) {
                            previo = "${it.pregunta}|$radiocheck"
                        }
                        val item = Respuesta(it.id, it.pregunta, radiocheck, "")
                        respuesta.add(item)
                        drawQuestion()
                    }
                    "M" -> {
                        val item = Respuesta(it.id, it.pregunta, radiocheck, "")
                        respuesta.add(item)
                        drawQuestion()
                    }
                    else -> "Nothing"
                }
            }
        } else {
            if (foto) {
                val rt = bind.txtRuta.text.toString()
                if (rt == "") {
                    snack("Debe tomar una foto para la encuesta")
                } else {
                    val item = Respuesta(encuesta, 0, "", rt)
                    respuesta.add(item)
                    drawQuestion()
                }
            }
        }
    }

    /*private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager).also {
                val photo: File? = try{
                    crearArchivoImagen()
                }catch (ex: IOException) {
                    Log.e(_tag,"Error ->${ex.stackTrace} ->${ex.message}")
                    null
                }
                photo?.also {
                    val fotoURI: Uri = FileProvider.getUriForFile(requireContext(),"com.upd.kvupd",it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,fotoURI)
                    startActivityForResult(takePictureIntent, SOLICITAR_FOTO)
                }
            }
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val codigo = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)!![0]
            bind.searchView.setQuery(codigo, true)
        } else {
            snack("Error procesando codigo")
        }
    }*/
}