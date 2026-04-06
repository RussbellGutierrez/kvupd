package com.upd.kvupd.data.remote

import android.util.Log
import com.upd.kvupd.data.remote.sealed.SocketEvent
import io.socket.client.IO
import io.socket.client.Socket
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

            // 🔹 CONNECT
            socket.on(Socket.EVENT_CONNECT) {
                trySend(SocketEvent.Debug("Conectado al servidor"))

                socket.emit("request")
                trySend(SocketEvent.Debug("Solicitando actualización al servidor, demora entre 1min a 5min..."))
            }

            // 🔹 RESPONSE
            socket.on("response") { args ->
                trySend(SocketEvent.Debug("Servidor respondió correctamente"))
                trySend(SocketEvent.Success("Actualización completada"))
                close()
            }

            // 🔹 DISCONNECT
            socket.on(Socket.EVENT_DISCONNECT) {
                trySend(SocketEvent.Debug("Conexión cerrada"))
            }

            // 🔹 ERROR CONEXIÓN
            socket.on("connect_error") { args ->
                val msg = args.firstOrNull()?.toString() ?: "Error desconocido"

                Log.e("SOCKET", "CONNECT_ERROR: $msg")

                trySend(SocketEvent.Debug("Error al conectar con el servidor"))
                trySend(SocketEvent.Error(msg))
                close()
            }

            // 🔹 ERROR GENERAL
            socket.on("error") { args ->
                val msg = args.firstOrNull()?.toString() ?: "Error interno"
                trySend(SocketEvent.Debug("Error durante la comunicación con el servidor"))
            }

            socket.connect()

            awaitClose {
                socket.disconnect()
                socket.off()
            }

        } catch (e: Exception) {
            trySend(SocketEvent.Error("Error inesperado: ${e.message}"))
            close()
        }
    }
}