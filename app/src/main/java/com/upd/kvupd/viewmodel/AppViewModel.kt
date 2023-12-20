package com.upd.kvupd.viewmodel

import android.graphics.Bitmap
import android.location.Location
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.upd.kvupd.data.model.Cabecera
import com.upd.kvupd.data.model.DataAlta
import com.upd.kvupd.data.model.DataCliente
import com.upd.kvupd.data.model.JBajaSupervisor
import com.upd.kvupd.data.model.JBajaVendedor
import com.upd.kvupd.data.model.JCambio
import com.upd.kvupd.data.model.JCliente
import com.upd.kvupd.data.model.JCobCart
import com.upd.kvupd.data.model.JCoberturados
import com.upd.kvupd.data.model.JConsulta
import com.upd.kvupd.data.model.JDetCob
import com.upd.kvupd.data.model.JEncuesta
import com.upd.kvupd.data.model.JFoto
import com.upd.kvupd.data.model.JGenerico
import com.upd.kvupd.data.model.JObj
import com.upd.kvupd.data.model.JPediGen
import com.upd.kvupd.data.model.JPedido
import com.upd.kvupd.data.model.JPedimap
import com.upd.kvupd.data.model.JRuta
import com.upd.kvupd.data.model.JSoles
import com.upd.kvupd.data.model.JUmes
import com.upd.kvupd.data.model.JVisicooler
import com.upd.kvupd.data.model.JVisisuper
import com.upd.kvupd.data.model.JVolumen
import com.upd.kvupd.data.model.LocationAlta
import com.upd.kvupd.data.model.Login
import com.upd.kvupd.data.model.MarkerMap
import com.upd.kvupd.data.model.MiniUpdAlta
import com.upd.kvupd.data.model.MiniUpdBaja
import com.upd.kvupd.data.model.Pedimap
import com.upd.kvupd.data.model.TADatos
import com.upd.kvupd.data.model.TAFoto
import com.upd.kvupd.data.model.TAlta
import com.upd.kvupd.data.model.TBEstado
import com.upd.kvupd.data.model.TBaja
import com.upd.kvupd.data.model.TBajaSuper
import com.upd.kvupd.data.model.TClientes
import com.upd.kvupd.data.model.TConsulta
import com.upd.kvupd.data.model.TEncuesta
import com.upd.kvupd.data.model.TEncuestaSeleccionado
import com.upd.kvupd.data.model.TRespuesta
import com.upd.kvupd.data.model.TSeguimiento
import com.upd.kvupd.data.model.TVisita
import com.upd.kvupd.data.model.asTEstado
import com.upd.kvupd.domain.Functions
import com.upd.kvupd.domain.Repository
import com.upd.kvupd.utils.Constant.CONF
import com.upd.kvupd.utils.Event
import com.upd.kvupd.utils.NetworkRetrofit
import com.upd.kvupd.utils.dateToday
import com.upd.kvupd.utils.toReqBody
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.Calendar

