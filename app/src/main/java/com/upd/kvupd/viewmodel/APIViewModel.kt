package com.upd.kvupd.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upd.kvupd.data.model.FlowCliente
import com.upd.kvupd.data.model.JsonCliente
import com.upd.kvupd.data.model.JsonPedimap
import com.upd.kvupd.data.model.JsonResponseAny
import com.upd.kvupd.data.model.TableBaja
import com.upd.kvupd.domain.JsObFunctions
import com.upd.kvupd.domain.RoomFunctions
import com.upd.kvupd.domain.ServerFunctions
import com.upd.kvupd.domain.search.ClienteSearchSource
import com.upd.kvupd.ui.sealed.ResultadoApi
import com.upd.kvupd.ui.sealed.TipoUsuario
import com.upd.kvupd.utils.EventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class APIViewModel @Inject constructor(
    private val serverFunctions: ServerFunctions,
    private val roomFunctions: RoomFunctions,
    private val jsobFunctions: JsObFunctions,
    private val clienteSearchSource: ClienteSearchSource
) : ViewModel() {

    private val _registerEvent = EventFlow<ResultadoApi<JsonResponseAny>>()
    val registerEvent = _registerEvent.events

    private val _bajaMessage = EventFlow<String>()
    val bajaMessage = _bajaMessage.events

    private val _pedimapEvent = EventFlow<ResultadoApi<JsonPedimap>>()
    val pedimapEvent = _pedimapEvent.events

    private val _clienteEvent = EventFlow<ResultadoApi<JsonCliente>>()
    val clienteEvent = _clienteEvent.events

    private val flowClientes = roomFunctions.listFlowClientes()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /// VARIABLES PRIVADAS ARRIBA ///

    val flowConfiguracion = roomFunctions.listFlowConfiguracion()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val flowRutas = roomFunctions.listFlowClientes()
        .map { list ->
            if (list.isEmpty()) "" else list.map { it.ruta.toString() }.distinct()
                .joinToString(" - ")
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val flowPolygon = roomFunctions.listFlowPolygon()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val flowVendedores = roomFunctions.listFlowVendedores()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

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

    fun saveAndSendBaja(item: TableBaja) {
        viewModelScope.launch {
            roomFunctions.saveBaja(item)
            intentarEnviarBaja(item)
        }
    }

    fun retrySendBaja(item: TableBaja) {
        viewModelScope.launch {
            roomFunctions.updateBaja(
                item.copy(
                    sincronizado = false
                )
            )
            intentarEnviarBaja(item)
        }
    }

    private suspend fun intentarEnviarBaja(item: TableBaja) {
        val config = roomFunctions.queryConfiguracion() ?: return

        val json = jsobFunctions.jsonObjectBajas(config, item)

        serverFunctions.apiSendBaja(json).collect { result ->
            when (result) {
                is ResultadoApi.Exito -> {
                    roomFunctions.updateBaja(
                        item.copy(
                            sincronizado = true
                        )
                    )
                }

                is ResultadoApi.ErrorHttp,
                is ResultadoApi.Fallo -> {
                    roomFunctions.updateBaja(
                        item.copy(
                            sincronizado = false
                        )
                    )
                    result.mensajeUsuario()
                        ?.let { _bajaMessage.emit(it) }
                }

                ResultadoApi.Loading -> Unit
            }
        }
    }
}