package com.upd.kvupd.data.remote

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.upd.kvupd.utils.FirebaseKeys.NODO_DIRECCION
import com.upd.kvupd.utils.FirebaseKeys.NODO_IP
import com.upd.kvupd.utils.FirebaseKeys.NODO_KVENTAS
import com.upd.kvupd.utils.FirebaseKeys.NODO_UUID
import com.upd.kvupd.utils.FirebaseKeys.NO_EXISTE
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseHelper @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) {
    suspend fun obtenerIpFirebase(): String {
        return try {
            Log.d("FirebaseHelper", "Consultando IP en Firebase...")
            val snapshot = firebaseDatabase
                .getReference(NODO_DIRECCION)
                .child(NODO_IP)
                .get()
                .await()

            //snapshot.getValue(String::class.java) ?: "0.0.0.0"
            val ip = snapshot.getValue(String::class.java) ?: "0.0.0.0"
            Log.d("FirebaseHelper", "IP obtenida: $ip")
            ip
        } catch (e: Exception) {
            Log.e("FirebaseHelper", "Error obteniendo IP", e)
            "0.0.0.0"
        }
    }

    suspend fun existeHashFirebase(hash: String): Boolean {
        return try {
            val snapshot = firebaseDatabase
                .getReference(NODO_KVENTAS)
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
            val snapshot = firebaseDatabase
                .getReference(NODO_KVENTAS)
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
            firebaseDatabase
                .getReference(NODO_KVENTAS)
                .child(hash)
                .setValue(contenido)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}