package com.upd.kvupd.ui.fragment.encuesta

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.location.Location
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
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.MenuProvider
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.upd.kvupd.R
import com.upd.kvupd.data.model.FlowHeaderEncuestas
import com.upd.kvupd.data.model.JsonEncuesta
import com.upd.kvupd.data.model.TableFoto
import com.upd.kvupd.data.model.TableRespuesta
import com.upd.kvupd.databinding.FlowRowPreguntasBinding
import com.upd.kvupd.databinding.FragmentFEncuestaBinding
import com.upd.kvupd.ui.dialog.SeleccionEncuesta
import com.upd.kvupd.ui.fragment.encuesta.enumFile.EstadoEncuesta
import com.upd.kvupd.ui.fragment.encuesta.enumFile.TipoPregunta
import com.upd.kvupd.ui.fragment.encuesta.mapper.toClienteUI
import com.upd.kvupd.ui.fragment.encuesta.mapper.toEncuestaUI
import com.upd.kvupd.ui.fragment.encuesta.mapper.toPreguntaUI
import com.upd.kvupd.ui.fragment.encuesta.modelUI.ClienteUI
import com.upd.kvupd.ui.fragment.encuesta.modelUI.PreguntaUI
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.ui.sealed.ResultadoApi
import com.upd.kvupd.utils.FechaHoraUtil
import com.upd.kvupd.utils.GPSConstants.GPS_INTERVALO_NORMAL
import com.upd.kvupd.utils.GPSConstants.GPS_INTERVALO_RAPIDO
import com.upd.kvupd.utils.GPSConstants.IGNORAR_METROS
import com.upd.kvupd.utils.GPSConstants.TRACKER_RAPIDO
import com.upd.kvupd.utils.InstanciaDialog.REFERENCIA_DIALOG
import com.upd.kvupd.utils.InstanciaDialog.cerrarDialogActual
import com.upd.kvupd.utils.MaterialDialogTexto
import com.upd.kvupd.utils.MaterialDialogTexto.T_ERROR
import com.upd.kvupd.utils.MaterialDialogTexto.T_SUCCESS
import com.upd.kvupd.utils.MaterialDialogTexto.T_WARNING
import com.upd.kvupd.utils.buildMaterialDialog
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.utils.consume
import com.upd.kvupd.utils.gone
import com.upd.kvupd.utils.gps.GpsTracker
import com.upd.kvupd.utils.snack
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.utils.visible
import com.upd.kvupd.utils.visibleIf
import com.upd.kvupd.viewmodel.ALLViewModel
import com.upd.kvupd.viewmodel.APIViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.File
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class FEncuesta : Fragment(), MenuProvider {

    private val apiViewModel by activityViewModels<APIViewModel>()
    private val localViewmodel by activityViewModels<ALLViewModel>()
    private val binding by viewBinding(FragmentFEncuestaBinding::bind)

    private var requiereFoto = false
    private var clienteActual: ClienteUI? = null
    private var getLocation: Location? = null
    private var estadoEncuesta = EstadoEncuesta.INIT
    private var preguntasActuales: List<PreguntaUI> = emptyList()
    private var cabecerasCache: List<FlowHeaderEncuestas> = emptyList()
    private lateinit var adapterAutoComplete: ArrayAdapter<ClienteUI>
    private val _tag by lazy { FEncuesta::class.java.simpleName }

    @Inject
    lateinit var gpsTracker: GpsTracker

    override fun onDestroyView() {
        super.onDestroyView()
        stopGps()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFEncuestaBinding.inflate(inflater, container, false).root

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        initAutoComplete()
        startGps()
        setupActionViews()
        collectFlows()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.n_encuesta_menu, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.descargar -> consume { launchApiDownload() }
        R.id.seleccion -> consume { selectEncuesta() }
        else -> false
    }

    private fun collectFlows() {

        collectFlow(
            apiViewModel.flowCabeceraEncuesta
                .combine(apiViewModel.flowPreguntas) { cabecera, preguntas ->
                    cabecera to preguntas
                }
        ) { (cabeceras, preguntas) ->

            cabecerasCache = cabeceras

            if (cabeceras.size == 1) {
                val id = cabeceras.first().id

                if (localViewmodel.encuestaState.encuestaId != id) {
                    localViewmodel.encuestaState.encuestaId = id
                }
            }

            // 🔥 ignorar estado inicial (0,0)
            if (estadoEncuesta == EstadoEncuesta.INIT &&
                cabeceras.isEmpty() && preguntas.isEmpty()
            ) {
                return@collectFlow
            }

            // 🔥 bloquear estados intermedios cuando cambia encuesta
            if (estadoEncuesta == EstadoEncuesta.LOADING && preguntas.isEmpty()) {
                return@collectFlow
            }

            // 🔥 prioridad absoluta → render
            if (preguntas.isNotEmpty()) {
                estadoEncuesta = EstadoEncuesta.RENDER
                setTituloEncuesta()
                renderFormulario(preguntas.toPreguntaUI())
                return@collectFlow
            }

            // 🔥 sin datos reales
            if (cabeceras.isEmpty()) {
                estadoEncuesta = EstadoEncuesta.SIN_DATOS
                mostrarSinEncuestas()
                return@collectFlow
            }

            // 🔥 selector
            if (cabeceras.size > 1) {
                estadoEncuesta = EstadoEncuesta.SELECCION
                mostrarSelector()
            }
        }

        collectFlow(apiViewModel.flowClientesPendientes) { lista ->
            val uiList = lista.toClienteUI()

            if (uiList.isEmpty()) {
                mostrarSinClientes()
                return@collectFlow
            }

            submitList(uiList)
        }

        collectFlow(apiViewModel.encuestaEvent) { resultado ->
            when (resultado) {
                is ResultadoApi.Loading -> mostrarDialog(
                    AppDialogType.Progreso(
                        mensaje = "Descargando encuestas..."
                    )
                )

                is ResultadoApi.Exito -> {
                    apiViewModel.selectUniqueEncuesta()
                    stateSuccess(resultado.data)
                }

                is ResultadoApi.ErrorHttp -> mostrarDialog(
                    AppDialogType.Informativo(
                        titulo = T_ERROR,
                        mensaje = "Error HTTP ${resultado.code}: ${resultado.mensaje}"
                    )
                )

                is ResultadoApi.Fallo -> mostrarDialog(
                    AppDialogType.Informativo(
                        titulo = T_ERROR,
                        mensaje = "Fallo: ${resultado.mensaje}"
                    )
                )
            }
        }

        collectFlow(apiViewModel.respuestaMessage) { mensaje ->
            mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = mensaje
                )
            )
        }

        collectFlow(apiViewModel.fotoMessage) { mensaje ->
            mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = mensaje
                )
            )
        }
    }

    private fun setupActionViews() {
        binding.btnGuardar.setOnClickListener {
            guardarEncuesta()
        }

        binding.autoCliente.setOnItemClickListener { parent, _, position, _ ->
            clienteActual = parent.getItemAtPosition(position) as ClienteUI
            onClienteSelected()
        }

        binding.imgFoto.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun setTituloEncuesta() {
        val encuestaIdActual = localViewmodel.encuestaState.encuestaId
        val encuesta = cabecerasCache.firstOrNull {
            it.id == encuestaIdActual
        } ?: cabecerasCache.firstOrNull() // 🔥 fallback

        binding.txtTitulo.text = encuesta?.encuesta.orEmpty()
    }

    private fun initAutoComplete() {
        adapterAutoComplete = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        )

        binding.autoCliente.setAdapter(adapterAutoComplete)
    }

    private fun submitList(list: List<ClienteUI>) {
        adapterAutoComplete.clear()
        adapterAutoComplete.addAll(list)
        adapterAutoComplete.notifyDataSetChanged()

        if (list.isNotEmpty() && clienteActual == null) {
            val first = list.first()

            clienteActual = first
            binding.autoCliente.setText(first.toString(), false)

            onClienteSelected()
        }
    }

    private fun onClienteSelected() {
        localViewmodel.limpiarEncuesta()
        renderFormulario(preguntasActuales)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                mostrarFoto()
            } else {
                snack("Error procesando foto")
            }
        }

    private fun createPhoto(): File {
        val directory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("KV_${System.currentTimeMillis()}_", ".jpg", directory).apply {
            localViewmodel.encuestaState.rutaFoto = absolutePath
        }
    }

    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val file = try {
            createPhoto()
        } catch (e: Exception) {
            null
        }

        file?.let {
            val uri = FileProvider.getUriForFile(
                requireContext(),
                "com.upd.kvupd",
                it
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            resultLauncher.launch(intent)
        }
    }

    private fun mostrarFoto() {

        val ruta = localViewmodel.encuestaState.rutaFoto

        if (ruta.isBlank()) return

        Glide.with(this)
            .load(ruta)
            .override(500, 600)
            .centerCrop()
            .into(binding.imgFoto)

        binding.txtRuta.text = ruta

        snack("Foto almacenada")
    }

    private fun startGps() {
        launchGpsRastreo()
    }

    private fun stopGps() {
        gpsTracker.stopTracking(TRACKER_RAPIDO)
    }

    private fun launchGpsRastreo() {
        gpsTracker.startTracking(
            id = TRACKER_RAPIDO,
            interval = GPS_INTERVALO_NORMAL,
            fastest = GPS_INTERVALO_RAPIDO,
            minDistance = IGNORAR_METROS,
            onLocation = { location ->
                getLocation = location
            },
            onError = { error -> Log.e(_tag, "Error GPS: $error") }
        )
    }

    private fun launchApiDownload() {
        apiViewModel.downloadEncuestas()
    }

    private fun selectEncuesta() {
        if (cabecerasCache.isEmpty()) {
            mostrarSinEncuestas()
            return
        }

        SeleccionEncuesta(
            requireContext(),
            cabecerasCache.toEncuestaUI()
        ) { id ->
            localViewmodel.encuestaState.encuestaId = id
            estadoEncuesta = EstadoEncuesta.LOADING
            localViewmodel.limpiarEncuesta()

            // 🔥 reset cliente a default
            clienteActual = null

            apiViewModel.setEncuestaSeleccionada(id)
        }.show()
    }

    private fun mostrarSinClientes() {
        binding.lnrPreguntas.gone()
        binding.btnGuardar.gone()

        mostrarDialog(
            AppDialogType.Informativo(
                titulo = T_WARNING,
                mensaje = "Debe descargar una cartera de clientes antes de continuar",
                onPositive = {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            )
        )
    }

    private fun mostrarSinEncuestas() {
        mostrarDialog(AppDialogType.Informativo(
            titulo = T_WARNING,
            mensaje = "No descargo encuestas aún. Desea descargarlo?",
            onPositive = {
                launchApiDownload()
            }
        ))
    }

    private fun mostrarSelector() {
        mostrarDialog(AppDialogType.Informativo(
            titulo = T_WARNING,
            mensaje = "No selecciono ninguna encuesta. Desea elegir ahora?",
            onPositive = {
                selectEncuesta()
            }
        ))
    }

    private fun mostrarDialog(dialogType: AppDialogType) {
        lifecycleScope.launch(Dispatchers.Main) {
            cerrarDialogActual()
            val dialog = buildMaterialDialog(requireContext(), dialogType)
            dialog.show()
            REFERENCIA_DIALOG = WeakReference(dialog)
        }
    }

    private fun stateSuccess(encuestas: JsonEncuesta?) {
        when {
            encuestas == null -> mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = "No se obtuvo respuesta del servidor"
                )
            )

            encuestas.jobl.isEmpty() -> mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_ERROR,
                    mensaje = "No se encontraron clientes"
                )
            )

            else -> mostrarDialog(
                AppDialogType.Informativo(
                    titulo = MaterialDialogTexto.T_SUCCESS,
                    mensaje = "Se descargaron ${encuestas.jobl.size} encuestas"
                )
            )
        }
    }

    private fun renderFormulario(preguntas: List<PreguntaUI>) {

        preguntasActuales = preguntas

        val ruta = localViewmodel.encuestaState.rutaFoto

        if (ruta.isNotBlank()) {
            mostrarFoto()
        } else {
            binding.imgFoto.setImageResource(R.drawable.camara)
            binding.txtRuta.text = ""
        }

        val contenedor = binding.lnrPreguntas
        contenedor.removeAllViews()

        binding.lnrPreguntas.visible()
        binding.btnGuardar.visible()

        requiereFoto = preguntas.any { it.tieneFoto }
        binding.cardFoto.visibleIf(requiereFoto)

        preguntas.forEach { pregunta ->

            val view = when (pregunta.tipo) {
                TipoPregunta.UNICA -> renderUnica(pregunta)
                TipoPregunta.MULTIPLE -> renderMultiple(pregunta)
                TipoPregunta.LIBRE -> renderLibre(pregunta)
            }

            view.tag = pregunta.pregunta

            // 🔹 solo ocultar condicionales por defecto
            val esCondicional = pregunta.previa != 0 && pregunta.eleccion.isNotBlank()
            view.visibleIf(!esCondicional)

            contenedor.addView(view)
        }

        // 🔥 aplicar lógica real de visibilidad
        actualizarVisibilidadCondicional()
    }

    private fun renderUnica(p: PreguntaUI): View {

        val seleccion = localViewmodel.encuestaState.respuestas[p.pregunta]

        val binding = FlowRowPreguntasBinding.inflate(layoutInflater)
        binding.txtPregunta.text = p.descripcion
        binding.txtObligatorio.visibleIf(p.esObligatoria)
        binding.rbUnico.visible()

        p.opciones.split("|").forEach { opcion ->

            val radio = RadioButton(requireContext()).apply {
                text = opcion
                isChecked = opcion == seleccion

                setOnClickListener {
                    localViewmodel.actualizarRespuesta(p.pregunta, opcion)
                    limpiarDependientes(p.pregunta)
                    actualizarVisibilidadCondicional()
                }
            }
            binding.rbUnico.addView(radio)
        }

        return binding.root
    }

    private fun renderMultiple(p: PreguntaUI): View {

        val seleccion = localViewmodel.encuestaState.respuestas[p.pregunta]
            ?.split("|") ?: emptyList()

        val binding = FlowRowPreguntasBinding.inflate(layoutInflater)
        binding.txtPregunta.text = p.descripcion
        binding.txtObligatorio.visibleIf(p.esObligatoria)
        binding.lnrMultiple.visible()

        p.opciones.split("|").forEach { opcion ->

            val check = CheckBox(requireContext()).apply {
                text = opcion
                isChecked = seleccion.contains(opcion)

                setOnCheckedChangeListener { _, _ ->

                    val seleccionadas = binding.lnrMultiple.children
                        .filterIsInstance<CheckBox>()
                        .filter { it.isChecked }
                        .joinToString("|") { it.text.toString() }

                    localViewmodel.actualizarRespuesta(p.pregunta, seleccionadas)
                    limpiarDependientes(p.pregunta)
                    actualizarVisibilidadCondicional()
                }
            }
            binding.lnrMultiple.addView(check)
        }

        return binding.root
    }

    private fun renderLibre(p: PreguntaUI): View {

        val valor = localViewmodel.encuestaState.respuestas[p.pregunta] ?: ""

        val binding = FlowRowPreguntasBinding.inflate(layoutInflater)
        binding.txtPregunta.text = p.descripcion
        binding.txtObligatorio.visibleIf(p.esObligatoria)
        binding.txtLibre.visible()

        // 🔥 evitar loop
        if (binding.edtLibre.text.toString() != valor) {
            binding.edtLibre.setText(valor)
        }

        binding.edtLibre.doAfterTextChanged {
            val texto = it.toString()

            localViewmodel.actualizarRespuesta(p.pregunta, texto)
            limpiarDependientes(p.pregunta)
            actualizarVisibilidadCondicional()
        }

        return binding.root
    }

    private fun limpiarDependientes(preguntaId: Int) {

        val dependientes = preguntasActuales.filter { it.previa == preguntaId }

        dependientes.forEach { dep ->
            // 🔥 borrar respuesta
            localViewmodel.encuestaState.respuestas.remove(dep.pregunta)

            // 🔁 recursivo (si hay niveles)
            limpiarDependientes(dep.pregunta)
        }
    }

    private fun limpiarView(view: View?) {
        if (view !is ViewGroup) return

        view.children.forEach { child ->
            when (child) {
                is RadioButton -> child.isChecked = false
                is CheckBox -> child.isChecked = false
                is ViewGroup -> limpiarView(child)
            }
        }
        view.findViewById<android.widget.EditText>(R.id.edt_libre)?.setText("")
    }

    private fun actualizarVisibilidadCondicional() {

        val respuestas = localViewmodel.encuestaState.respuestas
        val contenedor = binding.lnrPreguntas

        preguntasActuales.forEach { pregunta ->

            // 🔹 solo evaluar condicionales
            if (pregunta.previa == 0 || pregunta.eleccion.isBlank()) return@forEach

            val respuestaPrevia = respuestas[pregunta.previa]

            val cumple = respuestaPrevia
                ?.split("|")
                ?.contains(pregunta.eleccion) == true

            val view = contenedor.children
                .firstOrNull { it.tag == pregunta.pregunta }

            if (cumple) {
                view?.visible()
            } else {
                view?.gone()

                // 🔥 limpiar estado (NO UI)
                if (respuestas.containsKey(pregunta.pregunta)) {
                    limpiarDependientes(pregunta.pregunta)
                    respuestas.remove(pregunta.pregunta)
                }

                limpiarView(view)
            }
        }
    }

    private fun validarFormulario(): Boolean {

        // 🔥 VALIDAR FOTO PRIMERO
        val ruta = localViewmodel.encuestaState.rutaFoto

        if (requiereFoto && ruta.isBlank()) {
            mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_WARNING,
                    mensaje = "Debe tomar una foto del negocio"
                )
            )
            return false
        }

        val encuestaIdActual = localViewmodel.encuestaState.encuestaId
        if (encuestaIdActual == 0) {
            mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_WARNING,
                    mensaje = "No se encontro codigo de encuesta"
                )
            )
            return false
        }

        val respuestas = localViewmodel.encuestaState.respuestas
        val contenedor = binding.lnrPreguntas

        val invalidas = preguntasActuales.filter { pregunta ->

            // 🔹 solo obligatorias
            if (!pregunta.esObligatoria) return@filter false

            // 🔹 obtener view
            val view = contenedor.children
                .firstOrNull { it.tag == pregunta.pregunta }

            val visible = view?.visibility == View.VISIBLE

            // 🔹 solo visibles
            if (!visible) return@filter false

            // 🔹 validar respuesta
            val respuesta = respuestas[pregunta.pregunta]
            respuesta.isNullOrBlank()
        }

        if (invalidas.isNotEmpty()) {
            mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_WARNING,
                    mensaje = "Complete todas las preguntas obligatorias"
                )
            )
            return false
        }
        return true
    }

    private fun construirDatos(): Pair<List<TableRespuesta>, TableFoto?> {

        val fecha = FechaHoraUtil.ahora()
        val contenedor = binding.lnrPreguntas
        val respuestas = localViewmodel.encuestaState.respuestas
        val clienteId = convertClienteCodigo()
        val encuestaIdActual = localViewmodel.encuestaState.encuestaId

        val listaRespuestas = preguntasActuales
            .filter { pregunta ->
                contenedor.children.any {
                    it.tag == pregunta.pregunta && it.visibility == View.VISIBLE
                } && respuestas.containsKey(pregunta.pregunta)
            }
            .sortedBy { it.pregunta }
            .map { pregunta ->
                TableRespuesta(
                    cliente = clienteId,
                    fecha = fecha,
                    encuesta = encuestaIdActual,
                    pregunta = pregunta.pregunta,
                    respuesta = respuestas[pregunta.pregunta].orEmpty(),
                    longitud = getLocation?.longitude ?: 0.0,
                    latitud = getLocation?.latitude ?: 0.0,
                    sincronizado = false
                )
            }

        val ruta = localViewmodel.encuestaState.rutaFoto
        val foto = if (ruta.isNotBlank()) {
            TableFoto(
                cliente = clienteId,
                encuesta = encuestaIdActual,
                rutafoto = ruta,
                sincronizado = false
            )
        } else null

        return listaRespuestas to foto
    }

    private fun convertClienteCodigo(): String {
        val id = clienteActual?.id?.toIntOrNull() ?: 0

        return if (id > 0) {
            id.toString()
        } else {
            FechaHoraUtil.timestamp()
        }
    }

    private fun setDefaultCliente() {
        if (adapterAutoComplete.count == 0) return

        val default = adapterAutoComplete.getItem(0)
        clienteActual = default
        binding.autoCliente.setText(default?.toString(), false)
    }

    private fun guardarEncuesta() {
        if (!validarFormulario()) return

        val (respuestas, foto) = construirDatos()

        if (respuestas.isEmpty()) return

        apiViewModel.saveAndSendRespuestas(respuestas)

        foto?.let {
            apiViewModel.saveAndSendFoto(it)
        }

        // 🔥 seleccionar default
        setDefaultCliente()

        // 🔥 RE-RENDER COMPLETO (clave)
        onClienteSelected()

        mostrarDialog(
            AppDialogType.Informativo(
                titulo = T_SUCCESS,
                mensaje = "Encuesta guardada correctamente"
            )
        )
    }
}