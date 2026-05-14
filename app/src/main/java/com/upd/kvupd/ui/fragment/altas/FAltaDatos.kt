package com.upd.kvupd.ui.fragment.altas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputEditText
import com.upd.kvupd.data.model.core.TableAltaDatos
import com.upd.kvupd.databinding.FragmentFAltadatosBinding
import com.upd.kvupd.ui.fragment.altas.enumAltaDatos.Documento
import com.upd.kvupd.ui.fragment.altas.enumAltaDatos.Numeracion
import com.upd.kvupd.ui.fragment.altas.enumAltaDatos.TipoPersona
import com.upd.kvupd.ui.fragment.altas.enumAltaDatos.Via
import com.upd.kvupd.ui.fragment.altas.enumAltaDatos.Zona
import com.upd.kvupd.ui.fragment.altas.modelUI.DistritoUI
import com.upd.kvupd.ui.fragment.altas.modelUI.GiroUI
import com.upd.kvupd.ui.fragment.altas.modelUI.RutaUI
import com.upd.kvupd.ui.fragment.altas.modelUI.SubGiroUI
import com.upd.kvupd.ui.fragment.altas.sealed.AltaResult
import com.upd.kvupd.ui.sealed.AppDialogType
import com.upd.kvupd.utils.FechaHoraUtil
import com.upd.kvupd.utils.InstanciaDialog.REFERENCIA_DIALOG
import com.upd.kvupd.utils.InstanciaDialog.cerrarDialogActual
import com.upd.kvupd.utils.MaterialDialogTexto.T_WARNING
import com.upd.kvupd.utils.buildMaterialDialog
import com.upd.kvupd.utils.collectFlow
import com.upd.kvupd.utils.geo.GeoManager
import com.upd.kvupd.utils.gone
import com.upd.kvupd.utils.isValidDocumento
import com.upd.kvupd.utils.isValidEmail
import com.upd.kvupd.utils.isValidPhone
import com.upd.kvupd.utils.isValidPositiveNumber
import com.upd.kvupd.utils.onItemSelectedItem
import com.upd.kvupd.utils.popWithResult
import com.upd.kvupd.utils.selectItem
import com.upd.kvupd.utils.selectedItemTyped
import com.upd.kvupd.utils.setAdapterList
import com.upd.kvupd.utils.setErrorBorder
import com.upd.kvupd.utils.toUpper
import com.upd.kvupd.utils.viewBinding
import com.upd.kvupd.utils.visible
import com.upd.kvupd.utils.visibleIf
import com.upd.kvupd.viewmodel.APIViewModel
import com.upd.kvupd.viewmodel.state.AltaFormState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

@AndroidEntryPoint
class FAltaDatos : Fragment() {

    private val apiViewModel by activityViewModels<APIViewModel>()
    private val args: FAltaDatosArgs by navArgs()
    private val binding by viewBinding(FragmentFAltadatosBinding::bind)

    private var idaux = ""
    private var fecha = ""
    private var longitud = 0.0f
    private var latitud = 0.0f
    private var distritoAutoSeteado = false
    private var empleadoActual: String = ""
    private var initializedAltaDatos = false
    private var distritoSugerido: String? = null
    private var currentState: AltaFormState? = null
    private var tipoPersona: TipoPersona = TipoPersona.NATURAL
    private val _tag by lazy { FAltaDatos::class.java.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        empleadoActual = args.empleado
        idaux = args.idaux
        fecha = args.fecha
        longitud = args.longitud
        latitud = args.latitud
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentFAltadatosBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!GeoManager.isLoaded()) {
            GeoManager.load(requireContext())
        }

