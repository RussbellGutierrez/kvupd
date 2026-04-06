package com.upd.kvupd.data.remote.sealed

sealed class SocketEvent {
    object Loading : SocketEvent()
    data class Success(val msg: String) : SocketEvent()
    data class Error(val msg: String) : SocketEvent()
    data class Debug(val msg: String) : SocketEvent() // 👈 clave
}