class AppViewModel @ViewModelInject constructor(
    private val repository: Repository,
    private val functions: Functions
) : ViewModel() {

    private val _tag by lazy { AppViewModel::class.java.simpleName }

    //  MutableLiveData with Event trigger only once
    private val _ipaux: MutableLiveData<Event<String>> = MutableLiveData()
    val ipaux: LiveData<Event<String>> = _ipaux

    private val _sincro: MutableLiveData<Event<Int>> = MutableLiveData()
    val sincro: LiveData<Event<Int>> = _sincro

    private val _inicio: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val inicio: LiveData<Event<Boolean>> = _inicio

    private val _checking: MutableLiveData<Boolean> = MutableLiveData()
    val checking: LiveData<Boolean> = _checking

    private val _filtro: MutableLiveData<Event<Int>> = MutableLiveData()
    val filtro: LiveData<Event<Int>> = _filtro

    private val _filtromark: MutableLiveData<Event<Int>> = MutableLiveData()
    val filtromark: LiveData<Event<Int>> = _filtromark

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

    private val _altamark: MutableLiveData<Event<DataAlta>> = MutableLiveData()
    val altamark: LiveData<Event<DataAlta>> = _altamark

    private val _bajasuperspecif: MutableLiveData<Event<TBajaSuper>> = MutableLiveData()
    val bajasuperspecif: LiveData<Event<TBajaSuper>> = _bajasuperspecif

    private val _register: MutableLiveData<Event<NetworkRetrofit<JObj>>> = MutableLiveData()
    val register: LiveData<Event<NetworkRetrofit<JObj>>> = _register

    private val _login: MutableLiveData<Event<NetworkRetrofit<Login>>> = MutableLiveData()
    val login: LiveData<Event<NetworkRetrofit<Login>>> = _login

    private val _cliente: MutableLiveData<Event<NetworkRetrofit<JCliente>>> = MutableLiveData()
    val cliente: LiveData<Event<NetworkRetrofit<JCliente>>> = _cliente

    private val _rutas: MutableLiveData<Event<NetworkRetrofit<JRuta>>> = MutableLiveData()
    val rutas: LiveData<Event<NetworkRetrofit<JRuta>>> = _rutas

    private val _encuesta: MutableLiveData<Event<NetworkRetrofit<JEncuesta>>> = MutableLiveData()
    val encuesta: LiveData<Event<NetworkRetrofit<JEncuesta>>> = _encuesta

    private val _consulta: MutableLiveData<Event<NetworkRetrofit<JConsulta>>> = MutableLiveData()
    val consulta: LiveData<Event<NetworkRetrofit<JConsulta>>> = _consulta

    private val _preventa: MutableLiveData<Event<NetworkRetrofit<JVolumen>>> = MutableLiveData()
    val preventa: LiveData<Event<NetworkRetrofit<JVolumen>>> = _preventa

    private val _cobertura: MutableLiveData<Event<NetworkRetrofit<JCobCart>>> = MutableLiveData()
    val cobertura: LiveData<Event<NetworkRetrofit<JCobCart>>> = _cobertura

    private val _cartera: MutableLiveData<Event<NetworkRetrofit<JCobCart>>> = MutableLiveData()
    val cartera: LiveData<Event<NetworkRetrofit<JCobCart>>> = _cartera

    private val _pedidos: MutableLiveData<Event<NetworkRetrofit<JPedido>>> = MutableLiveData()
    val pedidos: LiveData<Event<NetworkRetrofit<JPedido>>> = _pedidos

    private val _visicooler: MutableLiveData<Event<NetworkRetrofit<JVisicooler>>> =
        MutableLiveData()
    val visicooler: LiveData<Event<NetworkRetrofit<JVisicooler>>> = _visicooler

    private val _visisuper: MutableLiveData<Event<NetworkRetrofit<JVisisuper>>> = MutableLiveData()
    val visisuper: LiveData<Event<NetworkRetrofit<JVisisuper>>> = _visisuper

    private val _cambios: MutableLiveData<Event<NetworkRetrofit<JCambio>>> = MutableLiveData()
    val cambios: LiveData<Event<NetworkRetrofit<JCambio>>> = _cambios

    private val _umes: MutableLiveData<Event<NetworkRetrofit<JUmes>>> = MutableLiveData()
    val umes: LiveData<Event<NetworkRetrofit<JUmes>>> = _umes

    private val _soles: MutableLiveData<Event<NetworkRetrofit<JSoles>>> = MutableLiveData()
    val soles: LiveData<Event<NetworkRetrofit<JSoles>>> = _soles

    private val _generico: MutableLiveData<Event<NetworkRetrofit<JGenerico>>> = MutableLiveData()
    val generico: LiveData<Event<NetworkRetrofit<JGenerico>>> = _generico

    private val _detcob: MutableLiveData<Event<NetworkRetrofit<JDetCob>>> = MutableLiveData()
    val detcob: LiveData<Event<NetworkRetrofit<JDetCob>>> = _detcob

    private val _detalle: MutableLiveData<Event<NetworkRetrofit<JGenerico>>> = MutableLiveData()
    val detalle: LiveData<Event<NetworkRetrofit<JGenerico>>> = _detalle

    private val _cobpendiente: MutableLiveData<Event<NetworkRetrofit<JCoberturados>>> =
        MutableLiveData()
    val cobpendiente: LiveData<Event<NetworkRetrofit<JCoberturados>>> = _cobpendiente

    private val _pedigen: MutableLiveData<Event<NetworkRetrofit<JPediGen>>> = MutableLiveData()
    val pedigen: LiveData<Event<NetworkRetrofit<JPediGen>>> = _pedigen

    private val _pedimap: MutableLiveData<Event<NetworkRetrofit<JPedimap>>> = MutableLiveData()
    val pedimap: LiveData<Event<NetworkRetrofit<JPedimap>>> = _pedimap

    private val _bajasuper: MutableLiveData<Event<NetworkRetrofit<JBajaSupervisor>>> =
        MutableLiveData()
    val bajasuper: LiveData<Event<NetworkRetrofit<JBajaSupervisor>>> = _bajasuper

    private val _bajaven: MutableLiveData<Event<NetworkRetrofit<JBajaVendedor>>> = MutableLiveData()
    val bajavend: LiveData<Event<NetworkRetrofit<JBajaVendedor>>> = _bajaven

    private val _altadatos: MutableLiveData<Event<TADatos?>> = MutableLiveData()
    val altadatos: LiveData<Event<TADatos?>> = _altadatos

    private val _preguntas: MutableLiveData<Event<List<TEncuesta>>> = MutableLiveData()
    val preguntas: LiveData<Event<List<TEncuesta>>> = _preguntas

    private val _consultado: MutableLiveData<Event<List<TConsulta>>> = MutableLiveData()
    val consultado: LiveData<Event<List<TConsulta>>> = _consultado

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

    private val _servdni: MutableLiveData<Event<List<TAFoto>>> = MutableLiveData()
    val servdni: LiveData<Event<List<TAFoto>>> = _servdni

    private val _respseguimiento: MutableLiveData<Event<NetworkRetrofit<JObj>>> = MutableLiveData()
    val respseguimiento: LiveData<Event<NetworkRetrofit<JObj>>> = _respseguimiento

    private val _respvisita: MutableLiveData<Event<NetworkRetrofit<JObj>>> = MutableLiveData()
    val respvisita: LiveData<Event<NetworkRetrofit<JObj>>> = _respvisita

    private val _respalta: MutableLiveData<Event<NetworkRetrofit<JObj>>> = MutableLiveData()
    val respalta: LiveData<Event<NetworkRetrofit<JObj>>> = _respalta

    private val _respaltadatos: MutableLiveData<Event<NetworkRetrofit<JObj>>> = MutableLiveData()
    val respaltadatos: LiveData<Event<NetworkRetrofit<JObj>>> = _respaltadatos

    private val _respbaja: MutableLiveData<Event<NetworkRetrofit<JObj>>> = MutableLiveData()
    val respbaja: LiveData<Event<NetworkRetrofit<JObj>>> = _respbaja

    private val _respbajaestado: MutableLiveData<Event<NetworkRetrofit<JObj>>> = MutableLiveData()
    val respbajaestado: LiveData<Event<NetworkRetrofit<JObj>>> = _respbajaestado

    private val _resprespuesta: MutableLiveData<Event<NetworkRetrofit<JObj>>> = MutableLiveData()
    val resprespuesta: LiveData<Event<NetworkRetrofit<JObj>>> = _resprespuesta

    private val _respfoto: MutableLiveData<Event<NetworkRetrofit<JFoto>>> = MutableLiveData()
    val respfoto: LiveData<Event<NetworkRetrofit<JFoto>>> = _respfoto

    private val _respdni: MutableLiveData<Event<NetworkRetrofit<JFoto>>> = MutableLiveData()
    val respdni: LiveData<Event<NetworkRetrofit<JFoto>>> = _respdni

    private val _cabecera: MutableLiveData<Event<List<Cabecera>>> = MutableLiveData()
    val cabecera: LiveData<Event<List<Cabecera>>> = _cabecera

    private val _respuesta: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val respuesta: LiveData<Event<Boolean>> = _respuesta

    fun markerMap(observacion: Int) {
        viewModelScope.launch {
            repository.getFlowMarker(observacion.toString()).collect {
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
            repository.getServerAltaFoto(estado).let {
                _servdni.value = Event(it)
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

    fun webDNI(body: RequestBody) = viewModelScope.launch {
        repository.setWebFotos(body).collect {
            _respdni.value = Event(it)
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

    fun fetchRutas(body: RequestBody) = viewModelScope.launch {
        repository.getWebRutas(body).collect() { values ->
            values.data?.jobl?.let { repository.saveRutas(it) }
            _rutas.value = Event(values)
        }
    }

    fun fetchEncuesta(body: RequestBody) = viewModelScope.launch {
        repository.getWebEncuesta(body).collect { values ->
            values.data?.jobl.let {
                if (!it.isNullOrEmpty()) {
                    repository.saveEncuesta(it)
                    if (CONF.tipo == "V") {
                        val item = TEncuestaSeleccionado(1, it[0].id, it[0].foto)
                        repository.saveSeleccionado(item)
                    }
                }
            }
            _encuesta.value = Event(values)
        }
    }

    fun fetchConsulta(body: RequestBody) = viewModelScope.launch {
        repository.getWebConsulta(body).collect { values ->
            values.data?.jobl?.let { repository.saveConsulta(it) }
            _consulta.value = Event(values)
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

    fun fetchCambios(body: RequestBody) = viewModelScope.launch {
        if (CONF.tipo == "S") {
            repository.getWebCambiosEmp(body).collect {
                _cambios.value = Event(it)
            }
        } else {
            repository.getWebCambiosCli(body).collect {
                _cambios.value = Event(it)
            }
        }
    }

    fun getClienteConsultado(numero: String, nombre: String) = viewModelScope.launch {
        repository.getConsultaCliente(numero, nombre).let {
            _consultado.value = Event(it)
        }
    }

    /*fun fetchCambiosEmpleado(body: RequestBody) = viewModelScope.launch {
        repository.getWebCambiosEmp(body).collect {
            _cambioemp.value = Event(it)
        }
    }*/

    /*fun fetchUmes(body: RequestBody) = viewModelScope.launch {
        repository.getWebUmes(body).collect {
            _umes.value = Event(it)
        }
    }*/

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

    /*fun fetchUmeDetalle(body: RequestBody) = viewModelScope.launch {
        repository.getWebUmesDetalle(body).collect {
            _detalle.value = Event(it)
        }
    }*/

    fun fetchDetCobertura(body: RequestBody) = viewModelScope.launch {
        repository.getWebCoberturaDetalle(body).collect {
            _detcob.value = Event(it)
        }
    }

    fun fetchSolesDetalle(body: RequestBody) = viewModelScope.launch {
        repository.getWebSolesDetalle(body).collect {
            _detalle.value = Event(it)
        }
    }

    fun fetchCoberturaPendiente(body: RequestBody) = viewModelScope.launch {
        repository.getWebCoberturaPendiente(body).collect {
            _cobpendiente.value = Event(it)
        }
    }

    fun fetchPediGen(body: RequestBody) = viewModelScope.launch {
        repository.getWebPedidosRealizados(body).collect {
            _pedigen.value = Event(it)
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
            _altadatos.value = Event(it)
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

    fun filterMarkerObs(filtro: Int) {
        _filtromark.value = Event(filtro)
    }

    fun setClienteSelect(cliente: String) {
        _climap.value = Event(cliente)
    }

    fun setVendedorSelect(list: List<String>) {
        _vendedor.value = Event(list)
    }

    fun getClientDet(cliente: String, observacion: Int) {
        viewModelScope.launch {
            val result = repository.getClienteDetail(cliente, observacion.toString())
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
            functions.addIPtoQRIMEI()
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
            repository.updateSeguimiento(it)
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

    fun savingDNI(it: TAFoto) {
        viewModelScope.launch {
            repository.saveAltaFoto(it)
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
                        "V" -> repository.getClientes()
                        else -> repository.getEmpleados()
                    }
                    val dist = repository.getDistritos()
                    val neg = repository.getNegocios()

                    val userb = user.isNotEmpty()
                    val distb = dist.isNotEmpty()
                    val negb = neg.isNotEmpty()

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
                j.put("empleado", conf.codigo)
                j.put("empresa", conf.empresa)

                val k = JSONObject()
                k.put("empresa", conf.empresa)

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

    fun settingIPaux(ip: String) {
        _ipaux.value = Event(ip)
    }

    fun savingRespuestas(list: List<TRespuesta>) {
        viewModelScope.launch {
            repository.saveRespuesta(list)
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
            _respuesta.value = Event(respuesta)
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
            repository.deleteIncidencia()
            repository.deleteAAux()
        }
    }

    fun cleanDataVendedor() {
        viewModelScope.launch {
            repository.deleteClientes()
            repository.deleteRutas()
        }
    }
}