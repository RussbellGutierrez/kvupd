package com.upd.kvupd.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upd.kvupd.data.model.JsonResponseAny
import com.upd.kvupd.domain.JsObFunctions
import com.upd.kvupd.domain.ServerFunctions
import com.upd.kvupd.ui.sealed.ResultadoApi
import com.upd.kvupd.utils.EventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class APIViewModel @Inject constructor(
    private val serverFunctions: ServerFunctions,
    private val jsobFunctions: JsObFunctions
) : ViewModel() {

    private val _registerEvent = EventFlow<ResultadoApi<JsonResponseAny>>()
    val registerEvent = _registerEvent.events

    //private val _configEvent = EventFlow<ResultadoApi<JConfig>>()
    //val configEvent = _configEvent.events

    fun registrarEquipoServidor(identificador: String, empresa: String) {
        viewModelScope.launch {
            val json = jsobFunctions.jsonRegistrarEquipo(identificador, empresa)
            serverFunctions.apiSendRegistro(json).collect {
                _registerEvent.emit(it)
            }
        }
    }
}