        setupUIForms()       // registra listeners
        populateSpinners()   // setea enums
        observerData()       // empieza a recibir state (combine emite con al menos 1 dato disponible)
        existDataPrevious()  // dispara carga suspend, busca datos previos de alta
    }

    private fun setupUIForms() {
        val cliente = "Alta cliente :: ${args.idaux}"

        binding.txtTitulo.text = cliente

        binding.btnGuardar.setOnClickListener {

            if (!validateAlta()) return@setOnClickListener

            validateWarningsAndSave()
        }

        binding.rbGrupo.setOnCheckedChangeListener { _, id ->
            tipoPersona = when (id) {
                binding.rbJuridica.id -> TipoPersona.JURIDICA
                binding.rbNatural.id -> TipoPersona.NATURAL
                else -> return@setOnCheckedChangeListener
            }

            setDocumentoSpinner(tipoPersona)
            showFields(tipoPersona)
        }

        binding.spnDocumento.onItemSelectedItem<Documento> { documento ->
            filterDocumento(documento)
        }

        binding.spnGiro.onItemSelectedItem<GiroUI> { giro ->
            val subgiros = currentState?.subgiros?.get(giro.codigo).orEmpty()
            binding.spnSubgiro.setAdapterList(subgiros)
        }
    }

    private fun existDataPrevious() {
        apiViewModel.obtainAltaDatos(idaux, fecha)
    }

    private fun validateWarningsAndSave() {

        val warnings = mutableListOf<String>()

        // -------- Distrito --------
        val distritoActual = binding.spnDistrito
            .selectedItemTyped<DistritoUI>()
            ?.codigo

        val cambioDistrito =
            distritoSugerido != null &&
                    distritoActual != distritoSugerido

        if (cambioDistrito) {
            warnings.add(
                "• El distrito seleccionado es diferente al distrito detectado por ubicación."
            )
        }

        // -------- Día ruta --------
        val ruta = binding.spnRuta
            .selectedItemTyped<RutaUI>()

        val diaActual = FechaHoraUtil
            .diaNumero(FechaHoraUtil.dia())

        ruta?.let {

            if (it.dia != diaActual) {

                warnings.add(
                    "• La ruta seleccionada corresponde a ${FechaHoraUtil.diaTexto(it.dia)} y hoy es ${
                        FechaHoraUtil.diaTexto(
                            diaActual
                        )
                    }."
                )
            }
        }

        // -------- Advertencias --------
        if (warnings.isNotEmpty()) {

            mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_WARNING,
                    mensaje = warnings.joinToString("\n\n") +
                            "\n\n¿Desea continuar?",
                    mostrarNegativo = true,
                    onPositive = {
                        saveAltaDatos()
                    }
                )
            )

            return
        }

        saveAltaDatos()
    }

    private fun saveAltaDatos() {

        val item = buildAltaDatos()
        val isEdit = currentState?.alta != null

        if (isEdit) {
            apiViewModel.retrySendAltaDatos(item)
        } else {
            apiViewModel.saveAndSendAltaDatos(item)
        }
    }

    private fun observerData() {
        collectFlow(apiViewModel.altaFormState) { state ->
            renderForm(state)
        }

        collectFlow(apiViewModel.altaDatosMessage) { mensaje ->
            popWithResult("alta_result", AltaResult.Error(mensaje))
        }

        collectFlow(apiViewModel.altaDatosSuccess) {
            popWithResult("alta_result", AltaResult.Success)
        }
    }

    private fun renderForm(state: AltaFormState) {
        currentState = state

        binding.apply {

            if (spnDistrito.adapter == null) {
                spnDistrito.setAdapterList(state.distritos)
            }

            if (spnGiro.adapter == null) {
                spnGiro.setAdapterList(state.giros)
            }

            if (spnRuta.adapter == null) {
                spnRuta.setAdapterList(state.rutas)
            }
        }

        val alta = state.alta

        if (alta != null && !initializedAltaDatos) {
            setupFields(alta)
            initializedAltaDatos = true
        }

        checkDistritoCloser(alta, state.distritos)
    }

    private fun populateSpinners() {
        binding.apply {
            spnVia.setAdapterList(Via.entries)
            spnZona.setAdapterList(Zona.entries)
            spnNumero.setAdapterList(Numeracion.entries)
        }
    }

    private fun setDocumentoSpinner(tipo: TipoPersona) {
        binding.spnDocumento.setAdapterList(
            Documento.byTipo(tipo)
        )

        val doc = binding.spnDocumento.selectedItem as? Documento ?: return
        filterDocumento(doc)
    }

    private fun showFields(tipo: TipoPersona) {
        cleanFields()
        binding.lnrDetalle.visible()

        val esJuridica = tipo == TipoPersona.JURIDICA

        binding.txtRazon.visibleIf(esJuridica)
        binding.lnrCliente.visibleIf(!esJuridica)
    }

    private fun checkDistritoCloser(
        item: TableAltaDatos?,
        distritos: List<DistritoUI>
    ) {
        if (item != null) return
        if (distritoAutoSeteado) return
        if (!GeoManager.isLoaded()) return
        if (distritos.isEmpty()) return

        val latLng = LatLng(latitud.toDouble(), longitud.toDouble())
        val codigoGeo = GeoManager.findDistrito(latLng)

        codigoGeo?.let { codigo ->

            distritoSugerido = codigo

            binding.spnDistrito.post {
                binding.spnDistrito.selectItem<DistritoUI> {
                    it.codigo == codigo
                }
            }

            distritoAutoSeteado = true
        }
    }

    private fun filterDocumento(doc: Documento) {
        binding.apply {

            when (doc) {

                Documento.RUC -> {
                    inlRuc.visible()
                    inlDnice.gone()
                    txtMensaje.visible()
                    txtMensaje.text = "* EL RUC ES OBLIGATORIO"
                    edtDnice.setText("")
                }

                Documento.DNI,
                Documento.CARNET -> {
                    inlRuc.gone()
                    inlDnice.visible()
                    txtMensaje.visible()
                    txtMensaje.text = "* EL DNI/CARNET ES OBLIGATORIO"
                    edtRuc.setText("")
                }
            }
        }
    }

    private fun cleanFields() {
        binding.apply {
            edtRazon.setText("")
            edtPaterno.setText("")
            edtMaterno.setText("")
            edtNombre.setText("")
            edtDnice.setText("")
            edtRuc.setText("")
            edtMovil1.setText("")
            edtMovil2.setText("")
            edtCorreo.setText("")
            edtManzana.setText("")
            edtDireccion.setText("")
            edtNumero.setText("")
            edtZona.setText("")
            edtSecuencia.setText("")
            edtObservacion.setText("")

            spnDocumento.setSelection(0)
            spnVia.setSelection(0)
            spnNumero.setSelection(0)
            spnZona.setSelection(0)
            spnGiro.setSelection(0)
            spnRuta.setSelection(0)

            if (spnSubgiro.adapter != null) {
                spnSubgiro.setSelection(0)
            }

            txtMensaje.gone()
            inlRuc.gone()
            inlDnice.gone()
        }
    }

    private fun clearAltaErrors() {

        with(binding) {
            edtRazon.setErrorBorder(false)

            edtPaterno.setErrorBorder(false)
            edtMaterno.setErrorBorder(false)
            edtNombre.setErrorBorder(false)

            edtRuc.setErrorBorder(false)
            edtDnice.setErrorBorder(false)

            edtMovil1.setErrorBorder(false)
            edtMovil2.setErrorBorder(false)

            edtCorreo.setErrorBorder(false)

            edtNumero.setErrorBorder(false)
            edtSecuencia.setErrorBorder(false)
        }
    }

    private fun setupFields(item: TableAltaDatos) {

        val state = currentState ?: return

        // -------- Tipo persona --------
        tipoPersona = TipoPersona.from(item.tipo)

        binding.rbGrupo.check(
            if (tipoPersona == TipoPersona.JURIDICA)
                binding.rbJuridica.id
            else
                binding.rbNatural.id
        )

        // -------- Documento --------
        val doc = Documento.from(item.tipodocu)
        binding.spnDocumento.selectItem<Documento> { it == doc }

        // -------- Textos --------
        binding.apply {
            edtRazon.setText(item.razon)
            edtPaterno.setText(item.appaterno)
            edtMaterno.setText(item.apmaterno)
            edtNombre.setText(item.nombre)
            edtDnice.setText(item.dnice)
            edtRuc.setText(item.ruc)
            edtMovil1.setText(item.movil1)
            edtMovil2.setText(item.movil2)
            edtCorreo.setText(item.correo)
            edtManzana.setText(item.manzana)
            edtDireccion.setText(item.direccion)
            edtNumero.setText(item.numero)
            edtZona.setText(item.zonanombre)
            edtSecuencia.setText(item.secuencia)
            edtObservacion.setText(item.observacion)
        }

        // -------- Enums --------
        binding.spnVia.selectItem<Via> { it.code == item.via }
        binding.spnNumero.selectItem<Numeracion> { it.code == item.ubicacion }
        binding.spnZona.selectItem<Zona> { it.code == item.zona }

        // -------- Spinner simples --------
        binding.spnDistrito.selectItem<DistritoUI> { it.codigo == item.distrito }
        binding.spnRuta.selectItem<RutaUI> { it.codigo == item.ruta }

        // -------- Giro / Subgiro --------
        val codigoSubGiro = item.giro

        val subgiro = state.subgiros
            .values
            .flatten()
            .firstOrNull { it.codigo == codigoSubGiro }

        if (subgiro != null) {

            val giroPadre = state.giros.firstOrNull { giro ->
                state.subgiros[giro.codigo]
                    ?.any { it.codigo == subgiro.codigo } == true
            }

            giroPadre?.let { giro ->

                binding.spnGiro.selectItem<GiroUI> { it.codigo == giro.codigo }

                binding.spnSubgiro.post {
                    binding.spnSubgiro.selectItem<SubGiroUI> {
                        it.codigo == subgiro.codigo
                    }
                }
            }
        }
    }

    private fun validateAlta(): Boolean {

        clearAltaErrors()

        val errores = mutableListOf<String>()

        val tipo = tipoPersona

        val razon = binding.edtRazon.text.toString().trim()
        val paterno = binding.edtPaterno.text.toString().trim()
        val materno = binding.edtMaterno.text.toString().trim()
        val nombre = binding.edtNombre.text.toString().trim()
        val dnice = binding.edtDnice.text.toString().trim()
        val ruc = binding.edtRuc.text.toString().trim()
        val movil1 = binding.edtMovil1.text.toString().trim()
        val movil2 = binding.edtMovil2.text.toString().trim()
        val correo = binding.edtCorreo.text.toString().trim()
        val numero = binding.edtNumero.text.toString().trim()
        val secuencia = binding.edtSecuencia.text.toString().trim()

        val doc = binding.spnDocumento.selectedItemTyped<Documento>()
        val via = binding.spnVia.selectedItemTyped<Via>()
        val zona = binding.spnZona.selectedItemTyped<Zona>()
        val numeracion = binding.spnNumero.selectedItemTyped<Numeracion>()

        val subgiro = binding.spnSubgiro
            .selectedItemTyped<SubGiroUI>()?.codigo.orEmpty()

        // ===== VALIDACIONES =====

        if (doc == null) {
            errores.add("• Seleccione documento")
        }

        if (tipo == TipoPersona.JURIDICA && razon.isEmpty()) {
            markError(
                binding.edtRazon,
                "Ingrese razón social",
                errores
            )
        }

        if (
            tipo == TipoPersona.NATURAL &&
            (paterno.isEmpty() || materno.isEmpty() || nombre.isEmpty())
        ) {

            if (paterno.isEmpty()) {
                binding.edtPaterno.setErrorBorder(true)
            }

            if (materno.isEmpty()) {
                binding.edtMaterno.setErrorBorder(true)
            }

            if (nombre.isEmpty()) {
                binding.edtNombre.setErrorBorder(true)
            }

            errores.add("• Ingrese nombre y apellidos")
        }

        if (tipo == TipoPersona.JURIDICA && ruc.isEmpty()) {
            markError(
                binding.edtRuc,
                "Debe ingresar RUC",
                errores
            )
        }

        if (
            tipo == TipoPersona.NATURAL &&
            ruc.isEmpty() &&
            dnice.isEmpty()
        ) {

            binding.edtRuc.setErrorBorder(true)
            binding.edtDnice.setErrorBorder(true)

            errores.add("• Debe ingresar documento")
        }

        if (movil1.isEmpty() && movil2.isEmpty()) {

            binding.edtMovil1.setErrorBorder(true)
            binding.edtMovil2.setErrorBorder(true)

            errores.add("• Ingrese celular")
        }

        if (movil1.isNotEmpty() && !movil1.isValidPhone()) {
            markError(
                binding.edtMovil1,
                "Celular 1 inválido",
                errores
            )
        }

        if (movil2.isNotEmpty() && !movil2.isValidPhone()) {
            markError(
                binding.edtMovil2,
                "Celular 2 inválido",
                errores
            )
        }

        if (correo.isNotEmpty() && !correo.isValidEmail()) {
            markError(
                binding.edtCorreo,
                "Correo inválido",
                errores
            )
        }

        if (doc == Documento.RUC && ruc.isEmpty()) {
            markError(
                binding.edtRuc,
                "Debe completar RUC",
                errores
            )
        }

        if (
            (doc == Documento.DNI || doc == Documento.CARNET) &&
            dnice.isEmpty()
        ) {
            markError(
                binding.edtDnice,
                "Debe completar DNI/Carnet",
                errores
            )
        }

        if (!numero.isValidPositiveNumber()) {
            markError(
                binding.edtNumero,
                "Número inválido",
                errores
            )
        }

        if (!secuencia.isValidPositiveNumber()) {
            markError(
                binding.edtSecuencia,
                "Secuencia inválida",
                errores
            )
        }

        if (numeracion == null || numeracion == Numeracion.NINGUNO) {
            errores.add("• Seleccione numeración")
        }

        if (subgiro == "0" || subgiro.isEmpty()) {
            errores.add("• Seleccione subgiro")
        }

        if (via == null || via == Via.NINGUNO) {
            errores.add("• Seleccione una vía válida")
        }

        if (zona == null || zona == Zona.NINGUNO) {
            errores.add("• Seleccione una zona válida")
        }

        if (ruc.isNotEmpty() && !ruc.isValidDocumento(tipo)) {
            markError(
                binding.edtRuc,
                "RUC inválido",
                errores
            )
        }

        if (dnice.isNotEmpty() && !dnice.isValidDocumento(tipo)) {
            markError(
                binding.edtDnice,
                "DNI/Carnet inválido",
                errores
            )
        }

        if (errores.isNotEmpty()) {
            mostrarDialog(
                AppDialogType.Informativo(
                    titulo = T_WARNING,
                    mensaje = errores.joinToString("\n")
                )
            )
            return false
        }

        return true
    }

    private fun buildAltaDatos(): TableAltaDatos {

        val documento = binding.spnDocumento.selectedItemTyped<Documento>()!!
        val via = binding.spnVia.selectedItemTyped<Via>()?.code.orEmpty()
        val ubicacion = binding.spnNumero.selectedItemTyped<Numeracion>()?.code.orEmpty()
        val zona = binding.spnZona.selectedItemTyped<Zona>()?.code.orEmpty()

        val distrito = binding.spnDistrito
            .selectedItemTyped<DistritoUI>()?.codigo.orEmpty()

        val ruta = binding.spnRuta
            .selectedItemTyped<RutaUI>()?.codigo.orEmpty()

        val subgiro = binding.spnSubgiro
            .selectedItemTyped<SubGiroUI>()?.codigo.orEmpty()

        return TableAltaDatos(
            fecha = fecha,
            idaux = idaux,
            empleado = empleadoActual,

            tipo = tipoPersona.code,
            razon = binding.edtRazon.text.toString().toUpper(),
            nombre = binding.edtNombre.text.toString().toUpper(),
            appaterno = binding.edtPaterno.text.toString().toUpper(),
            apmaterno = binding.edtMaterno.text.toString().toUpper(),

            ruc = binding.edtRuc.text.toString(),
            dnice = binding.edtDnice.text.toString(),
            tipodocu = documento.code,
            movil1 = binding.edtMovil1.text.toString(),
            movil2 = binding.edtMovil2.text.toString(),
            correo = binding.edtCorreo.text.toString(),

            via = via,
            direccion = binding.edtDireccion.text.toString().toUpper(),
            manzana = binding.edtManzana.text.toString().toUpper(),
            zona = zona,
            zonanombre = binding.edtZona.text.toString().toUpper(),
            ubicacion = ubicacion,
            numero = binding.edtNumero.text.toString().toUpper(),

            distrito = distrito,
            giro = subgiro,
            ruta = ruta,
            secuencia = binding.edtSecuencia.text.toString().toUpper(),
            observacion = binding.edtObservacion.text.toString().toUpper()
        )
    }

    private fun mostrarDialog(dialogType: AppDialogType) {
        lifecycleScope.launch(Dispatchers.Main) {
            cerrarDialogActual()
            val dialog = buildMaterialDialog(requireContext(), dialogType)
            dialog.show()
            REFERENCIA_DIALOG = WeakReference(dialog)
        }
    }

    private fun markError(
        field: TextInputEditText,
        message: String,
        errors: MutableList<String>
    ) {
        field.setErrorBorder(true)
        errors.add("• $message")
    }
}
