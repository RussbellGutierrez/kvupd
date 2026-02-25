package com.upd.kvupd.viewmodel

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upd.kvupd.data.remote.FirebaseHelper
import com.upd.kvupd.domain.IdentityFunctions
import com.upd.kvupd.domain.OperationsFunctions
import com.upd.kvupd.domain.RoomFunctions
import com.upd.kvupd.ui.fragment.enumClass.TipoUsuario
import com.upd.kvupd.ui.sealed.EstadoSesion
import com.upd.kvupd.ui.sealed.InitialState
import com.upd.kvupd.utils.EventFlow
import com.upd.kvupd.utils.PlayServicesChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ALLViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val identityFunctions: IdentityFunctions,
    private val operationsFunctions: OperationsFunctions,
    private val roomFunctions: RoomFunctions,
    private val playServicesChecker: PlayServicesChecker,
    private val firebaseHelper: FirebaseHelper
) : ViewModel() {

    init {
        tipoUsuarioActual()
    }

    private val _uuidEstados = MutableStateFlow<InitialState>(InitialState.Loading())
    val uuidEstados: StateFlow<InitialState> = _uuidEstados

    private val _configMensaje = EventFlow<String>()
    val configMensaje = _configMensaje.events

    private val _pedimapMensaje = EventFlow<String>()
    val pedimapMensaje = _pedimapMensaje.events

    private val _sesionEstado = MutableStateFlow<EstadoSesion>(EstadoSesion.Loading)
    val sesionEstado: StateFlow<EstadoSesion> = _sesionEstado

    private val _tipoUsuario = MutableStateFlow<TipoUsuario?>(null)
    val tipoUsuario: StateFlow<TipoUsuario?> = _tipoUsuario

    private val _remainingWorkersIds = EventFlow<List<UUID>>()
    val remainingWorkersIds = _remainingWorkersIds.events

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    fun obtenerUUID(): String? {
        return identityFunctions.obtenerIdentificador()
    }

    fun iniciarFlujo(
        activity: ComponentActivity,
        permisosBaseOtorgados: Boolean,
        permisoBackgroundOtorgado: Boolean
    ) {
        if (!playServicesChecker.hayServiciosGoogle(activity)) {
            _uuidEstados.value = InitialState.NoGooglePlay
            return
        }
        if (!permisosBaseOtorgados) {
            _uuidEstados.value = InitialState.NoBasePermissions
            return
        }
        if (!permisoBackgroundOtorgado) {
            _uuidEstados.value = InitialState.NoBackgroundLocationPermission
            return
        }
        if (!identityFunctions.existeIdentificador()) {
            _uuidEstados.value = InitialState.NoUUID
            return
        }
        _uuidEstados.value = InitialState.HasUUID
    }

    fun procesandoHashFirebase() {
        viewModelScope.launch {
            _uuidEstados.value = InitialState.Loading("Consultando firebase...")

            val hash = identityFunctions.crearHash()
            val uuid = if (firebaseHelper.existeHashFirebase(hash)) {
                // Ya existe, obtenerlo directamente
                firebaseHelper.obtenerUUIDFirebase(hash).also {
                    identityFunctions.guardarIdentificador(it)
                }
            } else {
                // No existe, crear uno nuevo y guardarlo
                val nuevoUuid = identityFunctions.crearIdentificador()
                val contenido = identityFunctions.obtenerNodoDatos(nuevoUuid)

                val enviado = firebaseHelper.guardarHashFirebase(hash, contenido)
                if (enviado) {
                    identityFunctions.guardarIdentificador(nuevoUuid)
                    nuevoUuid
                } else null
            }

            _uuidEstados.value = when (uuid) {
                null -> InitialState.FailCreateUUID
                else -> InitialState.CreatedUUID
            }

            if (uuid != null) {
                // Avanzar al estado estable
                delay(1000) // opcional
                _uuidEstados.value = InitialState.HasUUID
            }
        }
    }

    fun ejecutandoWorkerInicial(): UUID {
        return operationsFunctions.initWorker()
    }

    fun ejecutandoWorkersRestantes() {
        viewModelScope.launch {
            try {
                val config = roomFunctions.queryConfiguracion()
                if (config == null) {
                    _configMensaje.emit("No se encontró configuración local")
                    return@launch
                }

                val tipo = TipoUsuario.fromCodigo(config.tipo)
                val ids = operationsFunctions.remainingWorkers(tipo)

                if (ids.isEmpty()) {
                    _configMensaje.emit("No se pudieron ejecutar los workers restantes")
                } else {
                    _remainingWorkersIds.emit(ids)
                }
            } catch (e: Exception) {
                _configMensaje.emit("Error: ${e.message}")
            }
        }
    }

    fun ejecutarSyncInicial() {
        viewModelScope.launch {
            operationsFunctions.syncInitial()
        }
    }

    fun reprogramarUsandoConfig() {
        operationsFunctions.reprogramBeforeConfig()
    }

    fun iniciarServiceSiHayConfiguracion() {
        viewModelScope.launch {
            roomFunctions.queryConfiguracion() ?: return@launch
            operationsFunctions.syncInitial()
        }
    }

    fun entregarRegistroPedimap() {
        viewModelScope.launch {
            val uuid = identityFunctions.obtenerIdentificador() ?: return@launch
            val mensaje = firebaseHelper.obtenerMensajePedimap()
            val completo = "$mensaje:\n$uuid"
            _pedimapMensaje.emit(completo)
        }
    }

    fun setQuery(text: String) {
        _query.value = text
    }

    fun clearQuery() {
        _query.value = ""
    }

    fun verificarFechaSesion() {
        viewModelScope.launch {
            val config = roomFunctions.queryConfiguracion()
                ?: return@launch

            val esValida = operationsFunctions.checkTodaySesion(config)

            val nuevoEstado = if (esValida)
                EstadoSesion.Valida
            else
                EstadoSesion.Invalida

            if (_sesionEstado.value != nuevoEstado) {
                _sesionEstado.value = nuevoEstado
            }
        }
    }

    fun tipoUsuarioActual() {
        viewModelScope.launch {
            val config = roomFunctions.queryConfiguracion()
                ?: return@launch

            val tipo = TipoUsuario.fromCodigo(config.tipo)
            _tipoUsuario.value = tipo
        }
    }
}