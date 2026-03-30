package com.upd.kvupd.data.remote

import com.upd.kvupd.data.remote.sealed.SocketEvent
import io.socket.client.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SocketSource @Inject constructor(
    private val socketBuilder: SocketBuilder
) {
    fun executeUpdater(empresa: Int): Flow<SocketEvent> = callbackFlow {

        try {
            trySend(SocketEvent.Loading)

            val url = socketBuilder.createUrl(empresa)

            val options = IO.Options().apply {
                forceNew = false
                timeout = 30000
            }

            val socket = IO.socket(url, options)

            // 🔹 Escucha respuesta del servidor
            socket.on("response") {
                trySend(SocketEvent.Success("Actualización completada"))
                close() // cerramos flow
            }

            // 🔹 Manejo de error
            socket.on("connect_error") {
                trySend(SocketEvent.Error("Error de conexión"))
                close()
            }

            socket.connect()
            socket.emit("request")

            // 🔹 Limpieza cuando se cancela el Flow
            awaitClose {
                socket.disconnect()
                socket.off()
            }

        } catch (e: Exception) {
            trySend(SocketEvent.Error("Error inesperado"))
            close()
        }
    }
}