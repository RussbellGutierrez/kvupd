package com.upd.kvupd.ui.sealed

sealed class InitialState {
    data class Loading(val mensaje: String = "") : InitialState()
    object NoGooglePlay : InitialState()
    object NoBasePermissions : InitialState()
    object NoBackgroundLocationPermission : InitialState()
    object NoUUID : InitialState()
    object CreatedUUID : InitialState()
    object FailCreateUUID : InitialState()
    object HasUUID : InitialState()
}