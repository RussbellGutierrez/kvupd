package com.upd.kvupd.ui.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import com.upd.kvupd.R
import com.upd.kvupd.data.model.Respuesta
import com.upd.kvupd.data.model.TEncuesta
import com.upd.kvupd.data.model.TRespuesta
import com.upd.kvupd.databinding.FragmentFEncuestaBinding
import com.upd.kvupd.utils.*
import com.upd.kvupd.utils.Constant.PROCEDE
import com.upd.kvupd.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

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
    private var abspath = ""
    private var necesario = false
    private var foto = false
    private val _tag by lazy { FEncuesta::class.java.simpleName }

    override fun onDestroy() {
        super.onDestroy()
        _bind = null
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
        bind.btnCancelar.setOnClickListener { returnFragment() }
        bind.btnSiguiente.setOnClickListener { storeAnswer() }
        bind.btnGuardar.setOnClickListener { saveRespuesta() }

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

    private fun returnFragment() {
        when (PROCEDE) {
            "Cliente" -> findNavController().navigate(R.id.action_FEncuesta_to_FCliente)
            "Vendedor" -> findNavController().navigate(R.id.action_FEncuesta_to_FVendedor)
            "Mapa" -> findNavController().navigate(R.id.action_FEncuesta_to_FMapa)
        }
    }

    private fun setup() {
        val mensaje = "Completó la encuesta del cliente ${args.cliente}"
        bind.txtCliente.text = args.cliente
        bind.txtFoto.text = args.cliente
        bind.txtMensaje.text = mensaje
        viewmodel.getPreguntas()
    }

    private fun drawQuestion() {
        if (posicion < total) {
            preguntas[posicion].let {
                hasPrevio(it) {
                    cleanFields(it)
                    when (it.tipo) {
                        "L" -> {
                            when (it.formato) {
                                "N" -> bind.edtLibre.inputType = InputType.TYPE_CLASS_NUMBER
                                "A" -> bind.edtLibre.inputType = InputType.TYPE_CLASS_TEXT
                            }
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
                        }
                    }
                }
            }
        } else {
            if (foto) {
                bind.cardPregunta.setUI("v", false)
                bind.cardFoto.setUI("v", true)
                bind.imgFoto.setOnClickListener { dispatchTakePictureIntent() }
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
            preguntas[posicion].let {
                when (it.tipo) {
                    "L" -> {
                        if (it.condicional) {
                            previo = "${it.pregunta}|${bind.edtLibre.text}"
                        }
                        val rsp = bind.edtLibre.text.toString()
                        val item = Respuesta(it.id, it.pregunta, rsp, "")
                        respuesta.add(item)
                        posicion++
                        drawQuestion()
                    }
                    "U" -> {
                        if (it.condicional) {
                            previo = "${it.pregunta}|$radiocheck"
                        }
                        val item = Respuesta(it.id, it.pregunta, radiocheck, "")
                        respuesta.add(item)
                        posicion++
                        drawQuestion()
                    }
                    "M" -> {
                        val item = Respuesta(it.id, it.pregunta, radiocheck, "")
                        respuesta.add(item)
                        posicion++
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
                    foto = false
                }
            }
            drawQuestion()
        }
    }

    private fun createPhoto(): File {
        val time = viewmodel.fecha(4).multiReplace(listOf(" ", "-", ":"), "_")
        val directory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("Kvupd_${time}_", ".jpg", directory).apply {
            abspath = absolutePath
        }
    }

    private fun thumbnailPhoto() {
        val bitmap = BitmapFactory.decodeFile(abspath)
        bind.txtRuta.setUI("v", true)
        bind.txtRuta.text = abspath
        //show on ImageView
        Glide
            .with(requireContext())
            .load(bitmap)
            .override(400, 500)
            .centerCrop()
            .into(bind.imgFoto)
        //save on smartphone
        /*Glide
            .with(requireContext())
            .load(bitmap)
            .centerCrop()
            .into(object : CustomViewTarget<ImageView, Drawable>(bind.imgFoto) {
                override fun onLoadFailed(errorDrawable: Drawable?) {}

                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    try {
                        bind.imgFoto.setImageDrawable(resource)
                        val out = FileOutputStream(abspath)
                        resource.toBitmap().compress(Bitmap.CompressFormat.JPEG, 70, out)
                        out.flush()
                        out.close()
                        toast("Photo saved!")
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                override fun onResourceCleared(placeholder: Drawable?) {}
            })*/
        //.into(GlideFileTarget(this,abspath,bind.imgFoto,400,500))
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

    private fun saveRespuesta() {
        val list = mutableListOf<TRespuesta>()
        val cliente = args.cliente.split("-")[0].trim().toInt()
        val fecha = viewmodel.fecha(4)
        respuesta.forEach {
            val item = TRespuesta(cliente, fecha, it.encuesta, it.pregunta, it.respuesta, it.ruta,"Pendiente")
            list.add(item)
        }
        viewmodel.savingRespuestas(list)
        returnFragment()
    }

}