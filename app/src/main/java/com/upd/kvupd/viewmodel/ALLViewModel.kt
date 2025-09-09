package com.upd.kvupd.viewmodel

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upd.kvupd.data.remote.FirebaseHelper
import com.upd.kvupd.domain.GeneralFunctions
import com.upd.kvupd.ui.sealed.InitialState
import com.upd.kvupd.utils.PlayServicesChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ALLViewModel @Inject constructor(
    private val generalFunctions: GeneralFunctions,
    private val playServicesChecker: PlayServicesChecker,
    private val firebaseHelper: FirebaseHelper
) : ViewModel() {

    private val _uuidEstados = MutableStateFlow<InitialState>(InitialState.Loading())
    val uuidEstados: StateFlow<InitialState> = _uuidEstados

    fun obtenerUUID(): String? {
        return generalFunctions.obtenerIdentificador()
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
        if (!generalFunctions.existeIdentificador()) {
            _uuidEstados.value = InitialState.NoUUID
            return
        }
        _uuidEstados.value = InitialState.HasUUID
    }

    fun procesandoHashFirebase() {
        viewModelScope.launch {
            _uuidEstados.value = InitialState.Loading("Consultando firebase...")

            val hash = generalFunctions.crearHash()
            val uuid = if (firebaseHelper.existeHashFirebase(hash)) {
                // Ya existe, obtenerlo directamente
                firebaseHelper.obtenerUUIDFirebase(hash).also {
                    generalFunctions.guardarIdentificador(it)
                }
            } else {
                // No existe, crear uno nuevo y guardarlo
                val nuevoUuid = generalFunctions.crearIdentificador()
                val contenido = generalFunctions.obtenerNodoDatos(nuevoUuid)

                val enviado = firebaseHelper.guardarHashFirebase(hash, contenido)
                if (enviado) {
                    generalFunctions.guardarIdentificador(nuevoUuid)
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
}