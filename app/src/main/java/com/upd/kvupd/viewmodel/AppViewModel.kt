package com.upd.kvupd.viewmodel

import android.graphics.Bitmap
import android.location.Location
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.upd.kvupd.data.model.*
import com.upd.kvupd.domain.Functions
import com.upd.kvupd.domain.Repository
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Event
import com.upd.kvupd.utils.Network
import com.upd.kvupd.utils.toReqBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONObject

class AppViewModel @ViewModelInject constructor(
    private val repository: Repository,
    private val functions: Functions
) : ViewModel() {

    private val _tag by lazy { AppViewModel::class.java.simpleName }

    //  MutableLiveData with Event trigger only once
    private val _sincro: MutableLiveData<Event<Int>> = MutableLiveData()
    val sincro: LiveData<Event<Int>> = _sincro

    private val _inicio: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val inicio: LiveData<Event<Boolean>> = _inicio

    private val _checking: MutableLiveData<Boolean> = MutableLiveData()
    val checking: LiveData<Boolean> = _checking

    private val _filtro: MutableLiveData<Event<Int>> = MutableLiveData()
    val filtro: LiveData<Event<Int>> = _filtro

    private val _fecha: MutableLiveData<Event<String>> = MutableLiveData()
    val fecha: LiveData<Event<String>> = _fecha

    private val _climap: MutableLiveData<Event<String>> = MutableLiveData()
    val climap: LiveData<Event<String>> = _climap

    private val _marker: MutableLiveData<Event<List<MarkerMap>>> = MutableLiveData()
    val marker: LiveData<Event<List<MarkerMap>>> = _marker

    private val _vendedor: MutableLiveData<Event<List<String>>> = MutableLiveData()
    val vendedor: LiveData<Event<List<String>>> = _vendedor

    private val _detail: MutableLiveData<Event<List<DataCliente>>> = MutableLiveData()
    val detail: LiveData<Event<List<DataCliente>>> = _detail

    private val _altamark: MutableLiveData<Event<DataCliente>> = MutableLiveData()
    val altamark: LiveData<Event<DataCliente>> = _altamark

    private val _bajasuperspecif: MutableLiveData<Event<TBajaSuper>> = MutableLiveData()
    val bajasuperspecif: LiveData<Event<TBajaSuper>> = _bajasuperspecif

    private val _register: MutableLiveData<Event<Network<JObj>>> = MutableLiveData()
    val register: LiveData<Event<Network<JObj>>> = _register

    private val _login: MutableLiveData<Event<Network<Login>>> = MutableLiveData()
    val login: LiveData<Event<Network<Login>>> = _login

    private val _cliente: MutableLiveData<Event<Network<JCliente>>> = MutableLiveData()
    val cliente: LiveData<Event<Network<JCliente>>> = _cliente

    private val _encuesta: MutableLiveData<Event<Network<JEncuesta>>> = MutableLiveData()
    val encuesta: LiveData<Event<Network<JEncuesta>>> = _encuesta

    private val _preventa: MutableLiveData<Event<Network<JVolumen>>> = MutableLiveData()
    val preventa: LiveData<Event<Network<JVolumen>>> = _preventa

    private val _cobertura: MutableLiveData<Event<Network<JCobCart>>> = MutableLiveData()
    val cobertura: LiveData<Event<Network<JCobCart>>> = _cobertura

    private val _cartera: MutableLiveData<Event<Network<JCobCart>>> = MutableLiveData()
    val cartera: LiveData<Event<Network<JCobCart>>> = _cartera

    private val _pedidos: MutableLiveData<Event<Network<JPedido>>> = MutableLiveData()
    val pedidos: LiveData<Event<Network<JPedido>>> = _pedidos

    private val _visicooler: MutableLiveData<Event<Network<JVisicooler>>> = MutableLiveData()
    val visicooler: LiveData<Event<Network<JVisicooler>>> = _visicooler

    private val _visisuper: MutableLiveData<Event<Network<JVisisuper>>> = MutableLiveData()
    val visisuper: LiveData<Event<Network<JVisisuper>>> = _visisuper

    private val _cambiocli: MutableLiveData<Event<Network<JCambio>>> = MutableLiveData()
    val cambiocli: LiveData<Event<Network<JCambio>>> = _cambiocli

    private val _cambioemp: MutableLiveData<Event<Network<JCambio>>> = MutableLiveData()
    val cambioemp: LiveData<Event<Network<JCambio>>> = _cambioemp

    private val _umes: MutableLiveData<Event<Network<JUmes>>> = MutableLiveData()
    val umes: LiveData<Event<Network<JUmes>>> = _umes

    private val _soles: MutableLiveData<Event<Network<JSoles>>> = MutableLiveData()
    val soles: LiveData<Event<Network<JSoles>>> = _soles

    private val _generico: MutableLiveData<Event<Network<JGenerico>>> = MutableLiveData()
    val generico: LiveData<Event<Network<JGenerico>>> = _generico

    private val _detalle: MutableLiveData<Network<JGenerico>> = MutableLiveData()
    val detalle: LiveData<Network<JGenerico>> = _detalle

    private val _cobpendiente: MutableLiveData<Network<JCoberturados>> = MutableLiveData()
    val cobpendiente: LiveData<Network<JCoberturados>> = _cobpendiente

    private val _pedigen: MutableLiveData<Network<JPediGen>> = MutableLiveData()
    val pedigen: LiveData<Network<JPediGen>> = _pedigen

    private val _pedimap: MutableLiveData<Event<Network<JPedimap>>> = MutableLiveData()
    val pedimap: LiveData<Event<Network<JPedimap>>> = _pedimap

    private val _bajasuper: MutableLiveData<Event<Network<JBajaSupervisor>>> = MutableLiveData()
    val bajasuper: LiveData<Event<Network<JBajaSupervisor>>> = _bajasuper

    private val _bajaven: MutableLiveData<Event<Network<JBajaVendedor>>> = MutableLiveData()
    val bajavend: LiveData<Event<Network<JBajaVendedor>>> = _bajaven

    private val _altadatos: MutableLiveData<TADatos> = MutableLiveData()
    val altadatos: LiveData<TADatos> = _altadatos

    private val _preguntas: MutableLiveData<Event<List<TEncuesta>>> = MutableLiveData()
    val preguntas: LiveData<Event<List<TEncuesta>>> = _preguntas

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

    private val _servseguimiento: MutableLiveData<Event<List<TSeguimiento>>> = MutableLiveData()
    val servseguimiento: LiveData<Event<List<TSeguimiento>>> = _servseguimiento

    private val _servvisita: MutableLiveData<Event<List<TVisita>>> = MutableLiveData()
    val servvisita: LiveData<Event<List<TVisita>>> = _servvisita

    private val _servalta: MutableLiveData<Event<List<TAlta>>> = MutableLiveData()
    val servalta: LiveData<Event<List<TAlta>>> = _servalta

    private val _servaltadatos: MutableLiveData<Event<List<TADatos>>> = MutableLiveData()
    val servaltadatos: LiveData<Event<List<TADatos>>> = _servaltadatos

    private val _servbaja: MutableLiveData<Event<List<TBaja>>> = MutableLiveData()
    val servbaja: LiveData<Event<List<TBaja>>> = _servbaja

    private val _servbajaestado: MutableLiveData<Event<List<TBEstado>>> = MutableLiveData()
    val servbajaestado: LiveData<Event<List<TBEstado>>> = _servbajaestado

    private val _servrespuesta: MutableLiveData<Event<List<TRespuesta>>> = MutableLiveData()
    val servrespuesta: LiveData<Event<List<TRespuesta>>> = _servrespuesta

    private val _servfoto: MutableLiveData<Event<List<TRespuesta>>> = MutableLiveData()
    val servfoto: LiveData<Event<List<TRespuesta>>> = _servfoto

    private val _respseguimiento: MutableLiveData<Event<Network<JObj>>> = MutableLiveData()
    val respseguimiento: LiveData<Event<Network<JObj>>> = _respseguimiento

    private val _respvisita: MutableLiveData<Event<Network<JObj>>> = MutableLiveData()
    val respvisita: LiveData<Event<Network<JObj>>> = _respvisita

    private val _respalta: MutableLiveData<Event<Network<JObj>>> = MutableLiveData()
    val respalta: LiveData<Event<Network<JObj>>> = _respalta

    private val _respaltadatos: MutableLiveData<Event<Network<JObj>>> = MutableLiveData()
    val respaltadatos: LiveData<Event<Network<JObj>>> = _respaltadatos

    private val _respbaja: MutableLiveData<Event<Network<JObj>>> = MutableLiveData()
    val respbaja: LiveData<Event<Network<JObj>>> = _respbaja

    private val _respbajaestado: MutableLiveData<Event<Network<JObj>>> = MutableLiveData()
    val respbajaestado: LiveData<Event<Network<JObj>>> = _respbajaestado

    private val _resprespuesta: MutableLiveData<Event<Network<JObj>>> = MutableLiveData()
    val resprespuesta: LiveData<Event<Network<JObj>>> = _resprespuesta

    private val _respfoto: MutableLiveData<Event<Network<JObj>>> = MutableLiveData()
    val respfoto: LiveData<Event<Network<JObj>>> = _respfoto

    private val _cabecera: MutableLiveData<Event<List<Cabecera>>> = MutableLiveData()
    val cabecera: LiveData<Event<List<Cabecera>>> = _cabecera

    private val _respuesta: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val respuesta: LiveData<Event<Boolean>> = _respuesta

    fun markerMap() {
        viewModelScope.launch {
            repository.getFlowMarker().collect {
                _marker.value = Event(it)
            }
        }
    }

    fun fetchServerAll(estado: String) {
        viewModelScope.launch {
            repository.getServerSeguimiento(estado).let {
                _servseguimiento.value = Event(it)
            }
            repository.getServerVisita(estado).let {
                _servvisita.value = Event(it)
            }
            repository.getServerAlta(estado).let {
                _servalta.value = Event(it)
            }
            repository.getServerAltadatos(estado).let {
                _servaltadatos.value = Event(it)
            }
            repository.getServerBaja(estado).let {
                _servbaja.value = Event(it)
            }
            repository.getServerBajaestado(estado).let {
                _servbajaestado.value = Event(it)
            }
            repository.getServerRespuesta(estado).let {
                _servrespuesta.value = Event(it)
            }
            repository.getServerFoto(estado).let {
                _servfoto.value = Event(it)
            }
        }
    }

    fun webSeguimiento(body: RequestBody) = viewModelScope.launch {
        repository.setWebSeguimiento(body).collect {
            _respseguimiento.value = Event(it)
        }
    }

    fun webVisita(body: RequestBody) = viewModelScope.launch {
        repository.setWebVisita(body).collect {
            _respvisita.value = Event(it)
        }
    }

    fun webAlta(body: RequestBody) = viewModelScope.launch {
        repository.setWebAlta(body).collect {
            _respalta.value = Event(it)
        }
    }

    fun webAltaDatos(body: RequestBody) = viewModelScope.launch {
        repository.setWebAltaDatos(body).collect {
            _respaltadatos.value = Event(it)
        }
    }

    fun webBaja(body: RequestBody) = viewModelScope.launch {
        repository.setWebBaja(body).collect {
            _respbaja.value = Event(it)
        }
    }

    fun webBajaEstado(body: RequestBody) = viewModelScope.launch {
        repository.setWebBajaEstados(body).collect {
            _respbajaestado.value = Event(it)
        }
    }

    fun webRespuesta(body: RequestBody) = viewModelScope.launch {
        repository.setWebRespuestas(body).collect {
            _resprespuesta.value = Event(it)
        }
    }

    fun webFoto(body: RequestBody) = viewModelScope.launch {
        repository.setWebFotos(body).collect {
            _respfoto.value = Event(it)
        }
    }

    fun fetchRegisterDevice(body: RequestBody) = viewModelScope.launch {
        repository.registerWebDevice(body).collect {
            _register.value = Event(it)
        }
    }

    fun fetchLoginAdmin(body: RequestBody) = viewModelScope.launch {
        repository.loginAdministrator(body).collect {
            _login.value = Event(it)
        }
    }

    fun fetchClientes(body: RequestBody) = viewModelScope.launch {
        repository.getWebClientes(body).collect { values ->
            values.data?.jobl?.let { repository.saveClientes(it) }
            _cliente.value = Event(values)
        }
    }

    fun fetchEncuesta(body: RequestBody) = viewModelScope.launch {
        repository.getWebEncuesta(body).collect { values ->
            values.data?.jobl?.let {
                repository.saveEncuesta(it)
                if (CONF.tipo == "V") {
                    val item = TEncuestaSeleccionado(1,it[0].id,it[0].foto)
                    repository.saveSeleccionado(item)
                }
            }
            _encuesta.value = Event(values)
        }
    }

    fun fetchPreventa(body: RequestBody) = viewModelScope.launch {
        repository.getWebPreventa(body).collect {
            _preventa.value = Event(it)
        }
    }

    fun fetchCobertura(body: RequestBody) = viewModelScope.launch {
        repository.getWebCobertura(body).collect {
            _cobertura.value = Event(it)
        }
    }

    fun fetchCartera(body: RequestBody) = viewModelScope.launch {
        repository.getWebCartera(body).collect {
            _cartera.value = Event(it)
        }
    }

    fun fetchPedidos(body: RequestBody) = viewModelScope.launch {
        repository.getWebPedidos(body).collect {
            _pedidos.value = Event(it)
        }
    }

    fun fetchVisicooler(body: RequestBody) = viewModelScope.launch {
        repository.getWebVisicooler(body).collect {
            _visicooler.value = Event(it)
        }
    }

    fun fetchVisisuper(body: RequestBody) = viewModelScope.launch {
        repository.getWebVisisuper(body).collect {
            _visisuper.value = Event(it)
        }
    }

    fun fetchCambiosCliente(body: RequestBody) = viewModelScope.launch {
        repository.getWebCambiosCli(body).collect {
            _cambiocli.value = Event(it)
        }
    }

    fun fetchCambiosEmpleado(body: RequestBody) = viewModelScope.launch {
        repository.getWebCambiosEmp(body).collect {
            _cambioemp.value = Event(it)
        }
    }

    fun fetchUmes(body: RequestBody) = viewModelScope.launch {
        repository.getWebUmes(body).collect {
            _umes.value = Event(it)
        }
    }

    fun fetchSoles(body: RequestBody) = viewModelScope.launch {
        repository.getWebSoles(body).collect {
            _soles.value = Event(it)
        }
    }

    fun fetchUmesGenerico(body: RequestBody) = viewModelScope.launch {
        repository.getWebUmesGenerico(body).collect {
            _generico.value = Event(it)
        }
    }

    fun fetchSolesGenerico(body: RequestBody) = viewModelScope.launch {
        repository.getWebSolesGenerico(body).collect {
            _generico.value = Event(it)
        }
    }

    fun fetchUmeDetalle(body: RequestBody) = viewModelScope.launch {
        repository.getWebUmesDetalle(body).collect {
            _detalle.value = it
        }
    }

    fun fetchSolesDetalle(body: RequestBody) = viewModelScope.launch {
        repository.getWebSolesDetalle(body).collect {
            _detalle.value = it
        }
    }

    fun fetchCoberturaPendiente(body: RequestBody) = viewModelScope.launch {
        repository.getWebCoberturaPendiente(body).collect {
            _cobpendiente.value = it
        }
    }

    fun fetchPediGen(body: RequestBody) = viewModelScope.launch {
        repository.getWebPedidosRealizados(body).collect {
            _pedigen.value = it
        }
    }

    fun fetchPedimap(body: RequestBody) = viewModelScope.launch {
        repository.getWebPedimap(body).collect {
            _pedimap.value = Event(it)
        }
    }

    fun fetchBajaVendedor(body: RequestBody) = viewModelScope.launch {
        repository.getWebBajaVendedor(body).collect {
            _bajaven.value = Event(it)
        }
    }

    fun fetchBajaSupervisor(body: RequestBody) = viewModelScope.launch {
        repository.getWebBajaSupervisor(body).collect { values ->
            values.data?.jobl?.let { repository.saveBajaSuper(it) }
            _bajasuper.value = Event(values)
        }
    }

    fun fetchAltaDatos(alta: String) = viewModelScope.launch {
        repository.getAltaDatoSpecific(alta).let {
            _altadatos.value = it
        }
    }

    fun getPreguntas() = viewModelScope.launch {
        repository.getPreguntas().let {
            _preguntas.value = Event(it)
        }
    }

    suspend fun isConfigEmpty(): Boolean =
        repository.getConfig() == null

    suspend fun isClienteBaja(cliente: String): Boolean =
        repository.isClienteBaja(cliente)

    suspend fun gettingVendedores() =
        repository.getEmpleados()

    fun setFecha(fecha: String) {
        _fecha.value = Event(fecha)
    }

    fun setFiltro(filtro: Int) {
        _filtro.value = Event(filtro)
    }

    fun setClienteSelect(cliente: String) {
        _climap.value = Event(cliente)
    }

    fun setVendedorSelect(list: List<String>) {
        _vendedor.value = Event(list)
    }

    fun getClientDet(cliente: String) {
        viewModelScope.launch {
            val result = repository.getClienteDetail(cliente)
            _detail.value = Event(result)
        }
    }

    fun getAltaData(alta: String) {
        viewModelScope.launch {
            val result = repository.getDataAlta(alta)
            _altamark.value = Event(result)
        }
    }

    fun getBajaSuperSpecific(codigo: String, fecha: String) {
        viewModelScope.launch {
            val result = repository.getBajaSuperSpecific(codigo, fecha)
            _bajasuperspecif.value = Event(result)
        }
    }

    fun setupApp(T: () -> Unit) {
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

    fun launchPosition() {
        functions.executeService("position", false)
    }

    fun launchSetup() {
        functions.executeService("setup", false)
    }

    fun fecha(opt: Int) =
        functions.dateToday(opt)

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
            repository.processAlta(functions.dateToday(4), location)
        }
    }

    fun saveAltaDatos(datos: TADatos) {
        viewModelScope.launch {
            val mini = MiniUpdAlta(datos.idaux, 1)
            repository.saveAltaDatos(datos)
            repository.updateMiniAlta(mini)
        }
    }

    fun updateLocationAlta(m: Marker) {
        viewModelScope.launch {
            val item = LocationAlta(
                m.snippet!!.toInt(),
                functions.dateToday(4),
                m.position.longitude,
                m.position.latitude,
                10.0,
                "Pendiente"
            )
            repository.updateLocationAlta(item)
        }
    }

    fun updateBaja(datos: MiniUpdBaja) {
        viewModelScope.launch {
            repository.updateMiniBaja(datos)
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

    fun updSeguimiento(it: TSeguimiento) {
        viewModelScope.launch {
            repository.saveSeguimiento(it)
        }
    }

    fun updVisita(it: TVisita) {
        viewModelScope.launch {
            repository.saveVisita(it)
        }
    }

    fun updAlta(it: TAlta) {
        viewModelScope.launch {
            repository.saveAlta(it)
        }
    }

    fun updAltaDatos(it: TADatos) {
        viewModelScope.launch {
            repository.saveAltaDatos(it)
        }
    }

    fun updBaja(it: TBaja) {
        viewModelScope.launch {
            repository.saveBaja(it)
        }
    }

    fun updBajaEstado(it: TBEstado) {
        viewModelScope.launch {
            repository.saveBajaEstado(it)
        }
    }

    fun updRespuesta(it: TRespuesta) {
        viewModelScope.launch {
            repository.saveRespuestaOneByOne(it)
        }
    }

    fun updFoto(it: TRespuesta) {
        viewModelScope.launch {
            repository.saveFoto(it)
        }
    }

    private fun intoHours() {
        viewModelScope.launch {
            repository.getConfig().let {
                if (it != null) {
                    val hora = functions.dateToday(3).replace(":", "").toInt()
                    val inicio = it.hini.replace(":", "").toInt()
                    val fin = it.hfin.replace(":", "").toInt()
                    _checking.value = hora in inicio..fin
                } else {
                    _checking.value = true
                }
            }
        }
    }

    fun dataDownloaded() {
        viewModelScope.launch {
            repository.getConfig().let {
                if (it != null) {
                    val user = when (it.tipo) {
                        "V" -> repository.getClientes()
                        else -> repository.getEmpleados()
                    }
                    val dist = repository.getDistritos()
                    val neg = repository.getNegocios()

                    val userb = !user.isNullOrEmpty()
                    val distb = !dist.isNullOrEmpty()
                    val negb = !neg.isNullOrEmpty()

                    _inicio.value = Event(userb && distb && negb)
                } else {
                    _inicio.value = Event(false)
                }
            }
        }
    }

    fun internetAvailable() =
        functions.isConnected()

    fun fetchSinchro() {
        viewModelScope.launch {
            val conf = repository.getConfig()
            if (conf == null) {
                _sincro.value = Event(90)
            } else {
                val j = JSONObject()
                j.put("empleado",conf.codigo)
                j.put("empresa",conf.empresa)

                val k = JSONObject()
                k.put("empresa",conf.empresa)

                if (conf.tipo == "V")
                    repository.getWebClientes(j.toReqBody()).collect {
                        it.data?.jobl?.let { y ->
                            repository.saveClientes(y)
                        }
                        _sincro.value = Event(1)
                    }
                else
                    repository.getWebEmpleados(j.toReqBody()).collect {
                        it.data?.jobl?.let { y ->
                            repository.saveEmpleados(y)
                        }
                        _sincro.value = Event(1)
                    }

                repository.getWebNegocios(k.toReqBody()).collect {
                    it.data?.jobl?.let { y ->
                        repository.saveNegocios(y)
                    }
                    _sincro.value = Event(1)
                }
                repository.getWebDistritos(k.toReqBody()).collect {
                    it.data?.jobl?.let { y ->
                        repository.saveDistritos(y)
                    }
                    _sincro.value = Event(1)
                }
                repository.getWebRutas(j.toReqBody()).collect {
                    it.data?.jobl?.let { y ->
                        repository.saveRutas(y)
                    }
                    _sincro.value = Event(1)
                }
            }
        }
    }

    fun checkingEncuesta(T: (Boolean) -> Unit) {
        viewModelScope.launch {
            val lista = repository.getListEncuestas()
            val seleccionado = repository.getSeleccionado() != null
            when {
                lista.isNullOrEmpty() -> T(true)
                seleccionado -> T(true)
                else -> T(false)
            }
        }
    }

    fun gettingEncuestaLista() {
        viewModelScope.launch {
            val list = repository.getListEncuestas()
            _cabecera.value = Event(list)
        }
    }

    fun savingRespuestas(list: List<TRespuesta>) {
        viewModelScope.launch {
            repository.saveRespuesta(list)
        }
    }

    fun clienteRespondio(cliente: String) {
        viewModelScope.launch {
            val rsp = repository.clienteRespondio(cliente)
            _respuesta.value = Event(rsp)
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
            repository.deleteSeleccionado()
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
        }
    }
}