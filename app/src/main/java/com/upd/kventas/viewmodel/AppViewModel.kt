package com.upd.kventas.viewmodel

import android.graphics.Bitmap
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.gms.maps.GoogleMap
import com.upd.kventas.application.ToastHelper
import com.upd.kventas.data.model.*
import com.upd.kventas.domain.Functions
import com.upd.kventas.domain.Repository
import com.upd.kventas.utils.Event
import com.upd.kventas.utils.Network
import kotlinx.coroutines.launch
import okhttp3.RequestBody

class AppViewModel @ViewModelInject constructor(
    private val repository: Repository,
    private val functions: Functions,
    private val toastHelper: ToastHelper
) : ViewModel() {

    private val _tag by lazy { AppViewModel::class.java.simpleName }

    //  MutableLiveData with Event trigger only once
    private val _fecha: MutableLiveData<Event<String>> = MutableLiveData()
    val fecha: LiveData<Event<String>> = _fecha

    private val _climap: MutableLiveData<Event<String>> = MutableLiveData()
    val climap: LiveData<Event<String>> = _climap

    private val _detail: MutableLiveData<Event<List<DataCliente>>> = MutableLiveData()
    val detail: LiveData<Event<List<DataCliente>>> = _detail

    private val _register: MutableLiveData<Event<Network<JObj>>> = MutableLiveData()
    val register: LiveData<Event<Network<JObj>>> = _register

    private val _login: MutableLiveData<Event<Network<Login>>> = MutableLiveData()
    val login: LiveData<Event<Network<Login>>> = _login

    private val _cliente: MutableLiveData<Event<Network<JCliente>>> = MutableLiveData()
    val cliente: LiveData<Event<Network<JCliente>>> = _cliente

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

    fun configObserver() = repository.getFlowConfig().asLiveData()

    fun rowClienteObs() = repository.getFlowRowCliente().asLiveData()

    fun lastLocation() = repository.getFlowLocation().asLiveData()

    fun markerMap() = repository.getFlowMarker().asLiveData()

    fun fetchRegisterDevice(body: RequestBody) = viewModelScope.launch {
        repository.registerWebDevice(body).collect { values ->
            _register.value = Event(values)
        }
    }

    fun fetchLoginAdmin(body: RequestBody) = viewModelScope.launch {
        repository.loginAdministrator(body).collect { values ->
            _login.value = Event(values)
        }
    }

    fun fetchClientes(body: RequestBody) = viewModelScope.launch {
        repository.getWebClientes(body).collect { values ->
            values.data?.jobl?.let { repository.saveClientes(it) }
            _cliente.value = Event(values)
        }
    }

    fun fetchPreventa(body: RequestBody) = viewModelScope.launch {
        repository.getWebPreventa(body).collect { values ->
            _preventa.value = Event(values)
        }
    }

    fun fetchCobertura(body: RequestBody) = viewModelScope.launch {
        repository.getWebCobertura(body).collect { values ->
            _cobertura.value = Event(values)
        }
    }

    fun fetchCartera(body: RequestBody) = viewModelScope.launch {
        repository.getWebCartera(body).collect { values ->
            _cartera.value = Event(values)
        }
    }

    fun fetchPedidos(body: RequestBody) = viewModelScope.launch {
        repository.getWebPedidos(body).collect { values ->
            _pedidos.value = Event(values)
        }
    }

    fun fetchVisicooler(body: RequestBody) = viewModelScope.launch {
        repository.getWebVisicooler(body).collect { values ->
            _visicooler.value = Event(values)
        }
    }

    fun fetchVisisuper(body: RequestBody) = viewModelScope.launch {
        repository.getWebVisisuper(body).collect { values ->
            _visisuper.value = Event(values)
        }
    }

    fun fetchCambiosCliente(body: RequestBody) = viewModelScope.launch {
        repository.getWebCambiosCli(body).collect { values ->
            _cambiocli.value = Event(values)
        }
    }

    fun fetchCambiosEmpleado(body: RequestBody) = viewModelScope.launch {
        repository.getWebCambiosEmp(body).collect { values ->
            _cambioemp.value = Event(values)
        }
    }

    fun fetchUmes(body: RequestBody) = viewModelScope.launch {
        repository.getWebUmes(body).collect { values ->
            _umes.value = Event(values)
        }
    }

    fun fetchSoles(body: RequestBody) = viewModelScope.launch {
        repository.getWebSoles(body).collect { values ->
            _soles.value = Event(values)
        }
    }

    fun fetchUmesGenerico(body: RequestBody) = viewModelScope.launch {
        repository.getWebUmesGenerico(body).collect { values ->
            _generico.value = Event(values)
        }
    }

    fun fetchSolesGenerico(body: RequestBody) = viewModelScope.launch {
        repository.getWebSolesGenerico(body).collect { values ->
            _generico.value = Event(values)
        }
    }

    fun fetchUmeDetalle(body: RequestBody) = viewModelScope.launch {
        repository.getWebUmesDetalle(body).collect { values ->
            _detalle.value = values
        }
    }

    fun fetchSolesDetalle(body: RequestBody) = viewModelScope.launch {
        repository.getWebSolesDetalle(body).collect { values ->
            _detalle.value = values
        }
    }

    fun fetchCoberturaPendiente(body: RequestBody) = viewModelScope.launch {
        repository.getWebCoberturaPendiente(body).collect { values ->
            _cobpendiente.value = values
        }
    }

    fun fetchPediGen(body: RequestBody) = viewModelScope.launch {
        repository.getWebPedidosRealizados(body).collect { values ->
            _pedigen.value = values
        }
    }

    suspend fun isConfigEmpty(): Boolean =
        repository.getConfig().isNullOrEmpty()

    suspend fun isClienteBaja(cliente: String): Boolean =
        repository.isClienteBaja(cliente)

    fun setFecha(fecha: String) {
        _fecha.value = Event(fecha)
    }

    fun setClienteSelect(cliente: String) {
        _climap.value = Event(cliente)
    }

    fun getClientDet(cliente: String) {
        viewModelScope.launch {
            val result = repository.getClienteDetail(cliente)
            _detail.value = Event(result)
        }
    }

    fun setupApp(T: () -> Unit) {
        if (functions.existQR()) {
            functions.executeService("setup", true)
        } else {
            T()
        }
    }

    fun generateAndSaveQR(value: String): Bitmap {
        val qr = functions.generateQR(value)
        functions.saveQR(qr)
        return qr
    }

    //  change to suspend
    fun workDay(E: () -> Unit, S: () -> Unit) {
        viewModelScope.launch {
            repository.workDay()?.let {
                if (it) {
                    E()
                } else {
                    repository.deleteClientes()
                    repository.deleteEmpleados()
                    repository.deleteDistritos()
                    repository.deleteNegocios()
                    S()
                }
            }
        }
    }

    fun getIMEI(add: Boolean = false) =
        functions.parseQRtoIMEI(add)

    fun getQR(value: String) =
        functions.generateQR(value)

    fun setMarker(map: GoogleMap, list: List<MarkerMap>) =
        functions.setupMarkers(map, list)

    fun launchPosition() {
        functions.executeService("position", false)
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
}