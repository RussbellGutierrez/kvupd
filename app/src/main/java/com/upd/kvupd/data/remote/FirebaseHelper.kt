package com.upd.kvupd.data.remote

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.upd.kvupd.data.local.enumClass.InfoDispositivo
import com.upd.kvupd.utils.ExtraInfo
import com.upd.kvupd.utils.FirebaseKeys.NODO_DEBUG
import com.upd.kvupd.utils.FirebaseKeys.NODO_DIRECCION
import com.upd.kvupd.utils.FirebaseKeys.NODO_IP
import com.upd.kvupd.utils.FirebaseKeys.NODO_KVENTAS
import com.upd.kvupd.utils.FirebaseKeys.NODO_MENSAJE
import com.upd.kvupd.utils.FirebaseKeys.NODO_PEDIMAP
import com.upd.kvupd.utils.FirebaseKeys.NODO_RELEASE
import com.upd.kvupd.utils.FirebaseKeys.NODO_UUID
import com.upd.kvupd.utils.FirebaseKeys.NO_EXISTE
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.net.SocketTimeoutException
import javax.inject.Inject

class FirebaseHelper @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) {
    suspend fun obtenerIpFirebase(): String {
        Log.d("FirebaseHelper", "Consultando IP en Firebase...")

        val snapshot = withTimeoutOrNull(FIREBASE_TIMEOUT_MS) {
            firebaseDatabase
                .getReference(NODO_DIRECCION)
                .child(NODO_IP)
                .get()
                .await()
        } ?: throw SocketTimeoutException(
            "Tiempo de espera agotado obteniendo la IP"
        )

        val ip = snapshot
            .getValue(String::class.java)
            ?.trim()
            .orEmpty()

        if (ip.isBlank() || ip == "0.0.0.0") {
            throw IllegalStateException(
                "Firebase no proporcionó una IP válida"
            )
        }

        Log.d("FirebaseHelper", "IP obtenida: $ip")
        return ip
    }

    suspend fun existeHashFirebase(hash: String): Boolean {
        return try {
            val snapshot = getKventasReference()
                .child(hash)
                .get()
                .await()

            snapshot.exists()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obtenerUUIDFirebase(hash: String): String {
        return try {
            val snapshot = getKventasReference()
                .child(hash)
                .child(NODO_UUID)
                .get()
                .await()

            snapshot.getValue(String::class.java) ?: NO_EXISTE
        } catch (e: Exception) {
            NO_EXISTE
        }
    }

    suspend fun guardarHashFirebase(hash: String, contenido: Map<String, String>): Boolean {
        return try {
            getKventasReference()
                .child(hash)
                .setValue(contenido)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obtenerMensajePedimap(): String {
        return try {
            val snapshot = firebaseDatabase
                .getReference(NODO_PEDIMAP)
                .child(NODO_MENSAJE)
                .get()
                .await()

            val mensaje = snapshot.getValue(String::class.java) ?: "No hay mensaje"
            Log.d("FirebaseHelper", "Mensaje obtenido: $mensaje")
            mensaje
        } catch (e: Exception) {
            Log.e("FirebaseHelper", "Error obteniendo mensaje", e)
            "No se pudo obtener el mensaje"
        }
    }

    private fun obtenerNodoBuild() =
        if (ExtraInfo.obtener(InfoDispositivo.BUILD_TYPE) == "DEBUG")
            NODO_DEBUG
        else
            NODO_RELEASE

    private fun getKventasReference() =
        firebaseDatabase
            .getReference(NODO_KVENTAS)
            .child(obtenerNodoBuild())

    companion object {
        private const val FIREBASE_TIMEOUT_MS = 15_000L
    }
}