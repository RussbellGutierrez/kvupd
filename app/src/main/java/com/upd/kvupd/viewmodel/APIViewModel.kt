package com.upd.kvupd.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.upd.kvupd.data.model.JsonResponseAny
import com.upd.kvupd.domain.JsObFunctions
import com.upd.kvupd.domain.RoomFunctions
import com.upd.kvupd.domain.ServerFunctions
import com.upd.kvupd.ui.sealed.ResultadoApi
import com.upd.kvupd.utils.EventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class APIViewModel @Inject constructor(
    private val serverFunctions: ServerFunctions,
    private val roomFunctions: RoomFunctions,
    private val jsobFunctions: JsObFunctions
) : ViewModel() {

    private val _registerEvent = EventFlow<ResultadoApi<JsonResponseAny>>()
    val registerEvent = _registerEvent.events

    fun flowConfiguracion() = roomFunctions.listFlowConfiguracion().asLiveData()

    fun flowRutas(): LiveData<String> =
        roomFunctions.listFlowClientes()
            .map { list ->
                // si la lista viene vacía, devolvemos string vacío
                if (list.isEmpty()) return@map ""

                // obtener rutas en una lista de strings
                val rutas = list.map { it.ruta.toString() }

                // eliminar duplicados
                val rutasDistintas = rutas.distinct()

                // construir el mensaje final
                rutasDistintas.joinToString(separator = " - ")
            }.asLiveData()

    fun registrarEquipoServidor(identificador: String, empresa: String) {
        viewModelScope.launch {
            val json = jsobFunctions.jsonRegistrarEquipo(identificador, empresa)
            serverFunctions.apiSendRegistro(json).collect {
                _registerEvent.emit(it)
            }
        }
    }
}