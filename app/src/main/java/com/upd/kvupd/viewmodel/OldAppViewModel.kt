package com.upd.kvupd.viewmodel

import androidx.lifecycle.ViewModel
import com.upd.kvupd.domain.OldFunctions
import com.upd.kvupd.domain.OldRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OldAppViewModel @Inject constructor(
    private val repository: OldRepository,
    private val functions: OldFunctions
) : ViewModel() {

    private val _tag by lazy { OldAppViewModel::class.java.simpleName }

    /*private val _startUp: MutableLiveData<OldEvent<Boolean>> = MutableLiveData()
    val startUp: LiveData<OldEvent<Boolean>> = _startUp

    private val _ipaux: MutableLiveData<OldEvent<String>> = MutableLiveData()
    val ipaux: LiveData<OldEvent<String>> = _ipaux

    private val _sincro: MutableLiveData<OldEvent<Int>> = MutableLiveData()
    val sincro: LiveData<OldEvent<Int>> = _sincro

    private val _inicio: MutableLiveData<OldEvent<Boolean>> = MutableLiveData()
    val inicio: LiveData<OldEvent<Boolean>> = _inicio

    private val _checking: MutableLiveData<Boolean> = MutableLiveData()
    val checking: LiveData<Boolean> = _checking

    private val _filtro: MutableLiveData<OldEvent<Int>> = MutableLiveData()
    val filtro: LiveData<OldEvent<Int>> = _filtro

    private val _filtromark: MutableLiveData<OldEvent<Int>> = MutableLiveData()
    val filtromark: LiveData<OldEvent<Int>> = _filtromark

    private val _fecha: MutableLiveData<OldEvent<String>> = MutableLiveData()
    val fecha: LiveData<OldEvent<String>> = _fecha

    private val _climap: MutableLiveData<OldEvent<String>> = MutableLiveData()
    val climap: LiveData<OldEvent<String>> = _climap

    private val _marker: MutableLiveData<OldEvent<List<MarkerMap>>> = MutableLiveData()
    val marker: LiveData<OldEvent<List<MarkerMap>>> = _marker

    private val _vendedor: MutableLiveData<OldEvent<List<String>>> = MutableLiveData()
    val vendedor: LiveData<OldEvent<List<String>>> = _vendedor

    private val _detail: MutableLiveData<OldEvent<List<DataCliente>>> = MutableLiveData()
    val detail: LiveData<OldEvent<List<DataCliente>>> = _detail

    private val _altamark: MutableLiveData<OldEvent<DataAlta>> = MutableLiveData()
    val altamark: LiveData<OldEvent<DataAlta>> = _altamark

    private val _bajasuperspecif: MutableLiveData<OldEvent<TBajaSuper>> = MutableLiveData()
    val bajasuperspecif: LiveData<OldEvent<TBajaSuper>> = _bajasuperspecif

    /*private val _register: MutableLiveData<OldEvent<OldNetworkRetrofit<JObj>>> = MutableLiveData()
    val register: LiveData<OldEvent<OldNetworkRetrofit<JObj>>> = _register*/

    private val _login: MutableLiveData<OldEvent<OldNetworkRetrofit<Login>>> = MutableLiveData()
    val login: LiveData<OldEvent<OldNetworkRetrofit<Login>>> = _login

    private val _cliente: MutableLiveData<OldEvent<OldNetworkRetrofit<JCliente>>> = MutableLiveData()
    val cliente: LiveData<OldEvent<OldNetworkRetrofit<JCliente>>> = _cliente

    private val _rutas: MutableLiveData<OldEvent<OldNetworkRetrofit<JRuta>>> = MutableLiveData()
    val rutas: LiveData<OldEvent<OldNetworkRetrofit<JRuta>>> = _rutas

    private val _encuesta: MutableLiveData<OldEvent<OldNetworkRetrofit<JEncuesta>>> = MutableLiveData()
    val encuesta: LiveData<OldEvent<OldNetworkRetrofit<JEncuesta>>> = _encuesta

    private val _consulta: MutableLiveData<OldEvent<OldNetworkRetrofit<JConsulta>>> = MutableLiveData()
    val consulta: LiveData<OldEvent<OldNetworkRetrofit<JConsulta>>> = _consulta

    private val _preventa: MutableLiveData<OldEvent<OldNetworkRetrofit<JVolumen>>> = MutableLiveData()
    val preventa: LiveData<OldEvent<OldNetworkRetrofit<JVolumen>>> = _preventa

    private val _cobertura: MutableLiveData<OldEvent<OldNetworkRetrofit<JCobCart>>> = MutableLiveData()
    val cobertura: LiveData<OldEvent<OldNetworkRetrofit<JCobCart>>> = _cobertura

    private val _cartera: MutableLiveData<OldEvent<OldNetworkRetrofit<JCobCart>>> = MutableLiveData()
    val cartera: LiveData<OldEvent<OldNetworkRetrofit<JCobCart>>> = _cartera

    private val _pedidos: MutableLiveData<OldEvent<OldNetworkRetrofit<JPedido>>> = MutableLiveData()
    val pedidos: LiveData<OldEvent<OldNetworkRetrofit<JPedido>>> = _pedidos

    /*private val _visicooler: MutableLiveData<OldEvent<OldNetworkRetrofit<JVisicooler>>> =
        MutableLiveData()
    val visicooler: LiveData<OldEvent<OldNetworkRetrofit<JVisicooler>>> = _visicooler

    private val _visisuper: MutableLiveData<OldEvent<OldNetworkRetrofit<JVisisuper>>> = MutableLiveData()
    val visisuper: LiveData<OldEvent<OldNetworkRetrofit<JVisisuper>>> = _visisuper*/

    private val _cambios: MutableLiveData<OldEvent<OldNetworkRetrofit<JCambio>>> = MutableLiveData()
    val cambios: LiveData<OldEvent<OldNetworkRetrofit<JCambio>>> = _cambios

    //private val _umes: MutableLiveData<OldEvent<OldNetworkRetrofit<JUmes>>> = MutableLiveData()
    //val umes: LiveData<OldEvent<OldNetworkRetrofit<JUmes>>> = _umes

    private val _soles: MutableLiveData<OldEvent<OldNetworkRetrofit<JSoles>>> = MutableLiveData()
    val soles: LiveData<OldEvent<OldNetworkRetrofit<JSoles>>> = _soles

    private val _generico: MutableLiveData<OldEvent<OldNetworkRetrofit<JGenerico>>> = MutableLiveData()
    val generico: LiveData<OldEvent<OldNetworkRetrofit<JGenerico>>> = _generico

    private val _detcob: MutableLiveData<OldEvent<OldNetworkRetrofit<JDetCob>>> = MutableLiveData()
    val detcob: LiveData<OldEvent<OldNetworkRetrofit<JDetCob>>> = _detcob

    private val _detalle: MutableLiveData<OldEvent<OldNetworkRetrofit<JGenerico>>> = MutableLiveData()
    val detalle: LiveData<OldEvent<OldNetworkRetrofit<JGenerico>>> = _detalle

    private val _cobpendiente: MutableLiveData<OldEvent<OldNetworkRetrofit<JCoberturados>>> =
        MutableLiveData()
    val cobpendiente: LiveData<OldEvent<OldNetworkRetrofit<JCoberturados>>> = _cobpendiente

    private val _pedigen: MutableLiveData<OldEvent<OldNetworkRetrofit<JPediGen>>> = MutableLiveData()
    val pedigen: LiveData<OldEvent<OldNetworkRetrofit<JPediGen>>> = _pedigen

    private val _pedimap: MutableLiveData<OldEvent<OldNetworkRetrofit<JPedimap>>> = MutableLiveData()
    val pedimap: LiveData<OldEvent<OldNetworkRetrofit<JPedimap>>> = _pedimap

    private val _bajasuper: MutableLiveData<OldEvent<OldNetworkRetrofit<JBajaSupervisor>>> =
        MutableLiveData()
    val bajasuper: LiveData<OldEvent<OldNetworkRetrofit<JBajaSupervisor>>> = _bajasuper

    private val _bajaven: MutableLiveData<OldEvent<OldNetworkRetrofit<JBajaVendedor>>> = MutableLiveData()
    val bajavend: LiveData<OldEvent<OldNetworkRetrofit<JBajaVendedor>>> = _bajaven

    private val _altadatos: MutableLiveData<OldEvent<TADatos?>> = MutableLiveData()
    val altadatos: LiveData<OldEvent<TADatos?>> = _altadatos

    private val _consultado: MutableLiveData<OldEvent<List<TConsulta>>> = MutableLiveData()
    val consultado: LiveData<OldEvent<List<TConsulta>>> = _consultado

    private val _clienteRoom: MutableLiveData<OldEvent<List<TClientes>>> = MutableLiveData()
    val clienteRoom: LiveData<OldEvent<List<TClientes>>> = _clienteRoom

    private val _urlServer: MutableLiveData<OldEvent<Boolean>> = MutableLiveData()
    val urlServer: LiveData<OldEvent<Boolean>> = _urlServer

    private val _preguntas: MutableLiveData<OldEvent<List<TEncuesta>>> = MutableLiveData()
    val preguntas: LiveData<OldEvent<List<TEncuesta>>> = _preguntas

    private val _respuestas = mutableStateMapOf<Int, String>() // preguntaId -> respuesta
    val respuestas: Map<Int, String> get() = _respuestas

    private val _respuestaPrevia: MutableLiveData<OldEvent<RespuestaClientePrevio>> = MutableLiveData()
    val respuestaPrevia: LiveData<OldEvent<RespuestaClientePrevio>> = _respuestaPrevia

    fun actualizarRespuesta(preguntaId: Int, respuesta: String) {
        _respuestas[preguntaId] = respuesta
    }

    fun limpiarRespuestas() {
        _respuestas.clear()
    }

    fun configObserver() = repository.getFlowConfig().asLiveData()

    fun sessionObserver() = repository.getFlowSession().asLiveData()

    fun rowClienteObs() = repository.getFlowRowCliente().asLiveData()

    fun lastLocation() = repository.getFlowLocation().asLiveData()

    fun altasObs() = repository.getFlowAltas().asLiveData()

    fun distritosObs() = repository.getFlowDistritos().asLiveData()

    fun negociosObs() = repository.getFlowNegocios().asLiveData()

    fun bajasObs() = repository.getFlowBajas().asLiveData()

    fun rowBajaObs() = repository.getFlowRowBaja().asLiveData()

    fun rutasObs() = repository.getFlowRutas().asLiveData()

    fun incidenciaObs() = repository.getFlowIncidencias().asLiveData()

    private val _servseguimiento: MutableLiveData<OldEvent<List<TSeguimiento>>> = MutableLiveData()
    val servseguimiento: LiveData<OldEvent<List<TSeguimiento>>> = _servseguimiento

    private val _servvisita: MutableLiveData<OldEvent<List<TVisita>>> = MutableLiveData()
    val servvisita: LiveData<OldEvent<List<TVisita>>> = _servvisita

    private val _servalta: MutableLiveData<OldEvent<List<TAlta>>> = MutableLiveData()
    val servalta: LiveData<OldEvent<List<TAlta>>> = _servalta

    private val _servaltadatos: MutableLiveData<OldEvent<List<TADatos>>> = MutableLiveData()
    val servaltadatos: LiveData<OldEvent<List<TADatos>>> = _servaltadatos

    private val _servbaja: MutableLiveData<OldEvent<List<TBaja>>> = MutableLiveData()
    val servbaja: LiveData<OldEvent<List<TBaja>>> = _servbaja

    private val _servbajaestado: MutableLiveData<OldEvent<List<TBEstado>>> = MutableLiveData()
    val servbajaestado: LiveData<OldEvent<List<TBEstado>>> = _servbajaestado

    private val _servrespuesta: MutableLiveData<OldEvent<List<TRespuesta>>> = MutableLiveData()
    val servrespuesta: LiveData<OldEvent<List<TRespuesta>>> = _servrespuesta

    private val _servfoto: MutableLiveData<OldEvent<List<TRespuesta>>> = MutableLiveData()
    val servfoto: LiveData<OldEvent<List<TRespuesta>>> = _servfoto

    private val _servdni: MutableLiveData<OldEvent<List<TAFoto>>> = MutableLiveData()
    val servdni: LiveData<OldEvent<List<TAFoto>>> = _servdni

    /*private val _respseguimiento: MutableLiveData<OldEvent<OldNetworkRetrofit<JObj>>> = MutableLiveData()
    val respseguimiento: LiveData<OldEvent<OldNetworkRetrofit<JObj>>> = _respseguimiento

    private val _respvisita: MutableLiveData<OldEvent<OldNetworkRetrofit<JObj>>> = MutableLiveData()
    val respvisita: LiveData<OldEvent<OldNetworkRetrofit<JObj>>> = _respvisita

    private val _respalta: MutableLiveData<OldEvent<OldNetworkRetrofit<JObj>>> = MutableLiveData()
    val respalta: LiveData<OldEvent<OldNetworkRetrofit<JObj>>> = _respalta

    private val _respaltadatos: MutableLiveData<OldEvent<OldNetworkRetrofit<JObj>>> = MutableLiveData()
    val respaltadatos: LiveData<OldEvent<OldNetworkRetrofit<JObj>>> = _respaltadatos

    private val _respbaja: MutableLiveData<OldEvent<OldNetworkRetrofit<JObj>>> = MutableLiveData()
    val respbaja: LiveData<OldEvent<OldNetworkRetrofit<JObj>>> = _respbaja

    private val _respbajaestado: MutableLiveData<OldEvent<OldNetworkRetrofit<JObj>>> = MutableLiveData()
    val respbajaestado: LiveData<OldEvent<OldNetworkRetrofit<JObj>>> = _respbajaestado

    private val _resprespuesta: MutableLiveData<OldEvent<OldNetworkRetrofit<JObj>>> = MutableLiveData()
    val resprespuesta: LiveData<OldEvent<OldNetworkRetrofit<JObj>>> = _resprespuesta*/

    /*private val _respfoto: MutableLiveData<OldEvent<OldNetworkRetrofit<JRespuestaFoto>>> = MutableLiveData()
    val respfoto: LiveData<OldEvent<OldNetworkRetrofit<JRespuestaFoto>>> = _respfoto

    private val _respdni: MutableLiveData<OldEvent<OldNetworkRetrofit<JRespuestaFoto>>> = MutableLiveData()
    val respdni: LiveData<OldEvent<OldNetworkRetrofit<JRespuestaFoto>>> = _respdni*/

    private val _cabecera: MutableLiveData<OldEvent<List<Cabecera>>> = MutableLiveData()
    val cabecera: LiveData<OldEvent<List<Cabecera>>> = _cabecera

    private val _clienteRespondio: MutableLiveData<OldEvent<Boolean>> = MutableLiveData()
    val clienteRespondio: LiveData<OldEvent<Boolean>> = _clienteRespondio

    fun startingApp() {
        _startUp.value = OldEvent(true)
    }

    fun markerMap(observacion: Int) {
        viewModelScope.launch {
            repository.getFlowMarker(observacion.toString()).collect {
                _marker.value = OldEvent(it)
            }
        }
    }

    fun fetchServerAll() {
        viewModelScope.launch {
            repository.getServerSeguimiento("Todo").let {
                _servseguimiento.value = OldEvent(it)
            }
            repository.getServerVisita("Todo").let {
                _servvisita.value = OldEvent(it)
            }
            repository.getServerAlta("Todo").let {
                _servalta.value = OldEvent(it)
            }
            repository.getServerAltadatos("Todo").let {
                _servaltadatos.value = OldEvent(it)
            }
            repository.getServerBaja("Todo").let {
                _servbaja.value = OldEvent(it)
            }
            repository.getServerBajaestado("Todo").let {
                _servbajaestado.value = OldEvent(it)
            }
            repository.getServerRespuesta("Todo").let {
                _servrespuesta.value = OldEvent(it)
            }
            repository.getServerFoto("Todo").let {
                _servfoto.value = OldEvent(it)
            }
            repository.getServerAltaFoto("Todo").let {
                _servdni.value = OldEvent(it)
            }
        }
    }

    fun webSeguimiento(body: RequestBody) = viewModelScope.launch {
        /*repository.setWebSeguimiento(body).collect {
            _respseguimiento.value = OldEvent(it)
        }*/
    }

    fun webVisita(body: RequestBody) = viewModelScope.launch {
        /*repository.setWebVisita(body).collect {
            _respvisita.value = OldEvent(it)
        }*/
    }

    fun webAlta(body: RequestBody) = viewModelScope.launch {
        /*repository.setWebAlta(body).collect {
            _respalta.value = OldEvent(it)
        }*/
    }

    fun webAltaDatos(body: RequestBody) = viewModelScope.launch {
        /*repository.setWebAltaDatos(body).collect {
            _respaltadatos.value = OldEvent(it)
        }*/
    }

    fun webBaja(body: RequestBody) = viewModelScope.launch {
        /*repository.setWebBaja(body).collect {
            _respbaja.value = OldEvent(it)
        }*/
    }

    fun webBajaEstado(body: RequestBody) = viewModelScope.launch {
        /*repository.setWebBajaEstados(body).collect {
            _respbajaestado.value = OldEvent(it)
        }*/
    }

    fun webRespuesta(body: RequestBody) = viewModelScope.launch {
        /*repository.setWebRespuestas(body).collect {
            _resprespuesta.value = OldEvent(it)
        }*/
    }

    fun webFoto(body: RequestBody) = viewModelScope.launch {
        /*repository.setWebFotos(body).collect {
            _respfoto.value = OldEvent(it)
        }*/
    }

    fun webDNI(body: RequestBody) = viewModelScope.launch {
        /*repository.setWebFotos(body).collect {
            _respdni.value = OldEvent(it)
        }*/
    }

    fun fetchRegisterDevice(body: RequestBody) = viewModelScope.launch {
        /*repository.registerWebDevice(body).collect {
            _register.value = OldEvent(it)
        }*/
    }

    fun fetchLoginAdmin(body: RequestBody) = viewModelScope.launch {
        /*repository.loginAdministrator(body).collect {
            _login.value = OldEvent(it)
        }*/
    }

    fun fetchClientes(body: RequestBody) = viewModelScope.launch {
        /*repository.getWebClientes(body).collect { values ->
            values.data?.jobl?.let { repository.saveClientes(it) }
            _cliente.value = OldEvent(values)
        }*/
    }

    fun fetchRutas(body: RequestBody) = viewModelScope.launch {
        /*repository.getWebRutas(body).collect() { values ->
            values.data?.jobl?.let { repository.saveRutas(it) }
            _rutas.value = OldEvent(values)
        }*/
    }

    fun fetchEncuesta(body: RequestBody) = viewModelScope.launch {
        /*repository.getWebEncuesta(body).collect { values ->
            values.data?.jobl.let {
                if (!it.isNullOrEmpty()) {
                    repository.saveEncuesta(it)
                    if (CONF.tipo == "V") {
                        val item = TEncuestaSeleccionado(1, it[0].id, it[0].foto)
                        repository.saveSeleccionado(item)
                    }
                }
            }
            _encuesta.value = OldEvent(values)
        }*/
    }

    fun fetchConsulta(body: RequestBody) = viewModelScope.launch {
        /*repository.getWebConsulta(body).collect { values ->
            values.data?.jobl?.let { repository.saveConsulta(it) }
            _consulta.value = OldEvent(values)
        }*/
    }

    fun fetchPreventa(body: RequestBody) = viewModelScope.launch {
        /*repository.getWebPreventa(body).collect {
            _preventa.value = OldEvent(it)
        }*/
    }

    fun fetchCobertura(body: RequestBody) = viewModelScope.launch {
        /*repository.getWebCobertura(body).collect {
            _cobertura.value = OldEvent(it)
        }*/
    }

    fun fetchCartera(body: RequestBody) = viewModelScope.launch {
        /*repository.getWebCartera(body).collect {
            _cartera.value = OldEvent(it)
        }*/
    }

    fun fetchPedidos(body: RequestBody) = viewModelScope.launch {
        /*repository.getWebPedidos(body).collect {
            _pedidos.value = OldEvent(it)
        }*/
    }

    fun fetchVisicooler(body: RequestBody) = viewModelScope.launch {

        /*repository.getWebVisicooler(body).collect {
            _visicooler.value = OldEvent(it)
        }*/
    }

    fun fetchVisisuper(body: RequestBody) = viewModelScope.launch {
        /*repository.getWebVisisuper(body).collect {
            _visisuper.value = OldEvent(it)
        }*/
    }

    fun fetchCambios(body: RequestBody) = viewModelScope.launch {
        /*if (CONF.tipo == "S") {
            repository.getWebCambiosEmp(body).collect {
                _cambios.value = OldEvent(it)
            }
        } else {
            repository.getWebCambiosCli(body).collect {
                _cambios.value = OldEvent(it)
            }
        }*/
    }

    fun getClienteConsultado(numero: String, nombre: String) = viewModelScope.launch {
        /*repository.getConsultaCliente(numero, nombre).let {
            _consultado.value = OldEvent(it)
        }*/
    }

    fun fetchSoles(body: RequestBody) = viewModelScope.launch {
        /*repository.getWebSoles(body).collect {
            _soles.value = OldEvent(it)
        }*/
    }

    fun fetchSolesGenerico(body: RequestBody) = viewModelScope.launch {
        /*repository.getWebSolesGenerico(body).collect {
            _generico.value = OldEvent(it)
        }*/
    }

    fun fetchDetCobertura(body: RequestBody) = viewModelScope.launch {
        /*repository.getWebCoberturaDetalle(body).collect {
            _detcob.value = OldEvent(it)
        }*/
    }

    fun fetchSolesDetalle(body: RequestBody) = viewModelScope.launch {
        /*repository.getWebSolesDetalle(body).collect {
            _detalle.value = OldEvent(it)
        }*/
    }

    fun fetchCoberturaPendiente(body: RequestBody) = viewModelScope.launch {
        /*repository.getWebCoberturaPendiente(body).collect {
            _cobpendiente.value = OldEvent(it)
        }*/
    }

    fun fetchPediGen(body: RequestBody) = viewModelScope.launch {
        /*repository.getWebPedidosRealizados(body).collect {
            _pedigen.value = OldEvent(it)
        }*/
    }

    fun fetchPedimap(body: RequestBody) = viewModelScope.launch {
        /*repository.getWebPedimap(body).collect {
            _pedimap.value = OldEvent(it)
        }*/
    }

    fun fetchBajaVendedor(body: RequestBody) = viewModelScope.launch {
        /*repository.getWebBajaVendedor(body).collect {
            _bajaven.value = OldEvent(it)
        }*/
    }

    fun fetchBajaSupervisor(body: RequestBody) = viewModelScope.launch {
        /*repository.getWebBajaSupervisor(body).collect { values ->
            values.data?.jobl?.let { repository.saveBajaSuper(it) }
            _bajasuper.value = OldEvent(values)
        }*/
    }

    fun fetchAltaDatos(alta: String) = viewModelScope.launch {
        repository.getAltaDatoSpecific(alta).let {
            _altadatos.value = OldEvent(it)
        }
    }

    fun getClientes() = viewModelScope.launch {
        repository.getClientes().let { result ->
            val newList = result.map { it.asTCliente() }
            _clienteRoom.value = OldEvent(newList)
        }
    }

    fun getPreguntas() = viewModelScope.launch {
        repository.getPreguntas().let {
            _preguntas.value = OldEvent(it)
        }
    }

    suspend fun isEncuestaEmpty(): Boolean =
        repository.getPreguntas().isEmpty()

    suspend fun isConfigEmpty(): Boolean =
        repository.getConfig() == null

    suspend fun isClienteBaja(cliente: String): Boolean =
        repository.isClienteBaja(cliente)

    suspend fun gettingVendedores() =
        repository.getEmpleados()

    fun setFecha(fecha: String) {
        _fecha.value = OldEvent(fecha)
    }

    fun setFiltro(filtro: Int) {
        _filtro.value = OldEvent(filtro)
    }

    fun filterMarkerObs(filtro: Int) {
        _filtromark.value = OldEvent(filtro)
    }

    fun setClienteSelect(cliente: String) {
        _climap.value = OldEvent(cliente)
    }

    fun setVendedorSelect(list: List<String>) {
        _vendedor.value = OldEvent(list)
    }

    fun getClientDet(cliente: String, observacion: Int) {
        viewModelScope.launch {
            val result = repository.getClienteDetail(cliente, observacion.toString())
            _detail.value = OldEvent(result)
        }
    }

    fun getAltaData(alta: String) {
        viewModelScope.launch {
            val result = repository.getDataAlta(alta)
            _altamark.value = OldEvent(result)
        }
    }

    fun getBajaSuperSpecific(codigo: String, fecha: String) {
        viewModelScope.launch {
            val result = repository.getBajaSuperSpecific(codigo, fecha)
            _bajasuperspecif.value = OldEvent(result)
        }
    }

    fun checkHoursAndLaunch(T: () -> Unit) {
        if (functions.existQR()) {
            intoHours()
        } else {
            T()
        }
    }

    fun generateAndSaveQR(value: String): Bitmap {
        val qr = functions.generateQR(value)
        functions.saveQR(qr)
        return qr
    }

    fun getIP() =
        functions.parseQRtoIP()

    fun getIMEI(add: Boolean = false) =
        functions.parseQRtoIMEI(add)

    fun getQR(value: String) =
        functions.generateQR(value)

    fun setMarker(map: GoogleMap, list: List<MarkerMap>) =
        functions.setupMarkers(map, list)

    fun pedimapMarker(map: GoogleMap, list: List<Pedimap>) =
        functions.pedimapMarkers(map, list)

    fun altaMarker(map: GoogleMap, list: List<TAlta>) =
        functions.altaMarkers(map, list)

    fun bajaMarker(map: GoogleMap, baja: TBajaSuper) =
        functions.bajaMarker(map, baja)

    fun consultaMarker(map: GoogleMap, list: List<TConsulta>) =
        functions.consultaMarker(map, list)

    fun launchPosition() {
        functions.executeService("position", false)
    }

    fun launchSetup() {
        functions.executeService("setup", false)
    }

    fun filterListCliente(list: List<DataCliente>) =
        functions.filterListCliente(list)

    fun fecha(opt: Int) =
        Calendar.getInstance().time.dateToday(opt)

    fun saveVisita(visita: TVisita, ruta: Int) {
        viewModelScope.launch {
            repository.saveVisita(visita)
            repository.saveEstado(visita.asTEstado(ruta))
        }
    }

    fun saveBaja(baja: TBaja, ruta: Int) {
        viewModelScope.launch {
            repository.saveBaja(baja)
            repository.saveEstado(baja.asTEstado(ruta))
        }
    }

    fun addingAlta(location: Location) {
        viewModelScope.launch {
            repository.processAlta(Calendar.getInstance().time.dateToday(4), location)
        }
    }

    fun saveAltaDatos(datos: TADatos) {
        viewModelScope.launch {
            val mini = MiniUpdAlta(datos.idaux, 1)
            repository.saveAltaDatos(datos)
            repository.updateMiniAlta(mini)
        }
    }

    fun saveAltaFoto(it: TAFoto) {
        viewModelScope.launch {
            repository.saveAltaFoto(it)
        }
    }

    fun updateLocationAlta(m: Marker) {
        viewModelScope.launch {
            val item = LocationAlta(
                m.snippet!!.toInt(),
                Calendar.getInstance().time.dateToday(4),
                m.position.longitude,
                m.position.latitude,
                10.0,
                "Pendiente"
            )
            repository.updateLocationAlta(item)
        }
    }

    fun saveEstadoBaja(dato: TBEstado) {
        viewModelScope.launch {
            repository.saveBajaEstado(dato)
        }
    }

    fun saveSeleccion(datos: TEncuestaSeleccionado) {
        viewModelScope.launch {
            repository.saveSeleccionado(datos)
            rowClienteObs()
        }
    }

    fun updateBaja(datos: MiniUpdBaja) {
        viewModelScope.launch {
            repository.updateMiniBaja(datos)
        }
    }

    fun updSeguimiento(it: TSeguimiento) {
        viewModelScope.launch {
            repository.updateSeguimiento(it)
        }
    }

    fun updVisita(it: TVisita) {
        viewModelScope.launch {
            repository.updateVisita(it)
        }
    }

    fun updAlta(it: TAlta) {
        viewModelScope.launch {
            repository.updateAlta(it)
        }
    }

    fun updAltaDatos(it: TADatos) {
        viewModelScope.launch {
            repository.updateAltaDatos(it)
        }
    }

    fun updAltaFoto(it: TAFoto) {
        viewModelScope.launch {
            repository.updateAltaFoto(it)
        }
    }

    fun updBaja(it: TBaja) {
        viewModelScope.launch {
            repository.updateBaja(it)
        }
    }

    fun updBajaEstado(it: TBEstado) {
        viewModelScope.launch {
            repository.updateBajaEstado(it)
        }
    }

    fun updRespuesta(it: TRespuesta) {
        viewModelScope.launch {
            repository.updateRespuesta(it)
        }
    }

    private fun intoHours() {
        viewModelScope.launch {
            _checking.value = repository.getIntoHours()
        }
    }

    fun dataDownloaded() {
        viewModelScope.launch {
            repository.getConfig().let {
                if (it != null) {
                    val user = when (it.tipo) {
                        "V" -> repository.getClientes().isNotEmpty()
                        else -> repository.getEmpleados().isNotEmpty()
                    }
                    val dist = repository.getDistritos().isNotEmpty()
                    val neg = repository.getNegocios().isNotEmpty()

                    _inicio.value = OldEvent(user && dist && neg)
                } else {
                    _inicio.value = OldEvent(false)
                }
            }
        }
    }

    fun internetAvailable() =
        functions.isConnected()

    fun fetchSinchro() {
        /*viewModelScope.launch {
            val conf = repository.getConfig()
            if (conf == null) {
                _sincro.value = OldEvent(90)
            } else {
                val j = JSONObject()
                j.put("empleado", conf.codigo)
                j.put("empresa", conf.empresa)

                val k = JSONObject()
                k.put("empresa", conf.empresa)

                if (conf.tipo == "V")
                    repository.getWebClientes(j.toReqBody()).collect {
                        it.data?.jobl?.let { y ->
                            repository.saveClientes(y)
                        }
                        _sincro.value = OldEvent(1)
                    }
                else
                    repository.getWebEmpleados(j.toReqBody()).collect {
                        it.data?.jobl?.let { y ->
                            repository.saveEmpleados(y)
                        }
                        _sincro.value = OldEvent(1)
                    }

                repository.getWebNegocios(k.toReqBody()).collect {
                    it.data?.jobl?.let { y ->
                        repository.saveNegocios(y)
                    }
                    _sincro.value = OldEvent(1)
                }
                repository.getWebDistritos(k.toReqBody()).collect {
                    it.data?.jobl?.let { y ->
                        repository.saveDistritos(y)
                    }
                    _sincro.value = OldEvent(1)
                }
                repository.getWebRutas(j.toReqBody()).collect {
                    it.data?.jobl?.let { y ->
                        repository.saveRutas(y)
                    }
                    _sincro.value = OldEvent(1)
                }
            }
        }*/
    }

    fun checkingEncuesta(T: (Boolean) -> Unit) {
        viewModelScope.launch {
            val lista = repository.getListEncuestas()
            val seleccionado = repository.getSeleccionado() != null
            when {
                lista.isEmpty() -> T(true)
                seleccionado -> T(true)
                else -> T(false)
            }
        }
    }

    fun gettingEncuestaLista() {
        viewModelScope.launch {
            val list = repository.getListEncuestas()
            _cabecera.value = OldEvent(list)
        }
    }

    fun settingIPaux(ip: String) {
        _ipaux.value = OldEvent(ip)
    }

    fun savingRespuestas(list: List<TRespuesta>) {
        viewModelScope.launch {
            repository.saveRespuesta(list)
        }
    }

    fun respuestaPreviaCliente(cabecera: List<Cabecera>, cliente: Int) {
        viewModelScope.launch {

            var clienteRespondio = false
            if (repository.clienteRespondioActual(cliente.toString())) {
                clienteRespondio = true
            } else {
                cabecera.forEach { i ->
                    if (i.seleccion == 1) {
                        val rsp = repository.clienteRespondioAntes(cliente.toString())
                        if (rsp.isEmpty()) {
                            clienteRespondio = false
                        } else {
                            rsp.split(",").forEach { j ->
                                if (j.trim() == i.id.toString()) {
                                    clienteRespondio = true
                                }
                            }
                        }
                    }
                }
            }
            _respuestaPrevia.value =
                OldEvent(RespuestaClientePrevio(clienteRespondio, cliente))
        }
    }

    fun clienteRespondio(cabecera: List<Cabecera>, cliente: String) {
        viewModelScope.launch {

            var respuesta = false
            if (repository.clienteRespondioActual(cliente)) {
                respuesta = true
            } else {
                cabecera.forEach { i ->
                    if (i.seleccion == 1) {
                        val rsp = repository.clienteRespondioAntes(cliente)
                        if (rsp.isEmpty()) {
                            respuesta = false
                        } else {
                            rsp.split(",").forEach { j ->
                                if (j.trim() == i.id.toString()) {
                                    respuesta = true
                                }
                            }
                        }
                    }
                }
            }
            _clienteRespondio.value = OldEvent(respuesta)
        }
    }

    fun appSo() = functions.appSO()

    fun deleteTables() {
        viewModelScope.launch {
            repository.deleteConfig()
            repository.deleteClientes()
            repository.deleteEmpleados()
            repository.deleteDistritos()
            repository.deleteNegocios()
            repository.deleteRutas()
            repository.deleteEncuesta()
            repository.deleteEncuestaSeleccionado()
            repository.deleteRespuesta()
            repository.deleteEstado()
            repository.deleteSeguimiento()
            repository.deleteVisita()
            repository.deleteAlta()
            repository.deleteAltaDatos()
            repository.deleteBaja()
            repository.deleteBajaSuper()
            repository.deleteBajaEstado()
            functions.deleteFotos()
            repository.deleteIncidencia()
            repository.deleteAAux()
        }
    }

    fun cleanSomeTables() {
        viewModelScope.launch {
            repository.deleteConfig()
            repository.deleteClientes()
            repository.deleteEmpleados()
            repository.deleteDistritos()
            repository.deleteNegocios()
            repository.deleteRutas()
            repository.deleteEncuesta()
            repository.deleteEncuestaSeleccionado()
            repository.deleteSeguimiento()
            repository.deleteAlta()
            repository.deleteAltaDatos()
            repository.deleteBaja()
            repository.deleteBajaSuper()
            repository.deleteBajaEstado()
            functions.deleteFotos()

            val item = functions.saveSystemActions("APP", "Limpieza y sincronizacion total")
            if (item != null) {
                repository.saveIncidencia(item)
            }
        }
    }

    fun cleanDataVendedor() {
        viewModelScope.launch {
            repository.deleteClientes()
            repository.deleteRutas()
        }
    }

    fun changeURLserver() {
        viewModelScope.launch {
            repository.getSesion().let { sesion ->
                when (OPTURL) {
                    "base" -> {
                        OPTURL = "aux"
                        IP_AUX = "http://${IPA}/api/"
                    }

                    "aux" -> {
                        OPTURL = "ipp"
                        IP_P = "http://${sesion!!.ipp}/api/"
                    }

                    "ipp" -> {
                        OPTURL = "ips"
                        IP_S = "http://${sesion!!.ips}/api/"
                    }

                    "ips" -> {
                        OPTURL = "aux"
                        IP_AUX = "http://${IPA}/api/"
                    }

                    else -> {
                        OPTURL = "base"
                    }
                }
            }
            _urlServer.value = OldEvent(true)
        }
    }
    */
}