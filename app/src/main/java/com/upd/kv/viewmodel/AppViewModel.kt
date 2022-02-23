package com.upd.kv.viewmodel

import android.graphics.Bitmap
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.gms.maps.GoogleMap
import com.upd.kv.application.ToastHelper
import com.upd.kv.data.model.*
import com.upd.kv.domain.Functions
import com.upd.kv.domain.Repository
import com.upd.kv.utils.Event
import com.upd.kv.utils.Network
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

    fun configObserver() = repository.getFlowConfig().asLiveData()

    fun rowClienteObs() = repository.getFlowRowCliente().asLiveData()

    fun lastLocation() = repository.getFlowLocation().asLiveData()

    fun markerMap() = repository.getFlowMarker().asLiveData()

    suspend fun getConfig(T: (Config) -> Unit) {
        val conf = repository.getConfig()
        if (conf.isNullOrEmpty()) {
            toastHelper.sendToast("Configuracion vacio")
        } else {
            T(conf[0])
        }
    }

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
            values.data?.data?.let { repository.saveClientes(it) }
            _cliente.value = Event(values)
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

    suspend fun isClienteBaja(cliente: String): Boolean =
        repository.isClienteBaja(cliente)
}