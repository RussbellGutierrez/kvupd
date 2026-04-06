package com.upd.kvupd.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upd.kvupd.data.model.FlowBajaSupervisor
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.data.model.JsonBajaSupervisor
import com.upd.kvupd.data.model.JsonBajaVendedor
import com.upd.kvupd.data.model.JsonCambio
import com.upd.kvupd.data.model.JsonCliente
import com.upd.kvupd.data.model.JsonCoberturaCartera
import com.upd.kvupd.data.model.JsonCoberturados
import com.upd.kvupd.data.model.JsonDetalleCobertura
import com.upd.kvupd.data.model.JsonEncuesta
import com.upd.kvupd.data.model.JsonPedido
import com.upd.kvupd.data.model.JsonPedidoGeneral
import com.upd.kvupd.data.model.JsonPedimap
import com.upd.kvupd.data.model.JsonResponseAny
import com.upd.kvupd.data.model.JsonSoles
import com.upd.kvupd.data.model.JsonVolumen
import com.upd.kvupd.data.model.TableAlta
import com.upd.kvupd.data.model.TableAltaDatos
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.data.model.TableBajaProcesada
import com.upd.kvupd.data.model.TableFoto
import com.upd.kvupd.data.model.TableRespuesta
import com.upd.kvupd.data.remote.sealed.SocketEvent
import com.upd.kvupd.domain.JsObFunctions
import com.upd.kvupd.domain.RoomFunctions
import com.upd.kvupd.domain.ServerFunctions
import com.upd.kvupd.domain.enumFile.TipoUsuario
import com.upd.kvupd.domain.search.BajaSearchSource
import com.upd.kvupd.domain.search.ClienteSearchSource
import com.upd.kvupd.domain.send.SendServerFunctions
import com.upd.kvupd.ui.fragment.encuesta.mapper.toDistritoUI
import com.upd.kvupd.ui.fragment.encuesta.mapper.toGiroUI
import com.upd.kvupd.ui.fragment.encuesta.mapper.toRutaUI
import com.upd.kvupd.ui.fragment.encuesta.mapper.toSubGiroUI
import com.upd.kvupd.ui.fragment.reportes.enumFile.ReportAction
import com.upd.kvupd.ui.fragment.reportes.mapper.ReporteMapper.mapToLineas
import com.upd.kvupd.ui.fragment.reportes.mapper.ReporteMapper.mapVolumenToSoles
import com.upd.kvupd.ui.fragment.reportes.modelUI.DetalleParams
import com.upd.kvupd.ui.fragment.reportes.modelUI.LineaUI
import com.upd.kvupd.ui.fragment.reportes.modelUI.SolesRequestConfig
import com.upd.kvupd.ui.sealed.ResultadoApi
import com.upd.kvupd.utils.EventFlow
import com.upd.kvupd.utils.FechaHoraUtil
import com.upd.kvupd.utils.to2Decimals
import com.upd.kvupd.utils.toReqBody
import com.upd.kvupd.viewmodel.state.AltaFormState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONObject
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class APIViewModel @Inject constructor(
    private val serverFunctions: ServerFunctions,
    private val roomFunctions: RoomFunctions,
    private val jsobFunctions: JsObFunctions,
    private val clienteSearchSource: ClienteSearchSource,
    private val bajaSearchSource: BajaSearchSource,
    private val sendServerFunctions: SendServerFunctions
) : ViewModel() {

    private val _registerEvent = EventFlow<ResultadoApi<JsonResponseAny>>()
    val registerEvent = _registerEvent.events

    private val _bajaMessage = EventFlow<String>()
    val bajaMessage = _bajaMessage.events

    private val _bajaProcesadaMessage = EventFlow<String>()
    val bajaProcesadaMessage = _bajaProcesadaMessage.events

    private val _altaMessage = EventFlow<String>()
    val altaMessage = _altaMessage.events

    private val _altaDatosMessage = EventFlow<String>()
    val altaDatosMessage = _altaDatosMessage.events

    private val _altaDatosSuccess = EventFlow<Unit>()
    val altaDatosSuccess = _altaDatosSuccess.events

    private val _respuestaMessage = EventFlow<String>()
    val respuestaMessage = _respuestaMessage.events

    private val _fotoMessage = EventFlow<String>()
    val fotoMessage = _fotoMessage.events

    private val _pedimapEvent = EventFlow<ResultadoApi<JsonPedimap>>()
    val pedimapEvent = _pedimapEvent.events

    private val _clienteEvent = EventFlow<ResultadoApi<JsonCliente>>()
    val clienteEvent = _clienteEvent.events

    private val _bajasuperEvent = EventFlow<ResultadoApi<JsonBajaSupervisor>>()
    val bajasuperEvent = _bajasuperEvent.events

    private val _bajaestadoEvent = EventFlow<ResultadoApi<JsonBajaVendedor>>()
    val bajaestadoEvent = _bajaestadoEvent.events

    private val _encuestaEvent = EventFlow<ResultadoApi<JsonEncuesta>>()
    val encuestaEvent = _encuestaEvent.events

    private val _altaDatos = MutableStateFlow<TableAltaDatos?>(null)
    val altaDatos = _altaDatos

    ///     REPORTES
    private val _preventaEvent = EventFlow<ResultadoApi<JsonVolumen>>()
    val preventaEvent = _preventaEvent.events

    private val _coberturaEvent = EventFlow<ResultadoApi<JsonCoberturaCartera>>()
    val coberturaEvent = _coberturaEvent.events

    private val _carteraEvent = EventFlow<ResultadoApi<JsonCoberturaCartera>>()
    val carteraEvent = _carteraEvent.events

    private val _generalEvent = EventFlow<ResultadoApi<JsonPedido>>()
    val generalEvent = _generalEvent.events

    private val _cambioEvent = EventFlow<ResultadoApi<JsonCambio>>()
    val cambioEvent = _cambioEvent.events

    private val _solesEvent = EventFlow<ResultadoApi<List<LineaUI>>>()
    val solesEvent = _solesEvent.events

    ///     DETALLE REPORTES
    private val _coberturaDetalleEvent = EventFlow<ResultadoApi<JsonDetalleCobertura>>()
    val coberturaDetalleEvent = _coberturaDetalleEvent.events

    private val _coberturaPendienteEvent = EventFlow<ResultadoApi<JsonCoberturados>>()
    val coberturaPendienteEvent = _coberturaPendienteEvent.events

    private val _pedidoEmpleadoEvent = EventFlow<ResultadoApi<JsonPedidoGeneral>>()
    val pedidoEmpleadoEvent = _pedidoEmpleadoEvent.events

    private val _solesDetalleEvent = EventFlow<ResultadoApi<JsonVolumen>>()
    val solesDetalleEvent = _solesDetalleEvent.events

    private val _subSolesEvent = EventFlow<ResultadoApi<JsonVolumen>>()
    val subSolesEvent = _subSolesEvent.events

    private val _socketEvent = EventFlow<SocketEvent>()
    val socketEvent = _socketEvent.events

    private val flowClientes = roomFunctions.listFlowClientes()
    private val flowBaja = roomFunctions.listFlowBajas()
    private val flowBajaSupervisor = roomFunctions.listFlowBajaSupervisor()

    private val negocioFlow = roomFunctions.listFlowNegocios()
    private val distritoFlow = roomFunctions.listFlowDistritos()
    private val rutaFlow = roomFunctions.listFlowRutas()

    /// VARIABLES PRIVADAS ARRIBA ///

    val flowConfiguracion = roomFunctions.listFlowConfiguracion()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flowUIRutas = roomFunctions.listFlowClientes()
        .map { list ->
            if (list.isEmpty()) "" else list.map { it.ruta.toString() }.distinct()
                .joinToString(" - ")
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val flowPolygon = roomFunctions.listFlowRutas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flowVendedores = roomFunctions.listFlowVendedores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flowLastGPS = roomFunctions.listFlowLastGPS()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val flowAlta = roomFunctions.listFlowAltas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    val altaFormState = combine(
        negocioFlow,
        distritoFlow,
        rutaFlow,
        altaDatos
    ) { negocios, distritos, rutas, altaDatos ->

        AltaFormState(
            giros = negocios.toGiroUI(),
            subgiros = negocios.toSubGiroUI(),
            distritos = distritos.toDistritoUI(),
            rutas = rutas.toRutaUI(),
            alta = altaDatos
        )
    }

    val flowPreguntas = roomFunctions.listFlowPreguntas()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flowCabeceraEncuesta = roomFunctions.listFlowCabeceraEncuesta()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val encuestaSeleccionadaId = flowPreguntas
        .map { lista -> lista.firstOrNull()?.id }
        .filterNotNull().distinctUntilChanged()

    val flowClientesPendientes = encuestaSeleccionadaId
        .flatMapLatest { id ->
            roomFunctions.listFlowClientesPendientes(id)
        }

    fun registrarEquipoServidor(identificador: String, empresa: String) {
        viewModelScope.launch {
            val json = jsobFunctions.jsonRegistrarEquipo(identificador, empresa)
            serverFunctions.apiSendRegistro(json).collect {
                _registerEvent.emit(it)
            }
        }
    }

    fun downloadPedimap() {
        viewModelScope.launch {
            val config = roomFunctions.queryConfiguracion() ?: return@launch
            val json = jsobFunctions.jsonObjectPedimap(config)
            serverFunctions.apiQueryPedimap(json).collect {
                _pedimapEvent.emit(it)
            }
        }
    }

    fun downloadClientes(vendedor: Int? = null, fecha: String? = null) {
        viewModelScope.launch {
            val config = roomFunctions.queryConfiguracion() ?: return@launch
            val json = jsobFunctions.jsonObjectClientes(config, vendedor, fecha)
            serverFunctions.apiDownloadCliente(json).collect { result ->
                if (result is ResultadoApi.Exito) {
                    result.data?.jobl?.let { lista ->
                        roomFunctions.apiSaveClientes(lista)
                    }
                }
                _clienteEvent.emit(result)
            }
        }
    }

    fun downloadBajasSupervisor() {
        viewModelScope.launch {
            val config = roomFunctions.queryConfiguracion() ?: return@launch
            val json = jsobFunctions.jsonObjectBasico(config)
            serverFunctions.apiDownloadSupervisorBajas(json).collect { result ->
                if (result is ResultadoApi.Exito) {
                    result.data?.jobl?.let { lista ->
                        roomFunctions.apiSaveBajaSupervisor(lista)
                    }
                }
                _bajasuperEvent.emit(result)
            }
        }
    }

    fun downloadAndShowBajas() {
        viewModelScope.launch {
            val config = roomFunctions.queryConfiguracion() ?: return@launch
            val json = jsobFunctions.jsonObjectBasico(config)
            serverFunctions.apiQueryVendedorBajas(json).collect {
                _bajaestadoEvent.emit(it)
            }
        }
    }

    fun obtainAltaDatos(idaux: String, fecha: String) {
        viewModelScope.launch {
            _altaDatos.value = roomFunctions.queryAltaDatos(idaux, fecha)
        }
    }

    fun downloadEncuestas() {
        viewModelScope.launch {
            val config = roomFunctions.queryConfiguracion() ?: return@launch
            val json = jsobFunctions.jsonObjectBasico(config)
            serverFunctions.apiDownloadEncuesta(json).collect { result ->
                if (result is ResultadoApi.Exito) {
                    result.data?.jobl?.let { lista ->
                        roomFunctions.replaceEncuesta(lista)
                    }
                }
                _encuestaEvent.emit(result)
            }
        }
    }

    fun apiPreventa() {
        viewModelScope.launch {
            downloadBaseReport(serverFunctions::apiReportPreventa) // Pasar como parametro usando ::
                .collect { _preventaEvent.emit(it) }
        }
    }

    fun apiCobertura() {
        viewModelScope.launch {
            downloadBaseReport(serverFunctions::apiReportCobertura)
                .collect { _coberturaEvent.emit(it) }
        }
    }

    fun apiCartera() {
        viewModelScope.launch {
            downloadBaseReport(serverFunctions::apiReportCartera)
                .collect { _carteraEvent.emit(it) }
        }
    }

    fun apiGeneral() {
        viewModelScope.launch {
            downloadBaseReport(serverFunctions::apiReportGeneral)
                .collect { _generalEvent.emit(it) }
        }
    }

    fun apiCambio() {
        viewModelScope.launch {
            val config = roomFunctions.queryConfiguracion() ?: return@launch

            val api = when (TipoUsuario.fromCodigo(config.tipo)) {
                TipoUsuario.VENDEDOR -> serverFunctions::apiReportClienteCambio
                TipoUsuario.SUPERVISOR -> serverFunctions::apiReportEmpleadoCambio
                TipoUsuario.JEFE_VENTAS -> return@launch
            }
            downloadBaseReport(api)
                .collect { _cambioEvent.emit(it) }
        }
    }

    fun apiSolesPorLineas() {
        viewModelScope.launch {

            val config = roomFunctions.queryConfiguracion() ?: return@launch
            val tipoUsuario = TipoUsuario.fromCodigo(config.tipo)

            // 🔹 1. Base (líneas)
            val base = downloadBaseReport(
                apiCall = serverFunctions::apiReportSoles
            ).first { it !is ResultadoApi.Loading }

            val lineas = mapLineasResult(base) {
                _solesEvent.emit(it)
            }

            if (lineas.isEmpty()) return@launch

            // 🔹 2. Detalle por línea
            val resultado = coroutineScope {
                lineas.map { linea ->
                    async {

                        val request = when (tipoUsuario) {

                            TipoUsuario.VENDEDOR -> SolesRequestConfig(
                                apiCall = serverFunctions::apiReportSolesGenerico,
                                linea = linea.codigo
                            )

                            TipoUsuario.SUPERVISOR -> SolesRequestConfig(
                                apiCall = serverFunctions::apiReportPreventa,
                                marca = linea.codigo
                            )

                            else -> return@async linea
                        }

                        val result = downloadBaseReport(
                            apiCall = request.apiCall,
                            linea = request.linea,
                            marca = request.marca
                        ).first { it !is ResultadoApi.Loading }

                        val soles = mapSolesResult(result) {
                            mapVolumenToSoles(it)
                        }

                        linea.copy(
                            soles = soles,
                            isLoading = false
                        )
                    }
                }.awaitAll()
            }

            _solesEvent.emit(ResultadoApi.Exito(resultado))
        }
    }

    fun apiDetalleCobertura() {
        viewModelScope.launch {
            downloadBaseReport(serverFunctions::apiReportCoberturaDetalle)
                .collect { _coberturaDetalleEvent.emit(it) }
        }
    }

    fun apiPendienteCobertura() {
        viewModelScope.launch {
            downloadBaseReport(serverFunctions::apiReportCoberturaPendiente)
                .collect { _coberturaPendienteEvent.emit(it) }
        }
    }

    fun apiEmpleadoPedidos() {
        viewModelScope.launch {
            downloadBaseReport(serverFunctions::apiReportEmpleado)
                .collect { _pedidoEmpleadoEvent.emit(it) }
        }
    }

    fun apiSolesDetalle(linea: Int?) {
        viewModelScope.launch {
            val codigo = linea ?: return@launch
            downloadBaseReport(
                apiCall = serverFunctions::apiReportSolesGenerico,
                linea = codigo
            ).collect { _solesDetalleEvent.emit(it) }
        }
    }

    fun apiSubSolesDetalle(linea: Int) {
        viewModelScope.launch {
            downloadBaseReport(
                apiCall = serverFunctions::apiReportPreventa,
                linea = linea
            ).collect { _subSolesEvent.emit(it) }
        }
    }

    fun setEncuestaSeleccionada(id: Int) {
        viewModelScope.launch {
            roomFunctions.reselectEncuesta(id.toString())
        }
    }

    fun selectUniqueEncuesta() {
        viewModelScope.launch {
            val cabeceras = roomFunctions.queryCabeceraEncuesta()

            if (cabeceras.size == 1) {
                roomFunctions.updateEncuestaSeleccion(cabeceras.first().id.toString())
            }
        }
    }

    fun flowClientesFiltrados(
        query: StateFlow<String>
    ): StateFlow<List<FlowCliente>> =
        combine(flowClientes, query) { lista, q ->
            clienteSearchSource.filtrar(lista, q)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun flowBajasFiltrados(
        query: StateFlow<String>
    ): StateFlow<List<TableBaja>> =
        combine(flowBaja, query) { lista, q ->
            bajaSearchSource.filtrarGeneradas(lista, q)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun flowBajasSupervisorFiltrados(
        query: StateFlow<String>
    ): StateFlow<List<FlowBajaSupervisor>> =
        combine(flowBajaSupervisor, query) { lista, q ->
            bajaSearchSource.filtrarSupervisor(lista, q)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    fun saveAndSendBaja(item: TableBaja) {
        viewModelScope.launch {
            roomFunctions.saveBaja(item)

            handleResult(
                result = sendServerFunctions.enviarBaja(item),
                onError = { _bajaMessage.emit(it) }
            )
        }
    }

    fun retrySendBaja(item: TableBaja) {
        viewModelScope.launch {
            val actualizado = item.copy(sincronizado = false)
            roomFunctions.updateBaja(actualizado)

            handleResult(
                result = sendServerFunctions.enviarBaja(actualizado),
                onError = { _bajaMessage.emit(it) }
            )
        }
    }

    fun saveAndSendBajaProcesada(item: TableBajaProcesada) {
        viewModelScope.launch {
            roomFunctions.saveBajaProcesada(item)

            handleResult(
                result = sendServerFunctions.enviarBajaProcesada(item),
                onError = { _bajaProcesadaMessage.emit(it) }
            )
        }
    }

    fun createAlta(location: Location) {
        viewModelScope.launch {
            val config = roomFunctions.queryConfiguracion() ?: return@launch
            val fecha = FechaHoraUtil.ahora()
            val timeStamp = FechaHoraUtil.timestamp()

            val idaux = "${config.codigo}$timeStamp"

            val item = TableAlta(
                idaux = idaux,
                empleado = config.codigo,
                fecha = fecha,
                longitud = location.longitude,
                latitud = location.latitude,
                precision = location.accuracy.toDouble().to2Decimals(),
                datos = 0
            )

            saveAndSendAlta(item)
        }
    }

    private fun marcarAltaConDatos(idaux: String, fecha: String) {
        viewModelScope.launch {
            val alta = roomFunctions.queryAltaSpecific(idaux, fecha) ?: return@launch
            val actualizado = alta.copy(datos = 1)
            roomFunctions.updateAlta(actualizado)
        }
    }

    private fun saveAndSendAlta(item: TableAlta) {
        viewModelScope.launch {
            roomFunctions.saveAlta(item)

            handleResult(
                result = sendServerFunctions.enviarAlta(item),
                onError = { _altaMessage.emit(it) }
            )
        }
    }

    fun retrySendAlta(item: TableAlta) {
        viewModelScope.launch {
            val actualizado = item.copy(sincronizado = false)
            roomFunctions.updateAlta(actualizado)

            handleResult(
                result = sendServerFunctions.enviarAlta(actualizado),
                onError = { _altaMessage.emit(it) }
            )
        }
    }

    fun saveAndSendAltaDatos(item: TableAltaDatos) {
        viewModelScope.launch {
            roomFunctions.saveDatosAlta(item)

            // Actualizar campo datos de alta origen
            marcarAltaConDatos(item.idaux, item.fecha)

            handleResult(
                result = sendServerFunctions.enviarAltaDatos(item),
                onError = { _altaDatosMessage.emit(it) },
                onSuccess = { _altaDatosSuccess.emit(Unit) }
            )
        }
    }

    fun retrySendAltaDatos(item: TableAltaDatos) {
        viewModelScope.launch {
            val actualizado = item.copy(sincronizado = false)
            roomFunctions.updateDatosAlta(actualizado)

            handleResult(
                result = sendServerFunctions.enviarAltaDatos(actualizado),
                onError = { _altaDatosMessage.emit(it) },
                onSuccess = { _altaDatosSuccess.emit(Unit) }
            )
        }
    }

    fun saveAndSendRespuestas(item: List<TableRespuesta>) {
        viewModelScope.launch {
            roomFunctions.saveRespuestas(item)

            handleResult(
                result = sendServerFunctions.enviarRespuesta(item),
                onError = { _respuestaMessage.emit(it) }
            )
        }
    }

    fun saveAndSendFoto(item: TableFoto) {
        viewModelScope.launch {
            roomFunctions.saveFoto(item)

            handleResult(
                result = sendServerFunctions.enviarFoto(item),
                onError = { _fotoMessage.emit(it) }
            )
        }
    }

    fun executeUpdater() {
        viewModelScope.launch {
            val config = roomFunctions.queryConfiguracion() ?: return@launch

            serverFunctions.apiSocketUpdate(config.empresa)
                .collect { event ->

                    _socketEvent.emit(event)

                    if (event is SocketEvent.Error) {
                        return@collect
                    }
                }
        }
    }

    fun downloadAllReports() {
        apiPreventa()
        apiCobertura()
        apiCartera()
        apiGeneral()
        apiCambio()
        apiSolesPorLineas()
    }

    fun downloadDetailReport(params: DetalleParams) {
        viewModelScope.launch {
            val tipoAccion = params.tipo.resolveAction(params.tipoUsuario)

            when (tipoAccion) {
                ReportAction.PREVENTA -> apiPreventa()
                ReportAction.COBERTURA_SUP -> apiCobertura()
                ReportAction.COBERTURA_VEN -> apiDetalleCobertura()
                ReportAction.CARTERA_SUP -> apiCartera()
                ReportAction.CARTERA_VEN -> apiPendienteCobertura()
                ReportAction.PEDIDOS -> apiEmpleadoPedidos()
                ReportAction.CAMBIOS -> apiCambio()
                ReportAction.SOLES -> apiSolesDetalle(params.codigoLinea)
            }
        }
    }

    private fun <T> downloadBaseReport(
        apiCall: suspend (RequestBody) -> Flow<ResultadoApi<T>>,
        linea: Int? = null,
        marca: Int? = null
    ): Flow<ResultadoApi<T>> = flow {

        val config = roomFunctions.queryConfiguracion()
            ?: return@flow

        /*val json = jsobFunctions.jsonObjectReporte(
            item = config,
            linea = linea,
            marca = marca
        )*/

        // Momentaneo
        val json = JSONObject().apply {
            put("empleado", config.codigo)
            put("empresa", config.empresa)
            linea?.let { put("linea", it) }
            marca?.let { put("marca", it) }

            // 👇 temporal
            put("fecha", "2026-04-01")
        }.toReqBody()

        emitAll(apiCall(json))
    }

    private suspend fun mapLineasResult(
        result: ResultadoApi<JsonSoles>,
        onError: suspend (ResultadoApi<List<LineaUI>>) -> Unit
    ): List<LineaUI> {

        return when (result) {

            is ResultadoApi.Exito -> {
                result.data?.let { mapToLineas(it) } ?: emptyList()
            }

            is ResultadoApi.ErrorHttp -> {
                onError(result)
                emptyList()
            }

            is ResultadoApi.Fallo -> {
                onError(result)
                emptyList()
            }

            is ResultadoApi.Loading -> emptyList()
        }
    }

    private fun <T, R> mapSolesResult(
        result: ResultadoApi<T>,
        mapper: (T) -> List<R>
    ): List<R> {
        return if (result is ResultadoApi.Exito) {
            result.data?.let(mapper) ?: emptyList()
        } else emptyList()
    }

    private suspend fun handleResult(
        result: ResultadoApi<Unit>,
        onError: suspend (String) -> Unit,
        onSuccess: (suspend () -> Unit)? = null
    ) {
        when (result) {
            is ResultadoApi.ErrorHttp,
            is ResultadoApi.Fallo -> {
                result.mensajeUsuario()?.let { onError(it) }
            }

            is ResultadoApi.Exito -> {
                onSuccess?.invoke()
            }

            else -> Unit
        }
    }
}